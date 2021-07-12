package com.group6.noteapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

/* User Object */
public class User implements Serializable {

    /* Object Properties */
    private String fullName;            // User's full name
    private String address;             // User's address
    private String birthdate;           // User's birth date
    @ServerTimestamp
    private Timestamp createdDate;      // User's account creation date

    /* Constructors */
    public User() {
    }

    public User(String fullName, String address, String birthdate, Timestamp createdDate) {
        this.fullName = fullName;
        this.address = address;
        this.birthdate = birthdate;
        this.createdDate = createdDate;
    }

    /* Getters and Setters */

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
}