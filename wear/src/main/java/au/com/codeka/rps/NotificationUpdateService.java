package au.com.codeka.rps;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.UnsupportedEncodingException;

import au.com.codeka.rps.game.StateManager;

public class NotificationUpdateService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent msgEvent) {
        if (msgEvent.getPath().equals("/rps/StartGame")) {
            Intent startIntent = new Intent(this, GameActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        } else if (msgEvent.getPath().equals("/rps/StateChange")) {
            String newStateName = null;
            try {
                newStateName = new String(msgEvent.getData(), "utf-8");
            } catch (UnsupportedEncodingException e) {
            }

            StateManager.i.enterState(newStateName);

        }
    }
}