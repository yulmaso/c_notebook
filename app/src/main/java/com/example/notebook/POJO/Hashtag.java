package com.example.notebook.POJO;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Hashtag implements Serializable, Parcelable {

    private int note_id;
    private String hashtag;

    private int color; // db doesn't have this field!
    private int id; // db doesn't have this field!

    public Hashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public Hashtag(int note_id, String hashtag) {
        this.note_id = note_id;
        this.hashtag = hashtag;
    }

    public Hashtag(int note_id, String hashtag, int color) {
        this.note_id = note_id;
        this.hashtag = hashtag;
        this.color = color;
    }

    public boolean equals(Hashtag hashtag) {
        return hashtag.getId() == id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(note_id);
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
        note_id = parcel.readInt();
        hashtag = parcel.readString();
        color = parcel.readInt();
    }

    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
