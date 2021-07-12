package com.group6.noteapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

/* Note Object */
@IgnoreExtraProperties
public class Note implements Serializable {

    /* Object Properties */
    private String id;              // Note's doc ID
    private String title;           // Note's title
    private String content;         // Note's content
    private boolean isDeleted;      // Is the note in trash?
    @ServerTimestamp
    private Timestamp createdDate;  // Note's created date
    @ServerTimestamp
    private Timestamp updatedDate;  // Note's updated date

    /* Constructors */
    public Note() {
    }

    public Note(String id, String title, String content, boolean isDeleted, Timestamp createdDate, Timestamp updatedDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isDeleted = isDeleted;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    /* Getters and Setters */

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }
}
