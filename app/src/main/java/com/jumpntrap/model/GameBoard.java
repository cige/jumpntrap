package com.jumpntrap.model;

/**
 * Created by Victor on 13/12/2016.
 */

public class GameBoard {
    private Tile[][] tiles;
    private final int nbColumns;
    private final int nbLines;

    public GameBoard(int nbColumns, int nbLines, int nbPlayers) {
        this.nbColumns = nbColumns;
        this.nbLines = nbLines;

        generateTiles(nbPlayers);
    }

    private void generateTiles(int nbPlayers) {
        tiles = new Tile[nbColumns][nbLines];

        for (int i = 0; i < nbColumns; ++i) {
            for (int j = 0; j < nbLines; ++j) {
                tiles[i][j] = new Tile();
            }
        }

        // HERE WE HAVE TO CHECK IF THE BOARD IS VALID WITH nbPlayers
    }

    public boolean isTileFallen(int x, int y) {
        return tiles[y][x].isFallen();
    }
}
