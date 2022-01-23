package jackdaw.game.map.level;

import jackdaw.game.Level;
import jackdaw.game.map.*;

public abstract class PlaceableElement extends Element implements Buyable, Hoverable {

    protected boolean bought;

    public PlaceableElement(Level level, Coord coord) {
        super(level, coord);
    }

    @Override
    public Hover getHoverable(Coord coord) {
        return Hover.buyableMouseHover(coord, this);
    }

    @Override
    public boolean isBought() {
        return bought;
    }

}
