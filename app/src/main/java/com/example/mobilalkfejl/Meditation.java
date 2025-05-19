package com.example.mobilalkfejl;

public class Meditation {
    private String title;
    private String description;
    private int notesCount;

    public Meditation(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Meditation(String title, String description, int notesCount) {
        this.title = title;
        this.description = description;
        this.notesCount = notesCount;
    }

    public Meditation() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getNotesCount() {
        return notesCount;
    }

    public void setNotesCount(int notesCount) {
        this.notesCount = notesCount;
    }
}