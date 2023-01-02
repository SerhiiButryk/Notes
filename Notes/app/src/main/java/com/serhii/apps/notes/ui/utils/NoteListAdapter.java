package com.serhii.apps.notes.ui.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.ui.data_model.NoteModel;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    private List<NoteModel> notes = new ArrayList<>();
    private final NoteViewHolder.ClickListener clickListener;

    public NoteListAdapter(NoteViewHolder.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_view, parent, false);

        return new NoteViewHolder(view, new ClickListener() {
            @Override
            public void onClick(int position) {
                if (clickListener != null) {
                    clickListener.onClick(notes.get(position));
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i) {
        noteViewHolder.setTitle(notes.get(i).getTitle());
        noteViewHolder.setDescription(notes.get(i).getNote());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataChanged(List<NoteModel> noteModels) {
        notes = noteModels;
        notifyDataSetChanged();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;

        public NoteViewHolder(@NonNull View itemView, final NoteListAdapter.ClickListener clickListener) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_note_title);
            description = itemView.findViewById(R.id.tv_note_description);

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickListener.onClick(position);
                        }
                    }
                });
            }

        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setDescription(String description) {
            this.description.setText(description);
        }

        public interface ClickListener {
            void onClick(NoteModel noteModel);
        }

    }

    interface ClickListener {
        void onClick(int position);
    }

}
