package com.group6.noteapp.controller;

import android.content.DialogInterface;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteAppProgressDialog;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ViewEditNoteActivity extends AppCompatActivity {
    private String savedNoteTitle;
    private String savedNoteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_note);

        Note note = (Note) getIntent().getParcelableExtra("note");

        savedNoteTitle = note.getTitle();
        savedNoteContent = note.getContent();

        TextInputLayout txtInputNoteTitle = findViewById(R.id.txtInputNoteTitle);
        TextInputLayout txtInputNoteContent = findViewById(R.id.txtInputNoteContent);
        MaterialTextView txtNotebook = findViewById(R.id.txtNotebook);
        FloatingActionButton fabSave = findViewById(R.id.fabSave);

        txtInputNoteTitle.getEditText().setText(note.getTitle());
        txtInputNoteContent.getEditText().setText(note.getContent());
        txtNotebook.setText(note.getNotebook().getTitle());

        MaterialToolbar toolbar = findViewById(R.id.noteToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTitle = txtInputNoteTitle.getEditText().getText().toString();
                String noteContent = txtInputNoteContent.getEditText().getText().toString();

                if (isUnsaved(noteTitle, noteContent)){
                    NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);

                    dialog.setupConfirmationDialog("Unsaved Changes",
                            "Do you want to save changes to note?");
                    dialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                /**
                                 * Log the current user out
                                 * @param dialog
                                 * @param which
                                 */
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveNote(note, noteTitle, noteContent);
                                }
                            });

                    dialog.create().show();
                } else {
                    onBackPressed();
                }
            }
        });
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTitle = txtInputNoteTitle.getEditText().getText().toString();
                String noteContent = txtInputNoteContent.getEditText().getText().toString();

                if (isUnsaved(noteTitle, noteContent)){
                    saveNote(note, noteTitle, noteContent);
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
            case R.id.nav_undo:
//                Toast.makeText(this, "Click Undo Icon.", Toast.LENGTH_SHORT).show();

                break;

            case R.id.nav_redo:
//                Toast.makeText(this, "Click Redo Icon.", Toast.LENGTH_SHORT).show();

                break;

            case R.id.nav_menu:
//                Toast.makeText(this, "Click Menu Icon.", Toast.LENGTH_SHORT).show();

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

    private void saveNote(Note note, String noteTitle, String noteContent){
        NoteAppProgressDialog progressDialog = new NoteAppProgressDialog(ViewEditNoteActivity.this);
        progressDialog.setUpDialog("Just a moment...",
                "Please wait while we update your note.");
        progressDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DocumentReference noteRef = db.collection("users").document(firebaseUser.getUid())
                .collection("notebooks").document(note.getNotebook().getId())
                .collection("notes").document(note.getId());

        HashMap<String, String> noteDataMap = new HashMap<>();
        noteDataMap.put("title", noteTitle);
        noteDataMap.put("content", noteContent);
        noteDataMap.put("updatedDate", Timestamp.now().toString());

        noteRef.update(
                "title", noteTitle,
                "content", noteContent,
                "updatedDate", Timestamp.now())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();

                        savedNoteTitle = noteTitle;
                        savedNoteContent = noteContent;

                        NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                        dialog.setupOKDialog("Update Successful",
                                "Note has been updated.");
                        dialog.create().show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.e("error", e.getMessage(), e);

                        NoteAppDialog dialog = new NoteAppDialog(ViewEditNoteActivity.this);
                        dialog.setupOKDialog("Update Failed",
                                "Something went wrong while we update your note. Please try again!");
                        dialog.create().show();
                    }
                });
    }
}