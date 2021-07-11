package com.group6.noteapp.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.view.NoteViewHolder;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private Context context; // activity context
    private List<Note> notes; // list of notes

    /**
     * Constructor
     *
     * @param context activity context
     * @param notes   list of notes
     */
    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    /**
     * Create new view holder when there are no existing ones that can be reused
     *
     * @param parent   view group
     * @param viewType view type
     * @return the view holder
     */
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View noteView = inflater.inflate(R.layout.rv_note_item, parent, false);

        return new NoteViewHolder(noteView);
    }

    /**
     * Display items of the adapter
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder,
                                 int position) {
        Note note = notes.get(position);                // get note from position
        holder.getTxtTitle().setText(note.getTitle());       // set note title
        holder.getTxtContent().setText(note.getContent());   // set note content
    }

    /**
     * Get amount of notes in list
     *
     * @return the amount of notes in list
     */
    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * Get note's ID from its position in note list
     *
     * @param position position of note in note list
     * @return the note's ID
     */
    public int getNoteId(int position) {
        return 0;
    }

    /**
     * Return note list which the adapter uses
     *
     * @return list of notes
     */
    public List<Note> getNotes() {
        return notes;
    }
}