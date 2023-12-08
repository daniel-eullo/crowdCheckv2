package com.example.customchu;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class userFeedback extends AppCompatActivity {

    ImageButton userFeedbackBack;
    TextView ratingBarOutput;
    RatingBar ratingBar;
    Float userRating;
    Button submitFeedback, userFBDialogProceed;
    EditText feedbackInput;
    String feedback;
    GoogleSignInAccount user;
    DatabaseReference databaseFacility, dbFeedback, idCheck;
    int feedbackCounter = 0;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);

        databaseFacility = FirebaseDatabase.getInstance().getReference();

        // fetch the logged in user
        user = GoogleSignIn.getLastSignedInAccount(this);

        userFeedbackBack = findViewById(R.id.userFeedbackBack);
        userFeedbackBack.setOnClickListener(view -> {
           Intent intent = new Intent(userFeedback.this, home.class);
           startActivity(intent);
           finish();
        });

        ratingBarOutput = findViewById(R.id.ratingBarOutput);

        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (v >= 0 && v < 1) {
                    ratingBarOutput.setText("Needs significant improvement!");
                    userRating = v;
                } else if (v >= 1 && v < 2) {
                    ratingBarOutput.setText("Needs more work");
                    userRating = v;
                } else if (v >= 2 && v < 3) {
                    ratingBarOutput.setText("Making progress, but still needs improvement");
                    userRating = v;
                } else if (v >= 3 && v < 4) {
                    ratingBarOutput.setText("Good effort, room for refinement");
                    userRating = v;
                } else if (v >= 4 && v < 5) {
                    ratingBarOutput.setText("Well done, but some minor adjustments could be made");
                    userRating = v;
                } else if (v >= 5 && v < 5.5) {
                    ratingBarOutput.setText("Excellent!");
                    userRating = v;
                }

            }
        });

        submitFeedback = findViewById(R.id.submitFeedback);
        feedbackInput = findViewById(R.id.feedbackInput);
        dbFeedback = databaseFacility.child("Feedback").child("feedbackTicket");

        //check current feedbackID
        idCheck = databaseFacility.child("Feedback");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                feedbackCounter = Integer.parseInt(dataSnapshot.child("feedbackID").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };
        idCheck.addValueEventListener(postListener);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());


        //testRating.setText(userRating.toString());
        submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dbFeedback.child("User1").push().setValue(userRating);
                //String feedbackid = "feedbackID" + feedbackCounter;
                feedback = feedbackInput.getText().toString();
                Map <String, Object> data = new HashMap<>();
                data.put("account_id", user.getId());
                data.put("username", user.getDisplayName());
                data.put("rating",userRating);
                data.put("userFeedback",feedback);
                data.put("date",date);
                dbFeedback.child(feedbackCounter + "").setValue(data);
                idCheck.child("feedbackID").setValue(feedbackCounter + 1);

                dialog.show();
            }
        });


        //navigation box
        dialog = new Dialog(userFeedback.this);
        dialog.setContentView(R.layout.dialogbox_userfeedback);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_qr_bg));
        dialog.setCancelable(false);

        userFBDialogProceed = dialog.findViewById(R.id.userFBDialogProceed);

        userFBDialogProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userFeedback.this, home.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
    }
}