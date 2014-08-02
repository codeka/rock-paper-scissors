package au.com.codeka.rps;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class lets us log to a fragment so we can see what's going on live in the app.
 */
public class DebugLog {
    private static ArrayList<String> msgs = new ArrayList<String>();
    private static Runnable onMsgsChangedRunnable;
    private static Handler handler;

    public static void setOnMsgsChangedRunnable(Runnable runnable) {
        onMsgsChangedRunnable = runnable;
        handler = new Handler();
    }
    public static Runnable getOnMsgsChangedRunnable() {
        return onMsgsChangedRunnable;
    }

    public static List<String> getMessages() {
        return msgs;
    }

    public static void write(String fmt, Object... args) {
        String msg = String.format(Locale.ENGLISH, fmt, args);
        msgs.add(msg);

        if (onMsgsChangedRunnable != null) {
            handler.post(onMsgsChangedRunnable);
        }
    }
}
