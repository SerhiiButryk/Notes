package com.example.notes.test.db;

import android.content.Context;

import com.example.notes.test.db.impl.base.AppDataBaseImpl;
import com.example.notes.test.db.impl.LocalAppDatabase;
import com.example.notes.test.ui.data_model.NoteModel;

import java.util.List;

public class LocalDataBase implements DataBase {

    private static LocalDataBase instance;
    private AppDataBaseImpl impl;

    private LocalDataBase() { impl = LocalAppDatabase.getInstance(); }

    public static LocalDataBase getInstance() {
        if (instance == null) {
            instance = new LocalDataBase();
        }
        return instance;
    }

    @Override
    public void initDb(Context context) {
        impl.initDbImpl(context);
    }

    @Override
    public void clearDatabase() {
        impl.clearDatabaseImpl();
    }

    @Override
    public boolean addRecord(NoteModel uiData) {
        return impl.addRecordImpl(uiData);
    }

    @Override
    public void deleteRecord(int id) {
        impl.deleteRecordImpl(id);
    }

    @Override
    public boolean updateRecord(int id, NoteModel uiData) {
        return impl.updateRecordImpl(id, uiData);
    }

    @Override
    public NoteModel getRecord(int id) {
        return impl.getRecordImpl(id);
    }

    @Override
    public List<NoteModel> getRecords() {
        return impl.getRecordsImpl();
    }

    @Override
    public void close() {
        impl.closeImpl();
    }
}
