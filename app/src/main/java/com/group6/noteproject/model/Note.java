package com.group6.noteproject.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id; // Note's id

    @ColumnInfo(name = "user_id")
    private int userId; // Note's user id

    @ColumnInfo(name = "title")
    private String title; // Note's title

    @ColumnInfo(name = "content")
    private String content; // Note's content

    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted; // Is note deleted?

    public Note() {
    }

    @Ignore
    public Note(int id, int userId, String title, String content, Boolean isDeleted) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.isDeleted = isDeleted;
    }

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
