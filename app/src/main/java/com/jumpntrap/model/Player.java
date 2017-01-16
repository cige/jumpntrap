package com.jumpntrap.model;

public abstract class Player {

    private Position position;
    private boolean isDead;
    protected Game game;

    public Player() {
        isDead = false;
        this.position = null;
    }

    void setGame(final Game game) {
        this.game = game;
    }

    final void setPosition(final Position pos) {
        this.position = pos;
    }

    public final Position getPosition() {
        return position;
    }

    public final boolean isAlive(){
        return !isDead;
    }

    final void playMove(final Game game, final Direction direction){
        if(isDead)
            return;

        final Position newPosition = direction.newPosition(position);
        game.dropTile(position);

        if(!game.isTileOccupied(newPosition)){
            position = newPosition;
        }

        if(!game.boardContainsTile(position)){
            kill();
        }
    }

    protected void actionRequired(Game game) {
    }

    protected void kill(){
        isDead = true;
    }

    void resurrect() {
        isDead = false;
    }

}
