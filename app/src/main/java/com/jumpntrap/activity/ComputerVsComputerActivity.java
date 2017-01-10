package com.jumpntrap.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.jumpntrap.model.Game;
import com.jumpntrap.model.OneVSOneGame;
import com.jumpntrap.players.HumanPlayer;
import com.jumpntrap.players.RandomPlayer;
import com.jumpntrap.view.GameView;

public class ComputerVsComputerActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final RandomPlayer randomPlayer1 = new RandomPlayer();
        final RandomPlayer randomPlayer2 = new RandomPlayer();
        final OneVSOneGame game = new OneVSOneGame(randomPlayer1,randomPlayer2);

        this.setGame(game);
        startGame();
    }
}
