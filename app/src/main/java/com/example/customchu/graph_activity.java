package com.example.customchu;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class graph_activity extends AppCompatActivity {
    ImageButton graphBack;
    TextView statTxt;
    DatabaseReference databaseReference2;
    ArrayList<BarEntry> barArrayList;
    BarChart barChart;
    Button to2FLogs;

    @Override
    //?android:attr/textColorPrimary
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        graphBack = findViewById(R.id.graphBack);
        graphBack.setOnClickListener(view -> {
            Intent intent = new Intent(graph_activity.this, home.class);
            startActivity(intent);
        });

        to2FLogs = findViewById(R.id.to2FLogs);
        to2FLogs.setOnClickListener(view -> {
            Intent intent = new Intent(graph_activity.this, activity_graph2.class);
            startActivity(intent);
        });
        //statTxt = findViewById(R.id.statTxt);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference2 = database.getReference("Rooms").child("GF").child("History");

        barArrayList = new ArrayList<>();
        barChart = findViewById(R.id.barChart);


        // Color and design change


        // Assume you have a class for user information, adjust it accordingly
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Integer> hourCounts = new HashMap<>();

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot userSnapshot : dateSnapshot.getChildren()) {
                        // Get the date_and_time from user information
                        String dateTime = userSnapshot.child("date_and_time").getValue(String.class);

                        // Check if dateTime is not null before splitting
                        if (dateTime != null) {
                            // Extract only the time part (HH:mm:ss)
                            String[] dateTimeParts = dateTime.split(" ");
                            if (dateTimeParts.length > 1) {
                                String time = dateTimeParts[1];

                                // Extract only the hour part (HH)
                                String hour = time.split(":")[0];

                                // Update the count for this hour
                                hourCounts.put(hour, hourCounts.getOrDefault(hour, 0) + 1);
                            }
                        }
                    }
                }

                // Populate barArrayList with actual data from Firebase
                getData(hourCounts);

                // Update the text view with the log counts
                //updateGraphStat(hourCounts);

                // Notify the chart that the data has changed
                BarDataSet barDataSet = new BarDataSet(barArrayList, "Log Activity");
                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);
                barDataSet.setColors(Color.parseColor("#4E7AC7"));
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(0f);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setTextSize(12f);

                YAxis leftYAxis = barChart.getAxisLeft();
                leftYAxis.setTextSize(12f);

                YAxis rightYAxis = barChart.getAxisRight();
                rightYAxis.setDrawLabels(false);

                barChart.getAxisLeft().setDrawGridLines(false);
                barChart.getAxisRight().setDrawGridLines(false);
                barChart.getXAxis().setDrawGridLines(false);

                barChart.getDescription().setEnabled(false);

                barData.notifyDataChanged();
                barChart.notifyDataSetChanged();
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void getData(Map<String, Integer> hourCounts) {
        // Ensure barArrayList is initialized
        if (barArrayList == null) {
            barArrayList = new ArrayList<>();
        } else {
            // Clear the existing entries
            barArrayList.clear();
        }

        // Populate barArrayList with actual data from Firebase
        for (Map.Entry<String, Integer> entry : hourCounts.entrySet()) {
            float hour = Float.parseFloat(entry.getKey());
            float count = entry.getValue();
            barArrayList.add(new BarEntry(hour, count));
        }
    }
//
//    private void updateGraphStat(Map<String, Integer> hourCounts) {
//        // Display the counts in the TextView
//        StringBuilder result = new StringBuilder();
//        for (Map.Entry<String, Integer> entry : hourCounts.entrySet()) {
//            result.append(entry.getKey()).append(": ").append(entry.getValue()).append(" logs\n");
//        }
//
//        // Make sure statTxt is not null before trying to set its text
//        if (statTxt != null) {
//            statTxt.setText(result.toString());
//        } else {
//            Log.e("graph_activity", "statTxt is null");
//        }
//    }
}