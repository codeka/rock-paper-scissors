package au.com.codeka.rps.game;

import au.com.codeka.rps.GameFragment;

/**
 * This is the state we enter when we've found an opponent and the game itself is running.
 */
public class GameRunningState extends State {
    private StateManager stateManager;

    @Override
    public void onEnter(StateManager stateManager) {
        this.stateManager = stateManager;
        this.stateManager.getGameActivity().setFragment(new GameFragment());
    }
}
