package com.frc2135.android.frc_scout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
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
    private static final int SPLASH_DISPLAY_LENGTH = 1500; // Duration of pause in milliseconds

    /**
     * Called when the activity is first created.
     * Initializes the theme and schedules the transition to the main activity.
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        Log.d(TAG, "onCreate");
        // Apply theme preference before super.onCreate to ensure the correct theme is applied early
        Preferences.get(this).applyTheme();

        super.onCreate(icicle);
        Log.i(TAG, "SplashScreenActivity created.");

        setContentView(R.layout.splash_screen_activity);

        // Simple fade-in animation
        View container = findViewById(R.id.splash_container);
        if (container != null)
        {
            container.setAlpha(0f);
            container.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .withEndAction(() -> startMainTransition())
                    .start();
        }
        else
        {
            // Fallback: transition immediately if container is missing
            startMainTransition();
        }

        logDisplayResolution();
    }

    /**
     * Schedules transition to MatchListActivity after the pause duration.
     */
    private void startMainTransition()
    {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent mainIntent = new Intent(SplashScreenActivity.this, MatchListActivity.class);
            startActivity(mainIntent);
            // Use fade-out transition
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, SPLASH_DISPLAY_LENGTH);
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
