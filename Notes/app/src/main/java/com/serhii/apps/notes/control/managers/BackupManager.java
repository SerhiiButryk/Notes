package com.serhii.apps.notes.control.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.serhii.apps.notes.database.NotesDatabaseProvider;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.apps.notes.ui.view_model.NotesViewModel;
import com.serhii.core.log.Log;
import com.serhii.core.security.Cipher;
import com.serhii.core.security.Hash;
import com.serhii.core.security.impl.crypto.Result;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BackupManager {

    public static final String TAG = "BackupManager";

    public static final int REQUEST_CODE_EXTRACT_NOTES = 1;
    public static final int REQUEST_CODE_BACKUP_NOTES = 2;
    public static final int REQUEST_CODE_OPEN_BACKUP_FILE = 3;

    private static final String FILE_MIME_TYPE = "text/plain";
    private static final String FILE_NAME_EXTRACTED_DATA = "NotesExtracted.txt";
    private static final String FILE_NAME_BACKUP_DATA = "NotesBackup.txt";
    private static final String IV_KEY_MARKER = "RTSASPE00";

    private WeakReference<NotesViewModel> notesViewModelWeakReference;

    private static BackupManager instance;

    private BackupManager() {}

    public static BackupManager getInstance() {
        if (instance == null) {
            instance = new BackupManager();
        }
        return instance;
    }

    public void setNotesViewModelWeakReference(NotesViewModel notesViewModel) {
        notesViewModelWeakReference = new WeakReference<>(notesViewModel);
    }

    public void clearNotesViewModelWeakReference() {
        if (notesViewModelWeakReference != null) {
            notesViewModelWeakReference.clear();
        }
    }

    public void openDirectoryChooserForExtractData(Activity activity) {

        Log.info(TAG, "openDirectoryChooser()");

        activity.startActivityForResult(createIntent(FILE_NAME_EXTRACTED_DATA), REQUEST_CODE_EXTRACT_NOTES);
    }

    public void openDirectoryChooserForBackup(Activity activity) {

        Log.info(TAG, "openDirectoryChooserForBackup()");

        activity.startActivityForResult(createIntent(FILE_NAME_BACKUP_DATA), REQUEST_CODE_BACKUP_NOTES);
    }

    public void openBackUpFile(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(FILE_MIME_TYPE);
        intent.putExtra(Intent.EXTRA_TITLE, FILE_NAME_BACKUP_DATA);

        activity.startActivityForResult(intent, REQUEST_CODE_OPEN_BACKUP_FILE);
    }

    public boolean saveDataAsPlainText(final OutputStream outputStream, Context context) {

        Log.info(TAG, "backupDataAsPlainText()");

        try {

            NotesDatabaseProvider notesDatabaseProvider = new NotesDatabaseProvider(context);

            if (notesDatabaseProvider.getRecordsCount() != 0) {

                List<NoteModel> notes = notesDatabaseProvider.getRecords();

                StringBuilder builder = new StringBuilder();

                for (NoteModel note : notes) {
                    builder.append("*********** ")
                            .append(note.getTitle().trim())
                            .append(" ***********")
                            .append("\n")
                            .append(note.getNote().trim())
                            .append("\n");
                }

                String data = builder.toString();

                if (!data.isEmpty()) {
                    outputStream.write(data.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    Log.info(TAG, "backupDataAsPlainText() backup is finished");

                    return true;
                }

            } else {
                Log.info(TAG, "backupDataAsPlainText() database is empty");
            }

        } catch (Exception e) {
            Log.error(TAG, "backupDataAsPlainText() exception: " + e);
            e.printStackTrace();
        }

        return false;
    }

    public boolean backupData(final OutputStream outputStream, Context context, String passwordHash) {

        NotesDatabaseProvider notesDatabaseProvider = new NotesDatabaseProvider(context);

        Log.info(TAG, "backupDataAsEncryptedText() records: " + notesDatabaseProvider.getRecordsCount());

        if (notesDatabaseProvider.getRecordsCount() != 0) {

            List<NoteModel> notes = notesDatabaseProvider.getRecords();
            List<NoteAdapter> serializedNotes = new ArrayList<>();

            for (int i = 0; i < notes.size(); i++) {
                serializedNotes.add(new NoteAdapter(i, notes.get(i).getTitle(), notes.get(i).getNote()));
            }

            BackupAdapter backupAdapter = new BackupAdapter(serializedNotes);

            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<BackupAdapter> jsonAdapter = moshi.adapter(BackupAdapter.class);

            String json = jsonAdapter.toJson(backupAdapter);

            // Gen random iv every time
//            String iv = UUID.randomUUID().toString();
            passwordHash = "passwordHash112";
            String iv = "902r9eworweoijf";

            // Encrypt
            Cipher cipher = new Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL);
            Result result = cipher.encryptSymmetric(json, passwordHash, iv.getBytes());

            if (result.isResultAvailable() && !result.getMessage().isEmpty()) {
                try {
                    // Encrypted data
                    outputStream.write(result.getMessage().getBytes());
                    outputStream.write(IV_KEY_MARKER.getBytes());
                    outputStream.write(iv.getBytes());
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    Log.info(TAG, "backupDataAsEncryptedText() error: " + e);
                    e.printStackTrace();
                    return false;
                }
                // Succeeded to backup data
                return true;
            } else {
                Log.info(TAG, "backupDataAsEncryptedText() failed to encrypt data");
            }

            // Failed to backup data
            return false;

        } else {
            Log.info(TAG, "backupDataAsEncryptedText() database is empty");
        }

        // Failed no data to backup
        return false;
    }

    public boolean restoreData(String json, Context context, String passwordHash) {

        Log.detail(TAG, "restoreData() json: " + json);

        // Get iv
        //String iv = json.split(IV_KEY_MARKER)[1];
        passwordHash = "passwordHash112";
        String iv = "902r9eworweoijf";
        // Get data
        json = json.split(IV_KEY_MARKER)[0];

        // Decrypt
        Cipher cipher = new Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL);
        Result result = cipher.decryptSymmetric(json, passwordHash, iv.getBytes());
        if (!result.isResultAvailable() || result.getMessage().isEmpty()) {
            Log.error(TAG, "restoreData() failed to decrypt");
            return false;
        }
        // Decrypted data
        json = result.getMessage();

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<BackupAdapter> jsonAdapter = moshi.adapter(BackupAdapter.class);

        BackupAdapter backupAdapter = null;
        try {
            backupAdapter = jsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            Log.info(TAG, "restoreData() error parsing json content: " + e);
            return false;
        }

        List<NoteAdapter> notes = backupAdapter.notes;
        NotesDatabaseProvider notesDatabaseProvider = new NotesDatabaseProvider(context);

        for (NoteAdapter note : notes) {
            notesDatabaseProvider.addRecord(new NoteModel(note.note, note.title));
        }

        if (notesViewModelWeakReference != null) {
            NotesViewModel notesViewModel = notesViewModelWeakReference.get();
            if (notesViewModel != null) {
                notesViewModel.updateData();
            } else {
                Log.error(TAG, "restoreData() nvm is null");
                return false;
            }
        } else {
            Log.error(TAG, "restoreData() wr is null");
            return false;
        }

        return true;
    }

    private Intent createIntent(final String fileName) {
        // Show directory chooser
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(FILE_MIME_TYPE);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        return intent;
    }

    /**
     *  Backup Adapter
     */
    public static class BackupAdapter {
        public List<NoteAdapter> notes;

        public BackupAdapter(List<NoteAdapter> notes) {
            this.notes = notes;
        }

    }

    public static class NoteAdapter {
        private int id;
        private String title;
        private String note;

        public NoteAdapter(int id, String title, String note) {
            this.id = id;
            this.title = title;
            this.note = note;
        }

    }

}
