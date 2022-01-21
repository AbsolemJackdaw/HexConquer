package jackdaw.game.container.button;

import java.awt.*;

public abstract class Button {
    Rectangle box;

    public Button(int x, int y, int sx, int sy) {
        box = new Rectangle(x, y, sx, sy);
    }

    public abstract void draw(Graphics2D g);

    public abstract void update();

}
