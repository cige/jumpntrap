package com.jumpntrap.model;

/**
 * Created by clementgeorge on 10/01/17.
 */

public interface GameObserver {

    public void onGameStarted(Game game);

    public void onGameOver(Game game,Player winner);

}
