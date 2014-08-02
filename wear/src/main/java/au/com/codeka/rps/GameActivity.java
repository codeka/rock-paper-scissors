package au.com.codeka.rps;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.FrameLayout;

import au.com.codeka.rps.game.StateManager;

public class GameActivity extends Activity {
    private StateManager stateManager;
    private FrameLayout contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                contentView = (FrameLayout) stub.findViewById(R.id.content);
                stateManager = new StateManager(GameActivity.this);
            }
        });
    }

    public void setFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }
}
