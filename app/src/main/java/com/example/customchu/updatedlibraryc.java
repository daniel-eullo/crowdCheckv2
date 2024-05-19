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

public class updatedlibraryc extends AppCompatActivity {
    ImageView crowdLogo, info;
    DatabaseReference databaseFacility;
    int libRoom3, room3Low, room3Medium, room3High;
    TextView floor3count;
    Button to1stFloor, to2ndFloor, infoClose;
    Dialog dialogInfo;
    boolean isLowLoaded = false;
    boolean isMediumLoaded = false;
    boolean isHighLoaded = false;
    boolean isCurrentLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatedlibraryc);

        // Navigation
        ImageButton mapBack = findViewById(R.id.toHome2);
        mapBack.setOnClickListener(view -> {
            Intent intent = new Intent(updatedlibraryc.this, home.class);
            startActivity(intent);
            finish();
        });

        to1stFloor = findViewById(R.id.to1stFloor3);
        to1stFloor.setOnClickListener(view -> {
            Intent intent = new Intent(updatedlibraryc.this, updatedlibrary.class);
            startActivity(intent);
        });

        to2ndFloor = findViewById(R.id.to2ndFloor4);
        to2ndFloor.setOnClickListener(view -> {
            Intent intent = new Intent(updatedlibraryc.this, updatedlibraryb.class);
            startActivity(intent);
        });

        info = findViewById(R.id.info4);
        info.setOnClickListener(view -> dialogInfo.show());

        dialogInfo = new Dialog(updatedlibraryc.this);
        dialogInfo.setContentView(R.layout.dialog_info);
        dialogInfo.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogInfo.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        dialogInfo.setCancelable(false);

        infoClose = dialogInfo.findViewById(R.id.infoClose);
        infoClose.setOnClickListener(view -> dialogInfo.dismiss());

        ZoomageView MapViewer = findViewById(R.id.img_view3);

        // Declare the radio buttons
        RadioButton radioNone = findViewById(R.id.radiobuttonnone);
        RadioButton radioBooks = findViewById(R.id.radiobuttonbooks);
        RadioButton radioScanner = findViewById(R.id.radiobuttonscanner);
        RadioButton radioOpac = findViewById(R.id.radiobuttonopac);

        // Events for clicks
        radioNone.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf3base));
        radioBooks.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf3books));
        radioScanner.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf3scanner));
        radioOpac.setOnClickListener(view -> MapViewer.setImageResource(R.drawable.nf3opac));

        radioBooks.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                radioBooks.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Perform click after the layout is complete
                radioBooks.performClick();
            }
        });

        crowdLogo = findViewById(R.id.crowdLogo4);
        floor3count = findViewById(R.id.floor2count3);

        databaseFacility = FirebaseDatabase.getInstance().getReference();

        // Create references for each node
        DatabaseReference room3Current = databaseFacility.child("Library").child("Current");
        DatabaseReference room3LowRef = databaseFacility.child("Library").child("Low");
        DatabaseReference room3MediumRef = databaseFacility.child("Library").child("Medium");
        DatabaseReference room3HighRef = databaseFacility.child("Library").child("High");

        // Listener for "Current" node
        room3Current.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                libRoom3 = Integer.parseInt(dataSnapshot.getValue().toString());
                isCurrentLoaded = true;
                checkAndUpdateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadCurrent:onCancelled", databaseError.toException());
            }
        });

        // Listener for "Low" node
        room3LowRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room3Low = Integer.parseInt(dataSnapshot.getValue().toString());
                isLowLoaded = true;
                checkAndUpdateUI();
                Log.d(TAG, "Low: " + room3Low);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadLow:onCancelled", databaseError.toException());
            }
        });

        // Listener for "Medium" node
        room3MediumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room3Medium = Integer.parseInt(dataSnapshot.getValue().toString());
                isMediumLoaded = true;
                checkAndUpdateUI();
                Log.d(TAG, "Medium: " + room3Medium);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadMedium:onCancelled", databaseError.toException());
            }
        });

        // Listener for "High" node
        room3HighRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room3High = Integer.parseInt(dataSnapshot.getValue().toString());
                isHighLoaded = true;
                checkAndUpdateUI();
                Log.d(TAG, "High: " + room3High);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadHigh:onCancelled", databaseError.toException());
            }
        });
    }

    private void checkAndUpdateUI() {
        if (isCurrentLoaded && isLowLoaded && isMediumLoaded && isHighLoaded) {
            updateUI(libRoom3, room3Low, room3Medium, room3High);
        }
    }

    private void updateUI(int libRoom3, int room3Low, int room3Medium, int room3High) {
        floor3count.setText("Current Occupied: " + libRoom3);

        if (libRoom3 < room3Low) {
            floor3count.setTextColor(getResources().getColor(R.color.green));
            ImageViewCompat.setImageTintMode(crowdLogo, PorterDuff.Mode.SRC_ATOP);
            ImageViewCompat.setImageTintList(crowdLogo, ColorStateList.valueOf(Color.parseColor("#388E3C")));
        } else if (libRoom3 < room3Medium) {
            floor3count.setTextColor(getResources().getColor(R.color.yellow));
            ImageViewCompat.setImageTintMode(crowdLogo, PorterDuff.Mode.SRC_ATOP);
            ImageViewCompat.setImageTintList(crowdLogo, ColorStateList.valueOf(Color.parseColor("#DAC21F")));
        } else if (libRoom3 < room3High) {
            floor3count.setTextColor(getResources().getColor(R.color.red));
            ImageViewCompat.setImageTintMode(crowdLogo, PorterDuff.Mode.SRC_ATOP);
            ImageViewCompat.setImageTintList(crowdLogo, ColorStateList.valueOf(Color.parseColor("#D32F2F")));
        }
    }
}
