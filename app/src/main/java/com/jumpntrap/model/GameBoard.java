package com.jumpntrap.model;

import java.io.Serializable;

/**
 * Created by Victor on 13/12/2016.
 */

public class GameBoard implements Serializable{

    private final static double DENSITY = 0.85;
    private boolean[][] tiles;
    private final int nbLines;
    private final int nbColumns;

    public GameBoard(int nbLines,int nbColumns, int nbPlayers) {

        this.nbLines = nbLines;
        this.nbColumns = nbColumns;
        tiles = new boolean[nbLines][nbColumns];

    }

    private void generateTiles(int nbPlayers,double density) {

        for (int line = 0; line < nbLines; ++line) {
            for (int column = 0; column < nbColumns; ++column) {
                if(Math.random() < density) //TODO generate a playable board, No isolated tile for example, no disvantaged player
                    tiles[line][column] = true;
                else
                    tiles[line][column] = false;
            }
        }

        //TODO HERE WE HAVE TO CHECK IF THE BOARD IS VALID WITH nbPlayers
    }

    public boolean containsTile(Position position) {
       return containsTile(position.line,position.column);
    }

    private boolean containsTile(int line, int column) {
        if(line < 0 || column < 0 || line >= nbLines || column >= nbColumns)
            return false;
        return tiles[line][column];
    }

    public void dropTile(Position position){
        this.dropTile(position.line,position.column);
    }

    private void dropTile(int line, int column) {
        if(line < 0 || column < 0 || line >= nbLines || column >= nbColumns)
            return;
        tiles[line][column] = false;
    }

    void init(int nbPlayers) {
        generateTiles(nbPlayers,DENSITY);
    }

    void init(boolean[][] set,int nbPlayers){

        if(set == null){
            init(nbPlayers);
            return;
        }

        if(set.length != nbLines)
            throw new RuntimeException("Size of tile set does not match with the gameboard");

        for(int line = 0; line < nbLines; line ++){

            if(set[line].length != nbColumns)
                throw new RuntimeException("Size of tile set does not match with the gameboard");

            for(int column = 0; column < nbColumns; column ++)
                tiles[line][column] = set[line][column];

        }
    }

    boolean[][] serialize(){
        return tiles.clone();
    }


}
