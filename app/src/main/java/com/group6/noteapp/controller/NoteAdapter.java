/*
 * Group 06 SE1402
 */

package com.group6.noteapp.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.model.Notebook;
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteViewHolder;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

/**
 * Note Adapter to store list of notes and display
 */
public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteViewHolder> {

    private final Context context;              // activity's context
    private final Notebook notebook;            // notebook in which the notes in adapter are in
    private final FirebaseUser firebaseUser;          // Firebase user
    private final FirebaseStorage firebaseStorage;    // Firebase storage

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options Firestore Recycler Options
     */
    public NoteAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Note> options, Context context,
                       Notebook notebook) {
        super(options);
        this.context = context;
        this.notebook = notebook;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    /**
     * Get notebook to pass to add new note
     * @return  notebook
     */
    public Notebook getNotebook() {
        return notebook;
    }

    /**
     * Get note item view type to display
     * @param position  note position in adapter
     * @return  1 if note is text note
     *          2 if note is image note
     *          3 if note is audio note
     */
    @Override
    public int getItemViewType(int position) {
        Note note = getItem(position);

        return note.getType();
    }

    /**
     * Replace contents of a view (invoked by the layout manager)
     *
     * @param holder   note view holder
     * @param position note position in adapter
     * @param model    note object
     */
    @Override
    protected void onBindViewHolder(@NonNull @NotNull NoteViewHolder holder, int position,
                                    @NonNull @NotNull Note model) {
        // Date formatter and note's updated date
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy h:mm aa");
        String updatedDate;

        switch (model.getType()) {
            case 1:
                // Set note's title
                holder.getNoteTitle().setText(model.getTitle());

                // Set note's content
                holder.getNoteContent().setText(model.getContent());

                // Set note's updated date
                updatedDate = dateFormat.format(model.getUpdatedDate().toDate());
                holder.getNoteUpdatedDate().setText(updatedDate);           // set note updated date
                model.setNotebook(notebook);

                // Card view's On Click Listener
                holder.getNoteCardView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewEditNoteIntent = new Intent(context, ViewEditNoteActivity.class);

                        viewEditNoteIntent.putExtra("note", model);

                        context.startActivity(viewEditNoteIntent);
                    }
                });

                break;

            case 2:
                // Set note's title
                holder.getNoteTitle().setText(model.getTitle());

                // Set captured image in note
                StorageReference profileRef = firebaseStorage.getReference()
                        .child(firebaseUser.getUid() + "/images/" + model.getContent());
                profileRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(context).load(uri).into(holder.getNoteImage());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Log.e("error", e.getMessage());
                            }
                        });

                // Set note's updated date
                updatedDate = dateFormat.format(model.getUpdatedDate().toDate());
                holder.getNoteUpdatedDate().setText(updatedDate);

                // Set note's notebook
                model.setNotebook(notebook);

                // Card view's On Click Listener
                holder.getNoteCardView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewImageDetailsIntent = new Intent(context, ViewImageDetails.class);

                        viewImageDetailsIntent.putExtra("note", model);

                        context.startActivity(viewImageDetailsIntent);
                    }
                });

                break;
            case 3:
                // Set note's title
                holder.getNoteTitle().setText(model.getTitle());

                // Set note's updated date
                updatedDate = dateFormat.format(model.getUpdatedDate().toDate());
                holder.getNoteUpdatedDate().setText(updatedDate);

                // Set note's notebook
                model.setNotebook(notebook);

                // Card view's On Click Listener
                holder.getNoteCardView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewRecordIntent = new Intent(context, RecordPlayItem.class);

                        viewRecordIntent.putExtra("note", model);

                        context.startActivity(viewRecordIntent);
                    }
                });

                break;
        }

    }


    /**
     * Create new view (invoked by the layout manager)
     * @param parent   parent of view layout
     * @param viewType type of view
     * @return note view holder
     */
    @NonNull
    @NotNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View noteView = null;

        switch (viewType){
            case 1:
                noteView = inflater.inflate(R.layout.recycler_text_note_item, parent, false);

                break;

            case 2:
                noteView = inflater.inflate(R.layout.recycler_image_note_item, parent, false);

                break;

            case 3:
                noteView = inflater.inflate(R.layout.recycler_audio_note_item, parent, false);

                break;
        }

        return new NoteViewHolder(noteView);
    }

    /**
     * Triggered when there is an error getting a query snapshot data.
     *
     * @param e Firebase Firestore Exception
     */
    @Override
    public void onError(@NotNull FirebaseFirestoreException e) {
        Log.e("note adapter error", e.getMessage(), e);

        NoteAppDialog dialog = new NoteAppDialog(context);
        dialog.setupOKDialog("Error Encountered",
                "Something went wrong while we load your notes. Please try again!");
        dialog.create().show();
    }
}
