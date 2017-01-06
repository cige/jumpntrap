package com.jumpntrap.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jumpntrap.model.Game;
import com.jumpntrap.model.OneVSOneGame;
import com.jumpntrap.players.HumanPlayer;
import com.jumpntrap.players.RandomPlayer;
import com.jumpntrap.view.GameView;

public class GameActivity extends AppCompatActivity{

    public final static String GAME_TYPE = "GAME_TYPE";

    public final static int HUMAN_VS_COMPUTER = 0;
    public final static int HUMAN_VS_REMOTE = 1;
    public final static int COMPUTER_VS_COMPUTER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int gameType = getIntent().getIntExtra(GAME_TYPE,COMPUTER_VS_COMPUTER);

        final Game game = createGame(gameType);

        final GameView view = new GameView(this,game);
        this.setContentView(view);

        new Thread(new Runnable() {
            @Override
            public void run() {
                game.start();
            }
        }).start();

    }

    private Game createGame(int gameType) {

        if(gameType == HUMAN_VS_COMPUTER){
            final RandomPlayer randomPlayer = new RandomPlayer();
            final HumanPlayer humanPlayer = new HumanPlayer(this);
            return new OneVSOneGame(humanPlayer,randomPlayer);
            //TODO set the humanPlayer as a listener
        }

        if(gameType == HUMAN_VS_REMOTE){
            return null;
        }

        if(gameType == COMPUTER_VS_COMPUTER){
            final RandomPlayer j1 = new RandomPlayer();
            final RandomPlayer j2 = new RandomPlayer();
            return new OneVSOneGame(j1,j2);
        }

        throw new RuntimeException("This game type is not supported");

    }


}
