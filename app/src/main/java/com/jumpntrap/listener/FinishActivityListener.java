package com.jumpntrap.listener;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;

/**
 * FinishActivityListener defines a key listener to finish an activity.
 */
public final class FinishActivityListener implements DialogInterface.OnKeyListener {
    /**
     * The activity to finish.
     */
    private final Activity activity;

    /**
     * The alert dialog.
     */
    private final AlertDialog dialog;

    /**
     * Constructor.
     * @param activity the activity to finish.
     * @param dialog the alert dialog which is opened.
     */
    public FinishActivityListener(final Activity activity, final AlertDialog dialog) {
        this.activity = activity;
        this.dialog = dialog;
    }

    /**
     * Callback when a key is pressed.
     * @param dialogInterface the dialog interface.
     * @param keyCode the key code.
     * @param keyEvent the key event.
     * @return true if the event is handled.
     */
    @Override
    public boolean onKey(final DialogInterface dialogInterface, final int keyCode, final KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activity.finish();
            dialog.dismiss();
        }

        return true;
    }
}
