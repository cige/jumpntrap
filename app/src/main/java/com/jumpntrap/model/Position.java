package com.jumpntrap.model;

/**
 * Position defines a position.
 */
public final class Position {
    /**
     * The line.
     */
    public final int line;

    /**
     * The column.
     */
    public final int column;

    /**
     * Constructor.
     * @param line the line.
     * @param column the column.
     */
    public Position(final int line, final int column) {
        this.line = line;
        this.column = column;
    }

    /**
     * Get the sum with another position.
     * @param pos the other position to add.
     * @return the sum with the other position.
     */
    public Position add(final Position pos) {
        return new Position(line + pos.line, column + pos.column);
    }

    /**
     * Get the column.
     * @return the column.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Get the line.
     * @return the line.
     */
    public int getLine() {
        return line;
    }

    /**
     * Check if a move is legal.
     * @param nbLines the number of lines.
     * @param nbColumns the number of columns.
     * @return true if the move is legal.
     */
    public boolean isLegalPosition(final int nbLines, final int nbColumns){
        return line > -1 && column > -1 && line < nbLines && column < nbColumns;
    }

    /**
     * Check if the positions are the same.
     * @param obj the other position.
     * @return true if the positions are the same.
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Position))
            return false;

        final Position p = (Position) obj;
        return p.line == this.line && p.column == this.column;
    }
}
