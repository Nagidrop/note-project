/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.group6.noteapp.R;

/**
 * Class represents each Recycler View's item in note list
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {
    private final MaterialTextView noteTitle;           // note title
    private final MaterialTextView noteContent;         // note content
    private final MaterialTextView noteUpdatedDate;     // note updated date
    private final MaterialCardView noteCardView;        // note card view
    private final ShapeableImageView noteImageView;       // note image view

    /**
     * Constructor
     *
     * @param noteView item view
     */
    public NoteViewHolder(@NonNull View noteView) {
        super(noteView);

        noteTitle = noteView.findViewById(R.id.txtNoteTitle);
        noteContent = noteView.findViewById(R.id.txtNoteContent);
        noteUpdatedDate = noteView.findViewById(R.id.txtNoteUpdatedDate);
        noteCardView = noteView.findViewById(R.id.cardView);
        noteImageView = noteView.findViewById(R.id.imgNote);
    }

    /* Getters */
    public MaterialTextView getNoteTitle() {
        return noteTitle;
    }

    public MaterialTextView getNoteContent() {
        return noteContent;
    }

    public MaterialTextView getNoteUpdatedDate() {
        return noteUpdatedDate;
    }

    public MaterialCardView getNoteCardView() {
        return noteCardView;
    }

    public ShapeableImageView getNoteImageView() {
        return noteImageView;
    }
}