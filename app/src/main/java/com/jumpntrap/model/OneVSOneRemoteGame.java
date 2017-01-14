package com.jumpntrap.model;


public final class OneVSOneRemoteGame extends OneVSOneGame implements GameObserver {

    private boolean isHost;

    public OneVSOneRemoteGame(final Player player1, final Player player2) {
        this(player1, player2, true);
    }

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
    public final int getUserScore(){
        return isHost ? player1Score : player2Score;
    }

    @Override
    public final int getOpponentScore(){
        return isHost ? player2Score : player1Score;
    }

    @Override
    public final boolean isUserPlayer(Player p){
        return isHost ? p == player1 : p == player2;
    }

}
