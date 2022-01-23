package jackdaw.game.map;

import framework.window.Window;
import jackdaw.game.TexLoader;
import jackdaw.game.container.DrawProvider;

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

    public void draw(Graphics2D g) {
        g.drawImage(TexLoader.HOVER, coord.getPosX(), coord.getPosY(), width, height, null);
        if (drawProvider != null)
            drawProvider.draw(g);
    }

    public Hover withDraw(DrawProvider drawProvider) {
        this.drawProvider = drawProvider;
        return this;
    }


}
