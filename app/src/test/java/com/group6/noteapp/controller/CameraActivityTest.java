package com.group6.noteapp.controller;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CameraActivityTest {

    @Test
    public void test1(){
        CameraActivity cameraActivity = new CameraActivity();
        cameraActivity.onCreate(new Bundle());
    }
}