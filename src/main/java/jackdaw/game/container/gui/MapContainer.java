package jackdaw.game.container.gui;

import framework.GameState;
import framework.window.Window;
import jackdaw.game.container.BufferedContainer;
import jackdaw.game.container.DrawProvider;

import java.awt.*;

public class MapContainer extends BufferedContainer {

    public MapContainer(GameState state, int width, int height) {
        super(state, width, height);
    }

    @Override
    public void draw(Graphics2D containerGraphics, DrawProvider provider) {

        containerGraphics.setColor(new Color(0x063357));
        containerGraphics.fillRect(0, 0, getWidth(), getHeight());
        if (provider != null)
            provider.draw(containerGraphics);
    }

    @Override
    public void setOrigin(double x, double y) {
        if (x > 0)
            x = 0;
        else if (x < Window.getWidth() - getWidth())
            x = Window.getWidth() - getWidth();

        if (y > 0)
            y = 0;
        else if (y < Window.getHeight() - getHeight())
            y = Window.getHeight() - getHeight();

        super.setOrigin(x, y);
    }

    @Override
    public void move(double dx, double dy) {
        if (this.originX + dx <= 0 && this.originX + dx > Window.getWidth() - getWidth() && this.originY + dy <= 0 && this.originY + dy > Window.getHeight() - getHeight())
            super.move(dx, dy);
    }
}
