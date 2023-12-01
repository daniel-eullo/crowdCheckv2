package com.example.customchu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Button btnToSignup;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    DatabaseReference DB;
    Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnToSignup = findViewById(R.id.btnToSignup);
        btnToSignup.setOnClickListener(v -> signIn());
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        ProfileViewModel profileViewModel = new ProfileViewModel();

        DB = FirebaseDatabase.getInstance().getReference();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            if (!isUserAdamsonian(account)) {
                Toast.makeText(this, "Please use your Adamson email", Toast.LENGTH_SHORT).show();
                gsc.signOut();
                return;
            }

            Log.i("account id", account.getId());
            DatabaseReference ProfileReference = DB.child("Profiles").child(account.getId());
            ProfileReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userProfile = snapshot.getValue(Profile.class);
                        toHomeActivity();
                    } else {
                        // notify the user to complete the profile
                        Toast.makeText(MainActivity.this, "Please complete your profile", Toast.LENGTH_SHORT).show();
                        toProfileActivity();
                    }
                    profileViewModel.setProfile(userProfile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MainActivity", "onCancelled", databaseError.toException());
                }
            });

//            DB.child("Profiles").child(account.getId()).get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    DataSnapshot snapshot = task.getResult();
//                    Log.i("MainActivity", "onComplete: " + snapshot);
//
//                    if (snapshot.getValue(Profile.class).student_id != 0) {
//                        userProfile = snapshot.getValue(Profile.class);
//                        profileViewModel.setProfile(userProfile);
//                        toHomeActivity();
//                    } else {
//                        toProfileActivity();
//                    }
//                }
//            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            toHomeActivity();
        }
    }

    private void signIn() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount gmsacc = task.getResult(ApiException.class);
                if (!isUserAdamsonian(gmsacc)) {
                    Toast.makeText(this, "Please use your Adamson email", Toast.LENGTH_SHORT).show();
                    gsc.signOut();
                    return;
                }
                toHomeActivity();
            } catch (ApiException e) {
                Log.e("GoogleSignIn", Objects.requireNonNull(e.getMessage()));
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isUserAdamsonian(GoogleSignInAccount gmsacc) {
        return Objects.requireNonNull(gmsacc.getEmail()).contains("@adamson.edu.ph");
    }

    private void toProfileActivity() {
        Intent intent = new Intent(getApplicationContext(), profileActivity.class);
        startActivity(intent);
    }

    private void toHomeActivity() {
        finish();
        Intent intent = new Intent(getApplicationContext(), home.class);
        startActivity(intent);
    }
}


