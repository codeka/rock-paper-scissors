package au.com.codeka.rps.game;

import au.com.codeka.rps.DebugLog;

/**
 * This is the state we're in once the game has finished and we're waiting for the watch to send
 * us the player's choice.
 */
public class AwaitingPlayerChoiceState extends State {
    private final StateManager stateManager;
    private final MatchInfo matchInfo;

    public AwaitingPlayerChoiceState(StateManager stateManager, MatchInfo matchInfo) {
        this.stateManager = stateManager;
        this.matchInfo = matchInfo;
    }

    @Override
    public void onMessageReceived(String path, String payload) {
        if (path.equals("/rps/PlayerChoice")) {
            String choice = payload;
            DebugLog.write("Player choice: %s", choice);

            stateManager.enterState(new AwaitingResultState(stateManager, matchInfo, choice));
        }
    }
}
