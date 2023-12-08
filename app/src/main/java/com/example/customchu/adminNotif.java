package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class adminNotif extends AppCompatActivity {

    ImageButton adminNotifBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notif);

        adminNotifBack = findViewById(R.id.adminNotifBack);
        adminNotifBack.setOnClickListener(view -> {
            Intent intent = new Intent(adminNotif.this, adminActivity.class);
            startActivity(intent);
        });
    }


}
