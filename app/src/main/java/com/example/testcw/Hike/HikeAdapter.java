package com.example.testcw.Hike;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcw.DataBaseHelper;
import com.example.testcw.Observation.ViewObservationActivity;
import com.example.testcw.R;

import java.util.ArrayList;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.MyViewHolder> {

    private final Context context;
    private final Activity activity;
    private final ArrayList<String> hikeId;
    private final ArrayList<String> nameHike;
    private final ArrayList<String> locationHike;
    private final ArrayList<String> dateHike;
    private final ArrayList<String> parkingAvailableHike;
    private final ArrayList<String> lengthOfHike;
    private final ArrayList<String> difficultLevelHike;
    private final ArrayList<String> descriptionHike;
    DataBaseHelper myDB;


    public HikeAdapter(Context context, Activity activity, ArrayList<String> hikeId, ArrayList<String> nameHike, ArrayList<String> locationHike, ArrayList<String> dateHike, ArrayList<String> parkingAvailableHike, ArrayList<String> lengthOfHike, ArrayList<String> difficultLevelHike, ArrayList<String> descriptionHike, DataBaseHelper myDB) {
        this.context = context;
        this.activity = activity;
        this.hikeId = hikeId;
        this.nameHike = nameHike;
        this.locationHike = locationHike;
        this.dateHike = dateHike;
        this.parkingAvailableHike = parkingAvailableHike;
        this.lengthOfHike = lengthOfHike;
        this.difficultLevelHike = difficultLevelHike;
        this.descriptionHike = descriptionHike;
        this.myDB = myDB; // Initialize myDB
    }
    public void updateHikeData(ArrayList<String> hikeData) {
        // Clear the existing data
        this.hikeId.clear();
        nameHike.clear();
        locationHike.clear();
        dateHike.clear();
        parkingAvailableHike.clear();
        lengthOfHike.clear();
        difficultLevelHike.clear();
        descriptionHike.clear();

        // Add the new data
        for (int i = 0; i < hikeData.size(); i += 8) {
            this.hikeId.add(hikeData.get(i));
            nameHike.add(hikeData.get(i + 1));
            locationHike.add(hikeData.get(i + 2));
            dateHike.add(hikeData.get(i + 3));
            parkingAvailableHike.add(hikeData.get(i + 4));
            lengthOfHike.add(hikeData.get(i + 5));
            difficultLevelHike.add(hikeData.get(i + 6));
            descriptionHike.add(hikeData.get(i + 7));
        }
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hike_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") int i) {

        holder.position = i; // Set the position
        holder.hikeName.setText(String.valueOf(nameHike.get(i)));
        holder.hikeName.setText(String.valueOf(nameHike.get(i)));
        holder.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, UpdateActivity.class);
            intent.putExtra("id", String.valueOf(hikeId.get(i)));
            intent.putExtra("name", String.valueOf(nameHike.get(i)));
            intent.putExtra("location", String.valueOf(locationHike.get(i)));
            intent.putExtra("date", String.valueOf(((dateHike.get(i)))));
            intent.putExtra("parking", String.valueOf((parkingAvailableHike.get(i))));
            intent.putExtra("length", String.valueOf((lengthOfHike.get(i))));
            intent.putExtra("difficultLevel", String.valueOf((difficultLevelHike.get(i))));
            intent.putExtra("description", String.valueOf((descriptionHike.get(i))));
            //hikeId,nameHike,locationHike,dateHike,parkingAvailableHike,lengthOfHike,difficultLevelHike,descriptionHike
            activity.startActivityForResult(intent, 1); // Use context to start the activity
        });

        holder.moreBtn.setOnClickListener(view -> {
            int hikeId = Integer.parseInt(HikeAdapter.this.hikeId.get(i)); // Use the instance variable
            Intent intent = new Intent(context, ViewObservationActivity.class);
            intent.putExtra("HikeId", hikeId);
            activity.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        if (hikeId != null) {
            return hikeId.size();
        } else {
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView hikeName;
        LinearLayout mainLayout;
        Button deleteBtn;
        Button moreBtn;
        int position; // Declare the position variable
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            hikeName = itemView.findViewById(R.id.hikeNameView);
            mainLayout=itemView.findViewById(R.id.mainLayout);
            deleteBtn = itemView.findViewById(R.id.DeleteHikeBtn);
            moreBtn = itemView.findViewById(R.id.moreBtn); // Initialize moreBtn

            deleteBtn.setOnClickListener(view -> confirmDialog(position));
            moreBtn.setOnClickListener(view -> {
                Intent intent = new Intent(context, ViewObservationActivity.class);
                intent.putExtra("hikeId", String.valueOf(hikeId.get(position)));
                activity.startActivity(intent);
            });
        }
    }
    private void confirmDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + nameHike.get(position) + " ?");
        builder.setMessage("Are you sure you want to delete " + nameHike.get(position) + " ?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            DataBaseHelper myDb = new DataBaseHelper(context);
            myDb.deleteOneHike(String.valueOf(hikeId.get(position)));
            // Reload data and update the RecyclerView
            storeDataInArrays(); // Add this to refresh data
            notifyDataSetChanged(); // Notify the adapter of the data change
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle the cancel action
            }
        });
        builder.create().show();
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
