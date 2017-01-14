package com.jumpntrap.players;

import android.content.Context;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.jumpntrap.model.Direction;
import com.jumpntrap.model.Game;
import com.jumpntrap.model.Player;

/**
 * Based on: http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
 */
public final class HumanPlayer extends Player implements OnTouchListener {

    private final GestureDetector gestureDetector;
    private final Context context;

    public HumanPlayer(Context context){
        super();
        game = null;
        this.context = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    protected final void kill(){
        super.kill();
        Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(game == null)
            return false;
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener { //TODO improve the gestureListener to make it more natural

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
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

        private void onSwipeRight() {
            game.handleMove(Direction.E,this);
        }

        private void onSwipeLeft() {
            game.handleMove(Direction.W,this);
        }

        private void onSwipeTop() {
            game.handleMove(Direction.N,this);
        }

        private void onSwipeBottom() {
            game.handleMove(Direction.S,this);
        }

        private void onSwipeTopRight() {
            game.handleMove(Direction.NE,this);
        }

        private void onSwipeTopLeft() {
            game.handleMove(Direction.NW,this);
        }

        private void onSwipeBottomRight() {
            game.handleMove(Direction.SE,this);
        }

        private void onSwipeBottomLeft() {
            game.handleMove(Direction.SW,this);
        }
    }