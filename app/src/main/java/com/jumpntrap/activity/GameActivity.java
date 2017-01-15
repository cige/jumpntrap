package com.jumpntrap.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jumpntrap.R;
import com.jumpntrap.model.Direction;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.GameObserver;
import com.jumpntrap.model.OneVSOneGame;
import com.jumpntrap.model.Player;
import com.jumpntrap.players.HumanPlayer;
import com.jumpntrap.view.GameView;

public abstract class GameActivity extends AppCompatActivity implements GameObserver {

    protected OneVSOneGame game;
    private GameView view;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //removing the action bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (getActionBar() != null) {
            getActionBar().hide();
        }

        this.setContentView(R.layout.activity_game);

        view = new GameView(this);
        LinearLayout boardLayout = (LinearLayout)findViewById(R.id.board);
        boardLayout.addView(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
    }

    void setGame(OneVSOneGame game){
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

    public final void setOnTouchListener(HumanPlayer humanPlayer) {
        this.findViewById(R.id.board).setOnTouchListener(humanPlayer);
    }

    @Override
    public void onGameOver(final Game game,final Player winner) {
        if (this.game != game) {
            return;
        }

        final int userScore = this.game.getUserScore();
        final int opponentScore = this.game.getOpponentScore();

        final TextView scoreBottom = (TextView) findViewById(R.id.score_bottom);
        final TextView scoreTop = (TextView) findViewById(R.id.score_top);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreBottom.setText(String.valueOf(userScore));
                scoreTop.setText(String.valueOf(opponentScore));

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
        });
    }

    @Override
    public void onGameStarted(final Game game) {

        if(game != this.game)
            return;

        final OneVSOneGame g = this.game;

        final LinearLayout topBar = (LinearLayout) findViewById(R.id.topBar);
        final LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottomBar);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(g.isUserPlayer(g.nextPlayer())){
                    bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.bottomPlayerColor));
                    topBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.topPlayerWaitingColor));
                }
                else{
                    bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.bottomPlayerWaitingColor));
                    topBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.topPlayerColor));
                }
            }
        });

    }

    @Override
    public void onMovedPlayed(Game game,final Player player, Direction move){
        if(this.game != game)
            return;

        final OneVSOneGame g = this.game;

        final LinearLayout topBar = (LinearLayout) findViewById(R.id.topBar);
        final LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottomBar);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

        if(g.isUserPlayer(player)){
            bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.bottomPlayerWaitingColor));
            topBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.topPlayerColor));
        }
        else{
            bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.bottomPlayerColor));
            topBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.topPlayerWaitingColor));
        }}});
    }
}
