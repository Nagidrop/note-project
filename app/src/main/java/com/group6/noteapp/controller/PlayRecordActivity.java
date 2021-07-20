package com.group6.noteapp.controller;

import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.group6.noteapp.util.ValidationUtils;
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteAppProgressDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayRecordActivity extends AppCompatActivity {

    private static final String TAG = "ViewRecord"; // Tag for loggin

    private Handler handler = new Handler();    //Handler
    private MediaPlayer mediaPlayer;            //MediaPlayer
    //Declare variable
    private boolean isPlaying = false;
    long minutes = 0;
    long seconds = 0;

    TextInputLayout recordNameChange;           //Text name change
    TextView fileNameTxtView;                   //Text file Name
    TextView fileLengthTxtView;                 //Length file record
    TextView fileCurrentProgress;               //File Current Progress
    SeekBar seekBar;                            //Seekbar
    FloatingActionButton floatingActionButton;  //FloatingActionButton
    Button btnSaveNote;                         //Button save

    Note note;                                  //Note object
    private String filename=null;               //File  path

    /* Firebase instances */
    private FirebaseStorage storage;          //Firebase Storage
    private FirebaseAuth mAuth;               //Firebase Auth
    private FirebaseUser user;                //Firebase User
    private FirebaseFirestore db;              //Firebase Firestore

    private NoteAppProgressDialog progressDialog;  //ProgressDialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_play_item);
        // Get view component
        fileNameTxtView=findViewById(R.id.file_name_text_view);
        fileLengthTxtView=findViewById(R.id.file_length_text_view);
        fileCurrentProgress=findViewById(R.id.current_progress_text_view);
        btnSaveNote=findViewById(R.id.btnSave);
        recordNameChange=findViewById(R.id.textInputRecordName);

        seekBar=findViewById(R.id.seekbar);
        floatingActionButton=findViewById(R.id.fab_play);
        // Get database, auth, current user instance
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        //Get note Intent
        note=getIntent().getParcelableExtra("note");
        //Get dialog
        progressDialog = new NoteAppProgressDialog(PlayRecordActivity.this);
        progressDialog.setUpDialog("Just a moment...",
                "Please wait while we loading your record.");
        progressDialog.show();
        //Load record
        loadRecord(note);
        //Get file path
        filename=getExternalCacheDir().getAbsolutePath();
        //Set file name to textview
        fileNameTxtView.setText(note.getTitle());
        recordNameChange.getEditText().setText(note.getTitle());
        //Set value Seekbar
        setSeekbarValues();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onPlay(isPlaying);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlaying = !isPlaying;

            }
        });
        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName();
            }
        });



    }
    // On play
    private void onPlay(boolean isPlaying) throws IOException {
        if(!isPlaying)
        {
            if(mediaPlayer == null) {
                startPlaying();
            }else {
                floatingActionButton.setImageResource(R.drawable.ic_media_pause);
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                mediaPlayer.start();
                setSeekbarValues();
                updateSeekbar();

            }
        }
        else
        {
            pausePlaying();
        }
    }
    //Pause record
    private void pausePlaying()
    {
        floatingActionButton.setImageResource(R.drawable.ic_media_play);
        handler.removeCallbacks(mRunnable);
        mediaPlayer.pause();
    }
    //Star record
    private void startPlaying() throws IOException {
        floatingActionButton.setImageResource(R.drawable.ic_media_pause);
        //Setup mediaPlayer
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setDataSource(filename);
        mediaPlayer.prepare();
        seekBar.setMax(mediaPlayer.getDuration());
        //Set time length record
        minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration());
        seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) - TimeUnit.MINUTES.toSeconds(minutes);
        fileLengthTxtView.setText(String.format("%02d:%02d",minutes,seconds));
        //Action button
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        //Action button
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });
        //Update seekbar
        updateSeekbar();
        PlayRecordActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }
    //Set Seekbar Values
    private void setSeekbarValues()
    {
        ColorFilter colorFilter = new LightingColorFilter(getResources().getColor(R.color.purple_500),
                getResources().getColor(R.color.purple_500));

        seekBar.getProgressDrawable().setColorFilter(colorFilter);
        seekBar.getThumb().setColorFilter(colorFilter);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser)
                {
                    mediaPlayer.seekTo(progress);
                    handler.removeCallbacks(mRunnable);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);

                    fileCurrentProgress.setText(String.format("%02d:%02d",minutes,seconds));
                    updateSeekbar();

                }
                else if(mediaPlayer == null && fromUser)
                {
                    try {
                        prepareMediaPlayerFromPoint(progress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateSeekbar();

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    //Prepare MediaPlayer From Point
    private void prepareMediaPlayerFromPoint(int progress) throws IOException {
        //Setup mediaPlayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(filename);
        mediaPlayer.prepare();
        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.seekTo(progress);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();

            }
        });
    }
    //Stop record
    private void stopPlaying()
    {
        //Setup mediaPlayer
        floatingActionButton.setImageResource(R.drawable.ic_media_play);
        handler.removeCallbacks(mRunnable);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        //Set value max of seekbar
        seekBar.setProgress(seekBar.getMax());
        isPlaying = !isPlaying;
        fileCurrentProgress.setText(fileLengthTxtView.getText());
        seekBar.setProgress(seekBar.getMax());
    }

    //Runnable
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mediaPlayer!=null)
            {
                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition)
                        - TimeUnit.MINUTES.toSeconds(minutes);

                fileCurrentProgress.setText(String.format("%02d:%02d",minutes,seconds));
                updateSeekbar();

            }
        }
    };
    // Create a thread to update position of SeekBar.
    private void updateSeekbar()
    {
        handler.postDelayed(mRunnable,1000);
    }
    //Load record
    private void loadRecord (Note note){
        // Get user document from database
        DocumentReference notebookDocRef = db.collection("users").document(user.getUid())
                .collection("notebooks").document(note.getNotebook().getId()).collection("notes")
                .document(note.getId());
        notebookDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot document = task.getResult();
                    // if document exist
                    if(document.exists()){
                        Note note = document.toObject(Note.class);
                        StorageReference storageRef = storage.getReference();
                        StorageReference pathReference = storageRef
                                .child(user.getUid() + "/Record/" +note.getContent());
                        try {
                            //Create file
                            File tempRecord = File.createTempFile("record", ".3gp");
                            pathReference.getFile(tempRecord).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    //Set path file to filename
                                    filename=tempRecord.getAbsolutePath();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    // Handle any errors
                                    progressDialog.dismiss();
                                    NoteAppDialog dialog = new NoteAppDialog(PlayRecordActivity.this);
                                    dialog.setupOKDialog("Load Failed",
                                            "An error occurred when loading your record. Please try again!");
                                    dialog.create().show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            }
        });
    }

    private void changeName(){

        /* Firebase instances */
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // Get image name from edit text
        String name = recordNameChange.getEditText().getText().toString();

        // validate image name
        if(ValidationUtils.validateFileName(name) == 1){
            // Show error on edit text
            recordNameChange.setErrorEnabled(true);
            recordNameChange.setError("Please enter record Name!");
        }else{
            // Update date note title(record Name)
            note.setTitle(name);

            DocumentReference noteRef = db.collection("users").document(user.getUid())
                    .collection("notebooks").document(note.getNotebook().getId())
                    .collection("notes").document(note.getId());

            noteRef.update("title", note.getTitle(),
                    "updatedDate", Timestamp.now());

            // To main activity
            Intent intent = new Intent(PlayRecordActivity.this, MainActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}

