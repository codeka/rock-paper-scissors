package au.com.codeka.rps;

import android.app.Activity;
import android.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import au.com.codeka.rps.game.StateManager;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private WatchConnection watchConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        DebugLog.initialize();
        watchConnection = new WatchConnection();
        watchConnection.setup(this);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DebugLogFragment())
                    .commit();
        }

        StateManager.i.start(watchConnection);
        watchConnection.sendMessage(new WatchConnection.Message("/rps/StartGame", null));
    }

    @Override
    protected void onStart() {
        super.onStart();
        watchConnection.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        watchConnection.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This fragment contains a list view "log" of debug events.
     */
    public static class DebugLogFragment extends Fragment {
        private ArrayAdapter<String> adapter;

        public DebugLogFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_debuglog, container, false);

            ListView listview = (ListView) view.findViewById(R.id.debug_log);
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.debuglog_row,
                    DebugLog.getMessages());
            listview.setAdapter(adapter);
            DebugLog.setOnMsgsChangedRunnable(onMsgsChangedRunnable);

            return view;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (DebugLog.getOnMsgsChangedRunnable() == onMsgsChangedRunnable) {
                DebugLog.setOnMsgsChangedRunnable(null);
            }
        }

        private Runnable onMsgsChangedRunnable = new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        };
    }
}
