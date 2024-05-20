package com.example.customchu;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QRActivity extends AppCompatActivity {
    ImageButton qrBack;
    TextView txtScan;
    private CodeScanner mCodeScanner;

    private static final String TAG = "QRActivity";

    private boolean qrCodeScanned = false, returningBook = false;
    GoogleSignInAccount user;
    String scannedContent;
    Button testDialog;

    Dialog dialog, dialogExit, book_title_item;
    Button qrDialogCancel, qrDialogProceed, qrToHome, confirmBtn, cancelBtn;
    Spinner bookSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr);

        // Fetch the logged in user
        user = GoogleSignIn.getLastSignedInAccount(this);

        txtScan = findViewById(R.id.txtScan);

        setupPermissions();

        qrBack = findViewById(R.id.qrBack);
        qrBack.setOnClickListener(view -> finish());

        testDialog = findViewById(R.id.testDialog);

        book_title_item = new Dialog(QRActivity.this);
        book_title_item.setContentView(R.layout.book_title_item);
        book_title_item.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        book_title_item.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        book_title_item.setCancelable(false);

        confirmBtn = book_title_item.findViewById(R.id.confirmBtn);
        cancelBtn = book_title_item.findViewById(R.id.cancelBtn);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            scannedContent = result.getText();
            txtScan.setText(scannedContent);

            DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("Books").child(scannedContent);

            if (!qrCodeScanned) {
                checkAndHandleReturn(booksRef);
                qrCodeScanned = true; // Set the flag to indicate that the QR code has been scanned
            } else {
                booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            checkAndHandleReturn(booksRef);
                        } else {
                            Toast.makeText(QRActivity.this, "Book not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Error checking book existence", databaseError.toException());
                    }
                });
            }
        }));

        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    private boolean dialogShown = false;

    private void checkAndHandleReturn(DatabaseReference booksRef) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int status = dataSnapshot.child("status").getValue(Integer.class);
                    String borrowerNameInDB = dataSnapshot.child("borrower").getValue(String.class);
                    String currentUser = user != null ? user.getGivenName() : "Unknown";

                    if (status == 1) {
                        if (borrowerNameInDB != null && borrowerNameInDB.equals(currentUser)) {
                            if (!dialogShown) {
                                new AlertDialog.Builder(QRActivity.this)
                                        .setTitle("Return Book")
                                        .setMessage("Would you like to return this book?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                returningBook = true;
                                                Map<String, Object> bookUpdates = new HashMap<>();
                                                bookUpdates.put("borrower", " ");
                                                bookUpdates.put("date_borrowed", " ");
                                                bookUpdates.put("date_due", " ");
                                                bookUpdates.put("status", 0); // Assuming status 0 means available

                                                booksRef.updateChildren(bookUpdates)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Log.d(TAG, "Book returned successfully");
                                                            Toast.makeText(QRActivity.this, "Book returned successfully", Toast.LENGTH_LONG).show();
                                                            qrCodeScanned = false; // Reset the flag for borrowing
                                                            dialogShown = false; // Reset the dialogShown flag
                                                            returningBook = false;
                                                            mCodeScanner.startPreview(); // Restart the QR scanner
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e(TAG, "Error updating book data", e);
                                                            Toast.makeText(QRActivity.this, "Error returning book", Toast.LENGTH_LONG).show();
                                                            qrCodeScanned = false; // Reset the flag for borrowing
                                                            dialogShown = false; // Reset the dialogShown flag
                                                            returningBook = false;
                                                            mCodeScanner.startPreview(); // Restart the QR scanner
                                                        });
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                qrCodeScanned = false; // Reset the flag for borrowing
                                                dialogShown = false; // Reset the dialogShown flag
                                                mCodeScanner.startPreview(); // Restart the QR scanner
                                            }
                                        })
                                        .show();
                                dialogShown = true; // Set dialogShown to true after showing the dialog
                            }
                        } else {
                            Toast.makeText(QRActivity.this, "This book is borrowed by another user", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (!dialogShown) {
                            updateBookData(scannedContent);
                            dialogShown = true; // Set dialogShown to true after showing the dialog
                        }
                    }
                }
                mCodeScanner.startPreview(); // Restart the QR scanner preview after each scan
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error checking book data", databaseError.toException());
                mCodeScanner.startPreview(); // Restart the QR scanner preview on error
            }
        };
        booksRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void updateBookData(String bookTitle) {
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("Books").child(bookTitle);

        String borrowerName = user != null ? user.getGivenName() : "Unknown";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 0);
        String dueDate = sdf.format(calendar.getTime());

        Map<String, Object> bookUpdates = new HashMap<>();
        bookUpdates.put("borrower", borrowerName);
        bookUpdates.put("date_borrowed", currentDate);
        bookUpdates.put("status", 1); // Assuming status 1 means borrowed

        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    book_title_item.show();

                    bookSpinner = book_title_item.findViewById(R.id.bookSpinner);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(QRActivity.this,
                            R.array.due_dates, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    bookSpinner.setAdapter(adapter);

                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String selectedItem = bookSpinner.getSelectedItem().toString();
                            Log.d(TAG, "Selected item: " + selectedItem);

                            int daysToAdd;
                            switch (selectedItem) {
                                case "1 day":
                                    daysToAdd = 1;
                                    break;
                                case "3 days":
                                    daysToAdd = 3;
                                    break;
                                case "5 days":
                                    daysToAdd = 5;
                                    break;
                                case "1 week":
                                default:
                                    daysToAdd = 7;
                                    break;
                            }

                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.setTime(calendar.getTime());
                            newCalendar.add(Calendar.DAY_OF_YEAR, daysToAdd);

                            // Adjust for weekends
                            int dayOfWeek = newCalendar.get(Calendar.DAY_OF_WEEK);
                            if (dayOfWeek == Calendar.SATURDAY) {
                                newCalendar.add(Calendar.DAY_OF_YEAR, 2); // Move to Monday
                            } else if (dayOfWeek == Calendar.SUNDAY) {
                                newCalendar.add(Calendar.DAY_OF_YEAR, 1); // Move to Monday
                            }

                            String newDueDate = sdf.format(newCalendar.getTime());
                            bookUpdates.put("date_due", newDueDate);

                            book_title_item.dismiss();

                            booksRef.updateChildren(bookUpdates)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Book data updated successfully");
                                        Toast.makeText(QRActivity.this, "Book borrowed successfully", Toast.LENGTH_SHORT).show();
                                        qrCodeScanned = false; // Reset the flag for borrowing
                                        dialogShown = false; // Reset the dialogShown flag
                                        mCodeScanner.startPreview(); // Restart the QR scanner
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating book data", e);
                                        Toast.makeText(QRActivity.this, "Error borrowing book", Toast.LENGTH_SHORT).show();
                                        qrCodeScanned = false; // Reset the flag for borrowing
                                        dialogShown = false; // Reset the dialogShown flag
                                        mCodeScanner.startPreview(); // Restart the QR scanner
                                    });
                        }
                    });

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            book_title_item.dismiss();
                            qrCodeScanned = false; // Reset the flag for borrowing
                            dialogShown = false; // Reset the dialogShown flag
                            mCodeScanner.startPreview(); // Restart the QR scanner
                        }
                    });
                } else {
                    Toast.makeText(QRActivity.this, "Book not found", Toast.LENGTH_SHORT).show();
                    qrCodeScanned = false; // Reset the flag for borrowing
                    mCodeScanner.startPreview(); // Restart the QR scanner
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error checking book existence", databaseError.toException());
                qrCodeScanned = false; // Reset the flag for borrowing
                mCodeScanner.startPreview(); // Restart the QR scanner
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        qrCodeScanned = false;
        dialogShown = false; // Reset dialogShown flag on resume
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
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You need camera permission to use the scanner", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static final int CAMERA_REQUEST_CODE = 101;
}
