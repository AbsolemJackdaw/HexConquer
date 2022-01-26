package jackdaw.game.level;

import framework.window.Window;
import jackdaw.game.Level;
import jackdaw.game.TexLoader;
import jackdaw.game.level.map.Coord;
import jackdaw.game.level.map.Hover;
import jackdaw.game.player.Player;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.awt.*;
import java.util.Objects;

public class Road extends PlaceableElement {
    private final Coord linkedNodeA, linkedNodeB;
    private final Polygon road;
    private Coord boundingBoxCoordA, boundingBoxCoordB, boundingBoxCoordC, boundingBoxCoordD;
    private Direction direction = Direction.N;

    public Road(Level level, Coord start, Coord end) {
        super(level, start);
        this.linkedNodeA = this.boundingBoxCoordA = this.boundingBoxCoordC = start;
        this.linkedNodeB = this.boundingBoxCoordB = this.boundingBoxCoordD = end;
        int offset = (int) (8 * Window.getScale());
        boolean horizontalRoad = start.posY() == end.posY();
        if (horizontalRoad) {
            if (start.posX() > end.posX()) {
                offset *= -1;
                direction = Direction.S;
            } else
                direction = Direction.N;
            this.boundingBoxCoordA = new Coord(start.posX() + offset, start.posY() + offset);
            this.boundingBoxCoordC = new Coord(start.posX() + offset, start.posY() - offset);
            this.boundingBoxCoordB = new Coord(end.posX() - offset, end.posY() + offset);
            this.boundingBoxCoordD = new Coord(end.posX() - offset, end.posY() - offset);

        } else {
            int offX = (int) (Math.cos(Math.toRadians(60)) * offset);
            int offY = (int) (Math.sin(Math.toRadians(60)) * offset);
            int offXEnd = offX;
            int offYEnd = offY;

            if (start.posX() < end.posX() && start.posY() < end.posY()) {
                offXEnd *= -1;
                offYEnd *= -1;
                direction = Direction.NE;
            }

            if (start.posX() > end.posX() && start.posY() < end.posY()) {
                offX *= -1;
                offYEnd *= -1;
                direction = Direction.SE;

            }

            if (start.posX() > end.posX() && start.posY() > end.posY()) {
                offX *= -1;
                offY *= -1;
                direction = Direction.SW;
            }

            if (start.posX() < end.posX() && start.posY() > end.posY()) {
                offY *= -1;
                offXEnd *= -1;
                direction = Direction.NW;
            }

            this.boundingBoxCoordA = new Coord(start.posX() + offX + offset, start.posY() + offY);
            this.boundingBoxCoordC = new Coord(start.posX() + offX - offset, start.posY() + offY);
            this.boundingBoxCoordB = new Coord(end.posX() + offXEnd + offset, end.posY() + offYEnd);
            this.boundingBoxCoordD = new Coord(end.posX() + offXEnd - offset, end.posY() + offYEnd);
        }

        this.road = createRoad();
    }

    private Polygon createRoad() {
        Polygon road = new Polygon();
        road.addPoint(boundingBoxCoordA.posX(), boundingBoxCoordA.posY());
        road.addPoint(boundingBoxCoordC.posX(), boundingBoxCoordC.posY());
        road.addPoint(boundingBoxCoordD.posX(), boundingBoxCoordD.posY());
        road.addPoint(boundingBoxCoordB.posX(), boundingBoxCoordB.posY());
        return road;
    }

    public Polygon getRoad() {
        return road;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g) {
        if (bought) {

            int offX = (int) Level.bone;
            int offY = 0;

            switch (direction) {
                case SE -> {
                    offX /= 4;
                    offY = (int) Level.bone / 2;
                }
                case NE -> {
                    offY = (int) Level.bone / 2;
                    offX /= 1.3;
                }
                case S -> offX = 0;
                case NW -> offY = -(int) Level.bone / 2;
                case SW -> {
                    offX /= 2;
                    offY = -(int) Level.bone / 2;
                }
            }
            g.setClip(getRoad());
            g.drawImage(TexLoader.ROAD, getPosition().posX() - (int) Level.bone + offX, getPosition().posY() + offY - (int) Level.bone / 2, (int) Level.bone, (int) Level.bone, null);
            g.setClip(Level.viewPort);

        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Road road &&
                (linkedNodeA.equals(road.linkedNodeB) || linkedNodeA.equals(road.linkedNodeA)) &&
                (linkedNodeB.equals(road.linkedNodeA) || linkedNodeB.equals(road.linkedNodeB));
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkedNodeA, linkedNodeB);
    }

    @Override
    public Shape getBoundingBox() {
        return road;
    }

    @Override
    public void buy(Player player) {

        if (player.canPay(cost()) && !bought) {
            //if a node connects with a city, or doesn't: set road and opposite node as connected
            if (canBuyCheckNodeA()) {
                level.getCitySpot(linkedNodeB).attachRoad();
                bought = true;
            } else if (canBuyCheckNodeB()) {
                level.getCitySpot(linkedNodeA).attachRoad();
                bought = true;
            }
            if (bought) {
                this.setOwner(player);
                for (MatStack matStack : cost()) {
                    player.substractWith(matStack);
                }
            }
        }
    }

    public Coord getLinkedNodeA() {
        return linkedNodeA;
    }

    public Coord getLinkedNodeB() {
        return linkedNodeB;
    }


    @Override
    public MatStack[] cost() {
        return new MatStack[]{new MatStack(Material.CLAY, 2), new MatStack(Material.WOOD, 2)};
    }

    private boolean canBuyCheckNodeA() {
        return level.isCityBuild(linkedNodeA) || level.getCitySpot(linkedNodeA).connectsWithRoad();
    }

    private boolean canBuyCheckNodeB() {
        return level.isCityBuild(linkedNodeB) || level.getCitySpot(linkedNodeB).connectsWithRoad();
    }

    @Override
    public boolean canBuy() {
        return canBuyCheckNodeA() || canBuyCheckNodeB();
    }

    @Override
    public Hover getHoverable(Coord coord) {
        Hover theSuper = super.getHoverable(coord);
        return new Hover(theSuper).withDraw(g -> {

            if (!bought && canBuy()) {
                g.setColor(new Color(114, 70, 25, 100));
                g.fill(level.getRelativeShape(getRoad()));
            }
            theSuper.draw(g);
        });
    }

    public boolean sharePoint(Coord coord) {
        return getLinkedNodeA().equals(coord) || getLinkedNodeB().equals(coord);
    }

    public boolean sharePoint(Road road) {
        return equals(road) || getLinkedNodeA().equals(road.getLinkedNodeB()) || getLinkedNodeB().equals(road.getLinkedNodeA());
    }

    public boolean shareUnbuildPoint(Road road) {
        if (equals(road))
            return false;
        if (getLinkedNodeB().equals(road.getLinkedNodeB()) || getLinkedNodeB().equals(road.getLinkedNodeA()))
            return !level.isCityBuild(getLinkedNodeB());
        if (getLinkedNodeA().equals(road.getLinkedNodeB()) || getLinkedNodeA().equals(road.getLinkedNodeA()))
            return !level.isCityBuild(getLinkedNodeA());
        return false;
    }

    @Override
    public String toString() {
        return "Road{" +
                "linkedNodeA=" + linkedNodeA +
                ", linkedNodeB=" + linkedNodeB +
                '}';
    }

    public enum Direction {
        N, S, SE, NE, SW, NW
    }
}
