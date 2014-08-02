package au.com.codeka.rps.game;

import android.os.AsyncTask;
import android.os.Handler;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import au.com.codeka.rps.DebugLog;

/**
 * The state we're in when we're finding an opponent.
 */
public class FindingOpponentState extends State {
    private final StateManager stateManager;

    public FindingOpponentState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        new FindOpponentTask().execute();
    }

    private class FindOpponentTask extends AsyncTask<Void, Void, MatchInfo> {
        HttpClient httpClient = new DefaultHttpClient();

        @Override
        protected MatchInfo doInBackground(Void... params) {
            DebugLog.write("Waiting for opponent...");

            String url = "http://192.168.1.4:8274/game/find-opponent"; // TODO: configure URL
            try {
                HttpResponse resp = httpClient.execute(new HttpGet(url));
                StatusLine statusLine = resp.getStatusLine();
                if (statusLine.getStatusCode() != 200) {
                    DebugLog.write("ERROR : Unexpected status code: %d", statusLine.getStatusCode());
                    return null;
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                resp.getEntity().writeTo(out);
                out.close();
                String result = out.toString();
                if (result.startsWith("ERR:")) {
                    handleError(result.substring(4));
                    return null;
                }

                JSONObject json = new JSONObject(result);
                return new MatchInfo(json.getString("match_id"), json.getString("your_id"));
            } catch (IOException e) {
                DebugLog.write("ERROR : %s", e.getMessage());
                return null;
            } catch (JSONException e) {
                DebugLog.write("ERROR : %s", e.getMessage());
                return null;
            }
        }

        private void handleError(String err) {
            if (err.equals("NO-OPPONENT")) {
                DebugLog.write("No opponents yet.");
            } else {
                DebugLog.write("Unknown error: %s", err);
            }
        }

        @Override
        protected void onPostExecute(MatchInfo matchInfo) {
            if (matchInfo == null) {
                // usually just a timeout, we'll just try again. TODO: handle network errors
                new FindOpponentTask().execute();
            } else {
                stateManager.onMatchStarted(matchInfo);
            }
        }
    };
}
