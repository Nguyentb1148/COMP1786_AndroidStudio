package com.example.testcw.Hike;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testcw.DataBaseHelper;
import com.example.testcw.MainActivity;
import com.example.testcw.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;

public class AddActivity extends AppCompatActivity {


    int radioId;
    RadioButton parkingAvailable;
    RadioGroup radioGroup;
    TextView dateControl;
    EditText nameHike, locationHike, lengthOfHike, description;
    Spinner levelSpinner;
    Button addBtn;
    String radioButtonValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_add);

        //bottom menu
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.add) {
                return true;
            } else if (item.getItemId() == R.id.search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                return true;
            }
            return false;
        });


        nameHike = findViewById(R.id.nameHikeAdd);
        locationHike = findViewById(R.id.locationHikeAdd);
        dateControl = findViewById(R.id.dateOfHikeAdd);
        radioGroup = findViewById(R.id.radioGroupAdd);
        parkingAvailable = findViewById(radioId);
        lengthOfHike = findViewById(R.id.lengthOfHikeAdd);
        // Initialize the Spinner
        levelSpinner = findViewById(R.id.levelDifficultyAdd);
        description = findViewById(R.id.descriptionhikeAdd);

        //define adapter for all hikes
        // Populate the Spinner with items from strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(adapter);
        dateControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        addBtn = findViewById(R.id.addHikeBtn);
        addBtn.setOnClickListener(v -> addNewHike());
    }
    public void addNewHike() {
        String name = nameHike.getText().toString().trim();
        String location = locationHike.getText().toString().trim();
        String date = dateControl.getText().toString().trim();
        String length = lengthOfHike.getText().toString().trim();
        String difficulty = levelSpinner.getSelectedItem().toString().trim();
        String hikeDescription = description.getText().toString().trim();

        if (name.isEmpty() || location.isEmpty() || date.isEmpty() || length.isEmpty() || difficulty.isEmpty() || hikeDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                int hikeLength = Integer.parseInt(length);

                if (hikeLength < 0) {
                    Toast.makeText(this, "Hike length should be a positive number.", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Name : " + name +
                            "\nLocation : " + location +
                            "\nDate of the hike : " + date +
                            "\nParking available : " + radioButtonValue +
                            "\nLength of the hike : " + hikeLength +
                            "\nDifficulty : " + difficulty +
                            "\nDescription : " + hikeDescription +
                            "\nAre you sure ?");
                    builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                        DataBaseHelper db = new DataBaseHelper(AddActivity.this);
                        db.addNewHike(name, location, date, radioButtonValue, hikeLength, difficulty, hikeDescription);
                    });
                    builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                        // Do nothing if canceled
                    });
                    builder.create().show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number for hike length.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkButton(View v) {
        radioId = radioGroup.getCheckedRadioButtonId();
        parkingAvailable = findViewById(radioId);
        if (parkingAvailable != null) {
            // A radio button is selected, so you can access its text
            radioButtonValue = parkingAvailable.getText().toString();
            // Now you can use the radioButtonValue as needed
        }
    }

    // DatePicker Fragment inside MainActivity
    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            LocalDate d = LocalDate.now();
            int year = d.getYear();
            int month = d.getMonthValue();
            int day = d.getDayOfMonth();
            return new DatePickerDialog(getActivity(), this, year, --month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            LocalDate dob = LocalDate.of(year, ++month, day);
            ((AddActivity) getActivity()).updateDate(dob);
        }
    }

    public void updateDate(LocalDate dob) {
        TextView dobControl = findViewById(R.id.dateOfHikeAdd);
        dobControl.setText(dob.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate(); // Recreate the activity to refresh the data
        }
    }

}
