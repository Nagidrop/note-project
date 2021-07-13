/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

/* Notebook Object */
public class Notebook implements Parcelable {

    /* Object Properties */
    private String title;               // Notebook's title
    @ServerTimestamp
    private Timestamp createdDate;      // Notebook's created date
    private boolean isDeleted;          // Is the notebook deleted?

    /* Constructors */
    public Notebook() {
    }

    public Notebook(String title, Timestamp createdDate, boolean isDeleted) {
        this.title = title;
        this.createdDate = createdDate;
        this.isDeleted = isDeleted;
    }

    /* Getters and Setters */

    protected Notebook(Parcel in) {
        title = in.readString();
        createdDate = in.readParcelable(Timestamp.class.getClassLoader());
        isDeleted = in.readByte() != 0;
    }

    public static final Creator<Notebook> CREATOR = new Creator<Notebook>() {
        @Override
        public Notebook createFromParcel(Parcel in) {
            return new Notebook(in);
        }

        @Override
        public Notebook[] newArray(int size) {
            return new Notebook[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeParcelable(createdDate, flags);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
    }
}
