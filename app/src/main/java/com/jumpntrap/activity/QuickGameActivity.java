package com.jumpntrap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
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
import com.jumpntrap.dialog.RematchRemoteDialog;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.OneVSOneRemoteGame;
import com.jumpntrap.model.Player;
import com.jumpntrap.players.HumanPlayer;
import com.jumpntrap.players.RemotePlayer;
import com.jumpntrap.util.RoomUtils;

import java.util.List;

public class QuickGameActivity extends GameActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        RoomUpdateListener, RoomStatusUpdateListener, RealTimeMessageReceivedListener {
    private static final String TAG = "QuickGameActivity";

    private GoogleApiClient googleApiClient;

    private List<Participant> participants;
    private String myId;
    private String roomId;

    private RemotePlayer remotePlayer = null;
    private boolean isHost = false;

    private RematchRemoteDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public void onGameOver(Game game, Player winner) {
        if (this.game != game) {
            return;
        }

        final int userScore = this.game.getUserScore();
        final int opponentScore = this.game.getOpponentScore();

        final TextView scoreBottom = (TextView) findViewById(R.id.score_bottom);
        final TextView scoreTop = (TextView) findViewById(R.id.score_top);

        final OneVSOneRemoteGame remoteGame = (OneVSOneRemoteGame) game;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreBottom.setText(String.valueOf(userScore));
                scoreTop.setText(String.valueOf(opponentScore));

                dialog = new RematchRemoteDialog(
                        QuickGameActivity.this,
                        googleApiClient,
                        remoteGame,
                        roomId,
                        participants.get(isHost ? 0 : 1),
                        participants.get(isHost ? 1 : 0),
                        isHost
                );
                dialog.show();
            }
        });
    }

    private void showGameBoard(final int view) {
        findViewById(R.id.board).setVisibility(view);
        findViewById(R.id.topBar).setVisibility(view);
        findViewById(R.id.bottomBar).setVisibility(view);
    }

    private void showSpinner(boolean show) {
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Launch waiting room
        Games.RealTimeMultiplayer.create(googleApiClient, rtmConfigBuilder.build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        switch (requestCode) {
            case RoomUtils.RC_WAITING_ROOM:
                // we got the result from the "waiting room" UI.
                if (resultCode == Activity.RESULT_OK) {
                    // ready to start playing
                    Log.d(TAG, "Starting game");
                    showSpinner(false);
                    initGame();
                }
                /*
                else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                }
                */
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
            game = new OneVSOneRemoteGame(humanPlayer, remotePlayer);
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

    private void updateRoom(Room room) {
        if (room != null) {
            participants = room.getParticipants();
        }
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        Log.d(TAG, "onRealTimeMessageReceived");

        // Create game is not created yet
        if (game == null) {
            createPlayersAndGame();
        }

        byte[] buff = realTimeMessage.getMessageData();
        remotePlayer.handleRealTimeMessageReceived(game, buff, dialog);
    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            return;
        }

        roomId = room.getRoomId();
        Log.d(TAG, "Nb part : " + room.getParticipantIds().size());
        showSpinner(false);
        RoomUtils.showWaitingRoom(this, room, googleApiClient);
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom");
    }

    @Override
    public void onLeftRoom(int statusCode, String s) {
        Log.d(TAG, "onLeftRoom");
    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            return;
        }

        updateRoom(room);
    }

    @Override
    public void onRoomConnecting(Room room) {
        Log.d(TAG, "onRoomConnecting");
        Log.d(TAG, "Nb part : " + room.getParticipantIds().size());
        updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        Log.d(TAG, "onRoomAutoMatching");
        updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {
        Log.d(TAG, "onPeerInvitedToRoom");
    }

    @Override
    public void onPeerDeclined(Room room, List<String> list) {
        Log.d(TAG, "onPeerInvitedToRoom");
    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {
        Log.d(TAG, "onPeerJoined");
        Log.d(TAG, "Nb part : " + room.getParticipantIds().size());
        updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> list) {
        Log.d(TAG, "onPeerLeft");
        updateRoom(room);
    }

    @Override
    public void onConnectedToRoom(Room room) {
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
    public void onDisconnectedFromRoom(Room room) {
        Log.d(TAG, "onDisconnectedFromRoom");
        roomId = null;
    }

    @Override
    public void onPeersConnected(Room room, List<String> list) {
        Log.d(TAG, "onPeersConnected");
        updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> list) {
        Log.d(TAG, "onPeersDisconnected");
        updateRoom(room);
    }

    @Override
    public void onP2PConnected(String s) {
        Log.d(TAG, "onP2PConnected");
    }

    @Override
    public void onP2PDisconnected(String s) {
        Log.d(TAG, "onP2PDisconnected");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected() called. Sign in successful!");
        createQuickMatch();
    }

    @Override
    public void onConnectionSuspended(int statusCode) {
        googleApiClient.connect();
    }

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;

    private final static int RC_SIGN_IN = 9001;
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(
                    this,
                    googleApiClient,
                    connectionResult,
                    RC_SIGN_IN,
                    getString(R.string.signin_error)
            );
        }
    }
}
