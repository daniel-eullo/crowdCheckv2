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
    int libRoom1, room1Low, room1Medium, room1High;
    TextView floor1count;
    Button to2ndFloor, to3rdFloor, infoClose;
    Dialog dialogInfo;
    boolean isLowLoaded = false;
    boolean isMediumLoaded = false;
    boolean isHighLoaded = false;
    boolean isCurrentLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatedlibrary);

        // Navigation
        ImageButton mapBack = findViewById(R.id.toHome1);
        mapBack.setOnClickListener(view -> {
            Intent intent = new Intent(updatedlibrary.this, home.class);
            startActivity(intent);
            finish();
        });

        to2ndFloor = findViewById(R.id.to2ndFloor);
        to2ndFloor.setOnClickListener(view -> {
            Intent intent = new Intent(updatedlibrary.this, updatedlibraryb.class);
            startActivity(intent);
        });

        to3rdFloor = findViewById(R.id.to3rdFloor);
        to3rdFloor.setOnClickListener(view -> {
            Intent intent = new Intent(updatedlibrary.this, updatedlibraryc.class);
            startActivity(intent);
        });

        info = findViewById(R.id.info);
        info.setOnClickListener(view -> dialogInfo.show());

        dialogInfo = new Dialog(updatedlibrary.this);
        dialogInfo.setContentView(R.layout.dialog_info);
        dialogInfo.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogInfo.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        dialogInfo.setCancelable(false);

        infoClose = dialogInfo.findViewById(R.id.infoClose);
        infoClose.setOnClickListener(view -> dialogInfo.dismiss());

        ZoomageView MapViewer = findViewById(R.id.img_view);

        // Declare the radio buttons
        RadioButton radioNone = findViewById(R.id.radiobuttonnone);
        RadioButton radioBooks = findViewById(R.id.radiobuttonbooks);
        RadioButton radioScanner = findViewById(R.id.radiobuttonscanner);
        RadioButton radioOpac = findViewById(R.id.radiobuttonopac);

        // Events for clicks
        radioNone.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf1base));
        radioBooks.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf1books));
        radioScanner.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf1scanner));
        radioOpac.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf1opac));

        radioBooks.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                radioBooks.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Perform click after the layout is complete
                radioBooks.performClick();
            }
        });

        crowdLogo = findViewById(R.id.crowdLogo);
        floor1count = findViewById(R.id.floor1count);

        databaseFacility = FirebaseDatabase.getInstance().getReference();

        // Create references for each node
        DatabaseReference room1Current = databaseFacility.child("Library").child("Current");
        DatabaseReference room1LowRef = databaseFacility.child("Library").child("Low");
        DatabaseReference room1MediumRef = databaseFacility.child("Library").child("Medium");
        DatabaseReference room1HighRef = databaseFacility.child("Library").child("High");

        // Listener for "Current" node
        room1Current.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                libRoom1 = Integer.parseInt(dataSnapshot.getValue().toString());
                isCurrentLoaded = true;
                checkAndUpdateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadCurrent:onCancelled", databaseError.toException());
            }
        });

        // Listener for "Low" node
        room1LowRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room1Low = Integer.parseInt(dataSnapshot.getValue().toString());
                isLowLoaded = true;
                checkAndUpdateUI();
                Log.d(TAG, "Low: " + room1Low);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadLow:onCancelled", databaseError.toException());
            }
        });

        // Listener for "Medium" node
        room1MediumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room1Medium = Integer.parseInt(dataSnapshot.getValue().toString());
                isMediumLoaded = true;
                checkAndUpdateUI();
                Log.d(TAG, "Medium: " + room1Medium);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadMedium:onCancelled", databaseError.toException());
            }
        });

        // Listener for "High" node
        room1HighRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room1High = Integer.parseInt(dataSnapshot.getValue().toString());
                isHighLoaded = true;
                checkAndUpdateUI();
                Log.d(TAG, "High: " + room1High);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadHigh:onCancelled", databaseError.toException());
            }
        });
    }

    private void checkAndUpdateUI() {
        if (isCurrentLoaded && isLowLoaded && isMediumLoaded && isHighLoaded) {
            updateUI(libRoom1, room1Low, room1Medium, room1High);
        }
    }

    private void updateUI(int libRoom1, int room1Low, int room1Medium, int room1High) {
        floor1count.setText("Current Occupied: " + libRoom1);

        if (libRoom1 < room1Low) {
            floor1count.setTextColor(getResources().getColor(R.color.green));
            ImageViewCompat.setImageTintMode(crowdLogo, PorterDuff.Mode.SRC_ATOP);
            ImageViewCompat.setImageTintList(crowdLogo, ColorStateList.valueOf(Color.parseColor("#388E3C")));
        } else if (libRoom1 < room1Medium) {
            floor1count.setTextColor(getResources().getColor(R.color.yellow));
            ImageViewCompat.setImageTintMode(crowdLogo, PorterDuff.Mode.SRC_ATOP);
            ImageViewCompat.setImageTintList(crowdLogo, ColorStateList.valueOf(Color.parseColor("#DAC21F")));
        } else if (libRoom1 < room1High) {
            floor1count.setTextColor(getResources().getColor(R.color.red));
            ImageViewCompat.setImageTintMode(crowdLogo, PorterDuff.Mode.SRC_ATOP);
            ImageViewCompat.setImageTintList(crowdLogo, ColorStateList.valueOf(Color.parseColor("#D32F2F")));
        }
    }
}
