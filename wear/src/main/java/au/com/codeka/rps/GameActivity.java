package au.com.codeka.rps;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.FrameLayout;

import au.com.codeka.rps.game.StateManager;

public class GameActivity extends Activity {
    private boolean inflateComplete;
    private PhoneConnection phoneConnection;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        phoneConnection = new PhoneConnection();
        phoneConnection.setup(this);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                inflateComplete = true;
                StateManager.i.start(GameActivity.this, phoneConnection);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (inflateComplete) {
            StateManager.i.start(this, phoneConnection);
        }
        phoneConnection.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        StateManager.i.stop();
        phoneConnection.stop();
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setFragment(Fragment fragment) {
        currentFragment = fragment;
        getFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }
}
