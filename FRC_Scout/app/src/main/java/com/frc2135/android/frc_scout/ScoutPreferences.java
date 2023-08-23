package com.frc2135.android.frc_scout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

public class ScoutPreferences
{
    private static final String TAG = "ScoutPreferences";

    // Data members
    private boolean m_bNightMode;
    private final SharedPreferences m_sharedPreferences;

    private static ScoutPreferences sScoutPreferences;

    private ScoutPreferences(Activity activity)
    {
        m_bNightMode = true;  // default

        // Get from shared preferences           
        m_sharedPreferences = activity.getSharedPreferences("night", 0);
        boolean booleanValue = m_sharedPreferences.getBoolean("night_mode", true);
        if (booleanValue)
        {
            Log.d(TAG, "From shared preferences: dark mode");
            m_bNightMode = true;
        } else
        {
            Log.d(TAG, "From shared preferences: light mode");
            m_bNightMode = false;
        }
    }

    public static ScoutPreferences get(Activity activity)
    {
        if (sScoutPreferences == null)
        {
            Log.d(TAG, "Creating a new sScoutPreferences");
            sScoutPreferences = new ScoutPreferences(activity);
        }
        return sScoutPreferences;
    }

    public boolean getNightMode()
    {
        return m_bNightMode;
    }

    public void setNightMode(boolean bMode)
    {
        if (m_bNightMode != bMode)
        {
            m_bNightMode = bMode;

            // set in shared preferences
            SharedPreferences.Editor editor = m_sharedPreferences.edit();
            Log.d(TAG, "Setting shared preferences night_mode: " + bMode);
            editor.putBoolean("night_mode", bMode);
            editor.apply();
        } else
            Log.d(TAG, "Ignoring night_mode setting: " + bMode);
    }
}
