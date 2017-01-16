package com.jumpntrap.model;

/**
 * GameObserver defines an observer interface for the game.
 */
public interface GameObserver {

    /**
     * Callback when the game starts.
     * @param game the game.
     */
    void onGameStarted(final Game game);

    /**
     * Callback when a move is played.
     * @param game the game.
     * @param player the current player.
     * @param move the move played.
     */
    void onMovedPlayed(final Game game, final Player player, final Direction move);

    /**
     * Callback when the game is over.
     * @param game the game.
     * @param winner the player who won.
     */
    void onGameOver(final Game game, final Player winner);
}
