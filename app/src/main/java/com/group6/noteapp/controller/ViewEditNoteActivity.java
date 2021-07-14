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

public class ViewEditNoteActivity extends AppCompatActivity {
    private String savedNoteTitle;
    private String savedNoteContent;
    private TextInputLayout txtInputNoteTitle;
    private TextInputLayout txtInputNoteContent;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_note);

        if (getIntent().getParcelableExtra("note") == null) {
            note = new Note();
        } else {
            note = getIntent().getParcelableExtra("note");
        }

        if (note.getNotebook() == null) {
            Notebook notebook = new Notebook();
            notebook.setTitle("My First Notebook");
            note.setNotebook(notebook);
        }

        savedNoteTitle = note.getTitle();
        savedNoteContent = note.getContent();
        txtInputNoteTitle = findViewById(R.id.txtInputNoteTitle);
        txtInputNoteContent = findViewById(R.id.txtInputNoteContent);

        MaterialTextView txtNotebook = findViewById(R.id.txtNotebook);
        FloatingActionButton fabSave = findViewById(R.id.fabSave);

        txtInputNoteTitle.getEditText().setText(savedNoteTitle);
        txtInputNoteContent.getEditText().setText(savedNoteContent);
        txtNotebook.setText(note.getNotebook().getTitle());

        MaterialToolbar toolbar = findViewById(R.id.noteToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTitle = txtInputNoteTitle.getEditText().getText().toString();
                String noteContent = txtInputNoteContent.getEditText().getText().toString();

                if (isUnsaved(noteTitle, noteContent)) {
                    note.setTitle(noteTitle);
                    note.setContent(noteContent);
                    saveNote(note, false);
                } else {
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