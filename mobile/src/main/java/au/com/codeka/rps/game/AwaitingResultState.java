package au.com.codeka.rps.game;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import au.com.codeka.rps.DebugLog;

/**
 * We've got our choice, now we need to post to the server and wait for the other player to post
 * their result so we get the final result.
 */
public class AwaitingResultState extends State {
    private final StateManager stateManager;
    private final MatchInfo matchInfo;
    private final String playerChoice;

    public AwaitingResultState(StateManager stateManager, MatchInfo matchInfo, String choice) {
        this.stateManager = stateManager;
        this.matchInfo = matchInfo;
        this.playerChoice = choice;
    }

    @Override
    public void onEnter() {
        new PostChoiceTask().execute();
    }

    private class PostChoiceTask extends AsyncTask<Void, Void, ResultInfo> {
        HttpClient httpClient = new DefaultHttpClient();

        @Override
        protected ResultInfo doInBackground(Void... params) {
            DebugLog.write("Posting choice, awaiting result...");

            String url = String.format("http://192.168.1.4:8274/game/%s?player_id=%s&choice=%s", // TODO: configure URL
                    matchInfo.getMatchId(), matchInfo.getPlayerId(), playerChoice.toLowerCase());
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
                String playerChoice = null;
                String otherChoice = null;
                Iterator<String> keysIter = json.getJSONObject("players").keys();
                while (keysIter.hasNext()) {
                    String key = keysIter.next();
                    if (key.equals(matchInfo.getPlayerId())) {
                        playerChoice = json.getJSONObject("players").getString(key);
                    } else {
                        otherChoice = json.getJSONObject("players").getString(key);
                    }
                }

                if (playerChoice == null) {
                    DebugLog.write("ERROR : playerChoice is null!");
                    return null;
                }
                if (otherChoice == null) {
                    DebugLog.write("ERROR : otherChoice is null!");
                    return null;
                }

                return new ResultInfo(matchInfo.getMatchId(), playerChoice, otherChoice);
            } catch (IOException e) {
                DebugLog.write("ERROR : %s", e.getMessage());
                return null;
            } catch (JSONException e) {
                DebugLog.write("ERROR : %s", e.getMessage());
                return null;
            }
        }

        private void handleError(String err) {
            if (err.equals("NO-RESPONSE")) {
                DebugLog.write("Other player has not checked in yet.");
            } else {
                DebugLog.write("Unknown error: %s", err);
            }
        }

        @Override
        protected void onPostExecute(ResultInfo resultInfo) {
            if (resultInfo == null) {
                // usually just a timeout, we'll just try again. TODO: handle network errors
                new PostChoiceTask().execute();
            } else {
                stateManager.enterState(new DisplayingResultState(stateManager, matchInfo,
                        resultInfo));
            }
        }
    };
}
