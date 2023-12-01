package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.squareup.picasso.Picasso;

public class profileActivity extends AppCompatActivity {
    ImageButton profileBack;
    ImageView profilePicture;
    EditText firstName, studentNumber, email;
    Button logout, changePass;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

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

        profileBack.setOnClickListener(view -> finish());
        logout.setOnClickListener(view -> {
            gsc.signOut().addOnCompleteListener(this, task -> {
                // navigate back to home activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        });

        updateUserinfo();
    }

    private void updateUserinfo() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // check if this account has been logged in with Google
        // if it is, update the profile picture, and make the fields read-only
        if (account != null) {
            // update the fields
            String profilePicUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";
            // only change profilePicUrl if it's not empty string
            if (!profilePicUrl.equals("")){
                Picasso.get()
                        .load(profilePicUrl)
                        .into(profilePicture);
            }


//            email.setTextColor(getResources().getColor(R.color.black));
//            firstName.setTextColor(getResources().getColor(R.color.black));
//            studentNumber.setTextColor(getResources().getColor(R.color.black));

            profilePicture.setImageURI(Uri.parse(profilePicUrl));
            email.setText(account.getEmail());
            firstName.setText(account.getGivenName() + " " + account.getFamilyName());
            //studentNumber.setText(account.getFamilyName());

            // make the fields read-only
            email.setEnabled(false);
            firstName.setEnabled(false);
            //studentNumber.setEnabled(false);
        }
    }
}