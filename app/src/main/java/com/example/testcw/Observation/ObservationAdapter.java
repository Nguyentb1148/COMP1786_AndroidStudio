package com.example.testcw.Observation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcw.DataBaseHelper;
import com.example.testcw.R;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder> {

    private final Context context;
    private Cursor cursor;

    public ObservationAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public ObservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.observation_item, parent, false);
        return new ObservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservationViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            String observation = cursor.getString(cursor.getColumnIndexOrThrow("observation"));
            String observationId = cursor.getString(cursor.getColumnIndexOrThrow("observation_id"));

            holder.observationTextView.setText(observation);

            // Set click listener for the Edit button
            holder.editObserItemBtn.setOnClickListener(v -> {
                // Start the EditObservationActivity with the observationId
                Intent intent = new Intent(context, EditObservationActivity.class);
                intent.putExtra("ObservationId", Integer.parseInt(observationId));
                context.startActivity(intent);
            });

            // Set click listener for the Delete button
            holder.deleteObserBtn.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
    public Cursor getCursor() {
        return cursor;
    }

    public static class ObservationViewHolder extends RecyclerView.ViewHolder {
        TextView observationTextView;
        Button editObserItemBtn, deleteObserBtn;

        public ObservationViewHolder(@NonNull View itemView) {
            super(itemView);
            observationTextView = itemView.findViewById(R.id.nameObserTxt);
            editObserItemBtn = itemView.findViewById(R.id.editObserItemBtn);
            deleteObserBtn = itemView.findViewById(R.id.deleteObserBtn);
        }
    }
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        onDeleteClickListener = listener;
    }
}
