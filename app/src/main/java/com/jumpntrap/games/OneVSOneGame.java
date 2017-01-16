package com.jumpntrap.games;

import com.jumpntrap.model.Direction;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.GameObserver;
import com.jumpntrap.model.Player;

/**
 * OneVSOneGame defines a game between two local players.
 */
public class OneVSOneGame extends Game implements GameObserver {
    /**
     * The first player.
     */
    final Player player1;

    /**
     * The second player.
     */
    final Player player2;

    /**
     * Score of the first player.
     */
    int player1Score = 0;

    /**
     * Score of the second player.
     */
    int player2Score = 0;

    /**
     * Constructor.
     * @param player1 the first player.
     * @param player2 the second player.
     */
    public OneVSOneGame(Player player1,Player player2) {
        super(2);
        this.player1 = player1;
        this.player2 = player2;
        addPlayer(this.player1);
        addPlayer(this.player2);
        this.addObserver(this);
    }

    /**
     * Get the score of the first player.
     * @return the score of the first player.
     */
    public int getFirstPlayerScore(){
        return player1Score;
    }

    /**
     * Get the score of the second player.
     * @return the score of the second player.
     */
    public int getSecondPlayerScore(){
        return player2Score;
    }

    /**
     * Check if a player is the first player.
     * @param p the player to check if he is the first player.
     * @return true is the player is the first player.
     */
    public boolean isFirstPlayer(final Player p){
        return p == player1;
    }

    /**
     * Check if a player is the second player.
     * @param p the player to check if he is the second player.
     * @return true is the player is the second player.
     */
    public boolean isSecondPlayer(final Player p){
        return p == player2;
    }

    /**
     * Callback when the game starts.
     * @param game the game.
     */
    @Override
    public void onGameStarted(final Game game) {
    }

    /**
     * Callback when a move is played.
     * @param game the game.
     * @param player the current player.
     * @param move the move played.
     */
    @Override
    public void onMovedPlayed(final Game game, final Player player, final Direction move) {
    }

    /**
     * Callback when the game is over.
     * @param game the game.
     * @param winner the player who won.
     */
    @Override
    public void onGameOver(final Game game, final Player winner) {
        if(winner == player1)
            player1Score ++;
        else if(winner == player2)
            player2Score ++;
    }
}
