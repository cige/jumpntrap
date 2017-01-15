package com.jumpntrap.games;


import com.jumpntrap.model.GameObserver;
import com.jumpntrap.model.Player;

public final class OneVSOneRemoteGame extends OneVSOneGame implements GameObserver {

    private boolean isHost;

    /**
     * @param host : true if the game is hosted on the user device, false if hosted on a remote device
     */
    public OneVSOneRemoteGame(final Player player1, final Player player2, final boolean host) {
        super(player1, player2);
        this.isHost = host;
    }

    @Override
    public final void start() {
        if (isHost) {
            start(null, -1, null);
        }
    }

    @Override
    public final int getFirstPlayerScore(){
        return isHost ? player1Score : player2Score;
    }

    @Override
    public final int getSecondPlayerScore(){
        return isHost ? player2Score : player1Score;
    }

    @Override
    public final boolean isFirstPlayer(Player p){
        return isHost ? p == player1 : p == player2;
    }

    @Override
    public final boolean isSecondPlayer(Player p){
        return isHost ? p == player1 : p == player1;
    }

}
