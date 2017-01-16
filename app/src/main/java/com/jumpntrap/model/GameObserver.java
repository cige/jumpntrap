package com.jumpntrap.model;

public interface GameObserver {

    void onGameStarted(final Game game);
    void onMovedPlayed(final Game game, final Player player, final Direction move);
    void onGameOver(final Game game, final Player winner);

}
