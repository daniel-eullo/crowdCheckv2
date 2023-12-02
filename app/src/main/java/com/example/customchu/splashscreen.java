package com.example.customchu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatDelegate;



public class splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // DARK MODE STATE GETTER
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        boolean nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        // DARK MODE STATE GETTER



        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splashscreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1500);
    }
}