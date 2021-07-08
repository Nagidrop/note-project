/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class represents each recycler view's item view in note list
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {
    private TextView txtTitle;      // note title
    private TextView txtContent;    // note content

    /**
     * Constructor
     *
     * @param itemView item view
     */
    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);
//        txtTitle = itemView.findViewById(R.id.tv_note_item_title);
//        txtContent = itemView.findViewById(R.id.tv_note_item_content);
    }

    /* Getters and Setters */
    public TextView getTxtTitle() {
        return txtTitle;
    }

    public void setTxtTitle(TextView txtTitle) {
        this.txtTitle = txtTitle;
    }

    public TextView getTxtContent() {
        return txtContent;
    }

    public void setTxtContent(TextView txtContent) {
        this.txtContent = txtContent;
    }
}