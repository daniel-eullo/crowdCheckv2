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
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Map;
    import java.text.SimpleDateFormat;
    import java.util.Calendar;
    import java.util.Date;



    public class QRActivity extends AppCompatActivity {
        ImageButton qrBack;
        TextView txtScan;
        private CodeScanner mCodeScanner;
        GoogleSignInAccount user;
        Boolean qrScanned;
        String scannedContent;
        Button testDialog;

        Dialog dialog, dialogExit, book_title_item;
        Button qrDialogCancel, qrDialogProceed, qrToHome, confirmBtn, cancelBtn;
        Spinner bookSpinner;
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

            testDialog = findViewById(R.id.testDialog);

            book_title_item = new Dialog(QRActivity.this);
            book_title_item.setContentView(R.layout.book_title_item);
            book_title_item.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            book_title_item.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
            book_title_item.setCancelable(false);

            confirmBtn = book_title_item.findViewById(R.id.confirmBtn);
            cancelBtn = book_title_item.findViewById(R.id.cancelBtn);
            testDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    book_title_item.show();

                    // Initialize and set up the Spinner
                    bookSpinner = book_title_item.findViewById(R.id.bookSpinner);

                    // Set up the Spinner with data
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(QRActivity.this,
                            R.array.due_dates, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    bookSpinner.setAdapter(adapter);

                    // Set onClickListener for the confirmBtn
                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get the selected item from the Spinner
                            String selectedItem = bookSpinner.getSelectedItem().toString();
                            Log.d(TAG, "Selected item: " + selectedItem);

                            // Close the dialog
                            book_title_item.dismiss();
                        }
                    });

                    // Set onClickListener for the cancelBtn
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Close the dialog
                            book_title_item.dismiss();
                        }
                    });
                }
            });


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
                // Check if the scannedContent exists under the Books node
                DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("Books").child(scannedContent);
                booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Book exists, update the database
                            updateBookData(scannedContent);
                        } else {
                            // Book not found
                            Toast.makeText(QRActivity.this, "Book not found", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Error checking book existence", databaseError.toException());
                    }
                });

                mCodeScanner.startPreview();
            }));

            scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
        }

        private void updateBookData(String bookTitle) {
            DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("Books").child(bookTitle);

            // Get current user details
            String borrowerName = user != null ? user.getGivenName() : "Unknown";

            // Get current date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(new Date());

            // Calculate due date (7 days from now)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            String dueDate = sdf.format(calendar.getTime());

            // Check if due date is weekend and adjust if necessary
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY) {
                calendar.add(Calendar.DAY_OF_YEAR, 2); // Move to Monday
                dueDate = sdf.format(calendar.getTime());
            } else if (dayOfWeek == Calendar.SUNDAY) {
                calendar.add(Calendar.DAY_OF_YEAR, 1); // Move to Monday
                dueDate = sdf.format(calendar.getTime());
            }

            // Update book data
            Map<String, Object> bookUpdates = new HashMap<>();
            bookUpdates.put("borrower", borrowerName);
            bookUpdates.put("date_borrowed", currentDate);
            bookUpdates.put("date_due", dueDate);
            bookUpdates.put("status", 1); // Assuming status 1 means borrowed

            // Check if the scannedContent exists under the Books node
            booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Book exists, show the dialog to choose due date
                        book_title_item.show();

                        // Initialize and set up the Spinner
                        bookSpinner = book_title_item.findViewById(R.id.bookSpinner);

                        // Set up the Spinner with data
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(QRActivity.this,
                                R.array.due_dates, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        bookSpinner.setAdapter(adapter);

                        // Set onClickListener for the confirmBtn
                        confirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Get the selected item from the Spinner
                                String selectedItem = bookSpinner.getSelectedItem().toString();
                                Log.d(TAG, "Selected item: " + selectedItem);

                                // Calculate new due date based on selected item
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

                                // Update due date
                                Calendar newCalendar = Calendar.getInstance();
                                newCalendar.setTime(calendar.getTime());
                                newCalendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
                                String newDueDate = sdf.format(newCalendar.getTime());
                                bookUpdates.put("date_due", newDueDate);

                                // Close the dialog
                                book_title_item.dismiss();

                                // Update book data in Firebase
                                booksRef.updateChildren(bookUpdates)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Book data updated successfully");
                                            Toast.makeText(QRActivity.this, "Book borrowed successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error updating book data", e);
                                            Toast.makeText(QRActivity.this, "Error borrowing book", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        });

                        // Set onClickListener for the cancelBtn
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Close the dialog
                                book_title_item.dismiss();
                            }
                        });
                    } else {
                        // Book not found
                        Toast.makeText(QRActivity.this, "Book not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error checking book existence", databaseError.toException());
                }
            });
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

