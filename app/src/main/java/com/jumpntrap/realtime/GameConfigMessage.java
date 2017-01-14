package com.jumpntrap.realtime;

import com.jumpntrap.model.Game;
import com.jumpntrap.model.Player;
import com.jumpntrap.model.Position;

import java.io.Serializable;
import java.util.List;

public final class GameConfigMessage implements Serializable {
    private final boolean[][] tiles;
    private final int turn;
    private final int[] positions;

    public GameConfigMessage(final Game game) {
        tiles = game.getGameBoard().serialize();
        turn = game.getTurn();

        final List<Player> players = game.getPlayers();
        positions = new int[players.size() * 2];
        for (int i = 0; i < players.size(); ++i) {
            final Position pos = players.get(i).getPosition();
            final int index = i * 2;
            positions[index] = pos.line;
            positions[index + 1] = pos.column;
        }
    }

    public final boolean[][] getTiles() {
        return tiles;
    }

    public final int getTurn() {
        return turn;
    }

    public final int[] getPositions() {
        return positions;
    }
}
