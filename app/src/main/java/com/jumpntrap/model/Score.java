package com.jumpntrap.model;

/**
 * Created by clementgeorge on 06/01/17.
 */

public class Score {

    private int player1Score;
    private int player2Score;

    public Score(){
        player1Score = 0;
        player2Score = 0;
    }

    public void incrementP1Score() {
        player1Score ++;
    }

    public void incrementP2Score() {
        player2Score ++;
    }
}
