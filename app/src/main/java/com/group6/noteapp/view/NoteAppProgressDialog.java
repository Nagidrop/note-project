/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.view;

import android.app.ProgressDialog;
import android.content.Context;

public class NoteAppProgressDialog extends ProgressDialog {

    public NoteAppProgressDialog(Context context) {
        super(context);
    }

    public void setUpDialog(String title, String message){
        this.setTitle("Just a moment...");
        this.setMessage("Please wait while we connect you to Note App.");
        this.setCanceledOnTouchOutside(false);
    }
}
