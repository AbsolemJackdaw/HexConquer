package jackdaw.game;

import framework.GameState;
import framework.GameStateHandler;

public class ConquerStates extends GameStateHandler {
    public static final String LEVEL = "level";

    public ConquerStates() {
        addGameState(TexLoader.class, GameState.FIRST_SCREEN);
        addGameState(Level.class, LEVEL);
    }
}
