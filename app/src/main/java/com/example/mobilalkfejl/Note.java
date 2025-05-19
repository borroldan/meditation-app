package com.example.mobilalkfejl;

public class Note {
    private String id;
    private String text;

    public Note() {}

    public Note(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}