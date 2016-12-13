package com.jumpntrap.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

        final Game game = new Game(new RandomPlayer(), new RandomPlayer());
        game.addObserver(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                game.start();
            }
        }).start();
    }

    @Override
    public void update(final Observable observable, Object o) {

        if(! (observable instanceof Game))
            return;

        Log.d("bla","observer");

        final TextView tv = (TextView) this.findViewById(R.id.mainView);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(observable.toString());
            }
        });
    }


}
