package au.com.codeka.rps.game;

import au.com.codeka.rps.FindingOpponentFragment;

/**
 * The initial state of the game, while we're waiting for an opponent to join.
 */
public class FindingOpponentState extends State {
    private final StateManager stateManager;

    public FindingOpponentState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        stateManager.getGameActivity().setFragment(new FindingOpponentFragment());
    }
}
