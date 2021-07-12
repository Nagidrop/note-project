package com.group6.noteapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

/* Note Entity */
@IgnoreExtraProperties
public class Note implements Serializable {

    /* Entity Properties */
    private String id;              // Note's doc ID
    private String title;           // Note's title
    private String content;         // Note's content
    private boolean isDeleted;      // Is the note in trash?
    @ServerTimestamp
    private Timestamp createdDate;

    /* Constructors */
    public Note() {
    }

    public Note(String id, String title, String content, boolean isDeleted, Timestamp createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isDeleted = isDeleted;
        this.createdDate = createdDate;
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

    public boolean getDeleted() {
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

    @Override
    public String toString(){
        return "NOTE" + this.getTitle() + this.getContent() + this.getCreatedDate();
    }
}
