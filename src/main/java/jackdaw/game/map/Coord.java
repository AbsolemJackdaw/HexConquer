package jackdaw.game.map;

import java.util.Objects;

public class Coord implements Comparable<Coord> {

    private final int posX, posY;
    private Coord originToCompare;

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
     * use compareTo(origin, comparing) intsead
     */
    @Override
    @Deprecated
    public int compareTo(Coord o) {
        if (originToCompare == null)
            return 0;

        int compared = 0;
        double angleA = Math.atan2(this.posY - originToCompare.posY, this.posX - originToCompare.posX);
        double angleB = Math.atan2(o.posY - originToCompare.posY, o.posX - originToCompare.posX);
        compared = Double.compare(angleB, angleA);
        originToCompare = null; // reset so the next check doesnt compare to an old value
        return compared;
    }

    public int compareTo(Coord origin, Coord comparing) {
        this.originToCompare = origin;
        return compareTo(comparing);
    }
}
