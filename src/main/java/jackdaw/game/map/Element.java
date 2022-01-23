package jackdaw.game.map;

import jackdaw.game.Level;

import java.awt.*;

public abstract class Element {
    private final Coord absolutePosition;
    public Level level;
    private String owner;

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        if (owner != null && !owner.isEmpty() && !owner.isBlank())
            this.owner = owner;
        else
            throw new IllegalArgumentException("player name cannot be emtpy or nihil !");
    }
}
