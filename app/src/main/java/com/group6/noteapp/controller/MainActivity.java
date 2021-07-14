package com.group6.noteapp.controller;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 696;
    private final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private BroadcastReceiver MyReceiver = null;

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

        setContentView(R.layout.activity_main);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

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
        txtNavFullName.setText(Html.fromHtml(getString(R.string.header_title, firebaseUser.getDisplayName())));
        txtNavEmail.setText(getString(R.string.header_text, firebaseUser.getEmail()));

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
        // Text Note
        // -----------------------------
        fabNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createNoteIntent = new Intent(MainActivity.this, ViewEditNoteActivity.class);

                startActivity(createNoteIntent);
            }
        });

        // -----------------------------
        // Image Note
        // -----------------------------
        fabCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (hasPermissions(MainActivity.this, PERMISSIONS)) {
                    fabMenuOnClick();
                    enableRecord();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_CODE);
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
        NoteAppDialog dialog = new NoteAppDialog(this);

        dialog.setupConfirmationDialog("Logout Confirmation",
                "Are you sure you want to log out?");
        dialog.setPositiveButton("Yes",
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
    public void onBackPressed() {
        NoteAppDialog dialog = new NoteAppDialog(this);
        dialog.setupConfirmationDialog("Exit Confirmation",
                "Are you sure you want to exit Note App?");
        dialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    /**
                     * Log the current user out
                     * @param dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                });

        dialog.create().show();
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(
                intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
