package com.group6.noteapp.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.login.LoginManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.group6.noteapp.R;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver MyReceiver = null;
    private FirebaseAuth firebaseAuth;
    private MenuItem previousItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth= FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

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

                        if(item.getItemId() == R.id.menu_logout){
                            signOut();
                            return false;
                        }


                        // Uncheck previous item if it exists
//                        Menu navigationViewMenu = navigationView.getMenu();
//                        if (uncheckedItem != null) {
//                            Log.d("Check Item", String.valueOf(uncheckedItem.getItemId()));
//                            uncheckedItem.setChecked(false);
//                        }

                        // Check selected item
                        if(previousItem!= null){
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
     * Logout method
     */
    public void signOut() {
        firebaseAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
