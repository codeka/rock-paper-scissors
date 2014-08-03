package au.com.codeka.rps.game;

import android.os.Handler;

import au.com.codeka.rps.DebugLog;

/**
 * This is the state when the game has started and the player is making their choice.
 */
public class GameRunningState extends State {
    private final StateManager stateManager;
    private final MatchInfo matchInfo;
    private final Handler handler;

    public GameRunningState(StateManager stateManager, MatchInfo matchInfo) {
        this.stateManager = stateManager;
        this.matchInfo = matchInfo;
        handler = new Handler();
    }

    public void onEnter() {
        DebugLog.write("Game Started: match-id=%s round=%d", matchInfo.getMatchId(),
                matchInfo.getRound());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            DebugLog.write("Game finished, fetching player choice from watch.");
            stateManager.enterState(new AwaitingPlayerChoiceState(stateManager, matchInfo));
            }
        }, 6000);
    }
}
