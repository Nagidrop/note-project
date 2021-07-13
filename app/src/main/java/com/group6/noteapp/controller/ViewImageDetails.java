package com.group6.noteapp.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.model.Notebook;
import com.group6.noteapp.util.Constants;

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
        String noteId = getIntent().getExtras().getString("noteId");

        loadImage(noteId);

    }

    private void loadImage(String noteId) {
        DocumentReference notebookDocRef = db.collection("users").document(user.getUid())
                .collection("notebooks").document(Constants.FIRST_NOTEBOOK_NAME).collection("notes")
                .document(noteId);

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
                                                        ExifInterface ei = new ExifInterface(tempImage.getPath());
                                                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
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

                                                    Bitmap bitmap = BitmapFactory.decodeFile(tempImage.getAbsolutePath());
                                                    viewImage.setImageBitmap(RotateBitmap(bitmap, angle));
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
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