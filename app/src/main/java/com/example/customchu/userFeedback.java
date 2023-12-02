package com.example.customchu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class userFeedback extends AppCompatActivity {

    ImageButton userFeedbackBack;
    TextView ratingBarOutput, testRating;
    RatingBar ratingBar;
    Float userRating;
    Button submitFeedback;
    EditText feedbackInput;
    String feedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);

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
        testRating = findViewById(R.id.testRating);
        feedbackInput = findViewById(R.id.feedbackInput);
        feedback = String.valueOf(feedbackInput.getText());
        //testRating.setText(userRating.toString());
        submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testRating.setText(feedbackInput.getText() + "");
                feedback = testRating.getText() + " ";
            }
        });

    }
}