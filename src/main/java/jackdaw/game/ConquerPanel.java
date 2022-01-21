package jackdaw.game;

import framework.GamePanel;
import framework.GameStateHandler;

import java.awt.*;

public class ConquerPanel extends GamePanel {
    @Override
    public GameStateHandler getCustomGameStateHandler() {
        return new ConquerStates();
    }

}
