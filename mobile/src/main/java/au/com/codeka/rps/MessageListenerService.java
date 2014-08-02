package au.com.codeka.rps;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.UnsupportedEncodingException;

import au.com.codeka.rps.game.StateManager;

/**
 * This service listens for messages back from the watch.
 */
public class MessageListenerService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent msgEvent) {
        String payload = null;
        if (msgEvent.getData() != null) {
            try {
                payload = new String(msgEvent.getData(), "utf-8");
            } catch (UnsupportedEncodingException e) {
            }
        }

        StateManager.i.onMessageReceived(msgEvent.getPath(), payload);
    }
}