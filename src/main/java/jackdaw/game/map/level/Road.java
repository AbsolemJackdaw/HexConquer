package jackdaw.game.map.level;

import framework.window.Window;
import jackdaw.game.Level;
import jackdaw.game.TexLoader;
import jackdaw.game.map.Coord;
import jackdaw.game.map.Hover;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.awt.*;
import java.util.Objects;

public class Road extends PlaceableElement {
    private final Coord linkedNodeA, linkedNodeB;
    private Coord start, end;
    private Coord startB, endB;
    private Polygon road;
    private Direction direction = Direction.N;

    public Road(Level level, Coord start, Coord end, boolean horizontalRoad) {
        super(level, start.getPosX(), start.posY());
        this.linkedNodeA = this.start = this.startB = start;
        this.linkedNodeB = this.end = this.endB = end;
        int offset = (int) (8 * Window.getScale());
        if (horizontalRoad) {
            if (start.getPosX() > end.getPosX()) {
                offset *= -1;
                direction = Direction.S;
            } else
                direction = Direction.N;
            this.start = new Coord(start.getPosX() + offset, start.getPosY() + offset);
            this.startB = new Coord(start.getPosX() + offset, start.getPosY() - offset);
            this.end = new Coord(end.getPosX() - offset, end.getPosY() + offset);
            this.endB = new Coord(end.getPosX() - offset, end.getPosY() - offset);

        } else {
            double offX = (Math.cos(Math.toRadians(60)) * offset);
            double offY = (Math.sin(Math.toRadians(60)) * offset);
            double offXEnd = offX;
            double offYEnd = offY;

            if (start.getPosX() < end.getPosX() && start.getPosY() < end.getPosY()) {
                offXEnd *= -1;
                offYEnd *= -1;
                direction = Direction.NE;
            }

            if (start.getPosX() > end.getPosX() && start.getPosY() < end.getPosY()) {
                offX *= -1;
                offYEnd *= -1;
                direction = Direction.SE;

            }

            if (start.getPosX() > end.getPosX() && start.getPosY() > end.getPosY()) {
                offX *= -1;
                offY *= -1;
                direction = Direction.SW;
            }

            if (start.getPosX() < end.getPosX() && start.getPosY() > end.getPosY()) {
                offY *= -1;
                offXEnd *= -1;
                direction = Direction.NW;
            }

            this.start = new Coord(start.getPosX() + offX + offset, start.getPosY() + offY);
            this.startB = new Coord(start.getPosX() + offX - offset, start.getPosY() + offY);
            this.end = new Coord(end.getPosX() + offXEnd + offset, end.getPosY() + offYEnd);
            this.endB = new Coord(end.getPosX() + offXEnd - offset, end.getPosY() + offYEnd);
        }

        this.road = createRoad();
    }

    private Polygon createRoad() {
        Polygon road = new Polygon();
        road.addPoint(start.getPosX(), start.getPosY());
        road.addPoint(startB.getPosX(), startB.getPosY());
        road.addPoint(endB.getPosX(), endB.getPosY());
        road.addPoint(end.getPosX(), end.getPosY());
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
            g.setClip(createRoad());
            g.drawImage(TexLoader.ROAD, getPosition().getPosX() - (int) Level.bone + offX, getPosition().getPosY() + offY - (int) Level.bone / 2, (int) Level.bone, (int) Level.bone, null);
            g.setClip(0, 0, Window.getWidth(), Window.getHeight());

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
        return Objects.hash(start, end, startB, endB);
    }

    @Override
    public Shape getBoundingBox() {
        return road;
    }

    @Override
    public void buy(String buyer) {
        level.getPlayerByName(buyer).ifPresent(player -> {
            if (player.canPay(cost()) && !bought) {
                //if a node connects with a city, or doesn't set road and opposite node as connected
                if (canBuyCheckNodeA()) {
                    level.getCitySpot(linkedNodeB).attachRoad();
                    bought = true;
                } else if (canBuyCheckNodeB()) {
                    level.getCitySpot(linkedNodeA).attachRoad();
                    bought = true;
                }
                if (bought) {
                    this.setOwner(buyer);
                    for (MatStack matStack : cost()) {
                        player.substractWith(matStack);
                    }
                }
            }
        });
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

    public enum Direction {
        N, S, SE, NE, SW, NW;
    }
}
