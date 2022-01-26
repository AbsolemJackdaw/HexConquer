package jackdaw.game.level.map;

import java.awt.*;
import java.util.Objects;

public class Coord {

    private final int posX, posY;

    public Coord(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int posX() {
        return posX;
    }

    public int posY() {
        return posY;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Coord coord && coord.posX() == posX() && coord.posY() == posY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }

    public Coord move(int dx, int dy) {
        return new Coord(posX + dx, posY + dy);
    }

    @Override
    public String toString() {
        return "Coord{" +
                "posX=" + posX +
                ", posY=" + posY +
                '}';
    }

    /**
     * returns 1 or -1 depending on where the coord is positioned regarding the origin.
     * useful to obtain values clock wise from a value, mostly to construct shapes from
     */
    public int compareTo(Coord origin, Coord comparing) {
        if (origin == null)
            return 0;

        int compared = 0;
        double angleA = Math.atan2(this.posY - origin.posY, this.posX - origin.posX);
        double angleB = Math.atan2(comparing.posY - origin.posY, comparing.posX - origin.posX);
        compared = Double.compare(angleB, angleA);
        return compared;
    }

    /**
     * returns the euclidean distance to the given point from the instance this is called on
     */
    public int distanceTo(Coord o) {
        int x = posX - o.posX;
        int y = posY - o.posY;
        return (int) Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
    }

    public Point point(){
        return new Point(posX,posY);
    }
}
