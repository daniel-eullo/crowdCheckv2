package com.example.customchu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class managefriends extends AppCompatActivity {

    private ImageButton managefriendsBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managefriends);

        managefriendsBack = findViewById(R.id.managefriendsBack);
        managefriendsBack.setOnClickListener(view -> {
            Intent intent = new Intent(managefriends.this, friends.class);
            startActivity(intent);
        });

    }
}