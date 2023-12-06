package com.example.customchu;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class QRActivity extends AppCompatActivity {
    ImageButton qrBack;
    TextView txtScan;
    DatabaseReference databaseFacility;
    int libRoom1 = 0, libRoom2 = 0;
    int capLibRoom1 = 50, capLibRoom2 = 50;
    private CodeScanner mCodeScanner;
    GoogleSignInAccount user;

    DatabaseReference room1, room2, capRoom1, capRoom2;
    Dialog dialog;
    Button qrDialogCancel, qrDialogProceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr);

        // fetch the logged in user
        user = GoogleSignIn.getLastSignedInAccount(this);

        txtScan = findViewById(R.id.txtScan);

        setupPermissions();

        qrBack = findViewById(R.id.qrBack);
        qrBack.setOnClickListener(view -> finish());

        databaseFacility = FirebaseDatabase.getInstance().getReference();

        //dialog box
        dialog = new Dialog(QRActivity.this);
        dialog.setContentView(R.layout.dialogbox_qr);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        dialog.setCancelable(false);

        qrDialogCancel = dialog.findViewById(R.id.qrDialogCancel);
        qrDialogProceed = dialog.findViewById(R.id.qrDialogProceed);

        qrDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        qrDialogProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QRActivity.this, updatedlibrary.class);
                startActivity(intent);

                dialog.dismiss();
            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                libRoom1 = Integer.parseInt(dataSnapshot.child("Current").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };
        room1 = databaseFacility.child("Rooms").child("GF");
        room1.addValueEventListener(postListener);

        ValueEventListener postListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                capLibRoom1 = Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };
        capRoom1 = databaseFacility.child("Rooms").child("GF").child("Cap");
        capRoom1.addValueEventListener(postListener3);

        ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                libRoom2 = Integer.parseInt(dataSnapshot.child("Current").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };
        room2 = databaseFacility.child("Rooms").child("2F");
        room2.addValueEventListener(postListener2);

        ValueEventListener postListener4 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                capLibRoom2 = Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };
        capRoom2 = databaseFacility.child("Rooms").child("2F").child("Cap");
        capRoom2.addValueEventListener(postListener4);


        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            String scannedContent = result.getText();
            String expectedContent = "Library Room1";
            if (scannedContent.equalsIgnoreCase("Library Ground Floor")) {
                // check if room is full
                if (libRoom1 >= capLibRoom1) {
                    // show message that the room is full
                    txtScan.setText("Room is full, try again later");
                    return;
                }

                else{
                    txtScan.setText("QR Code successfully scanned: " + scannedContent);

                    // insert to database
                    room1.child("Current").setValue(libRoom1 + 1);
                    insertOnRoom1();

                    dialog.show();
//                    Intent intent = new Intent(QRActivity.this, updatedlibrary.class);
//                    startActivity(intent);
                }
            } else if (scannedContent.equalsIgnoreCase("Library Second Floor")) {
                // check if room is full
                if (libRoom2 >= capLibRoom2) {
                    txtScan.setText("Room is full, try again later");
                    return;
                }

                else{
                    txtScan.setText("QR Code successfully scanned: " + scannedContent);

                    // insert to database
                    room2.child("Current").setValue(libRoom2 + 1);
                    insertOnRoom2();

                    Intent intent = new Intent(QRActivity.this, updatedlibraryb.class);
                    startActivity(intent);
                }


            } else if (scannedContent.equalsIgnoreCase("Library Ground Floor Exit")) {
                // check if room is full
                if (libRoom1 <= 0) {
                    txtScan.setText("Room is empty, try again later");
                    return;
                }

                else {
                    txtScan.setText("QR Code successfully scanned: " + scannedContent);

                    // insert to database
                    room1.child("Current").setValue(libRoom1 - 1);
                    outsertOnRoom1();

                    txtScan.setText("Exit scanned. See you again!");
                    Intent intent = new Intent(QRActivity.this, updatedlibrary.class);
                    startActivity(intent);
                }
            } else if (scannedContent.equalsIgnoreCase("Library Second Floor Exit")) {
                // check if room is full
                if (libRoom2 <= 0) {
                    txtScan.setText("Room is empty, try again later");
                    return;
                }

                else{
                    txtScan.setText("QR Code successfully scanned: " + scannedContent);

                    // insert to database
                    room2.child("Current").setValue(libRoom2 - 1);
                    outsertOnRoom2();

                    txtScan.setText("Exit scanned. See you again!");
                    //successful notif muna dapat dito
                    Intent intent = new Intent(QRActivity.this, updatedlibraryb.class);
                    startActivity(intent);
                }


            } else {
                txtScan.setText("Invalid QR Code, try again");
            }
            mCodeScanner.startPreview();
        }));

        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
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

    private void setupPermissions() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
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
                }  // Successful

                break;
        }
    }

    private void outsertOnRoom1() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        room1.child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("account_id", user.getId());
                    data.put("in", false);
                    data.put("out", true);
                    data.put("date_and_time", dateAndTime);

                    room1.child("History").child(date).child(user.getId()+"out").setValue(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void outsertOnRoom2() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        room2.child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("account_id", user.getId());
                    data.put("in", false);
                    data.put("out", true);
                    data.put("date_and_time", dateAndTime);

                    room2.child("History").child(date).child(user.getId()+"out").setValue(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void insertOnRoom1() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        room1.child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("account_id", user.getId());
                    data.put("in", true);
                    data.put("out", false);
                    data.put("date_and_time", dateAndTime);

                    room1.child("History").child(date).child(user.getId()+"in").setValue(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void insertOnRoom2() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        room2.child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("account_id", user.getId());
                    data.put("in", true);
                    data.put("out", false);
                    data.put("date_and_time", dateAndTime);

                    room2.child("History").child(date).child(user.getId()+"in").setValue(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static final int CAMERA_REQUEST_CODE = 101;
}

