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

public class updatedlibraryb extends AppCompatActivity {
    ImageView crowdLogo2, info2;
    DatabaseReference databaseFacility;
    int libRoom2, room2Low, room2Medium, room2High;
    TextView floor2count;
    Button to1stFloor, to3rdFloor ,infoClose;
    Dialog dialogInfo2;
    boolean isLowLoaded = false;
    boolean isMediumLoaded = false;
    boolean isHighLoaded = false;
    boolean isCurrentLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatedlibraryb);

        // Navigation
        ImageButton mapBack = findViewById(R.id.toHome1);
        mapBack.setOnClickListener(view -> {
            Intent intent = new Intent(updatedlibraryb.this, home.class);
            startActivity(intent);
            finish();
        });

        to1stFloor = findViewById(R.id.to1stFloor);
        to1stFloor.setOnClickListener(view -> {
            Intent intent = new Intent(updatedlibraryb.this, updatedlibrary.class);
            startActivity(intent);
        });

        to3rdFloor = findViewById(R.id.to3rdFloor2);
        to3rdFloor.setOnClickListener(view -> {
            Intent intent = new Intent(updatedlibraryb.this, updatedlibraryc.class);
            startActivity(intent);
        });

        info2 = findViewById(R.id.info2);
        info2.setOnClickListener(view -> dialogInfo2.show());

        dialogInfo2 = new Dialog(updatedlibraryb.this);
        dialogInfo2.setContentView(R.layout.dialog_info);
        dialogInfo2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogInfo2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        dialogInfo2.setCancelable(false);

        infoClose = dialogInfo2.findViewById(R.id.infoClose);
        infoClose.setOnClickListener(view -> dialogInfo2.dismiss());

        ZoomageView MapViewer = findViewById(R.id.img_view);

        // Declare the radio buttons
        RadioButton radioNone = findViewById(R.id.radiobuttonnone);
        RadioButton radioBooks = findViewById(R.id.radiobuttonbooks);
        RadioButton radioScanner = findViewById(R.id.radiobuttonscanner);
        RadioButton radioOpac = findViewById(R.id.radiobuttonopac);

        // Events for clicks
        radioNone.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf2base));
        radioBooks.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf2books));
        radioScanner.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf2scanner));
        radioOpac.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf2opac));

        radioBooks.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                radioBooks.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Perform click after the layout is complete
                radioBooks.performClick();
            }
        });

        crowdLogo2 = findViewById(R.id.crowdLogo2);
        floor2count = findViewById(R.id.floor2count);

        databaseFacility = FirebaseDatabase.getInstance().getReference();

        // Create references for each node
        DatabaseReference room2Current = databaseFacility.child("Library").child("Current");
        DatabaseReference room2LowRef = databaseFacility.child("Library").child("Low");
        DatabaseReference room2MediumRef = databaseFacility.child("Library").child("Medium");
        DatabaseReference room2HighRef = databaseFacility.child("Library").child("High");

        // Listener for "Current" node
        room2Current.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                libRoom2 = Integer.parseInt(dataSnapshot.getValue().toString());
                isCurrentLoaded = true;
                checkAndUpdateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadCurrent:onCancelled", databaseError.toException());
            }
        });

        // Listener for "Low" node
        room2LowRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room2Low = Integer.parseInt(dataSnapshot.getValue().toString());
                isLowLoaded = true;
                checkAndUpdateUI();
                Log.d(TAG, "Low: " + room2Low);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadLow:onCancelled", databaseError.toException());
            }
        });

        // Listener for "Medium" node
        room2MediumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room2Medium = Integer.parseInt(dataSnapshot.getValue().toString());
                isMediumLoaded = true;
                checkAndUpdateUI();
                Log.d(TAG, "Medium: " + room2Medium);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadMedium:onCancelled", databaseError.toException());
            }
        });

        // Listener for "High" node
        room2HighRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room2High = Integer.parseInt(dataSnapshot.getValue().toString());
                isHighLoaded = true;
                checkAndUpdateUI();
                Log.d(TAG, "High: " + room2High);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadHigh:onCancelled", databaseError.toException());
            }
        });
    }

    private void checkAndUpdateUI() {
        if (isCurrentLoaded && isLowLoaded && isMediumLoaded && isHighLoaded) {
            updateUI(libRoom2, room2Low, room2Medium, room2High);
        }
    }

    private void updateUI(int libRoom2, int room2Low, int room2Medium, int room2High) {
        floor2count.setText("Current Occupied: " + libRoom2);

        if (libRoom2 < room2Low) {
            floor2count.setTextColor(getResources().getColor(R.color.green));
            ImageViewCompat.setImageTintMode(crowdLogo2, PorterDuff.Mode.SRC_ATOP);
            ImageViewCompat.setImageTintList(crowdLogo2, ColorStateList.valueOf(Color.parseColor("#388E3C")));
        } else if (libRoom2 < room2Medium) {
            floor2count.setTextColor(getResources().getColor(R.color.yellow));
            ImageViewCompat.setImageTintMode(crowdLogo2, PorterDuff.Mode.SRC_ATOP);
            ImageViewCompat.setImageTintList(crowdLogo2, ColorStateList.valueOf(Color.parseColor("#DAC21F")));
        } else if (libRoom2 < room2High) {
            floor2count.setTextColor(getResources().getColor(R.color.red));
            ImageViewCompat.setImageTintMode(crowdLogo2, PorterDuff.Mode.SRC_ATOP);
            ImageViewCompat.setImageTintList(crowdLogo2, ColorStateList.valueOf(Color.parseColor("#D32F2F")));
        }
    }
}
