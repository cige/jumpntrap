package com.jumpntrap.model.game;

import com.jumpntrap.model.Player;
import com.jumpntrap.model.Direction;

/**
 * Created by clementgeorge on 05/01/17.
 */

public final class HumanVSComputerGame extends Game {

    private Player human;
    private Player computer;

    public HumanVSComputerGame() {
        super(2);
    }

    public final void setHumanPlayer(Player player){
        this.human = player;
        addPlayer(player);
    }

    public final void setComputerPlayer(Player player){
        this.computer = player;
        addPlayer(player);
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

        player.playMove(this,direction);
        checkIsOver();
        this.waitIAMove();

        computer.actionRequired();
    }

    @Override
    public void handleIAMove(Direction direction, Player player) {

        if(!isWaitingIAMove())
            return;

        if(player != computer)
            return;

        player.playMove(this,direction);
        checkIsOver();

        waitHumanMove();
        return;
    }
}
