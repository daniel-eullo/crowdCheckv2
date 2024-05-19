package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

public class adminActivity extends AppCompatActivity {

    ImageButton adminBack;
    ImageView setNotif, checkFeedback2, checkLog, occupancy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        adminBack = findViewById(R.id.adminBack);
        adminBack.setOnClickListener(view -> {
            Intent intent = new Intent(adminActivity.this, home.class);
            startActivity(intent);
        });


        // NAVIGATIONS

        setNotif = findViewById(R.id.setNotif);
        setNotif.setOnClickListener(view -> {
            Intent intent = new Intent(adminActivity.this, adminNotif.class);
            startActivity(intent);
        });

        checkFeedback2 = findViewById(R.id.checkFeedback2);
        checkFeedback2.setOnClickListener(view -> {
            Intent intent = new Intent(adminActivity.this, checkFeedbackActivity.class);
            startActivity(intent);
        });

        checkLog= findViewById(R.id.checkLog);
        checkLog.setOnClickListener(view -> {
            Intent intent = new Intent(adminActivity.this, logHistoryActivity.class);
            startActivity(intent);
        });

        occupancy = findViewById(R.id.occupancy);
        occupancy.setOnClickListener(view -> {
            Intent intent = new Intent(adminActivity.this, setOccupancy.class);
            startActivity(intent);
        });
    }
}