package com.jumpntrap.model;

import java.util.ArrayList;

/**
 * Created by Victor on 13/12/2016.
 */

public final class Position {
    public final int line;
    public final int column;

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public Position add(Position pos) {
        return new Position(line + pos.line, column + pos.column);
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Position))
            return false;
        Position p = (Position) obj;
        return p.line == this.line && p.column == this.column;
    }
}
