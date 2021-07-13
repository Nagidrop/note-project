package com.group6.noteapp.controller;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;

public class ViewNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        Note note = (Note) getIntent().getSerializableExtra("note");
//        Note note = (Note) getIntent().getExtras().getSerializable("note");

        TextInputLayout txtInputNoteTitle = findViewById(R.id.txtInputNoteTitle);
        TextInputLayout txtInputNoteContent = findViewById(R.id.txtInputNoteContent);
        MaterialTextView txtNotebook = findViewById(R.id.txtNotebook);

        txtInputNoteTitle.getEditText().setText(note.getTitle());
        txtInputNoteContent.getEditText().setText(note.getContent());
        txtNotebook.setText(note.getNotebook().getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_undo:
                Toast.makeText(this, "Click Undo Icon.", Toast.LENGTH_SHORT).show();

                break;

            case R.id.nav_redo:
                Toast.makeText(this, "Click Redo Icon.", Toast.LENGTH_SHORT).show();

                break;

            case R.id.nav_menu:
                Toast.makeText(this, "Click Menu Icon.", Toast.LENGTH_SHORT).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}