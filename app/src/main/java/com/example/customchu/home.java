
package com.example.customchu;


import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

import java.util.Objects;


public class home extends AppCompatActivity {

    ImageButton howTo, toScanQR, toMap, notificationBtn, profileBtn;
    ImageView toFeedback, toAdmin, toGraph;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView greetings, txtCounter;
    DatabaseReference databaseFacility, facilityStatus;;
    Button infoClose, eventClose;
    Profile userProfile;
    int libRoom1 = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Dialog dialoghowTo, dialogEvent;
    Boolean status = false;

    private static final String CHANNEL_ID = "library_closure_channel";
    private static final String CHANNEL_NAME = "Library Closure Channel";

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
        toScanQR = findViewById(R.id.toScanQR);
        toMap = findViewById(R.id.toMap);
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

        // DARK MODE

        Switch darkModeSwitch;
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        boolean nightMode;

        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode) {
            darkModeSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        darkModeSwitch.setOnClickListener(view -> {
            if (nightMode) {
                // Change to light mode without animation
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", false);
            } else {
                // Change to dark mode without animation
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", true);
            }
            editor.apply();
        });


        // DARK MODE


        // NAVIGATION

        toGraph = findViewById(R.id.toGraph);
        toGraph.setOnClickListener(view -> {
            Intent intent = new Intent(home.this, graph_activity.class);
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

        toScanQR.setOnClickListener(view -> {
            if (status == true){
                dialogEvent.show();
            } else if (status == false){
                Intent intent = new Intent(home.this, QRActivity.class);
                startActivity(intent);
            }
        });

        notificationBtn.setOnClickListener(view -> {
            Intent intent = new Intent(home.this, notifActivity.class);
            startActivity(intent);
        });

        profileBtn.setOnClickListener(view -> {
            Intent intent = new Intent(home.this, profileActivity.class);
            intent.putExtra("profile", userProfile);
            startActivity(intent);
        });

        toMap.setOnClickListener(view -> {
            if (status == true){
                dialogEvent.show();
            } else if (status == false){
                Intent intent = new Intent(home.this, updatedlibrary.class); //mapActivity or updatedlibrary
                startActivity(intent);
            }

        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        dialoghowTo= new Dialog(home.this);
        dialoghowTo.setContentView(R.layout.dialog_howto);
        dialoghowTo.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialoghowTo.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        dialoghowTo.setCancelable(false);

        infoClose = dialoghowTo.findViewById(R.id.infoClose);

        infoClose.setOnClickListener(view -> {
            dialoghowTo.dismiss();
        });

        howTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialoghowTo.show();
            }
        });

        dialogEvent= new Dialog(home.this);
        dialogEvent.setContentView(R.layout.dialog_event);
        dialogEvent.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogEvent.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        dialogEvent.setCancelable(false);

        eventClose = dialogEvent.findViewById(R.id.eventClose);

        eventClose.setOnClickListener(view -> {
            dialogEvent.dismiss();
        });


        facilityStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String newStatus = snapshot.getValue(String.class);

                // Check if newStatus is not null and not equal to "None"
                if (newStatus != null && !newStatus.equals("None")) {
                    // Call the method to handle the notification logic
                    sendNotification("Library Closure", "The library is closed due to " + newStatus);
                }

                if (newStatus.equals("None")) {
                    status = false;
                } else if (newStatus != null){
                    status = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle onCancelled event if needed
            }
        });

        updateUsername();
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

    private void updateUsername() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String name = account.getGivenName();
            greetings.setText(name);
            //Toast.makeText(this, "Login Success! Welcome " + name, Toast.LENGTH_SHORT).show();
        }
    }
}
