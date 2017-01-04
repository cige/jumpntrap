package com.jumpntrap.model;

import java.util.Observable;
import java.util.Random;

/**
 * Created by Victor on 13/12/2016.
 */

public class Game{

    public final static int NB_COLUMNS = 4;
    public final static int NB_LINES = 6;

    private GameBoard gameBoard;
    private AbstractPlayer[] players;

    private int turn;
    private boolean isOver;

    public Game(AbstractPlayer... players) {
        assert players.length < NB_COLUMNS * NB_LINES;


        // "Init" players
        this.players = new AbstractPlayer[players.length];
        for (int i = 0; i < players.length; ++i) {
            this.players[i] = players[i];
        }

        gameBoard = new GameBoard(NB_LINES, NB_COLUMNS, this.players.length);
        setPlayersPositions();
        turn = 0;
        isOver = false;
    }


    private void setPlayersPositions() {
        final Random rand = new Random();

        // Place players with a random algorithm
        for (AbstractPlayer player : players) {
            int line, column;
            Position pos;

            do {
                line = rand.nextInt(NB_LINES);
                column = rand.nextInt(NB_COLUMNS);
                pos = new Position(line, column);

            } while (!gameBoard.containsTile(pos) || isTileOccupied(pos));

            player.setPosition(pos);
        }
    }

    public boolean isTileOccupied(Position position) {
        for (AbstractPlayer player : players) {
            Position pos = player.getPosition();

            if (pos != null && pos.equals(position)) {
                return true;
            }
        }

        return false;
    }

    public boolean boardContainsTile(Position pos) {
        return gameBoard.containsTile(pos);
    }

    public void start() {

        while (!isOver) {
            Direction d = players[turn].chooseMove(this);
            boolean b = players[turn].playMove(this, d);
            if (!b)
                checkIsOver();
            turn = (turn + 1) % players.length;

        }
    }

    private void checkIsOver() {

        boolean flag = false;

        for (AbstractPlayer player : players) {

            if (!player.isAlive()) {
                if (flag) {
                    isOver = false;
                    return;
                }
                flag = true;
            }

        }

        isOver = true;
    }

    public void dropTile(Position position) {
        this.gameBoard.dropTile(position);
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        Boolean[][] board = new Boolean[NB_LINES][NB_COLUMNS];


        for (AbstractPlayer player : players) {
            Position p = player.getPosition();
            if (p != null)
                board[p.line][p.column] = true;
        }

        for (int i = 0; i < NB_LINES; i++) {
            for (int j = 0; j < NB_COLUMNS; j++) {

                Position position = new Position(i, j);
                if (board[i][j] == Boolean.TRUE) {
                    builder.append("P");
                    continue;
                }

                if (gameBoard.containsTile(position)) {
                    builder.append("O");
                    continue;
                }

                builder.append("X");
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public AbstractPlayer[] getPlayers(){
        return players;
    }
}
