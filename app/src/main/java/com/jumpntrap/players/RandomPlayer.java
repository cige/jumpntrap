package com.jumpntrap.players;

import com.jumpntrap.model.Player;
import com.jumpntrap.model.Direction;
import com.jumpntrap.model.Position;
import com.jumpntrap.model.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Victor on 13/12/2016.
 */

public class RandomPlayer extends Player {

    public RandomPlayer() {
    }

    @Override
    public void actionRequired(Game game) {
        Direction direction = chooseMove(game);
        game.handleMove(direction,this);
    }

    private final Direction chooseMove(Game game) {
        List<Direction> directions = new ArrayList<>();
        // Get valid directions
        for (Direction direction : Direction.values()) {
            // Get player direction and add direction
            Position newPosition = getPosition().add(direction.getPos());

            // Check if new position is valid
            if (game.boardContainsTile(newPosition) && !game.isTileOccupied(newPosition)) {
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
