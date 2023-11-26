package com.example.customchu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class signup extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        TextView btnToLogin = findViewById(R.id.btnToLogin);
        btnToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(signup.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
