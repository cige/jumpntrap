package com.jumpntrap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jumpntrap.model.Game;
import com.jumpntrap.model.GameBoard;
import com.jumpntrap.model.GameState;
import com.jumpntrap.model.OneVSOneGame;
import com.jumpntrap.model.Player;
import com.jumpntrap.model.Position;

// SurfaceView est une surface de dessin.
// référence : http://developer.android.com/reference/android/view/SurfaceView.html
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameLoopThread gameLoopThread;
    private Game game;
    private GameBoard board;
    private final int nbLines,nbColumns;

    // Dimensions
    private int tileLength;

    // création de la surface de dessin
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        gameLoopThread = new GameLoopThread(this);

        this.nbLines = Game.NB_LINES;
        this.nbColumns = Game.NB_COLUMNS;
    }

    // Fonction qui "dessine" un écran de jeu
    public void doDraw(Canvas canvas) {

        if(game == null || game.getGameState() == GameState.INITIAL)
            return;

        if (canvas == null) {
            return;
        }

        // on efface l'écran
        canvas.drawColor(Color.BLACK);

        this.drawBoard(canvas);
        this.drawPlayers(canvas);

    }

    private void drawBoard(Canvas canvas) { //TODO fulfill the screen

        int lineCursor = 0;
        int columnCursor;

        Paint p = new Paint();
        p.setColor(Color.GRAY);

        for(int line = 0; line < nbLines; line ++){
            columnCursor = 0;
            for(int column = 0; column < nbColumns; column ++) {
                if (board.containsTile(new Position(line, column))) {
                    Rect rect = new Rect(columnCursor, lineCursor, columnCursor + tileLength, lineCursor + tileLength);
                    canvas.drawRect(rect, p);
                }
                columnCursor += tileLength;
            }
            lineCursor += tileLength;
        }
    }

    private void drawPlayers(Canvas canvas) {

        Paint p = new Paint();

        int radius = tileLength / 2;

        for(Player player: game.getPlayers()){

            if(player.isMainPlayer())
                p.setColor(Color.BLUE);
            else
                p.setColor(Color.RED);

            if(player.isAlive()) {
                canvas.drawCircle(tileLength*player.getPosition().getColumn()+ radius,tileLength*player.getPosition().getLine()+ radius, radius,p);
            }
        }
    }

    // Fonction obligatoire de l'objet SurfaceView
    // Fonction appelée immédiatement après la création de l'objet SurfaceView
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // création du processus GameLoopThread si cela n'est pas fait
        if (gameLoopThread.getState() == Thread.State.TERMINATED) {
            gameLoopThread = new GameLoopThread(this);
        }
        gameLoopThread.setRunning(true);
        gameLoopThread.start();
    }

    // Fonction obligatoire de l'objet SurfaceView
    // Fonction appelée juste avant que l'objet ne soit détruit.
    // on tente ici de stopper le processus de gameLoopThread
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        gameLoopThread.setRunning(false);
        while (retry) {
            try {
                gameLoopThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    // Fonction obligatoire de l'objet SurfaceView
    // Fonction appelée à la CREATION et MODIFICATION et ONRESUME de l'écran
    // nous obtenons ici la largeur/hauteur de l'écran en pixels
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int w, int h) {
        this.resize(w,h);
    }

    private void resize(int w, int h) {
        int maxScreen = w > h ? w : h;
        int maxBoard = nbLines > nbColumns ? nbLines : nbColumns;
        this.tileLength =  maxScreen / maxBoard;
    }

    public void setGame(OneVSOneGame game) {
        this.game = game;
        this.board = game.getGameBoard();
    }
}// class GameView