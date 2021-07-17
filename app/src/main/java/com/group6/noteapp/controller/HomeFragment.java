package com.group6.noteapp.controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

/**
 * Home Fragment (Main Activity)
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";   // Log tag
    private NoteAdapter adapter;                        // Firestore adapter

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu);

        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortByTitleAscItem:
                Toast.makeText(getActivity(), "title asc", Toast.LENGTH_SHORT).show();

                break;

            case R.id.sortByTitleDescItem:
                Toast.makeText(getActivity(), "title desc", Toast.LENGTH_SHORT).show();

                break;

            case R.id.sortByCreatedDateItem:
                Toast.makeText(getActivity(), "created date", Toast.LENGTH_SHORT).show();

                break;

            case R.id.sortByUpdatedDateItem:
                Toast.makeText(getActivity(), "updated date", Toast.LENGTH_SHORT).show();

                break;

            default:
                // Do nothing

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
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
                        Query query = noteColRef.whereEqualTo("deleted", false).orderBy("updatedDate", Query.Direction.DESCENDING);

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
                                                "Do you want to move this note to trash?");
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
                                                                "Please wait while we moving your note.");
                                                        progressDialog.show();

                                                        // Delete note
                                                        deleteNote(db, viewHolder, firebaseUser, progressDialog);
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


    /**
     * Delete Note
     *
     * @param db             firestore database
     * @param viewHolder     Recycler View view holder
     * @param firebaseUser   Current firebase user
     * @param progressDialog progress dialog
     */
    private void deleteNote(FirebaseFirestore db, RecyclerView.ViewHolder viewHolder, FirebaseUser firebaseUser, NoteAppProgressDialog progressDialog) {
        ;
        // Get note from its position in Firestore adapter
        Note deleteNote = adapter.getItem(viewHolder.getAdapterPosition());
        deleteNote.setDeleted(true);

        db.collection("users").document(firebaseUser.getUid())
                .collection("notebooks").document(deleteNote.getNotebook().getId()).collection("notes")
                .document(deleteNote.getId())
                .update("deleted", deleteNote.isDeleted(),
                        "updatedDate", Timestamp.now())
                // If delete note successful
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Show toast message to notify user
                        Toast.makeText(getActivity(), "Move note to trash successful.", Toast.LENGTH_SHORT).show();
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
                        dialog.setupOKDialog("Move To Trash Failed",
                                "Something went wrong while we move your note. Please try again!");
                        dialog.create().show();

                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    }
                });
    }
}
