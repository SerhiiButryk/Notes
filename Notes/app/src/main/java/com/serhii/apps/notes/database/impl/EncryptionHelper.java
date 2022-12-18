/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.database.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.core.log.Log;
import com.serhii.core.security.Cipher;
import com.serhii.core.security.impl.crypto.CryptoError;
import com.serhii.core.security.impl.crypto.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EncryptionHelper {

    private static final String TAG = "EncryptionHelper";
    private static final String IV_DATA_FILE = "com.example.app.db.pref.local";
    private static final String KEY_IV_NOTE = "note_iv";

    private byte[] ivNote;
    // Application context
    private final Context context;

    private CryptoError lastError = CryptoError.OK;

    public EncryptionHelper(Context context) {
        this.context = context;
    }

    public String encrypt(final NoteModel noteModel) {
        Log.info(TAG, "encrypt() in");

        resetErrors();

        Cipher csk = new Cipher();

        String json = NoteModel.Companion.getJson(noteModel);
        Result note = csk.encryptSymmetric(json);

        ivNote = note.getIv();

        return note.getMessage();
    }

    public NoteModel decrypt(String note, Integer index) {
        Log.info(TAG, "decrypt() with index = " + index + ", in");
        resetErrors();
        retrieveMetaData(index);
        return decryptInternal(note);
    }

    public void saveMetaData(int id) {
        Log.info(TAG, "saveMetaData() index " + id + ", in");

        String fileName = IV_DATA_FILE + id;

        // Save to shared preferences
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();

        // Remove old metadata
        if (preferences.contains(KEY_IV_NOTE)) {
            edit.remove(KEY_IV_NOTE);
        }

        edit.putString(KEY_IV_NOTE, new String(Base64.encode(ivNote, Base64.NO_WRAP)));
        edit.apply();
        Log.info(TAG, "saveMetaData() out");
    }

    private void retrieveMetaData(int id) {
        Log.info(TAG, "retrieveMetaData() index " + id);

        String fileName = IV_DATA_FILE + id;

        // Retrieve from shared preferences
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String val = preferences.getString(KEY_IV_NOTE, "");

        ivNote = Base64.decode(val, Base64.NO_WRAP);
    }

    public List<NoteModel> decrypt(Map<Integer, String> data) {

        resetErrors();

        List<NoteModel> noteDec = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : data.entrySet()) {

            Log.info(TAG, "decryptData() index " + entry.getKey());

            NoteModel noteModel = decrypt(entry.getValue(), entry.getKey());

            if (noteModel != null) {
                noteDec.add(noteModel);
            }
        }

        return noteDec;
    }

    public CryptoError getLastError() { return lastError; }

    public void resetErrors() { lastError = CryptoError.OK; }

    private NoteModel decryptInternal(String note) {
        Cipher csk = new Cipher();
        Result decodedNote = csk.decryptSymmetric(note, ivNote);
        String json = decodedNote.getMessage();
        return NoteModel.Companion.fromJson(json);
    }

}
