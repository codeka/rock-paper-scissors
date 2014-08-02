package au.com.codeka.rps;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.FrameLayout;

import au.com.codeka.rps.game.StateManager;

public class GameActivity extends Activity {
    private boolean inflateComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                inflateComplete = true;
                StateManager.i.start(GameActivity.this);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (inflateComplete) {
            StateManager.i.start(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        StateManager.i.stop();
    }

    public void setFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }
}
