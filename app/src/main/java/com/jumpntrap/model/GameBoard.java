package com.jumpntrap.model;

/**
 * GameBoard defines the board of the game.
 */
public final class GameBoard {
    /**
     * The density to generate a board.
     */
    private final static double DENSITY = 0.85;

    /**
     * The tiles.
     */
    private boolean[][] tiles;

    /**
     * The number of lines.
     */
    private final int nbLines;

    /**
     * The number of columns.
     */
    private final int nbColumns;

    /**
     * Constructor.
     * @param nbLines the number of lines.
     * @param nbColumns the number of columns.
     */
    GameBoard(final int nbLines, final int nbColumns) {
        this.nbLines = nbLines;
        this.nbColumns = nbColumns;
        tiles = new boolean[nbLines][nbColumns];
    }

    /**
     * Generate a random board.
     * @param nbPlayers the number of players to generate a valid board.
     * @param density the density to generate a random board.
     */
    private void generateTiles(final int nbPlayers, final double density) {
        for (int line = 0; line < nbLines; ++line) {
            for (int column = 0; column < nbColumns; ++column) {
                tiles[line][column] = Math.random() < density; //TODO generate a playable board, no isolated tile for example, no disadvantaged player ...
            }
        }

        //TODO HERE WE HAVE TO CHECK IF THE BOARD IS VALID WITH nbPlayers
    }

    /**
     * Check if the board contains a tile.
     * @param position the position to check.
     * @return true if the board contains a tile.
     */
    public final boolean containsTile(Position position) {
       return containsTile(position.line,position.column);
    }

    /**
     * Check if the board contains a tile.
     * @param line the line position to check.
     * @param column the column position to check.
     * @return true if the board contains a tile.
     */
    private boolean containsTile(int line, int column) {
        return !(line < 0 || column < 0 || line >= nbLines || column >= nbColumns) && tiles[line][column];
    }

    /**
     * Drop a tile.
     * @param position the position of the tile to drop.
     */
    void dropTile(final Position position){
        this.dropTile(position.line,position.column);
    }

    /**
     * Drop a tile.
     * @param line the line position to check.
     * @param column the column position to check.
     */
    private void dropTile(int line, int column) {
        if(line < 0 || column < 0 || line >= nbLines || column >= nbColumns)
            return;
        tiles[line][column] = false;
    }

    /**
     * Initialize the board.
     * @param nbPlayers the number of players.
     */
    private void init(final int nbPlayers) {
        generateTiles(nbPlayers,DENSITY);
    }

    /**
     * Initialize the board with a given board.
     * @param set the state of board.
     * @param nbPlayers the number of players.
     */
    void init(final boolean[][] set, final int nbPlayers){

        if(set == null){
            init(nbPlayers);
            return;
        }

        if(set.length != nbLines)
            throw new RuntimeException("Size of tile set does not match with the gameboard");

        for(int line = 0; line < nbLines; line ++){
            if(set[line].length != nbColumns)
                throw new RuntimeException("Size of tile set does not match with the gameboard");

            System.arraycopy(set[line], 0, tiles[line], 0, nbColumns);
        }
    }

    /**
     * Clone the tiles.
     * @return the tiles.
     */
    public final boolean[][] serialize(){
        return tiles.clone();
    }
}
