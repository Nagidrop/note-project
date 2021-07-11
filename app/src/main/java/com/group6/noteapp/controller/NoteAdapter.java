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

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private Context context; // activity context
    private ArrayList<Note> notes; // list of notes

    /**
     * Constructor
     *
     * @param context activity context
     * @param notes   list of notes
     */
    public NoteAdapter(Context context, ArrayList<Note> notes) {
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
        Note note = notes.get(position);                            // get note from position
        holder.getNoteTitle().setText(note.getTitle());             // set note title
        holder.getNoteContent().setText(note.getContent());         // set note content
        holder.getNoteCreatedDate().setText(String.valueOf(note.getCreatedDate())); // set note created date
    }

    /**
     * Get amount of notes in list
     *
     * @return the amount of notes in list
     */
    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
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