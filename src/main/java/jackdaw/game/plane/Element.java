package jackdaw.game.plane;

import jackdaw.game.Level;

import java.awt.*;

public abstract class Element {
    private final Coord absolutePosition;
    public Level level;
    public String owner;

    public Element(Level level, double x, double y) {
        this.level = level;
        this.absolutePosition = new Coord(x, y);
    }

    public abstract void update();

    public abstract void draw(Graphics2D g);

    public abstract Shape getBoundingBox();

    public Coord getPosition() {
        return absolutePosition;
    }
}
