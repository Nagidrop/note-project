/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.group6.noteapp.R;

/**
 * Class represents each Recycler View's item in note list
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {
    private final MaterialTextView noteTitle;           // note title
    private final MaterialTextView noteContent;         // note content
    private final MaterialTextView noteCreatedDate;     // note created date
    private final MaterialCardView noteCardView;        // note card view

    /**
     * Constructor
     *
     * @param noteView item view
     */
    public NoteViewHolder(@NonNull View noteView) {
        super(noteView);

        noteTitle = noteView.findViewById(R.id.txtNoteTitle);
        noteContent = noteView.findViewById(R.id.txtNoteContent);
        noteCreatedDate = noteView.findViewById(R.id.txtNoteCreatedDate);
        noteCardView = noteView.findViewById(R.id.cardView);
    }

    /* Getters */
    public MaterialTextView getNoteTitle() {
        return noteTitle;
    }

    public MaterialTextView getNoteContent() {
        return noteContent;
    }

    public MaterialTextView getNoteCreatedDate() {
        return noteCreatedDate;
    }

    public MaterialCardView getNoteCardView() {return noteCardView;}
}