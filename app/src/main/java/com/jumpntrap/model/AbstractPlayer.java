package com.jumpntrap.model;

/**
 * Created by Victor on 13/12/2016.
 */

public abstract class AbstractPlayer {
    protected Position pos;
    protected boolean isDead;

    public AbstractPlayer() {
        isDead = false;
        pos = null;
    }

    public abstract boolean play(GameBoard gameBoard);

    public void setPosition(Position pos) {
        this.pos = pos;
    }

    public Position getPosition() {
        return pos;
    }
    /*
    public boolean isDead(GameBoard gameBoard) {
        return gameBoard.isTileFallen(pos.x, pos.y);
    }
    */
}
