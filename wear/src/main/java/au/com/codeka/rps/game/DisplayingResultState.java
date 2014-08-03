package au.com.codeka.rps.game;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import au.com.codeka.rps.ResultFragment;

/**
 * The state we're in once we've got the other player's choice and we're displaying the final
 * result of the game to the user.
 */
public class DisplayingResultState extends State {
    private StateManager stateManager;

    @Override
    public void onEnter(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onFinalResult(final JSONObject json) {
        final ResultFragment fragment = (ResultFragment) stateManager.getGameActivity().getCurrentFragment();
        try {
            ResultFragment.Result result = ResultFragment.Result.Draw;
            String s = json.getString("result").toLowerCase();
            if (s.equals("win")) {
                result = ResultFragment.Result.Win;
            } else if (s.equals("loss")) {
                result = ResultFragment.Result.Loss;
            }
            fragment.setOtherChoice(json.getString("other_choice"), result);
        } catch (JSONException e) {
        }
    }

}
