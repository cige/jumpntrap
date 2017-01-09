package com.jumpntrap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.jumpntrap.R;

public class MenuActivity extends BaseGameActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    private final static String TAG = "MenuActivity";
    private GoogleApiClient googleApiClient;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    private final static int RC_SIGN_IN = 9001;

    final static int[] SCREENS = {
            R.id.screen_main,
            R.id.screen_wait,
            R.id.screen_sign_in
    };

    final static int[] BUTTONS = {
            R.id.btn_one_player,
            R.id.btn_one_player_2,
            R.id.btn_quick_game,
            R.id.btn_sign_in,
            R.id.btn_help,
            R.id.btn_help_2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Create the Google Api Client with access to Games
        googleApiClient = getApiClient();
        googleApiClient.registerConnectionCallbacks(this);
        googleApiClient.registerConnectionFailedListener(this);

        // Set up a click listener for buttons
        for (int id : BUTTONS) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!googleApiClient.isConnected()) {
            Log.d(TAG, "Connecting client.");

            switchToScreen(R.id.screen_wait);
            googleApiClient.connect();
        }
        else {
            Log.w(TAG, "GameHelper: client was already connected on onStart()");
        }
    }

    private void startOnePlayerActivity() {
        startActivity(new Intent(this, GameActivity.class));
    }

    private void startHelpActivity() {
        startActivity(new Intent(this, HelpActivity.class));
    }

    private void startQuickGame() {
        Log.d(TAG, "Start quick game");
        Intent intent = new Intent(this, QuickGameActivity.class);
        startActivity(intent);
    }

    private void signIn() {
        Log.e(TAG, "SINGIN");
        // user wants to sign in
        // Check to see the developer who's running this sample code read the instructions :-)
        // NOTE: this check is here only because this is a sample! Don't include this
        // check in your actual production app.
        if (!BaseGameUtils.verifySampleSetup(this, R.string.app_id)) {
            Log.w(TAG, "*** Warning: setup problems detected. Sign in may not work!");
        }

        // Start the sign-in flow
        mSignInClicked = true;
        googleApiClient.connect();
    }

    private void switchToScreen(int screenId) {
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
    }

    private void switchToMainScreen() {
        switchToScreen(
                // Google API client is valid ?
                googleApiClient != null && googleApiClient.isConnected()
                        ? R.id.screen_main
                        : R.id.screen_sign_in
        );
    }

    /*
    private void showGameError() {
        BaseGameUtils.makeSimpleDialog(this, getString(R.string.game_problem));
        switchToMainScreen();
    }
    */

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_one_player:
            case R.id.btn_one_player_2:
                startOnePlayerActivity();
                break;

            case R.id.btn_sign_in:
                signIn();
                break;

            case R.id.btn_quick_game:
                startQuickGame();
                break;

            case R.id.btn_help:
            case R.id.btn_help_2:
                startHelpActivity();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected() called. Sign in successful!");
        switchToMainScreen();
    }

    @Override
    public void onConnectionSuspended(int statusCode) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(
                    this,
                    googleApiClient,
                    connectionResult,
                    RC_SIGN_IN,
                    getString(R.string.signin_error)
            );
        }

        switchToScreen(R.id.screen_sign_in);
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }
}
