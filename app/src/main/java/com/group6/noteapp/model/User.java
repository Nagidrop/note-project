package com.group6.noteapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

/* User Object */
public class User implements Parcelable {

    /* Object Properties */
    private String fullName;            // User's full name
    private String address;             // User's address
    private String birthdate;           // User's birth date
    @ServerTimestamp
    private Timestamp createdDate;      // User's account creation date

    /* Constructors */
    public User() {
    }

    public User(String fullName, String address, String birthdate, Timestamp createdDate) {
        this.fullName = fullName;
        this.address = address;
        this.birthdate = birthdate;
        this.createdDate = createdDate;
    }

    /* Getters and Setters */

    protected User(Parcel in) {
        fullName = in.readString();
        address = in.readString();
        birthdate = in.readString();
        createdDate = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

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
        dest.writeString(fullName);
        dest.writeString(address);
        dest.writeString(birthdate);
        dest.writeParcelable(createdDate, flags);
    }
}