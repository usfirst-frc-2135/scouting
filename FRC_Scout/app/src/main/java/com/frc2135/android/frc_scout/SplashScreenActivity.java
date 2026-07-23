/*
 * Copyright (c) 2025 FRC 2135 Presentation Invasion
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
 * Entry point of the application. Displays a splash screen for a short duration while performing initialization tasks.
 * <p>
 * Initializes the application's theme, displays current configuration metadata, and transitions to {@link MatchListActivity}.
 */
@SuppressWarnings("ALL")
@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity
{
    private static final String TAG = "SplashScreenActivity";

    /**
     * The duration for which the splash screen metadata remains visible before transitioning.
     */
    private static final int SPLASH_DISPLAY_LENGTH = 1500;

    private SplashScreenActivityBinding m_binding;
    private Settings m_settings;

    /**
     * Called when the activity is first created.
     * Applies the user's theme preference, inflates the splash layout, and starts the transition sequence.
     *
     * @param icicle if the activity is being re-initialized after previously being shut down
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        Log.v(TAG, "onCreate");
        // Apply theme preference before super.onCreate to ensure the correct theme is applied early
        Preferences.getInstance(this).applyTheme();
        super.onCreate(icicle);

        m_settings = Settings.getInstance(this);
        m_binding = SplashScreenActivityBinding.inflate(getLayoutInflater());
        setContentView(m_binding.getRoot());

        populateSettingsSummary();

        // Simple fade-in animation
        m_binding.splashActivityContainer.setAlpha(0f);
        m_binding.splashActivityContainer.animate()
                .alpha(1f)
                .setDuration(500)
                .withEndAction(this::startMainTransition)
                .start();

        logDisplayResolution();
    }

    /**
     * Populates the settings summary text view with the current FRC event code, team index selection, and most recent scout name.
     */
    private void populateSettingsSummary()
    {
        String eventCode = m_settings.getEventCode();
        String teamIndex = m_settings.getTeamIndexStr();
        String scoutName = m_settings.getMostRecentScoutName();

        String eventCodeDisplay = eventCode.isEmpty() ? "None" : eventCode;
        String scoutNameDisplay = scoutName.isEmpty() ? "None" : scoutName;

        String summary = "Event: " + eventCodeDisplay + "\n" +
                "Index: " + teamIndex + "\n" +
                "Scout: " + scoutNameDisplay;

        m_binding.splashSettingsSummaryText.setText(summary);
    }

    /**
     * Schedules the transition to the {@link MatchListActivity} after a pre-defined delay.
     */
    @SuppressWarnings("deprecation")
    private void startMainTransition()
    {
        Log.d(TAG, "startMainTransition");
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent mainIntent = new Intent(SplashScreenActivity.this, MatchListActivity.class);
            startActivity(mainIntent);

            // overridePendingTransition is deprecated in API 34.
            // Android for Kindle is built on Android 9 (API 28)
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
     * Retrieves and logs the current device's display resolution and density for diagnostic purposes.
     * Uses modern WindowMetrics for API 30+ and legacy DisplayMetrics for older versions.
     */
    @SuppressWarnings("deprecation")
    private void logDisplayResolution()
    {
        int width;
        int height;
        int dpi;

        // Android for Kindle is built on Android 9 (API 28)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            Log.i(TAG, "Using newer API > " + Build.VERSION_CODES.R);
            WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
            width = windowMetrics.getBounds().width();
            height = windowMetrics.getBounds().height();
            dpi = getResources().getConfiguration().densityDpi;
        }
        else
        {
            Log.i(TAG, "Using older API < " + Build.VERSION_CODES.R);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
            dpi = displayMetrics.densityDpi;
        }
        Log.i(TAG, "Display resolution: " + width + " x " + height + " @ " + dpi + "dpi");
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    /**
     * Perform any final cleanup before the activity is destroyed.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        m_binding = null;
    }

}
