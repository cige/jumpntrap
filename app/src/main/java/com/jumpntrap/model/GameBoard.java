package com.jumpntrap.model;

/**
 * Created by Victor on 13/12/2016.
 */

public class GameBoard {
    private Tile[][] tiles;
    private final int nbColumns;
    private final int nbLines;

    public GameBoard(int nbLines,int nbColumns, int nbPlayers) {

        this.nbLines = nbLines;
        this.nbColumns = nbColumns;

        generateTiles(nbPlayers);
    }

    private void generateTiles(int nbPlayers) {
        tiles = new Tile[nbLines][nbColumns];

        for (int line = 0; line < nbLines; ++line) {
            for (int column = 0; column < nbColumns; ++column) {
                tiles[line][column] = new Tile();
            }
        }

        // HERE WE HAVE TO CHECK IF THE BOARD IS VALID WITH nbPlayers
    }

    public boolean containsTile(Position pos) {
        return pos.line >= 0 && pos.line < nbLines &&
               pos.column >= 0 && pos.column < nbColumns &&
               !tiles[pos.line][pos.column].isFallen();
    }
}
