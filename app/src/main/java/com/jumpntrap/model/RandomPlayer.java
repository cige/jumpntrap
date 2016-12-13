package com.jumpntrap.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor on 13/12/2016.
 */

public class RandomPlayer extends AbstractPlayer {
    private Map<Direction, Position> directionPositionMap;

    public RandomPlayer() {
        super();

        directionPositionMap = new HashMap<>(Direction.values().length);
        directionPositionMap.put(Direction.NW, new Position(-1, -1));
        directionPositionMap.put(Direction.N, new Position(0, -1));
        directionPositionMap.put(Direction.NE, new Position(1, -1));
        directionPositionMap.put(Direction.E, new Position(1, 0));
        directionPositionMap.put(Direction.SE, new Position(1, 1));
        directionPositionMap.put(Direction.S, new Position(0, 1));
        directionPositionMap.put(Direction.SW, new Position(-1, 1));
        directionPositionMap.put(Direction.W, new Position(-1, 0));
    }

    @Override
    public boolean play(Game game) {
        /*
        Position pos = null;
        do {
            pos = directionPositionMap.get(Direction.getRandom());

        } while (game.isTileOccupied(this));
        */

        return false;
    }
}
