package com.jumpntrap.model;

import java.util.ArrayList;

/**
 * Created by Victor on 13/12/2016.
 */

public class Position {
    int line;
    int column;

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public Position(Position pos) {
        this(pos.line, pos.column);
    }

    public void add(Position pos) {
        line += pos.line;
        column += pos.column;
    }
}
