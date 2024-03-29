/*
 * Group 06 SE1402
 */

package com.group6.noteapp.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.util.ValidationUtils;
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteAppProgressDialog;

import org.jetbrains.annotations.NotNull;

/**
 * Activity for viewing image details
 */
public class ViewImageDetails extends AppCompatActivity {
    private static final String TAG = "ViewImageDetails"; // Tag for logging

    private FirebaseStorage storage;                    // Firebase storage
    private FirebaseUser user;                          // Firebase user
    private FirebaseFirestore db;                       // Firebase Firestore
    private ShapeableImageView viewImage;               // Image view
    private TextInputLayout imageName;                  // Image name edit text
    private NoteAppProgressDialog progressDialog;       // Progress dialog
    private long lastClickTime;                         // User's last click time (to prevent multiple clicks)

    /**
     * Initialize activity
     *
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_details);

        // Get Firebase instance
        db = FirebaseFirestore.getInstance();
        // Firebase auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        // Get components from view
        viewImage = findViewById(R.id.imgView);
        // button change name
        MaterialButton btnChangeName = findViewById(R.id.btnChangeImageName);
        imageName = findViewById(R.id.textInputChangeImageName);


        // Get Note from intent extra
        Note note = getIntent().getParcelableExtra("note");

        // Handle button change name on click
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // Multiple click prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    // Show message to notify user of fast clicks
                    Toast.makeText(ViewImageDetails.this,
                            "You are tapping too fast. Please wait.", Toast.LENGTH_SHORT).show();

                    return;
                }

                // Update last click time
                lastClickTime = SystemClock.elapsedRealtime();

                changeImageName(note);
            }
        });

        // Show progress dialog for loading image
        progressDialog = new NoteAppProgressDialog(ViewImageDetails.this);
        progressDialog.setUpDialog("Just a moment...",
                "Please wait while we loading your image.");
        progressDialog.show();

        // load image
        loadImage(note);

        // set Image name to edit text
        imageName.getEditText().setText(note.getTitle());

    }

    /**
     * Change current image name and save to database
     */
    private void changeImageName(Note note) {
        // Get image name from edit text
        String name = imageName.getEditText().getText().toString();

        // validate image name
        if (ValidationUtils.validateFileName(name) == 1) {
            // Show error on edit text
            imageName.setErrorEnabled(true);
            imageName.setError("Please enter Image Name!");
        } else {
            // Update date note title( Image Name)
            note.setTitle(name);

            DocumentReference noteRef = db.collection("users").document(user.getUid())
                    .collection("notebooks").document(note.getNotebook().getId())
                    .collection("notes").document(note.getId());

            noteRef.update("title", note.getTitle(),
                    "updatedDate", Timestamp.now());

            // To main activity
            Intent intent = new Intent(ViewImageDetails.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * Load image from storage and firebase
     *
     * @param note note
     */
    private void loadImage(Note note) {

        // get notebook reference
        DocumentReference notebookDocRef = db.collection("users").document(user.getUid())
                .collection("notebooks").document(note.getNotebook().getId()).collection("notes")
                .document(note.getId());

        notebookDocRef.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override public void onComplete(
                            @NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // if document exists
                            // load image to image view
                            if (document.exists()) {
                                Note note = document.toObject(Note.class);
                                StorageReference storageRef = storage.getReference();
                                StorageReference pathReference = storageRef
                                        .child(user.getUid() + "/images/" + note.getContent());
                                // Set captured image in note
                                pathReference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(ViewImageDetails.this).load(uri)
                                                        .into(viewImage);
                                                progressDialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                progressDialog.dismiss();
                                                NoteAppDialog dialog = new NoteAppDialog(ViewImageDetails.this);
                                                dialog.setupOKDialog("Load Failed",
                                                        "An error occurred when loading your image. Please try again!");
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


    /**
     * Rotate bitmap base on angle
     *
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
}