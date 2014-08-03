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
import java.util.UUID;

import au.com.codeka.rps.DebugLog;

/**
 * The state we're in when we're finding an opponent.
 */
public class FindingOpponentState extends State {
    private final StateManager stateManager;
    private final HttpClient httpClient = new DefaultHttpClient();
    private final String playerId = UUID.randomUUID().toString();

    public FindingOpponentState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        DebugLog.write("Waiting for opponent...");
        new FindOpponentTask().execute();
    }

    private class FindOpponentTask extends AsyncTask<Void, Void, MatchInfo> {
        @Override
        protected MatchInfo doInBackground(Void... params) {
            String url = "https://rps-server.appspot.com/game/find-opponent?player_id=" + playerId; // TODO: configure URL
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
                    // Wait a couple of milliseconds before returning, so that we don't inundate
                    // the server with requests.
                    try {
                        Thread.sleep(100, 0);
                    } catch (InterruptedException e) { }
                    return null;
                }

                JSONObject json = new JSONObject(result);
                String otherPlayerId = null;
                if (!json.getString("player_one_id").equals(playerId)) {
                    otherPlayerId = json.getString("player_one_id");
                } else {
                    otherPlayerId = json.getString("player_two_id");
                }
                return new MatchInfo(json.getString("match_id"), playerId, otherPlayerId);
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
                // don't write anything.
            } else {
                DebugLog.write("Unknown error: %s", err);
            }
        }

        @Override
        protected void onPostExecute(MatchInfo matchInfo) {
            if (matchInfo == null) {
                // usually because of no opponent, we'll just try again. TODO: handle network errors
                new FindOpponentTask().execute();
            } else {
                stateManager.enterState(new GameRunningState(stateManager, matchInfo));
            }
        }
    };
}
