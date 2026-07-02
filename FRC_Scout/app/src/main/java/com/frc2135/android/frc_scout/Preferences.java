package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to manage application preferences, specifically dark mode.
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

    public interface OnPreferenceChangeListener
    {
        /**
         * Called when the dark mode setting is changed.
         *
         * @param isEnabled true if dark mode is enabled, false otherwise
         */
        void onDarkModeChanged(boolean isEnabled);
    }

    private Preferences(Context context)
    {
        Log.d(TAG, "Preferences constructor");
        // Use application context to avoid memory leaks
        m_sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        m_darkMode = m_sharedPreferences.getBoolean(KEY_DARK_MODE, true);
        m_listeners = new ArrayList<>();
    }

    public static Preferences getInstance(Context context)
    {
        Log.d(TAG, "getInstance()");
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

    public boolean isDarkMode()
    {
        return m_darkMode;
    }

    public void setDarkMode(boolean isEnabled)
    {
        Log.d(TAG, "setDarkMode(): " + isEnabled);
        m_darkMode = isEnabled;
        m_sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isEnabled).apply();
        applyTheme();
        notifyListeners();
    }

    /**
     * Applies the dark mode setting to the entire application.
     */
    public void applyTheme()
    {
        Log.d(TAG, "applyTheme()");
        if (m_darkMode)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

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
