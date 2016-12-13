package com.jumpntrap.model;

/**
 * Created by Victor on 13/12/2016.
 */

public enum Direction {
    NW(new Position(-1, -1)),
    N(new Position(-1, -1)),
    NE(new Position(-1, -1)),
    E(new Position(-1, -1)),
    SE(new Position(-1, -1)),
    S(new Position(-1, -1)),
    SW(new Position(-1, -1)),
    W(new Position(-1, -1));

    private final Position pos;

    private Direction(Position pos) {
        this.pos = pos;
    }

    public Position getPos() {
        return pos;
    }

    public static Direction getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }
}
