package com.jumpntrap.model;

/**
 * Created by clementgeorge on 06/01/17.
 */

public final class OneVSOneGame extends Game implements GameObserver{

    private Player player1;
    private Player player2;

    private int player1Score = 0;
    private int player2Score = 0;

    public OneVSOneGame(Player player1,Player player2) {
        super(2);
        this.player1 = player1;
        this.player2 = player2;
        addPlayer(this.player1);
        addPlayer(this.player2);
        this.addObserver(this);
    }

    public final int getPlayer1Score(){
        return player1Score;
    }

    public final int getPlayer2Score(){
        return player2Score;
    }

    @Override
    public void onGameStarted(Game game) {

    }

    @Override
    public void onGameOver(Game game,Player winner) {
        if(winner == player1)
            player1Score ++;
        else if(winner == player2)
            player2Score ++;
    }
}
