/*
 * Group 06 SE1402
 */

package com.group6.noteapp.controller;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group6.noteapp.R;
import com.group6.noteapp.view.NoteAppDialog;

import org.jetbrains.annotations.NotNull;

/**
 * Main Activity
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 696;
    private final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private final String MANAGE_EXTERNAL_STORAGE = Manifest.permission.MANAGE_EXTERNAL_STORAGE;


    private MenuItem previousItem; // Menu previous clicked itemm
    private Animation rotateClose; //Rotate close animation
    private Animation rotateOpen; // Rotate open animation
    private Animation fromBottom; // From bottom animation
    private Animation toBottom; // To bottom animation
    private FloatingActionButton fabMenu; // Fab menu button
    private FloatingActionButton fabNote; // Fab note button
    private FloatingActionButton fabRecord; // Fab record button
    private FloatingActionButton fabCapture; // Fab capture button
    private boolean clicked; // fabMenu clicked state
    private long lastClickTime; // User's last click time (to prevent multiple clicks)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            checkManageStoragePermissions();
        }

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
            @Override
            public void onClick(View v) {
                fabMenuOnClick();
            }
        });

        // close fab menu
        clicked = true;
        fabMenuOnClick();

        // -----------------------------
        // Navigation drawer
        //------------------------------
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        View navHeader = navigationView.getHeaderView(0);
        MaterialTextView txtNavFullName = navHeader.findViewById(R.id.txtNavFullname);
        MaterialTextView txtNavEmail = navHeader.findViewById(R.id.txtNavEmail);
        ShapeableImageView imgProfilePicture = navHeader.findViewById(R.id.imgProfilePicture);

        setSupportActionBar(topAppBar);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profileRef = storage.getReference()
                .child(firebaseUser.getUid() + "/images/profilePicture.png");
        profileRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(MainActivity.this).load(uri).into(imgProfilePicture);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.e("error", e.getMessage());
                    }
                });

        txtNavFullName.setText(
                Html.fromHtml(getString(R.string.header_title, firebaseUser.getDisplayName())));

        if (TextUtils.isEmpty(firebaseUser.getEmail())) {
            txtNavEmail.setText(getString(R.string.header_text, ""));
        } else {
            txtNavEmail.setText(getString(R.string.header_text, firebaseUser.getEmail()));
        }


        // Set navigation icon click event to show navigation drawer
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        // Set navigation item selected
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                        // Multiple click prevention, using threshold of 1000 ms
                        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                            // Show message to notify user of fast clicks
                            Toast.makeText(MainActivity.this,
                                    "You are tapping too fast. Please wait.", Toast.LENGTH_SHORT).show();

                            return false;
                        }

                        // Update last click time
                        lastClickTime = SystemClock.elapsedRealtime();

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

                        Fragment fragment;
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        /*
                         *  Navigate fragment base on menu item
                         */
                        switch (item.getItemId()) {
                            case R.id.menu_all_notes:
                                fragment = new HomeFragment();
                                break;

                            case R.id.menu_trash:
                                fragment = new TrashFragment();
                                break;

                            default:
                                fragment = new HomeFragment();
                                break;
                        }

                        fragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, fragment)
                                .commit();

                        // close fab menu
                        if (clicked) {
                            fabMenuOnClick();
                        }
                        return true;
                    }
                });

        previousItem = navigationView.getMenu().getItem(0).getSubMenu().getItem(0);
        previousItem.setChecked(true);

        // -----------------------------
        // Text Note
        // -----------------------------
        fabNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Multiple click prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    // Show message to notify user of fast clicks
                    Toast.makeText(MainActivity.this, "You are tapping too fast. Please wait.", Toast.LENGTH_SHORT).show();

                    return;
                }

                // Update last click time
                lastClickTime = SystemClock.elapsedRealtime();

                Intent createNoteIntent = new Intent(MainActivity.this, ViewEditNoteActivity.class);

                /* Get recyclerview and its adapter */
                RecyclerView rvNote = findViewById(R.id.recyclerView);
                NoteAdapter adapter = (NoteAdapter) rvNote.getAdapter();

                // Put notebook to adapter which is used to create new note
                createNoteIntent.putExtra("notebook", adapter.getNotebook());

                startActivity(createNoteIntent);
            }
        });

        // -----------------------------
        // Image Note
        // -----------------------------
        fabCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Multiple click prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    // Show message to notify user of fast clicks
                    Toast.makeText(MainActivity.this, "You are tapping too fast. Please wait.", Toast.LENGTH_SHORT).show();

                    return;
                }

                // Update last click time
                lastClickTime = SystemClock.elapsedRealtime();

                if (hasPermissions(MainActivity.this, PERMISSIONS)) {
                    fabMenuOnClick();
                    enableCamera();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_CODE);
                }
            }
        });

        // -----------------------------
        // Audio Note
        // -----------------------------
        fabRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Multiple click prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    // Show message to notify user of fast clicks
                    Toast.makeText(MainActivity.this, "You are tapping too fast. Please wait.", Toast.LENGTH_SHORT).show();

                    return;
                }

                // Update last click time
                lastClickTime = SystemClock.elapsedRealtime();

                if (hasPermissions(MainActivity.this, PERMISSIONS)) {
                    fabMenuOnClick();
                    enableRecord();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_CODE);
                }
            }
        });
    }

    /**
     * Navigate to camera activity
     */
    private void enableCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    /**
     * Navigate to record activity
     */
    private void enableRecord() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);

    }

    /**
     * Function check request permissions
     *
     * @param context     application context
     * @param permissions array of permission
     * @return allow state of permission
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Log out confirmation
     */
    public void logOutConfirmation() {
        NoteAppDialog dialog = new NoteAppDialog(this);

        dialog.setupConfirmationDialog("Logout Confirmation",
                "Are you sure you want to log out?");
        dialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    /**
                     * Log the current user out
                     * @param dialog dialog
                     * @param which  which button has been clicked
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                });

        dialog.create().show();
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
     * @param clicked   status of click
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

    /**
     * Override on back and show dialog for confirmation
     */
    @Override
    public void onBackPressed() {
        NoteAppDialog dialog = new NoteAppDialog(this);
        dialog.setupConfirmationDialog("Exit Confirmation",
                "Are you sure you want to exit Note App?");
        dialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    /**
                     * Log the current user out
                     * @param dialog    dialog
                     * @param which     which button has been clicked
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                });

        dialog.create().show();
    }

    /**
     * Log the user out
     */
    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(
                intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * check manage External storage permission for android R
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void checkManageStoragePermissions() {
        if (Environment.isExternalStorageManager()) {
        } else {
            //request for the permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }
}
