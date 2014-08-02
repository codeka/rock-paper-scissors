package au.com.codeka.rps;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Class that represents our connection to the phone.
 */
public class PhoneConnection {
    private static final String TAG = "PhoneConnection";
    private GoogleApiClient googleApiClient;
    private boolean isConnected;
    private String phoneNode;

    public void setup(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(
                                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                                    @Override
                                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                        for (Node node : getConnectedNodesResult.getNodes()) {
                                            phoneNode = node.getId();
                                            break;
                                        }
                                        isConnected = true;
                                    }
                                });
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                        isConnected = false;
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                        isConnected = false;
                    }
                })
                .addApi(Wearable.API)
                .build();
    }

    public void start() {
        googleApiClient.connect();
    }

    public void stop() {
        googleApiClient.disconnect();
    }

    public void sendMessage(Message msg) {
        if (!isConnected) {
            throw new IllegalStateException("Cannot send message while not connected to phone.");
        } else {
            Wearable.MessageApi.sendMessage(googleApiClient, phoneNode, msg.getPath(),
                    msg.getPayload());
        }
    }

    public static class Message {
        private final String path;
        private final byte[] payload;

        public Message(String path) {
            this.path = path;
            this.payload = null;
        }

        public Message(String path, byte[] payload) {
            this.path = path;
            this.payload = payload;
        }

        public String getPath() {
            return path;
        }

        public byte[] getPayload() {
            return payload;
        }
    }
}
