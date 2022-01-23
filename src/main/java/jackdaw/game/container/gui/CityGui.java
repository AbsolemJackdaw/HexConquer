package jackdaw.game.container.gui;

import framework.GameState;
import jackdaw.game.Level;
import jackdaw.game.TexLoader;
import jackdaw.game.container.BufferedContainer;
import jackdaw.game.container.DrawProvider;

import java.awt.*;

public class CityGui extends BufferedContainer {

    public CityGui(GameState state, int width, int height) {
        super(state, width, height);


    }

    @Override
    public void draw(Graphics2D g, DrawProvider provider) {
        g.setColor(new Color(50, 50, 50, 230));
        g.fill(Level.viewPort);
        g.drawImage(TexLoader.CITY_GUI, (int) originX, (int) originY, getWidth(), getHeight(), null);

        Rectangle zoneForSlots = new Rectangle((int) originX, (int) originY, (int) (getWidth() * 0.60), (int) (getHeight() * 0.70));
        Rectangle zoneForKnights = new Rectangle((int) originX, (int) originY + 1 + (int) (getHeight() * 0.70), (int) (getWidth() * 0.60), (int) (getHeight() * 0.30));
        Rectangle zoneForButton = new Rectangle((int) originX+(int) (getWidth() * 0.60), (int) originY, (int) (getWidth() * 0.40), (int) (getHeight() * 0.15));
        Rectangle zoneforCards = new Rectangle((int) originX+(int) (getWidth() * 0.60), (int) originY+(int) (getHeight() * 0.15), (int) (getWidth() * 0.40), (int) (getHeight() * 0.85));

        g.setColor(new Color(50, 50, 50, 230));
        g.fill(zoneForSlots);
        g.setColor(new Color(50, 50, 70, 230));
        g.fill(zoneForKnights);
        g.setColor(new Color(50, 70, 50, 230));
        g.fill(zoneForButton);
        g.setColor(new Color(70, 50, 50, 230));
        g.fill(zoneforCards);

    }

    @Override
    public void update() {
        super.update();
    }
}
