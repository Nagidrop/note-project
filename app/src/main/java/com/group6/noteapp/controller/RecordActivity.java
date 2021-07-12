package com.group6.noteapp.controller;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {
    private  String fileName = null;

    private  final String LOG_TAG = "Record_log";
    private MediaRecorder recorder ;
    private MediaPlayer player = null;
    private TextView textTimeRecord;
    private Button btnRecording, btnPlaying,btnSaveRecord, btnStop,btnReset;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private TextInputLayout recordName;
    private FirebaseAuth firebaseAuth;
    private SeekBar seekBar;
    private Handler threadHandler = new Handler();

    private FirebaseStorage storage;
    private FirebaseUser user;
    private FirebaseFirestore db;


    private Chronometer timer;


    //Get current date and time
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.getDefault());
    Date now = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        timer = findViewById(R.id.record_timer);

        recordName=findViewById(R.id.textInputRecordName);
        textTimeRecord=findViewById(R.id.textTimeRecord);
        progressDialog = new ProgressDialog(this);
        seekBar=findViewById(R.id.seekBar);


        storageReference= FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        fileName = getExternalCacheDir().getAbsolutePath();

        btnPlaying=(Button)findViewById(R.id.btnPlay);
        btnSaveRecord=(Button)findViewById(R.id.btnSaveRecord);
        btnRecording=(Button)findViewById(R.id.btnRecording);
        btnReset=(Button)findViewById(R.id.btnReset);
        btnStop=(Button)findViewById(R.id.btnStop);

        btnPlaying.setOnClickListener(this);
        btnSaveRecord.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnRecording.setOnClickListener(this);

        staticButtonDefault();

    }

    public boolean staticButtonDefault() {
        btnPlaying.setEnabled(false);
        btnSaveRecord.setEnabled(false);
        btnStop.setEnabled(false);
        btnReset.setEnabled(false);
        btnRecording.setEnabled(true);
        recordName.setEnabled(false);
        seekBar.setEnabled(false);
        btnPlaying.setText("Play");
    return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPlay:
                if(btnPlaying.getText().equals("Play")){
                    btnPlaying.setText("Stop");
                    startPlaying();
                }else if(btnPlaying.getText().equals("Stop")){
                    btnPlaying.setText("Play");
                    stopPlaying();
                }
                break;
            case R.id.btnSaveRecord:
                    uploadAudio(Uri.fromFile(new File(fileName)));
                break;
            case R.id.btnStop:
                    stopRecording();
                    btnReset.setEnabled(true);
                    recordName.setEnabled(true);
                    btnPlaying.setEnabled(true);
                    btnSaveRecord.setEnabled(true);
                    btnRecording.setEnabled(false);
                    btnStop.setEnabled(false);
                break;
            case R.id.btnReset:
                staticButtonDefault();
                resetAll();
                break;
            case R.id.btnRecording:
                startRecording();
                btnStop.setEnabled(true);
                btnRecording.setEnabled(false);
                break;
        }
    }
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


    private void stopRecording() {
        //Stop Timer, very obvious
        timer.stop();

        //Stop media recorder and set it to null for further use to record new audio
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;

    }


    private void uploadAudio(Uri uri) {
        progressDialog.setMessage("Uploading Record ...");
        progressDialog.show();

        String userID=firebaseAuth.getUid();
        StorageReference filepath = storageReference.child("Record").child(userID).child("Recording_" + formatter.format(now)+ ".3gp");
        UploadTask uploadTask =filepath.putFile(uri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RecordActivity.this, "Record Upload Unsuccessful!!", Toast.LENGTH_SHORT).show();
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
                                    progressDialog.dismiss();
                                    DocumentSnapshot document = task.getResult();
                                    Log.e(LOG_TAG, "get failed with ", task.getException());
                                    if(document.exists()){

                                        Notebook defaultNotebook = new Notebook();
                                        defaultNotebook.setTitle(Constants.FIRST_NOTEBOOK_NAME);
                                        DocumentReference userDefNotebookDoc = userInfoDoc.collection("notebooks")
                                                .document(defaultNotebook.getTitle());

                                        String name = recordName.getEditText().getText().toString();
                                        if(TextUtils.isEmpty(name)){
                                            recordName.setErrorEnabled(true);
                                            recordName.setError("Please enter Record Name!");

                                        }else{

                                            Note recordNote = new Note();
                                            recordNote.setTitle(name);
                                            recordNote.setContent(uri.getLastPathSegment());
                                            CollectionReference userDefNoteCollection = userDefNotebookDoc.collection("notes");
                                            userDefNoteCollection.add(recordNote);

                                            Toast.makeText(RecordActivity.this, "Record Upload Successful!!", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(RecordActivity.this, LoginActivity.class);
                                            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

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
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startPlaying() {

        player = new MediaPlayer();
        try {

            player.setDataSource(fileName);
            player.prepare();
            // The duration in milliseconds
            int duration = player.getDuration();

            int currentPosition = player.getCurrentPosition();
            if(currentPosition== 0)  {
                seekBar.setMax(duration);
                String maxTimeString = millisecondsToString(duration);
                timer.setText(maxTimeString);
            } else if(currentPosition== duration)  {
                // Resets the MediaPlayer to its uninitialized state.
                player.reset();
            }
            player.start();
            // Create a thread to update position of SeekBar.
            UpdateSeekBarThread updateSeekBarThread= new UpdateSeekBarThread();
            threadHandler.postDelayed(updateSeekBarThread,50);
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

    }
    private String millisecondsToString(int milliseconds)  {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds =  TimeUnit.MILLISECONDS.toSeconds((long) milliseconds) ;
        return minutes + ":"+ seconds;
    }
    private void stopPlaying() {
        //Stop Timer, very obvious
        timer.stop();
        if(player.isPlaying()){
            player.stop();

        }

    }

    // Thread to Update position for SeekBar.
    class UpdateSeekBarThread implements Runnable {

        public void run()  {
            int currentPosition = player.getCurrentPosition();
            String currentPositionStr = millisecondsToString(currentPosition);
            textTimeRecord.setText(currentPositionStr);
            seekBar.setProgress(currentPosition);
            // Delay thread 50 milisecond.
            threadHandler.postDelayed(this, 50);
        }
    }
    private void resetAll(){

    }


}
