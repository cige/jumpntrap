package com.jumpntrap.realtime;

import com.jumpntrap.model.Direction;

import java.io.Serializable;

/**
 * Created by Victor on 10/01/2017.
 */

public class MoveMessage implements Serializable {
    final Direction direction;

    public MoveMessage(final Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
