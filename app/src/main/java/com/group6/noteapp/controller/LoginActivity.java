package com.group6.noteapp.controller;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.group6.noteapp.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}