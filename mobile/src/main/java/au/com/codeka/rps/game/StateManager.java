package au.com.codeka.rps.game;

/**
 * Manages which game state we're currently in.
 */
public class StateManager {
    private State currentState;

    public StateManager() {
        enterState(new FindingOpponentState(this));
    }

    public void onMatchStarted(MatchInfo matchInfo) {
        enterState(new GameRunningState(this, matchInfo));
    }

    private void enterState(State newState) {
        if (currentState != newState) {
            currentState = newState;
            currentState.onEnter();
        }
    }
}
