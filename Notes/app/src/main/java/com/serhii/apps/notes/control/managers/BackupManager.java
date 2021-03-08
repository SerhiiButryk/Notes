package com.serhii.apps.notes.control.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.database.NotesDataProvider;
import com.serhii.apps.notes.database.NotesDatabase;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.apps.notes.ui.dialogs.DialogHelper;
import com.serhii.core.log.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class BackupManager {

    public static final String TAG = BackupManager.class.getSimpleName();

    public static final int REQUEST_CODE = 1;
    public static final int ALERT_DIALOG_TYPE = 101;

    private static final String MIME_TYPE = "text/plain";
    private static final String BACKUP_FILE_NAME = "BackupNotes.txt";

    public void openDirectoryChooser(Activity activity) {

        // Check if there is data to backup
        if (NotesDatabase.getInstance().getRecordsCount() == 0) {
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

        // TODO: Check keystore unauthorize error !!!

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

                    Toast.makeText(context, context.getString(R.string.backup_file_success), Toast.LENGTH_LONG).show();
                    return;
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, context.getString(R.string.backup_file_fail), Toast.LENGTH_LONG).show();

        Log.info(TAG, "backupData() DONE");
    }

}
