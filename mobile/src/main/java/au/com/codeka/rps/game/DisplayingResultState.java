package au.com.codeka.rps.game;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import au.com.codeka.rps.DebugLog;
import au.com.codeka.rps.WatchConnection;

/**
 * Created by dean on 2/08/14.
 */
public class DisplayingResultState extends State {
    private final StateManager stateManager;
    private final MatchInfo matchInfo;
    private final ResultInfo resultInfo;
    private final Handler handler;

    public DisplayingResultState(StateManager stateManager, MatchInfo matchInfo,
                                 ResultInfo resultInfo) {
        this.stateManager = stateManager;
        this.matchInfo = matchInfo;
        this.resultInfo = resultInfo;
        handler = new Handler();
    }

    @Override
    public void onEnter() {
        DebugLog.write("Game complete - match: %s, round: %d, result: %s (%s vs %s)",
                matchInfo.getMatchId(), matchInfo.getRound(), resultInfo.getResult(),
                resultInfo.getPlayerChoice(), resultInfo.getOtherChoice());

        JSONObject json = new JSONObject();
        try {
            json.put("round", matchInfo.getRound());
            json.put("player_choice", resultInfo.getPlayerChoice());
            json.put("other_choice", resultInfo.getOtherChoice());
            json.put("result", resultInfo.getResult().toString());
        } catch (JSONException e) {
            DebugLog.write("ERROR : %s", e.getMessage());
        }
        try {
            stateManager.getWatchConnection().sendMessage(new WatchConnection.Message(
                    "/rps/FinalResult", json.toString().getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            DebugLog.write("ERROR : %s", e.getMessage());
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DebugLog.write("Starting next match!");
                matchInfo.nextRound();
                stateManager.enterState(new GameRunningState(stateManager, matchInfo));
            }
        }, 5000);
    }
}
