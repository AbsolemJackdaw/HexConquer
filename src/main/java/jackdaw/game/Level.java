package jackdaw.game;

import framework.GameState;
import framework.GameStateHandler;
import framework.input.KeyHandler;
import framework.input.MouseHandler;
import framework.window.Window;
import jackdaw.game.container.BufferedContainer;
import jackdaw.game.container.gui.InventoryContainer;
import jackdaw.game.container.gui.MapContainer;
import jackdaw.game.map.*;
import jackdaw.game.map.level.CitySpot;
import jackdaw.game.map.level.PlainHex;
import jackdaw.game.map.level.Road;
import jackdaw.game.noise.OpenSimplexNoise;
import jackdaw.game.player.Player;
import jackdaw.game.resources.PlainType;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public class Level extends GameState {


    public static final Rectangle viewPort = new Rectangle(0, 0, Window.getWidth(), Window.getHeight());
    public static double bone = 100 * Window.getScale();
    public final int lengthOfDay = 600;//3600;
    public Player player = new Player();
    protected OpenSimplexNoise NOISE;
    BufferedContainer map;
    private Ellipse2D clockPort = new Ellipse2D.Double(viewPort.width - Window.getGameScale(60), Window.getGameScale(10), Window.getGameScale(50), Window.getGameScale(50));
    private Ellipse2D eventPort = new Ellipse2D.Double(viewPort.width - Window.getGameScale(110), Window.getGameScale(10), Window.getGameScale(50), Window.getGameScale(50));
    private ArrayList<PlainHex> plains = new ArrayList<>();
    private ArrayList<CitySpot> citieSpots = new ArrayList<>();
    private ArrayList<Road> roads = new ArrayList<>();
    private ArrayList<ArrayList<? extends Element>> mapElements = new ArrayList<>();
    private float fieldSize = 10.5f;
    public double fieldSizeX = (fieldSize) * bone * 3;
    public double fieldSizeY = (fieldSize) * bone * 2;
    private Random rand = new Random();
    private InventoryContainer inventory;
    private BufferedContainer currentGUI;
    private boolean hasOpenGui = false;
    private long timePassed = 0L;
    private DayCycleEvent dayCycleEvent = new DayCycleEvent();

    public Level(GameStateHandler gsh) {
        super(gsh);
        NOISE = new OpenSimplexNoise(rand.nextLong());
        double translateX = bone * 3;
        double translateY = bone * 2;

        map = new MapContainer(this, (int) (fieldSizeX + bone * 4), (int) (fieldSizeY + bone));
        map.setOrigin(0, 0);
        for (double y = 0; y < fieldSize; y += 0.5f) {
            for (double x = 0; x < fieldSize; x += 0.5f) {
                PlainType plainType = PlainType.WATER;
                if (y > 0.5 && x > 0 && y < fieldSize - 1 && x < fieldSize - 0.5)
                    plainType = PlainType.get((byte) rand.nextInt(52));

                double posX = 3 * x * bone;
                double posY = 2 * y * bone;
                posY *= Math.sqrt(3) / 2; // height of an equilateral triangle is shorter then the height of its sides
                if (x * 2 % 2 == 0 && y * 2 % 2 == 0) {
                    plains.add(new PlainHex(this, plainType, translateX + posX, translateY + posY));
                } else if (x * 2 % 2 == 1 && y * 2 % 2 == 1) {
                    plains.add(new PlainHex(this, plainType, translateX + posX, translateY + posY));
                }
            }
        }

        for (PlainHex plane : plains) {
            for (Coord corner : plane.getCorners()) {
                CitySpot node = new CitySpot(this, corner.getPosX(), corner.getPosY());
                if (!citieSpots.contains(node))
                    citieSpots.add(node);
            }
            for (Road road : plane.getRoads()) {
                if (!roads.contains(road))
                    roads.add(road);
            }
        }

        mapElements.add(plains);
        mapElements.add(roads);
        mapElements.add(citieSpots);

        CitySpot startCity = citieSpots.get(rand.nextInt(citieSpots.size()));
        startCity.purchasePlot("player");
        map.setOrigin(
                -startCity.getPosition().getPosX() + Window.getWidth() / 2,
                -startCity.getPosition().getPosY() + Window.getHeight() / 2);
        inventory = new InventoryContainer(this, Window.getWidth() / 3, Window.getHeight());

        for (int i = 0; i < 2; i++) //aid player inventory with some resources his city surrounds
            dayCycleEvent.rollDay(this, plains);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.black);
        g.fill(viewPort);

        //only draw to the map buffer what is effectively concidered on screen
        map.drawToScreen(g, (containerGraphics) ->
                mapElements.forEach(allElements ->
                        allElements.stream().filter(element ->
                                getRelativeBounds(element).intersects(viewPort)).forEach(element ->
                                element.draw(containerGraphics)
                        )));

        if (hasOpenGui) {
            if (currentGUI != null) {
                currentGUI.draw(g, null);
            }
        } else {
            mapElements.forEach(elements -> elements.stream().
                    filter(element -> element.getBoundingBox().contains(getRelativeMousePosition()) && element instanceof Hoverable).
                    forEach(element -> {
                        Hover hover = ((Hoverable) element).getHoverable(new Coord(MouseHandler.mouseX, MouseHandler.mouseY));
                        if (hover != null && !hover.equals(Hover.EMPTY))
                            hover.draw(g);
                    }));
        }

        //draw clock
        g.setColor(Color.black.brighter());
        g.setClip(clockPort);
        g.drawImage(TexLoader.CLOCK, (int) clockPort.getX(), (int) clockPort.getY(), (int) clockPort.getWidth(), (int) clockPort.getHeight(), null);
        if (player.getCurrentNoonEvent() != DayCycleEvent.NOONEVENTS.NONE) {
            g.setClip(eventPort);
            BufferedImage eventImg = switch (player.getCurrentNoonEvent()) {
                case COMMERCE -> TexLoader.COMMERCE;
                case ROBBERS -> TexLoader.ROBBERS;
                case RODENTS -> TexLoader.RODENTS;
                default -> TexLoader.BADYEAR; //default for ease of using switch. none isnt included anyway
            };
            g.drawImage(eventImg, (int) eventPort.getX(), (int) eventPort.getY(), (int) eventPort.getWidth(), (int) eventPort.getHeight(), null);
        }
        g.setClip(viewPort);

        g.setColor(Color.red.darker());
        double angle = (getTimePassed() % (double) lengthOfDay) * (360.0 / (double) lengthOfDay);
        Arc2D.Double arcShape = new Arc2D.Double(viewPort.width - Window.getGameScale(60), Window.getGameScale(10), Window.getGameScale(50), Window.getGameScale(50), 90, -angle, Arc2D.OPEN);
        g.drawLine((int) arcShape.getCenterX(), (int) arcShape.getCenterY(), (int) arcShape.getEndPoint().getX(), (int) arcShape.getEndPoint().getY());

    }

    @Override
    public void update() {
        timePassed++;
        double moveMapSpeed = 5;
        if (KeyHandler.isHeld(KeyHandler.RIGHT)) {
            map.move(-moveMapSpeed, 0.0);
        } else if (KeyHandler.isHeld(KeyHandler.LEFT)) {
            map.move(moveMapSpeed, 0.0);
        }
        if (KeyHandler.isHeld(KeyHandler.DOWN)) {
            map.move(0.0, -moveMapSpeed);
        } else if (KeyHandler.isHeld(KeyHandler.UP))
            map.move(0.0, moveMapSpeed);

        if (!hasOpenGui) {
            if (MouseHandler.click) {
                mapElements.forEach(elements -> elements.stream().
                        filter(element -> element.getBoundingBox().contains(getRelativeMouseClick())).
                        forEach(element -> {
                            if (element instanceof Buyable buyable) {
                                if (!buyable.isBought()) {
                                    buyable.buy("player");
                                    return;
                                }
                            }
                            if (element instanceof GuiAble guiAble) {
                                if (guiAble.getGui() != null) {
                                    this.openGui(guiAble.getGui());
                                    return; //currently final statement. keep for futur code additions
                                }
                            }
                        }));
            }
        } else if (currentGUI != null) {
            currentGUI.update();
        }

        if (KeyHandler.isPressed(KeyHandler.SPACE)) {
            gsh.changeGameState(GameState.FIRST_SCREEN);
        }

        if (KeyHandler.isPressed(KeyHandler.SHIFT) || timePassed % lengthOfDay == 0) {
            nextDay();
        }

        if (timePassed % (lengthOfDay / 2) == 0 && timePassed % lengthOfDay != 0)
            dayCycleEvent.rollNoon(this);

        if (KeyHandler.isPressed(KeyHandler.ESCAPE)) {
            if (!hasOpenGui)
                openGui(inventory);
            else {
                hasOpenGui = false;
                currentGUI = null;
            }
        }
    }

    public Collection<Road> nodeRoads(CitySpot node) {
        Collection<Road> coll = new ArrayList<>();
        for (Road road : roads) {
            if (road.getLinkedNodeB().equals(node.getPosition()) || road.getLinkedNodeA().equals(node.getPosition())) {
                coll.add(road);
            }
        }
        return coll;
    }

    public boolean isCityBuild(Coord coord) {
        for (CitySpot node : citieSpots) {
            if (node.getPosition().equals(coord)) {
                return node.isBought();
            }
        }
        return false;
    }

    public CitySpot getCitySpot(Coord coord) {
        for (CitySpot city : citieSpots) {
            if (city.getPosition().equals(coord))
                return city;
        }
        return null;
    }

    public Point getRelativeMouseClick() {
        double x = MouseHandler.clicked.x - map.originX;
        double y = MouseHandler.clicked.y - map.originY;
        return new Point((int) x, (int) y);
    }

    public Point getRelativeMousePosition() {
        double x = MouseHandler.mouseX - map.originX;
        double y = MouseHandler.mouseY - map.originY;
        return new Point((int) x, (int) y);
    }

    public void openGui(BufferedContainer gui) {
        this.hasOpenGui = true;
        this.currentGUI = gui;
    }

    public void exploitSurrounds(CitySpot city) {
        plains.forEach(plainHex -> {
            if (city.getBoundingBox().intersects(plainHex.getBoundingBox().getBounds())) {
                plainHex.trackCity(city);
            }
        });
    }

    public Optional<Player> getPlayerByName(String name) {
        return Optional.of(player);
    }

    /**
     * returns a rectangle based on the element's bounding box
     */
    public Rectangle getRelativeBounds(Element element) {

        return new Rectangle(
                element.getBoundingBox().getBounds().x + (int) map.originX,
                element.getBoundingBox().getBounds().y + (int) map.originY,
                element.getBoundingBox().getBounds().width,
                element.getBoundingBox().getBounds().height
        );
    }

    /**
     * returns a coordinate with added offset from the point of origin of the map.
     * For drawing purposes
     */
    public Coord getRelativeCoord(Coord coord) {
        return new Coord(coord.getPosX() + map.originX, coord.getPosY() + map.originY);
    }

    public Rectangle getRelativeShape(Rectangle rectangle) {
        Rectangle r = new Rectangle(rectangle);
        r.translate((int) map.originX, (int) map.originY);
        return r;
    }

    public Polygon getRelativeShape(Polygon polygon) {
        Polygon p = new Polygon(polygon.xpoints, polygon.ypoints, polygon.npoints);
        p.translate((int) map.originX, (int) map.originY);
        return p;
    }

    private void nextDay() {
        dayCycleEvent.rollDay(this, plains);

        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        System.out.printf("%d:%d:%d New Day%n", hour, minute, second);
    }

    public long getTimePassed() {
        return timePassed;
    }
}
