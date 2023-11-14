package com.example.testcw;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.Console;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    private final Context context;
    //Hike
    private static final String DATABASE_NAME = "hikeDb.db";
    private static final int DATABASE_VERSION = 2;
    static final String TABLE_NAME = "hikeDb";
    private static final String COLUMN_ID = "_id";
    static final String COLUMN_NAME = "name";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_PARKING_AVAILABLE = "parkingavailable";
    private static final String COLUMN_LENGTH = "length";
    private static final String COLUMN_DIFFICULT_LEVEL = "difficultlevel";
    private static final String COLUMN_DESCRIPTION = "description";

    //Observation
    private static final String TABLE_OBSERVATION = "observation";
    private static final String COLUMN_OBSERVATION_ID = "observation_id";
    private static final String COLUMN_HIKE_ID = "hike_id";
    private static final String COLUMN_OBSERVATION = "observation";
    private static final String COLUMN_TIME_OF_OBSERVATION = "time_of_observation";
    private static final String COLUMN_COMMENT = "comment";
    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the "hikeDb" table
        String hikeDbTableQuery = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_PARKING_AVAILABLE + " TEXT, " +
                COLUMN_LENGTH + " INTEGER, " +
                COLUMN_DIFFICULT_LEVEL + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT);";

        // Create the "observation" table
        String observationTableQuery = "CREATE TABLE " + TABLE_OBSERVATION +
                " (" + COLUMN_OBSERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HIKE_ID + " INTEGER, " +
                COLUMN_OBSERVATION + " TEXT, " +
                COLUMN_TIME_OF_OBSERVATION + " TEXT, " +
                COLUMN_COMMENT + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_HIKE_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + ")" +
                ");";

        Log.d("HikeDbTableQuery", hikeDbTableQuery); // Log the SQL query for hikeDb table
        Log.d("ObservationTableQuery", observationTableQuery); // Log the SQL query for observation table

        db.execSQL(hikeDbTableQuery);
        db.execSQL(observationTableQuery);
    }





    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATION);

        onCreate(db);
    }
    public void addNewHike(String name, String location, String date, String parkingAvailable, int length, String difficultlevel, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_LOCATION, location);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_PARKING_AVAILABLE, parkingAvailable);
        cv.put(COLUMN_LENGTH, length);
        cv.put(COLUMN_DIFFICULT_LEVEL, difficultlevel);
        cv.put(COLUMN_DESCRIPTION, description);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }
    public Cursor readAllHike(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    public void updateHike(String id, String name, String location, String date, Boolean parkingAvailable, String length, String difficultLevel, String description){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_LOCATION, location);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_PARKING_AVAILABLE, parkingAvailable);
        cv.put(COLUMN_LENGTH, length);
        cv.put(COLUMN_DIFFICULT_LEVEL, difficultLevel);
        cv.put(COLUMN_DESCRIPTION, description);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{id});
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }

    }
    public void deleteOneHike(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }
    public ArrayList<String> searchHikeByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + name + "%"};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        ArrayList<String> searchData = new ArrayList<>();
        while (cursor.moveToNext()) {
            searchData.add(cursor.getString(0));
            searchData.add(cursor.getString(1));
            searchData.add(cursor.getString(2));
            searchData.add(cursor.getString(3));
            searchData.add(cursor.getString(4));
            searchData.add(cursor.getString(5));
            searchData.add(cursor.getString(6));
            searchData.add(cursor.getString(7));
        }
        cursor.close(); // Close the cursor when done
        return searchData;
    }
    void deleteAllHike(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    //Observation
    public void addObservation(int hikeId, String observation, String timeOfObservation, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_HIKE_ID, hikeId);
        cv.put(COLUMN_OBSERVATION, observation);
        cv.put(COLUMN_TIME_OF_OBSERVATION, timeOfObservation);
        cv.put(COLUMN_COMMENT, comment);
        long result = db.insert(TABLE_OBSERVATION, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed to add observation", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Observation added successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateObservation(String observationId, String observation, String timeOfObservation, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_OBSERVATION, observation);
        cv.put(COLUMN_TIME_OF_OBSERVATION, timeOfObservation);
        cv.put(COLUMN_COMMENT, comment);
        long result = db.update(TABLE_OBSERVATION, cv, COLUMN_OBSERVATION_ID + "=?", new String[]{observationId});
        if (result == -1) {
            Toast.makeText(context, "Failed to update observation", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Observation updated successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllObservations(int hikeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_OBSERVATION + " WHERE " + COLUMN_HIKE_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(hikeId)};
        return db.rawQuery(query, selectionArgs);
    }

    public void deleteObservation(String observationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_OBSERVATION, COLUMN_OBSERVATION_ID + "=?", new String[]{observationId});
        if (result == -1) {
            Toast.makeText(context, "Failed to delete observation", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Observation deleted successfully!", Toast.LENGTH_SHORT).show();
        }
    }
    public int getHikeIdForObservation(String observationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_HIKE_ID + " FROM " + TABLE_OBSERVATION +
                " WHERE " + COLUMN_OBSERVATION_ID + " = ?";
        String[] selectionArgs = new String[]{observationId};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        int hikeId = -1; // Default value if not found
        if (cursor != null && cursor.moveToFirst()) {
            hikeId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HIKE_ID));
            cursor.close();
        }

        return hikeId;
    }
    public Cursor readObservationDetails(String observationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_OBSERVATION +
                " WHERE " + COLUMN_OBSERVATION_ID + " = ?";
        String[] selectionArgs = new String[]{observationId};

        return db.rawQuery(query, selectionArgs);
    }
    public boolean fetchParkingAvailability(String hikeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_PARKING_AVAILABLE + " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{hikeId};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        boolean parkingAvailable = false; // Default value if not found
        if (cursor != null && cursor.moveToFirst()) {
            parkingAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PARKING_AVAILABLE)) == 1;
            cursor.close();
        }

        return parkingAvailable;
    }

}
