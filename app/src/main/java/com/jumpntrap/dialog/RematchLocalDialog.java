package com.jumpntrap.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.jumpntrap.R;
import com.jumpntrap.games.OneVSOneGame;
import com.jumpntrap.listener.FinishActivityListener;

/**
 * RematchLocalDialog defines an alert dialog for when a local game is finished.
 */
public final class RematchLocalDialog {
    /**
     * The alert dialog.
     */
    private final AlertDialog dialog;

    /**
     * Constructor.
     * @param activity the activity.
     * @param game the game.
     */
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

    /**
     * Show the dialog.
     */
    public void show() {
        dialog.show();
    }
}
