package com.jumpntrap.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.jumpntrap.R;
import com.jumpntrap.util.ConnectionUtils;

/**
 * HelpActivity defines an activity to show details of the game.
 */
public final class HelpActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    /**
     * A tag for debug purpose.
     */
    private final static String TAG = "HelpActivity";

    /**
     * Google API client.
     */
    private GoogleApiClient googleApiClient;

    /**
     * Returned code when sign in to Google Play.
     */
    private final static int RC_SIGN_IN = 9001;

    /**
     * Flag to indicate if connection failure to Google Play has already been handled.
     */
    private boolean resolvingConnectionFailure = false;

    /**
     * Flag to indicate if we need to resolve connection to Google Play.
     */
    private boolean autoStartSignInFlow = true;

    /**
     * Returned code for achievements.
     */
    private final static int REQUEST_ACHIEVEMENTS = 11111;

    /**
     * Create the activity.
     * @param savedInstanceState the instance state to save.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //removing the action bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_help);
        findViewById(R.id.btn_achievements).setOnClickListener(this);
    }

    /**
     * Callback when connection to Google Play has succeed.
     * @param bundle the bundle.
     */
    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        startAchievementsActivity();
    }

    /**
     * Callback when connection to Google Play has been suspended.
     * @param statusCode the status code of the connection.
     */
    @Override
    public void onConnectionSuspended(final int statusCode) {
        googleApiClient.connect();
    }


    /**
     * Callback when connection to Google Play has failed.
     * @param connectionResult the result of the connection.
     */
    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        // Already solved
        if (resolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed : ignoring connection failure; already resolving.");
            return;
        }

        // Try to resolve if connection failed
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

    /**
     * Callback when a click event is triggered.
     * @param view the view to handle.
     */
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_achievements:
                // We can connect only if an internet connection is available
                if (ConnectionUtils.isInternetConnectionAvailable(this)) {
                    if (googleApiClient == null) {
                        // Create the Google Api Client with access to Games
                        googleApiClient = new GoogleApiClient.Builder(this)
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                                .build();
                    }

                    // Connect client if needed
                    if (!googleApiClient.isConnected()) {
                        Log.d(TAG, "onCreate : connecting client.");
                        googleApiClient.connect();
                    }

                    startAchievementsActivity();
                }
                else {
                    Toast.makeText(this, getString(R.string.network_not_available), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * Starts the achievements activity.
     */
    private void startAchievementsActivity() {
        if (googleApiClient.isConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(googleApiClient), REQUEST_ACHIEVEMENTS);
        }
    }
}
