package com.group6.noteapp.controller;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.login.LoginManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.group6.noteapp.R;
import com.group6.noteapp.view.NoteAppDialog;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 696;
    private static final int STORAGE_READ_REQUEST_CODE = 697;
    private static final int STORAGE_WRITE_REQUEST_CODE = 698;


    private BroadcastReceiver MyReceiver = null;
    private FirebaseAuth firebaseAuth;
    private MenuItem previousItem;
    private Animation rotateClose;
    private Animation rotateOpen;
    private Animation fromBottom;
    private Animation toBottom;
    private FloatingActionButton fabMenu;
    private FloatingActionButton fabNote;
    private FloatingActionButton fabRecord;
    private FloatingActionButton fabCapture;
    private boolean clicked; // fabMenu clicked state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        // -----------------------------
        // Floating button
        // -----------------------------
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        fabMenu = findViewById(R.id.fabMenu);
        fabNote = findViewById(R.id.fabNote);
        fabRecord = findViewById(R.id.fabRecord);
        fabCapture = findViewById(R.id.fabCapture);

        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                fabMenuOnClick();
            }
        });

        // -----------------------------
        // Navigation drawer
        //------------------------------
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        // Set navigation icon click event to show navigation drawer
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                drawerLayout.open();
            }
        });

        // Set navigation item selected
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                        drawerLayout.close();

                        if (item.getItemId() == R.id.menu_logout) {
                            logOutConfirmation();
                            return false;
                        }

                        // Check selected item
                        if (previousItem != null) {
                            previousItem.setChecked(false);
                        }
                        item.setChecked(true);
                        previousItem = item;
                        topAppBar.setTitle(item.getTitle());
                        return true;
                    }
                });

        previousItem = navigationView.getMenu().getItem(0).getSubMenu().getItem(0);
        previousItem.setChecked(true);

        // -----------------------------
        // Capture Image
        // -----------------------------
        fabCapture.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                        STORAGE_READ_REQUEST_CODE) &&
                        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                STORAGE_WRITE_REQUEST_CODE) &&
                        checkPermission(Manifest.permission.CAMERA, CAMERA_REQUEST_CODE)){
                    fabMenuOnClick();
                    enableCamera();
                }
            }
        });

//        if (!isNetworkAvailable()) {
//            new AlertDialog.Builder(this)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setTitle("Internet Connection Alert")
//                    .setMessage("Please Check Your Internet Connection")
//                    .setPositiveButton("Close", (dialogInterface, i) -> finish()).show();
//        } else if (isNetworkAvailable()) {
//            Toast.makeText(MainActivity.this,
//                    "Welcome", Toast.LENGTH_LONG).show();
//        }
    }


    private void enableCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    // Function to check and request permission.
    public boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // Requesting the permission
            ActivityCompat
                    .requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }

        return false;
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager
                        .getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Log out confirmation
     */
    public void logOutConfirmation() {
        NoteAppDialog noteAppDialog = new NoteAppDialog(this);
        noteAppDialog.setupConfirmationDialog("Logout Confirmation",
                "Are you sure you want to log out?");
        noteAppDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    /**
                     * Log the current user out
                     * @param dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                });

        noteAppDialog.show();
    }

    /**
     * handle fabMenu button onclick action
     */
    public void fabMenuOnClick() {
        setVisibility(clicked);
        setAnimation(clicked);
        clicked = !clicked;
    }

    /**
     * Set Animation of floating action button
     *
     * @param clicked fabMenu click state
     */
    private void setAnimation(boolean clicked) {
        if (!clicked) {
            fabMenu.startAnimation(rotateOpen);
            fabNote.startAnimation(fromBottom);
            fabCapture.startAnimation(fromBottom);
            fabRecord.startAnimation(fromBottom);
        } else {
            fabMenu.startAnimation(rotateClose);
            fabNote.startAnimation(toBottom);
            fabCapture.startAnimation(toBottom);
            fabRecord.startAnimation(toBottom);
        }
    }

    /**
     * Set visibility of fabRecord, fabNote, fabCapture
     *
     * @param clicked
     */
    private void setVisibility(boolean clicked) {
        if (!clicked) {
            fabNote.setVisibility(View.VISIBLE);
            fabRecord.setVisibility(View.VISIBLE);
            fabCapture.setVisibility(View.VISIBLE);
        } else {
            fabNote.setVisibility(View.INVISIBLE);
            fabRecord.setVisibility(View.INVISIBLE);
            fabCapture.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed(){
        NoteAppDialog noteAppDialog = new NoteAppDialog(this);
        noteAppDialog.setupConfirmationDialog("Exit Confirmation",
                "Are you sure you want to exit Note App?");
        noteAppDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    /**
                     * Log the current user out
                     * @param dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                });

        noteAppDialog.show();
    }

    private void logOut(){
        firebaseAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(
                intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
