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
    public boolean play(Game game) {
        List<Position> positions = new ArrayList<>();
        // Get valid positions
        for (Direction direction : Direction.values()) {
            // Get player direction and add direction
            Position currentPos = new Position(pos);
            currentPos.add(direction.getPos());

            // Check if new position is valid
            if (!game.boardContainsTile(currentPos) && !game.isTileOccupied(currentPos)) {
                positions.add(currentPos);
            }
        }

        // No moves found
        if (positions.isEmpty()) {
            return false;
        }

        // Set new position
        int randomIndex = new Random().nextInt(positions.size());
        setPosition(positions.get(randomIndex));

        return true;
    }
}
