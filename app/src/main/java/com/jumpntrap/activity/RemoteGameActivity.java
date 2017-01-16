package com.jumpntrap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.jumpntrap.R;
import com.jumpntrap.dialog.OpponentLeftDialog;
import com.jumpntrap.dialog.RematchRemoteDialog;
import com.jumpntrap.games.OneVSOneRemoteGame;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.Player;
import com.jumpntrap.players.HumanPlayer;
import com.jumpntrap.players.RemotePlayer;
import com.jumpntrap.util.RoomUtils;
import com.jumpntrap.util.ScreenUtils;

import java.util.List;

/**
 * RemoteGameActivity defines a remote game activity.
 */
public final class RemoteGameActivity extends GameActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        RoomUpdateListener, RoomStatusUpdateListener, RealTimeMessageReceivedListener {
    /**
     * A tag for debug purpose.
     */
    private static final String TAG = "RemoteGameActivity";

    /**
     * Google API client.
     */
    private GoogleApiClient googleApiClient;

    /**
     * Flag to indicate if connection failure to Google Play has already been handled.
     */
    private boolean resolvingConnectionFailure = false;

    /**
     * Flag to indicate if we need to resolve connection to Google Play.
     */
    private boolean autoStartSignInFlow = true;

    /**
     * Returned code when sign in to Google Play.
     */
    private final static int RC_SIGN_IN = 9001;

    /**
     * List of the participants of the game.
     */
    private List<Participant> participants;

    /**
     * Current player id.
     */
    private String myId;

    /**
     * Current room id.
     */
    private String roomId;

    /**
     * Remote player.
     */
    private RemotePlayer remotePlayer = null;

    /**
     * Flag to indicate if the player is the host.
     */
    private boolean isHost = false;

    /**
     * Rematch dialog when a game is finished.
     */
    private RematchRemoteDialog rematchDialog = null;

    /**
     * Flag to indicate if the player wants to leave the game.
     */
    private boolean wantsToLeave = false;

    /**
     * Create the activity.
     * @param savedInstanceState the instance state to save.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showGameBoard(View.GONE);
        showSpinner(true);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        if (googleApiClient.isConnected()) {
            Log.w(TAG, "GameHelper: client was already connected on onStart()");
            return;
        }

        Log.d(TAG, "Connecting client.");
        googleApiClient.connect();
    }

    /**
     * Stop the activity.
     */
    @Override
    protected void onStop() {
        leaveRoom();
        super.onStop();
    }

    /**
     * Callback when a key down event is triggered.
     * @param keyCode the key code.
     * @param e the key event.
     * @return true if the event is handled.
     */
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent e) {
        Log.d(TAG, "onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyDown : button back pressed");

            wantsToLeave = true;
            leaveRoom();
            return true;
        }

        return super.onKeyDown(keyCode, e);
    }

    /**
     * Callback when the game is over.
     * @param game the game.
     * @param winner the player who won.
     */
    @Override
    public void onGameOver(final Game game, final Player winner) {
        if (this.game != game) {
            return;
        }

        final int userScore = this.game.getFirstPlayerScore();
        final int opponentScore = this.game.getSecondPlayerScore();

        final TextView scoreBottom = (TextView) findViewById(R.id.score_bottom);
        final TextView scoreTop = (TextView) findViewById(R.id.score_top);

        final OneVSOneRemoteGame remoteGame = (OneVSOneRemoteGame) game;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreBottom.setText(String.valueOf(userScore));
                scoreTop.setText(String.valueOf(opponentScore));

                rematchDialog = new RematchRemoteDialog(
                        RemoteGameActivity.this,
                        googleApiClient,
                        remoteGame,
                        roomId,
                        participants.get(isHost ? 0 : 1),
                        participants.get(isHost ? 1 : 0),
                        isHost
                );
                rematchDialog.show();
            }
        });
    }

    /**
     * Show the game board depending on the view flag.
     * @param view the visibility of the board. The value can be
     *             View.GONE, View.VISIBLE or View.INVISIBLE.
     */
    private void showGameBoard(final int view) {
        findViewById(R.id.board).setVisibility(view);
        findViewById(R.id.topBar).setVisibility(view);
        findViewById(R.id.bottomBar).setVisibility(view);
    }

    /**
     * Show the spinner depending on the show flag.
     * @param show the flag to show the spinner.
     */
    private void showSpinner(final boolean show) {
        findViewById(R.id.loading_panel).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Create a remote match.
     */
    private void createRemoteMatch() {
        // Quick-start a game with 1 randomly selected opponent
        final Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, 0);

        final RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this)
                .setAutoMatchCriteria(autoMatchCriteria);

        // Keep on screen
        ScreenUtils.keepScreenOn(getWindow());

        // Launch waiting room
        Games.RealTimeMultiplayer.create(googleApiClient, rtmConfigBuilder.build());
    }

    /**
     * Callback for activity result.
     * @param requestCode the request code.
     * @param resultCode the result code.
     * @param data the intent.
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Log.d(TAG, "onActivityResult");

        switch (requestCode) {
            case RoomUtils.RC_WAITING_ROOM:
                // Result from waiting room
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Starting game");
                    showSpinner(false);
                    initGame();
                }
                else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM || resultCode == Activity.RESULT_CANCELED) {
                    leaveRoom();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Initialize the game.
     */
    private void initGame() {
        Log.d(TAG, "initGame");

        // Take 1st player as host
        final Participant participant = participants.get(0);
        isHost = myId.equals(participant.getParticipantId());

        // If player is host
        Log.d(TAG, "initGame - isHost : " + isHost);
        if (isHost) {
            createPlayersAndGame();
        }
    }

    /**
     * Create players and game instances.
     */
    private void createPlayersAndGame() {
        HumanPlayer humanPlayer;

        // Create players and game
        if (isHost) {
            humanPlayer = new HumanPlayer(this);
            remotePlayer = new RemotePlayer(googleApiClient, roomId, participants.get(1).getParticipantId());
            game = new OneVSOneRemoteGame(humanPlayer, remotePlayer, true);
        }
        else {
            humanPlayer = new HumanPlayer(this);
            remotePlayer = new RemotePlayer(googleApiClient, roomId, participants.get(0).getParticipantId());
            remotePlayer.setHost(true);
            game = new OneVSOneRemoteGame(remotePlayer, humanPlayer, false);
        }

        // Setup game
        game.addObserver(remotePlayer);
        setGame(game);
        game.start();
        this.setOnTouchListener(humanPlayer);
        showGameBoard(View.VISIBLE);
    }

    /**
     * Update the list of the participants of the room.
     * @param room the current room.
     */
    private void updateRoom(final Room room) {
        if (room != null) {
            participants = room.getParticipants();
        }
    }

    /**
     * Leave the current room.
     */
    private void leaveRoom() {
        ScreenUtils.keepScreenOff(getWindow());
        if (roomId != null) {
            Games.RealTimeMultiplayer.leave(googleApiClient, this, roomId);
            roomId = null;
        }

        finish();
    }

    /**
     * Callback when a real time message is received via Google Play API.
     * @param realTimeMessage the message received.
     */
    @Override
    public void onRealTimeMessageReceived(final RealTimeMessage realTimeMessage) {
        Log.d(TAG, "onRealTimeMessageReceived");

        // Create game is not created yet
        if (game == null) {
            createPlayersAndGame();
        }

        byte[] buff = realTimeMessage.getMessageData();
        remotePlayer.handleRealTimeMessageReceived(game, buff, rematchDialog);
    }

    /**
     * Callback when a room is created.
     * @param statusCode the status code.
     * @param room the current room.
     */
    @Override
    public void onRoomCreated(final int statusCode, final Room room) {
        Log.d(TAG, "onRoomCreated");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            return;
        }

        roomId = room.getRoomId();
        showSpinner(false);
        RoomUtils.showWaitingRoom(this, room, googleApiClient);
    }

    /**
     * Callback when a room is joined.
     * @param statusCode the status code.
     * @param room the current room.
     */
    @Override
    public void onJoinedRoom(final int statusCode, final Room room) {
        Log.d(TAG, "onJoinedRoom");
    }

    /**
     * Callback when a room is left.
     * @param statusCode the status code.
     * @param s the ID of the participant.
     */
    @Override
    public void onLeftRoom(final int statusCode, final String s) {
        Log.d(TAG, "onLeftRoom");
    }

    /**
     * Callback when a room is connected.
     * @param statusCode the status code.
     * @param room the current room.
     */
    @Override
    public void onRoomConnected(final int statusCode, final Room room) {
        Log.d(TAG, "onRoomConnected");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            return;
        }

        updateRoom(room);
    }

    /**
     * Callback when a room is connecting.
     * @param room the current room.
     */
    @Override
    public void onRoomConnecting(final Room room) {
        Log.d(TAG, "onRoomConnecting");
        updateRoom(room);
    }

    /**
     * Callback when a room is auto matching.
     * @param room the current room.
     */
    @Override
    public void onRoomAutoMatching(final Room room) {
        Log.d(TAG, "onRoomAutoMatching");
        updateRoom(room);
    }

    /**
     * Callback when a room is auto matching.
     * @param room the current room.
     */
    @Override
    public void onPeerInvitedToRoom(final Room room, final List<String> list) {
        Log.d(TAG, "onPeerInvitedToRoom");
    }

    /**
     * Callback when peer is declined.
     * @param room the current room.
     */
    @Override
    public void onPeerDeclined(final Room room, final List<String> list) {
        Log.d(TAG, "onPeerInvitedToRoom");
    }

    /**
     * Callback when a peer is joined.
     * @param room the current room.
     */
    @Override
    public void onPeerJoined(final Room room, final List<String> list) {
        Log.d(TAG, "onPeerJoined");
        updateRoom(room);
    }

    /**
     * Callback when a peer is left.
     * @param room the current room.
     */
    @Override
    public void onPeerLeft(final Room room, final List<String> list) {
        Log.d(TAG, "onPeerLeft");
        updateRoom(room);
    }

    /**
     * Callback when connected to room.
     * @param room the current room.
     */
    @Override
    public void onConnectedToRoom(final Room room) {
        Log.d(TAG, "onConnectedToRoom");

        // Get participants and my ID
        participants = room.getParticipants();
        myId = room.getParticipantId(Games.Players.getCurrentPlayerId(googleApiClient));

        // Save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts
        if (roomId == null) {
            roomId = room.getRoomId();
        }
    }

    /**
     * Callback when disconnected from room.
     * @param room the current room.
     */
    @Override
    public void onDisconnectedFromRoom(final Room room) {
        Log.d(TAG, "onDisconnectedFromRoom");

        if (wantsToLeave) {
            leaveRoom();
        }
        // The opponent left the game
        else {
            // Dismiss rematch dialog if needed
            if (rematchDialog != null) {
                rematchDialog.dismiss();
            }

            // Show opponent left dialog
            new OpponentLeftDialog(this).show();
        }
    }

    /**
     * Callback when peers are connected.
     * @param room the current room.
     * @param list the IDs of the participants.
     */
    @Override
    public void onPeersConnected(final Room room, final List<String> list) {
        Log.d(TAG, "onPeersConnected");
        updateRoom(room);
    }

    /**
     * Callback when peers are disconnected.
     * @param room the current room.
     * @param list the IDs of the participants.
     */
    @Override
    public void onPeersDisconnected(final Room room, final List<String> list) {
        Log.d(TAG, "onPeersDisconnected");
        updateRoom(room);
    }

    /**
     * Callback when P2P is connected.
     * @param s the ID of the participants.
     */
    @Override
    public void onP2PConnected(final String s) {
        Log.d(TAG, "onP2PConnected");
    }

    /**
     * Callback when P2P is disconnected.
     * @param s the ID of the participants.
     */
    @Override
    public void onP2PDisconnected(final String s) {
        Log.d(TAG, "onP2PDisconnected");
    }

    /**
     * Callback when connection to Google Play has succeed.
     * @param bundle the bundle.
     */
    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        Log.d(TAG, "onConnected() called. Sign in successful!");
        createRemoteMatch();
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
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
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
}
