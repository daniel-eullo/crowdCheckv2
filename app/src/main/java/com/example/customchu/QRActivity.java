package com.example.customchu;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;


public class QRActivity extends AppCompatActivity {
    ImageButton qrBack;
    TextView txtScan;
    DatabaseReference databaseFacility;
    int libRoom1 = 0, libRoom2 = 0;
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr);

        txtScan = findViewById(R.id.txtScan);

        setupPermissions();

        qrBack = findViewById(R.id.qrBack);
        qrBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QRActivity.this, home.class);
                startActivity(intent);
            }
        });

        databaseFacility = FirebaseDatabase.getInstance().getReference();
        DatabaseReference room1 = databaseFacility.child("Rooms").child("GF").child("Current");
        DatabaseReference room2 = databaseFacility.child("Rooms").child("2F").child("Current");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                libRoom1 = Integer.parseInt(dataSnapshot.getValue() + "");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };
        room1.addValueEventListener(postListener);

        ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                libRoom2 = Integer.parseInt(dataSnapshot.getValue() + "");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };
        room2.addValueEventListener(postListener2);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String scannedContent = result.getText();
                        String expectedContent = "Library Room1";
                        if (scannedContent.equalsIgnoreCase("Library Ground Floor")) {
                            txtScan.setText("QR Code successfully scanned: " + scannedContent);
                            room1.setValue(libRoom1 + 1);
                            //successful notif muna dapat dito
                            Intent intent = new Intent(QRActivity.this, mapActivity.class);
                            startActivity(intent);
                        } else if (scannedContent.equalsIgnoreCase("Library Second Floor")) {
                            txtScan.setText("QR Code successfully scanned: " + scannedContent);
                            room2.setValue(libRoom2 + 1);
                            //successful notif muna dapat dito
                            Intent intent = new Intent(QRActivity.this, library2Activity.class);
                            startActivity(intent);
                        } else if (scannedContent.equalsIgnoreCase("Library Ground Floor Exit")) {
                            txtScan.setText("QR Code successfully scanned: " + scannedContent);
                            room1.setValue(libRoom1 - 1);
                            txtScan.setText("Exit scanned. See you again!");
                            //successful notif muna dapat dito
                            Intent intent = new Intent(QRActivity.this, mapActivity.class);
                            startActivity(intent);
                        } else if (scannedContent.equalsIgnoreCase("Library Second Floor Exit")) {
                            txtScan.setText("QR Code successfully scanned: " + scannedContent);
                            room2.setValue(libRoom2 - 1);
                            txtScan.setText("Exit scanned. See you again!");
                            //successful notif muna dapat dito
                            Intent intent = new Intent(QRActivity.this, library2Activity.class);
                           startActivity(intent);
                        }else {
                            txtScan.setText("Invalid QR Code, try again");
                        }
                        mCodeScanner.startPreview();
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void setupPermissions(){
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest();
        }
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need camera permission to use the scanner", Toast.LENGTH_SHORT).show();
                } else {
                    // Successful
                }
                break;
        }
    }

    public static final int CAMERA_REQUEST_CODE = 101;

}

