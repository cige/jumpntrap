package com.jumpntrap.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Victor on 13/12/2016.
 */

public class RandomPlayer extends AbstractPlayer {
    public RandomPlayer() {
        super();
    }

    @Override
    public Direction chooseMove(Game game) {
        List<Direction> directions = new ArrayList<>();
        // Get valid directions
        for (Direction direction : Direction.values()) {
            // Get player direction and add direction
            Position currentPos = new Position(position);
            currentPos.add(direction.getPos());

            // Check if new position is valid
            if (game.boardContainsTile(currentPos) && !game.isTileOccupied(currentPos)) {
                directions.add(direction);
            }
        }

        // No moves found
        if (directions.isEmpty()) {
            return Direction.getRandom();
        }

        // Simulating a reflexion time
        try {
            Thread.sleep((long) (Math.random()*1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Return random direction
        int randomIndex = new Random().nextInt(directions.size());
        return directions.get(randomIndex);
    }
}
