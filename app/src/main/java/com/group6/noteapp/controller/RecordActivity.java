/*
 * Group 06 SE1402
 */

package com.group6.noteapp.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Activity for recording audio
 */
public class RecordActivity extends AppCompatActivity implements View.OnClickListener {
    private String fileName = null;

    /* Text Input Layouts and Button */
    private String fileName = null;                  // File name path
    private final String LOG_TAG = "Record_log";
    private MediaRecorder recorder;                 // Media Recorder
    private MediaPlayer player = null;               // Media player
    private TextView textTimeRecord;                 // Textview Time
    private Button btnRecording, btnPlaying, btnSaveRecord, btnStop, btnReset; // Button Record, Play , Save, Reset
    private StorageReference storageReference;       // Storage Reference
    private ProgressDialog progressDialog, progressDialog2; // Progress Dialog
    private TextInputLayout recordName;              // Text input record name
    private SeekBar seekBar;                         // Seekbar
    private final Handler threadHandler = new Handler();   // Handler

    /* Firebase instances */
    private FirebaseAuth firebaseAuth;  // Firebase Auth
    private FirebaseUser user;          // Firebase User
    private FirebaseFirestore db;       // Firebase FireStore

    private Chronometer timer;          // Chronometer

    //Get current date and time
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.getDefault());
    Date now = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        // Get view component
        timer = findViewById(R.id.record_timer);
        recordName = findViewById(R.id.textInputRecordName);
        textTimeRecord = findViewById(R.id.textTimeRecord);
        progressDialog = new ProgressDialog(this);
        progressDialog2 = new ProgressDialog(this);
        seekBar = findViewById(R.id.seekBar);

        // Get database, auth, current user instance
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        // Get file path
        fileName = getExternalCacheDir().getAbsolutePath();
        // Get view component
        btnPlaying = findViewById(R.id.btnPlay);
        btnSaveRecord = findViewById(R.id.btnSaveRecord);
        btnRecording = findViewById(R.id.btnRecording);
        btnReset = findViewById(R.id.btnReset);
        btnStop = findViewById(R.id.btnStop);

        //Get action click button
        btnPlaying.setOnClickListener(this);
        btnSaveRecord.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnRecording.setOnClickListener(this);

        statusButtonDefault();

    }

    /**
     * Set status button default
     * @return status
     */
    public boolean statusButtonDefault() {
        btnPlaying.setEnabled(false);
        btnSaveRecord.setEnabled(false);
        btnStop.setEnabled(false);
        btnReset.setEnabled(false);
        btnRecording.setEnabled(true);
        recordName.setEnabled(false);
        seekBar.setEnabled(false);
        btnPlaying.setText(getString(R.string.play));
        return false;
    }

    /**
     * Action click button
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay://Button Play

                if (btnPlaying.getText().equals("Play")) {
                    btnPlaying.setText(getString(R.string.replay));
                    startPlaying();
                } else if (btnPlaying.getText().equals("Replay")) {
                    btnPlaying.setText(getString(R.string.replay));
                    stopPlaying();
                    startPlaying();
                }
                break;

            case R.id.btnSaveRecord://Button save
                uploadAudio(Uri.fromFile(new File(fileName)));
                break;

            case R.id.btnStop:  //Button Stop
                stopRecording();
                btnReset.setEnabled(true);
                recordName.setEnabled(true);
                btnPlaying.setEnabled(true);
                btnSaveRecord.setEnabled(true);
                btnRecording.setEnabled(false);
                btnStop.setEnabled(false);
                break;

            case R.id.btnReset: //Button reset
                statusButtonDefault();
                resetAll();
                break;

            case R.id.btnRecording: //Button Record
                progressDialog2.setMessage("Record start ...");
                progressDialog2.show();
                //Set time dialog close
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog2.dismiss();
                    }
                }).start();

                startRecording();
                btnStop.setEnabled(true);
                btnRecording.setEnabled(false);
                break;

        }
    }

    /**
     * Start record
     */
    private void startRecording() {
        //Start timer from 0
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        fileName += "Recording_" + formatter.format(now) + ".3gp";

        //Setup Media Recorder for recording
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Start Recording
        recorder.start();
    }

    //Stop record
    private void stopRecording() {
        //Stop Timer, very obvious
        timer.stop();

        //Stop media recorder and set it to null for further use to record new audio
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;
    }

    /**
     * Update record to storage
     * @param uri audio URI
     */
    private void uploadAudio(Uri uri) {
        //Show dialog uploading record
        progressDialog.setMessage("Uploading Record ...");
        progressDialog.show();
        //Get ID user
        String userID = firebaseAuth.getUid();
        //Get Storage Reference
        StorageReference filepath = storageReference.child(userID).child("Record").child(uri.getLastPathSegment());
        // Create upload Task
        UploadTask uploadTask = filepath.putFile(uri);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RecordActivity.this, "Record Upload Unsuccessful!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get user document from database
                DocumentReference userInfoDoc = db.collection("users").document(user.getUid());
                userInfoDoc.get().addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(
                                    @NonNull @NotNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    DocumentSnapshot document = task.getResult();
                                    Log.e(LOG_TAG, "get failed with ", task.getException());
                                    // if document exist
                                    if (document.exists()) {
                                        Notebook defaultNotebook = new Notebook();
                                        defaultNotebook.setTitle(Constants.FIRST_NOTEBOOK_NAME);
                                        // Add new note to collection
                                        DocumentReference userDefNotebookDoc = userInfoDoc.collection("notebooks")
                                                .document(user.getUid());
                                        String name = recordName.getEditText().getText().toString();
                                        if (TextUtils.isEmpty(name)) {
                                            recordName.setErrorEnabled(true);
                                            recordName.setError("Please enter Record Name!");
                                        } else {
                                            Note recordNote = new Note();
                                            recordNote.setType(3);
                                            recordNote.setTitle(name);
                                            recordNote.setContent(uri.getLastPathSegment());
                                            recordNote.setUpdatedDate(Timestamp.now());
                                            CollectionReference userDefNoteCollection = userDefNotebookDoc.collection("notes");
                                            userDefNoteCollection.add(recordNote).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                /**
                                                 * Add success then go to main activity
                                                 * @param documentReference document reference
                                                 */
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(RecordActivity.this, "Record Upload Successful!!", Toast.LENGTH_SHORT).show();
                                                    toMainActivity();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                /**
                                                 * On failure display a error dialog
                                                 * @param e exception
                                                 */
                                                @Override
                                                public void onFailure(@NonNull @NotNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Log.e("ViewRecord", "Error adding new note", e);

                                                }
                                            });


                                        }
                                    }
                                } else {
                                    Log.e(LOG_TAG, "get failed with ", task.getException());
                                }
                            }
                        });
            }
        });
    }

    /**
     * Play record
     */
    private void startPlaying() {
        textTimeRecord.setEnabled(true);
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            // The duration in milliseconds
            int duration = player.getDuration();
            int currentPosition = player.getCurrentPosition();
            if (currentPosition == 0) {
                seekBar.setMax(duration);
                String maxTimeString = millisecondsToString(duration);
                timer.setText(maxTimeString);
            } else if (currentPosition == duration) {
                // Resets the MediaPlayer to its uninitialized state.
                player.reset();
            }
            player.start();
            // Create a thread to update position of SeekBar.
            UpdateSeekBarThread updateSeekBarThread = new UpdateSeekBarThread();
            threadHandler.postDelayed(updateSeekBarThread, 50);

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    /**
     * Milliseconds To String
     * @param milliseconds milliseconds
     * @return string presentation of milliseconds
     */
    private String millisecondsToString(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        return minutes + ":" + seconds;
    }

    /**
     * Stop record playing
     */
    private void stopPlaying() {
        //Stop Timer, very obvious
        timer.stop();
        if (player.isPlaying()) {
            player.stop();
        }
    }

    /**
     * Thread to Update position for SeekBar.
     */
    class UpdateSeekBarThread implements Runnable {
        public void run() {
            int currentPosition = player.getCurrentPosition();
            String currentPositionStr = millisecondsToString(currentPosition);
            textTimeRecord.setText(currentPositionStr);
            seekBar.setProgress(currentPosition);
            // Delay thread 50 milisecond.
            threadHandler.postDelayed(this, 50);
        }
    }

    /**
     * Reset button
     */
    private void resetAll() {
        timer.setText(getString(R.string.initial_timer));
        textTimeRecord.setEnabled(false);
        textTimeRecord.setText(getString(R.string.time));
    }

    /**
     * To main activity
     */
    private void toMainActivity() {
        progressDialog.dismiss();
        Intent intent = new Intent(RecordActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }
}
