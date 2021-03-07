package com.serhii.apps.notes.ui.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.ui.data_model.PlansModel;

import java.util.ArrayList;
import java.util.List;

public class PlansRecyclerAdapter extends RecyclerView.Adapter<PlansRecyclerAdapter.PlansViewHolder> {

    private List<Integer> colors = new ArrayList<>();
    private int currentUsedColor = -1;

    private List<PlansModel> plansModels;
    private ClickListener clickListener;

    public PlansRecyclerAdapter(List<PlansModel> plansModels, ClickListener clickListener, Context context) {
        this.plansModels = plansModels;
        this.clickListener = clickListener;

        initColorsFromResources(context);
    }

    @NonNull
    @Override
    public PlansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_item_view, parent, false);

        return new PlansViewHolder(v, clickListener, getColor());
    }

    @Override
    public void onBindViewHolder(@NonNull PlansViewHolder holder, int position) {
        holder.setTitle(plansModels.get(position).getTitle());
        holder.setDateTime(plansModels.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return plansModels.size();
    }

    public static class PlansViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView dateTime;
        private View indicator;

        public PlansViewHolder(@NonNull View itemView, final ClickListener clickListener, int color) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            dateTime = itemView.findViewById(R.id.date_time_view);
            indicator = itemView.findViewById(R.id.indicator);

            indicator.setBackgroundColor(color);

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

        public void setDateTime(String dateTime) {
            this.dateTime.setText(dateTime);
        }

    }

    private int getColor() {
        if (currentUsedColor == -1) {
            // Get the first color
            currentUsedColor = 1;
            return colors.get(0);
        }

        if (currentUsedColor != colors.size()) {
            // Get the next color
            return colors.get(currentUsedColor++);
        } else {
            // Go to the first
            currentUsedColor = -1;
            return getColor();
        }
    }

    private void initColorsFromResources(Context context) {
        int[] intArrayColors = context.getResources().getIntArray(R.array.plan_item_colors);

        for (int i = 0; i < intArrayColors.length; i++) {
            colors.add(intArrayColors[i]);
        }
    }

    public interface ClickListener {

        void onClick(int position);
    }

}
