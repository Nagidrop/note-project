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
    private final TextView noteTitle;            // note title
    private final TextView noteContent;          // note content
    private final TextView noteCreatedDate;      // note created date

    /**
     * Constructor
     *
     * @param noteView item view
     */
    public NoteViewHolder(@NonNull View noteView, NoteAdapterClickListener itemClickListener) {
        super(noteView);
        noteTitle = noteView.findViewById(R.id.txtNoteTitle);
        noteContent = noteView.findViewById(R.id.txtNoteContent);
        noteCreatedDate = noteView.findViewById(R.id.txtNoteCreatedDate);

//        CardView cv = noteView.findViewById(R.id.cardView);

//        noteView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (itemClickListener != null){
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION){
//                        itemClickListener.onItemClick(position);
//                    }
//                    Toast.makeText(noteView.getContext(), "lala at" + position, Toast.LENGTH_SHORT);
//                }
//            }
//        });
    }
//
//    @NonNull
//    public static NoteViewHolder create(@NonNull ViewGroup parent) {
//        return new NoteViewHolder(
//                LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.material_list_item_three_line, parent, false));
//    }

    /* Getters */
    public TextView getNoteTitle() {
        return noteTitle;
    }

    public TextView getNoteContent() {
        return noteContent;
    }

    public TextView getNoteCreatedDate() {
        return noteCreatedDate;
    }
}