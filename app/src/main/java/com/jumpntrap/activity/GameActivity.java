package com.jumpntrap.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jumpntrap.R;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.RandomPlayer;

import java.util.Observable;
import java.util.Observer;

public class GameActivity extends AppCompatActivity implements Observer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Game game = new Game(new RandomPlayer(), new RandomPlayer());
        game.addObserver(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                game.start();
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        TextView tv = (TextView) this.findViewById(R.id.mainView);
        tv.setText((String)o);
    }
}
