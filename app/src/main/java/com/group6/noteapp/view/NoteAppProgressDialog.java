/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.view;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Note App specific implementation of Progress Dialog
 */
public class NoteAppProgressDialog extends ProgressDialog {

    /**
     * Constructor
     * @param context   activity context
     */
    public NoteAppProgressDialog(Context context) {
        super(context);
    }

    /**
     * Set up the dialog
     * @param title     dialog's title
     * @param message   dialog's message
     */
    public void setUpDialog(String title, String message){
        this.setTitle(title);
        this.setMessage(message);
        this.setCancelable(false);              // prevent the dialog from being canceled
        this.setCanceledOnTouchOutside(false);  // avoid the dialog from being cancelled on outside touch
    }
}
