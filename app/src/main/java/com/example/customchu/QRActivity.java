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
    private CodeScanner mCodeScanner;
    GoogleSignInAccount user;
    Boolean qrScanned;
    String scannedContent;

    Dialog dialog, dialogExit;
    Button qrDialogCancel, qrDialogProceed, qrToHome;
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

        //databaseFacility = FirebaseDatabase.getInstance().getReference();

        //dialog box
//        dialog = new Dialog(QRActivity.this);
//        dialog.setContentView(R.layout.dialogbox_qr);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
//        dialog.setCancelable(false);
//
//        qrDialogCancel = dialog.findViewById(R.id.qrDialogCancel);
//        qrDialogProceed = dialog.findViewById(R.id.qrDialogProceed);

//        qrDialogCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });

//        qrDialogProceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(QRActivity.this, updatedlibrary.class);
////                startActivity(intent);
//
//                if (scannedContent.equalsIgnoreCase("Library Ground Floor")){
//                    Intent intent = new Intent(QRActivity.this, updatedlibrary.class);
//                    startActivity(intent);
//                } else if (scannedContent.equalsIgnoreCase("Library Second Floor")){
//                    Intent intent = new Intent(QRActivity.this, updatedlibraryb.class);
//                    startActivity(intent);
//                }
//
//                dialog.dismiss();
//            }
//        });

        //dialog box for
//        dialogExit = new Dialog(QRActivity.this);
//        dialogExit.setContentView(R.layout.dialog_exit);
//        dialogExit.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialogExit.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
//        dialogExit.setCancelable(false);
//
//        qrToHome = dialogExit.findViewById(R.id.qrToHome);
//        qrToHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(QRActivity.this, home.class);
//                startActivity(intent);
//
//                dialogExit.dismiss();
//            }
//        });

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            scannedContent = result.getText();
            txtScan.setText(scannedContent);

            mCodeScanner.startPreview();
        }));

        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrScanned = false;
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


    public static final int CAMERA_REQUEST_CODE = 101;
}

