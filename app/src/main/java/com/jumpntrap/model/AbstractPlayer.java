package com.jumpntrap.model;

import android.support.annotation.NonNull;

/**
 * Created by Victor on 13/12/2016.
 */

public abstract class AbstractPlayer {
    protected Position position;
    private boolean isDead;

    public AbstractPlayer() {
        isDead = false;
        position = null;
    }

    @NonNull
    public abstract Direction chooseMove(Game game);

    public void setPosition(Position pos) {
        this.position = pos;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isAlive(){
        return !isDead;
    }

    public boolean playMove(Game game, Direction direction){

        Position newPosition = direction.newPosition(position);
        game.dropTile(position);

        if(game.boardContainsTile(newPosition) && ! game.isTileOccupied(newPosition)){
            position = newPosition;
            return true;
        }

        isDead = true;
        return false;
    }
}
