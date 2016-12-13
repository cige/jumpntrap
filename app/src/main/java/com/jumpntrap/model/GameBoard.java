package com.jumpntrap.model;

/**
 * Created by Victor on 13/12/2016.
 */

public class GameBoard {
    private Tile[][] tiles;
    private final int nbColumns;
    private final int nbLines;

    public GameBoard(int nbColumns, int nbLines) {
        this.nbColumns = nbColumns;
        this.nbLines = nbLines;

        generateTiles();
    }

    private void generateTiles() {
        tiles = new Tile[nbColumns][nbLines];

        for (int i = 0; i < nbColumns; ++i) {
            for (int j = 0; j < nbLines; ++j) {
                tiles[i][j] = new Tile(true);
            }
        }
    }

    public boolean isTileFallen(int x, int y) {
        return tiles[y][x].isFallen();
    }
}
