package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class notifActivity extends AppCompatActivity {
    ImageButton notifBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);

        notifBack = findViewById(R.id.notifBack);
        notifBack.setOnClickListener(view -> {
            Intent intent = new Intent(notifActivity.this, home.class);
            startActivity(intent);
        });
    }
}