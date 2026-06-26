package com.frc2135.android.frc_scout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity
{
    private static final String TAG = "SplashScreen";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.splash_screen_layout);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/

        int SPLASH_DISPLAY_LENGTH = 1475;   // Duration of wait
        new Handler().postDelayed(() -> {
            /* Create an Intent that will start the Menu-Activity. */
            Intent mainIntent = new Intent(Splash.this, MatchListActivity.class);
            Splash.this.startActivity(mainIntent);
            Splash.this.finish();
        }, SPLASH_DISPLAY_LENGTH);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Log.i(TAG, "Display resolution width = " + width + ", height = " + height);
    }
}
