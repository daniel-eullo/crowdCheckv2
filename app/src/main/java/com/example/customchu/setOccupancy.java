package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class setOccupancy extends AppCompatActivity {
    private static final String TAG = "setOccupancy";
    ImageButton adminNotifBack;
    Button occupancySubmit; // Define occupancySubmit button
    DatabaseReference databaseFacility;

    // Define EditText fields
    EditText curTxt, lowTxt, medTxt, highTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_occupancy);

        // Initialize EditText fields
        curTxt = findViewById(R.id.curTxt);
        lowTxt = findViewById(R.id.lowTxt);
        medTxt = findViewById(R.id.medTxt);
        highTxt = findViewById(R.id.highTxt);

        // Initialize adminNotifBack using findViewById
        adminNotifBack = findViewById(R.id.adminNotifBack);

        // Initialize occupancySubmit using findViewById
        occupancySubmit = findViewById(R.id.occupancySubmit);

        // Set OnClickListener for adminNotifBack
        adminNotifBack.setOnClickListener(view -> {
            Intent intent = new Intent(setOccupancy.this, adminActivity.class);
            startActivity(intent);
        });

        // Initialize Firebase database reference
        databaseFacility = FirebaseDatabase.getInstance().getReference().child("Library");

        // Add a listener to get the values of Current, Low, Medium, and High
        databaseFacility.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long current = dataSnapshot.child("Current").getValue(Long.class);
                    Long low = dataSnapshot.child("Low").getValue(Long.class);
                    Long medium = dataSnapshot.child("Medium").getValue(Long.class);
                    Long high = dataSnapshot.child("High").getValue(Long.class);

                    // Log the values
                    Log.d(TAG, "Current: " + current);
                    Log.d(TAG, "Low: " + low);
                    Log.d(TAG, "Medium: " + medium);
                    Log.d(TAG, "High: " + high);

                    // Set the values to EditText fields
                    curTxt.setText(current != null ? String.valueOf(current) : "0");
                    lowTxt.setText(low != null ? String.valueOf(low) : "0");
                    medTxt.setText(medium != null ? String.valueOf(medium) : "0");
                    highTxt.setText(high != null ? String.valueOf(high) : "0");
                } else {
                    Log.d(TAG, "No data found at Library node.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadLibrary:onCancelled", databaseError.toException());
            }
        });

        // Set OnClickListener for occupancySubmit
        occupancySubmit.setOnClickListener(view -> {
            // Read values from EditText fields
            String currentStr = curTxt.getText().toString().trim();
            String lowStr = lowTxt.getText().toString().trim();
            String mediumStr = medTxt.getText().toString().trim();
            String highStr = highTxt.getText().toString().trim();

            // Parse strings to Long
            Long current = !currentStr.isEmpty() ? Long.parseLong(currentStr) : 0;
            Long low = !lowStr.isEmpty() ? Long.parseLong(lowStr) : 0;
            Long medium = !mediumStr.isEmpty() ? Long.parseLong(mediumStr) : 0;
            Long high = !highStr.isEmpty() ? Long.parseLong(highStr) : 0;

            // Update values in Firebase
            databaseFacility.child("Current").setValue(current);
            databaseFacility.child("Low").setValue(low);
            databaseFacility.child("Medium").setValue(medium);
            databaseFacility.child("High").setValue(high);

            // Log the update
            Log.d(TAG, "Updated Current: " + current);
            Log.d(TAG, "Updated Low: " + low);
            Log.d(TAG, "Updated Medium: " + medium);
            Log.d(TAG, "Updated High: " + high);
        });
    }
}
