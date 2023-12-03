package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class checkFeedbackActivity extends AppCompatActivity {

    ImageButton adminFeedbackBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_feedback);

        adminFeedbackBack = findViewById(R.id.adminFeedbackBack);
        adminFeedbackBack.setOnClickListener(view -> {
            Intent intent = new Intent(checkFeedbackActivity.this, adminActivity.class);
            startActivity(intent);
        });
    }
}