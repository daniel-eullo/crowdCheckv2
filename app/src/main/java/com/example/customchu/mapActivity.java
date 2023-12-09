package com.example.customchu;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class mapActivity extends AppCompatActivity {

    ImageView lowCrowd;
    ImageView midCrowd;
    ImageView hiCrowd;
    ImageButton mapBack;
    Button btn2ndFloor;
    DatabaseReference databaseFacility;
    int libRoom1;
    TextView room1Count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        mapBack = findViewById(R.id.mapBack);
        mapBack.setOnClickListener(view -> {
            Intent intent = new Intent(mapActivity.this, home.class);
            startActivity(intent);
        });
        btn2ndFloor = findViewById(R.id.btn2ndFloor);
        btn2ndFloor.setOnClickListener(view -> {
            Intent intent = new Intent(mapActivity.this, library2Activity.class);
            startActivity(intent);
        });

        room1Count = findViewById(R.id.room1Count);lowCrowd = findViewById(R.id.imageView9);
        midCrowd = findViewById(R.id.imageView12);
        hiCrowd = findViewById(R.id.imageView15);

        databaseFacility = FirebaseDatabase.getInstance().getReference();
        DatabaseReference room1 = databaseFacility.child("Rooms").child("GF").child("Current");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room1Count.setText(dataSnapshot.getValue() + "");
                libRoom1 = Integer.parseInt(dataSnapshot.getValue() + "");

                if (libRoom1 < 21) {
                    lowCrowd.setVisibility(View.VISIBLE);
                    midCrowd.setVisibility(View.INVISIBLE);
                    hiCrowd.setVisibility(View.INVISIBLE);
                } else if (libRoom1 < 36) {
                    lowCrowd.setVisibility(View.INVISIBLE);
                    midCrowd.setVisibility(View.VISIBLE);
                    hiCrowd.setVisibility(View.INVISIBLE);
                } else {
                    lowCrowd.setVisibility(View.INVISIBLE);
                    midCrowd.setVisibility(View.INVISIBLE);
                    hiCrowd.setVisibility(View.VISIBLE);
                }

                // Update notification preferences based on the current count
                updateNotificationPreferences(libRoom1);
            }

            private void updateNotificationPreferences(int currentCount) {
                // Get the SharedPreferences editor
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();

                // Save the notification preferences based on the current count
                editor.putBoolean("lowDensity", currentCount <= 10);
                editor.putBoolean("mediumDensity", currentCount > 10 && currentCount <= 30);
                editor.putBoolean("highDensity", currentCount > 30 && currentCount <= 50);

                // Apply changes
                editor.apply();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        // Attach the ValueEventListener to the DatabaseReference
        room1.addValueEventListener(postListener);
    }
}
