package com.serhii.apps.notes.control.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.database.NotesDataProvider;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.apps.notes.ui.dialogs.DialogHelper;
import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;

import java.io.OutputStream;
import java.util.List;

public class BackupManager {

    public static final String TAG = BackupManager.class.getSimpleName();

    public static final int REQUEST_CODE = 1;
    public static final int ALERT_DIALOG_TYPE = 101;

    private static final String MIME_TYPE = "text/plain";
    private static final String BACKUP_FILE_NAME = "BackupNotes.txt";

    public void openDirectoryChooser(Activity activity) {

        Log.info(TAG, "openDirectoryChooser()");

        NotesDataProvider notesDataProvider = new NotesDataProvider(activity);

        // Check if there is data to backup
        if (notesDataProvider.getRecordsCount() == 0) {
            DialogHelper.showAlertDialog(ALERT_DIALOG_TYPE, activity);
            return;
        }

        // Show directory chooser
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(MIME_TYPE);
        intent.putExtra(Intent.EXTRA_TITLE, BACKUP_FILE_NAME);

        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public void backupData(final OutputStream outputStream, Context context) {

        Log.info(TAG, "backupData()");

        try {

            NotesDataProvider notesDataProvider = new NotesDataProvider(context);

            List<NoteModel> notes = notesDataProvider.getRecords();

            if (notes != null && !notes.isEmpty()) {

                StringBuilder builder = new StringBuilder();

                for (NoteModel note : notes) {
                    builder.append(note.getTitle());
                    builder.append('\n');
                    builder.append(note.getNote());
                    builder.append("\n\n");
                }

                String data = builder.toString();

                if (!data.isEmpty()) {
                    outputStream.write(data.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    GoodUtils.showToast(context, R.string.backup_file_success);

                    Log.info(TAG, "backupData() backup is finished");

                    return;
                }

            } else {
                Log.info(TAG, "backupData() database is empty");
            }

        } catch (Exception e) {
            Log.error(TAG, "backupData() exception: " + e);
            e.printStackTrace();
        }

        GoodUtils.showToast(context, R.string.backup_file_fail);

    }

}
