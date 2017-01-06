package com.jumpntrap.model;

/**
 * Created by clementgeorge on 06/01/17.
 */

public final class OneVSOneGame extends Game {

    private Player p1;
    private Player p2;
    private Match match;

    public OneVSOneGame(Player player1,Player player2) {
        super(2);
        this.p1 = player1;
        this.p2 = player2;
        addPlayer(p1);
        addPlayer(p2);
        match = null;
    }

    public void setMatch(Match match){
        this.match = match;
    }

    @Override
    public final Player checkIsOver() {
        Player winner = super.checkIsOver();
        if(winner == null || match == null)
            return null;
        if(winner == p1)
            match.incrementP1Score();
        else if(winner == p2)
            match.incrementP2Score();
        return winner;
    }
}
