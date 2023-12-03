package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class logHistoryActivity extends AppCompatActivity {

    ImageButton adminLogBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_history);

        adminLogBack = findViewById(R.id.adminLogBack);
        adminLogBack.setOnClickListener(view -> {
            Intent intent = new Intent(logHistoryActivity.this, adminActivity.class);
            startActivity(intent);
        });
    }
}