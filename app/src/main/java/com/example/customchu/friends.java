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

public class friends extends AppCompatActivity {

    private ImageButton friendsBack;
    private Button ManageFriendsButton, friendRequestBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        friendsBack = findViewById(R.id.friendsBack);
        friendsBack.setOnClickListener(view -> {
            Intent intent = new Intent(friends.this, home.class);
            startActivity(intent);
        });


        ManageFriendsButton = findViewById(R.id.ManageFriendsBtn);
        ManageFriendsButton.setOnClickListener(view -> {
            Intent intent = new Intent(friends.this, managefriends.class);
            startActivity(intent);
        });

        friendRequestBtn = findViewById(R.id.friendRequestBtn);
        friendRequestBtn.setOnClickListener(view -> {
            Intent intent = new Intent(friends.this, recFriendRequest.class);
            startActivity(intent);
        });

    }
}