package com.jumpntrap.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.jumpntrap.R;
import com.jumpntrap.listener.FinishActivityListener;

public final class OpponentLeftDialog {

    private final AlertDialog dialog;

    public OpponentLeftDialog(final Activity activity) {
        dialog = new AlertDialog.Builder(activity).create();

        // Message
        dialog.setMessage(activity.getString(R.string.opponent_left));

        // Ok button
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, activity.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.dismiss();
                        activity.finish();
                    }
                });

        dialog.setOnKeyListener(new FinishActivityListener(activity, dialog));
        dialog.setCanceledOnTouchOutside(false);
    }

    public void show() {
        dialog.show();
    }

}
