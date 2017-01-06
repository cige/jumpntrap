package com.jumpntrap.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jumpntrap.players.HumanPlayer;
import com.jumpntrap.players.RandomPlayer;
import com.jumpntrap.model.game.HumanVSComputerGame;
import com.jumpntrap.view.GameView;

public class GameActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final HumanVSComputerGame game = new HumanVSComputerGame();

        final RandomPlayer randomPlayer = new RandomPlayer(game);
        final HumanPlayer humanPlayer = new HumanPlayer(this,game);

        game.setHumanPlayer(humanPlayer);
        game.setComputerPlayer(randomPlayer);

        final GameView view = new GameView(this,game);
        view.setOnTouchListener(humanPlayer);

        this.setContentView(view);

        game.start();
    }




}
