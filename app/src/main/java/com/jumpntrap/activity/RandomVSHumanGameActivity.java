package com.jumpntrap.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jumpntrap.model.Game;
import com.jumpntrap.model.OneVSOneGame;
import com.jumpntrap.players.HumanPlayer;
import com.jumpntrap.players.RandomPlayer;
import com.jumpntrap.view.GameView;

public class RandomVSHumanGameActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final RandomPlayer randomPlayer = new RandomPlayer();
        final HumanPlayer humanPlayer = new HumanPlayer(this);
        final Game game = new OneVSOneGame(humanPlayer,randomPlayer);

        final GameView view = new GameView(this,game);
        view.setOnTouchListener(humanPlayer);

        this.setContentView(view);

        new Thread(new Runnable() { //TODO find another way to manage the loop due to player.actionRequired
            @Override
            public void run() {
                game.start();
            }
        }).start();

    }

}
