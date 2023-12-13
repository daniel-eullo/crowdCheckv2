package com.example.customchu;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

    ImageButton notifBack;
    Switch enableSwitch;
    Switch soundVibrateSwitch;

    CheckBox lowDensity, mediumDensity, highDensity, groundfloorchkb, secondfloorchkb;

    // SharedPreferences keys
    private static final String SWITCH_STATE_KEY = "enableSwitchState";
    private static final String SOUND_VIBRATE_SWITCH_KEY = "soundVibrateSwitchState";

    // SharedPreferences
    SharedPreferences sharedPreferences;
    int libRoom1, libRoom2;

    DatabaseReference databaseFacility;

    private boolean lowDensityNotificationShown = false;
    private boolean mediumDensityNotificationShown = false;
    private boolean highDensityNotificationShown = false;

    // Broadcast receiver for density changes
    private BroadcastReceiver densityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check for density changes and update notifications
            int newDensity = getResources().getConfiguration().densityDpi;
            if (densityChanged(newDensity)) {
                createNotifications();
            }
        }
    };

    private boolean densityChanged(int newDensity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int oldDensity = preferences.getInt("density", -1);

        if (oldDensity != newDensity) {
            // Update the stored density value
            preferences.edit().putInt("density", newDensity).apply();
            return true;
        }

        return false;
    }


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
        groundfloorchkb = findViewById(R.id.groundfloorchkb);
        secondfloorchkb = findViewById(R.id.secondfloorchkb);

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set the initial state of checkboxes and switches based on the stored values
        lowDensity.setChecked(sharedPreferences.getBoolean("lowDensity", false));
        mediumDensity.setChecked(sharedPreferences.getBoolean("mediumDensity", false));
        highDensity.setChecked(sharedPreferences.getBoolean("highDensity", false));
        groundfloorchkb.setChecked(sharedPreferences.getBoolean("groundfloorchkb", false));
        secondfloorchkb.setChecked(sharedPreferences.getBoolean("secondfloorchkb", false));

        enableSwitch.setChecked(sharedPreferences.getBoolean(SWITCH_STATE_KEY, false));
        soundVibrateSwitch.setChecked(sharedPreferences.getBoolean(SOUND_VIBRATE_SWITCH_KEY, false));

        // Add listeners to the switches
        enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateCheckBoxesAvailability(isChecked);
            saveSwitchState(isChecked, SWITCH_STATE_KEY);

            if (!isChecked) {
                lowDensityNotificationShown = false;
                mediumDensityNotificationShown = false;
                highDensityNotificationShown = false;

                lowDensity.setChecked(false);
                mediumDensity.setChecked(false);
                highDensity.setChecked(false);
                groundfloorchkb.setChecked(false);
                secondfloorchkb.setChecked(false);
            } else {
                if (!groundfloorchkb.isChecked() && !secondfloorchkb.isChecked()) {
                    lowDensity.setChecked(false);
                    mediumDensity.setChecked(false);
                    highDensity.setChecked(false);
                }

                createNotifications();
            }
        });

        soundVibrateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(isChecked, SOUND_VIBRATE_SWITCH_KEY);
        });

        notifBack.setOnClickListener(view -> {
            saveCheckboxState(lowDensity, "lowDensity");
            saveCheckboxState(mediumDensity, "mediumDensity");
            saveCheckboxState(highDensity, "highDensity");
            saveCheckboxState(groundfloorchkb, "groundfloorchkb");
            saveCheckboxState(secondfloorchkb, "secondfloorchkb");
            saveSwitchState(enableSwitch.isChecked(), SWITCH_STATE_KEY);
            saveSwitchState(soundVibrateSwitch.isChecked(), SOUND_VIBRATE_SWITCH_KEY);

            if (enableSwitch.isChecked()) {
                createNotifications();
            }

            Intent intent = new Intent(notifActivity.this, home.class);
            startActivity(intent);
        });

        databaseFacility = FirebaseDatabase.getInstance().getReference();

        if (enableSwitch.isChecked()) {
            createNotifications();
        }

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

        if (enableSwitch.isChecked()) {
            updateCheckBoxesAvailability(true);
        } else {
            updateCheckBoxesAvailability(false);
        }

        // Register broadcast receiver for density changes
        registerReceiver(densityChangeReceiver, new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Handle configuration changes, e.g., density changes
        createNotifications();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(densityChangeReceiver);
        super.onDestroy();
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

        if (!isGroundFloorChecked && !isSecondFloorChecked) {
            lowDensity.setChecked(false);
            mediumDensity.setChecked(false);
            highDensity.setChecked(false);
        }
    }

    private void saveCheckboxState(CheckBox checkBox, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, checkBox.isChecked());
        editor.apply();
    }

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

        getCurrentCount("GF", new CountCallback() {
            @Override
            public void onCountReady(int room1Count) {
                libRoom1 = room1Count;

                getCurrentCount("2F", new CountCallback() {
                    @Override
                    public void onCountReady(int room2Count) {
                        libRoom2 = room2Count;

                        lowDensityNotificationShown = false;
                        mediumDensityNotificationShown = false;
                        highDensityNotificationShown = false;

                        checkAndShowNotification(notificationManager, libRoom1, "Ground Floor");

                        checkAndShowNotification(notificationManager, libRoom2, "Second Floor");

                        if (groundfloorchkb.isChecked()) {
                            checkAndShowNotification(notificationManager, libRoom1, "Ground Floor");
                        }

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
                .setSmallIcon(R.drawable.icons8_notification_90)
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

    private interface CountCallback {
        void onCountReady(int count);
    }
}
