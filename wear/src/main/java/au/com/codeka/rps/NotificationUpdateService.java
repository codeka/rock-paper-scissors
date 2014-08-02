package au.com.codeka.rps;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class NotificationUpdateService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent msgEvent) {
        if (msgEvent.getPath().equals("/rps/StartGame")) {
            Intent startIntent = new Intent(this, GameActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
    }
}