package com.example.customchu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BookReturn extends AppCompatActivity {
    private ImageButton ReturnBookBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_return);

        ReturnBookBack = findViewById(R.id.ReturnBookBack);
        ReturnBookBack.setOnClickListener(view -> {
            Intent intent = new Intent(BookReturn.this, BookSystem.class);
            startActivity(intent);
        });

    }
}