package com.jumpntrap.model;

public interface GameObserver {

    void onGameStarted(Game game);
    void onMovedPlayed(Game game, Player player, Direction move);
    void onGameOver(Game game, Player winner);

}
