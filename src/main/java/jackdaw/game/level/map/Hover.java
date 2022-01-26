package jackdaw.game.level.map;

import framework.window.Window;
import jackdaw.game.TexLoader;
import jackdaw.game.container.BufferedContainer;
import jackdaw.game.container.DrawProvider;
import jackdaw.game.resources.MatStack;

import java.awt.*;

public class Hover {
    public static final Hover EMPTY = new Hover(new Coord(0, 0), 0, 0) {
        @Override
        public void draw(Graphics2D g) {
        }
    };
    int width, height;
    Coord coord;
    DrawProvider drawProvider = null;

    public Hover(Coord coord, int w, int h) {
        this.coord = coord;
        this.width = w; //Window.getGameScale(w);
        this.height = h;//Window.getGameScale(h);
    }

    public Hover(Hover hover) {
        this.width = hover.width;
        this.height = hover.height;
        this.coord = hover.coord;
    }

    public static Hover buyableMouseHover(Coord coord, Buyable buyable) {
        if (!buyable.isBought()) {
            if (buyable.canBuy()) {
                int size = Window.getGameScale(32);
                int width = 3 * size;
                return new Hover(coord, width, (buyable.cost().length) * size).withDraw(g -> {
                    int itt = 0;
                    for (MatStack matStack : buyable.cost()) {
                        g.setFont(BufferedContainer.FONT);
                        g.setColor(Color.white);
                        matStack.draw(coord.posX(), coord.posY() + (itt * size), size).draw(g);
                        itt++;
                    }
                });
            }
        }
        return Hover.EMPTY;
    }

    public void draw(Graphics2D g) {
        g.drawImage(TexLoader.HOVER, coord.posX(), coord.posY(), width, height, null);
        if (drawProvider != null)
            drawProvider.draw(g);
    }

    public Hover withDraw(DrawProvider drawProvider) {
        this.drawProvider = drawProvider;
        return this;
    }
}
