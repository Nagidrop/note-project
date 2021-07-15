package com.group6.noteapp.controller;

import android.content.DialogInterface;
import android.os.Bundle;
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
    private Note note;                              // Note object with data to add or update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_note);

        // If user add new note
        if (getIntent().getParcelableExtra("note") == null) {
            note = new Note();
        } else {
            // If user update existing note
            note = getIntent().getParcelableExtra("note");
        }

        // Check if user add new note and then add new notebook
        if (note.getNotebook() == null) {
            // Set notebook title as first notebook
            Notebook notebook = new Notebook();
            notebook.setTitle("My First Notebook");
            note.setNotebook(notebook);
        }

        /* Set saved data as current data */
        savedNoteTitle = note.getTitle();
        savedNoteContent = note.getContent();

        /* Get TextInputLayout, Text Views and Button */
        txtInputNoteTitle = findViewById(R.id.txtInputNoteTitle);
        txtInputNoteContent = findViewById(R.id.txtInputNoteContent);
        MaterialTextView txtNotebook = findViewById(R.id.txtNotebook);
        FloatingActionButton fabSave = findViewById(R.id.fabSave);

        /* Set data to display to user */
        txtInputNoteTitle.getEditText().setText(savedNoteTitle);
        txtInputNoteContent.getEditText().setText(savedNoteContent);
        txtNotebook.setText(note.getNotebook().getTitle());

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
                    note.setTitle(noteTitle);
                    note.setContent(noteContent);

                    // Proceed to save note
                    saveNote(note, false);
                } else {
                    // If content is saved (not changed), display toast message to user
                    Toast.makeText(ViewEditNoteActivity.this, "Note not changed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_menu:
                // Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ViewEditNoteActivity.this, findViewById(R.id.nav_menu));

                // Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.note_popup_menu, popup.getMenu());

                // Registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                        dialog.setupConfirmationDialog("Delete Confirmation",
                                "Do you want to delete this note?");
                        dialog.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    /**
                                     * Log the current user out
                                     * @param dialog
                                     * @param which
                                     */
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(ViewEditNoteActivity.this);
                                        progressDialog.setUpDialog("Just a moment...",
                                                "Please wait while we deleting your note.");
                                        progressDialog.show();

                                        deleteNote(progressDialog, note);
                                    }
                                });
                        dialog.create().show();

                        return true;
                    }
                });

                popup.show(); //showing popup menu
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isUnsaved(String noteTitle, String noteContent) {
        if (!noteTitle.equals(savedNoteTitle) || !noteContent.equals(savedNoteContent)) {
            return true;
        }

        return false;
    }

    private void saveNote(Note note, boolean isBackPressed) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(ViewEditNoteActivity.this);

        if (note.getId() != null) {
            progressDialog.setUpDialog("Just a moment...",
                    "Please wait while we update your note.");
            progressDialog.show();

            DocumentReference noteRef = db.collection("users").document(firebaseUser.getUid())
                    .collection("notebooks").document(note.getNotebook().getId())
                    .collection("notes").document(note.getId());

            Timestamp updatedDate = Timestamp.now();

            note.setTitle(TextUtils.isEmpty(note.getTitle()) ? "Untitled Note" : note.getTitle());
            note.setUpdatedDate(updatedDate);

            noteRef.update(
                    "title", note.getTitle(),
                    "content", note.getContent(),
                    "updatedDate", updatedDate)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();

                            TextInputLayout txtInputNoteTitle = findViewById(R.id.txtInputNoteTitle);
                            TextInputLayout txtInputNoteContent = findViewById(R.id.txtInputNoteContent);

                            savedNoteTitle = note.getTitle();
                            savedNoteContent = note.getContent();

                            txtInputNoteTitle.getEditText().setText(savedNoteTitle);
                            txtInputNoteContent.getEditText().setText(savedNoteContent);

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
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.e("error", e.getMessage(), e);

                            progressDialog.dismiss();

                            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                            dialog.setupOKDialog("Update Failed",
                                    "Something went wrong while we update your note. Please try again!");
                            dialog.create().show();
                        }
                    });
        } else {
            progressDialog.setUpDialog("Just a moment...",
                    "Please wait while we add your note.");
            progressDialog.show();

            CollectionReference notesCollectionRef = db.collection("users").document(firebaseUser.getUid())
                    .collection("notebooks").document(firebaseUser.getUid())
                    .collection("notes");

            note.setTitle(TextUtils.isEmpty(note.getTitle()) ? "Untitled Note" : note.getTitle());

            notesCollectionRef.add(note)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressDialog.dismiss();

                            documentReference.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Note newNote = documentSnapshot.toObject(Note.class);

                                            TextInputLayout txtInputNoteTitle = findViewById(R.id.txtInputNoteTitle);
                                            TextInputLayout txtInputNoteContent = findViewById(R.id.txtInputNoteContent);

                                            savedNoteTitle = newNote.getTitle();
                                            savedNoteContent = newNote.getContent();

                                            txtInputNoteTitle.getEditText().setText(savedNoteTitle);
                                            txtInputNoteContent.getEditText().setText(savedNoteContent);

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
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Log.e("error", e.getMessage(), e);
                                        }
                                    });
                        }
                    })
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

    private void deleteNote(NoteAppProgressDialog progressDialog, Note note){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(firebaseUser.getUid())
                .collection("notebooks").document(note.getNotebook().getId())
                .collection("notes").document(note.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("delete note", "onSuccess: Removed list item");
                        progressDialog.dismiss();

                        Toast.makeText(ViewEditNoteActivity.this, "Delete note success", Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("delete note", "onFailure: " + e.getLocalizedMessage());

                        // Show dialog to notify user
                        NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                        dialog.setupOKDialog("Delete Failed",
                                "Something went wrong while we delete your note. Please try again!");
                        dialog.create().show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        String noteTitle = txtInputNoteTitle.getEditText().getText().toString();
        String noteContent = txtInputNoteContent.getEditText().getText().toString();

        if (isUnsaved(noteTitle, noteContent)) {
            NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);

            dialog.setupReturnConfirmationDialog("Unsaved Changes",
                    "Do you want to save changes to note?", this);
            dialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        /**
                         * Log the current user out
                         * @param dialog
                         * @param which
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            note.setTitle(noteTitle);
                            note.setContent(noteContent);
                            saveNote(note, true);
                        }
                    });

            dialog.create().show();
        } else {
            finish();
        }
    }
}