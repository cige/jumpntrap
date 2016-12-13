package com.jumpntrap.model;

/**
 * Created by Victor on 13/12/2016.
 */

public class Tile {
    private boolean isFallen;

    public Tile(boolean isFallen) {
        this.isFallen = isFallen;
    }

    public void drop() {
        isFallen = true;
    }

    public boolean isFallen() {
        return isFallen;
    }
}
