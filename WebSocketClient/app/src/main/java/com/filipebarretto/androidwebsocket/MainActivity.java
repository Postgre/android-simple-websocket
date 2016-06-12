package com.filipebarretto.androidwebsocket;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.filipebarretto.androidwebsocket.utils.Constants;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getCanonicalName();
    private WebSocketClient mWebSocketClient;

    private ImageView mConnectIcon;
    private TextView mConnectText;
    private TextView mMessages;
    private Button mConnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // SETUP LAYOUT ELEMENTS
        mConnectIcon = (ImageView) findViewById(R.id.connect_icon);
        mConnectText = (TextView) findViewById(R.id.connect_text);

        mMessages = (TextView) findViewById(R.id.messages);

        mConnectButton = (Button) findViewById(R.id.connect_button);
        mConnectButton.setOnClickListener(connectOnClickListener);


    }


    // INVOQUES FUNCTION TO OPEN OR CLOSE WEBSOCKET CONNECTION
    private View.OnClickListener connectOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mWebSocketClient != null) {
                closeWebSocket();
            } else {
                connectWebSocket();
            }

        }
    };

    private void setupWebSocket() {

        URI uri;
        try {
            addMessage(getString(R.string.connection_attempt));
            uri = new URI(Constants.URI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addMessage(getString(R.string.connection_opened));
                        mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);

                        mConnectIcon.setBackgroundResource(R.drawable.connected);
                        mConnectText.setText(R.string.connected);
                        mConnectButton.setText(R.string.close);
                        mConnectButton.setBackgroundColor(getResources().getColor(R.color.colorNotConnected));

                    }
                });

            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addMessage(message);
                        Log.i(TAG, message);
                    }
                });

            }

            @Override
            public void onClose(int i, String s, boolean b) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addMessage(getString(R.string.connection_closed));

                        mConnectIcon.setBackgroundResource(R.drawable.not_connected);
                        mConnectText.setText(R.string.not_connected);
                        mConnectButton.setText(R.string.connect);
                        mConnectButton.setBackgroundColor(getResources().getColor(R.color.colorConnected));
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "Error " + e.getMessage());
            }

        };

    }

    // OPENS WEBSOCKET CONNECTION
    private void connectWebSocket() {
        setupWebSocket();
        mWebSocketClient.connect();
    }

    // CLOSES WEBSOCKET CONNECTION
    private void closeWebSocket() {
        mWebSocketClient.close();
        mWebSocketClient = null;
    }


    public void addMessage(String message) {
        mMessages.setText(mMessages.getText() + "\n" + message);
    }
}
