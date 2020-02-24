package com.example.notebook.POJO;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Hashtag implements Parcelable {

    private int id_note;
    private String hashtag;

    private int color; // db doesn't have this field!

    public Hashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public Hashtag(int id_note, String hashtag) {
        this.id_note = id_note;
        this.hashtag = hashtag;
    }

    public Hashtag(int id_note, String hashtag, int color) {
        this.id_note = id_note;
        this.hashtag = hashtag;
        this.color = color;
    }

    public boolean equals(Hashtag hashtag) {
        return hashtag.getHashtag().equals(this.hashtag);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_note);
        dest.writeString(hashtag);
        dest.writeInt(color);
    }

    public static final Parcelable.Creator<Hashtag> CREATOR = new Parcelable.Creator<Hashtag>() {
        public Hashtag createFromParcel(Parcel in) {
            return new Hashtag(in);
        }

        public Hashtag[] newArray(int size) {
            return new Hashtag[size];
        }
    };

    private Hashtag(Parcel parcel) {
        id_note = parcel.readInt();
        hashtag = parcel.readString();
        color = parcel.readInt();
    }

    public int getId_note() {
        return id_note;
    }

    public void setId_note(int id_note) {
        this.id_note = id_note;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
