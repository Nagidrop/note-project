/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

public class NoteAppDialog extends MaterialAlertDialogBuilder {
    private String type;  // dialog Type

    public NoteAppDialog(Context context) {
        super(context);
    }

    public void setupOKDialog(String title, String message) {
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
        this.type = "OKDialog";
    }

    public void setupReturnOKDialog(String title, String message, Context activityContext) {
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
                        ((Activity) activityContext).onBackPressed();
                    }
                });
        this.type = "OKDialog";
    }

    public void setupConfirmationDialog(String title, String message) {
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
        this.type = "ConfirmationDialog";
    }

    @NonNull
    @NotNull
    @Override
    public AlertDialog create() {
        AlertDialog alertDialog = super.create();
        alertDialog.setCanceledOnTouchOutside(false);

        if (this.type.equalsIgnoreCase("ConfirmationDialog")) {
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                /**
                 * Highlight the negative button by default
                 * @param dialogInterface
                 */
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    btnNegative.setFocusable(true);
                    btnNegative.setFocusableInTouchMode(true);
                    btnNegative.requestFocus();
                }
            });
        }

        return alertDialog;
    }
}
