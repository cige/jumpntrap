package com.jumpntrap.players;

import com.jumpntrap.model.Player;
import com.jumpntrap.model.Direction;
import com.jumpntrap.model.Position;
import com.jumpntrap.model.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPlayer extends Player {

    public RandomPlayer() {
    }

    @Override
    public void actionRequired(Game game) {
        Direction direction = chooseMove(game);
        game.handleMove(direction,this);
    }

    private Direction chooseMove(Game game) {
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
        int randomIndex = new Random().nextInt(directions.size());
        return directions.get(randomIndex);
    }
}
