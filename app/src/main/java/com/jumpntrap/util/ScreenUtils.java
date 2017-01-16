package com.jumpntrap.util;


import android.view.Window;
import android.view.WindowManager;

public final class ScreenUtils {

    public static void keepScreenOn(final Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void keepScreenOff(final Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
