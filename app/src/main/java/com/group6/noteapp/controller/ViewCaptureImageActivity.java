/*
 * Group 06 SE1402
 */

package com.group6.noteapp.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.group6.noteapp.util.ValidationUtils;
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteAppProgressDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Activity for viewing captured image
 */
public class ViewCaptureImageActivity extends AppCompatActivity {

    private static final String TAG = "ViewCaptureImage"; // Tag for logging

    private FirebaseStorage storage;                // Firebase storage
    private FirebaseUser user;                      // Firebase user
    private FirebaseFirestore db;                   // Firestore
    private TextInputLayout imageName;              // Image name
    private NoteAppProgressDialog progressDialog;   // Progress dialog
    private long lastClickTime;                     // User's last click time (to prevent multiple clicks)


    /**
     * Handle on Activity create to show capture image
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_capture_image);

        // Get database, auth, current user instance
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        // Get view component
        ShapeableImageView imgReview = findViewById(R.id.imgReview);
        MaterialButton btnSave = findViewById(R.id.btnSaveImage);
        imageName = findViewById(R.id.textInputImageName);

        // Get image path
        String path = getIntent().getExtras().getString("path");

        // Get file from image path
        File image = new File(path);

        // if image exists get angle from image's exif
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

            // Show image to Review image
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            imgReview.setImageBitmap(RotateBitmap(bitmap, angle));

            // Handle button save to save picture to database and storage
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    // Multiple click prevention, using threshold of 1000 ms
                    if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                        // Show message to notify user of fast clicks
                        Toast.makeText(ViewCaptureImageActivity.this,
                                "You are tapping too fast. Please wait.", Toast.LENGTH_SHORT).show();

                        return;
                    }

                    // Update last click time
                    lastClickTime = SystemClock.elapsedRealtime();

                    // Get image name
                    String name = imageName.getEditText().getText().toString();

                    // Check if name is empty
                    if(ValidationUtils.validateFileName(name) == 1){
                        // Set error to edit text
                        imageName.setErrorEnabled(true);
                        imageName.setError("Please enter Image Name!");
                    }else {
                        // save image to storage
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

    /**
     * Rotate bitmap base on angle
     * @param source bitmap source
     * @param angle  angle
     * @return rotated bitmap
     */
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap
                .createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Save Image to storage
     * @param uri       image URI
     * @param imageName image name
     */
    public void saveImageToStorage(Uri uri, String imageName){

        // Get storage reference
        StorageReference storageRef = storage.getReference();
        // set image reference
        StorageReference imageReference = storageRef.child(user.getUid()+"/images/"+ uri.getLastPathSegment());
        // Create upload Task
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
                // Get user document from database
                DocumentReference userInfoDoc = db.collection("users").document(user.getUid());

                userInfoDoc.get().addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override public void onComplete(
                                    @NonNull @NotNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    // if document exist
                                    if(document.exists()){
                                        Notebook defaultNotebook = new Notebook();
                                        defaultNotebook.setTitle(Constants.FIRST_NOTEBOOK_NAME);

                                        // Add new note to collection
                                        DocumentReference userDefNotebookDoc = userInfoDoc.collection("notebooks")
                                                .document(user.getUid());
                                            Note imageNote = new Note();
                                            imageNote.setType(2);
                                            imageNote.setTitle(imageName);
                                            imageNote.setContent(uri.getLastPathSegment());
                                            imageNote.setUpdatedDate(Timestamp.now());
                                            CollectionReference userDefNoteCollection = userDefNotebookDoc.collection("notes");
                                            userDefNoteCollection.add(imageNote).addOnSuccessListener(
                                                    new OnSuccessListener<DocumentReference>() {
                                                        /**
                                                         * Add success then go to main activity
                                                         * @param documentReference document reference
                                                         */
                                                        @Override public void onSuccess(
                                                                DocumentReference documentReference) {
                                                            Toast.makeText(ViewCaptureImageActivity.this, "Add Note Successful!!", Toast.LENGTH_SHORT).show();
                                                            toMainActivity();
                                                        }
                                                    }).addOnFailureListener(
                                                    new OnFailureListener() {
                                                        /**
                                                         * On failure display a error dialog
                                                         * @param e exception
                                                         */
                                                        @Override public void onFailure(
                                                                @NonNull @NotNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Log.e("ViewCaptureImage", "Error adding new note", e);

                                                            NoteAppDialog dialog = new NoteAppDialog(ViewCaptureImageActivity.this);
                                                            dialog.setupOKDialog("Add Failed",
                                                                    "An error occurred when add new note. Please try again!");
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

    /**
     * To main activity
     */
    private void toMainActivity() {
        progressDialog.dismiss();

        Intent intent = new Intent(ViewCaptureImageActivity.this, MainActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }
}