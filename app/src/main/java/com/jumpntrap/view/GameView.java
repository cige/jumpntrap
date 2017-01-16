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

/**
 * SurfaceView defines a drawing area.
 * Reference : http://developer.android.com/reference/android/view/SurfaceView.html
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    /**
     * The game loop thread.
     */
    private GameLoopThread gameLoopThread;

    /**
     * The game.
     */
    private OneVSOneGame game;

    /**
     * The board of the game.
     */
    private GameBoard board;

    /**
     * The number of lines of the board.
     */
    private final int nbLines;

    /**
     * The number of columns of the board.
     */
    private final int nbColumns;

    /**
     * The length of the tile.
     */
    private int tileLength;

    /**
     * The height reminder.
     */
    private final int[] heightReminder;

    /**
     * The weight reminder.
     */
    private final int[] weightReminder;

    /**
     * Constructor.
     * @param context the context.
     */
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        gameLoopThread = new GameLoopThread(this);

        this.nbLines = Game.NB_LINES;
        this.nbColumns = Game.NB_COLUMNS;

        this.heightReminder = new int[nbLines];
        this.weightReminder = new int[nbColumns];
    }

    /**
     * Draw elements on the canvas.
     * @param canvas the canvas where elements will be drawn.
     */
    public void doDraw(final Canvas canvas) {
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

    /**
     * Draw the board of the game.
     * @param canvas the canvas where elements will be drawn.
     * @param paint the painting style.
     */
    private void drawBoard(final Canvas canvas, final Paint paint) {
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

    /**
     * Draw the players of the game.
     * @param canvas the canvas where elements will be drawn.
     * @param paint the painting style.
     */
    private void drawPlayers(final Canvas canvas, final Paint paint) {
        final int radius = tileLength / 2;

        for(final Player player: game.getPlayers()){
            final Position pos = player.getPosition();

            if(pos == null || !pos.isLegalPosition(nbLines,nbColumns))
                continue;

            if(!player.isAlive())
                paint.setColor(ContextCompat.getColor(getContext(), R.color.tileColor));
            else if(game.isFirstPlayer(player))
                paint.setColor(ContextCompat.getColor(getContext(), R.color.bottomPlayerColor));
            else if(game.isSecondPlayer(player))
                paint.setColor(ContextCompat.getColor(getContext(), R.color.topPlayerColor));
            else
                continue;

            int hReminders = 0;
            int wReminders = 0;
            for(int line = 0; line < pos.getLine(); line ++){
                hReminders += heightReminder[line];
            }
            for(int column = 0; column < pos.getColumn(); column ++){
                wReminders += weightReminder[column];
            }
            canvas.drawCircle(tileLength*pos.getColumn() + wReminders + radius + (weightReminder[pos.getColumn()] / 2),
                    tileLength*pos.getLine() + hReminders + radius + (heightReminder[pos.getLine()] / 2),
                    radius,paint);
        }
    }

    /**
     * Callback when surface is created.
     * @param surfaceHolder the surface holder.
     */
    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        // création du processus GameLoopThread si cela n'est pas fait
        if (gameLoopThread.getState() == Thread.State.TERMINATED) {
            gameLoopThread = new GameLoopThread(this);
        }
        gameLoopThread.setRunning(true);
        gameLoopThread.start();
    }

    /**
     * Callback when surface is destroyed.
     * @param surfaceHolder the surface holder.
     */
    @Override
    public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
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

    /**
     * Callback when surface changed.
     * @param surfaceHolder the surface holder.
     * @param i the format.
     * @param w the weight.
     * @param h the height.
     */
    @Override
    public void surfaceChanged(final SurfaceHolder surfaceHolder, final int i, final int w, final int h) {
        this.resize(w, h);
    }

    /**
     * Resize the surface.
     * @param w the width.
     * @param h the height.
     */
    private void resize(final int w, final int h) {
        for(int i = 0; i < nbLines; i ++)
            heightReminder[i] = 0;
        for(int i = 0; i < nbColumns; i ++)
            weightReminder[i] = 0;

        this.tileLength =  Math.min(h / nbLines, w / nbColumns);

        // For height
        int hReminder = h % tileLength;
        int i = 0;
        while(hReminder > 0){
            heightReminder[i] ++;
            hReminder --;
            i = (i + 1) % nbLines;
        }

        // For width
        i = 0;
        int wReminder = w % tileLength;
        while(wReminder > 0){
            weightReminder[i] ++;
            wReminder --;
            i = (i + 1) % nbColumns;
        }
    }

    /**
     * Set the game.
     * @param game the game to set.
     */
    public void setGame(final OneVSOneGame game) {
        this.game = game;
        this.board = game.getGameBoard();
    }
}