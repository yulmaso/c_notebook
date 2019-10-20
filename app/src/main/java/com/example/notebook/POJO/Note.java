package com.example.notebook.POJO;

import java.io.Serializable;

public class Note implements Serializable {
    private int id;
    private String text;
    private String date;
    private String time;

    public Note() {}

    public Note(String text, String date, String time) {
        this.text = text;
        this.date = date;
        this.time = time;
    }

    public Note(int id, String text, String date, String time){
        this.id = id;
        this.text = text;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
