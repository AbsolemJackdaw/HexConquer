package jackdaw.game.container;

import framework.GameState;
import jackdaw.game.Level;
import jackdaw.game.TexLoader;

import java.awt.*;

public class CityGui extends BufferedContainer {

    public CityGui(GameState state, int width, int height) {
        super(state, width, height);
    }

    @Override
    public void draw(Graphics2D g, DrawProvider provider) {
        g.setColor(new Color(50, 50, 50, 230));
        g.fill(Level.viewPort);
        g.drawImage(TexLoader.CITYGUI, (int) originX, (int) originY, getWidth(), getHeight(), null);
    }

    @Override
    public void update() {
        super.update();
    }
}
