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
        DebugLog.write("Waiting for response from other player...");
        new PostChoiceTask().execute();
    }

    private class PostChoiceTask extends AsyncTask<Void, Void, ResultInfo> {
        HttpClient httpClient = new DefaultHttpClient();

        @Override
        protected ResultInfo doInBackground(Void... params) {
            // TODO: post this, not GET
            String url = String.format("https://rps-server.appspot.com/game/%s?round=%d&player_id=%s&choice=%s", // TODO: configure URL
                    matchInfo.getMatchId(), matchInfo.getRound(), matchInfo.getPlayerId(),
                    playerChoice.toLowerCase());
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
                    try {
                        Thread.sleep(100, 0);
                    } catch (InterruptedException e) {}
                    return null;
                }

                JSONObject json = new JSONObject(result);
                String playerChoice = null;
                String otherChoice = null;
                JSONObject playerOne = json.getJSONObject("player_one");
                JSONObject playerTwo = json.getJSONObject("player_two");
                if (matchInfo.getPlayerId().equals(playerOne.getString("id"))) {
                    playerChoice = playerOne.getString("choice");
                    otherChoice = playerTwo.getString("choice");
                } else {
                    playerChoice = playerTwo.getString("choice");
                    otherChoice = playerOne.getString("choice");
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
                // Don't write anything.
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
