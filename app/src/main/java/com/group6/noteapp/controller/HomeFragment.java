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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.model.Notebook;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


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

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference notebookDocRef = db.collection("users").document(firebaseUser.getUid())
                .collection("notebooks");

        notebookDocRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        CollectionReference noteDocRef = document.getReference()
                                .collection("notes");
                        Notebook notebook = document.toObject(Notebook.class);
                        notebook.setId(document.getId());
                        noteDocRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                Log.d("noteerror", "Error getting documents: ");
                                if (task.isSuccessful()) {
                                    ArrayList<Note> noteList = new ArrayList<>();
                                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                        Note note = document.toObject(Note.class);
                                        note.setId(document.getId());
                                        note.setNotebook(notebook);

                                        noteList.add(note);
                                    }
                                    
                                    RecyclerView rvNote = inflatedView.findViewById(R.id.recyclerView);

                                    NoteAdapter noteAdapter = NoteAdapter.getInstance(getActivity(), noteList);
                                    rvNote.setAdapter(noteAdapter);
                                    rvNote.setLayoutManager(new LinearLayoutManager(getActivity()));
                                } else {
                                    Log.d("noteerror", "Error getting documents: ", task.getException());
                                }
                            }
                        });
                    }
                } else {
                    Log.d("notebookerror", "Error getting documents: ", task.getException());
                }
            }
        });

        return inflatedView;
    }

}