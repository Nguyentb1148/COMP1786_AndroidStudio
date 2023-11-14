package com.example.testcw.Observation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testcw.DataBaseHelper;
import com.example.testcw.R;

import java.util.Calendar;

public class AddObservationActivity extends AppCompatActivity {

    private EditText nameObserText, commentObserText;
    TextView timeOfObserText;
    private Button addNewObserBtn;
    private int hikeId;
    private DataBaseHelper dbHelper;
    private ImageButton backButton;

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_add);

        dbHelper = new DataBaseHelper(this);

        nameObserText = findViewById(R.id.nameObserText);
        timeOfObserText = findViewById(R.id.timeOfObserText);
        commentObserText = findViewById(R.id.commentObserText);
        addNewObserBtn = findViewById(R.id.addNewObserBtn);

        // Get the hikeId from the intent
        hikeId = getIntent().getIntExtra("HikeId", -1);
        String stringhikeId= String.valueOf(hikeId);
        Toast.makeText(this, "hike id: "+stringhikeId, Toast.LENGTH_SHORT).show();

        // Check if the hikeId is valid before using it
        if (hikeId == -1) {
            Toast.makeText(this, "Invalid Hike ID", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity if the Hike ID is invalid
        }

        // Set up click listener for selecting time
        timeOfObserText.setOnClickListener(v -> showDateTimePicker());

        // Set up click listener for the add button
        addNewObserBtn.setOnClickListener(v -> {
            // Get the values from the EditText fields
            String observation = nameObserText.getText().toString().trim();
            String comment = commentObserText.getText().toString().trim();

            // Check if any of the fields is empty
            if (observation.isEmpty() || comment.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Add the observation to the database
                dbHelper.addObservation(hikeId, observation, getDateTime(), comment); // Pass hikeId as int

                // Show a success message
                Toast.makeText(this, "Observation added successfully", Toast.LENGTH_SHORT).show();

                // Go back to the ViewObservationActivity
                Intent intent = new Intent(AddObservationActivity.this, ViewObservationActivity.class);
                intent.putExtra("HikeId", hikeId); // Pass hikeId as int
                startActivity(intent);
                finish();
            }
        });

        // Set up click listener for the back button
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
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
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddObservationActivity.this,
                            (view1, hourOfDay, minute) -> {
                                mHour = hourOfDay;
                                mMinute = minute;

                                // Set selected date and time to the EditText
                                timeOfObserText.setText(String.format("%02d/%02d/%04d %02d:%02d", mDay, mMonth + 1, mYear, mHour, mMinute));
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private String getDateTime() {
        // Return formatted date and time
        return String.format("%02d/%02d/%04d %02d:%02d", mDay, mMonth + 1, mYear, mHour, mMinute);
    }
}
