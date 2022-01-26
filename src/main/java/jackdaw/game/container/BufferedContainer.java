package jackdaw.game.container;

import framework.GameState;
import framework.window.Window;
import jackdaw.game.container.button.Button;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public abstract class BufferedContainer extends BufferedImage {

    public static final Font FONT = new Font(Font.MONOSPACED, Font.ITALIC | Font.BOLD, Window.getGameScale(32));
    static final Random rand = new Random();
    protected final GameState state; //the gamestate this container is in;
    public double originX, originY;
    protected ArrayList<Button> buttons = new ArrayList<>();
    private double cachedX, cachedY;

    public BufferedContainer(GameState state, int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);
        this.state = state;
    }

    public void drawToScreen(Graphics2D graphics2D, DrawProvider provider) {
        Graphics2D g = createGraphics();
        draw(g, provider);
        graphics2D.drawImage(this, (int) originX, (int) originY, getWidth(), getHeight(), null);

        g.dispose();
    }

    public abstract void draw(Graphics2D containerGraphics, DrawProvider provider);

    public void update() {
    }

    public void move(double dx, double dy) {
        originX += dx;
        originY += dy;
    }

    public void setOrigin(double x, double y) {
        cachedX = this.originX = x;
        cachedY = this.originY = y;
    }
}
