package com.jumpntrap.model;

import java.util.Random;

/**
 * Created by Victor on 13/12/2016.
 */

public class Game {

    private final static int NB_COLUMNS = 8;
    private final static int NB_LINES = 8;

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
                pos = new Position(line,column);

            } while (gameBoard.isTileFallen(pos) || isTileOccupied(pos));

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

    public void start(){

        while(!isOver){

            players[turn].play(this);
            checkIsOver();
            turn = (turn + 1) % players.length;

        }
    }

    private void checkIsOver(){

        boolean flag = false;

        for(AbstractPlayer player:players){

            if(!player.isAlive()) {
                if (flag) {
                    isOver = false;
                    return;
                }
                flag = true;
            }

        }

        isOver = true;
    }

    @Override
    public String toString(){
        boolean[][] board = new boolean[NB_LINES][NB_COLUMNS];
        //TODO cige
        return null;
    }
}
