/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

public class NoteAppDialog extends AlertDialog.Builder {
    public NoteAppDialog(Context context) {
        super(context);
    }

    public AlertDialog setupOKDialog(String title, String message){
        this.setTitle(title);
        this.setMessage(message);
        this.setCancelable(false);
        this.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    /**
                     * Dismiss the dialog
                     * @param dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = this.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    public AlertDialog setupConfirmationDialog(String title, String message){
        this.setTitle(title);
        this.setMessage(message);
        this.setCancelable(false);
        this.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    /**
                     * Dismiss the dialog
                     * @param dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = this.create();
//        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            /**
             * Highlight the negative button by default
             * @param dialogInterface
             */
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                btnNegative.setFocusable(true);
                btnNegative.setFocusableInTouchMode(true);
                btnNegative.requestFocus();
            }
        });

        return dialog;
    }
}
