package com.jumpntrap.controller;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.jumpntrap.model.Direction;
import com.jumpntrap.model.HumanPlayer;

/**
 * Based on: http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
 */
public class HumanPlayerController implements OnTouchListener {

    private final GestureDetector gestureDetector;
    private final HumanPlayer player;

    public HumanPlayerController(Context ctx, HumanPlayer player){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        this.player = player;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {

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
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
        synchronized (player) {
            player.setDirection(Direction.E);
            player.notify();
        }
    }

    public void onSwipeLeft() {
        synchronized (player) {
        player.setDirection(Direction.W);
        player.notify();
        }
    }

    public void onSwipeTop() {
        synchronized (player) {
        player.setDirection(Direction.N);
        player.notify();
        }
    }

    public void onSwipeBottom() {
        synchronized (player) {
        player.setDirection(Direction.S);
        player.notify();
        }
    }
}