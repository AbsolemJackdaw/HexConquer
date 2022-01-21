package jackdaw.game.container;

import java.awt.*;

@FunctionalInterface
public interface DrawProvider {
    void draw(Graphics2D containerGraphics);
}
