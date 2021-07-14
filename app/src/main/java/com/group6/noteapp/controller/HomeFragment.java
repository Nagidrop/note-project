package com.group6.noteapp.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

        /* Firebase instances */
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /* Set up progress dialog to tell user to wait */
        NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(getActivity());
        progressDialog.setUpDialog("Just a moment...",
                "Please wait while we get your notes from server.");
        progressDialog.show();

        CollectionReference notebookColRef = db.collection("users").document(firebaseUser.getUid())
                .collection("notebooks");   // "notebooks" collection reference

        // get all notebooks from collection
        notebookColRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Note> noteList = new ArrayList<>();   // instantiate new list of notes

                        // for each notebook document, get all notes and store to note list
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            /* Cast Notebook to object and set data */
                            Notebook notebook = document.toObject(Notebook.class);
                            notebook.setId(document.getId());

                            CollectionReference noteColRef = document.getReference()
                                    .collection("notes");   // "notes" collection reference

                            // get all notes from collection
                            noteColRef.get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                            // for each note document
                                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                                /* Cast Note to object and set data */
                                                Note note = document.toObject(Note.class);
                                                note.setId(document.getId());
                                                note.setNotebook(notebook);

                                                noteList.add(note); // add to list of notes
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Log.d(Constants.HOME_ERROR, "Error getting note documents: ", e);

                                            progressDialog.dismiss();

                                            // notify user the retrieve process failed
                                            NoteAppDialog dialog = new NoteAppDialog(getActivity());
                                            dialog.setupOKDialog("Retrieve Notes Failed",
                                                    "Something went wrong while we get your notes from server. Please try again!");
                                            dialog.create().show();
                                        }
                                    });
                        }

                        // initiate recycler view using list of notes
                        initRecyclerView(inflatedView, noteList);

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

        return inflatedView;
    }

    /**
     * Initialize recycler view
     *
     * @param inflatedView
     * @param noteList
     */
    private void initRecyclerView(View inflatedView, ArrayList<Note> noteList) {
        /* Get the recycler view, adapter and set layout, adapter to recycler view */
        RecyclerView rvNote = inflatedView.findViewById(R.id.recyclerView);

        NoteAdapter noteAdapter = NoteAdapter.getInstance(getActivity(), noteList);
        rvNote.setAdapter(noteAdapter);
        rvNote.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}