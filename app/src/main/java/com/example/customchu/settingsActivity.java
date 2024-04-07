package com.example.customchu;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Switch;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settingsActivity extends AppCompatActivity {
    private static final int LOW_DENSITY_NOTIFICATION_ID = 1;
    private static final int MEDIUM_DENSITY_NOTIFICATION_ID = 2;
    private static final int HIGH_DENSITY_NOTIFICATION_ID = 3;

    ImageButton notifBack;
    Switch enableSwitch;
    //Switch soundVibrateSwitch;
    SharedPreferences.Editor editor;

    CheckBox lowDensity, mediumDensity, highDensity, groundfloorchkb, secondfloorchkb;

    // SharedPreferences keys
    private static final String SWITCH_STATE_KEY = "enableSwitchState";
    private static final String SOUND_VIBRATE_SWITCH_KEY = "soundVibrateSwitchState";
    private static final String CHECKBOX_PREFIX = "checkbox_";

    // SharedPreferences
    SharedPreferences sharedPreferences;
    int libRoom1, libRoom2;

    DatabaseReference databaseFacility;

    private boolean lowDensityNotificationShown = false;
    private boolean mediumDensityNotificationShown = false;
    private boolean highDensityNotificationShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        notifBack = findViewById(R.id.notifBack);
        enableSwitch = findViewById(R.id.enableSwitch);
        lowDensity = findViewById(R.id.lowDensity);
        mediumDensity = findViewById(R.id.mediumDensity);
        highDensity = findViewById(R.id.highDensity);
        groundfloorchkb = findViewById(R.id.groundfloorchkb);
        secondfloorchkb = findViewById(R.id.secondfloorchkb);

        // Initialize SharedPreferences
        sharedPreferences = getPreferences(MODE_PRIVATE);

        // Set the initial state of checkboxes and switches based on the stored values
        lowDensity.setChecked(sharedPreferences.getBoolean(CHECKBOX_PREFIX + "lowDensity", false));
        mediumDensity.setChecked(sharedPreferences.getBoolean(CHECKBOX_PREFIX + "mediumDensity", false));
        highDensity.setChecked(sharedPreferences.getBoolean(CHECKBOX_PREFIX + "highDensity", false));
        groundfloorchkb.setChecked(sharedPreferences.getBoolean(CHECKBOX_PREFIX + "groundfloorchkb", false));
        secondfloorchkb.setChecked(sharedPreferences.getBoolean(CHECKBOX_PREFIX + "secondfloorchkb", false));

        enableSwitch.setChecked(sharedPreferences.getBoolean(SWITCH_STATE_KEY, false));
        //soundVibrateSwitch.setChecked(sharedPreferences.getBoolean(SOUND_VIBRATE_SWITCH_KEY, false));

        // Add listeners to the switches
        enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateCheckBoxesAvailability(isChecked);
            saveSwitchState(isChecked, SWITCH_STATE_KEY);

            if (!isChecked) {
                // Reset the state of checkboxes when the switch is turned off
                lowDensityNotificationShown = false;
                mediumDensityNotificationShown = false;
                highDensityNotificationShown = false;
                // Uncheck the checkboxes when the switch is turned off
                lowDensity.setChecked(false);
                mediumDensity.setChecked(false);
                highDensity.setChecked(false);
                groundfloorchkb.setChecked(false);
                secondfloorchkb.setChecked(false);
            } else {
                // Allow checking density checkboxes only if either groundfloorchkb or secondfloorchkb is checked
                if (!groundfloorchkb.isChecked() && !secondfloorchkb.isChecked()) {
                    lowDensity.setChecked(false);
                    mediumDensity.setChecked(false);
                    highDensity.setChecked(false);
                }

                // Update notifications when the switch is turned on
                createNotifications();
            }
        });

//        soundVibrateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            saveSwitchState(isChecked, SOUND_VIBRATE_SWITCH_KEY);
//        });

        notifBack.setOnClickListener(view -> {
            // Save the current state of checkboxes and switches
            saveCheckboxState(lowDensity, CHECKBOX_PREFIX + "lowDensity");
            saveCheckboxState(mediumDensity, CHECKBOX_PREFIX + "mediumDensity");
            saveCheckboxState(highDensity, CHECKBOX_PREFIX + "highDensity");
            saveCheckboxState(groundfloorchkb, CHECKBOX_PREFIX + "groundfloorchkb");
            saveCheckboxState(secondfloorchkb, CHECKBOX_PREFIX + "secondfloorchkb");
            saveSwitchState(enableSwitch.isChecked(), SWITCH_STATE_KEY);
            // saveSwitchState(soundVibrateSwitch.isChecked(), SOUND_VIBRATE_SWITCH_KEY);

            // Create notifications based on the current count if the switch is on
            if (enableSwitch.isChecked()) {
                createNotifications();
            }

            Intent intent = new Intent(settingsActivity.this, home.class);
            startActivity(intent);
        });

        // Initialize Firebase database reference
        databaseFacility = FirebaseDatabase.getInstance().getReference();

        // Call createNotifications when the activity is created
        if (enableSwitch.isChecked()) {
            createNotifications();
        }

        // Add listeners to groundfloorchkb and secondfloorchkb
        groundfloorchkb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateCheckBoxesAvailability(enableSwitch.isChecked());
            if (enableSwitch.isChecked()) {
                createNotifications();
            }
        });

        secondfloorchkb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateCheckBoxesAvailability(enableSwitch.isChecked());
            if (enableSwitch.isChecked()) {
                createNotifications();
            }
        });

        // Restore the state of checkboxes and switches when coming back to the activity
        if (enableSwitch.isChecked()) {
            updateCheckBoxesAvailability(true);
        } else {
            updateCheckBoxesAvailability(false);
        }
    }

    private void updateCheckBoxesAvailability(boolean isChecked) {
        boolean isGroundFloorChecked = groundfloorchkb.isChecked();
        boolean isSecondFloorChecked = secondfloorchkb.isChecked();

        boolean enableDensityCheckboxes = isChecked && (isGroundFloorChecked || isSecondFloorChecked);

        lowDensity.setEnabled(enableDensityCheckboxes);
        mediumDensity.setEnabled(enableDensityCheckboxes);
        highDensity.setEnabled(enableDensityCheckboxes);
        groundfloorchkb.setEnabled(isChecked);
        secondfloorchkb.setEnabled(isChecked);

        // If both groundfloorchkb and secondfloorchkb are unchecked, uncheck density checkboxes
        if (!isGroundFloorChecked && !isSecondFloorChecked) {
            lowDensity.setChecked(false);
            mediumDensity.setChecked(false);
            highDensity.setChecked(false);
        }
    }

    // Save the state of a checkbox in SharedPreferences
    private void saveCheckboxState(CheckBox checkBox, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, checkBox.isChecked());
        editor.apply();
    }

    // Save the state of the switches in SharedPreferences
    private void saveSwitchState(boolean isChecked, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

    private void getCurrentCount(String room, CountCallback callback) {
        DatabaseReference roomRef = databaseFacility.child("Rooms").child(room).child("Current");

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = Integer.parseInt(dataSnapshot.getValue() + "");
                callback.onCountReady(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    private void createNotifications() {
        if (!enableSwitch.isChecked()) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager, "lowChannel", "Low Channel");
            createNotificationChannel(notificationManager, "mediumChannel", "Medium Channel");
            createNotificationChannel(notificationManager, "highChannel", "High Channel");
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

        // Retrieve counts for both rooms
        getCurrentCount("GF", new CountCallback() {
            @Override
            public void onCountReady(int room1Count) {
                libRoom1 = room1Count;

                getCurrentCount("2F", new CountCallback() {
                    @Override
                    public void onCountReady(int room2Count) {
                        libRoom2 = room2Count;

                        // Reset flags
                        lowDensityNotificationShown = false;
                        mediumDensityNotificationShown = false;
                        highDensityNotificationShown = false;

                        // Check and show notifications for the first room (GF)
                        checkAndShowNotification(notificationManager, libRoom1, "Ground Floor");

                        // Check and show notifications for the second room (2F)
                        checkAndShowNotification(notificationManager, libRoom2, "Second Floor");

                        // Check and show notifications for groundfloorchkb
                        if (groundfloorchkb.isChecked()) {
                            checkAndShowNotification(notificationManager, libRoom1, "Ground Floor");
                        }

                        // Check and show notifications for secondfloorchkb
                        if (secondfloorchkb.isChecked()) {
                            checkAndShowNotification(notificationManager, libRoom2, "Second Floor");
                        }
                    }
                });
            }
        });
    }

    private void checkAndShowNotification(NotificationManager notificationManager, int roomCount, String floor) {
        boolean isGroundFloorChecked = groundfloorchkb.isChecked();
        boolean isSecondFloorChecked = secondfloorchkb.isChecked();

        if ((isGroundFloorChecked && floor.equals("Ground Floor")) || (isSecondFloorChecked && floor.equals("Second Floor"))) {
            if (floor.equals("Ground Floor")) {
                if (roomCount <= 20 && lowDensity.isChecked() && !lowDensityNotificationShown) {
                    Log.d("NotificationDebug", "Showing low density notification for " + floor);
                    showNotification(notificationManager, "lowChannel", LOW_DENSITY_NOTIFICATION_ID, floor, "Low crowd detected!");
                    lowDensityNotificationShown = true;
                } else if (roomCount > 20 && roomCount <= 35 && mediumDensity.isChecked() && !mediumDensityNotificationShown) {
                    Log.d("NotificationDebug", "Showing medium density notification for " + floor);
                    showNotification(notificationManager, "mediumChannel", MEDIUM_DENSITY_NOTIFICATION_ID, floor, "Medium crowd detected!");
                    mediumDensityNotificationShown = true;
                } else if (roomCount > 35 && roomCount <= 50 && highDensity.isChecked() && !highDensityNotificationShown) {
                    Log.d("NotificationDebug", "Showing high density notification for " + floor);
                    showNotification(notificationManager, "highChannel", HIGH_DENSITY_NOTIFICATION_ID, floor, "High crowd detected!");
                    highDensityNotificationShown = true;
                }
            } else if (floor.equals("Second Floor")) {
                // Adjust conditions for Second Floor as needed
                if (roomCount <= 20 && lowDensity.isChecked() && !lowDensityNotificationShown) {
                    Log.d("NotificationDebug", "Showing low density notification for " + floor);
                    showNotification(notificationManager, "lowChannel", LOW_DENSITY_NOTIFICATION_ID, floor, "Low crowd detected!");
                    lowDensityNotificationShown = true;
                } else if (roomCount > 20 && roomCount <= 35 && mediumDensity.isChecked() && !mediumDensityNotificationShown) {
                    Log.d("NotificationDebug", "Showing high density notification for " + floor);
                    showNotification(notificationManager, "mediumChannel", MEDIUM_DENSITY_NOTIFICATION_ID, floor, "Medium crowd detected!");
                    mediumDensityNotificationShown = true;
                } else if (roomCount > 35 && roomCount <= 50 && highDensity.isChecked() && !highDensityNotificationShown) {
                    Log.d("NotificationDebug", "Showing high density notification for " + floor);
                    showNotification(notificationManager, "highChannel", HIGH_DENSITY_NOTIFICATION_ID, floor, "High crowd detected!");
                    highDensityNotificationShown = true;
                }
            }
        }

        // Reset flags if the density condition is no longer met
        if (roomCount > 10) {
            lowDensityNotificationShown = false;
        }
        if (roomCount > 30) {
            mediumDensityNotificationShown = false;
        }
        if (roomCount > 50) {
            highDensityNotificationShown = false;
        }
    }

    private void showNotification(NotificationManager notificationManager, String channelId, int notificationId, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(notificationId, builder.build());
    }

    @SuppressLint("NewApi")
    private void createNotificationChannel(NotificationManager notificationManager, String channelId, String channelName) {
        if (notificationManager.getNotificationChannel(channelId) == null) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Callback interface to get the current count from Firebase
    private interface CountCallback {
        void onCountReady(int count);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup if needed
    }
}
