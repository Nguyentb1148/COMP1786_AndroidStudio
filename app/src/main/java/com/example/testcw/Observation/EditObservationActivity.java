package com.example.testcw.Observation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testcw.DataBaseHelper;
import com.example.testcw.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditObservationActivity extends AppCompatActivity {

    private EditText editObserText, editCommentText;
    TextView editTimeObserText;
    private String observationId;
    private DataBaseHelper dbHelper;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_edit);

        dbHelper = new DataBaseHelper(this);

        editObserText = findViewById(R.id.editOberText);
        editTimeObserText = findViewById(R.id.editTimeObserText);
        editCommentText = findViewById(R.id.editeCommentText);
        Button editObserBtn = findViewById(R.id.editObserBtn);

        // Get the observationId from the intent
        observationId = String.valueOf(getIntent().getIntExtra("ObservationId", -1));

        // Check if the observationId is valid before using it
        if (observationId.equals("-1")) {
            Toast.makeText(this, "Invalid Observation ID", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity if the Observation ID is invalid
        }

        // Load observation details
        loadObservationDetails();

        // Set up click listener for editing time
        editTimeObserText.setOnClickListener(v -> showDateTimePicker());

        editObserBtn.setOnClickListener(v -> {
            // Get the values from the EditText fields
            String observation = editObserText.getText().toString().trim();
            String timeOfObservation = editTimeObserText.getText().toString().trim();
            String comment = editCommentText.getText().toString().trim();

            // Check if any of the fields is empty
            if (observation.isEmpty() || timeOfObservation.isEmpty() || comment.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Update the observation in the database
                dbHelper.updateObservation(observationId, observation, timeOfObservation, comment);

                // Show a success message
                Toast.makeText(this, "Observation updated successfully", Toast.LENGTH_SHORT).show();

                // Go back to the ViewObservationActivity
                Intent intent = new Intent(EditObservationActivity.this, ViewObservationActivity.class);
                intent.putExtra("HikeId", dbHelper.getHikeIdForObservation(observationId));
                startActivity(intent);
                finish();
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void loadObservationDetails() {
        // Get observation details from the database based on the observationId
        Cursor observationCursor = dbHelper.readObservationDetails(observationId);

        // Check if the cursor is not null and move to the first row
        if (observationCursor != null && observationCursor.moveToFirst()) {
            // Populate the EditText fields with observation details
            editObserText.setText(observationCursor.getString(observationCursor.getColumnIndexOrThrow("observation")));
            editTimeObserText.setText(observationCursor.getString(observationCursor.getColumnIndexOrThrow("time_of_observation")));
            editCommentText.setText(observationCursor.getString(observationCursor.getColumnIndexOrThrow("comment")));
        }

        // Close the cursor when done
        if (observationCursor != null) {
            observationCursor.close();
        }
    }

    private void showDateTimePicker() {
        // Get the current date and time
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Show DatePickerDialog to select date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    // Show TimePickerDialog to select time
                    TimePickerDialog timePickerDialog = new TimePickerDialog(EditObservationActivity.this,
                            (view1, hourOfDay, minute) -> {
                                mHour = hourOfDay;
                                mMinute = minute;

                                // Set selected date and time to the EditText
                                editTimeObserText.setText(formatDateTime(mYear, mMonth + 1, mDay, mHour, mMinute));
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private String formatDateTime(int year, int month, int day, int hour, int minute) {
        // Format the date and time
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute);
        return sdf.format(calendar.getTime());
    }
}
