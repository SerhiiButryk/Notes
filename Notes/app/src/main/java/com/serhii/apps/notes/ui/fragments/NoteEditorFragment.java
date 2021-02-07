package com.serhii.apps.notes.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;
import com.serhii.apps.notes.R;
import com.serhii.apps.notes.databinding.FragmentNoteEditorViewBinding;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.apps.notes.ui.dialogs.DialogHelper;
import com.serhii.apps.notes.ui.fragments.base.IViewBindings;
import com.serhii.apps.notes.ui.view_model.NotesViewModel;
import com.serhii.apps.notes.ui.view_model.NotesViewModelFactory;

public class NoteEditorFragment extends Fragment implements IViewBindings {

    private static final String TAG = NoteEditorFragment.class.getSimpleName();

    public static final String ACTION_NOTE_OPEN = "action open note";
    public static final String ACTION_NOTE_CREATE = "action create note";

    public static final String ARG_ACTION = "action arg";
    public static final String ARG_NOTE_ID = "note id arg";
    public static final String ARG_NOTE_TEMPLATE = "template";

    private EditText titleNoteField;
    private EditText noteFiled;
    private TextView noteTimeFiled;
    private Toolbar toolbar;
    private NotesViewModel notesViewModel;
    private EditorNoteInteraction interaction;
    private String checkNoteTitleContent = "";
    private String checkNoteContent = "";
    private String action;
    private String noteId;
    private boolean isTemplateNote;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            interaction = (EditorNoteInteraction) context;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Should implement " + EditorNoteInteraction.class.getSimpleName() + " listener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initBinding(inflater, container);
    }

    @Override
    public View initBinding(LayoutInflater inflater, ViewGroup container) {
        FragmentNoteEditorViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_editor_view, container, false);

        // Set references
        titleNoteField = binding.titleNote;
        noteFiled = binding.bodyNote;
        toolbar = binding.toolbar;
        noteTimeFiled = binding.dateTimeView;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interaction != null) {
                    interaction.onBackPressedClicked();
                }
            }
        });

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_toolbar);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        notesViewModel = new ViewModelProvider(getActivity(), new NotesViewModelFactory(getActivity().getApplication()))
                .get(NotesViewModel.class);

        action = getArguments().getString(ARG_ACTION);
        noteId = getArguments().getString(ARG_NOTE_ID);

        processArgs();
    }

    private void processArgs() {

        if (action.equals(ACTION_NOTE_CREATE) || noteId.equals(ARG_NOTE_TEMPLATE)) {
            titleNoteField.setHint(getResources().getString(R.string.template_note_title));
            noteFiled.setHint(getResources().getString(R.string.template_short_note));

            isTemplateNote = true;

        } else if (action.equals(ACTION_NOTE_OPEN)) {

            final NoteModel note = notesViewModel.getNote(noteId);

            if (note != null) {
                checkNoteTitleContent = note.getTitle();
                checkNoteContent = note.getNote();

                titleNoteField.setText(checkNoteTitleContent);
                noteFiled.setText(checkNoteContent);

                String timeDate = note.getTime();
                if (!timeDate.isEmpty()) {
                    noteTimeFiled.setText(timeDate);
                    noteTimeFiled.setVisibility(View.VISIBLE);
                }

            } else {
                throw new IllegalStateException("No record found, invalid id received");
            }

        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Remove settings item, we don't need it
        menu.removeItem(R.id.settings_item);

        inflater.inflate(R.menu.editor_note_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_note_item :

                DialogHelper.showConfirmDialog(getActivity(), new DialogHelper.ConfirmDialogCallback() {
                    @Override
                    public void onOkClicked() {
                        deleteNote();
                    }

                    @Override
                    public void onCancelClicked() {
                        // no-op
                    }
                }, R.string.confirm_dialog_delete_note_title, R.string.confirm_dialog_delete_note_message);

                return true;

            case R.id.save_note:

                DialogHelper.showConfirmDialog(getActivity(), new DialogHelper.ConfirmDialogCallback() {
                    @Override
                    public void onOkClicked() {
                        saveUserNote();
                    }

                    @Override
                    public void onCancelClicked() {
                        // no-op
                    }
                }, R.string.confirm_dialog_save_note_title, R.string.confirm_dialog_save_note_message);

                return true;

            case R.id.clear_note:

                noteFiled.setText("");

                return true;
        }

        return false;
    }

    private void saveUserNote() {

        String title = GoodUtils.getText(titleNoteField);
        String note = GoodUtils.getText(noteFiled);

        if (title.isEmpty() && note.isEmpty()) {

            GoodUtils.showToast(getActivity(), R.string.toast_action_error_note_is_empty);

            return;
        }

        /*
            Checks if note needs update
        */
        if (checkIfNoteChanged()) {

            GoodUtils.showToast(getActivity(), R.string.toast_action_error_note_is_not_changed);

            return;
        }

        boolean result;

        // If there is a one template note and it is currently edited
        // then check if we need to update nodeId
        if (isTemplateNote && (notesViewModel.getNotes().getValue().size() == 1)) {
            noteId = notesViewModel.getNotes().getValue().get(0).getId();
            Log.info(TAG, "saveUserNote() updated noteId, new noteId=" + noteId);
        }

        if (action.equals(ACTION_NOTE_OPEN) && !noteId.equals(ARG_NOTE_TEMPLATE)) {
            Log.info(TAG, "saveUserNote() updated note");
            result = notesViewModel.updateNote(noteId, new NoteModel(note, title));
        } else {
            Log.info(TAG, "saveUserNote() add new note");
            result = notesViewModel.addNote(new NoteModel(note, title));
        }

        /*
            Display info toast message
        */
        if (result) {
            Log.info(TAG, "saveUserNote() saved new note");

            GoodUtils.showToast(getActivity(), R.string.toast_action_message);

            // Cache new values
            checkNoteContent = note;
            checkNoteTitleContent = title;
        }

    }

    private void deleteNote() {
        if (isTemplateNote) {
            GoodUtils.showToast(getActivity(), R.string.toast_action_delete_template_note);
            return;
        }

        if (TextUtils.isEmpty(titleNoteField.getText()) && TextUtils.isEmpty(noteFiled.getText())) {
            GoodUtils.showToast(getActivity(), R.string.toast_action_delete_error_note);
            return;
        }

        if (notesViewModel.deleteNote(noteId)) {
            GoodUtils.showToast(getActivity(), R.string.toast_action_deleted_message);
            if (interaction != null) {
                interaction.onNoteDeleted();
            }
        }

    }

    /**
     *  Compare notes before and after editing operation
     */
    private boolean checkIfNoteChanged() {
        String title = GoodUtils.getText(titleNoteField);
        String note = GoodUtils.getText(noteFiled);
        return  isEmptyNote() || (checkNoteTitleContent.equals(title) && checkNoteContent.equals(note));
    }

    private boolean isEmptyNote() {
        return TextUtils.isEmpty(GoodUtils.getText(titleNoteField))
                && TextUtils.isEmpty(GoodUtils.getText(noteFiled));
    }

    public interface EditorNoteInteraction {
        void onBackPressedClicked();
        void onNoteDeleted();
    }

}