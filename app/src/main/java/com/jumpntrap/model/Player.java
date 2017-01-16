package com.jumpntrap.model;

/**
 * Player defines a player for the game.
 */
public abstract class Player {
    /**
     * The position.
     */
    private Position position;

    /**
     * Flag to indicate if the player is dead.
     */
    private boolean isDead;

    /**
     * The game.
     */
    protected Game game;

    /**
     * Constructor.
     */
    public Player() {
        isDead = false;
        this.position = null;
    }

    /**
     * Set the game.
     * @param game the game to set.
     */
    void setGame(final Game game) {
        this.game = game;
    }

    /**
     * Set the position.
     * @param pos the position to set.
     */
    final void setPosition(final Position pos) {
        this.position = pos;
    }

    /**
     * Get the position.
     * @return the position.
     */
    public final Position getPosition() {
        return position;
    }

    /**
     * Check if the player is alive.
     * @return true is the player is alive.
     */
    public final boolean isAlive(){
        return !isDead;
    }

    /**
     * Play a move with a given direction.
     * @param game the game.
     * @param direction the direction to go.
     */
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

    /**
     * To perform an action.
     * @param game the game.
     */
    protected void actionRequired(final Game game) {
    }

    /**
     * Kill the player.
     */
    protected void kill(){
        isDead = true;
    }

    /**
     * Resurrect the player.
     */
    void resurrect() {
        isDead = false;
    }
}
