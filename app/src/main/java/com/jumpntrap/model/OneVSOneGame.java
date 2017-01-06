package com.jumpntrap.model;

/**
 * Created by clementgeorge on 06/01/17.
 */

public final class OneVSOneGame extends Game {

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
    }
    @Override
    public final Player checkIsOver() {
        Player winner = super.checkIsOver();
        if(winner == null)
            return null;
        if(winner == player1)
            player1Score ++;
        else if(winner == player2)
            player2Score ++;
        return winner;
    }
}
