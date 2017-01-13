package com.jumpntrap.model;

public abstract class Player {

    private Position position;
    private boolean isDead;
    protected Game game;

    public Player() {
        isDead = false;
        this.position = null;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public final void setPosition(Position pos) {
        this.position = pos;
    }

    public final Position getPosition() {
        return position;
    }

    public final boolean isAlive(){
        return !isDead;
    }

    public final void playMove(Game game, Direction direction){

        if(isDead)
            return;

        Position newPosition = direction.newPosition(position);
        game.dropTile(position);

        if(game.boardContainsTile(newPosition) && !game.isTileOccupied(newPosition)){
            position = newPosition;
            return;
        }

        isDead = true;
    }

    public void actionRequired(Game game) {
    }

    public void resurrect() {
        isDead = false;
    }
}
