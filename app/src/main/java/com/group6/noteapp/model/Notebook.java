/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

public class Notebook implements Serializable {
    private String title;
    @ServerTimestamp
    private Timestamp createdDate;
    private boolean isDeleted;

    /* Constructors */
    public Notebook() {
    }

    public Notebook(String title, Timestamp createdDate, boolean isDeleted) {
        this.title = title;
        this.createdDate = createdDate;
        this.isDeleted = isDeleted;
    }

    /* Getters and Setters */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
