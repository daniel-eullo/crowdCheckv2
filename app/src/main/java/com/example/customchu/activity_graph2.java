package com.example.customchu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.graphics.Color;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
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
    HashMap<String, Integer> hourCounts; // HashMap to store counts of each hour

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph2);

        graphBack2 = findViewById(R.id.graphBack2);
        graphBack2.setOnClickListener(view -> {
            Intent intent = new Intent(activity_graph2.this, home.class);
            startActivity(intent);
        });

//        to1FLogs = findViewById(R.id.to1FLogs);
//        to1FLogs.setOnClickListener(view -> {
//            Intent intent = new Intent(activity_graph2.this, graph_activity.class);
//            startActivity(intent);
//        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference2 = database.getReference("attendance");

        // Initialize HashMap
        hourCounts = new HashMap<>();

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot numerSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot timeSnapshot = numerSnapshot.child("time");
                        if (timeSnapshot.exists()) {
                            String time = timeSnapshot.getValue(String.class);
                            String hour = time.split(":")[0]; // Extracting only the hour part
                            Log.d("Hour", hour);

                            // Increment count for this hour in the HashMap
                            if (hourCounts.containsKey(hour)) {
                                hourCounts.put(hour, hourCounts.get(hour) + 1);
                            } else {
                                hourCounts.put(hour, 1);
                            }
                        } else {
                            Log.d("Firebase", "No 'time' child found for numerical snapshot: " + numerSnapshot.getKey());
                        }
                    }
                    // After iterating through all data, update the graph
                    updateGraph();
                } else {
                    Log.d("Firebase", "DataSnapshot does not have children");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data", databaseError.toException());
            }
        });

        barArrayList = new ArrayList<>();
        barChart = findViewById(R.id.barChart);
    }

    private void updateGraph() {
        // Populate barArrayList with actual data from HashMap
        for (Map.Entry<String, Integer> entry : hourCounts.entrySet()) {
            float hour = Float.parseFloat(entry.getKey());
            int count = entry.getValue(); // Convert count to integer
            barArrayList.add(new BarEntry(hour, count));
        }

        BarDataSet barDataSet = new BarDataSet(barArrayList, "Log Activity");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(Color.parseColor("#4E7AC7"));
        barDataSet.setValueTextSize(0f);
        barData.setValueTextColor(Color.parseColor("#4E7AC7"));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(16f);
        xAxis.setTextColor(Color.parseColor("#4E7AC7"));

        // Set a custom formatter to display whole numbers for x-axis labels
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Convert float value to integer
                int intValue = (int) value;
                // Return integer value as string
                return String.valueOf(intValue);
            }
        });

        YAxis leftYAxis = barChart.getAxisLeft();
        leftYAxis.setTextSize(16f);
        leftYAxis.setTextColor(Color.parseColor("#4E7AC7"));

        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setDrawLabels(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        // Changing the legend entry color
        Legend legend = barChart.getLegend();
        legend.setTextColor(Color.parseColor("#4E7AC7"));

        barChart.getDescription().setEnabled(false);

        barData.notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

}
