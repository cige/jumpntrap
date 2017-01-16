package com.jumpntrap.activity;

import android.os.Bundle;

import com.jumpntrap.games.OneVSOneGame;
import com.jumpntrap.players.HumanPlayer;
import com.jumpntrap.players.RandomPlayer;

public final class HumanVSComputerActivity extends GameActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final RandomPlayer randomPlayer = new RandomPlayer();
        final HumanPlayer humanPlayer = new HumanPlayer(this);
        final OneVSOneGame game = new OneVSOneGame(humanPlayer,randomPlayer);

        super.setGame(game);
        super.setOnTouchListener(humanPlayer);
        startGame();
    }

}
