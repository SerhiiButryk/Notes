package com.example.notes.test.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.core.log.Log;
import com.example.core.security.impl.crypto.CryptoError;
import com.example.notes.test.ui.data_model.NoteModel;
import com.example.core.security.Cipher;
import com.example.core.security.impl.crypto.Result;

import java.util.ArrayList;
import java.util.List;

public class EncryptionHelper {

    private static final String TAG = EncryptionHelper.class.getSimpleName();

    private static final String IV_DATA_FILE = "com.example.app.db.pref.local";

    private static final String KEY_IV_TITLE = "iv_title";
    private static final String KEY_IV_NOTE = "iv_note";
    private static final String KEY_IV_TIME = "iv_time";

    private byte[] ivTitle;
    private byte[] ivNote;
    private byte[] ivTime;

    // Application context
    private final Context context;

    public EncryptionHelper(Context context) {
        this.context = context;
    }

    public NoteModel encrypt(final NoteModel noteModel) {

        Log.info(TAG, "encrypt()");

        Cipher csk = new Cipher();

        Result note = csk.encryptSymmetric(noteModel.getNote());
        Result title = csk.encryptSymmetric(noteModel.getTitle());
        Result time = csk.encryptSymmetric(noteModel.getTime());

        if (!note.isResultAvailable() || !title.isResultAvailable()) {
            if (note.getError() == CryptoError.USER_NOT_AUTHORIZED) {
                Log.info(TAG, "encrypt() error: USER_NOT_AUTHORIZED");
                return null;
            }
        }

        ivTitle = title.getIv();
        ivNote = note.getIv();
        ivTime = time.getIv();

        return new NoteModel(note.toString(), title.toString(), time.toString());
    }

    public NoteModel decrypt(final NoteModel noteModel) {
        Log.info(TAG, "decrypt()");

        return decryptInternal(noteModel);
    }

    public NoteModel decrypt(final NoteModel noteModel, int index) {
        Log.info(TAG, "decrypt() with index = " + index);

        retrieveMetaData(index);

        return decryptInternal(noteModel);
    }

    public void saveMetaData(int id) {
        Log.info(TAG, "saveMetaData() index " + id);

        String fileName = IV_DATA_FILE + id;

        // Save to shared preferences
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();

        // Remove old values
        if (preferences.contains(KEY_IV_NOTE) || preferences.contains(KEY_IV_TITLE)) {
            removeMetaData(id);
        }

        edit.putString(KEY_IV_NOTE, new String(Base64.encode(ivNote, Base64.NO_WRAP)));
        edit.putString(KEY_IV_TITLE, new String(Base64.encode(ivTitle, Base64.NO_WRAP)));
        edit.putString(KEY_IV_TIME, new String(Base64.encode(ivTime, Base64.NO_WRAP)));
        edit.apply();
    }

    private void retrieveMetaData(int id) {
        Log.info(TAG, "retrieveMetaData() index " + id);

        String fileName = IV_DATA_FILE + id;

        // Retrieve from shared preferences
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String stitle = preferences.getString(KEY_IV_TITLE, "");
        String snote = preferences.getString(KEY_IV_NOTE, "");
        String stime = preferences.getString(KEY_IV_TIME, "");

        if (stitle.isEmpty() || snote.isEmpty() || stime.isEmpty()) {
            throw new IllegalStateException(TAG + " can not retrieve iv from shared preferences");
        }

        ivTitle = Base64.decode(stitle, Base64.NO_WRAP);
        ivNote = Base64.decode(snote, Base64.NO_WRAP);
        ivTime = Base64.decode(stime, Base64.NO_WRAP);
    }

    public void removeMetaData(int index) {
        Log.info(TAG, "removeMetaData() index " + index);
        String fileName = IV_DATA_FILE + index;

        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(KEY_IV_NOTE);
        edit.remove(KEY_IV_TITLE);
        edit.remove(KEY_IV_TIME);
        edit.apply();

    }

    public List<NoteModel> decodeData(List<NoteModel> data) {
        List<NoteModel> noteDec = new ArrayList<>();

        for (NoteModel n : data) {

            retrieveMetaData(Integer.parseInt(n.getId()));

            NoteModel noteModel = decrypt(n);

            if (noteModel != null)
                noteDec.add(noteModel);
        }

        return noteDec;
    }

    private NoteModel decryptInternal(final NoteModel encData) {
        Cipher csk = new Cipher();

        Result decodedNote = csk.decryptSymmetric(encData.getNote(), ivNote);
        Result decodedTitle = csk.decryptSymmetric(encData.getTitle(), ivTitle);
        Result decodedTime = csk.decryptSymmetric(encData.getTime(), ivTime);

        if (!decodedNote.isResultAvailable() || !decodedTitle.isResultAvailable() || !decodedTime.isResultAvailable()) {
            if (decodedNote.getError() == CryptoError.USER_NOT_AUTHORIZED) {
                Log.info(TAG, "decrypt(): error: USER_NOT_AUTHORIZED");
                return null;
            }
        }

        return new NoteModel(decodedNote.getMessage(), decodedTitle.getMessage(), decodedTime.getMessage(), encData.getId());
    }

}
