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

import com.frc2135.android.frc_scout.databinding.SplashScreenActivityBinding;

/**
 * Entry point of the application. Displays a splash screen for a short duration
 * while initializing the app's theme and then transitions to {@link MatchListActivity}.
 */
@SuppressWarnings("ALL")
@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity
{
    private static final String TAG = "SplashScreenActivity";
    private static final int SPLASH_DISPLAY_LENGTH = 1500; // Duration of pause in milliseconds
    private SplashScreenActivityBinding m_binding;

    /**
     * Called when the activity is first created.
     * Initializes the theme and schedules the transition to the main activity.
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        Log.d(TAG, "onCreate");
        // Apply theme preference before super.onCreate to ensure the correct theme is applied early
        Preferences.getInstance(this).applyTheme();
        super.onCreate(icicle);

        m_binding = SplashScreenActivityBinding.inflate(getLayoutInflater());
        setContentView(m_binding.getRoot());

        populateSettingsSummary();

        // Simple fade-in animation
        m_binding.splashContainer.setAlpha(0f);
        m_binding.splashContainer.animate()
                .alpha(1f)
                .setDuration(500)
                .withEndAction(this::startMainTransition)
                .start();

        logDisplayResolution();
    }

    /**
     * Populates the settings summary text view with the current event code, team index, and most recent scout name.
     */
    private void populateSettingsSummary()
    {
        Settings settings = Settings.getInstance(this);
        String eventCode = settings.getEventCode();
        String teamIndex = settings.getTeamIndexStr();
        String scoutName = settings.getMostRecentScoutName();

        if (eventCode.isEmpty())
        {
            eventCode = "None";
        }
        if (scoutName.isEmpty())
        {
            scoutName = "None";
        }

        String summary = "Event: " + eventCode + "\n" +
                "Index: " + teamIndex + "\n" +
                "Scout: " + scoutName;

        m_binding.settingsSummary.setText(summary);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        m_binding = null;
    }

    /**
     * Schedules transition to MatchListActivity after the pause duration.
     */
    @SuppressWarnings("deprecation")
    private void startMainTransition()
    {
        Log.d(TAG, "startMainTransition()");
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent mainIntent = new Intent(SplashScreenActivity.this, MatchListActivity.class);
            startActivity(mainIntent);

            // overridePendingTransition is deprecated in API 34.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.fade_in, R.anim.fade_out);
            }
            else
            {
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }

    /**
     * Retrieves and logs display resolution for debugging on different device screens.
     * Uses modern WindowMetrics for API 30+ and legacy DisplayMetrics for older versions.
     */
    @SuppressWarnings("deprecation")
    private void logDisplayResolution()
    {
        int width;
        int height;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            Log.d(TAG, "Using newer API > " + Build.VERSION_CODES.R);
            WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
            width = windowMetrics.getBounds().width();
            height = windowMetrics.getBounds().height();
        }
        else
        {
            Log.d(TAG, "Using older API < " + Build.VERSION_CODES.R);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
        }
        Log.i(TAG, "Display resolution: " + width + " x " + height);
    }
}
