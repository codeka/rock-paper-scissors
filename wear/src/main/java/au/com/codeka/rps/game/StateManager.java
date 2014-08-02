package au.com.codeka.rps.game;

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

    private StateManager() {
    }

    public void start(GameActivity gameActivity, PhoneConnection phoneConnection) {
        this.gameActivity = gameActivity;
        this.phoneConnection = phoneConnection;
        enterState(new FindingOpponentState());
    }

    public void stop() {
        this.gameActivity = null;
        // enterState(Stopped)?
    }

    public GameActivity getGameActivity() {
        return gameActivity;
    }
    public PhoneConnection getPhoneConnection() { return phoneConnection; }

    public void enterState(String stateName) {
        Class<?> cls;
        try {
            cls = Class.forName("au.com.codeka.rps.game." + stateName);
            enterState((State) cls.newInstance());
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
