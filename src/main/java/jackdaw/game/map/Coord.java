package jackdaw.game.map;

import java.awt.*;
import java.util.Objects;

public record Coord(double posX, double posY) {

    public Coord(Point point) {
        this(point.x, point.y);
    }

    public int getPosX() {
        return (int) posX;
    }

    public int getPosY() {
        return (int) posY;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Coord coord && coord.getPosX() == getPosX() && coord.getPosY() == getPosY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }

    public Coord move(double dx, double dy) {
        return new Coord(posX + dx, posY + dy);
    }
}
