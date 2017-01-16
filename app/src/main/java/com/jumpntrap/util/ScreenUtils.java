package com.jumpntrap.util;

import android.view.Window;
import android.view.WindowManager;

/**
 * ScreenUtils defines utility methods for the screen device.
 */
public final class ScreenUtils {
    /**
     * Keep the screen active.
     * @param window the window to keep active.
     */
    public static void keepScreenOn(final Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Disable keep on screen.
     * @param window the window to disable keep on screen.
     */
    public static void keepScreenOff(final Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
