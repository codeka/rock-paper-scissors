package au.com.codeka.rps.game;

import java.io.UnsupportedEncodingException;

import au.com.codeka.rps.DebugLog;
import au.com.codeka.rps.WatchConnection;

/**
 * Manages which game state we're currently in.
 */
public class StateManager {
    public static final StateManager i = new StateManager();
    private WatchConnection watchConnection;
    private State currentState;

    private StateManager() {
    }

    public void start(WatchConnection watchConnection) {
        this.watchConnection = watchConnection;
        enterState(new FindingOpponentState(this));
    }

    public WatchConnection getWatchConnection() {
        return watchConnection;
    }

    public void onMessageReceived(String path, String payload) {
        currentState.onMessageReceived(path, payload);
    }

    void enterState(State newState) {
        if (currentState != newState) {
            DebugLog.write("Entering state '%s'.", newState.getClass().getSimpleName());
            currentState = newState;

            try {
                watchConnection.sendMessage(new WatchConnection.Message("/rps/StateChange",
                        currentState.getClass().getSimpleName().getBytes("utf-8")));
            } catch (UnsupportedEncodingException e) {
            }

            currentState.onEnter();
        }
    }
}
