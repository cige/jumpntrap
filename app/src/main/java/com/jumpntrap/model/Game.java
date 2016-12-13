package com.jumpntrap.model;

import java.util.Random;

/**
 * Created by Victor on 13/12/2016.
 */

public class Game {
    private GameBoard gameBoard;
    private AbstractPlayer[] players;
    private final static int NB_COLUMNS = 8;
    private final static int NB_LINES = 8;


    private int turn;

    public Game(AbstractPlayer... players) {
        assert players.length < NB_COLUMNS * NB_LINES;


        // "Init" players
        this.players = new AbstractPlayer[players.length];
        for (int i = 0; i < players.length; ++i) {
            this.players[i] = players[i];
        }

        gameBoard = new GameBoard(NB_COLUMNS, NB_LINES, this.players.length);
        setPlayersPositions();
        turn = 0;
    }


    private void setPlayersPositions() {
        final Random rand = new Random();

        // Place players with a random algorithm
        for (AbstractPlayer player : players) {
            int x, y;
            do {
                x = rand.nextInt(NB_COLUMNS);
                y = rand.nextInt(NB_LINES);

            } while (gameBoard.isTileFallen(x, y) || isTileOccupied(player));

            player.setPosition(new Position(x, y));
        }
    }

    public boolean isTileOccupied(AbstractPlayer currentPlayer) {
        for (AbstractPlayer player : players) {
            if (player != currentPlayer) {
                Position pos = player.getPosition();

                if (pos != null && pos.equals(currentPlayer.getPosition())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isTileOccupied(AbstractPlayer currentPlayer, int x, int y) {
        for (AbstractPlayer player : players) {
            if (player != currentPlayer) {
                Position pos = player.getPosition();

                if (pos != null && pos.equals(currentPlayer.getPosition())) {
                    return true;
                }
            }
        }

        return false;
    }
}
