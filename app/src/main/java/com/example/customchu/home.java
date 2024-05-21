package com.example.customchu;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class home extends AppCompatActivity {

    private static final String CHANNEL_ID = "library_closure_channel";
    private static final String CHANNEL_NAME = "Library Closure Channel";
    private static final String DUE_BOOK_CHANNEL_ID = "due_book_channel";
    private static final String DUE_BOOK_CHANNEL_NAME = "Due Book Channel";

    ImageButton howTo, toBookSystem, toMap, notificationBtn, profileBtn, friendsBtn, ToBookSystem;
    ImageView toFeedback, toAdmin, toGraph;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView greetings, txtCounter;
    DatabaseReference databaseFacility, facilityStatus;
    Button infoClose, eventClose;
    Profile userProfile;
    int libRoom1 = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Dialog dialoghowTo, dialogEvent;
    Boolean status = false;

    String[] adminEmails = {
            "leonard.jade.balajadia@adamson.edu.ph",
            "mykah.anbea.cabiles@adamson.edu.ph",
            "don.daniel.eullo@adamson.edu.ph"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // fetch the user profile
        userProfile = (Profile) getIntent().getSerializableExtra("profile");

        facilityStatus = FirebaseDatabase.getInstance().getReference().child("Event");

        howTo = findViewById(R.id.homebtn);
        greetings = findViewById(R.id.userGreet);
        toBookSystem = findViewById(R.id.toBookSystem);
        toMap = findViewById(R.id.toMap);
        friendsBtn = findViewById(R.id.friendsbtn);
        notificationBtn = findViewById(R.id.notificationbtn);
        profileBtn = findViewById(R.id.profilebtn);

        // fetch the last signed in google account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // check if the account's email does not exist in the admin email array
        if (account != null && !Objects.equals(account.getEmail(), adminEmails[0]) && !Objects.equals(account.getEmail(), adminEmails[1]) && !Objects.equals(account.getEmail(), adminEmails[2])) {
            // if the account's email does not exist in the admin email array, hide the admin button
            toAdmin = findViewById(R.id.toAdmin);
            toAdmin.setVisibility(View.GONE);
        }

        // NAVIGATION

        toGraph = findViewById(R.id.toGraph);
        toGraph.setOnClickListener(view -> {
            Intent intent = new Intent(home.this, activity_graph2.class);
            startActivity(intent);
        });

        toFeedback = findViewById(R.id.toFeedback);
        toFeedback.setOnClickListener(view -> {
            Intent intent = new Intent(home.this, userFeedback.class);
            startActivity(intent);
        });

        toAdmin = findViewById(R.id.toAdmin);
        toAdmin.setOnClickListener(view -> {
            Intent intent = new Intent(home.this, adminActivity.class);
            startActivity(intent);
        });

        toBookSystem.setOnClickListener(view -> {
            if (status == true) {
                dialogEvent.show();
            } else if (status == false) {
                Intent intent = new Intent(home.this, QRActivity.class);
                startActivity(intent);
            }
        });

        friendsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(home.this, friends.class);
            startActivity(intent);
        });

        notificationBtn.setOnClickListener(view -> {
            Intent intent = new Intent(home.this, settingsActivity.class);
            startActivity(intent);
        });

        profileBtn.setOnClickListener(view -> {
            Intent intent = new Intent(home.this, profileActivity.class);
            intent.putExtra("profile", userProfile);
            startActivity(intent);
        });

        toMap.setOnClickListener(view -> {
            if (status == true) {
                dialogEvent.show();
            } else if (status == false) {
                Intent intent = new Intent(home.this, updatedlibrary.class); //mapActivity or updatedlibrary
                startActivity(intent);
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        dialoghowTo = new Dialog(home.this);
        dialoghowTo.setContentView(R.layout.dialog_howto);
        dialoghowTo.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialoghowTo.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        dialoghowTo.setCancelable(false);

        infoClose = dialoghowTo.findViewById(R.id.infoClose);
        infoClose.setOnClickListener(view -> dialoghowTo.dismiss());

        howTo.setOnClickListener(view -> dialoghowTo.show());

        dialogEvent = new Dialog(home.this);
        dialogEvent.setContentView(R.layout.dialog_event);
        dialogEvent.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogEvent.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        dialogEvent.setCancelable(false);

        eventClose = dialogEvent.findViewById(R.id.eventClose);
        eventClose.setOnClickListener(view -> dialogEvent.dismiss());

        facilityStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String newStatus = snapshot.getValue(String.class);

                if (newStatus != null && !newStatus.equals("None")) {
                    sendNotification("Library Closure", "The library is closed due to " + newStatus);
                }

                status = newStatus != null && !newStatus.equals("None");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle onCancelled event if needed
            }
        });

        updateUsername();

        // Check for due books on start
        checkDueBooks();

        // Schedule periodic checks
        scheduleDueBookCheck();
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icons8_notification_90)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

    private void sendDueBookNotification(String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    DUE_BOOK_CHANNEL_ID,
                    DUE_BOOK_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DUE_BOOK_CHANNEL_ID)
                .setSmallIcon(R.drawable.icons8_notification_90)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(2, builder.build());
    }

    private void updateUsername() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String name = account.getGivenName();
            greetings.setText(name);
        }
    }

    private void checkDueBooks() {
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("Books");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) return;

        String currentUserName = account.getGivenName();
        String currentDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());

        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    String dateDue = bookSnapshot.child("date_due").getValue(String.class);
                    String borrower = bookSnapshot.child("borrower").getValue(String.class);

                    if (dateDue != null && borrower != null &&
                            borrower.equals(currentUserName) && dateDue.equals(currentDate)) {
                        sendDueBookNotification("Book Due", "Your borrowed book is due today.");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });
    }

    private void scheduleDueBookCheck() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                checkDueBooks();
                handler.postDelayed(this, 24 * 60 * 60 * 1000); // Repeat every 24 hours
            }
        };
        handler.post(runnable);
    }
}
