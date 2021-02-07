package com.serhii.apps.notes.database;

import android.content.Context;

import java.util.List;

public interface Database<T> {

    void init(Context context);

    void clear();

    int addRecord(final T uiData);

    boolean deleteRecord(final String id);

    boolean updateRecord(final String id, final T uiData);

    T getRecord(final String id);

    List<T> getRecords();

    int getRecordsCount();

    void close();

}
