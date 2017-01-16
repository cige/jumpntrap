package com.jumpntrap.players;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.jumpntrap.dialog.RematchRemoteDialog;
import com.jumpntrap.model.Direction;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.GameObserver;
import com.jumpntrap.model.Player;
import com.jumpntrap.realtime.GameConfigMessage;
import com.jumpntrap.realtime.MoveMessage;
import com.jumpntrap.realtime.RematchMessage;

import org.apache.commons.lang3.SerializationUtils;

/**
 * RemotePlayer defines a remote player for the game.
 */
public final class RemotePlayer extends Player implements GameObserver {
    /**
     * A tag for debug purpose.
     */
    private final static String TAG = "RemotePlayer";

    /**
     * Google API client.
     */
    private final GoogleApiClient googleApiClient;

    /**
     * The current room ID.
     */
    private final String roomId;

    /**
     * The ID of the other player.
     */
    private final String destId;

    /**
     * Flag to indicate if the player is the host.
     */
    private boolean isHost;

    /**
     * Constructor.
     * @param googleApiClient the Google API client.
     * @param roomId the current room ID.
     * @param destId the ID of the other player.
     */
    public RemotePlayer(final GoogleApiClient googleApiClient, final String roomId, final String destId) {
        this.googleApiClient = googleApiClient;
        this.roomId = roomId;
        this.destId = destId;
        isHost = false;
    }

    /**
     * Callback when the game starts.
     * @param game the game.
     */
    @Override
    public void onGameStarted(final Game game) {
        Log.d(TAG, "onGameStarted");
        if (isHost)
            return;

        Log.d(TAG, "onGameStarted : sendMessage");
        GameConfigMessage gdm = new GameConfigMessage(game);
        byte[] buff = SerializationUtils.serialize(gdm);
        Games.RealTimeMultiplayer.sendUnreliableMessage(googleApiClient, buff, roomId, destId);
    }

    /**
     * Callback when a move is played.
     * @param game the game.
     * @param player the current player.
     * @param move the move played.
     */
    @Override
    public void onMovedPlayed(final Game game, final Player player, final Direction move) {
        Log.d(TAG, "onMovedPlayed");

        if(player == this)
            return;

        Log.d(TAG, "onMovedPlayed : sendMessage");

        MoveMessage mm = new MoveMessage(move);
        byte[] buff = SerializationUtils.serialize(mm);
        Games.RealTimeMultiplayer.sendUnreliableMessage(googleApiClient, buff, roomId, destId);
    }

    /**
     * Callback when the game is over.
     * @param game the game.
     * @param winner the player who won.
     */
    @Override
    public void onGameOver(final Game game, final Player winner) {
        Log.d(TAG, "onGameOver");
    }

    /**
     * Set the host.
     * @param host the host to set.
     */
    public void setHost(boolean host) {
        isHost = host;
    }

    /**
     * Handle a real time message.
     * @param game the game.
     * @param buff the buffer message received.
     * @param dialog the rematch alert dialog.
     */
    public void handleRealTimeMessageReceived(final Game game, final byte[] buff, final RematchRemoteDialog dialog) {
        Log.d(TAG, "handleRealTimeMessageReceived");

        switch (game.getGameState()) {
            // We have to init the game
            case INITIAL:
                Log.d(TAG, "INITIAL");
                final GameConfigMessage gcm = SerializationUtils.deserialize(buff);
                game.start(gcm.getTiles(), gcm.getTurn(), gcm.getPositions());
                break;

            case STARTED:
                Log.d(TAG, "STARTED");
                final MoveMessage mm = SerializationUtils.deserialize(buff);
                game.handleMove(mm.getDirection(), this);
                break;

            case GAME_OVER:
                Log.d(TAG, "GAME OVER");
                final RematchMessage rm = SerializationUtils.deserialize(buff);
                if (rm.isWantsToRematch()) {
                    dialog.setRemotePlayerTextAsReady();
                }
        }
    }
}
