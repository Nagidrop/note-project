/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.model.Notebook;
import com.group6.noteapp.view.NoteViewHolder;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteViewHolder> {
    private Context context;
    private Notebook notebook;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NoteAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Note> options, Context context, Notebook notebook) {
        super(options);
        this.context = context;
        this.notebook = notebook;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull NoteViewHolder holder, int position, @NonNull @NotNull Note model) {
        holder.getNoteTitle().setText(model.getTitle());             // set note title
        holder.getNoteContent().setText(model.getContent());         // set note content

        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy h:mm aa");

        String updatedDate = dateFormat.format(model.getUpdatedDate().toDate());
        holder.getNoteUpdatedDate().setText(updatedDate);           // set note updated date
        model.setNotebook(notebook);

        // card view onclick
        holder.getNoteCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewEditNoteIntent = new Intent(context, ViewEditNoteActivity.class);

                viewEditNoteIntent.putExtra("note", model);

                context.startActivity(viewEditNoteIntent);
            }
        });
    }

    @NonNull
    @NotNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View noteView = inflater.inflate(R.layout.recycler_note_item, parent, false);

        return new NoteViewHolder(noteView);
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
       Log.e("note adapter error", e.getMessage(), e);
    }
}
