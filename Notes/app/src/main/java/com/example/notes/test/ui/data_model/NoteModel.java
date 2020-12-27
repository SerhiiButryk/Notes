package com.example.notes.test.ui.data_model;

import android.content.Context;

import com.example.notes.test.R;

public class NoteModel {

    private String note;
    private String title;
    private String time;
    private String id;

    public NoteModel() {
        note = "";
        title = "";
        time = "";
        id = "";
    }

    public NoteModel(String note, String title, String time, String id) {
        this.note = note;
        this.title = title;
        this.time = time;
        this.id = id;
    }

    public NoteModel(String note, String title, String time) {
        this.note = note;
        this.title = title;
        this.time = time;
        this.id = "";
    }

    public NoteModel(String note, String title) {
        this.note = note;
        this.title = title;
        this.time = "";
        this.id = "";
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isTemplate(Context context) {
        return title.equals(context.getString(R.string.template_note_title))
                && note.equals(context.getString(R.string.template_short_note));
    }

    public boolean isEmpty() {
        return note.isEmpty() && title.isEmpty();
    }

    public static NoteModel createTemplateNote(Context context) {
        NoteModel noteValues = new NoteModel();
        noteValues.setTitle(context.getString(R.string.template_note_title));
        noteValues.setNote(context.getString(R.string.template_short_note));
        return noteValues;
    }

}
