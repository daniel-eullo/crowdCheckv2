package com.example.customchu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import androidx.annotation.NonNull;

public class BookSystem extends AppCompatActivity {

    private ImageButton BookSystemBack;
    private Button ReturnBookBtn;
    TextView txtScan;
    private CodeScanner mCodeScanner;
    Boolean qrScanned = false;
    String scannedContent;
    public static final int CAMERA_REQUEST_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_system);

        BookSystemBack = findViewById(R.id.BookSystemBack);
        BookSystemBack.setOnClickListener(view -> {
            Intent intent = new Intent(BookSystem.this, home.class);
            startActivity(intent);
        });

        ReturnBookBtn = findViewById(R.id.ReturnBookBtn);
        ReturnBookBtn.setOnClickListener(view -> {
            Intent intent = new Intent(BookSystem.this, BookReturn.class);
            startActivity(intent);
        });

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
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void setupPermissions() {
        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
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



}