package au.com.codeka.rps.game;

import java.io.UnsupportedEncodingException;

import au.com.codeka.rps.WatchConnection;

/**
 * Manages which game state we're currently in.
 */
public class StateManager {
    private final WatchConnection watchConnection;
    private State currentState;

    public StateManager(WatchConnection watchConnection) {
        this.watchConnection = watchConnection;
        enterState(new FindingOpponentState(this));
    }

    public WatchConnection getWatchConnection() {
        return watchConnection;
    }

    public void onMatchStarted(MatchInfo matchInfo) {
        enterState(new GameRunningState(this, matchInfo));
    }

    private void enterState(State newState) {
        if (currentState != newState) {
            currentState = newState;
            currentState.onEnter();
            try {
                watchConnection.sendMessage(new WatchConnection.Message("/rps/StateChange",
                        currentState.getClass().getSimpleName().getBytes("utf-8")));
            } catch (UnsupportedEncodingException e) {
            }
        }
    }
}
