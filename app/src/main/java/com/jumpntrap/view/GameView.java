package com.jumpntrap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jumpntrap.R;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.GameBoard;
import com.jumpntrap.games.OneVSOneGame;
import com.jumpntrap.model.Player;
import com.jumpntrap.model.Position;

// SurfaceView est une surface de dessin.
// référence : http://developer.android.com/reference/android/view/SurfaceView.html
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameLoopThread gameLoopThread;
    private OneVSOneGame game;
    private GameBoard board;
    private final int nbLines,nbColumns;

    // Dimensions
    private int tileLength;
    private final int[] heightReminder;
    private final int[] weightReminder;

    // création de la surface de dessin
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        gameLoopThread = new GameLoopThread(this);

        this.nbLines = Game.NB_LINES;
        this.nbColumns = Game.NB_COLUMNS;

        this.heightReminder = new int[nbLines];
        this.weightReminder = new int[nbColumns];
    }

    // Fonction qui "dessine" un écran de jeu
    public void doDraw(Canvas canvas) {

        if(game == null)
            return;

        if (canvas == null) {
            return;
        }

        // on efface l'écran
        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.emptyColor));

        final Paint p = new Paint();
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.drawBoard(canvas,p);
        this.drawPlayers(canvas,p);

    }

    private void drawBoard(Canvas canvas,Paint paint) {

        int lineCursor = 0;
        int columnCursor;

        paint.setColor(ContextCompat.getColor(getContext(), R.color.tileColor));

        for(int line = 0; line < nbLines; line ++){
            columnCursor = 0;
            for(int column = 0; column < nbColumns; column ++) {
                if (board.containsTile(new Position(line, column))) {
                    Rect rect = new Rect(columnCursor, lineCursor, columnCursor + tileLength + weightReminder[column], lineCursor + tileLength + heightReminder[line]);
                    canvas.drawRect(rect, paint);
                }
                columnCursor = columnCursor + tileLength + weightReminder[column];
            }
            lineCursor = lineCursor + tileLength + heightReminder[line];
        }
    }

    private void drawPlayers(Canvas canvas,Paint paint) {

        int radius = tileLength / 2;

        for(Player player: game.getPlayers()){

            Position pos = player.getPosition();

            if(pos == null || !pos.isLegalPosition(nbLines,nbColumns))
                continue;

            if(!player.isAlive())
                paint.setColor(ContextCompat.getColor(getContext(), R.color.deathColor));
            else if(game.isFirstPlayer(player))
                paint.setColor(ContextCompat.getColor(getContext(), R.color.bottomPlayerColor));
            else if(game.isSecondPlayer(player))
                paint.setColor(ContextCompat.getColor(getContext(), R.color.topPlayerColor));
            else
                continue;

            int hreminders = 0;
            int wreminders = 0;
            for(int line = 0; line < pos.getLine(); line ++){
                hreminders += heightReminder[line];
            }
            for(int column = 0; column < pos.getColumn(); column ++){
                wreminders += weightReminder[column];
            }
            canvas.drawCircle(tileLength*pos.getColumn() + wreminders + radius + (weightReminder[pos.getColumn()] / 2),
                    tileLength*pos.getLine() + hreminders + radius + (heightReminder[pos.getLine()] / 2),
                    radius,paint);
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
                e.printStackTrace();
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
        for(int i = 0; i < nbLines; i ++)
            heightReminder[i] = 0;
        for(int i = 0; i < nbColumns; i ++)
            weightReminder[i] = 0;

        this.tileLength =  Math.min(h / nbLines, w / nbColumns);
        int hReminder = h % tileLength;
        int wReminder = w % tileLength;
        int i = 0;
        while(hReminder > 0){
            heightReminder[i] ++;
            hReminder --;
            i = (i + 1) % nbLines;
        }
        i = 0;
        while(wReminder > 0){
            weightReminder[i] ++;
            wReminder --;
            i = (i + 1) % nbColumns;
        }
    }

    public void setGame(OneVSOneGame game) {
        this.game = game;
        this.board = game.getGameBoard();
    }
}// class GameView