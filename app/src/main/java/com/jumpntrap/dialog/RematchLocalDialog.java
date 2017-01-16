package com.jumpntrap.dialog;


import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.jumpntrap.R;
import com.jumpntrap.games.OneVSOneGame;
import com.jumpntrap.listener.FinishActivityListener;

public final class RematchLocalDialog {

    private final AlertDialog dialog;

    public RematchLocalDialog(final Activity activity, final OneVSOneGame game) {
        // Create dialog
        dialog = new AlertDialog.Builder(activity).create();

        // Rematch button
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, activity.getString(R.string.rematch),
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.dismiss();
                        game.restart();
                    }
                });

        dialog.setOnKeyListener(new FinishActivityListener(activity, dialog));
        dialog.setCanceledOnTouchOutside(false);
    }

    public void show() {
        dialog.show();
    }

}
