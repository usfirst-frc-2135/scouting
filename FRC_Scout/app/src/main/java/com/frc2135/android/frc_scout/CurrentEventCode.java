package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Singleton class for managing the current event code.
 */
public class CurrentEventCode
{
    private static final String TAG = "CurrentEventCode";
    private static final String KEY_EVENT_CODE = "eventCode";

    private String m_eventCode;

    private static volatile CurrentEventCode sCurrentEventCode;

    private CurrentEventCode()
    {
        Log.d(TAG, "CurrentEventCode constructor");
        m_eventCode = "";
    }

    public CurrentEventCode(JSONObject json)
            throws JSONException
    {
        Log.d(TAG, "CurrentEventCode constructor from JSON");
        m_eventCode = json.getString(KEY_EVENT_CODE);
    }

    /**
     * Returns the singleton instance of CurrentEventCode.
     * Loads from disk if not already loaded.
     *
     * @param context the context used for file operations
     * @return the singleton CurrentEventCode instance
     * @throws IOException   if reading the file fails
     * @throws JSONException if parsing the JSON fails
     */
    public static CurrentEventCode get(Context context)
            throws IOException, JSONException
    {
        Log.d(TAG, "get()");
        if (sCurrentEventCode == null)
        {
            synchronized (CurrentEventCode.class)
            {
                if (sCurrentEventCode == null)
                {
                    EventMatchesSerializer serializer = new EventMatchesSerializer(context.getApplicationContext());
                    sCurrentEventCode = serializer.loadCurrentEventCode();
                    if (sCurrentEventCode == null)
                    {
                        sCurrentEventCode = new CurrentEventCode();
                    }
                }
            }
        }
        return sCurrentEventCode;
    }

    public void setEventCode(String code)
    {
        m_eventCode = code;
    }

    public String getEventCode()
    {
        return m_eventCode;
    }

    /**
     * Serializes the current event code to a JSONObject.
     *
     * @return the serialized JSONObject
     * @throws JSONException if JSON creation fails
     */
    public JSONObject toJSON()
            throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(KEY_EVENT_CODE, m_eventCode);
        return json;
    }
}
