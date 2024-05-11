package com.example.customchu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class profileActivity extends AppCompatActivity {
    ImageButton profileBack;
    ImageView profilePicture;
    EditText firstName, studentNumber, email, uid;
    Button logout, changeInfo;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    DatabaseReference DB;
    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // create a reference to the database
        DB = FirebaseDatabase.getInstance().getReference();
        profile = getIntent().getSerializableExtra("profile") != null ? (Profile) getIntent().getSerializableExtra("profile") : new Profile();
        Log.i("profilePage", profile.toString());

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        profileBack = findViewById(R.id.profileBack);
        profilePicture = findViewById(R.id.profilepicture);
        firstName = findViewById(R.id.firstname);
        studentNumber = findViewById(R.id.studentNumber);
        email = findViewById(R.id.email);
        logout = findViewById(R.id.toLogout);
        changeInfo = findViewById(R.id.changeinfo);
        uid = findViewById(R.id.idnumber);

        profileBack.setOnClickListener(view -> {
            Intent intent = new Intent(profileActivity.this, home.class);
            intent.putExtra("profile", profile);
            startActivity(intent);
        });
        logout.setOnClickListener(view -> gsc.signOut().addOnCompleteListener(this, task -> {
            // navigate back to home activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }));
        changeInfo.setOnClickListener(view -> updateProfile());

        updateUserinfoUI();
    }

    private void updateUserinfoUI() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // update the profile picture regardless of the account status
        String profilePicUrl = account != null && account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";
        if (!profilePicUrl.equals("")) {
            Picasso.get()
                    .load(profilePicUrl)
                    .into(profilePicture);
        }

        // check if the account exists and update other UI fields
        if (account != null) {
            email.setText(account.getEmail());
            firstName.setText(account.getGivenName() + " " + account.getFamilyName());

            // Fetch student number and UID from the database
            DatabaseReference ProfileReference = DB.child("Profiles").child(account.getId());
            ProfileReference.child("student_id").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Integer studentIdValue = task.getResult().getValue(Integer.class);
                    if (studentIdValue != null) {
                        studentNumber.setText(String.valueOf(studentIdValue));
                    }
                } else {
                    Log.e("TAG", "Error getting student ID", task.getException());
                }
            });

            ProfileReference.child("uid").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Integer uidValue = task.getResult().getValue(Integer.class);
                    if (uidValue != null) {
                        uid.setText(String.valueOf(uidValue));
                    }
                } else {
                    Log.e("TAG", "Error getting UID", task.getException());
                }
            });

            // make the fields read-only
            email.setEnabled(false);
            firstName.setEnabled(false);
        }
    }

    private void updateProfile() {
        try {
            // Fetch the student number and UID from the UI
            String studentNumber = this.studentNumber.getText().toString();
            String userId = uid.getText().toString();
            Integer uidCur = 0;

            // Check if the student number or UID is empty
            if (studentNumber.isEmpty() || userId.isEmpty()) {
                Toast.makeText(this, "Please complete your profile", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fetch the currently signed-in account
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            // Create a reference to the profile
            DatabaseReference profileReference = DB.child("Profiles").child(account.getId());

            // Create a new profile object
            profile = new Profile(Integer.parseInt(studentNumber));

            // Set the value of the profile
            profileReference.setValue(profile);

            // Create or update the profile in the database
            profileReference.setValue(profile);

            // Set the UID in the profile
            DatabaseReference userIdReference = profileReference.child("uid");
            userIdReference.setValue(Integer.parseInt(userId));

            // Get the name of the user
            String userName = account.getGivenName() + " " + account.getFamilyName();

            // Update the user's name in the users node
            uidCur = Integer.parseInt(userId);
            DatabaseReference nameReference = DB.child("users").child(uidCur.toString()).child("student_name");
            nameReference.setValue(userName);

            // Notify the user that the profile has been updated
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Notify the user that there has been an error
            Toast.makeText(this, "There has been an error. Profile has not been updated", Toast.LENGTH_SHORT).show();
        }
    }

}