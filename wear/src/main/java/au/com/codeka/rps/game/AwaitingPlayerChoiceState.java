package au.com.codeka.rps.game;

import java.io.UnsupportedEncodingException;

import au.com.codeka.rps.GameFragment;
import au.com.codeka.rps.PhoneConnection;

/**
 * This is the state we're in while we're waiting to get the player's choice from the watch.
 */
public class AwaitingPlayerChoiceState extends State {
    @Override
    public void onEnter(StateManager stateManager) {
        // we should still be in the game fragment, grab the choice and send it over.
        GameFragment gameFragment = (GameFragment) stateManager.getGameActivity()
                .getCurrentFragment();
        String choice = gameFragment.getCurrentChoice();
        try {
            stateManager.getPhoneConnection().sendMessage(new PhoneConnection.Message(
                    "/rps/PlayerChoice", choice.getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
        }
    }
}
