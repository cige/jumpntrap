package com.jumpntrap.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jumpntrap.R;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.GameObserver;
import com.jumpntrap.model.OneVSOneGame;
import com.jumpntrap.model.Player;
import com.jumpntrap.players.HumanPlayer;
import com.jumpntrap.view.GameView;

public abstract class GameActivity extends AppCompatActivity implements GameObserver {

    private OneVSOneGame game;
    private GameView view;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //removing the action bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        this.setContentView(R.layout.game);

        view = new GameView(this,game);
        LinearLayout boardLayout = (LinearLayout)findViewById(R.id.board);
        boardLayout.addView(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);

    }

    void setGame(OneVSOneGame game){
        this.game = game;
        game.addObserver(this);
        view.setGame(game);
    }

    void startGame(){
        new Thread(new Runnable() { //TODO find another way to manage the loop due to player.actionRequired
            @Override
            public void run() {
                game.start();
            }
        }).start();
    }

    public final void setOnTouchListener(HumanPlayer humanPlayer) {
        this.findViewById(R.id.board).setOnTouchListener(humanPlayer);
    }

    @Override
    public void onGameOver(final Game game,final Player winner) {

        if(this.game != game)
            return;

        TextView score1 = (TextView) findViewById(R.id.score_bottom);
        TextView score2 = (TextView) findViewById(R.id.score_top);

        score1.setText(Integer.toString(this.game.getPlayer1Score()));
        score2.setText(Integer.toString(this.game.getPlayer2Score()));

        AlertDialog dialog = new AlertDialog.Builder(GameActivity.this).create();
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Rematch",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        game.restart();
                    }
                });

        dialog.show();

    }

    @Override
    public void onGameStarted(Game game) {

    }
}
