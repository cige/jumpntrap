package com.jumpntrap.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public abstract class Game {

    public final static int NB_COLUMNS = 5;
    public final static int NB_LINES = 7;

    private GameBoard gameBoard;
    private GameState state;

    private final int nbPlayers;
    private final List<Player> players;

    private final List<GameObserver> observers;

    private int turn;

    Game(int nbPlayers) {
        this.nbPlayers = nbPlayers;
        gameBoard = new GameBoard(NB_LINES, NB_COLUMNS);
        players = new ArrayList<>();
        observers = new ArrayList<>();
        state = GameState.INITIAL;
    }

    final void addPlayer(Player player){
        players.add(player);
        player.setGame(this);
    }

    public List<Player> getPlayers(){
        return players;
    }

    public final void addObserver(GameObserver observer){
        observers.add(observer);
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

    public final Player nextPlayer(){
        return players.get(turn);
    }

    public boolean boardContainsTile(Position pos) {
        return gameBoard.containsTile(pos);
    }

    public void start() {
        start(null, -1, null);
    }

    public void start(boolean[][] tiles,int turn,int[] positions) {

        if(players.size() < nbPlayers)
            throw new RuntimeException("The game require more players");

        if(players.size() > nbPlayers)
            throw new RuntimeException("Too many players in the game");

        initGameBoard(tiles);
        initPlayersPositions(positions);
        toss(turn);

        this.state = GameState.STARTED;

        for(GameObserver obs:observers){
            obs.onGameStarted(this);
        }

        nextPlayer().actionRequired(this);

    }

    private void initGameBoard(boolean[][] tiles) {
        gameBoard.init(tiles,nbPlayers);
    }

    private void initPlayersPositions(int[] positions) { // positions = [ x1 , y1 , x2 , y2 ... ]

        if(positions == null){
            initRandomPlayersPositions();
            return;
        }

        if(players.size() * 2 != positions.length)
            throw new RuntimeException("Invalid position set");

        for(int i = 0; i < players.size(); i ++){
            Position p = new Position(positions[i*2], positions[i*2 + 1]);
            players.get(i).setPosition(p);
        }
    }

    private void initRandomPlayersPositions() {
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

    private int toss(int turn){
        if(turn != -1) {
            this.turn = turn;
            return turn;
        }
        this.turn = (int)(Math.random() * nbPlayers);
        return this.turn;
    }

    public int getTurn() {
        return turn;
    }

    public void restart() {
        reset();
        start();
    }

    public final void reset() {
        state = GameState.INITIAL;

        for (final Player p : getPlayers()) {
            p.setPosition(null);
            p.resurrect();
        }
    }

    /**
     * A game isn't over if at least 2 players are still alive.
     */
    private void checkIsOver() {

        Player winner = null;

        for (Player player : getPlayers()) {

            if (player.isAlive()) {

                if (winner != null){
                    return;
                }
                winner = player;
            }

        }

        gameOver(winner);
    }

    final void dropTile(Position position) {
        this.gameBoard.dropTile(position);
    }

    public final GameBoard getGameBoard() {
        return gameBoard;
    }

    public final void handleMove(Direction direction, Player player){

        Log.d("Game","starting handleMove for "+ player.getClass().getName());
        if(isOver())
            return;

        if(player != nextPlayer()) { // check if it's its turn else ignore the move
            Log.d("Game","move ignored "+ player.getClass().getName());
            return;
        }

        player.playMove(this,direction);
        for (GameObserver go : observers) {
            go.onMovedPlayed(this, player, direction);
        }
        checkIsOver();
        turn = (turn + 1) % nbPlayers;

        if(!isOver()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    nextPlayer().actionRequired(Game.this);
                }
            }).start();
        }
        Log.d("Game","ending handleMove for "+ player.getClass().getName());
    }

    private void gameOver(Player winner){
        this.state = GameState.GAMEOVER;
        for(GameObserver observer:observers){
            observer.onGameOver(this,winner);
        }
    }

    private boolean isOver(){
        return this.state == GameState.GAMEOVER;
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

    public GameState getGameState() {
        return state;
    }
}
