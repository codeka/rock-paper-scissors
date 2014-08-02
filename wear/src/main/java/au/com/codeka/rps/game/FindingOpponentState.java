package au.com.codeka.rps.game;

import au.com.codeka.rps.FindingOpponentFragment;

/**
 * The initial state of the game, while we're waiting for an opponent to join.
 */
public class FindingOpponentState extends State {
    private StateManager stateManager;

    public FindingOpponentState() {
    }

    @Override
    public void onEnter(StateManager stateManager) {
        this.stateManager = stateManager;
        this.stateManager.getGameActivity().setFragment(new FindingOpponentFragment());
    }
}
