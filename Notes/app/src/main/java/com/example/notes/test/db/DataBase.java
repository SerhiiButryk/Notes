package com.example.notes.test.db;

import android.content.Context;

import com.example.notes.test.ui.data_model.NoteModel;

import java.util.List;

public interface DataBase {

    void initDb(Context context);

    void clearDatabase();

    boolean addRecord(final NoteModel uiData);

    void deleteRecord(int id);

    boolean updateRecord(final int id, final NoteModel uiData);

    NoteModel getRecord(final int id);

    List<NoteModel> getRecords();

    void close();

}
