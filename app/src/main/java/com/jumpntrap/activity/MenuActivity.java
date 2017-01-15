package com.jumpntrap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.jumpntrap.R;

public class MenuActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private final static String TAG = "MenuActivity";

    private GoogleApiClient googleApiClient;
    private final static int RC_SIGN_IN = 9001;
    private boolean resolvingConnectionFailure = false;
    private boolean autoStartSignInFlow = true;

    private final static int[] BUTTONS = {
            R.id.play,
            R.id.training,
            R.id.help
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        //removing the action bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_menu);

        // Set up a click listener for buttons
        for (int id : BUTTONS) {
            findViewById(id).setOnClickListener(this);
        }

        // Create the Google Api Client with access to Games
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        // Connect client if needed
        if (!googleApiClient.isConnected()) {
            Log.d(TAG, "onCreate : connecting client.");
            googleApiClient.connect();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                startActivity(new Intent(this, RemoteGameActivity.class));
                break;

            case R.id.training:
                startActivity(new Intent(this, HumanVSComputerActivity.class));
                break;

            case R.id.help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int statusCode) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");

        if (resolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed : ignoring connection failure; already resolving.");
            return;
        }

        if (autoStartSignInFlow) {
            autoStartSignInFlow = false;
            resolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(
                    this,
                    googleApiClient,
                    connectionResult,
                    RC_SIGN_IN,
                    getString(R.string.sign_in_error)
            );
        }
    }

}
