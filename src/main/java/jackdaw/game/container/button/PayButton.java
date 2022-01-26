package jackdaw.game.container.button;

import framework.input.MouseHandler;
import jackdaw.game.Level;
import jackdaw.game.TexLoader;
import jackdaw.game.level.map.Buyable;
import jackdaw.game.level.map.Coord;
import jackdaw.game.level.map.Hover;
import jackdaw.game.level.map.Hoverable;
import jackdaw.game.player.Player;
import jackdaw.game.resources.MatStack;

import java.awt.*;

public class PayButton extends ClickButton implements Hoverable, Buyable {
    MatStack[] cost;

    public PayButton(int x, int y, int sx, int sy, MatStack[] cost, OnClick click) {
        super(x, y, sx, sy, click);
        this.cost = cost;
    }

    @Override
    public void draw(Graphics2D g) {

        Coord mouse = new Coord((int) MouseHandler.mouseX, (int) MouseHandler.mouseY);
        if (box.contains(mouse.point()))
            if (getHoverable(mouse) != Hover.EMPTY)
                getHoverable(mouse).draw(g);
    }

    @Override
    public MatStack[] cost() {
        return cost;
    }

    @Override
    public void buy(Player owner) {

    }

    @Override
    public boolean isBought() {
        return false;
    }

    @Override
    public boolean canBuy() {
        return true;
    }

    @Override
    public Hover getHoverable(Coord coord) {
        return Hover.buyableMouseHover(coord, this);
    }
}
