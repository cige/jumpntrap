package com.jumpntrap.model;

/**
 * Direction defines the list of the possible moves for the game.
 */
public enum Direction {
    NW(new Position(-1, -1)),
    N(new Position(-1, 0)),
    NE(new Position(-1, 1)),
    E(new Position(0, 1)),
    SE(new Position(1, 1)),
    S(new Position(1, 0)),
    SW(new Position(1, -1)),
    W(new Position(0, -1));

    /**
     * The position.
     */
    private final Position pos;

    /**
     * Constructor.
     * @param pos the position.
     */
    Direction(final Position pos) {
        this.pos = pos;
    }

    /**
     * Get the position.
     * @return the position.
     */
    public Position getPos() {
        return pos;
    }

    /**
     * Get a random position.
     * @return a random position.
     */
    public static Direction getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }

    /**
     * Get the position from the sum of the two positions.
     * @param position the position to add.
     * @return the position from the sum of the two positions.
     */
    public final Position newPosition(Position position) {
        return new Position(position.line + this.pos.line, position.column + this.pos.column);
    }
}
