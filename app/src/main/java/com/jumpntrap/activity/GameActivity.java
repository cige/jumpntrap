package com.jumpntrap.activity;

import android.os.Bundle;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.TextView;

import com.jumpntrap.R;
import com.jumpntrap.controller.HumanPlayerController;
import com.jumpntrap.model.AbstractPlayer;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.HumanPlayer;
import com.jumpntrap.model.RandomPlayer;
import com.jumpntrap.view.GameView;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class GameActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final RandomPlayer randomPlayer = new RandomPlayer();
        final HumanPlayer humanPlayer = new HumanPlayer();

        final Game game = new Game(humanPlayer, randomPlayer);
        final GameView view = new GameView(this,game);
        view.setOnTouchListener(new HumanPlayerController(this,humanPlayer));

        this.setContentView(view);

        new Thread(new Runnable() {
            @Override
            public void run() {
                game.start();
            }
        }).start();
    }




}
