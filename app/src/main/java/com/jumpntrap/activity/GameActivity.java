package com.jumpntrap.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jumpntrap.R;
import com.jumpntrap.dialog.RematchLocalDialog;
import com.jumpntrap.games.OneVSOneGame;
import com.jumpntrap.model.Direction;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.GameObserver;
import com.jumpntrap.model.Player;
import com.jumpntrap.players.HumanPlayer;
import com.jumpntrap.view.GameView;

public abstract class GameActivity extends AppCompatActivity implements GameObserver {

    protected OneVSOneGame game;
    private GameView view;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //removing the action bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        this.setContentView(R.layout.activity_game);

        view = new GameView(this);
        LinearLayout boardLayout = (LinearLayout)findViewById(R.id.board);
        boardLayout.addView(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
    }

    void setGame(final OneVSOneGame game){
        this.game = game;
        game.addObserver(this);
        view.setGame(game);
    }

    void startGame(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                game.start();
            }
        }).start();
    }

    public final void setOnTouchListener(final HumanPlayer humanPlayer) {
        this.findViewById(R.id.board).setOnTouchListener(humanPlayer);
    }

    @Override
    public void onGameOver(final Game game,final Player winner) {
        if (this.game != game) {
            return;
        }

        final int userScore = this.game.getFirstPlayerScore();
        final int opponentScore = this.game.getSecondPlayerScore();

        final TextView scoreBottom = (TextView) findViewById(R.id.score_bottom);
        final TextView scoreTop = (TextView) findViewById(R.id.score_top);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreBottom.setText(String.valueOf(userScore));
                scoreTop.setText(String.valueOf(opponentScore));

                new RematchLocalDialog(GameActivity.this, (OneVSOneGame) game).show();
            }
        });
    }

    @Override
    public void onGameStarted(final Game game) {

        if(game != this.game)
            return;

        final OneVSOneGame g = this.game;
        final Player nextPlayer = this.game.nextPlayer();

        final LinearLayout topBar = (LinearLayout) findViewById(R.id.topBar);
        final LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottomBar);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(g.isFirstPlayer(nextPlayer)){
                    bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.bottomPlayerColor));
                    topBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.topPlayerWaitingColor));
                }
                else if(g.isSecondPlayer(nextPlayer)){
                    bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.bottomPlayerWaitingColor));
                    topBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.topPlayerColor));
                }
            }
        });

    }

    @Override
    public void onMovedPlayed(final Game game, final Player player, final Direction move){
        if(this.game != game)
            return;

        final OneVSOneGame g = this.game;

        final LinearLayout topBar = (LinearLayout) findViewById(R.id.topBar);
        final LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottomBar);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

        if(g.isFirstPlayer(player)){
            bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.bottomPlayerWaitingColor));
            topBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.topPlayerColor));
        }
        else if(g.isSecondPlayer(player)){
            bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.bottomPlayerColor));
            topBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.topPlayerWaitingColor));
        }}});
    }

}
