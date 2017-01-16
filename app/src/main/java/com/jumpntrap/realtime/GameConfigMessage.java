package com.jumpntrap.realtime;

import com.jumpntrap.model.Game;
import com.jumpntrap.model.Player;
import com.jumpntrap.model.Position;

import java.io.Serializable;
import java.util.List;

/**
 * GameConfigMessage defines a game configuration message.
 */
public final class GameConfigMessage implements Serializable {
    /**
     * The tiles of the game board.
     */
    private final boolean[][] tiles;

    /**
     * The turn of the player to play.
     */
    private final int turn;

    /**
     * The position of the players.
     */
    private final int[] positions;

    /**
     * Constructor.
     * @param game the game.
     */
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

    /**
     * Get the tiles of the board.
     * @return the tiles of the board.
     */
    public final boolean[][] getTiles() {
        return tiles;
    }

    /**
     * Get the turn of the player to player.
     * @return the turn of the player to player.
     */
    public final int getTurn() {
        return turn;
    }

    /**
     * Get the positions of the players.
     * @return the positions of the playrs.
     */
    public final int[] getPositions() {
        return positions;
    }
}
