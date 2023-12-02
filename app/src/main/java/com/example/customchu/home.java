
package com.example.customchu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class home extends AppCompatActivity {

    ImageButton homeBtn, toScanQR, toMap, notificationBtn, profileBtn;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView greetings, txtCounter;
    DatabaseReference databaseFacility;
    Button incrementBtn;
    Profile userProfile;
    int libRoom1 = 0;


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


        String PREF_NAME = "MyPreferences";
        String DARK_MODE_KEY = "darkMode";

        Switch darkMS;
        darkMS = findViewById(R.id.darkModeSwitch);

        // Restore the state of the switch from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean(DARK_MODE_KEY, false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }




        // Restore the state of the switch from SharedPreferences SSS
        darkMS.setChecked(isDarkMode);

        darkMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Save the state of the switch to SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(DARK_MODE_KEY, isChecked);
                editor.apply();

                // Set the night mode based on the switch state
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    darkMS.setText("Dark Mode");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    darkMS.setText("Light Mode");
                }

                // Recreate the activity to apply the night mode immediately
                recreate();
            }
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
        gsc = GoogleSignIn.getClient(this,gso);

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
