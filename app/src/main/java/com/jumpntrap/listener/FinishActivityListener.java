package com.jumpntrap.listener;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;

public final class FinishActivityListener implements DialogInterface.OnKeyListener {

    private final Activity activity;
    private final AlertDialog dialog;

    public FinishActivityListener(final Activity activity, final AlertDialog dialog) {
        this.activity = activity;
        this.dialog = dialog;
    }

    @Override
    public boolean onKey(final DialogInterface dialogInterface, final int keyCode, final KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activity.finish();
            dialog.dismiss();
        }

        return true;
    }
}
