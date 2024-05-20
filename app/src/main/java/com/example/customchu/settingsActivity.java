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
    SharedPreferences.Editor editor;

    CheckBox lowDensity, mediumDensity, highDensity;

    private static final String SWITCH_STATE_KEY = "enableSwitchState";
    private static final String CHECKBOX_PREFIX = "checkbox_";

    SharedPreferences sharedPreferences;
    int libCurrent;
    long lowThreshold, mediumThreshold, highThreshold;

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

        sharedPreferences = getPreferences(MODE_PRIVATE);

        lowDensity.setChecked(sharedPreferences.getBoolean(CHECKBOX_PREFIX + "lowDensity", false));
        mediumDensity.setChecked(sharedPreferences.getBoolean(CHECKBOX_PREFIX + "mediumDensity", false));
        highDensity.setChecked(sharedPreferences.getBoolean(CHECKBOX_PREFIX + "highDensity", false));

        enableSwitch.setChecked(sharedPreferences.getBoolean(SWITCH_STATE_KEY, false));

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
            } else {
                createNotifications();
            }
        });

        notifBack.setOnClickListener(view -> {
            saveCheckboxState(lowDensity, CHECKBOX_PREFIX + "lowDensity");
            saveCheckboxState(mediumDensity, CHECKBOX_PREFIX + "mediumDensity");
            saveCheckboxState(highDensity, CHECKBOX_PREFIX + "highDensity");
            saveSwitchState(enableSwitch.isChecked(), SWITCH_STATE_KEY);

            if (enableSwitch.isChecked()) {
                createNotifications();
            }

            Intent intent = new Intent(settingsActivity.this, home.class);
            startActivity(intent);
        });

        databaseFacility = FirebaseDatabase.getInstance().getReference().child("Library");

        databaseFacility.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    lowThreshold = dataSnapshot.child("Low").getValue(Long.class);
                    mediumThreshold = dataSnapshot.child("Medium").getValue(Long.class);
                    highThreshold = dataSnapshot.child("High").getValue(Long.class);
                    libCurrent = dataSnapshot.child("Current").getValue(Integer.class);

                    Log.d("FirebaseData", "Low: " + lowThreshold + ", Medium: " + mediumThreshold + ", High: " + highThreshold + ", Current: " + libCurrent);

                    if (enableSwitch.isChecked()) {
                        createNotifications();
                    }
                } else {
                    Log.d("FirebaseData", "No data found at Library node.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("FirebaseData", "loadLibrary:onCancelled", databaseError.toException());
            }
        });

        if (enableSwitch.isChecked()) {
            createNotifications();
        }
    }

    private void updateCheckBoxesAvailability(boolean isChecked) {
        lowDensity.setEnabled(isChecked);
        mediumDensity.setEnabled(isChecked);
        highDensity.setEnabled(isChecked);

        if (!isChecked) {
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

        int roomCount = libCurrent;

        checkAndShowNotification(notificationManager, roomCount);
    }

    private void checkAndShowNotification(NotificationManager notificationManager, int roomCount) {
        if (roomCount <= lowThreshold && lowDensity.isChecked() && !lowDensityNotificationShown) {
            Log.d("NotificationDebug", "Showing low density notification");
            showNotification(notificationManager, "lowChannel", LOW_DENSITY_NOTIFICATION_ID, "Library", "Low crowd detected!");
            lowDensityNotificationShown = true;
        } else if (roomCount > lowThreshold && roomCount <= mediumThreshold && mediumDensity.isChecked() && !mediumDensityNotificationShown) {
            Log.d("NotificationDebug", "Showing medium density notification");
            showNotification(notificationManager, "mediumChannel", MEDIUM_DENSITY_NOTIFICATION_ID, "Library", "Medium crowd detected!");
            mediumDensityNotificationShown = true;
        } else if (roomCount > mediumThreshold && roomCount <= highThreshold && highDensity.isChecked() && !highDensityNotificationShown) {
            Log.d("NotificationDebug", "Showing high density notification");
            showNotification(notificationManager, "highChannel", HIGH_DENSITY_NOTIFICATION_ID, "Library", "High crowd detected!");
            highDensityNotificationShown = true;
        }

        if (roomCount > lowThreshold) {
            lowDensityNotificationShown = false;
        }
        if (roomCount > mediumThreshold) {
            mediumDensityNotificationShown = false;
        }
        if (roomCount > highThreshold) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
