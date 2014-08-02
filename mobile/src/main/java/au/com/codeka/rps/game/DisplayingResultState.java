package au.com.codeka.rps.game;

import au.com.codeka.rps.DebugLog;

/**
 * Created by dean on 2/08/14.
 */
public class DisplayingResultState extends State {
    private final StateManager stateManager;
    private final MatchInfo matchInfo;
    private final ResultInfo resultInfo;

    public DisplayingResultState(StateManager stateManager, MatchInfo matchInfo,
                                 ResultInfo resultInfo) {
        this.stateManager = stateManager;
        this.matchInfo = matchInfo;
        this.resultInfo = resultInfo;
    }

    @Override
    public void onEnter() {
        DebugLog.write("Game complete, result: %s (%s vs %s)", resultInfo.getResult(),
                resultInfo.getPlayerChoice(), resultInfo.getOtherChoice());
    }
}
