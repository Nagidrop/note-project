/*
 * Group 06 SE1402
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
 * Recycler View's item view holder
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {
    private final MaterialTextView noteTitle;           // note title
    private final MaterialTextView noteContent;         // note content
    private final MaterialTextView noteUpdatedDate;     // note updated date
    private final MaterialCardView noteCardView;        // note card view
    private final ShapeableImageView noteImage;         // note image view

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
        noteImage = noteView.findViewById(R.id.imgNote);
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

    public ShapeableImageView getNoteImage() {
        return noteImage;
    }
}