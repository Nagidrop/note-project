package com.group6.noteapp.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteAppProgressDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class ViewImageDetails extends AppCompatActivity {
    private static final String TAG = "ViewImageDetails"; // Tag for logging

    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private MaterialButton btnChangeName;
    private ShapeableImageView viewImage;
    private TextInputLayout imageName;
    private NoteAppProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_details);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        storage = FirebaseStorage.getInstance();
        viewImage = (ShapeableImageView) findViewById(R.id.imgView);
        btnChangeName = (MaterialButton) findViewById(R.id.btnChangeImageName);
        imageName = findViewById(R.id.textInputChangeImageName);

        Note note = (Note) getIntent().getParcelableExtra("note");

        btnChangeName = findViewById(R.id.btnChangeImageName);

        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                changeImageName(note);
            }
        });

        progressDialog = new NoteAppProgressDialog(ViewImageDetails.this);
        progressDialog.setUpDialog("Just a moment...",
                "Please wait while we loading your image.");
        progressDialog.show();

        loadImage(note);

        imageName.getEditText().setText(note.getTitle());

    }

    /**
     * Change current image name and save to database
     */
    private void changeImageName(Note note) {
        String name = imageName.getEditText().getText().toString();

        if(TextUtils.isEmpty(name)){
            imageName.setErrorEnabled(true);
            imageName.setError("Please enter Image Name!");
        }else{

            note.setTitle(name);

            DocumentReference noteRef = db.collection("users").document(user.getUid())
                    .collection("notebooks").document(note.getNotebook().getId())
                    .collection("notes").document(note.getId());

            noteRef.update("title", note.getTitle(),
                    "updatedDate", Timestamp.now());
            Intent intent = new Intent(ViewImageDetails.this, LoginActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void loadImage(Note note) {
        DocumentReference notebookDocRef = db.collection("users").document(user.getUid())
                .collection("notebooks").document(note.getNotebook().getId()).collection("notes")
                .document(note.getId());

        notebookDocRef.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override public void onComplete(
                            @NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Note note = document.toObject(Note.class);
                                StorageReference storageRef = storage.getReference();
                                StorageReference pathReference = storageRef
                                        .child(user.getUid() + "/images/" + note.getContent());
                                try {
                                    File tempImage = File.createTempFile("image", "jpg");
                                    pathReference.getFile(tempImage).addOnSuccessListener(
                                            new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(
                                                        FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    int angle = 0;
                                                    try {
                                                        ExifInterface ei = new ExifInterface(
                                                                tempImage.getPath());
                                                        int orientation = ei.getAttributeInt(
                                                                ExifInterface.TAG_ORIENTATION,
                                                                ExifInterface.ORIENTATION_NORMAL);

                                                        switch (orientation) {
                                                            case ExifInterface.ORIENTATION_ROTATE_90:
                                                                angle = 90;
                                                                break;
                                                            case ExifInterface.ORIENTATION_ROTATE_180:
                                                                angle = 180;
                                                                break;
                                                            case ExifInterface.ORIENTATION_ROTATE_270:
                                                                angle = 270;
                                                                break;
                                                        }
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    Bitmap bitmap = BitmapFactory.decodeFile(
                                                            tempImage.getAbsolutePath());
                                                    viewImage.setImageBitmap(
                                                            RotateBitmap(bitmap, angle));
                                                    progressDialog.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                            progressDialog.dismiss();
                                            NoteAppDialog dialog = new NoteAppDialog(ViewImageDetails.this);
                                            dialog.setupOKDialog("Load Failed",
                                                    "An error occurred when loading your image. Please try again!");
                                            dialog.create().show();
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap
                .createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}