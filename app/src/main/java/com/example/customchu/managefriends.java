package com.example.customchu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class managefriends extends AppCompatActivity {

    private ImageButton managefriendsBack;
    EditText uid;
    Button uidSubmit;
    TextView testTxt, testTxt2;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    DatabaseReference DB;
    Integer uidCur = 0, uidFr = 0;
    String uidInput = "";
    boolean uidFound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managefriends);

        managefriendsBack = findViewById(R.id.managefriendsBack);
        managefriendsBack.setOnClickListener(view -> {
            Intent intent = new Intent(managefriends.this, friends.class);
            startActivity(intent);
        });

        uid = findViewById(R.id.uidTxt);
        uidSubmit = findViewById(R.id.uidSubmit);
        testTxt = findViewById(R.id.testTxt);
        testTxt2 = findViewById(R.id.testTxt2);

        uidSubmit.setOnClickListener(view -> {
            uidInput = uid.getText().toString();

            // Check if uidInput is equal to uidCur
            if (uidInput.equals(uidCur.toString())) {
                // If uidInput is equal to uidCur, display error message and return
                testTxt2.setText("Cannot send request to own UID");
                return;
            }

            // Reference to the "users" node
            DatabaseReference usersRef = DB.child("users");

            // Add a listener to read the data at the "users" node
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    uidFound = false;

                    // Iterate through all children of the "users" node
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userUid = userSnapshot.getKey();

                        // Compare each child UID with uidInput
                        if (userUid.equals(uidInput)) {
                            uidFound = true;
                            sendFriendRequest(uidCur.toString(), uidInput);
                            break;
                        }
                    }

                    // Update the TextView based on the result
                    if (uidFound) {
                        testTxt2.setText("True");
                    } else {
                        testTxt2.setText("False");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Log.e("TAG", "Error reading users node", databaseError.toException());
                }
            });
        });




        DB = FirebaseDatabase.getInstance().getReference();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            DatabaseReference ProfileReference = DB.child("Profiles").child(account.getId());

            ProfileReference.child("uid").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    uidCur = task.getResult().getValue(Integer.class);
                    if (uidCur != null) {
                        testTxt.setText("Current User: " + String.valueOf(uidCur));
                    }
                } else {
                    Log.e("TAG", "Error getting UID", task.getException());
                }
            });
        }

    }

    private void sendFriendRequest(String senderUid, String receiverUid) {
        // Reference to the sender's node in the database
        DatabaseReference senderRef = DB.child("users").child(senderUid);

        // Reference to the receiver's node in the database
        DatabaseReference receiverRef = DB.child("users").child(receiverUid);

        // Create a node under sender's node to store current request
        senderRef.child("currentRequest").child(receiverUid).setValue(true);

        // Create a node under receiver's node to store friend request
        receiverRef.child("friendRequests").child(senderUid).setValue(true);

        // Inform user that friend request has been sent
        // You can add any additional UI feedback here
        Log.d("TAG", "Friend request sent successfully");
    }


}