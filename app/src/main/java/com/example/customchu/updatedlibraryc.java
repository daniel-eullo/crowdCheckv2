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
    ImageView crowdLogo2, info2;
    DatabaseReference databaseFacility;
    int libRoom2;
    TextView floor2count;
    Button to1stFloor, to2ndFloor, infoClose;
    Dialog dialogInfo2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatedlibraryc);

        //Navigation
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

        info2 = findViewById(R.id.info4);
        info2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInfo2.show();
            }
        });



        dialogInfo2= new Dialog(updatedlibraryc.this);
        dialogInfo2.setContentView(R.layout.dialog_info);
        dialogInfo2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogInfo2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        dialogInfo2.setCancelable(false);

        infoClose = dialogInfo2.findViewById(R.id.infoClose);
        infoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInfo2.dismiss();
            }
        });

        ZoomageView MapViewer = findViewById(R.id.img_view3);

        // Declare the radiobs
        RadioButton radioNone = findViewById(R.id.radiobuttonnone);
        RadioButton radioBooks = findViewById(R.id.radiobuttonbooks);
        RadioButton radioScanner = findViewById(R.id.radiobuttonscanner);
        RadioButton radioOpac = findViewById(R.id.radiobuttonopac);

        //radioBooks.setChecked(true);

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

        crowdLogo2 = findViewById(R.id.crowdLogo4);
        floor2count = findViewById(R.id.floor2count3);

        databaseFacility = FirebaseDatabase.getInstance().getReference();
        DatabaseReference room2 = databaseFacility.child("Library").child("Current");

        ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                floor2count.setText("Current Occupied: " + dataSnapshot.getValue() + "");
                libRoom2 = Integer.parseInt(dataSnapshot.getValue() + "");
                if (libRoom2 < 50)
                {
                    floor2count.setTextColor(getResources().getColor(R.color.green));
                    ImageViewCompat.setImageTintMode(crowdLogo2, PorterDuff.Mode.SRC_ATOP);
                    ImageViewCompat.setImageTintList(crowdLogo2, ColorStateList.valueOf(Color.parseColor("#388E3C")));

                }
                else if (libRoom2 < 100)
                {
                    floor2count.setTextColor(getResources().getColor(R.color.yellow));
                    ImageViewCompat.setImageTintMode(crowdLogo2, PorterDuff.Mode.SRC_ATOP);
                    ImageViewCompat.setImageTintList(crowdLogo2, ColorStateList.valueOf(Color.parseColor("#DAC21F")));

                }
                else
                {
                    floor2count.setTextColor(getResources().getColor(R.color.red));
                    ImageViewCompat.setImageTintMode(crowdLogo2, PorterDuff.Mode.SRC_ATOP);
                    ImageViewCompat.setImageTintList(crowdLogo2, ColorStateList.valueOf(Color.parseColor("#D32F2F")));

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