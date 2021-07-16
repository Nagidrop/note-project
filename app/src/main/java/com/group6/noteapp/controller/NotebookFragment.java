package com.group6.noteapp.controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotebookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotebookFragment extends Fragment {

    private NoteAdapter adapter;                        // Firestore adapter

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotebookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotebookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotebookFragment newInstance(String param1, String param2) {
        NotebookFragment fragment = new NotebookFragment();
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
        View inflatedView = inflater.inflate(R.layout.fragment_notebook, container, false);

        /* Set up progress dialog to tell user to wait */
        NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(getActivity());
        progressDialog.setUpDialog("Just a moment...",
                "Please wait while we get your notebooks from server.");
        progressDialog.show();

        // Set up recycler view
        setupRecyclerView(inflatedView, progressDialog);

        // Dismiss dialog after setup
        progressDialog.dismiss();

        return inflatedView;
    }

    /**
     * Setup recycler view to display notes
     * @param inflatedView      the inflated view
     * @param progressDialog    Note App progress dialog
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
                        Query query = noteColRef.orderBy("updatedDate", Query.Direction.DESCENDING);

                        // Options to configure the FirestoreRecyclerAdapter
                        FirestoreRecyclerOptions<Note> options =
                                new FirestoreRecyclerOptions.Builder<Note>()
                                        .setQuery(query, Note.class)
                                        .setLifecycleOwner(getActivity())
                                        .build();

                        // instantiate new adapter
                        adapter = new NoteAdapter(options, getActivity(), notebook);

                        /* Set up Recycler View */
                        RecyclerView rvNote = inflatedView.findViewById(R.id.recyclerView);
                        rvNote.setHasFixedSize(true);   // notify recycler view that all items have same size
                        rvNote.setAdapter(adapter);     // set adapter
                        rvNote.setLayoutManager(new LinearLayoutManager(getActivity()));    // set layout

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
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Show progress dialog
                                                        NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(getActivity());
                                                        progressDialog.setUpDialog("Just a moment...",
                                                                "Please wait while we deleting your note.");
                                                        progressDialog.show();

                                                        // Delete note
//                                                        deleteNote(db, viewHolder, firebaseUser, progressDialog);
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
                                        dialog.create().show();
                                    }

                                }).attachToRecyclerView(rvNote);    // attach item call back to recycler view

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
}