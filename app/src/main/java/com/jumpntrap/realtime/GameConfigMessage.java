package com.jumpntrap.realtime;

import com.jumpntrap.model.Game;
import com.jumpntrap.model.Player;
import com.jumpntrap.model.Position;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Victor on 10/01/2017.
 */

public class GameConfigMessage implements Serializable {
    private boolean[][] tiles;
    private int turn;
    private int[] positions;

    public GameConfigMessage(Game game) {
        tiles = game.getGameBoard().serialize();
        turn = game.getTurn();

        List<Player> players = game.getPlayers();
        positions = new int[players.size() * 2];
        for (int i = 0; i < players.size(); ++i) {
            Position pos = players.get(i).getPosition();
            final int index = i * 2;
            positions[index] = pos.line;
            positions[index + 1] = pos.column;
        }
    }

    public boolean[][] getTiles() {
        return tiles;
    }

    public int getTurn() {
        return turn;
    }

    public int[] getPositions() {
        return positions;
    }
}
