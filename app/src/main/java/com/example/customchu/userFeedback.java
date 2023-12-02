package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class userFeedback extends AppCompatActivity {

    ImageButton userFeedbackBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);

        userFeedbackBack = findViewById(R.id.userFeedbackBack);
        userFeedbackBack.setOnClickListener(view -> {
           Intent intent = new Intent(userFeedback.this, home.class);
           startActivity(intent);
           finish();
        });
    }
}