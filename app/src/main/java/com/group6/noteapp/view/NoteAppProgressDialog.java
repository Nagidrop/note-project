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
        this.setTitle(title);
        this.setMessage(message);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }
}
