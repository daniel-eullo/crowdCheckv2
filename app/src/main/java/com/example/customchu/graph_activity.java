package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

public class graph_activity extends AppCompatActivity {
    ImageButton graphBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        graphBack = findViewById(R.id.graphBack);
        graphBack.setOnClickListener(view -> {
            Intent intent = new Intent(graph_activity.this, home.class);
            startActivity(intent);
        });

    }
}