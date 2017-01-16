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

public final class RemoteGameActivity extends GameActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        RoomUpdateListener, RoomStatusUpdateListener, RealTimeMessageReceivedListener {

    private static final String TAG = "RemoteGameActivity";

    private GoogleApiClient googleApiClient;
    private boolean resolvingConnectionFailure = false;
    private boolean autoStartSignInFlow = true;

    private final static int RC_SIGN_IN = 9001;

    private List<Participant> participants;
    private String myId;
    private String roomId;

    private RemotePlayer remotePlayer = null;
    private boolean isHost = false;

    private RematchRemoteDialog rematchDialog = null;
    private boolean wantsToLeave = false;

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

    @Override
    protected void onStop() {
        leaveRoom();
        super.onStop();
    }

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

    private void showGameBoard(final int view) {
        findViewById(R.id.board).setVisibility(view);
        findViewById(R.id.topBar).setVisibility(view);
        findViewById(R.id.bottomBar).setVisibility(view);
    }

    private void showSpinner(final boolean show) {
        findViewById(R.id.loading_panel).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void createQuickMatch() {
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

    private void updateRoom(final Room room) {
        if (room != null) {
            participants = room.getParticipants();
        }
    }

    private void leaveRoom() {
        ScreenUtils.keepScreenOff(getWindow());
        if (roomId != null) {
            Games.RealTimeMultiplayer.leave(googleApiClient, this, roomId);
            roomId = null;
        }

        finish();
    }

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

    @Override
    public void onJoinedRoom(final int statusCode, final Room room) {
        Log.d(TAG, "onJoinedRoom");
    }

    @Override
    public void onLeftRoom(final int statusCode, final String s) {
        Log.d(TAG, "onLeftRoom");
    }

    @Override
    public void onRoomConnected(final int statusCode, final Room room) {
        Log.d(TAG, "onRoomConnected");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            return;
        }

        updateRoom(room);
    }

    @Override
    public void onRoomConnecting(final Room room) {
        Log.d(TAG, "onRoomConnecting");
        updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(final Room room) {
        Log.d(TAG, "onRoomAutoMatching");
        updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(final Room room, final List<String> list) {
        Log.d(TAG, "onPeerInvitedToRoom");
    }

    @Override
    public void onPeerDeclined(final Room room, final List<String> list) {
        Log.d(TAG, "onPeerInvitedToRoom");
    }

    @Override
    public void onPeerJoined(final Room room, final List<String> list) {
        Log.d(TAG, "onPeerJoined");
        updateRoom(room);
    }

    @Override
    public void onPeerLeft(final Room room, final List<String> list) {
        Log.d(TAG, "onPeerLeft");
        updateRoom(room);
    }

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

    @Override
    public void onPeersConnected(final Room room, final List<String> list) {
        Log.d(TAG, "onPeersConnected");
        updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(final Room room, final List<String> list) {
        Log.d(TAG, "onPeersDisconnected");
        updateRoom(room);
    }

    @Override
    public void onP2PConnected(final String s) {
        Log.d(TAG, "onP2PConnected");
    }

    @Override
    public void onP2PDisconnected(final String s) {
        Log.d(TAG, "onP2PDisconnected");
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        Log.d(TAG, "onConnected() called. Sign in successful!");
        createQuickMatch();
    }

    @Override
    public void onConnectionSuspended(final int statusCode) {
        googleApiClient.connect();
    }

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
