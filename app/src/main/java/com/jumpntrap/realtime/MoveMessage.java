package com.jumpntrap.realtime;

import com.jumpntrap.model.Direction;

import java.io.Serializable;

/**
 * MoveMessage defines a move of a player message.
 */
public final class MoveMessage implements Serializable {
    /**
     * The direction to go..
     */
    private final Direction direction;

    /**
     * Constructor.
     * @param direction the direction to go..
     */
    public MoveMessage(final Direction direction) {
        this.direction = direction;
    }

    /**
     * Get the direction to go.
     * @return the direction to go.
     */
    public final Direction getDirection() {
        return direction;
    }
}
