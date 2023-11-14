package com.example.testcw.Hike;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcw.DataBaseHelper;
import com.example.testcw.MainActivity;
import com.example.testcw.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText searchHikeTxt;
    HikeAdapter hikeAdapter;
    ArrayList<String> hikeId, nameHike, locationHike, dateHike, parkingAvailableHike, lengthOfHike, difficultLevelHike, descriptionHike;
    DataBaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_search);


        //bottom menu
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.add) {
                startActivity(new Intent(getApplicationContext(), AddActivity.class));
                return true;
            } else if (item.getItemId() == R.id.search) {
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.recyclerSearchView);
        searchHikeTxt = findViewById(R.id.searchHikeTxt);

        myDB = new DataBaseHelper(SearchActivity.this);
        hikeId = new ArrayList<>();
        nameHike = new ArrayList<>();
        locationHike = new ArrayList<>();
        dateHike = new ArrayList<>();
        parkingAvailableHike = new ArrayList<>();
        lengthOfHike = new ArrayList<>();
        difficultLevelHike = new ArrayList<>();
        descriptionHike = new ArrayList<>();

        storeDataInArrays();

        hikeAdapter = new HikeAdapter(SearchActivity.this, this, hikeId, nameHike, locationHike, dateHike, parkingAvailableHike, lengthOfHike, difficultLevelHike, descriptionHike, myDB);
        recyclerView.setAdapter(hikeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        searchHikeTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchName = searchHikeTxt.getText().toString().trim();
                ArrayList<String> searchData = myDB.searchHikeByName(searchName);
                hikeAdapter.updateHikeData(searchData);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                storeDataInArrays();
                hikeAdapter.notifyDataSetChanged();
            }
        }
    }

    void storeDataInArrays() {
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
