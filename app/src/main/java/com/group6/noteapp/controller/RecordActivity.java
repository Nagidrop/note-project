package com.group6.noteapp.controller;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordActivity extends AppCompatActivity {
    private  String fileName = null;

    private  final String LOG_TAG = "Record_log";
    private MediaRecorder recorder ;
    private MediaPlayer player = null;
    private TextView textTimeRecord;
    private Button btnRecording, btnPlaying,btnSaveRecord;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private TextInputLayout recordName;
    private FirebaseAuth firebaseAuth;
    private long timeLeft=6000;

    private FirebaseStorage storage;
    private FirebaseUser user;
    private FirebaseFirestore db;

    //Get current date and time
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.getDefault());
    Date now = new Date();

    private Chronometer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        btnRecording=(Button) findViewById(R.id.btnRecording);
        timer = findViewById(R.id.record_timer);
        btnPlaying=(Button)findViewById(R.id.btnPlay);
        textTimeRecord=(TextView)findViewById(R.id.textTimeRecord);
        btnSaveRecord=(Button) findViewById(R.id.btnSaveRecord);
        recordName=findViewById(R.id.textInputRecordName);

        progressDialog = new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        fileName = getExternalCacheDir().getAbsolutePath();
        btnPlaying.setEnabled(false);
        btnSaveRecord.setEnabled(false);
        recordName.setEnabled(false);

        try{
            btnRecording.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_DOWN){

                        startRecording();
                        btnRecording.setText("Stop");

                    }else if(event.getAction()==MotionEvent.ACTION_UP){
                        stopRecording();
                        btnRecording.setText("Record");
                    }
                    return false;
                }
            });
        }catch(Exception e) {
           e.printStackTrace();
        }

        try {
            btnPlaying.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        btnPlaying.setText("Stop");
                        startPlaying();
//                        if(btnPlaying.getText().equals("Stop")){
//                            stopPlaying();
//                            btnPlaying.setText("Play");
//                        }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        try {

            btnSaveRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadAudio(Uri.fromFile(new File(fileName)));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
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
        btnSaveRecord.setEnabled(true);
        recordName.setEnabled(false);
    }


    private void stopRecording() {
        //Stop Timer, very obvious
        timer.stop();

        //Stop media recorder and set it to null for further use to record new audio
        recorder.stop();
        recorder.release();
        recorder = null;
        btnPlaying.setEnabled(true);
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
                                            progressDialog.dismiss();
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
    private void startPlaying() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
            textTimeRecord.setText(String.valueOf(player.getDuration()));
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }
    private void stopPlaying() {
        //Stop Timer, very obvious
        timer.stop();
        player.stop();
        player.release();
        player = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }
    public void getTimeRecord(){

    }
}
