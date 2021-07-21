/*
 * Group 06 SE1402
 */

package com.group6.noteapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group6.noteapp.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Activity for setting up camera and shooting picture
 */
public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;                                        // Camera preview view
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;   // Camera provider
    private ImageCapture imageCapture;                                      // Image Capture use case
    private Executor executor;                                              // Executor
    private FirebaseUser user;                                              // firebase current user
    private long lastClickTime;                                             // User's last click time (to prevent multiple clicks)

    /**
     * Set up camera and button onclick action
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Set executor
        executor = Executors.newSingleThreadExecutor();

        // Get auth instance and current user
        // Firebase auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Get view components
        previewView = findViewById(R.id.cameraPreview);
        // fab Take image
        FloatingActionButton fabTakeImage = findViewById(R.id.fabTakeImage);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // Add camera provideer
        cameraProviderFuture.addListener(new Runnable() {

            /**
             * Bind camera use case
             */
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));


        // set take Image onclick listener
        fabTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // Multiple click prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    // Show message to notify user of fast clicks
                    Toast.makeText(CameraActivity.this,
                            "You are tapping too fast. Please wait.", Toast.LENGTH_SHORT).show();

                    return;
                }

                // Update last click time
                lastClickTime = SystemClock.elapsedRealtime();


                captureImage(); // Capture image
            }
        });
    }

    /**
     * Bind camera use case to camera provider
     * @param cameraProvider camera provider
     */
    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {

        // Build analysis use case
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

        // Build capture use case
        imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();

        imageAnalysis
                .setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy image) {
                        image.close();
                    }
                });

        // Build preview
        Preview preview = new Preview.Builder().build();

        // Select back camera
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // bind use case to camera provider
        cameraProvider
                .bindToLifecycle(this, cameraSelector, imageCapture, imageAnalysis,
                        preview);
    }

    /**
     * Capture image
     */
    private void captureImage() {
        // Create file on local
        File file = new File(
                getBatchDirectoryName() + user.getUid() +
                        System.currentTimeMillis() + ".jpg");

        // Build output file option
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();

        // take picture
        imageCapture
                .takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {

                    /**
                     * Image save success then toast success and to ViewCaptureImageActivity
                     * @param outputFileResults output file results
                     */
                    @Override public void onImageSaved(
                            @NonNull @NotNull ImageCapture.OutputFileResults outputFileResults) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(CameraActivity.this, "Image Saved!!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(CameraActivity.this, ViewCaptureImageActivity.class);
                                intent.putExtra("path", file.getAbsolutePath());
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull @NotNull ImageCaptureException exception) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(CameraActivity.this, "Some error occurred!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        exception.printStackTrace();
                    }
                });
    }


    /**
     * get local directory name
     * @return directory name
     */
    public String getBatchDirectoryName() {
        String imagePath = Environment.getExternalStorageDirectory().toString() + "/images/";
        File dir = new File(imagePath);

        if (!dir.exists() && !dir.mkdirs()) {

        }

        return imagePath;
    }
}