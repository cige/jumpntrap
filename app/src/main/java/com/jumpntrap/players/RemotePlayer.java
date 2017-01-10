package com.jumpntrap.players;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.jumpntrap.model.Direction;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.GameObserver;
import com.jumpntrap.model.Player;
import com.jumpntrap.realtime.GameConfigMessage;
import com.jumpntrap.realtime.MoveMessage;

import org.apache.commons.lang3.SerializationUtils;

/**
 * Created by Victor on 10/01/2017.
 */

public final class RemotePlayer extends Player implements GameObserver {
    private final static String TAG = "RemotePlayer";

    private final GoogleApiClient googleApiClient;
    private final String roomId;
    private final String destId;

    private boolean isHost;

    public RemotePlayer(final GoogleApiClient googleApiClient, final String roomId, final String destId) {
        this.googleApiClient = googleApiClient;
        this.roomId = roomId;
        this.destId = destId;
        isHost = false;
    }

    @Override
    public void onGameStarted(Game game) {
        Log.d(TAG, "onGameStarted");
        if (isHost)
            return;

        Log.d(TAG, "onGameStarted : sendMessage");
        GameConfigMessage gdm = new GameConfigMessage(game);
        byte[] buff = SerializationUtils.serialize(gdm);
        Games.RealTimeMultiplayer.sendUnreliableMessage(googleApiClient, buff, roomId, destId);
    }

    @Override
    public void onMovedPlayed(Game game, Player player, Direction move) {
        Log.d(TAG, "onMovedPlayed");

        if(player == this)
            return;

        Log.d(TAG, "onMovedPlayed : sendMessage");

        MoveMessage mm = new MoveMessage(move);
        byte[] buff = SerializationUtils.serialize(mm);
        Games.RealTimeMultiplayer.sendUnreliableMessage(googleApiClient, buff, roomId, destId);
    }

    @Override
    public void onGameOver(Game game, Player winner) {
        Log.d(TAG, "onGameOver");


    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public void handleRealTimeMessageReceived(Game game, byte[] buff) {
        Log.d(TAG, "handleRealTimeMessageReceived");

        switch (game.getGameState()) {
            // We have to init the game
            case INITIAL:
                Log.d(TAG, "INITIAL");
                // TODO : HANDLE WHEN THE MESSAGE IS "INVALID"
                final GameConfigMessage gcm = SerializationUtils.deserialize(buff);
                game.start(gcm.getTiles(), gcm.getTurn(), gcm.getPositions());
                break;

            case STARTED:
                Log.d(TAG, "STARTED");
                final MoveMessage mm = SerializationUtils.deserialize(buff);
                game.handleMove(mm.getDirection(), this);
                break;

            case GAMEOVER:
                Log.d(TAG, "GAMEOVER");
                final GameConfigMessage gcm2 = SerializationUtils.deserialize(buff);
                game.start(gcm2.getTiles(), gcm2.getTurn(), gcm2.getPositions());
                break;
        }
    }
}
