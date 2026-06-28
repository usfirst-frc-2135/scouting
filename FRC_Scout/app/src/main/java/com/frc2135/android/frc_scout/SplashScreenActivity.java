package com.frc2135.android.frc_scout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowMetrics;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Entry point of the application. Displays a splash screen for a short duration
 * while initializing the app's theme and then transitions to {@link MatchListActivity}.
 */
@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity
{
    private static final String TAG = "SplashScreen";
    private static final int SPLASH_DISPLAY_LENGTH = 1475; // Duration of wait in milliseconds

    /**
     * Called when the activity is first created.
     * Initializes the theme and schedules the transition to the main activity.
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        // Apply theme preference before super.onCreate to ensure the correct theme is applied early
        Preferences.get(this).applyTheme();

        super.onCreate(icicle);
        Log.i(TAG, "SplashScreenActivity created.");

        setContentView(R.layout.splash_screen_layout);

        // Schedule transition to MatchListActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent mainIntent = new Intent(SplashScreenActivity.this, MatchListActivity.class);
            startActivity(mainIntent);
            finish();
        }, SPLASH_DISPLAY_LENGTH);

        logDisplayResolution();
    }

    /**
     * Retrieves and logs display resolution for debugging on different device screens.
     * Uses modern WindowMetrics for API 30+ and legacy DisplayMetrics for older versions.
     */
    private void logDisplayResolution()
    {
        int width;
        int height;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
            width = windowMetrics.getBounds().width();
            height = windowMetrics.getBounds().height();
        }
        else
        {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
        }
        Log.i(TAG, "Display resolution: " + width + " x " + height);
    }
}
