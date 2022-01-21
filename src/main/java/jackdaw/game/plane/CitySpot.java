package jackdaw.game.plane;

import framework.window.Window;
import jackdaw.game.Level;
import jackdaw.game.TexLoader;
import jackdaw.game.container.BufferedContainer;
import jackdaw.game.container.CityGui;
import jackdaw.game.player.MatStack;
import jackdaw.game.resources.Material;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.Objects;

public class CitySpot extends Element implements Buyable, GuiAble {
    private int size = (int) (15 * Window.getScale());
    private boolean build;
    private Shape boundingBox;
    private boolean hasBuildRoadNode;

    public CitySpot(Level level, double posX, double posY) {
        super(level, posX, posY);
        boundingBox = new Ellipse2D.Double((int) posX - size / 2f, (int) posY - size / 2f, size, size);
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
        if (isRoadFreeCounter == roads.size() && atLeastOneRoadHasBeenBought) {
            level.getPlayerByName(buyer).ifPresent(player -> {
                if (player.canPay(cost())) {
                    purchasePlot();
                    for (MatStack matStack : cost()) {
                        player.substractWith(matStack);
                    }
                }
            });
        }
    }

    public void purchasePlot() {
        build = true;
        size *= 2;
        level.exploitSurrounds(this);
    }

    @Override
    public void draw(Graphics2D g) {

        int size = (int) (16 * Window.getScale());

        if (build) {
            Rectangle r = new Rectangle(getPosition().getPosX() - (int) (size * 2.5), (int) (getPosition().posY() - size * 3.0), size * 5, size * 5);
            g.setClip(r);
            g.drawImage(TexLoader.CITY, r.x, r.y, r.width, r.height, null);
        } else if (hasBuildRoadNode) {
            Rectangle r = new Rectangle(getPosition().getPosX() - (int) (size * 1.5), (int) (getPosition().posY() - size * 1.5), size * 3, size * 3);
//            g.setClip(r);
//            g.drawImage(TexLoader.CROSSING, r.x, r.y, r.width, r.height, null);
            g.setColor(Color.DARK_GRAY);
            g.setClip(getBoundingBox());
            g.fill(getBoundingBox());
        }
        g.setClip(0, 0, Window.getWidth(), Window.getHeight());


    }

    public boolean isBuild() {
        return build;
    }

    public void attachRoad(String buyer) {
        hasBuildRoadNode = true;
        owner = buyer;
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
        if (build) {
            CityGui gui = new CityGui(level, Level.viewPort.width - Window.getGameScale(100), Level.viewPort.height - Window.getGameScale(100));
            gui.setOrigin(Window.getGameScale(50), Window.getGameScale(50));
            return gui;
        }
        return null;
    }
}
