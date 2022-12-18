/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database.impl.database;

import android.content.Context;

import java.util.Map;

public abstract class DatabaseImpl {

    public static final String FLAG_SELECT = "=?";
    public static final String FLAG_ORDER = " ASC";

    protected static boolean initialized;

    public void initDbImpl(Context context) { }

    public void clearDatabaseImpl() { checkIfInitialized(); }

    public int addRecordImpl(final String data) { checkIfInitialized(); return -1; }

    public boolean deleteRecordImpl(final String id) { checkIfInitialized(); return false; }

    public boolean updateRecordImpl(final String id, final String newData) { checkIfInitialized(); return false; }

    public String getRecordImpl(final String id) { checkIfInitialized(); return  null; }

    public Map<Integer, String> getRecordsImpl() { checkIfInitialized(); return null; }

    public void closeImpl() { checkIfInitialized(); }

    public int getRecordsCountImpl() { checkIfInitialized(); return 0; }

    protected void checkIfInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Database was not initialized");
        }
    }

}
