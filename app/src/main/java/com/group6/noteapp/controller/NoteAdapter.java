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
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteViewHolder;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

/**
 * Note Adapter to store list of notes
 */
public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteViewHolder> {
    private Context context;            // activity's context
    private Notebook notebook;          // notebook in which the notes in adapter are in

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NoteAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Note> options, Context context,
                       Notebook notebook) {
        super(options);
        this.context = context;
        this.notebook = notebook;
    }

    /**
     * Replace contents of a view (invoked by the layout manager)
     *
     * @param holder    note view holder
     * @param position  note position in adapter
     * @param model     note object
     */
    @Override
    protected void onBindViewHolder(@NonNull @NotNull NoteViewHolder holder, int position,
                                    @NonNull @NotNull Note model) {
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
    /**
     * Create new view (invoked by the layout manager)
     * @param parent   parent of view layout
     * @param viewType type of view
     * @return
     */
    public NoteViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View noteView = inflater.inflate(R.layout.recycler_note_item, parent, false);

        return new NoteViewHolder(noteView);
    }

    /**
     * Triggered when there is an error getting a query snapshot data.
     * @param e Firebase Firestore Exception
     */
    @Override
    public void onError(FirebaseFirestoreException e) {
       Log.e("note adapter error", e.getMessage(), e);

        NoteAppDialog dialog = new NoteAppDialog(context);
        dialog.setupOKDialog("Error Encountered",
                "Something went wrong while we load your notes. Please try again!");
        dialog.create().show();
    }
}
