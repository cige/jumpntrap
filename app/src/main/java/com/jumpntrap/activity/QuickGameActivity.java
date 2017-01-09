package com.jumpntrap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

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
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.jumpntrap.R;
import com.jumpntrap.model.Player;
import com.jumpntrap.model.Game;
import com.jumpntrap.util.RoomUtils;
import com.jumpntrap.view.GameView;

import java.util.List;

public class QuickGameActivity extends BaseGameActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        RoomUpdateListener, RoomStatusUpdateListener, RealTimeMessageReceivedListener {
    private static final String TAG = "QuickGameActivity";

    private GoogleApiClient googleApiClient;

    private List<Participant> participants;
    private String myId;
    private String roomId;

    private Game game;
    private Player playerOne;
    private Player playerTwo;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = getApiClient();
        googleApiClient.registerConnectionCallbacks(this);
        googleApiClient.registerConnectionFailedListener(this);

        if (!googleApiClient.isConnected()) {
            Log.d(TAG, "Connecting client.");
            googleApiClient.connect();
        }
        else {
            Log.w(TAG, "GameHelper: client was already connected on onStart()");
        }
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }

    private void createQuickMatch() {
        // Quick-start a game with 1 randomly selected opponent
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, 0);

        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
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
                    startGame();
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startGame() {
        Log.d(TAG, "startGame");

        final Participant participant = participants.get(0);

        Log.d(TAG, myId + " -- " + participant.getParticipantId());
        if (myId.equals(participant.getParticipantId())) {
            //game = new HumanVSRemoteGame();
            byte[] mMsgBuf = new byte[1];
            mMsgBuf[0] = 'A';
            Games.RealTimeMultiplayer.sendUnreliableMessage(googleApiClient, mMsgBuf, roomId, participants.get(1).getParticipantId());
        }
    }

    private void updateRoom(Room room) {
        if (room != null) {
            participants = room.getParticipants();
        }
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        Log.d(TAG, "onRealTimeMessageReceived");

        Log.d(TAG, "RECEIVED : " + new String(realTimeMessage.getMessageData()));
    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated");
        if (statusCode == GamesStatusCodes.STATUS_OK) {
            roomId = room.getRoomId();
            Log.d(TAG, "Nb part : " + room.getParticipantIds().size());
            RoomUtils.showWaitingRoom(this, room, googleApiClient);
        }
        else {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
        }
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
        if (statusCode == GamesStatusCodes.STATUS_OK) {
            updateRoom(room);
        }
        else {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
        }
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
