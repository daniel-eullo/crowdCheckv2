package com.example.customchu;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

public class DensityChangeService extends IntentService {

    public DensityChangeService() {
        super("DensityChangeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (true) {
            try {
                Thread.sleep(2000); // Sleep for 1 second, adjust as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Configuration configuration = getResources().getConfiguration();
            int densityDpi = configuration.densityDpi;

            // Retrieve room count and density check states
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            int roomCount = preferences.getInt("roomCount", -1);
            boolean lowDensityChecked = preferences.getBoolean("lowDensity", false);
            boolean mediumDensityChecked = preferences.getBoolean("mediumDensity", false);
            boolean highDensityChecked = preferences.getBoolean("highDensity", false);

            // Check for density changes and trigger notifications if needed
            if (densityChanged(densityDpi, roomCount, lowDensityChecked, mediumDensityChecked, highDensityChecked)) {
                sendBroadcast(new Intent("android.intent.action.CONFIGURATION_CHANGED"));
            }
        }
    }

    private boolean densityChanged(int newDensity, int roomCount, boolean lowDensityChecked, boolean mediumDensityChecked, boolean highDensityChecked) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int oldDensity = preferences.getInt("density", -1);
        int oldRoomCount = preferences.getInt("roomCount", -1);

        if (oldDensity != newDensity || oldRoomCount != roomCount) {
            // Update the stored density and room count values
            preferences.edit().putInt("density", newDensity).apply();
            preferences.edit().putInt("roomCount", roomCount).apply();

            // Add your logic to check other conditions and trigger notifications, if needed
            if (roomCount <= 20 && lowDensityChecked) {
                // Handle low density notification
                return true;
            } else if (roomCount > 20 && roomCount <= 35 && mediumDensityChecked) {
                // Handle medium density notification
                return true;
            } else if (roomCount > 35 && roomCount <= 50 && highDensityChecked) {
                // Handle high density notification
                return true;
            }

            // If no specific conditions are met, return false
            return false;
        }

        return false;
    }
}
