package au.com.codeka.rps.game;

import au.com.codeka.rps.DebugLog;

/**
 * This is the state when the game has started and the player is making their choice.
 */
public class GameRunningState extends State {
    private final StateManager stateManager;
    private final MatchInfo matchInfo;

    public GameRunningState(StateManager stateManager, MatchInfo matchInfo) {
        this.stateManager = stateManager;
        this.matchInfo = matchInfo;
    }

    public void onEnter() {
        DebugLog.write("Game Started: match-id=%s", matchInfo.getMatchId());
    }
}
