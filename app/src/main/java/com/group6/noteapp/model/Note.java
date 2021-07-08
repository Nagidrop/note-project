package com.group6.noteapp.model;

import java.io.Serializable;

/* Note Entity */
public class Note implements Serializable {

    /* Entity Properties */
    private int id;                 // Note's ID
    private int userId;             // Note's user ID (ID of user who created the note)
    private String title;           // Note's title
    private String content;         // Note's content
    private Boolean isDeleted;      // Is the note in trash?

    /* Constructors */
    public Note() {
    }

    public Note(int id, int userId, String title, String content, Boolean isDeleted) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.isDeleted = isDeleted;
    }

    /* Getters and Setters */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
