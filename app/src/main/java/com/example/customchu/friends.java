package com.example.customchu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class friends extends AppCompatActivity {

    private ImageButton friendsBack;
    private Button ManageFriendsButton, friendRequestBtn;
    TextView friendTxt;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    DatabaseReference DB;
    Integer uidCur = 0;
    RecyclerView recyclerView;
    friendAdapter friendAdapter;
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

        //friendTxt = findViewById(R.id.friendTxt);

        getCurrentUser();

        recyclerView = (RecyclerView)findViewById(R.id.rvFriends);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<friendModel> options =
                new FirebaseRecyclerOptions.Builder<friendModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference(), friendModel.class)
                        .build();

        friendAdapter = new friendAdapter(options);
        recyclerView.setAdapter(friendAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        friendAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        friendAdapter.stopListening();
    }

    private void getCurrentUser() {
        DB = FirebaseDatabase.getInstance().getReference();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            DatabaseReference ProfileReference = DB.child("Profiles").child(account.getId());

            ProfileReference.child("uid").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    uidCur = task.getResult().getValue(Integer.class);
                    if (uidCur != null) {
                        //friendTxt.setText("Current User: " + String.valueOf(uidCur));

                        // Now that you have the UID, update the FirebaseRecyclerOptions query
                        updateRecyclerView(uidCur);
                    }
                } else {
                    Log.e("TAG", "Error getting UID", task.getException());
                }
            });
        }
    }

    private void updateRecyclerView(Integer uid) {
        FirebaseRecyclerOptions<friendModel> options =
                new FirebaseRecyclerOptions.Builder<friendModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(uid.toString()).child("friends"), friendModel.class)
                        .build();

        friendAdapter.updateOptions(options);
    }
}