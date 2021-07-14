package com.group6.noteapp.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.model.Notebook;
import com.group6.noteapp.util.Constants;
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteAppProgressDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class ViewCaptureImageActivity extends AppCompatActivity {

    private static final String TAG = "ViewCaptureImage"; // Tag for logging

    private ShapeableImageView imgReview;
    private MaterialButton btnSave;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private TextInputLayout imageName;
    private NoteAppProgressDialog progressDialog;

    String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_capture_image);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        storage = FirebaseStorage.getInstance();
        imgReview = (ShapeableImageView) findViewById(R.id.imgReview);
        btnSave = (MaterialButton) findViewById(R.id.btnSaveImage);
        imageName = findViewById(R.id.textInputImageName);
        path = getIntent().getExtras().getString("path");
        File image = new File(path);

        if (image.exists()) {
            int angle = 0;
            try {
                ExifInterface ei = new ExifInterface(image.getPath());
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

            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            imgReview.setImageBitmap(RotateBitmap(bitmap, angle));

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    String name = imageName.getEditText().getText().toString();
                    if(TextUtils.isEmpty(name)){
                        imageName.setErrorEnabled(true);
                        imageName.setError("Please enter Image Name!");
                    }else {
                        progressDialog = new NoteAppProgressDialog(ViewCaptureImageActivity.this);
                        progressDialog.setUpDialog("Just a moment...",
                                "Please wait while we saving your note.");
                        progressDialog.show();
                        saveImageToStorage(Uri.fromFile(image),name);
                    }
                }
            });
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap
                .createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void saveImageToStorage(Uri uri, String imageName){
        StorageReference storageRef = storage.getReference();
        StorageReference imageReference = storageRef.child(user.getUid()+"/images/"+ uri.getLastPathSegment());
        UploadTask uploadTask = imageReference.putFile(uri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ViewCaptureImageActivity.this, "Image Upload Unsuccessful!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                DocumentReference userInfoDoc = db.collection("users").document(user.getUid());

                userInfoDoc.get().addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override public void onComplete(
                                    @NonNull @NotNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()){
                                        Notebook defaultNotebook = new Notebook();
                                        defaultNotebook.setTitle(Constants.FIRST_NOTEBOOK_NAME);

                                        DocumentReference userDefNotebookDoc = userInfoDoc.collection("notebooks")
                                                .document(user.getUid());
                                            Note imageNote = new Note();
                                            imageNote.setTitle(imageName);
                                            imageNote.setContent(uri.getLastPathSegment());
                                            imageNote.setUpdatedDate(Timestamp.now());
                                            CollectionReference userDefNoteCollection = userDefNotebookDoc.collection("notes");
                                            userDefNoteCollection.add(imageNote).addOnSuccessListener(
                                                    new OnSuccessListener<DocumentReference>() {
                                                        @Override public void onSuccess(
                                                                DocumentReference documentReference) {
                                                            Toast.makeText(ViewCaptureImageActivity.this, "Add Note Successful!!", Toast.LENGTH_SHORT).show();
                                                            toMainActivity();
                                                        }
                                                    }).addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override public void onFailure(
                                                                @NonNull @NotNull Exception e) {
                                                            Log.e("ViewCaptureImage", "Error adding new note", e);

                                                            NoteAppDialog dialog = new NoteAppDialog(ViewCaptureImageActivity.this);
                                                            dialog.setupOKDialog("Add Failed",
                                                                    "An error occurred during your account setup. Please try register again!");
                                                            dialog.create().show();
                                                        }
                                                    });
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
            }
        });

    }

    private void toMainActivity() {
        progressDialog.dismiss();
        Intent intent = new Intent(ViewCaptureImageActivity.this, MainActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }
}