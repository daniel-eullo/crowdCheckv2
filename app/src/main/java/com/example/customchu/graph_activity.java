package com.example.customchu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class graph_activity extends AppCompatActivity {
    ImageButton graphBack;
    DatabaseReference databaseReference2;
    TextView graphStat;
    Map<String,Map<String,Integer>> dateHourCountMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        graphStat = findViewById(R.id.graphStat);

        graphBack = findViewById(R.id.graphBack);
        graphBack.setOnClickListener(view -> {
            Intent intent = new Intent(graph_activity.this, home.class);
            startActivity(intent);
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference2 = database.getReference("Rooms").child("GF").child("History");
        //databaseReference2 = database.getReference("Rooms").child("GF").child("History").child("Rooms").child("GF").child("History");



        databaseReference2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (DataSnapshot accountSnapshot : snapshot.getChildren()){
                    String dateAndTime = accountSnapshot.child("date_and_time").getValue(String.class);

                    String date = getDate(dateAndTime);
                    String hour = getHour(dateAndTime);

                    if (dateHourCountMap.containsKey(date)) {
                        Map<String, Integer> hourCountMap = dateHourCountMap.get(date);
                        if (hourCountMap.containsKey(hour)) {
                            int count = hourCountMap.get(hour);
                            hourCountMap.put(hour, count + 1);
                        } else {
                            hourCountMap.put(hour, 1);
                        }
                    } else {
                        Map<String, Integer> hourCountMap = new HashMap<>();
                        hourCountMap.put(hour, 1);
                        dateHourCountMap.put(date, hourCountMap);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateGraphStat();
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    private String getDayAndHour(String dateAndTime) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        try{
//            Date dateTime = dateFormat.parse(dateAndTime);
//            SimpleDateFormat dayHourFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//            return dayHourFormat.format(dateTime);
//        } catch (ParseException e){
//            e.printStackTrace();
//            return "";
//        }
//    }
//    private void updateTextView(Map<String, Integer> dayHourCountMap){
//        StringBuilder result = new StringBuilder();
//        for (Map.Entry<String, Integer> entry : dayHourCountMap.entrySet()){
//            result.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
//        }
//        graphStat.setText(result.toString());
//    }



    private String getDate(String dateAndTime) {
        if (dateAndTime == null){
            return "";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date dateTime = dateFormat.parse(dateAndTime);
            SimpleDateFormat dateFormatOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormatOnly.format(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getHour(String dateAndTime) {
        if (dateAndTime == null) {
            return "";
        }

        Log.d("DEBUG", "dateAndTime: " + dateAndTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date dateTime = dateFormat.parse(dateAndTime);
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH", Locale.getDefault());
            return hourFormat.format(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    private void updateGraphStat() {
        // Update the TextView with the counts
        StringBuilder stringBuilder = new StringBuilder();

        // Flag to skip the first entry when building the string
        boolean isFirstEntry = true;

        for (Map.Entry<String, Map<String, Integer>> entry : dateHourCountMap.entrySet()) {
            if (isFirstEntry) {
                isFirstEntry = false;
                continue; // Skip the first entry
            }

            String date = entry.getKey();
            Map<String, Integer> hourCountMap = entry.getValue();

            for (Map.Entry<String, Integer> hourEntry : hourCountMap.entrySet()) {
                String hour = hourEntry.getKey();
                int count = hourEntry.getValue();
                stringBuilder.append(date).append(" ").append(hour).append(": ").append(count).append("\n");
            }
        }

        graphStat.setText(stringBuilder.toString());
    }


}