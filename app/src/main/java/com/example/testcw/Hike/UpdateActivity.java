package com.example.testcw.Hike;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.testcw.DataBaseHelper;
import com.example.testcw.R;

import java.time.LocalDate;

public class UpdateActivity extends AppCompatActivity {

    EditText nameHikeEdit, locationHikeEdit, lengthOfHikeEdit, descriptionEdit;
    Spinner levelSpinnerEdit;
    RadioGroup radioGroupEdit;
    RadioButton yesBtnEdit, noBtnEdit;
    TextView dateOfHikeEdit;
    Button updateBtn;
    ArrayAdapter<CharSequence> adapter;
    String id, name, location, date, lengthOfHike, difficultLevel, description;
    Boolean parkingAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_update);

        nameHikeEdit = findViewById(R.id.nameEdit);
        locationHikeEdit = findViewById(R.id.locationEdit);
        dateOfHikeEdit = findViewById(R.id.dateOfHikeEdit);
        radioGroupEdit = findViewById(R.id.radioGroupEdit);
        lengthOfHikeEdit = findViewById(R.id.lengthEdit);
        yesBtnEdit = findViewById(R.id.yesBtnEdit);
        noBtnEdit = findViewById(R.id.noBtnEdit);
        levelSpinnerEdit = findViewById(R.id.difficultLevelEdit);
        descriptionEdit = findViewById(R.id.decriptionEdit);
        updateBtn = findViewById(R.id.updateBtn);

        // Initialize the Spinner with items from strings.xml
        adapter = ArrayAdapter.createFromResource(this, R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinnerEdit.setAdapter(adapter);

        // Set up the date picker
        dateOfHikeEdit.setOnClickListener(view -> showDatePicker());
        updateBtn.setOnClickListener(view -> updateHike());
        getAndSetIntentData();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(name);
        }
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("name")
                && getIntent().hasExtra("location") && getIntent().hasExtra("date")
                && getIntent().hasExtra("length") && getIntent().hasExtra("difficultLevel")
                && getIntent().hasExtra("description")) {
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            location = getIntent().getStringExtra("location");
            date = getIntent().getStringExtra("date");
            lengthOfHike = getIntent().getStringExtra("length");
            difficultLevel = getIntent().getStringExtra("difficultLevel");
            description = getIntent().getStringExtra("description");

            // Fetch parking availability from the database
            parkingAvailable = fetchParkingAvailabilityFromDatabase(id);

            // Setting Intent Data
            nameHikeEdit.setText(name);
            locationHikeEdit.setText(location);
            dateOfHikeEdit.setText(date);
            if (parkingAvailable) {
                yesBtnEdit.setChecked(true);
            } else {
                noBtnEdit.setChecked(true);
            }
            lengthOfHikeEdit.setText(lengthOfHike);
            int spinnerPosition = adapter.getPosition(difficultLevel);
            levelSpinnerEdit.setSelection(spinnerPosition);
            descriptionEdit.setText(description);
        }
    }

    private boolean fetchParkingAvailabilityFromDatabase(String hikeId) {
        DataBaseHelper myDB = new DataBaseHelper(this);
        return myDB.fetchParkingAvailability(hikeId);
    }

    private void showDatePicker() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void updateDate(LocalDate dob) {
        dateOfHikeEdit.setText(dob.toString());
    }

    private void updateHike() {
        // Retrieve values from UI elements
        name = nameHikeEdit.getText().toString().trim();
        location = locationHikeEdit.getText().toString().trim();
        date = dateOfHikeEdit.getText().toString().trim();

        // Check which radio button is selected
        int radioId = radioGroupEdit.getCheckedRadioButtonId();
        if (radioId == R.id.yesBtnEdit) {
            parkingAvailable = true;
        } else if (radioId == R.id.noBtnEdit) {
            parkingAvailable = false;
        } else {
            // Handle the case when no radio button is selected
            parkingAvailable = false; // or set a default value
        }

        // Validate and parse length of the hike
        String lengthOfHikeStr = lengthOfHikeEdit.getText().toString().trim();
        if (lengthOfHikeStr.isEmpty()) {
            Toast.makeText(this, "Please enter the length of the hike.", Toast.LENGTH_SHORT).show();
            return;
        }

        int lengthOfHikeInt;
        try {
            lengthOfHikeInt = Integer.parseInt(lengthOfHikeStr);
            if (lengthOfHikeInt < 0) {
                Toast.makeText(this, "Hike length should be a positive number.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for hike length.", Toast.LENGTH_SHORT).show();
            return;
        }

        difficultLevel = levelSpinnerEdit.getSelectedItem().toString().trim();
        description = descriptionEdit.getText().toString().trim();

        // Check if any of the required fields are empty
        if (name.isEmpty() || location.isEmpty() || date.isEmpty() || difficultLevel.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
        } else {
            // Perform database update
            DataBaseHelper myDB = new DataBaseHelper(this);
            myDB.updateHike(id, name, location, date, parkingAvailable, String.valueOf(lengthOfHikeInt), difficultLevel, description);
            finish(); // Close the activity after updating
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LocalDate currentDate = LocalDate.now();
            int year = currentDate.getYear();
            int month = currentDate.getMonthValue() - 1;
            int day = currentDate.getDayOfMonth();
            return new DatePickerDialog(requireActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            LocalDate selectedDate = LocalDate.of(year, month + 1, day);
            ((UpdateActivity) requireActivity()).updateDate(selectedDate);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate(); // Recreate the activity to refresh the data
        }
    }
}
