package au.com.codeka.rps.game;

import au.com.codeka.rps.GameActivity;

/**
 * Manages the current state of the game.
 */
public class StateManager {
    private GameActivity gameActivity;
    private State currentState;

    public StateManager(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
        enterState(new FindingOpponentState(this));
    }

    public GameActivity getGameActivity() {
        return gameActivity;
    }

    private void enterState(State newState) {
        if (currentState != newState) {
            currentState = newState;
            currentState.onEnter();
        }
    }
}
