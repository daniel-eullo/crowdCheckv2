package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

public class adminActivity extends AppCompatActivity {

    ImageButton adminBack;
    ImageView setNotif, checkFeedback, checkLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        adminBack = findViewById(R.id.adminBack);
        adminBack.setOnClickListener(view -> {
            Intent intent = new Intent(adminActivity.this, home.class);
            startActivity(intent);
        });
    }
}