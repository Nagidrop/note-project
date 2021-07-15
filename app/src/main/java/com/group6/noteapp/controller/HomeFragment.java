package com.group6.noteapp.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;

/**
 * Home Fragment (Main Activity)
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    ArrayList<Note> noteList;
    NoteAdapter adapter;
    /**
     * Constructor
     */
    public HomeFragment() {
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
        View inflatedView = inflater.inflate(R.layout.fragment_home, container, false);
        /* Set up progress dialog to tell user to wait */
        NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(getActivity());
        progressDialog.setUpDialog("Just a moment...",
                "Please wait while we get your notes from server.");
        progressDialog.show();

        setupRecyclerView(inflatedView, progressDialog);

        progressDialog.dismiss();

        return inflatedView;
    }

    private void setupRecyclerView(View inflatedView, NoteAppProgressDialog progressDialog) {
        /* Firebase instances */
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference notebookColRef = db.collection("users").document(firebaseUser.getUid())
                .collection("notebooks");

        notebookColRef.document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Notebook notebook = documentSnapshot.toObject(Notebook.class);

                        CollectionReference noteColRef = documentSnapshot.getReference()
                                .collection("notes");   // "notebooks" collection reference

                        Query query = noteColRef.orderBy("updatedDate", Query.Direction.DESCENDING);

                        FirestoreRecyclerOptions<Note> options =
                                new FirestoreRecyclerOptions.Builder<Note>()
                                        .setQuery(query, Note.class)
                                        .setLifecycleOwner(getActivity())
                                        .build();

                        adapter = new NoteAdapter(options, getActivity(), notebook);

                        RecyclerView rvNote = inflatedView.findViewById(R.id.recyclerView);
                        rvNote.setHasFixedSize(true);
                        rvNote.setAdapter(adapter);
                        rvNote.setLayoutManager(new LinearLayoutManager(getActivity()));

                        new ItemTouchHelper(
                                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                                    @Override
                                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                                          @NonNull
                                                                  RecyclerView.ViewHolder viewHolder,
                                                          @NonNull RecyclerView.ViewHolder target) {
                                        // this method is called
                                        // when the item is moved.
                                        return false;
                                    }

                                    @Override
                                    public void onSwiped(
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            int direction) {

                                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                                        dialog.setupConfirmationDialog("Delete Confirmation",
                                                "Do you want to delete this note?");
                                        dialog.setPositiveButton("Yes",
                                                new DialogInterface.OnClickListener() {
                                                    /**
                                                     * Delete user note
                                                     * @param dialog
                                                     * @param which
                                                     */
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(getActivity());
                                                        progressDialog.setUpDialog("Just a moment...",
                                                                "Please wait while we deleting your note.");
                                                        progressDialog.show();
                                                        deleteNote(db, viewHolder, firebaseUser, progressDialog);
                                                    }
                                                });
                                        dialog.setNegativeButton("No",
                                                new DialogInterface.OnClickListener() {
                                                    /**
                                                     *  Reset recycler view
                                                     * @param dialog
                                                     * @param which
                                                     */
                                                    @Override
                                                    public void onClick(DialogInterface dialog,
                                                                        int which) {
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                });
                                        dialog.create().show();
                                    }

                                }).attachToRecyclerView(rvNote);

                        progressDialog.dismiss();
                    }
                })
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
     * Delete Note
     * @param db firestore database
     * @param viewHolder Recycler View view holder
     * @param firebaseUser Current firebase user
     * @param progressDialog progress dialog
     */
    private void deleteNote(FirebaseFirestore db, RecyclerView.ViewHolder viewHolder, FirebaseUser firebaseUser,NoteAppProgressDialog progressDialog) { ;
        Note deleteNote = adapter.getItem(viewHolder.getAdapterPosition());

        db.collection("users").document(firebaseUser.getUid())
                .collection("notebooks").document(deleteNote.getNotebook().getId()).collection("notes")
                .document(deleteNote.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Delete note successful", Toast.LENGTH_SHORT);
                        Log.d(TAG, "onSuccess: Removed list item");
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                        dialog.setupOKDialog("Delete Note Failed",
                                "Something went wrong while we delete your note. Please try again!");
                        dialog.create().show();

                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    }
                });
        adapter.notifyDataSetChanged();
    }
}
