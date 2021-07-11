/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group6.noteapp.R;

/**
 * Class represents each Recycler View's item in note list
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {
    private final TextView txtTitle;      // note title
    private final TextView txtContent;    // note content

    /**
     * Constructor
     *
     * @param noteView item view
     */
    public NoteViewHolder(@NonNull View noteView) {
        super(noteView);
        txtTitle = noteView.findViewById(R.id.txtNoteTitle);
        txtContent = noteView.findViewById(R.id.txtNoteContent);
    }
//
//    @NonNull
//    public static NoteViewHolder create(@NonNull ViewGroup parent) {
//        return new NoteViewHolder(
//                LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.material_list_item_three_line, parent, false));
//    }

    /* Getters */
    public TextView getTxtTitle() {
        return txtTitle;
    }

    public TextView getTxtContent() {
        return txtContent;
    }
}