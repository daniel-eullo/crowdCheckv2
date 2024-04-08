package com.example.customchu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BookSystem extends AppCompatActivity {

    private ImageButton BookSystemBack;
    private Button ReturnBookBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_system);

        BookSystemBack = findViewById(R.id.BookSystemBack);
        BookSystemBack.setOnClickListener(view -> {
            Intent intent = new Intent(BookSystem.this, home.class);
            startActivity(intent);
        });

        ReturnBookBtn = findViewById(R.id.ReturnBookBtn);
        ReturnBookBtn.setOnClickListener(view -> {
            Intent intent = new Intent(BookSystem.this, BookReturn.class);
            startActivity(intent);
        });

    }
}