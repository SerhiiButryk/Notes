package com.example.notes.test.ui.data_model;

public class NoteModel {

    private static final String TEMPLATE_TITLE_NOTE = "New title";
    private static final String TEMPLATE_CONTENT_NOTE = "Here will be your short note's content shown";

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

    public boolean isTemplate() {
        return noteTitle.equals(TEMPLATE_TITLE_NOTE) & note.equals(TEMPLATE_CONTENT_NOTE);
    }

    public static NoteModel createTemplateNote() {
        NoteModel noteValues = new NoteModel();

        noteValues.setNoteTitle(TEMPLATE_TITLE_NOTE);
        noteValues.setNote(TEMPLATE_CONTENT_NOTE);

        return noteValues;
    }
}
