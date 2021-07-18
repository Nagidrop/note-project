/*
 * Group 06 SE1402
 */

package com.group6.noteapp.controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.model.Notebook;
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteAppProgressDialog;

import org.jetbrains.annotations.NotNull;

/**
 * Activity for viewing and editing note
 */
public class ViewEditNoteActivity extends AppCompatActivity {

    private String savedNoteTitle;                  // Saved note title (to check if note title not saved)
    private String savedNoteContent;                // Saved note title (to check if note content not saved)
    private TextInputLayout txtInputNoteTitle;      // Note Title Text Input Layout
    private TextInputLayout txtInputNoteContent;    // Note Content Text Input Layout
    private Note savedNote;                         // Note object with data to add or update

    /**
     * Setup data to display
     * @param savedInstanceState    saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_note);

        // If user add new note
        if (getIntent().getParcelableExtra("note") == null) {
            // Create new note
            savedNote = new Note();
            savedNote.setType(1);
        } else {
            // If user update existing note

            // Get existing note object
            savedNote = getIntent().getParcelableExtra("note");
        }

        // Check if user add new note and then add new notebook
        if (savedNote.getNotebook() == null) {
            // Get notebook from intent
            Notebook notebook = getIntent().getParcelableExtra("notebook");

            // Set notebook for new note
            savedNote.setNotebook(notebook);
        }

        /* Set saved data as current data */
        savedNoteTitle = savedNote.getTitle();
        savedNoteContent = savedNote.getContent();

        /* Get TextInputLayout, Text Views and Button */
        txtInputNoteTitle = findViewById(R.id.txtInputNoteTitle);
        txtInputNoteContent = findViewById(R.id.txtInputNoteContent);
        MaterialTextView txtNotebook = findViewById(R.id.txtNotebook);
        FloatingActionButton fabSave = findViewById(R.id.fabSave);

        /* Set data to display to user */
        txtInputNoteTitle.getEditText().setText(savedNoteTitle);
        txtInputNoteContent.getEditText().setText(savedNoteContent);
        txtNotebook.setText(savedNote.getNotebook().getTitle());

        // If note is deleted then disable editing on title and content, hide save button
        if (savedNote.isDeleted()){
            txtInputNoteTitle.getEditText().setEnabled(false);
            txtInputNoteTitle.getEditText().setInputType(InputType.TYPE_NULL);
            txtInputNoteContent.getEditText().setEnabled(false);
            txtInputNoteContent.getEditText().setInputType(InputType.TYPE_NULL);
            fabSave.setVisibility(View.INVISIBLE);
        }

        /* Get Toolbar and set Action Bar as the Toolbar */
        MaterialToolbar toolbar = findViewById(R.id.noteToolbar);
        setSupportActionBar(toolbar);

        // Set navigation button to perform on back action
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set Save button's on click
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Get current note title and content */
                String noteTitle = txtInputNoteTitle.getEditText().getText().toString();
                String noteContent = txtInputNoteContent.getEditText().getText().toString();

                // Check if content is unsaved, if true then continue to save the note
                if (isUnsaved(noteTitle, noteContent)) {
                    savedNote.setTitle(noteTitle);
                    savedNote.setContent(noteContent);

                    // Proceed to save note
                    saveNote(false);
                } else {
                    // If content is saved (not changed), display toast message to user
                    Toast.makeText(ViewEditNoteActivity.this, "Note not changed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Create options menu for View Note activity
     * @param menu  menu to create
     * @return  true if successful
     *          false if failed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_note, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Triggers when menu item is selected
     * @param item  the selected item
     * @return  status of consumption
     *          true if handle selection here
     *          false if not
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(ViewEditNoteActivity.this, findViewById(R.id.nav_menu_note));

        // Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_popup_note, popup.getMenu());

        // Registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                // Check if note has been saved (created)
                if (savedNote.getId() == null) {
                    // Show dialog to notify user
                    NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                    dialog.setupOKDialog("Cannot Delete",
                            "Note hasn't been created yet.");
                    dialog.create().show();
                } else if (!savedNote.isDeleted()) {
                    // Create new confirmation dialog
                    NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                    dialog.setupConfirmationDialog("Delete Confirmation",
                            "Do you want to delete this note?");
                    dialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                /**
                                 * Log the current user out
                                 *
                                 * @param dialog dialog
                                 * @param which  which button is clicked
                                 */
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Show progress dialog
                                    NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(ViewEditNoteActivity.this);
                                    progressDialog.setUpDialog("Just a moment...",
                                            "Please wait while we deleting your note.");
                                    progressDialog.show();

                                    // Delete note
                                    deleteNote(progressDialog);
                                }
                            });
                    dialog.create().show();
                } else if (savedNote.isDeleted()){
                    // Create new confirmation dialog
                    NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                    dialog.setupConfirmationDialog("Delete Confirmation",
                            "Are you sure to permanently delete this note?");
                    dialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                /**
                                 * Log the current user out
                                 *
                                 * @param dialog dialog
                                 * @param which  which button is clicked
                                 */
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Show progress dialog
                                    NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(ViewEditNoteActivity.this);
                                    progressDialog.setUpDialog("Just a moment...",
                                            "Please wait while we deleting your note.");
                                    progressDialog.show();

                                    // Delete note
                                    deleteNote(progressDialog);
                                }
                            });
                    dialog.create().show();
                }
                return true;
            }
        });

        // Show popup menu
        popup.show();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check if note is unsaved
     *
     * @param noteTitle   note title
     * @param noteContent note content
     * @return true if note is unsaved
     * false if note is already saved
     */
    private boolean isUnsaved(String noteTitle, String noteContent) {
        // If note title or content differs from saved note, return true
        return !noteTitle.equals(savedNoteTitle) || !noteContent.equals(savedNoteContent);
    }

    /**
     * Save note (for both delete and update)
     *
     * @param isBackPressed detect if note is saved from return action (unsaved changes)
     */
    private void saveNote(boolean isBackPressed) {
        /* Firebase instances */
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(ViewEditNoteActivity.this);

        // If note already exists, proceed to update existing note
        if (savedNote.getId() != null) {
            // Show progress dialog
            progressDialog.setUpDialog("Just a moment...",
                    "Please wait while we update your note.");
            progressDialog.show();

            // Note document reference
            DocumentReference noteRef = db.collection("users").document(firebaseUser.getUid())
                    .collection("notebooks").document(savedNote.getNotebook().getId())
                    .collection("notes").document(savedNote.getId());

            // Get current timestamp
            Timestamp updatedDate = Timestamp.now();

            // Set note data
            savedNote.setTitle(TextUtils.isEmpty(savedNote.getTitle()) ? "Untitled Note" : savedNote.getTitle());  // Parse title to Untitled if it's empty
            savedNote.setUpdatedDate(updatedDate);

            // Update note
            noteRef.update(
                    "title", savedNote.getTitle(),
                    "content", savedNote.getContent(),
                    "updatedDate", updatedDate)
                    // If update note successful
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();

                            /* Get TextInputLayout Views */
                            TextInputLayout txtInputNoteTitle = findViewById(R.id.txtInputNoteTitle);
                            TextInputLayout txtInputNoteContent = findViewById(R.id.txtInputNoteContent);

                            // Update saved data
                            savedNoteTitle = savedNote.getTitle();
                            savedNoteContent = savedNote.getContent();

                            // Set saved data to display to user
                            txtInputNoteTitle.getEditText().setText(savedNoteTitle);
                            txtInputNoteContent.getEditText().setText(savedNoteContent);

                            // Show dialog depends on back button pressed or not
                            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                            if (isBackPressed) {
                                dialog.setUpReturnOKDialog("Update Successful",
                                        "Note has been updated.", ViewEditNoteActivity.this);
                            } else {
                                dialog.setupOKDialog("Update Successful",
                                        "Note has been updated.");
                            }
                            dialog.create().show();
                        }
                    })
                    // If update note failed
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.e("error", e.getMessage(), e);

                            progressDialog.dismiss();

                            // Show dialog to notify user of error
                            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                            dialog.setupOKDialog("Update Failed",
                                    "Something went wrong while we update your note. Please try again!");
                            dialog.create().show();
                        }
                    });
        } else {
            // If note doesn't exist, proceed to create new note

            // Show progress dialog
            progressDialog.setUpDialog("Just a moment...",
                    "Please wait while we add your note.");
            progressDialog.show();

            // "notes" collection reference
            CollectionReference notesCollectionRef = db.collection("users").document(firebaseUser.getUid())
                    .collection("notebooks").document(firebaseUser.getUid())
                    .collection("notes");

            // Parse title to Untitled if it's empty
            savedNote.setTitle(TextUtils.isEmpty(savedNote.getTitle()) ? "Untitled Note" : savedNote.getTitle());

            // Add note to database
            notesCollectionRef.add(savedNote)
                    // If add note successful
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressDialog.dismiss();

                            // Get the added note
                            documentReference.get()
                                    // If get added note successful
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            // Parse the new Note document snapshot to object
                                            Note newNote = documentSnapshot.toObject(Note.class);

                                            // Set ID for current saved note in activity
                                            savedNote.setId(newNote.getId());

                                            /* Get TextInputLayout Views */
                                            TextInputLayout txtInputNoteTitle = findViewById(R.id.txtInputNoteTitle);
                                            TextInputLayout txtInputNoteContent = findViewById(R.id.txtInputNoteContent);

                                            // Update saved data
                                            savedNoteTitle = newNote.getTitle();
                                            savedNoteContent = newNote.getContent();

                                            // Set saved data to display to user
                                            txtInputNoteTitle.getEditText().setText(savedNoteTitle);
                                            txtInputNoteContent.getEditText().setText(savedNoteContent);

                                            // Show dialog depends on back button pressed or not
                                            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                                            if (isBackPressed) {
                                                dialog.setUpReturnOKDialog("Add Successful",
                                                        "Note has been added.", ViewEditNoteActivity.this);
                                            } else {
                                                dialog.setupOKDialog("Add Successful",
                                                        "Note has been added.");
                                            }
                                            dialog.create().show();
                                        }
                                    })
                                    // If get added note failed
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Log.e("error", e.getMessage(), e);

                                            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                                            dialog.setupOKDialog("Add Failed",
                                                    "Something went wrong while we add your note. Please try again!");
                                            dialog.create().show();
                                        }
                                    });
                        }
                    })
                    // If add note failure
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.e("error", e.getMessage(), e);

                            progressDialog.dismiss();

                            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                            dialog.setupOKDialog("Add Failed",
                                    "Something went wrong while we add your note. Please try again!");
                            dialog.create().show();
                        }
                    });
        }
    }

    /**
     * Delete note from database
     *
     * @param progressDialog progress dialog
     */
    private void deleteNote(NoteAppProgressDialog progressDialog) {
        /* Firebase instances */
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        if (!savedNote.isDeleted()) {
            // Delete note (move to trash)
            db.collection("users").document(firebaseUser.getUid())
                    .collection("notebooks").document(savedNote.getNotebook().getId())
                    .collection("notes").document(savedNote.getId())
                    .update("deleted", true)
                    // If delete successful
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("delete note", "onSuccess: Removed list item");
                            progressDialog.dismiss();

                            // Show dialog to notify user
                            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                            dialog.setUpReturnOKDialog("Delete Successful",
                                    "Note has been deleted.", ViewEditNoteActivity.this);
                            dialog.create().show();
                        }
                    })
                    // If delete failed
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("delete note", "onFailure: " + e.getLocalizedMessage());
                            progressDialog.dismiss();

                            // Show dialog to notify user
                            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                            dialog.setupOKDialog("Delete Failed",
                                    "Something went wrong while we delete your note. Please try again!");
                            dialog.create().show();
                        }
                    });
        } else {
            // Delete note (move to trash)
            db.collection("users").document(firebaseUser.getUid())
                    .collection("notebooks").document(savedNote.getNotebook().getId())
                    .collection("notes").document(savedNote.getId())
                    .delete()
                    // If delete successful
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("delete note", "onSuccess: Removed list item");
                            progressDialog.dismiss();

                            // Show dialog to notify user
                            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                            dialog.setUpReturnOKDialog("Delete Successful",
                                    "Note has been permanently deleted.", ViewEditNoteActivity.this);
                            dialog.create().show();
                        }
                    })
                    // If delete failed
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("delete note", "onFailure: " + e.getLocalizedMessage());
                            progressDialog.dismiss();

                            // Show dialog to notify user
                            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                            dialog.setupOKDialog("Delete Failed",
                                    "Something went wrong while we delete your note. Please try again!");
                            dialog.create().show();
                        }
                    });
        }
    }

    /**
     * Override default on back behaviour to check if user doesn't save new content
     */
    @Override
    public void onBackPressed() {
        /* Get display data */
        String noteTitle = txtInputNoteTitle.getEditText().getText().toString();
        String noteContent = txtInputNoteContent.getEditText().getText().toString();

        // If display data is unsaved
        if (isUnsaved(noteTitle, noteContent)) {
            // Display a dialog to ask if user want to save
            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);

            dialog.setupReturnConfirmationDialog("Unsaved Changes",
                    "Do you want to save changes to note?", this);
            dialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        /**
                         * Log the current user out
                         * @param dialog    dialog
                         * @param which     which button has been selected
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            savedNote.setTitle(noteTitle);
                            savedNote.setContent(noteContent);

                            // Save the note
                            saveNote(true);
                        }
                    });

            dialog.create().show();
        } else {
            // Finish activity
            finish();
        }
    }
}