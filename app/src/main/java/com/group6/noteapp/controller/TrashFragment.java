package com.group6.noteapp.controller;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.model.Notebook;
import com.group6.noteapp.util.Constants;
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteAppProgressDialog;

import org.jetbrains.annotations.NotNull;


public class TrashFragment extends Fragment {


    private static final String TAG = "HomeFragment";   // Log tag
    private NoteAdapter adapter;                        // Firestore adapter

    /**
     * Constructor
     */
    public TrashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_trash, container, false);

        /* Set up progress dialog to tell user to wait */
        NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(getActivity());
        progressDialog.setUpDialog("Just a moment...",
                "Please wait while we get your notes from server.");
        progressDialog.show();

        // Set up recycler view
        setupRecyclerView(inflatedView, progressDialog);

        // Dismiss dialog after setup
        progressDialog.dismiss();

        return inflatedView;
    }

    /**
     * Setup recycler view to display notes
     *
     * @param inflatedView   the inflated view
     * @param progressDialog Note App progress dialog
     */
    private void setupRecyclerView(View inflatedView, NoteAppProgressDialog progressDialog) {
        /* Firebase instances */
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Notebook collection reference
        CollectionReference notebookColRef = db.collection("users").document(firebaseUser.getUid())
                .collection("notebooks");

        // Get notebook from Firestore
        notebookColRef.document(firebaseUser.getUid()).get()
                // If get notebook successful
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Parse notebook document to object
                        Notebook notebook = documentSnapshot.toObject(Notebook.class);

                        // "notes" collection reference
                        CollectionReference noteColRef = documentSnapshot.getReference()
                                .collection("notes");   // "notebooks" collection reference

                        // Query for Firestore adapter to listen to
                        Query query = noteColRef.whereEqualTo("deleted", true)
                                .orderBy("updatedDate", Query.Direction.DESCENDING);

                        // Options to configure the FirestoreRecyclerAdapter
                        FirestoreRecyclerOptions<Note> options =
                                new FirestoreRecyclerOptions.Builder<Note>()
                                        .setQuery(query, Note.class)
                                        .setLifecycleOwner(getActivity())
                                        .build();

                        // instantiate new adapter
                        adapter = new NoteAdapter(options, getActivity(), notebook);

                        /* Set up Recycler View */
                        RecyclerView rvNote = inflatedView.findViewById(R.id.recyclerViewTrash);
                        rvNote.setHasFixedSize(
                                true);   // notify recycler view that all items have same size
                        rvNote.setAdapter(adapter);     // set adapter
                        rvNote.setLayoutManager(
                                new LinearLayoutManager(getActivity()));    // set layout

                        new ItemTouchHelper(
                                new ItemTouchHelper.SimpleCallback(0,
                                        ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                                    @Override
                                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                                          @NonNull
                                                                  RecyclerView.ViewHolder viewHolder,
                                                          @NonNull RecyclerView.ViewHolder target) {
                                        // this method is called
                                        // when the item is moved.
                                        return false;
                                    }

                                    /**
                                     * Delete note on swipe
                                     * @param viewHolder    view holder
                                     * @param direction     swipe direction
                                     */
                                    @Override
                                    public void onSwiped(
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            int direction) {
                                        // Set up delete confirmation dialog
                                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                                        if (direction == ItemTouchHelper.RIGHT) {
                                            dialog.setupConfirmationDialog("Delete Confirmation",
                                                    "Do you want to delete this note?");
                                            dialog.setPositiveButton("Yes",
                                                    new DialogInterface.OnClickListener() {
                                                        /**
                                                         * Delete user note on confirmation
                                                         * @param dialog
                                                         * @param which
                                                         */
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            // Show progress dialog
                                                            NoteAppProgressDialog progressDialog =
                                                                    new NoteAppProgressDialog(
                                                                            getActivity());
                                                            progressDialog
                                                                    .setUpDialog("Just a moment...",
                                                                            "Please wait while we deleting your note.");
                                                            progressDialog.show();

                                                            // Delete note
                                                            deleteNote(db, viewHolder, firebaseUser,
                                                                    progressDialog);
                                                        }
                                                    });
                                            dialog.setNegativeButton("No",
                                                    new DialogInterface.OnClickListener() {
                                                        /**
                                                         * Refresh recycler view
                                                         * @param dialog
                                                         * @param which
                                                         */
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    });

                                        } else if (direction == ItemTouchHelper.LEFT) {

                                            dialog.setupConfirmationDialog("Restore Confirmation",
                                                    "Do you want to restore this note?");
                                            dialog.setPositiveButton("Yes",
                                                    new DialogInterface.OnClickListener() {
                                                        /**
                                                         * Delete user note on confirmation
                                                         *
                                                         * @param dialog
                                                         * @param which
                                                         */
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            // Show progress dialog
                                                            NoteAppProgressDialog progressDialog =
                                                                    new NoteAppProgressDialog(
                                                                            getActivity());
                                                            progressDialog
                                                                    .setUpDialog("Just a moment...",
                                                                            "Please wait while we restoring your note.");
                                                            progressDialog.show();

                                                            // Delete note
                                                            restoreNote(db, viewHolder,
                                                                    firebaseUser,
                                                                    progressDialog);
                                                        }
                                                    });
                                            dialog.setNegativeButton("No",
                                                    new DialogInterface.OnClickListener() {
                                                        /**
                                                         * Refresh recycler view
                                                         *
                                                         * @param dialog
                                                         * @param which
                                                         */
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    });
                                        }

                                        dialog.create().show();
                                    }

                                }).attachToRecyclerView(
                                rvNote);    // attach item call back to recycler view

                        progressDialog.dismiss();
                    }
                })
                // If get notebook failed
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.d(Constants.HOME_ERROR, "Error getting notebook documents: ", e);
                        progressDialog.dismiss();

                        // notify user the retrieve process failed
                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                        dialog.setupOKDialog("Retrieve Notebooks Failed",
                                "Something went wrong while we get your notebooks from server. Please try again!");
                        dialog.create().show();
                    }
                });
    }

    /**
     * Restore note
     *
     * @param db             firestore database
     * @param viewHolder     Recycler View view holder
     * @param firebaseUser   Current firebase user
     * @param progressDialog progress dialog
     */
    private void restoreNote(FirebaseFirestore db, RecyclerView.ViewHolder viewHolder,
                             FirebaseUser firebaseUser, NoteAppProgressDialog progressDialog) {

        // Get note from its position in Firestore adapter
        Note restoreNote = adapter.getItem(viewHolder.getAdapterPosition());
        restoreNote.setDeleted(false);

        db.collection("users").document(firebaseUser.getUid())
                .collection("notebooks").document(restoreNote.getNotebook().getId()).collection("notes")
                .document(restoreNote.getId())
                .update("deleted", restoreNote.isDeleted(),
                        "updatedDate", Timestamp.now())
                // If delete note successful
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Show toast message to notify user
                        Toast.makeText(getActivity(), "Restore Note successful.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: Removed list item");
                        progressDialog.dismiss();
                    }
                })
                // If delete note failed
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        // Show dialog to notify user of error
                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                        dialog.setupOKDialog("Restore Note Failed",
                                "Something went wrong while we restoring your note. Please try again!");
                        dialog.create().show();

                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    }
                });
    }


    /**
     * Delete Note
     *
     * @param db             firestore database
     * @param viewHolder     Recycler View view holder
     * @param firebaseUser   Current firebase user
     * @param progressDialog progress dialog
     */
    private void deleteNote(FirebaseFirestore db, RecyclerView.ViewHolder viewHolder,
                            FirebaseUser firebaseUser, NoteAppProgressDialog progressDialog) {
        // Get note from its position in Firestore adapter
        Note deleteNote = adapter.getItem(viewHolder.getAdapterPosition());

        db.collection("users").document(firebaseUser.getUid())
                .collection("notebooks").document(deleteNote.getNotebook().getId())
                .collection("notes")
                .document(deleteNote.getId())
                .delete()
                // If delete note successful
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Show toast message to notify user
                        Toast.makeText(getActivity(), "Delete note successful.", Toast.LENGTH_SHORT)
                                .show();
                        Log.d(TAG, "onSuccess: Removed list item");
                        progressDialog.dismiss();
                    }
                })
                // If delete note failed
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        // Show dialog to notify user of error
                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                        dialog.setupOKDialog("Delete Note Failed",
                                "Something went wrong while we delete your note. Please try again!");
                        dialog.create().show();

                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    }
                });
    }
}