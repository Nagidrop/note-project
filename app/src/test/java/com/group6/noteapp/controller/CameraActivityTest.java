package com.group6.noteapp.controller;

import android.os.Bundle;

import org.junit.jupiter.api.Test;

class CameraActivityTest {

    @Test
    public void test1(){
        CameraActivity cameraActivity = new CameraActivity();
        cameraActivity.onCreate(new Bundle());
    }
}