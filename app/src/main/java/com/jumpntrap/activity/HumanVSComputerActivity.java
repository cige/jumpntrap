package com.jumpntrap.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.jumpntrap.model.Game;
import com.jumpntrap.model.OneVSOneGame;
import com.jumpntrap.players.HumanPlayer;
import com.jumpntrap.players.RandomPlayer;
import com.jumpntrap.view.GameView;

public class HumanVSComputerActivity extends GameActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final RandomPlayer randomPlayer = new RandomPlayer();
        final HumanPlayer humanPlayer = new HumanPlayer(this);
        final OneVSOneGame game = new OneVSOneGame(humanPlayer,randomPlayer);

        super.setGame(game);
        super.setOnTouchListener(humanPlayer);
        startGame();

    }

}
