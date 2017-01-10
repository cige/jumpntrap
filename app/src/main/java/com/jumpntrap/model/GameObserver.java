package com.jumpntrap.model;

/**
 * Created by clementgeorge on 10/01/17.
 */

public interface GameObserver {

    public void onGameStarted(Game game);
    public void onMovedPlayed(Game game, Player player, Direction move);
    public void onGameOver(Game game, Player winner);

}
