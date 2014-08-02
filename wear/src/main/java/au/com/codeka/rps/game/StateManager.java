package au.com.codeka.rps.game;

import org.json.JSONObject;

import au.com.codeka.rps.GameActivity;
import au.com.codeka.rps.PhoneConnection;

/**
 * Manages the current state of the game.
 */
public class StateManager {
    public static StateManager i = new StateManager();
    private GameActivity gameActivity;
    private PhoneConnection phoneConnection;
    private State currentState;
    private boolean started;

    private StateManager() {
    }

    public void start(GameActivity gameActivity, PhoneConnection phoneConnection) {
        started = true;
        this.gameActivity = gameActivity;
        this.phoneConnection = phoneConnection;
        if (currentState == null) {
            currentState = new FindingOpponentState();
        }
        State state = currentState;
        currentState = null;
        enterState(state);
    }

    public void stop() {
        started = false;
        this.gameActivity = null;
        // enterState(Stopped)?
    }

    public GameActivity getGameActivity() {
        return gameActivity;
    }
    public PhoneConnection getPhoneConnection() { return phoneConnection; }

    public void onFinalResult(JSONObject json) {
        currentState.onFinalResult(json);
    }

    public void enterState(String stateName) {
        Class<?> cls;
        try {
            cls = Class.forName("au.com.codeka.rps.game." + stateName);
            State state = (State) cls.newInstance();
            if (started) {
                enterState(state);
            } else {
                currentState = state;
            }
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
    }

    private void enterState(State newState) {
        if (gameActivity != null && currentState != newState) {
            currentState = newState;
            currentState.onEnter(this);
        }
    }
}
