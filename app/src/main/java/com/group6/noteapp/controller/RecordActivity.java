package com.group6.noteapp.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group6.noteapp.R;

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
    private Button btnRecording;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    String firebaseAuth;

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

        progressDialog = new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference();
        fileName = getExternalCacheDir().getAbsolutePath();

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
        recorder.release();
        recorder = null;
        uploadAudio();
    }

    private void uploadAudio() {
        progressDialog.setMessage("Uploading Record ...");
        progressDialog.show();

        firebaseAuth=FirebaseAuth.getInstance().getUid();
        String userID=firebaseAuth;
        StorageReference filepath = storageReference.child("Record").child(userID).child("Recording_" + formatter.format(now) + ".3gp");
        Uri uri = Uri.fromFile(new File(fileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }
    private void stopPlaying() {
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
}
