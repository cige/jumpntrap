package com.jumpntrap.model;

public final class Position {

    public final int line;
    public final int column;

    public Position(final int line, final int column) {
        this.line = line;
        this.column = column;
    }

    public Position add(final Position pos) {
        return new Position(line + pos.line, column + pos.column);
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    public boolean isLegalPosition(final int nbLines, final int nbColumns){
        return line > -1 && column > -1 && line < nbLines && column < nbColumns;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Position))
            return false;

        final Position p = (Position) obj;
        return p.line == this.line && p.column == this.column;
    }
}
