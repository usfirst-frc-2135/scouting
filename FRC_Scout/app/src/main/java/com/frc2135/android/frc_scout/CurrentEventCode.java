package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Singleton class that maintains the currently active FRC event code information.
 * Information is persisted to and loaded from a JSON file.
 */
public class CurrentEventCode
{
    private static final String TAG = "CurrentEventCode";

    // JSON Keys
    private static final String KEY_EVENT_CODE = "eventCode";

    // Data members
    private String m_eventCode;

    private static volatile CurrentEventCode sCurrentEventCode;

    /**
     * Default constructor used when no saved event code is found.
     */
    public CurrentEventCode()
    {
        m_eventCode = "EVTX";
        Log.d(TAG, "Default constructor initialized: " + m_eventCode);
    }

    /**
     * Constructs a CurrentEventCode object from a {@link JSONObject}.
     *
     * @param json the source JSONObject
     */
    public CurrentEventCode(JSONObject json)
    {
        m_eventCode = json.optString(KEY_EVENT_CODE, "EVTX");
        Log.d(TAG, "Initialized from JSON: " + m_eventCode);
    }

    /**
     * Returns the singleton instance of CurrentEventCode.
     * Loads saved data from disk if it hasn't been initialized yet.
     *
     * @param context the application context for file operations
     * @return the singleton CurrentEventCode instance
     * @throws IOException   if reading the settings file fails
     * @throws JSONException if parsing the settings JSON fails
     */
    public static CurrentEventCode get(Context context)
            throws IOException, JSONException
    {
        if (sCurrentEventCode == null)
        {
            synchronized (CurrentEventCode.class)
            {
                if (sCurrentEventCode == null)
                {
                    Log.d(TAG, "Loading CurrentEventCode singleton");
                    EventMatchesSerializer serializer = new EventMatchesSerializer(context.getApplicationContext());
                    sCurrentEventCode = serializer.loadCurrentEventCode();

                    if (sCurrentEventCode == null)
                    {
                        Log.d(TAG, "No saved event codes found, using defaults");
                        sCurrentEventCode = new CurrentEventCode();
                    }
                }
            }
        }
        return sCurrentEventCode;
    }

    public void setEventCode(String eventCode)
    {
        m_eventCode = (eventCode != null) ? eventCode.trim() : "EVTX";
    }

    public String getEventCode()
    {
        return m_eventCode;
    }

    /**
     * Serializes the current event code data to a {@link JSONObject}.
     *
     * @return the serialized JSONObject
     * @throws JSONException if serialization fails
     */
    public JSONObject toJSON()
            throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(KEY_EVENT_CODE, m_eventCode);
        return json;
    }
}
