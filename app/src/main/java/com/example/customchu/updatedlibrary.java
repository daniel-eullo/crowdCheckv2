package com.example.customchu;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jsibbold.zoomage.ZoomageView;

public class updatedlibrary extends AppCompatActivity {

    ImageView crowdLogo, info;
    DatabaseReference databaseFacility;
    int libRoom1;
    TextView floor1count;
    Button to2ndFloor, infoClose;
    Dialog dialogInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatedlibrary);

        //Navigation
        ImageButton mapBack = findViewById(R.id.toHome1);
        mapBack.setOnClickListener(view -> {
//            Intent intent = new Intent(updatedlibrary.this, home.class);
//            startActivity(intent);
            finish();
        });

        to2ndFloor = findViewById(R.id.to2ndFloor);
        to2ndFloor.setOnClickListener(view -> {
            Intent intent = new Intent(updatedlibrary.this, updatedlibraryb.class);
            startActivity(intent);
        });

        info = findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInfo.show();
            }
        });



        dialogInfo= new Dialog(updatedlibrary.this);
        dialogInfo.setContentView(R.layout.dialog_info);
        dialogInfo.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogInfo.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        dialogInfo.setCancelable(false);

        infoClose = dialogInfo.findViewById(R.id.infoClose);
        infoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInfo.dismiss();
            }
        });

        ZoomageView MapViewer = findViewById(R.id.img_view);

        // Declare the radiobs
        RadioButton radioNone = findViewById(R.id.radiobuttonnone);
        RadioButton radioSeats = findViewById(R.id.radiobuttonseats);
        RadioButton radioScanner = findViewById(R.id.radiobuttonscanner);
        RadioButton radioOpac = findViewById(R.id.radiobuttonopac);

        radioSeats.setChecked(true);
        // Events for clicks
        radioNone.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.svgf1base));
        radioSeats.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.svgf1seats));
        radioScanner.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.svgf1scanner));
        radioOpac.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.svgf1opac));

        radioSeats.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                radioSeats.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Perform click after the layout is complete
                radioSeats.performClick();
            }
        });


        crowdLogo = findViewById(R.id.crowdLogo);
        floor1count = findViewById(R.id.floor1count);

        databaseFacility = FirebaseDatabase.getInstance().getReference();
        DatabaseReference room1 = databaseFacility.child("Rooms").child("GF").child("Current");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                floor1count.setText("Current Occupied: " + dataSnapshot.getValue() + "");
                libRoom1 = Integer.parseInt(dataSnapshot.getValue() + "");
                if (libRoom1 < 21) {
                    //crowdLogo.setColorFilter(Color.argb(1, 56, 142, 60));
                    floor1count.setTextColor(getResources().getColor(R.color.green));
                    ImageViewCompat.setImageTintMode(crowdLogo, PorterDuff.Mode.SRC_ATOP);
                    ImageViewCompat.setImageTintList(crowdLogo, ColorStateList.valueOf(Color.parseColor("#388E3C")));
                } else if (libRoom1 < 36) {
                   // crowdLogo.setColorFilter(Color.argb(1, 218, 194, 31));
                    floor1count.setTextColor(getResources().getColor(R.color.yellow));
                    ImageViewCompat.setImageTintMode(crowdLogo, PorterDuff.Mode.SRC_ATOP);
                    ImageViewCompat.setImageTintList(crowdLogo, ColorStateList.valueOf(Color.parseColor("#DAC21F")));

                } else {
                    //crowdLogo.setColorFilter(Color.argb(1, 211, 47, 47));
                    floor1count.setTextColor(getResources().getColor(R.color.red));
                    ImageViewCompat.setImageTintMode(crowdLogo, PorterDuff.Mode.SRC_ATOP);
                    ImageViewCompat.setImageTintList(crowdLogo, ColorStateList.valueOf(Color.parseColor("#D32F2F")));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        room1.addValueEventListener(postListener);
    }
}