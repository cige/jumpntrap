package com.jumpntrap.model.game;

import com.jumpntrap.model.Player;

/**
 * Created by Victor on 06/01/2017.
 */

public class HumanVSRemoteGame extends Game {
    private Player playerOne;
    private Player playerTwo;

    public HumanVSRemoteGame() {
        super(2);
    }

    public final void setPlayerOne(final Player player){
        playerOne = player;
        addPlayer(player);
    }

    public final void setPlayerTwo(final Player player){
        playerTwo = player;
        addPlayer(player);
    }

    @Override
    public void start() {
        super.start();
       // waitHumanMove();
    }
}
