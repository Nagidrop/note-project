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
import com.group6.noteapp.view.NoteAdapterClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private View inflatedView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private ArrayList<Note> notebookList;
    private ArrayList<Note> noteList;
    private NoteAdapter noteAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        CollectionReference notebookDocRef = db.collection("users").document(firebaseUser.getUid())
                .collection("notebooks");

        notebookDocRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        CollectionReference noteDocRef = document.getReference()
                                .collection("notes");
                        noteDocRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    noteList = new ArrayList<>();
                                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                        Note note = document.toObject(Note.class);
                                        note.setId(document.getId());

                                        noteList.add(note);
                                    }
                                    
                                    RecyclerView rvNote = inflatedView.findViewById(R.id.recyclerView);

                                    noteAdapter = new NoteAdapter(getActivity(), noteList);
                                    noteAdapter.setItemClickListener(new NoteAdapterClickListener() {
                                        @Override
                                        public void onItemClick(int position) {
                                            noteList.get(position);
                                        }
                                    });
                                    rvNote.setAdapter(noteAdapter);
                                    rvNote.setLayoutManager(new LinearLayoutManager(getActivity()));


//                                    rvNote.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(),
//                                            rvNote, new RecyclerViewClickListener() {
//                                        @Override
//                                        public void onClick(View view, int position) {
//                                            Toast.makeText(getActivity(), "lala", Toast.LENGTH_SHORT);
//                                        }
//
//                                        @Override
//                                        public void onLongClick(View view, int position) {
//                                            Toast.makeText(getActivity(), "lala long", Toast.LENGTH_SHORT);
//                                        }
//                                    }));
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