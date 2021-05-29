package com.serhii.apps.notes.control.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.serhii.apps.notes.database.NotesDatabaseProvider;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.core.log.Log;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class BackupManager {

    public static final String TAG = BackupManager.class.getSimpleName();

    public static final int REQUEST_CODE_EXTRACT_NOTES = 1;
    public static final int REQUEST_CODE_BACKUP_NOTES = 2;
    public static final int REQUEST_CODE_OPEN_BACKUP_FILE = 3;

    public static final int ALERT_DIALOG_TYPE = 101;

    private static final String TEXT_FILE_TYPE = "text/plain";
    private static final String FILE_NAME_EXTRACT_DATA = "NotesExtracted.txt";
    private static final String FILE_NAME_BACKUP = "NotesBackup.txt";

    // TODO: find better way to save it
    // Password which is used to encrypt / decrypt notes for backup
    private static String userPassword;

    public void openDirectoryChooserForExtractData(Activity activity) {

        Log.info(TAG, "openDirectoryChooser()");

        activity.startActivityForResult(createIntent(), REQUEST_CODE_EXTRACT_NOTES);
    }

    public void openDirectoryChooserForBackup(Activity activity) {

        Log.info(TAG, "openDirectoryChooserForBackup()");

        activity.startActivityForResult(createIntent(), REQUEST_CODE_BACKUP_NOTES);
    }

    public void openBackUpFile(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(TEXT_FILE_TYPE);
        intent.putExtra(Intent.EXTRA_TITLE, FILE_NAME_BACKUP);

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
                    builder.append("***********")
                            .append(note.getTitle().trim())
                            .append('\n')
                            .append(note.getNote().trim())
                            .append("***********");
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

    // TODO: Add encryption
    public boolean backupDataAsEncryptedText(final OutputStream outputStream, Context context) {

// TODO: Uncomment when this is tested
//        if (userPassword == null || userPassword.isEmpty()) {
//            Log.error(TAG, "backupDataAsEncryptedText() password is empty, return");
//            return false;
//        }

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

            try {
                outputStream.write(json.getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                Log.info(TAG, "backupDataAsEncryptedText() error: " + e);
                e.printStackTrace();
                return false;
            }

            return true;

        } else {
            Log.info(TAG, "backupDataAsEncryptedText() database is empty");
        }

        return false;
    }

    public boolean restoreData(String json, Context context) {

        Log.detail(TAG, "restoreData() json: " + json);

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

        return true;
    }

    public void setPassword(String password) {
        userPassword = password;
    }

    private Intent createIntent() {
        // Show directory chooser
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(TEXT_FILE_TYPE);
        intent.putExtra(Intent.EXTRA_TITLE, FILE_NAME_EXTRACT_DATA);
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
