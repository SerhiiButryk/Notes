package com.example.notes.test.db.impl.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.core.security.impl.crypto.CryptoError;
import com.example.notes.test.control.EventService;
import com.example.notes.test.ui.data_model.NoteModel;
import com.example.core.security.Cipher;
import com.example.core.security.impl.crypto.Result;

public class DBCypher {

    private static final String FILE_PREFERENCES = "com.example.app.db.pref.local";
    private static final String KEY_IV_TITLE = "iv_title";
    private static final String KEY_IV_NOTE = "iv_note";

    /**
     *  Indicates that this is not an update note request
     */
    public static final long ADD_MODE = -1;

    private byte[] ivTitle;
    private byte[] ivNote;

    // Application context
    private final Context context;

    public DBCypher(Context context) {
        this.context = context;
    }

    public NoteModel encrypt(final NoteModel noteModel, long id) {
        Cipher csk = new Cipher();

        Result note = csk.encryptSymmetric(noteModel.getNote());
        Result title = csk.encryptSymmetric(noteModel.getNoteTitle());

        if (!note.isResultAvailable() || !title.isResultAvailable()) {

            if (note.getError() == CryptoError.USER_NOT_AUTHORIZED) {
                // Need to request keystore unlock
                EventService.getInstance().onUnlockKeystore();
            }

            return null;
        }

        ivTitle = title.getIv();
        ivNote = note.getIv();

        /**
         *  Save IV if a note was updated
         */
        if (id != ADD_MODE) {
            saveInitializeVector(id);
        }

        return new NoteModel(note.toString(), title.toString());
    }

    public NoteModel decrypt(final NoteModel noteModel, long id) {
        Cipher csk = new Cipher();

        retrieveInitializeVector(id);

        Result decodedNote = csk.decryptSymmetric(noteModel.getNote(), ivNote);
        Result decodedTitle = csk.decryptSymmetric(noteModel.getNoteTitle(), ivTitle);

        if (!decodedNote.isResultAvailable() || !decodedTitle.isResultAvailable()) {

            if (decodedNote.getError() == CryptoError.USER_NOT_AUTHORIZED) {
                // Need to request keystore unlock
                EventService.getInstance().onUnlockKeystore();
            }

            return null;
        }

        return new NoteModel(decodedNote.getMessage(), decodedTitle.getMessage());
    }

    public void saveInitializeVector(long id) {

        String fileName = FILE_PREFERENCES + id;

        // Save to shared preferences
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(KEY_IV_NOTE, new String(Base64.encode(ivNote, Base64.NO_WRAP)));
        edit.putString(KEY_IV_TITLE, new String(Base64.encode(ivTitle, Base64.NO_WRAP)));
        edit.commit();

    }

    private void retrieveInitializeVector(long id) {

        String fileName = FILE_PREFERENCES + id;

        // Retrieve from shared preferences
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String stitle = preferences.getString(KEY_IV_TITLE, "");
        String snote = preferences.getString(KEY_IV_NOTE, "");

        if (stitle.isEmpty() || snote.isEmpty()) {
            throw new IllegalStateException("BDCypher error, can not retrieve iv from shared preferences");
        }

        ivTitle = Base64.decode(stitle, Base64.NO_WRAP);
        ivNote = Base64.decode(snote, Base64.NO_WRAP);
    }

}
