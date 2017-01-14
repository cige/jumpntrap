package com.jumpntrap.model;


public class OneVSOneGame extends Game implements GameObserver {
    Player player1;
    Player player2;

    int player1Score = 0;
    int player2Score = 0;

    public OneVSOneGame(Player player1,Player player2) {
        super(2);
        this.player1 = player1;
        this.player2 = player2;
        addPlayer(this.player1);
        addPlayer(this.player2);
        this.addObserver(this);
    }

    public int getUserScore(){
        return player1Score;
    }

    public int getOpponentScore(){
        return player2Score;
    }

    public boolean isUserPlayer(Player p){
        return p == player2;
    }

    @Override
    public void onGameStarted(Game game) {
    }

    @Override
    public void onMovedPlayed(Game game, Player player, Direction move) {
    }

    @Override
    public void onGameOver(Game game,Player winner) {
        if(winner == player1)
            player1Score ++;
        else if(winner == player2)
            player2Score ++;
    }
}
