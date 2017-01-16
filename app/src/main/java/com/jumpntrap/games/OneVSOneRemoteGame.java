package com.jumpntrap.games;

import com.jumpntrap.model.GameObserver;
import com.jumpntrap.model.Player;

/**
 * OneVSOneRemoteGame defines a game between two remote players.
 */
public final class OneVSOneRemoteGame extends OneVSOneGame implements GameObserver {
    /**
     * Flag to indicate if the game is hosted by the user device.
     */
    final private boolean isHost;

    /**
     * Constructor.
     * @param player1 the first player.
     * @param player2 the second player.
     * @param host true if the game is hosted on the user device, false if hosted on a remote device.
     */
    public OneVSOneRemoteGame(final Player player1, final Player player2, final boolean host) {
        super(player1, player2);
        this.isHost = host;
    }

    /**
     * Start the game.
     */
    @Override
    public final void start() {
        if (isHost) {
            start(null, -1, null);
        }
    }

    /**
     * Get the score of the first player.
     * @return the score of the first player.
     */
    @Override
    public final int getFirstPlayerScore(){
        return isHost ? player1Score : player2Score;
    }

    /**
     * Get the score of the second player.
     * @return the score of the second player.
     */
    @Override
    public final int getSecondPlayerScore(){
        return isHost ? player2Score : player1Score;
    }

    /**
     * Check if a player is the first player.
     * @param p the player to check if he is the first player.
     * @return true is the player is the first player.
     */
    @Override
    public final boolean isFirstPlayer(final Player p){
        return isHost ? p == player1 : p == player2;
    }

    /**
     * Check if a player is the second player.
     * @param p the player to check if he is the second player.
     * @return true is the player is the second player.
     */
    @Override
    public final boolean isSecondPlayer(final Player p){
        return isHost ? p == player2 : p == player1;
    }
}
