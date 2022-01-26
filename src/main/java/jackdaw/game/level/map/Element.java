package jackdaw.game.level.map;

import jackdaw.game.Level;
import jackdaw.game.player.Player;

import java.awt.*;

public abstract class Element implements Comparable<Element> {
    private final Coord absolutePosition;
    public Level level;
    private Player owner;

    public Element(Level level, Coord coord) {
        this.level = level;
        this.absolutePosition = coord;
    }

    public abstract void update();

    public abstract void draw(Graphics2D g);

    public abstract Shape getBoundingBox();

    public Coord getPosition() {
        return absolutePosition;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        if (owner != null)
            this.owner = owner;
        else
            throw new IllegalArgumentException("player registered to city was null");
    }

    @Override
    public int compareTo(Element o) {
        return o.getPosition().equals(getPosition()) ? 0 : 1;
    }
}
