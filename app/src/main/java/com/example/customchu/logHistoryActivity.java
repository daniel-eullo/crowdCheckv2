package com.example.customchu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class logHistoryActivity extends AppCompatActivity {

    ImageButton adminLogBack;
    RecyclerView recyclerView2;
    DatabaseReference database;

    LogAdapter logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_history);

        adminLogBack = findViewById(R.id.adminLogBack);
        adminLogBack.setOnClickListener(view -> {
            Intent intent = new Intent(logHistoryActivity.this, adminActivity.class);
            startActivity(intent);
        });

        recyclerView2 = findViewById(R.id.log_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView2.setLayoutManager(layoutManager);

        Query query = FirebaseDatabase.getInstance().getReference("/attendance");
        FirebaseRecyclerOptions<LogModel> options =
                new FirebaseRecyclerOptions.Builder<LogModel>()
                        .setQuery(query, LogModel.class)
                        .build();

        logAdapter = new LogAdapter(options);
        recyclerView2.setAdapter(logAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        logAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        logAdapter.stopListening();
    }
}
