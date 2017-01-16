package com.jumpntrap.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Game defines the engine of the game.
 */
public abstract class Game {
    /**
     * Number of columns for the board.
     */
    public final static int NB_COLUMNS = 5;

    /**
     * Number of lines for the board.
     */
    public final static int NB_LINES = 7;

    /**
     * The board of the game.
     */
    private GameBoard gameBoard;

    /**
     * The state of the game.
     */
    private GameState state;

    /**
     * The number of players.
     */
    private final int nbPlayers;

    /**
     * The list of the players.
     */
    private final List<Player> players;

    /**
     * The observers.
     */
    private final List<GameObserver> observers;

    /**
     * The turn of the player to play.
     */
    private int turn;

    /**
     * Constructor.
     * @param nbPlayers the number of players.
     */
    public Game(int nbPlayers) {
        this.nbPlayers = nbPlayers;
        gameBoard = new GameBoard(NB_LINES, NB_COLUMNS);
        players = new ArrayList<>(nbPlayers);
        observers = new ArrayList<>();
        state = GameState.INITIAL;
    }

    /**
     * Add a new player.
     * @param player the player to add.
     */
    protected final void addPlayer(Player player){
        players.add(player);
        player.setGame(this);
    }

    /**
     * Get the list of players.
     * @return the list of players.
     */
    public final List<Player> getPlayers(){
        return players;
    }

    /**
     * Add an observer.
     * @param observer the observer to add.
     */
    public final void addObserver(final GameObserver observer){
        observers.add(observer);
    }

    /**
     * Check if a tile is occupied by a player.
     * @param position the position to check.
     * @return true if a tile is occupied by a player.
     */
    public final boolean isTileOccupied(final Position position) {
        for (final Player player : getPlayers()) {
            final Position pos = player.getPosition();

            if (pos != null && pos.equals(position)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the next player to play.
     * @return the next player to player.
     */
    public final Player nextPlayer(){
        return players.get(turn);
    }

    /**
     * Check if the board contains a tile.
     * @param pos the position to check.
     * @return true if the board contains a tile.
     */
    public final boolean boardContainsTile(Position pos) {
        return gameBoard.containsTile(pos);
    }

    /**
     * Start the game.
     */
    public void start() {
        start(null, -1, null);
    }

    /**
     * Start the game with default information.
     * @param tiles the tiles of the board.
     * @param turn the turn of the player to play.
     * @param positions the position of the players.
     */
    public void start(final boolean[][] tiles, final int turn, final int[] positions) {
        if(players.size() < nbPlayers)
            throw new RuntimeException("The game require more players.");

        if(players.size() > nbPlayers)
            throw new RuntimeException("Too many players in the game.");

        initGameBoard(tiles);
        initPlayersPositions(positions);
        toss(turn);

        this.state = GameState.STARTED;

        for(final GameObserver obs:observers){
            obs.onGameStarted(this);
        }

        nextPlayer().actionRequired(this);
    }

    /**
     * Init the game board.
     * @param tiles the tiles of the board.
     */
    private void initGameBoard(boolean[][] tiles) {
        gameBoard.init(tiles,nbPlayers);
    }

    /**
     * Init the players positions.
     * @param positions the positions of the players.
     */
    private void initPlayersPositions(int[] positions) { // positions = [ x1 , y1 , x2 , y2 ... ]
        if(positions == null){
            initRandomPlayersPositions();
            return;
        }

        if(players.size() * 2 != positions.length)
            throw new RuntimeException("Invalid position set");

        for(int i = 0; i < players.size(); i ++){
            final Position p = new Position(positions[i*2], positions[i*2 + 1]);
            players.get(i).setPosition(p);
        }
    }

    /**
     * Initialize random positions for the playerd.
     */
    private void initRandomPlayersPositions() {
        final Random rand = new Random();

        // Place players with a random algorithm
        for (final Player player : getPlayers()) {
            Position pos;
            do {
                pos = new Position(rand.nextInt(NB_LINES), rand.nextInt(NB_COLUMNS));
            } while (!gameBoard.containsTile(pos) || isTileOccupied(pos));

            player.setPosition(pos);
        }
    }

    /**
     * Get a random turn.
     * @param turn the turn to set.
     * @return a random turn.
     */
    private int toss(final int turn){
        if(turn != -1) {
            this.turn = turn;
            return turn;
        }
        this.turn = (int)(Math.random() * nbPlayers);
        return this.turn;
    }

    /**
     * Get the turn.
     * @return the turn.
     */
    public final int getTurn() {
        return turn;
    }

    /**
     * Restart the game.
     */
    public final void restart() {
        reset();
        start();
    }

    /**
     * Reset the game.
     */
    public final void reset() {
        state = GameState.INITIAL;

        for (final Player p : getPlayers()) {
            p.setPosition(null);
            p.resurrect();
        }
    }

    /**
     * Check if a game is over.
     */
    private void checkIsOver() {
        // A game isn't over if at least 2 players are still alive.
        Player winner = null;

        for (final Player player : getPlayers()) {
            if (player.isAlive()) {
                if (winner != null){
                    return;
                }
                winner = player;
            }
        }

        gameOver(winner);
    }

    /**
     * Drop a tile.
     * @param position the position of the tile to drop.
     */
    final void dropTile(final Position position) {
        this.gameBoard.dropTile(position);
    }

    /**
     * Get the board of the game.
     * @return the board of the game.
     */
    public final GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Handle a move played.
     * @param direction the moved played.
     * @param player the player who plays.
     */
    public final void handleMove(final Direction direction, final Player player){
        Log.d("Game","starting handleMove for "+ player.getClass().getName());
        if(isOver())
            return;

        if(player != nextPlayer()) { // check if it's its turn else ignore the move
            Log.d("Game","move ignored "+ player.getClass().getName());
            return;
        }

        player.playMove(this,direction);
        for (final GameObserver go : observers) {
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

    /**
     * Set the game as over.
     * @param winner the winner of the game.
     */
    private void gameOver(final Player winner){
        this.state = GameState.GAME_OVER;
        for(GameObserver observer:observers){
            observer.onGameOver(this,winner);
        }
    }

    /**
     * Check if the game is over.
     * @return true if the game is over.
     */
    private boolean isOver(){
        return this.state == GameState.GAME_OVER;
    }

    /**
     * Get the String representation of the instance.
     * @return the String representation of the instance.
     */
    @Override
    public String toString() {

        final Boolean[][] board = new Boolean[NB_LINES][NB_COLUMNS];
        for (final Player player : getPlayers()) {
            final Position p = player.getPosition();
            if (p != null)
                board[p.line][p.column] = true;
        }

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < NB_LINES; i++) {
            for (int j = 0; j < NB_COLUMNS; j++) {
                final Position position = new Position(i, j);
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

    /**
     * Get the state of the game.
     * @return the state of the game.
     */
    public final GameState getGameState() {
        return state;
    }
}
