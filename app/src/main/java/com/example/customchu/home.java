
package com.example.customchu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;


public class home extends AppCompatActivity {

    ImageButton homeBtn, toScanQR, toMap, notificationBtn, profileBtn;
    ImageView toFeedback, toAdmin, toGraph;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView greetings, txtCounter;
    DatabaseReference databaseFacility;
    Button incrementBtn;
    Profile userProfile;
    int libRoom1 = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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

        homeBtn = findViewById(R.id.homebtn);
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
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
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
            Intent intent = new Intent(home.this, QRActivity.class);
            startActivity(intent);
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
            Intent intent = new Intent(home.this, updatedlibrary.class); //mapActivity or updatedlibrary
            startActivity(intent);
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        homeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(home.this, home.class);
            startActivity(intent);
        });
        updateUsername();
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
