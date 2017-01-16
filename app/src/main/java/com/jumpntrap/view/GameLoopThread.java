package com.jumpntrap.view;

import android.graphics.Canvas;

/**
 * GameLoopThread defines a loop thread for the game.
 * Based on: https://fr.jeffprod.com/blog/2015/les-bases-d-un-jeu-android-en-2d.html
 */
final class GameLoopThread extends Thread {
    /**
     * Number of frames per second.
     */
    private final static int FRAMES_PER_SECOND = 30;

    /**
     * Frequency.
     */
    private final static int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;

    /**
     * The game view.
     */
    private final GameView view;

    /**
     * Running state of the game.
     */
    private boolean running = false;

    /**
     * Constructor
     * @param view the view.
     */
    GameLoopThread(final GameView view) {
        this.view = view;
    }

    /**
     * Set the running state.
     * @param run the running state to set.
     */
    void setRunning(final boolean run) {
        running = run;
        }

    /**
     * Start of the thread.
     */
    @Override
    public void run() {
        // boucle tant que running est vrai
        // il devient faux par setRunning(false), notamment lors de l'arrêt de l'application
        // cf : surfaceDestroyed() dans GameView.java
        while (running) {
            // horodatage actuel
            final long startTime = System.currentTimeMillis();

            // Rendu de l'image, tout en vérrouillant l'accès car nous
            // y accédons à partir d'un processus distinct
            Canvas c = null;
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    view.doDraw(c);
                }
            }
            finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }

            // Calcul du temps de pause, et pause si nécessaire
            // afin de ne réaliser le travail ci-dessus que X fois par secondes
            final long sleepTime = SKIP_TICKS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime >= 0) {
                    sleep(sleepTime);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}