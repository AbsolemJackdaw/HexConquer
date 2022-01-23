package jackdaw.game.map.level;

import framework.window.Window;
import jackdaw.game.DayCycleEvent;
import jackdaw.game.Level;
import jackdaw.game.TexLoader;
import jackdaw.game.container.BufferedContainer;
import jackdaw.game.container.gui.CityGui;
import jackdaw.game.map.Coord;
import jackdaw.game.map.GuiAble;
import jackdaw.game.map.Hover;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.Objects;

public class CitySpot extends PlaceableElement implements GuiAble {
    private final Shape boundingBox;
    private final Rectangle imageSpace;
    private boolean hasBuildRoadNode;

    private DayCycleEvent.DAWNEVENTS currentDayEvent = DayCycleEvent.DAWNEVENTS.NONE;

    public CitySpot(Level level, double posX, double posY) {
        super(level, posX, posY);
        int size = (int) (15 * Window.getScale());
        boundingBox = new Ellipse2D.Double((int) posX - size / 2f, (int) posY - size / 2f, size, size);
        size = (int) (16 * Window.getScale());
        imageSpace = new Rectangle(getPosition().getPosX() - (int) (size * 2.5), (int) (getPosition().posY() - size * 3.0), size * 5, size * 5);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CitySpot intersection && intersection.getPosition().equals(getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPosition().getPosX(), getPosition().getPosY());
    }

    @Override
    public void update() {

    }

    @Override
    public Shape getBoundingBox() {
        return boundingBox;
    }

    @Override
    public void buy(String buyer) {
        if (canBuy()) {
            level.getPlayerByName(buyer).ifPresent(player -> {
                if (player.canPay(cost())) {
                    purchasePlot(buyer);
                    for (MatStack matStack : cost()) {
                        player.substractWith(matStack);
                    }
                }
            });
        }
    }

    public void purchasePlot(String buyer) {
        bought = true;
        level.exploitSurrounds(this);
        this.setOwner(buyer);
    }

    @Override
    public void draw(Graphics2D g) {
        if (bought) {
            g.setClip(imageSpace);
            g.drawImage(TexLoader.CITY, imageSpace.x, imageSpace.y, imageSpace.width, imageSpace.height, null);

            switch (currentDayEvent) {
                case GOODYEAR -> g.drawImage(TexLoader.GOODYEAR, imageSpace.x + imageSpace.width / 4, imageSpace.y, imageSpace.width / 2, imageSpace.height / 2, null);
                case BLACKDEATH -> g.drawImage(TexLoader.BADYEAR, imageSpace.x + imageSpace.width / 4, imageSpace.y, imageSpace.width / 2, imageSpace.height / 2, null);
            }

        } else if (hasBuildRoadNode) {
            g.setColor(Color.DARK_GRAY);
            g.setClip(getBoundingBox());
            g.fill(getBoundingBox());
        }
        g.setClip(0, 0, Window.getWidth(), Window.getHeight());

    }

    public void attachRoad() {
        hasBuildRoadNode = true;
    }

    public boolean connectsWithRoad() {
        return hasBuildRoadNode;
    }

    @Override
    public MatStack[] cost() {
        return new MatStack[]{new MatStack(Material.METAL, 10), new MatStack(Material.WOOD, 10), new MatStack(Material.CLAY, 10)};
    }

    @Override
    public BufferedContainer getGui() {
        if (bought) {
            CityGui gui = new CityGui(level, Level.viewPort.width - Window.getGameScale(100), Level.viewPort.height - Window.getGameScale(100));
            gui.setOrigin(Window.getGameScale(50), Window.getGameScale(50));
            return gui;
        }
        return null;
    }


    @Override
    public boolean canBuy() {
        byte isRoadFreeCounter = 0;
        boolean atLeastOneRoadHasBeenBought = false;
        Collection<Road> roads = level.nodeRoads(this);
        if (!roads.isEmpty()) {
            for (Road road : roads) {
                if (!atLeastOneRoadHasBeenBought)
                    atLeastOneRoadHasBeenBought = road.isBought();
                if (!level.isCityBuild(road.getLinkedNodeA()) && !level.isCityBuild(road.getLinkedNodeB()))
                    isRoadFreeCounter++;
            }
        }
        return isRoadFreeCounter == roads.size() && atLeastOneRoadHasBeenBought;
    }

    @Override
    public Hover getHoverable(Coord coord) {
        Hover theSuper = super.getHoverable(coord);
        return new Hover(theSuper).withDraw(g -> {
            Rectangle s = level.getRelativeShape(imageSpace);
            if (canBuy()) {
                g.drawImage(TexLoader.CITY_TRANSPARENT, s.x, s.y, s.width, s.height, null);
            } else if (bought) {
                g.drawImage(TexLoader.CITY_POP, s.x + s.width / 4, s.y + s.height / 4, s.width / 2, s.height / 2, null);
            }
            theSuper.draw(g);
        });
    }

    //TODO upgrades for cities
    public int getResourceMultiplier(Material material) {

        return switch (material) {
            //TODO double when upgraded
            //TODO double with upgrades
            default -> 1;
        };
    }

    public void setCurrentDayEvent(DayCycleEvent.DAWNEVENTS currentDayEvent) {
        this.currentDayEvent = currentDayEvent;
    }
}
