/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

/**
 * Notebook Object
 */
@IgnoreExtraProperties
public class Notebook implements Parcelable {

    /* Object Properties */
    @DocumentId
    private String id;                  // Notebook Document ID
    private String title;               // Notebook's title
    @ServerTimestamp
    private Timestamp createdDate;      // Notebook's created date
    private boolean isDeleted;          // Is the notebook deleted?

    /* Constructors */

    /**
     * No args constructor
     */
    public Notebook() {
    }

    /**
     * All args constructor
     * @param id            Notebook's Document ID
     * @param title         Notebook's title
     * @param createdDate   Notebook's created date
     * @param isDeleted     Is the notebook deleted?
     */
    public Notebook(String id, String title, Timestamp createdDate, boolean isDeleted) {
        this.id = id;
        this.title = title;
        this.createdDate = createdDate;
        this.isDeleted = isDeleted;
    }

    /* Getters and Setters */

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

    /* Parcelable implementation */

    protected Notebook(Parcel in) {
        id = in.readString();
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

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeParcelable(createdDate, flags);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
    }
}
