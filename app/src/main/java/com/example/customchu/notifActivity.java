package com.example.customchu;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class notifActivity extends AppCompatActivity {
    private static final int LOW_DENSITY_NOTIFICATION_ID = 1;
    private static final int MEDIUM_DENSITY_NOTIFICATION_ID = 2;
    private static final int HIGH_DENSITY_NOTIFICATION_ID = 3;
    private static final int SAME_DENSITY_NOTIFICATION_ID = 4;

    ImageButton notifBack;
    Switch enableSwitch;
    Switch soundVibrateSwitch;

    CheckBox lowDensity, mediumDensity, highDensity;

    // SharedPreferences keys
    private static final String SWITCH_STATE_KEY = "enableSwitchState";
    private static final String SOUND_VIBRATE_SWITCH_KEY = "soundVibrateSwitchState";

    // SharedPreferences
    SharedPreferences sharedPreferences;
    int libRoom1, libRoom2;

    DatabaseReference databaseFacility;

    private final Handler floatingNotificationHandler = new Handler();
    private static final int FLOATING_NOTIFICATION_DELAY = 3000;
    private final Handler handler = new Handler();
    private static final long NOTIFICATION_CHECK_INTERVAL = 3000; // 3secs

    private boolean lowDensityNotificationShown = false;
    private boolean mediumDensityNotificationShown = false;
    private boolean highDensityNotificationShown = false;
    private View floatingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);

        notifBack = findViewById(R.id.notifBack);
        enableSwitch = findViewById(R.id.enableswitch);
        soundVibrateSwitch = findViewById(R.id.defaultswitch);
        lowDensity = findViewById(R.id.lowDensity);
        mediumDensity = findViewById(R.id.mediumDensity);
        highDensity = findViewById(R.id.highDensity);

        // Initialize SharedPreferences
        sharedPreferences = getPreferences(MODE_PRIVATE);

        // Set the initial state of checkboxes and switches based on the stored values
        lowDensity.setChecked(sharedPreferences.getBoolean("lowDensity", false));
        mediumDensity.setChecked(sharedPreferences.getBoolean("mediumDensity", false));
        highDensity.setChecked(sharedPreferences.getBoolean("highDensity", false));
        enableSwitch.setChecked(sharedPreferences.getBoolean(SWITCH_STATE_KEY, false));
        soundVibrateSwitch.setChecked(sharedPreferences.getBoolean(SOUND_VIBRATE_SWITCH_KEY, false));

        // Add listeners to the switches
        enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateCheckBoxesAvailability(isChecked);
            saveSwitchState(isChecked, SWITCH_STATE_KEY);

            if (isChecked) {
                createNotifications();
            }
        });

        soundVibrateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(isChecked, SOUND_VIBRATE_SWITCH_KEY);
        });

        notifBack.setOnClickListener(view -> {
            // Save the current state of checkboxes and switches
            saveCheckboxState(lowDensity, "lowDensity");
            saveCheckboxState(mediumDensity, "mediumDensity");
            saveCheckboxState(highDensity, "highDensity");
            saveSwitchState(enableSwitch.isChecked(), SWITCH_STATE_KEY);
            saveSwitchState(soundVibrateSwitch.isChecked(), SOUND_VIBRATE_SWITCH_KEY);

            // Create notifications based on the current count if the switch is on
            if (enableSwitch.isChecked()) {
                createNotifications();
            }

            Intent intent = new Intent(notifActivity.this, home.class);
            startActivity(intent);
        });

        // Initialize Firebase database reference
        databaseFacility = FirebaseDatabase.getInstance().getReference();

        // Call createNotifications when the activity is created
        if (enableSwitch.isChecked()) {
            createNotifications();
        }

        // Schedule periodic notification checks
        handler.postDelayed(notificationCheckRunnable, NOTIFICATION_CHECK_INTERVAL);
    }

    private void updateCheckBoxesAvailability(boolean isChecked) {
        lowDensity.setEnabled(isChecked);
        mediumDensity.setEnabled(isChecked);
        highDensity.setEnabled(isChecked);
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

                        // Log statements to help diagnose the issue
                        Log.d("NotificationDebug", "libRoom2: " + libRoom2);
                        Log.d("NotificationDebug", "lowDensity: " + lowDensity.isChecked());
                        Log.d("NotificationDebug", "mediumDensity: " + mediumDensity.isChecked());
                        Log.d("NotificationDebug", "highDensity: " + highDensity.isChecked());

                        // Check and show notifications for the second room (2F)
                        checkAndShowNotification(notificationManager, libRoom2, "Second Floor");
                    }
                });
            }
        });
    }

    private void checkAndShowNotification(NotificationManager notificationManager, int roomCount, String floor) {
        int lowDensityThreshold = 10;
        int mediumDensityThreshold = 30;
        int highDensityThreshold = 50;

        boolean isLowDensity = roomCount <= lowDensityThreshold;
        boolean isMediumDensity = roomCount <= mediumDensityThreshold;
        boolean isHighDensity = roomCount <= highDensityThreshold;

        boolean showLowDensityNotification = isLowDensity && lowDensity.isChecked();
        boolean showMediumDensityNotification = isMediumDensity && mediumDensity.isChecked();
        boolean showHighDensityNotification = isHighDensity && highDensity.isChecked();

        if (showLowDensityNotification || showMediumDensityNotification || showHighDensityNotification) {
            // Check if the densities are the same for both floors
            boolean isSameDensity = libRoom1 <= lowDensityThreshold && libRoom2 <= lowDensityThreshold;

            // Choose the appropriate channel and notification ID based on the density
            String channel;
            int notificationId;

            if (isSameDensity) {
                channel = "sameDensityChannel";
                notificationId = SAME_DENSITY_NOTIFICATION_ID;
            } else if (showLowDensityNotification) {
                channel = "lowChannel";
                notificationId = LOW_DENSITY_NOTIFICATION_ID;
            } else if (showMediumDensityNotification) {
                channel = "mediumChannel";
                notificationId = MEDIUM_DENSITY_NOTIFICATION_ID;
            } else {
                channel = "highChannel";
                notificationId = HIGH_DENSITY_NOTIFICATION_ID;
            }

            Log.d("NotificationDebug", "Showing density notification for " + floor);
            showNotification(notificationManager, channel, notificationId, floor, getDensityMessage(roomCount));
            FloatingNotification.show(this, getDensityMessage(roomCount) + " (" + floor + ")");
        }
    }

    private String getDensityMessage(int roomCount) {
        if (roomCount <= 10) {
            return "Low crowd detected!";
        } else if (roomCount <= 30) {
            return "Medium crowd detected!";
        } else {
            return "High crowd detected!";
        }
    }
    private void showNotification(NotificationManager notificationManager, String channelId, int notificationId, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notificon)
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

    // Runnable for periodic notification checks
    private final Runnable notificationCheckRunnable = new Runnable() {
        @Override
        public void run() {
            // Perform notification checks
            createNotifications();

            // Schedule the next notification check
            handler.postDelayed(this, NOTIFICATION_CHECK_INTERVAL);
        }
    };


    // Show Floating Notification
    private void showFloatingNotification(String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // If the user has not granted the permission, request it.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1234);
        } else {
            // Display the floating notification
            FloatingNotification.show(this, message);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove callbacks to prevent memory leaks
        handler.removeCallbacks(notificationCheckRunnable);
        floatingNotificationHandler.removeCallbacksAndMessages(null);
    }
}
