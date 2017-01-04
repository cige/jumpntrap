package com.jumpntrap.model;

/**
 * Created by Victor on 13/12/2016.
 */

public class HumanPlayer extends AbstractPlayer {

    private Direction direction;

    public HumanPlayer() {
        super();
    }

    @Override
    public Direction chooseMove(Game game) {
        try{
            direction = null;
            synchronized (this) {
                while (direction == null)
                    this.wait(); // waiting a direction
            }

        return direction;
        }
        catch (InterruptedException e) { return null; }  // could mean thinking time excedeed

    }

    public void setDirection(Direction direction){
        this.direction = direction;
    }
}
