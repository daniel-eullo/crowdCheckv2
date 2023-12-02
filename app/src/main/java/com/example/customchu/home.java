
package com.example.customchu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import android.content.SharedPreferences;


public class home extends AppCompatActivity {

    ImageButton homeBtn, toScanQR, toMap, notificationBtn, profileBtn;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView greetings, txtCounter;
    DatabaseReference databaseFacility;
    Button incrementBtn;
    Profile userProfile;
    int libRoom1 = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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



        // DARK MODE

        Switch darkModeSwitch;
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        boolean nightMode;

        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode){
            darkModeSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        darkModeSwitch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (nightMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", false);
                } else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", true);
                }
                editor.apply();

            }
        });

        // DARK MODE


        // NAVIGATION

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
