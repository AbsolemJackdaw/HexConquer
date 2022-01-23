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
import jackdaw.game.resources.Material;
import jackdaw.game.resources.PlainType;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Level extends GameState {


    public static final Rectangle viewPort = new Rectangle(0, 0, Window.getWidth(), Window.getHeight());
    public static double bone = 100 * Window.getScale();
    public final int lengthOfDay = 600;//3600;
    private final BufferedContainer map;
    private final Ellipse2D clockPort = new Ellipse2D.Double(viewPort.width - Window.getGameScale(60), Window.getGameScale(10), Window.getGameScale(50), Window.getGameScale(50));
    private final Ellipse2D eventPort = new Ellipse2D.Double(viewPort.width - Window.getGameScale(110), Window.getGameScale(10), Window.getGameScale(50), Window.getGameScale(50));
    private final ArrayList<PlainHex> plains = new ArrayList<>();
    private final ArrayList<CitySpot> cityNodes = new ArrayList<>();
    private final ArrayList<Coord> allGeneratedNodes = new ArrayList<>();
    private final ArrayList<Road> roads = new ArrayList<>();
    private final ArrayList<ArrayList<? extends Element>> mapElements = new ArrayList<>();
    private final float gameFieldSize = 10.5f;
    private final Random rand = new Random();
    private final DayCycleEvent dayCycleEvent = new DayCycleEvent();
    public Player player = new Player("player");
    public double fieldSizeX = (gameFieldSize) * bone * 3;
    public double fieldSizeY = (gameFieldSize) * bone * 2;
    protected OpenSimplexNoise NOISE;
    private InventoryContainer inventory;
    private BufferedContainer currentGUI;
    private boolean hasOpenGui = false;
    private long timePassed = 0L;

    public Level(GameStateHandler gsh) {
        super(gsh);
        NOISE = new OpenSimplexNoise(rand.nextLong());
        double translateX = bone * 3;
        double translateY = bone * 2;

        map = new MapContainer(this, (int) (fieldSizeX + bone * 4), (int) (fieldSizeY + bone));
        map.setOrigin(0, 0);

        for (double y = 0; y < gameFieldSize; y += 0.5f) {
            for (double x = 0; x < gameFieldSize; x += 0.5f) {
                //create hexes
                PlainType plainType = PlainType.WATER;
                if (y > 0.5 && x > 0 && y < gameFieldSize - 1 && x < gameFieldSize - 0.5)
                    plainType = PlainType.get((byte) rand.nextInt(52));

                double posX = 3 * x * bone;
                double posY = y * bone * 1.732; // height of an equilateral triangle is shorter then the height of its sides

                Coord atPos = new Coord((int) (translateX + posX), (int) (translateY + posY));
                if ((x * 2 % 2 == 0 && y * 2 % 2 == 0) || (x * 2 % 2 == 1 && y * 2 % 2 == 1)) {
                    plains.add(new PlainHex(this, plainType, atPos));

                    //create nodes
                    double[] xDots = new double[]{0, bone / 2.0, bone * 1.5, bone * 2};
                    double[] yDots = new double[]{0, bone * 1.732 / 2, bone * 1.732};
                    Coord topLeft, topRight, left, right, bottomLeft, bottomRight;
                    //create biggest part of connecting hexagons with two points;
                    topLeft = new Coord((int) (translateX + posX + xDots[1] - (bone * 1)), (int) (translateY + posY + yDots[0] - (bone * 1.732 / 2)));
                    left = new Coord((int) (translateX + posX + xDots[0] - (bone * 1)), (int) (translateY + posY + yDots[1] - (bone * 1.732 / 2)));
                    this.allGeneratedNodes.add(topLeft);
                    this.allGeneratedNodes.add(left);
                    //generate four other points and apply to edge cases
                    topRight = new Coord((int) (translateX + posX + xDots[2] - (bone * 1)), (int) (translateY + posY + yDots[0] - (bone * 1.732 / 2)));
                    right = new Coord((int) (translateX + posX + xDots[3] - (bone * 1)), (int) (translateY + posY + yDots[1] - (bone * 1.732 / 2)));
                    bottomLeft = new Coord((int) (translateX + posX + xDots[1] - (bone * 1)), (int) (translateY + posY + yDots[2] - (bone * 1.732 / 2)));
                    bottomRight = new Coord((int) (translateX + posX + xDots[2] - (bone * 1)), (int) (translateY + posY + yDots[2] - (bone * 1.732 / 2)));

                    if (y == gameFieldSize - 0.5) {
                        if (!allGeneratedNodes.contains(right))
                            this.allGeneratedNodes.add(right);
                        if (!allGeneratedNodes.contains(bottomLeft))
                            this.allGeneratedNodes.add(bottomLeft);
                        if (!allGeneratedNodes.contains(bottomRight))
                            this.allGeneratedNodes.add(bottomRight);
                    }
                    if (y == 0) {
                        if (!allGeneratedNodes.contains(topRight))
                            this.allGeneratedNodes.add(topRight);
                    }
                    if (y < gameFieldSize - 0.5 && x == gameFieldSize - 0.5) {
                        if (!allGeneratedNodes.contains(right))
                            this.allGeneratedNodes.add(right);
                        if (!allGeneratedNodes.contains(bottomRight))
                            this.allGeneratedNodes.add(bottomRight);

                    }
                }
            }
        }

        for (PlainHex plain : plains) {
            Ellipse2D collectionRadius = new Ellipse2D.Double(plain.getPosition().posX() - bone * 1.25, plain.getPosition().posY() - bone * 1.25, bone * 2.5, bone * 2.5);
            List<Coord> surroundingNodes = allGeneratedNodes.stream().filter(citySpot -> collectionRadius.contains(citySpot.posX(), citySpot.posY())).sorted((o1, o2) -> o1.compareTo(plain.getPosition(), o2)).collect(Collectors.toList());
            //create bounding box of planes with the unique coordinates of the nodes
            if (surroundingNodes.size() != 6)
                throw new IllegalStateException("detected nodes where more or less then 6.");
            Polygon hex = new Polygon();
            //while we're looping all nodes, make a copy for cities and determine roads
            Coord roadNodeA = surroundingNodes.get(5);
            for (int i = 0; i < 6; i++) {
                Coord node = surroundingNodes.get(i); // note at first position
                hex.addPoint(node.posX(), node.posY());//add point to polygon
                if (plain.getMaterial() != Material.NONE) {//if the plain isnt water or a desert, add roads and a city point
                    //create node from first position
                    //make node from previous position and this pos
                    Road road = new Road(this, roadNodeA, node);
                    if (!roads.contains(road)) // check on doubles. we're collecting nodes from a center point and will have doubles
                        roads.add(road);
                    //Set the previous position to this position
                    roadNodeA = node;
                    CitySpot city = new CitySpot(this, node);
                    if (!cityNodes.contains(city))// check on doubles. we're collecting nodes from a center point and will have doubles
                        cityNodes.add(city);
                }
            }
            plain.createHex(hex);
        }

        mapElements.add(plains);
        mapElements.add(roads);
        mapElements.add(cityNodes);

        //init two starter cities
        CitySpot startCity = cityNodes.get(rand.nextInt(cityNodes.size()));

        Optional<CitySpot> secondCity =
                cityNodes.stream().filter(citySpot ->
                        !citySpot.getBoundingBox().intersects(startCity.getPosition().posX() - 1.1 * bone, startCity.getPosition().posY() - 1.1 * bone, 2.2 * bone, 2.2 * bone) && (
                                citySpot.getBoundingBox().intersects(startCity.getPosition().posX() - 1.6 * bone, startCity.getPosition().posY() - 1.6 * bone, 3.2 * bone, 3.2 * bone) ||
                                        citySpot.getBoundingBox().intersects(startCity.getPosition().posX() - 0.6 * bone, startCity.getPosition().posY() - 2.1 * bone, bone * 1.2, 4.2 * bone))
                ).reduce((citySpot, citySpot2) -> rand.nextBoolean() ? citySpot : citySpot2);
        startCity.purchasePlot(player);
        System.out.println("first spot coords are " + startCity.getPosition());
        secondCity.ifPresent(secondSpot -> {
            secondSpot.purchasePlot(player);
            System.out.println("second spot coords are " + secondSpot.getPosition());
            List<Road> starter = roads.stream().filter(road -> road.getBoundingBox().intersects(startCity.imageSpace) || road.getBoundingBox().intersects(secondSpot.imageSpace)).toList();
            if (!starter.isEmpty()) {
                for (int d = 0; d < starter.size(); d++) {
                    Road a = starter.get(d);
                    for (int i = 0; i < starter.size(); i++) {
                        Road b = starter.get(i);
                        if (a.shareUnbuildPoint(b)) {
                            a.buy(player.getName());
                            b.buy(player.getName());
                            break;
                        }
                    }
                }
            }
        });
        if (secondCity.isEmpty()) {
            System.exit(0);
            throw new NullPointerException("heavy error occured. second city could not be generated. exiting");
        }
        //move map to center on first city
        map.setOrigin(
                -startCity.getPosition().posX() + Window.getWidth() / 2d,
                -startCity.getPosition().posY() + Window.getHeight() / 2d);
        inventory = new InventoryContainer(this, Window.getWidth() / 3, Window.getHeight());

        player.initInventory();
        for (int i = 0; i < 2; i++) //aid player inventory with some resources his city surrounds
            dayCycleEvent.rollDay(this, plains);

    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.black);
        g.fill(viewPort);

        //only draw to the map buffer what is effectively concidered on screen
        map.drawToScreen(g, (containerGraphics) -> mapElements.forEach(allElements ->
                allElements.stream().filter(element -> getRelativeBounds(element).intersects(viewPort)).forEach(element ->
                        element.draw(containerGraphics)
                ))
        );

        if (hasOpenGui) {
            if (currentGUI != null) {
                currentGUI.draw(g, null);
            }
        } else {
            mapElements.forEach(elements -> elements.stream().
                    filter(element -> element.getBoundingBox().contains(getRelativeMousePosition()) && element instanceof Hoverable).
                    forEach(element -> {
                        Hover hover = ((Hoverable) element).getHoverable(new Coord((int) MouseHandler.mouseX, (int) MouseHandler.mouseY));
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
            gsh.changeGameState(ConquerStates.LEVEL);
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
        for (CitySpot node : cityNodes) {
            if (node.getPosition().equals(coord)) {
                return node.isBought();
            }
        }
        return false;
    }

    public CitySpot getCitySpot(Coord coord) {
        for (CitySpot city : cityNodes) {
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
        return new Coord(coord.posX() + (int) map.originX, coord.posY() + (int) map.originY);
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
