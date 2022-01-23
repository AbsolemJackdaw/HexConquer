package jackdaw.game.map.level;

import framework.window.Window;
import jackdaw.game.map.Hover;
import jackdaw.game.map.Hoverable;
import jackdaw.game.Level;
import jackdaw.game.map.Buyable;
import jackdaw.game.map.Coord;
import jackdaw.game.map.Element;
import jackdaw.game.resources.MatStack;

import java.util.Arrays;
import java.util.Optional;

public abstract class PlaceableElement extends Element implements Buyable, Hoverable {

    protected boolean bought;

    public PlaceableElement(Level level, double x, double y) {
        super(level, x, y);
    }

    @Override
    public Hover getHoverable(Coord coord) {
        if (!bought) {
            if (canBuy()) {
                int size = Window.getGameScale(32);
                Optional<MatStack> stack = Arrays.stream(cost()).reduce((matStack, matStack2) -> matStack.getAmmount() > matStack2.getAmmount() ? matStack : matStack2);
                int width = stack.map(value -> value.getAmmount() * size).orElse(0);
                return new Hover(coord, width, (cost().length) * size).withDraw(g -> {
                    int itt = 0;
                    for (MatStack matStack : cost()) {
                        matStack.draw(coord.getPosX(), coord.getPosY() + (itt * size), size).draw(g);
                        itt++;
                    }
                });
            }
        }
        return Hover.EMPTY;
    }

    @Override
    public boolean isBought() {
        return bought;
    }

}
