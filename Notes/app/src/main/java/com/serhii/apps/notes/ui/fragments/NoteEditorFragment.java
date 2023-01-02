package com.serhii.apps.notes.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.apps.notes.ui.dialogs.DialogHelper;
import com.serhii.apps.notes.ui.utils.NoteEditorAdapter;
import com.serhii.apps.notes.ui.view_model.NotesViewModel;
import com.serhii.apps.notes.ui.view_model.NotesViewModelFactory;
import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;

import java.util.ArrayList;
import java.util.List;

public class NoteEditorFragment extends Fragment {

    private static final String TAG = "NoteEditorFragment";

    public static final String FRAGMENT_TAG = "NoteEditorFragmentTAG";

    public static final String ACTION_NOTE_OPEN = "action open note";
    public static final String ACTION_NOTE_CREATE = "action create note";

    public static final String ARG_ACTION = "action arg";
    public static final String ARG_NOTE_ID = "note id arg";

    private EditText titleNoteField;
    private TextView noteTimeFiled;
    private Toolbar toolbar;
    private NotesViewModel notesViewModel;
    private EditorNoteInteraction interaction;
    private String action;
    private String noteId;
    private final NoteEditorAdapter noteEditorAdapter = new NoteEditorAdapter();

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
        return initView(inflater, container);
    }

    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_note_editor_view, container, false);
        // Set references
        titleNoteField = view.findViewById(R.id.title_note);
        toolbar = view.findViewById(R.id.toolbar);
        noteTimeFiled = view.findViewById(R.id.date_time_view);

        RecyclerView noteList = view.findViewById(R.id.note_list);
        noteList.setLayoutManager(new LinearLayoutManager(getContext()));
        noteList.setAdapter(noteEditorAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interaction != null) {
                    // Hide keyboard if it's open
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                    interaction.onBackNavigation();
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

        notesViewModel = new ViewModelProvider(requireActivity(), new NotesViewModelFactory(getActivity().getApplication()))
                .get(NotesViewModel.class);

        action = getArguments().getString(ARG_ACTION);
        noteId = getArguments().getString(ARG_NOTE_ID);

        processArgs();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        notesViewModel.cacheUserNote(noteEditorAdapter.getNoteList());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        List<NoteModel> cachedList = notesViewModel.getCachedUserNotes();
        if (cachedList != null) {
            noteEditorAdapter.setDataChanged(cachedList);
        }
    }

    private void processArgs() {

        if (action.equals(ACTION_NOTE_CREATE)) {
            // Display empty note
            noteEditorAdapter.prepareEmptyNote();
        } else if (action.equals(ACTION_NOTE_OPEN)) {

            final NoteModel note = notesViewModel.getNote(noteId);

            if (note != null) {
                // Set note title
                titleNoteField.setText(note.getTitle());

                // Set new data to list adapter. After that recycle view will be updated with
                // new data from list.
                List<NoteModel> noteList = new ArrayList<>();
                noteList.add(note);
                noteEditorAdapter.setDataChanged(noteList);

                String timeDate = note.getTime();
                if (!timeDate.isEmpty()) {
                    // TODO: Create helper class for such formatting and move this code away
                    String startText = getString(R.string.time_date_label);
                    String label = startText + " " + timeDate;

                    final StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
                    Spannable finalText = new SpannableStringBuilder(label);
                    finalText.setSpan(boldSpan, 0, startText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                    Spannable buffer = new SpannableString(finalText);
                    noteTimeFiled.setText(buffer);

                    noteTimeFiled.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Remove all items in the menu
        menu.clear();
        inflater.inflate(R.menu.editor_note_menu, menu);
    }

    @SuppressLint("NonConstantResourceId") // Suppress "Checks use of resource IDs in places requiring constants." warnning
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_note_item :

                /**
                 * Confirm dialog before delete
                 */
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

                /**
                 * Confirm dialog before save
                 */
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

                /**
                 * Clears Edit Text Views
                 */
                DialogHelper.showConfirmDialog(getActivity(), new DialogHelper.ConfirmDialogCallback() {
                    @Override
                    public void onOkClicked() {

                        List<NoteModel> noteModelList = noteEditorAdapter.getNoteList();

                        if (noteEditorAdapter.getNote().isEmpty()) {
                            GoodUtils.showToast(requireActivity(), R.string.toast_action_error_note_nothing_to_clear);
                            return;
                        }

                        for (NoteModel n : noteModelList) {
                            n.clearNotes();
                        }

                        noteEditorAdapter.setDataChanged(noteModelList);
                    }

                    @Override
                    public void onCancelClicked() {
                        // no-op
                    }
                }, R.string.confirm_dialog_clear_note_title, R.string.confirm_dialog_clear_note_message);

                return true;

            case R.id.item_type:
                /**
                 * Change note layout
                 */
                noteEditorAdapter.transformView();
            return true;
        }

        return false;
    }

    private void saveUserNote() {

        NoteModel notes = noteEditorAdapter.getNote();

        if (notes.isEmpty()) {

            GoodUtils.showToast(requireContext(), R.string.toast_action_error_note_is_empty);

            return;
        }

        boolean result;

        if (action.equals(ACTION_NOTE_OPEN)) {
            Log.info(TAG, "saveUserNote() updated note");
            result = notesViewModel.updateNote(noteId, notes);
        } else {
            Log.info(TAG, "saveUserNote() add new note");
            result = notesViewModel.addNote(notes);
        }

        /*
            Display info toast message
        */
        if (result) {
            Log.info(TAG, "saveUserNote() saved new note");

            GoodUtils.showToast(requireContext(), R.string.toast_action_message);
        }

    }

    private void deleteNote() {

        if (noteEditorAdapter.getNote().isEmpty()) {
            GoodUtils.showToast(requireContext(), R.string.toast_action_delete_error_note);
            return;
        }

        // Delete if this note already exists
        if (noteId != null && notesViewModel.deleteNote(noteId)) {
            GoodUtils.showToast(requireContext(), R.string.toast_action_deleted_message);
            if (interaction != null) {
                interaction.onDeleteNote();
            }
        } else {
            // Show warning message otherwise
            GoodUtils.showToast(requireContext(), R.string.toast_action_deleted_message_no_note);
        }

    }

    public interface EditorNoteInteraction {
        void onBackNavigation();
        void onDeleteNote();
    }

}
