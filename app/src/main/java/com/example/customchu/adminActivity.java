package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class adminActivity extends AppCompatActivity {

    ImageButton adminBack;

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