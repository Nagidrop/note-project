package com.group6.noteapp.model;

import java.io.Serializable;

/* User Entity */
public class User implements Serializable {

    /* Entity Properties */
    private int id;                 // User's ID
    private String fullName;        // User's full name
    private String email;           // User's email
    private String address;         // User's address
    private String phone;           // User's phone
    private String birthdate;       // User's birth date

    /* Constructors */
    public User() {
    }

    public User(int id,
                String fullName,
                String email,
                String address,
                String phone,
                String birthdate) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.birthdate = birthdate;
    }

    /* Getters and Setters */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}