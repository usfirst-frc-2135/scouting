package com.frc2135.android.frc_scout;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for managing application preferences using {@link SharedPreferences}.
 * Manages the dark mode setting for the application and handles theme application.
 */
public class Preferences
{
    private static final String TAG = "Preferences";
    private static final String PREFS_NAME = "dark";
    private static final String KEY_DARK_MODE = "dark_mode";

    private boolean m_darkMode;
    private final SharedPreferences m_sharedPreferences;
    private final List<OnPreferenceChangeListener> m_listeners;

    private static volatile Preferences sPreferences;

    /**
     * Interface for listening to preference changes.
     */
    public interface OnPreferenceChangeListener
    {
        /**
         * Called when the dark mode preference changes.
         *
         * @param isDarkMode true if dark mode is now enabled, false otherwise
         */
        void onDarkModeChanged(boolean isDarkMode);
    }

    /**
     * Private constructor to initialize preferences from {@link SharedPreferences}.
     *
     * @param context the context used to access SharedPreferences
     */
    private Preferences(Context context)
    {
        // Use application context to avoid memory leaks
        m_sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        m_darkMode = m_sharedPreferences.getBoolean(KEY_DARK_MODE, true);
        m_listeners = new ArrayList<>();
        Log.d(TAG, "Initialized from shared preferences: dark mode = " + m_darkMode);
    }

    /**
     * Returns the singleton instance of Preferences.
     *
     * @param context the context used to initialize the instance if it doesn't exist
     * @return the singleton Preferences instance
     */
    public static Preferences get(Context context)
    {
        if (sPreferences == null)
        {
            synchronized (Preferences.class)
            {
                if (sPreferences == null)
                {
                    Log.d(TAG, "Creating new Preferences instance");
                    sPreferences = new Preferences(context);
                }
            }
        }
        return sPreferences;
    }

    /**
     * Checks whether dark mode is currently enabled.
     *
     * @return true if dark mode is enabled, false otherwise
     */
    public boolean isDarkMode()
    {
        return m_darkMode;
    }

    /**
     * Sets the dark mode preference, persists it, and applies the theme.
     *
     * @param isDarkMode true to enable dark mode, false to disable it
     */
    public void setDarkMode(boolean isDarkMode)
    {
        if (m_darkMode != isDarkMode)
        {
            m_darkMode = isDarkMode;

            Log.d(TAG, "Updating dark_mode in shared preferences: " + isDarkMode);
            m_sharedPreferences.edit()
                    .putBoolean(KEY_DARK_MODE, isDarkMode)
                    .apply();

            applyTheme();
            notifyListeners();
        }
        else
        {
            Log.d(TAG, "Ignoring redundant dark_mode setting: " + isDarkMode);
        }
    }

    /**
     * Applies the current dark mode preference to the application theme.
     */
    public void applyTheme()
    {
        int mode = m_darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        Log.d(TAG, "Applying theme: " + (m_darkMode ? "Night" : "Light"));
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /**
     * Registers a listener for preference changes.
     *
     * @param listener the listener to register
     */
    @SuppressWarnings("unused")
    public void addListener(OnPreferenceChangeListener listener)
    {
        if (listener != null && !m_listeners.contains(listener))
        {
            m_listeners.add(listener);
        }
    }

    /**
     * Unregisters a listener for preference changes.
     *
     * @param listener the listener to unregister
     */
    @SuppressWarnings("unused")
    public void removeListener(OnPreferenceChangeListener listener)
    {
        m_listeners.remove(listener);
    }

    private void notifyListeners()
    {
        for (OnPreferenceChangeListener listener : m_listeners)
        {
            listener.onDarkModeChanged(m_darkMode);
        }
    }
}
