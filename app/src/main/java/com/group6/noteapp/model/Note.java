package com.group6.noteapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

/* Note Entity */
public class Note implements Serializable {

    /* Entity Properties */
    private String title;           // Note's title
    private String content;         // Note's content
    private Boolean isDeleted;      // Is the note in trash?
    @ServerTimestamp
    private Timestamp createdDate;

    /* Constructors */
    public Note() {
    }

    public Note(String title, String content, Boolean isDeleted, Timestamp createdDate) {
        this.title = title;
        this.content = content;
        this.isDeleted = isDeleted;
        this.createdDate = createdDate;
    }

    /* Getters and Setters */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
}
