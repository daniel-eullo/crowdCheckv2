package com.example.customchu;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class library2Activity extends AppCompatActivity {

    ImageView lowCrowd;
    ImageView midCrowd;
    ImageView hiCrowd;
    ImageButton mapBack;
    Button btn1stFloor;
    int libRoom1, libRoom2;
    //TextView room1Count;
    TextView room2Count;
    DatabaseReference databaseFacility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library2);

        mapBack = findViewById(R.id.mapBack);
        mapBack.setOnClickListener(view -> {
            Intent intent = new Intent(library2Activity.this, home.class);
            startActivity(intent);
        });

        btn1stFloor = findViewById(R.id.btn1stFloor);
        btn1stFloor.setOnClickListener(view -> {
            Intent intent = new Intent(library2Activity.this, mapActivity.class);
            startActivity(intent);
        });

        //room1Count = findViewById(R.id.room1Count);
        room2Count = findViewById(R.id.room2Count);

        lowCrowd = findViewById(R.id.imageView17);
        midCrowd = findViewById(R.id.imageView16);
        hiCrowd = findViewById(R.id.imageView9);

        databaseFacility = FirebaseDatabase.getInstance().getReference();
        //DatabaseReference room1 = databaseFacility.child("Rooms").child("GF").child("Current");
        DatabaseReference room2 = databaseFacility.child("Rooms").child("2F").child("Current");

//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                room1Count.setText(dataSnapshot.getValue() + "");
//                libRoom1 = Integer.parseInt(dataSnapshot.getValue() + "");
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//            }
//
//        };
//        room1.addValueEventListener(postListener);

        ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room2Count.setText(dataSnapshot.getValue() + "");
                libRoom2 = Integer.parseInt(dataSnapshot.getValue() + "");
                if (libRoom2 < 26)
                {
                    lowCrowd.setVisibility(View.VISIBLE);
                    midCrowd.setVisibility(View.INVISIBLE);
                    hiCrowd.setVisibility(View.INVISIBLE);

                }
                else if (libRoom2 < 51)
                {
                    lowCrowd.setVisibility(View.INVISIBLE);
                    midCrowd.setVisibility(View.VISIBLE);
                    hiCrowd.setVisibility(View.INVISIBLE);
                }
                else
                {
                    lowCrowd.setVisibility(View.INVISIBLE);
                    midCrowd.setVisibility(View.INVISIBLE);
                    hiCrowd.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };
        room2.addValueEventListener(postListener2);
    }
}