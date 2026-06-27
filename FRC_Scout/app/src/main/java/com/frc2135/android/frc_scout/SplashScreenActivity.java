package com.frc2135.android.frc_scout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity
{
    private static final String TAG = "SplashScreen";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        Log.i(TAG, "SplashActivity created.");
        super.onCreate(icicle);

        Preferences.get(this).applyTheme();

        setContentView(R.layout.splash_screen_layout);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/

        int SPLASH_DISPLAY_LENGTH = 1475;   // Duration of wait
        /* Create an Intent that will start the Menu-Activity. */
        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashScreenActivity.this, MatchListActivity.class);
            SplashScreenActivity.this.startActivity(mainIntent);
            SplashScreenActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);

        // Retrieve display resolution (for debugging on different displays)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Log.i(TAG, "Display resolution " + displayMetrics.widthPixels + " x " + displayMetrics.heightPixels);
    }
}
