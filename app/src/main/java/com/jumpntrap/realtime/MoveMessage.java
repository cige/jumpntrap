package com.jumpntrap.realtime;

import com.jumpntrap.model.Direction;

import java.io.Serializable;


public final class MoveMessage implements Serializable {

    private final Direction direction;

    public MoveMessage(final Direction direction) {
        this.direction = direction;
    }

    public final Direction getDirection() {
        return direction;
    }
}
