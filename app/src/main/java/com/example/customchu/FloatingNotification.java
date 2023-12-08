package com.example.customchu;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class FloatingNotification {

    private static WindowManager windowManager;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final long NOTIFICATION_DURATION = 3000; // 3 seconds

    private static View floatingView; // Keep track of the current floating view

    public static void show(Context context, String message) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        floatingView = inflater.inflate(R.layout.floating_notification, null);

        ImageView iconView = floatingView.findViewById(R.id.notificationIcon);
        iconView.setImageResource(R.drawable.notificon);

        TextView textView = floatingView.findViewById(R.id.notificationText);
        textView.setText(message);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1000, // width
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.x = 0; // Adjust the horizontal position as needed
        params.y = 32; // Adjust the vertical position as needed

        windowManager.addView(floatingView, params);

        // Schedule the notification to disappear after a certain duration
        handler.postDelayed(() -> hide(), NOTIFICATION_DURATION);
    }

    public static void hide() {
        if (floatingView != null && windowManager != null) {
            windowManager.removeView(floatingView);
            floatingView = null; // Reset the floating view reference
        }
    }
}
