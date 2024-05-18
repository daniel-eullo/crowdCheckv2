package com.example.customchu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
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
import java.util.Map;

public class activity_graph2 extends AppCompatActivity {
    ImageButton graphBack2;
    Button to1FLogs;
    DatabaseReference databaseReference2;
    ArrayList<BarEntry> barArrayList;
    BarChart barChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph2);

        graphBack2 = findViewById(R.id.graphBack2);
        graphBack2.setOnClickListener(view -> {
            Intent intent = new Intent(activity_graph2.this, home.class);
            startActivity(intent);
        });

        to1FLogs = findViewById(R.id.to1FLogs);
        to1FLogs.setOnClickListener(view -> {
            Intent intent = new Intent(activity_graph2.this, graph_activity.class);
            startActivity(intent);
        });
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference2 = database.getReference("attendance");

        barArrayList = new ArrayList<>();
        barChart = findViewById(R.id.barChart);

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Iterate over the dynamic numerical children nodes
                    for (DataSnapshot userSnapshot : childSnapshot.getChildren()) {
                        // Access the "time" value for each child
                        String time = userSnapshot.child("time").getValue(String.class);
                        // Log the time value
                        Log.d("TimeValue", "Time: " + time);
                    }
                }
                // Your existing code to update the chart can go here
                updateChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void updateChart() {
        // Populate barArrayList with actual data from Firebase
        BarDataSet barDataSet = new BarDataSet(barArrayList, "Log Activity");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(Color.parseColor("#4E7AC7"));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(0f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.parseColor("#4E7AC7"));

        YAxis leftYAxis = barChart.getAxisLeft();
        leftYAxis.setTextSize(12f);
        leftYAxis.setTextColor(Color.parseColor("#4E7AC7"));

        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setDrawLabels(false);

        Legend legend = barChart.getLegend();
        legend.setTextColor(Color.parseColor("#4E7AC7"));

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        barChart.getDescription().setEnabled(false);

        barData.notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.invalidate();
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