package com.example.testcw.Observation;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcw.DataBaseHelper;
import com.example.testcw.R;
import com.example.testcw.MainActivity;

public class ViewObservationActivity extends AppCompatActivity {

    private DataBaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ObservationAdapter observationAdapter;
    private Cursor observationCursor;
    private final int hikeId;

    public ViewObservationActivity(int hikeId) {
        this.hikeId = hikeId;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_view);

        dbHelper = new DataBaseHelper(this);

        recyclerView = findViewById(R.id.recyclerObservationView);
        Button addObservationButton = findViewById(R.id.addObserBtn);
        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> onBackPressed());

        addObservationButton.setOnClickListener(v -> {
            // Get the hike ID from the intent
            int hikeId = getIntent().getIntExtra("HikeId", -1);

            if (hikeId != -1) {
                // Start the AddObservationActivity
                Intent intent = new Intent(ViewObservationActivity.this, AddObservationActivity.class);
                intent.putExtra("HikeId", hikeId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid Hike ID", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Initialize the observationAdapter here
        observationAdapter = new ObservationAdapter(this, null);

        // Set the onDeleteClickListener
        observationAdapter.setOnDeleteClickListener(position -> {
            if (observationCursor != null && observationCursor.moveToPosition(position)) {
                String observationId = observationCursor.getString(observationCursor.getColumnIndexOrThrow("observation_id"));
                dbHelper.deleteObservation(observationId);
                observationAdapter.swapCursor(dbHelper.readAllObservations(hikeId));
            }
        });

        // Display observations when the activity is created
        int hikeId = getIntent().getIntExtra("HikeId", -1);
        displayObservations(hikeId);
    }
    @Override
    public void onBackPressed() {
        // Navigate back to MainActivity
        super.onBackPressed();
        Intent intent = new Intent(ViewObservationActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to remove it from the stack
    }


    private void displayObservations(int hikeId) {
        observationCursor = dbHelper.readAllObservations(hikeId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        observationAdapter = new ObservationAdapter(this, observationCursor);

        recyclerView.setAdapter(observationAdapter);

        // Move the setOnDeleteClickListener here
        observationAdapter.setOnDeleteClickListener(position -> {
            if (observationCursor != null && observationCursor.moveToPosition(position)) {
                String observationId = observationCursor.getString(observationCursor.getColumnIndexOrThrow("observation_id"));
                dbHelper.deleteObservation(observationId);
                observationAdapter.swapCursor(dbHelper.readAllObservations(hikeId));
            }
        });
    }



}
