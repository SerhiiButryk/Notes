package com.example.notes.test.ui.data_model;

import android.content.Context;

import com.example.notes.test.R;

public class NoteModel {

    private String note;
    private String noteTitle;

    public NoteModel() {
        note = "";
        noteTitle = "";
    }

    public NoteModel(String note, String noteTitle) {
        this.note = note;
        this.noteTitle = noteTitle;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public boolean isTemplate(Context context) {
        return noteTitle.equals(context.getString(R.string.template_note_title))
                && note.equals(context.getString(R.string.template_short_note));
    }

    public static NoteModel createTemplateNote(Context context) {
        NoteModel noteValues = new NoteModel();
        noteValues.setNoteTitle(context.getString(R.string.template_note_title));
        noteValues.setNote(context.getString(R.string.template_short_note));
        return noteValues;
    }
}
