package com.example.customchu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class checkFeedbackActivity extends AppCompatActivity {

    ImageButton adminFeedbackBack;
    RecyclerView recyclerView;
    DatabaseReference database;

    MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_feedback);

        adminFeedbackBack = findViewById(R.id.adminFeedbackBack);
        adminFeedbackBack.setOnClickListener(view -> {
            Intent intent = new Intent(checkFeedbackActivity.this, adminActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Feedback").child("feedbackTicket"), MainModel.class)
                        .build();

        mainAdapter = new MainAdapter(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull MainModel model) {
                super.onBindViewHolder(holder, position, model);
                holder.ticketNumber.setText(getRef(position).getKey());
            }
        };

        recyclerView.setAdapter(mainAdapter);

        Log.d("CheckFeedbackActivity", "onCreate: Initialized successfully");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();
        Log.d("CheckFeedbackActivity", "onStart: Adapter started listening");
    }


    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();

        Log.d("CheckFeedbackActivity", "onStop: Adapter stopped listening");
    }
}