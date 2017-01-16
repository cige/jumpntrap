package com.jumpntrap.players;

import com.jumpntrap.model.Player;
import com.jumpntrap.model.Direction;
import com.jumpntrap.model.Position;
import com.jumpntrap.model.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * RandomPlayer defines a random player (computer) for the game.
 */
public final class RandomPlayer extends Player {
    /**
     * Constructor.
     */
    public RandomPlayer() {
    }

    /**
     * To perform an action.
     * @param game the game.
     */
    @Override
    public void actionRequired(final Game game) {
        game.handleMove(chooseMove(game), this);
    }

    /**
     * Get a random move.
     * @param game the game.
     * @return a random move.
     */
    private Direction chooseMove(final Game game) {
        final List<Direction> directions = new ArrayList<>();

        // Get valid directions
        for (final Direction direction : Direction.values()) {
            // Get player direction and add direction
            final Position newPosition = getPosition().add(direction.getPos());

            // Check if new position is valid
            if (game.boardContainsTile(newPosition) && !game.isTileOccupied(newPosition)) {
                directions.add(direction);
            }
        }

        // Simulating a reflexion time
        try {
            Thread.sleep((long) (500+Math.random()*500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // No moves found
        if (directions.isEmpty()) {
            return Direction.getRandom();
        }

        // Return random direction
        return directions.get(new Random().nextInt(directions.size()));
    }
}
