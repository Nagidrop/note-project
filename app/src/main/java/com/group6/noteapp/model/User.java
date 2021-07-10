package com.group6.noteapp.model;

import java.io.Serializable;

/* User Entity */
public class User implements Serializable {

    /* Entity Properties */
    private String fullName;        // User's full name
    private String address;         // User's address
    private String birthdate;       // User's birth date
    private String createdDate;     // User's created date

    /* Constructors */
    public User() {
    }

    public User(String fullName,
                String address,
                String birthdate,
                String createdDate) {
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}