package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to manage application-wide user preferences, specifically dark mode.
 * Persists preferences using {@link android.content.SharedPreferences} and notifies listeners of changes.
 */
public class Preferences
{
    private static final String TAG = "Preferences";
    private static final String PREFS_NAME = "dark";
    private static final String KEY_DARK_MODE = "dark_mode";

    private boolean m_darkMode;
    private final android.content.SharedPreferences m_sharedPreferences;
    private final List<OnPreferenceChangeListener> m_listeners;

    private static volatile Preferences sPreferences;

    /**
     * Interface definition for a callback to be invoked when a preference is changed.
     */
    public interface OnPreferenceChangeListener
    {
        /**
         * Called when the dark mode setting is changed.
         *
         * @param isEnabled true if dark mode is now enabled, false otherwise
         */
        void onDarkModeChanged(boolean isEnabled);
    }

    /**
     * Initializes the preferences from storage.
     *
     * @param context the context used to retrieve shared preferences
     */
    private Preferences(Context context)
    {
        Log.v(TAG, "Preferences constructor");
        // Use application context to avoid memory leaks
        m_sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        m_darkMode = m_sharedPreferences.getBoolean(KEY_DARK_MODE, true);
        m_listeners = new ArrayList<>();
    }

    /**
     * Returns the thread-safe singleton instance of Preferences.
     *
     * @param context the context used to initialize the instance
     * @return the singleton Preferences instance
     */
    public static Preferences getInstance(Context context)
    {
        Log.v(TAG, "getInstance");
        if (sPreferences == null)
        {
            synchronized (Preferences.class)
            {
                if (sPreferences == null)
                {
                    sPreferences = new Preferences(context);
                }
            }
        }
        return sPreferences;
    }

    /**
     * Checks whether dark mode is currently enabled in the application settings.
     *
     * @return true if dark mode is enabled
     */
    public boolean isDarkMode()
    {
        return m_darkMode;
    }

    /**
     * Sets the dark mode preference, persists it to storage, applies the theme, and notifies all registered listeners.
     *
     * @param isEnabled true to enable dark mode, false for light mode
     */
    public void setDarkMode(boolean isEnabled)
    {
        Log.i(TAG, "setDarkMode: " + isEnabled);
        m_darkMode = isEnabled;
        m_sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isEnabled).apply();
        applyTheme();
        notifyListeners();
    }

    /**
     * Applies the current dark mode setting to the entire application using {@link AppCompatDelegate}.
     */
    public void applyTheme()
    {
        Log.v(TAG, "applyTheme");
        if (m_darkMode)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Registers a listener to be notified when preferences change.
     *
     * @param listener the listener to add
     */
    @SuppressWarnings("unused")
    public void addListener(OnPreferenceChangeListener listener)
    {
        synchronized (m_listeners)
        {
            if (listener != null && !m_listeners.contains(listener))
            {
                m_listeners.add(listener);
            }
        }
    }

    /**
     * Unregisters a previously registered listener.
     *
     * @param listener the listener to remove
     */
    @SuppressWarnings("unused")
    public void removeListener(OnPreferenceChangeListener listener)
    {
        synchronized (m_listeners)
        {
            if (listener != null)
            {
                m_listeners.remove(listener);
            }
        }
    }

    /**
     * Notifies all registered listeners that a preference has changed.
     */
    private void notifyListeners()
    {
        List<OnPreferenceChangeListener> listenersCopy;
        synchronized (m_listeners)
        {
            listenersCopy = new ArrayList<>(m_listeners);
        }
        for (OnPreferenceChangeListener listener : listenersCopy)
        {
            listener.onDarkModeChanged(m_darkMode);
        }
    }
}
