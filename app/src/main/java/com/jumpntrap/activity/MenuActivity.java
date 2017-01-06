package com.jumpntrap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.jumpntrap.R;
import com.jumpntrap.tbm.Turn;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, OnTurnBasedMatchUpdateReceivedListener,
        OnInvitationReceivedListener {
    private final static String TAG = "MenuActivity";
    private GoogleApiClient googleApiClient;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    private final static int RC_SIGN_IN = 9001;
    //private final static int RC_WAITING_ROOM = 10002;
    private final static int RC_SELECT_PLAYERS = 10000;

    final static int[] SCREENS = {
            R.id.screen_main,
            R.id.screen_wait,
            R.id.screen_sign_in
    };

    final static int[] BUTTONS = {
            R.id.btn_one_player,
            R.id.btn_one_player_2,
            R.id.btn_quick_game,
            R.id.btn_sign_in
    };

    //private List<Participant> participants = null;
    //private String roomId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Create the Google Api Client with access to Games
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

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

    private void startQuickGame() {
        Log.d(TAG, "Start quick game");

        Intent intent =
                Games.TurnBasedMultiplayer.getSelectOpponentsIntent(googleApiClient, 1, 1, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);

        /*
        // Quick-start a game with 1 randomly selected opponent
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, 0);

        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this)
                        .setRoomStatusUpdateListener(this)
                        .setAutoMatchCriteria(autoMatchCriteria);
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        //resetGameVars();

        Games.RealTimeMultiplayer.create(googleApiClient, rtmConfigBuilder.build());
        */
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

        // start the sign-in flow
        Log.d(TAG, "Sign-in button clicked");
        mSignInClicked = true;
        googleApiClient.connect();
    }

    private void switchToScreen(int screenId) {
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
    }

    /*
    private void showWaitingRoom(Room room) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(
                googleApiClient,
                room,
                MIN_PLAYERS
        );

        // Show waiting room
        startActivityForResult(intent, RC_WAITING_ROOM);
    }
    */

    /*
    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    */

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

    /*
    private void updateRoom(Room room) {
        if (room != null) {
            participants = room.getParticipants();
        }
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            /*
            case RC_WAITING_ROOM:
                // we got the result from the "waiting room" UI.
                if (resultCode == Activity.RESULT_OK) {
                    // ready to start playing
                    Log.d(TAG, "Starting game (waiting room returned OK).");
                    //startGame(true);
                }
                else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player indicated that they want to leave the room
                    //leaveRoom();
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    // Dialog was cancelled (user pressed back key, for instance). In our game,
                    // this means leaving the room too. In more elaborate games, this could mean
                    // something else (like minimizing the waiting room UI).
                    //leaveRoom();
                }
                break;
            */
            case RC_SELECT_PLAYERS:
                Log.d(TAG, "onActivityResult - RC_SELECT_PLAYERS: " + resultCode);

                if (resultCode != Activity.RESULT_OK) {
                    Log.e(TAG, "**Error: onActivityResult - RC_SELECT_PLAYERS");
                    return;
                }

                // Get the invitee list.
                final ArrayList<String> invitees =
                        data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

                // Auto-match criteria : 1 random player
                Bundle amCriteria = RoomConfig.createAutoMatchCriteria(1, 1, 0);

                // Config
                TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                        .addInvitedPlayers(invitees)
                        .setAutoMatchCriteria(amCriteria)
                        .build();

                // Create and start the match.
                Games.TurnBasedMultiplayer
                        .createMatch(googleApiClient, tbmc)
                        // Callback to init game
                        .setResultCallback(new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                            @Override
                            public void onResult(@NonNull TurnBasedMultiplayer.InitiateMatchResult result) {
                                processResult(result);
                            }
                        });
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private Turn turnData;
    private TurnBasedMatch match;

    // Init game
    private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        // Check if the status code is not success
        Status status = result.getStatus();
        if (!status.isSuccess()) {
            Log.e(TAG, "**Error : " + status.getStatusCode());
            return;
        }

        TurnBasedMatch match = result.getMatch();

        // Game has already started
        if (match.getData() != null) {
            Log.d(TAG, "processResult InitiateMatchResult - updateMatch");
            updateMatch(match);
        } else {
            Log.d(TAG, "processResult InitiateMatchResult - startMatch");
            startMatch(match);
        }
    }

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;

    // Update game
    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        TurnBasedMatch match = result.getMatch();

        // Rematch ?
        //if (match.canRematch()) {
        //askForRematch();
        //}

        isDoingTurn = match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN;
        if (isDoingTurn) {
            updateMatch(match);
        }
        //else {
        //setViewVisibility();
        //}
    }

    private void startMatch(TurnBasedMatch match) {
        // Create a new turn
        turnData = new Turn();

        // Keep match
        this.match = match;

        // Get current player
        Log.e(TAG, "Nb part : " + match.getParticipantIds().size());
        String playerId = Games.Players.getCurrentPlayerId(googleApiClient);
        String myParticipantId = this.match.getParticipantId(playerId);

        // Update match with new turn data
        /*
        Games.TurnBasedMultiplayer.takeTurn(
                googleApiClient,
                this.match.getMatchId(),
                turnData.persist(), // Data are here
                myParticipantId)
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(@NonNull TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });
         */
    }

    private void updateMatch(TurnBasedMatch match) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected() called. Sign in successful!");
        switchToMainScreen();

        Games.Invitations.loadInvitations(googleApiClient);

        // This is *NOT* required; if you do not register a handler for
        // invitation events, you will get standard notifications instead.
        // Standard notifications may be preferable behavior in many cases.
        Games.Invitations.registerInvitationListener(googleApiClient, this);

        // Likewise, we are registering the optional MatchUpdateListener, which
        // will replace notifications you would get otherwise. You do *NOT* have
        // to register a MatchUpdateListener.
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(googleApiClient, this);

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
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {

    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        Log.e(TAG, "INVITATION");
        final Invitation localInvitation = invitation;
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                        activity,
                        "An invitation has arrived from "
                                + localInvitation.getInviter().getDisplayName(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void onInvitationRemoved(String s) {

    }

    /*
    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode == GamesStatusCodes.STATUS_OK) {
            roomId = room.getRoomId();
            showWaitingRoom(room);
        }
        else {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            showGameError();
        }
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        if (statusCode == GamesStatusCodes.STATUS_OK) {
            showWaitingRoom(room);
        }
        else {
            Log.e(TAG, "*** Error: onJoinedRoom, status " + statusCode);
            showGameError();
        }
    }

    @Override
    public void onLeftRoom(int statusCode, String s) {

    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if (statusCode == GamesStatusCodes.STATUS_OK) {
            updateRoom(room);
        }
        else {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
        }
    }

    @Override
    public void onRoomConnecting(Room room) {

    }

    @Override
    public void onRoomAutoMatching(Room room) {

    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {

    }

    @Override
    public void onPeerDeclined(Room room, List<String> list) {

    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {

    }

    @Override
    public void onPeerLeft(Room room, List<String> list) {

    }

    @Override
    public void onConnectedToRoom(Room room) {

    }

    @Override
    public void onDisconnectedFromRoom(Room room) {

    }

    @Override
    public void onPeersConnected(Room room, List<String> list) {

    }

    @Override
    public void onPeersDisconnected(Room room, List<String> list) {

    }

    @Override
    public void onP2PConnected(String s) {

    }

    @Override
    public void onP2PDisconnected(String s) {

    }
    */
}
