package com.jumpntrap.model;

/**
 * Created by clementgeorge on 05/01/17.
 */

public final class HumanVSComputerGame extends OneVSOneGame {

    private Player human;
    private Player computer;

    public HumanVSComputerGame(Player human,Player computer) {
        super(human,computer);
    }

    @Override
    public void start() {
        super.start();
        waitHumanMove();
    }

    @Override
    public void handleHumanMove(Direction direction, Player player) {

        if(!isWaitingHumanMove())
            return;

        if(player != human)
            return;

        super.handleMove(direction,player);

        this.waitIAMove();

        computer.actionRequired(this);
    }

    @Override
    public void handleIAMove(Direction direction, Player player) {

        if(!isWaitingIAMove())
            return;

        if(player != computer)
            return;

        super.handleMove(direction,player);

        waitHumanMove();
        return;
    }
}
