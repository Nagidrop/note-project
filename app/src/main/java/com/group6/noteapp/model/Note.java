package com.group6.noteapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

/**
 * Note Object
 */
@IgnoreExtraProperties
public class Note implements Parcelable {

    /* Object Properties */
    @DocumentId
    private String id;              // Note Document ID
    private Notebook notebook;      // The notebook which the note is in
    private String title;           // Note's title
    private String content;         // Note's content
    private boolean isDeleted;      // Is the note in trash?
    @ServerTimestamp
    private Timestamp createdDate;  // Note's created date
    @ServerTimestamp
    private Timestamp updatedDate;  // Note's updated date

    /* Constructors */

    /**
     * No args constructor
     */
    public Note() {
    }

    /**
     * All args constructor
     * @param id            Note Document ID
     * @param notebook      The notebook which the note is in
     * @param title         Note's title
     * @param content       Note's content
     * @param isDeleted     Is the note in trash?
     * @param createdDate   Note's created date
     * @param updatedDate   Note's updated date
     */
    public Note(String id, Notebook notebook, String title, String content,
                boolean isDeleted, Timestamp createdDate, Timestamp updatedDate) {
        this.id = id;
        this.notebook = notebook;
        this.title = title;
        this.content = content;
        this.isDeleted = isDeleted;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    /* Getters and Setters */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude    // ignore from Firebase-related operations
    public Notebook getNotebook() {
        return notebook;
    }

    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
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


    /* Parcelable implementation */
    protected Note(Parcel in) {
        id = in.readString();
        notebook = in.readParcelable(Notebook.class.getClassLoader());
        title = in.readString();
        content = in.readString();
        isDeleted = in.readByte() != 0;
        createdDate = in.readParcelable(Timestamp.class.getClassLoader());
        updatedDate = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
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
        dest.writeParcelable(notebook, flags);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeParcelable(createdDate, flags);
        dest.writeParcelable(updatedDate, flags);
    }
}
