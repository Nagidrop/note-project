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

/**
 * Note App specific implementation of AlertDialog Builder
 */
public class NoteAppDialog extends MaterialAlertDialogBuilder {
    private String type;  // type of dialog

    /**
     * Constructor
     * @param context activity context
     */
    public NoteAppDialog(Context context) {
        super(context);
    }

    /**
     * Set up a dialog with only OK button
     * @param title             dialog's title
     * @param message           dialog's message
     */
    public void setupOKDialog(String title, String message) {
        this.setTitle(title);
        this.setMessage(message);
        this.setCancelable(false);          // prevent the dialog from being canceled
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
        this.type = "OKDialog";             // set the dialog type for use with create
    }

    /**
     * Set up a dialog with only OK button (for unsaved back actions)
     * @param title             dialog's title
     * @param message           dialog's message
     * @param activityContext   activity context
     */
    public void setUpReturnOKDialog(String title, String message, Context activityContext) {
        this.setTitle(title);
        this.setMessage(message);
        this.setCancelable(false);          // prevent the dialog from being canceled
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
        this.type = "OKDialog";             // set the dialog type for use with create
    }

    /**
     * Set up dialog with confirmation yes-no buttons
     * @param title             dialog's title
     * @param message           dialog's message
     */
    public void setupConfirmationDialog(String title, String message) {
        this.setTitle(title);
        this.setMessage(message);
        this.setCancelable(false);          // prevent the dialog from being canceled
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
        this.type = "ConfirmationDialog";   // set the dialog type for use with create
    }

    @NonNull
    @NotNull
    @Override
    /**
     * Create a dialog from builder
     */
    public AlertDialog create() {
        AlertDialog alertDialog = super.create();
        alertDialog.setCanceledOnTouchOutside(false);   // avoid the dialog from being cancelled on outside touch

        if (this.type.equalsIgnoreCase("ConfirmationDialog")) {
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                /**
                 * Highlight the negative button by default
                 * @param dialogInterface
                 */
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    /* Get negative button and set focus on it */
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
