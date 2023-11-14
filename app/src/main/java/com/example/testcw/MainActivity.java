package com.example.testcw;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcw.Hike.AddActivity;
import com.example.testcw.Hike.HikeAdapter;
import com.example.testcw.Hike.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DataBaseHelper myDB;
    ArrayList<String>hikeId, nameHike, locationHike, dateHike,parkingAvailableHike,lengthOfHike,difficultLevelHike,descriptionHike;
    Button deleteAllBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.home);
        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.add) {
                startActivity(new Intent(getApplicationContext(), AddActivity.class));
                return true;
            } else if (item.getItemId() == R.id.home) {
                return true;
            } else if (item.getItemId() == R.id.search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.recyclerView);

        myDB = new DataBaseHelper(MainActivity.this);
        hikeId = new ArrayList<>();
        nameHike = new ArrayList<>();
        locationHike = new ArrayList<>();
        dateHike = new ArrayList<>();
        parkingAvailableHike = new ArrayList<>();
        lengthOfHike = new ArrayList<>();
        difficultLevelHike = new ArrayList<>();
        descriptionHike = new ArrayList<>();

        storeDataInArrays();

        HikeAdapter adapter = new HikeAdapter(this, this, hikeId, nameHike, locationHike, dateHike,
                parkingAvailableHike, lengthOfHike, difficultLevelHike, descriptionHike,myDB);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        deleteAllBtn=findViewById(R.id.deleteAllBtn);
        deleteAllBtn.setOnClickListener(v -> confirmDialogDeleteAll());


    }

     void confirmDialogDeleteAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all Data?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            DataBaseHelper myDB = new DataBaseHelper(MainActivity.this);
            myDB.deleteAllHike();
            //Refresh Activity
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate(); // Recreate the activity to refresh the data
        }
    }
    void storeDataInArrays(){
        hikeId.clear();
        nameHike.clear();
        locationHike.clear();
        dateHike.clear();
        parkingAvailableHike.clear();
        lengthOfHike.clear();
        difficultLevelHike.clear();
        descriptionHike.clear();

        Cursor cursor = myDB.readAllHike();
        while (cursor.moveToNext()) {
            hikeId.add(cursor.getString(0));
            nameHike.add(cursor.getString(1));
            locationHike.add(cursor.getString(2));
            dateHike.add(cursor.getString(3));
            parkingAvailableHike.add(cursor.getString(4));
            lengthOfHike.add(cursor.getString(5));
            difficultLevelHike.add(cursor.getString(6));
            descriptionHike.add(cursor.getString(7));
        }
    }

}