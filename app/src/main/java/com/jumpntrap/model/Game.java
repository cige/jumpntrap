package com.jumpntrap.model;

import android.util.Log;

import com.jumpntrap.model.Player;
import com.jumpntrap.model.Direction;
import com.jumpntrap.model.GameBoard;
import com.jumpntrap.model.GameState;
import com.jumpntrap.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Victor on 13/12/2016.
 */

public abstract class Game {

    public final static int NB_COLUMNS = 4;
    public final static int NB_LINES = 6;

    private GameBoard gameBoard;
    private GameState state;

    final int nbPlayers;
    final List<Player> players;

    private int turn;

    public Game(int nbPlayers) {

        this.nbPlayers = nbPlayers;
        gameBoard = new GameBoard(NB_LINES, NB_COLUMNS,nbPlayers);
        players = new ArrayList<>();
        state = GameState.INITIAL;
    }

    final void addPlayer(Player player){
        players.add(player);
    }

    public List<Player> getPlayers(){
        return players;
    }

    private void setPlayersPositions() {
        final Random rand = new Random();

        // Place players with a random algorithm
        for (Player player : getPlayers()) {
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
        for (Player player : getPlayers()) {
            Position pos = player.getPosition();

            if (pos != null && pos.equals(position)) {
                return true;
            }
        }

        return false;
    }

    private final int toss(){
        turn = (int)(Math.random() * nbPlayers);
        return turn;
    }

    public boolean boardContainsTile(Position pos) {
        return gameBoard.containsTile(pos);
    }

    public void start() {

        if(players.size() < nbPlayers)
            throw new RuntimeException("The game require more players");

        if(players.size() > nbPlayers)
            throw new RuntimeException("Too many players in the game");

        setPlayersPositions();
        this.state = GameState.STARTED;

    }

    public void restart() {

        for(Player p: getPlayers()) {
            p.setPosition(null);
            p.resurrect();
        }

        gameBoard.restart(nbPlayers);

       start();
    }

    /**
     * A game isn't over if at least 2 players are still alive.
     * @return the winner of the game, if exists.
     */
    public Player checkIsOver() {

        Player winner = null;

        for (Player player : getPlayers()) {

            if (player.isAlive()) {

                if (winner != null){
                    return null;
                }
                winner = player;
            }

        }

        this.state = GameState.GAMEOVER;
        return winner;
    }

    public final void dropTile(Position position) {
        this.gameBoard.dropTile(position);
    }

    public final GameBoard getGameBoard() {
        return gameBoard;
    }

    final void handleMove(Direction direction, Player player){
        Player nextPlayer = players.get(turn);
        if(player != nextPlayer)
            return;
        player.playMove(this,direction);
        turn = (turn + 1) % nbPlayers;
    }

    public void handleHumanMove(Direction direction, Player player){
    };

    public void handleRemoteMove(Direction direction,Player player){
    };

    public void handleIAMove(Direction direction,Player player){
    };

    final void waitHumanMove(){
        if(!isOver())
        this.state = GameState.WAITING_HUMAN_MOVE;
    }

    final void waitIAMove(){
        if(!isOver())
        this.state = GameState.WAITING_IA_MOVE;
    }

    final void waitRemoteMove(){
        if(!isOver())
        this.state = GameState.WAITING_REMOTE_MOVE;
    }

    final boolean isOver(){
        return this.state == GameState.GAMEOVER;
    }

    final boolean isWaitingHumanMove(){
        return this.state == GameState.WAITING_HUMAN_MOVE;
    }

    final boolean isWaitingIAMove(){
        return this.state == GameState.WAITING_IA_MOVE;
    }

    final boolean isWaitingRemoteMove(){
        return this.state == GameState.WAITING_REMOTE_MOVE;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        Boolean[][] board = new Boolean[NB_LINES][NB_COLUMNS];


        for (Player player : getPlayers()) {
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

}
