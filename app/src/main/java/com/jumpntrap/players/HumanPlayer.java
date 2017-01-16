package com.jumpntrap.players;

import android.content.Context;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.jumpntrap.model.Direction;
import com.jumpntrap.model.Player;

/**
 * HumanPlayer defines a human player for the game.
 * Based on: http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
 */
public final class HumanPlayer extends Player implements OnTouchListener {
    /**
     * The gesture detector for swipe event.
     */
    private final GestureDetector gestureDetector;

    /**
     * The context.
     */
    private final Context context;

    /**
     * Constructor.
     * @param context the context.
     */
    public HumanPlayer(final Context context){
        super();
        game = null;
        this.context = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    /**
     * Kill the player.
     */
    @Override
    protected final void kill(){
        super.kill();
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
    }

    /**
     * Callback when a touch event is triggered.
     * @param v the view.
     * @param event the triggered event.
     * @return true if the event is handled.
     */
    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        return game != null && gestureDetector.onTouchEvent(event);
    }

    /**
     * GestureListener defines a simple gesture listener for swipe events.
     */
    private final class GestureListener extends SimpleOnGestureListener { //TODO improve the gestureListener to make it more natural

        /**
         * The swipe threshold.
         */
        private static final int SWIPE_THRESHOLD = 100;

        /**
         * The swipe velocity threshold.
         */
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        /**
         * Callback when a down event is triggered.
         * @param e the motion event triggered.
         * @return true if the event is handled.
         */
        @Override
        public boolean onDown(final MotionEvent e) {
            return true;
        }

        /**
         * Callback when a fling event is triggered.
         * @param e1 the start of the motion event.
         * @param e2 the end of the motion event.
         * @param velocityX the x velocity.
         * @param velocityY the y velocity.
         * @return true if the event is handled.
         */
        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
            boolean result = false;
            try {
                final float diffY = e2.getY() - e1.getY();
                final float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            if (diffY > 0)
                                onSwipeBottomRight();
                            else
                                onSwipeTopRight();
                        } else {
                            if (diffY > 0)
                                onSwipeBottomLeft();
                            else
                                onSwipeTopLeft();
                        }
                    } else {
                        if (diffX > 0)
                            onSwipeRight();
                        else
                            onSwipeLeft();
                    }
                    result = true;
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0)
                        onSwipeBottom();
                    else
                        onSwipeTop();

                    result = true;
                }

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

    /**
     * Swipe to the right.
     */
    private void onSwipeRight() {
            game.handleMove(Direction.E,this);
        }

    /**
     * Swipe to the left.
     */
    private void onSwipeLeft() {
        game.handleMove(Direction.W,this);
    }

    /**
     * Swipe to the top.
     */
    private void onSwipeTop() {
        game.handleMove(Direction.N,this);
    }

    /**
     * Swipe to the bottom.
     */
    private void onSwipeBottom() {
        game.handleMove(Direction.S,this);
    }

    /**
     * Swipe to the top right.
     */
    private void onSwipeTopRight() {
        game.handleMove(Direction.NE,this);
    }

    /**
     * Swipe to the top left.
     */
    private void onSwipeTopLeft() {
        game.handleMove(Direction.NW,this);
    }

    /**
     * Swipe to the bottom right.
     */
    private void onSwipeBottomRight() {
        game.handleMove(Direction.SE,this);
    }

    /**
     * Swipe to the bottom left.
     */
    private void onSwipeBottomLeft() {
        game.handleMove(Direction.SW,this);
    }
}