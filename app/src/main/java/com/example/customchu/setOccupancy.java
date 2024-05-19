package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class setOccupancy extends AppCompatActivity {
    ImageButton adminNotifBack;  // Correct type ImageButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_occupancy);

        // Initialize adminNotifBack using findViewById
        adminNotifBack = findViewById(R.id.adminNotifBack);

        // Set OnClickListener
        adminNotifBack.setOnClickListener(view -> {
            Intent intent = new Intent(setOccupancy.this, adminActivity.class);
            startActivity(intent);
        });
    }
}
