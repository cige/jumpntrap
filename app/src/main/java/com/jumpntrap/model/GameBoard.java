package com.jumpntrap.model;

public final class GameBoard {

    private final static double DENSITY = 0.85;
    private boolean[][] tiles;
    private final int nbLines;
    private final int nbColumns;

    GameBoard(final int nbLines, final int nbColumns) {
        this.nbLines = nbLines;
        this.nbColumns = nbColumns;
        tiles = new boolean[nbLines][nbColumns];
    }

    private void generateTiles(final int nbPlayers, final double density) {
        for (int line = 0; line < nbLines; ++line) {
            for (int column = 0; column < nbColumns; ++column) {
                tiles[line][column] = Math.random() < density; //TODO generate a playable board, no isolated tile for example, no disadvantaged player ...
            }
        }

        //TODO HERE WE HAVE TO CHECK IF THE BOARD IS VALID WITH nbPlayers
    }

    public final boolean containsTile(Position position) {
       return containsTile(position.line,position.column);
    }

    private boolean containsTile(int line, int column) {
        return !(line < 0 || column < 0 || line >= nbLines || column >= nbColumns) && tiles[line][column];
    }

    void dropTile(final Position position){
        this.dropTile(position.line,position.column);
    }

    private void dropTile(int line, int column) {
        if(line < 0 || column < 0 || line >= nbLines || column >= nbColumns)
            return;
        tiles[line][column] = false;
    }

    private void init(final int nbPlayers) {
        generateTiles(nbPlayers,DENSITY);
    }

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

    public final boolean[][] serialize(){
        return tiles.clone();
    }

}
