package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Singleton class that maintains the currently active FRC competition information.
 * Information is persisted to and loaded from a JSON file.
 */
public class CurrentCompetition
{
    private static final String TAG = "CurrentCompetition";

    // JSON Keys
    private static final String KEY_EVENT_CODE = "eventCode";
    private static final String KEY_COMP_NAME = "compName";

    // Data members
    private String m_eventCode;
    private String m_compName;

    private static volatile CurrentCompetition sCurrentCompetition;

    /**
     * Default constructor used when no saved competition data is found.
     */
    public CurrentCompetition()
    {
        m_eventCode = "EVTX";
        m_compName = "COMPX";
        Log.d(TAG, "Default constructor initialized: " + m_eventCode + " (" + m_compName + ")");
    }

    /**
     * Constructs a CurrentCompetition object from a {@link JSONObject}.
     *
     * @param json the source JSONObject
     */
    public CurrentCompetition(JSONObject json)
    {
        m_eventCode = json.optString(KEY_EVENT_CODE, "EVTX");
        m_compName = json.optString(KEY_COMP_NAME, "COMPX");
        Log.d(TAG, "Initialized from JSON: " + m_eventCode + " (" + m_compName + ")");
    }

    /**
     * Returns the singleton instance of CurrentCompetition.
     * Loads saved data from disk if it hasn't been initialized yet.
     *
     * @param context the application context for file operations
     * @return the singleton CurrentCompetition instance
     * @throws IOException if reading the settings file fails
     * @throws JSONException if parsing the settings JSON fails
     */
    public static CurrentCompetition get(Context context) throws IOException, JSONException
    {
        if (sCurrentCompetition == null)
        {
            synchronized (CurrentCompetition.class)
            {
                if (sCurrentCompetition == null)
                {
                    Log.d(TAG, "Loading CurrentCompetition singleton");
                    CompetitionDataSerializer serializer = new CompetitionDataSerializer(context.getApplicationContext());
                    sCurrentCompetition = serializer.loadCurrentComp();
                    
                    if (sCurrentCompetition == null)
                    {
                        Log.d(TAG, "No saved competition found, using defaults");
                        sCurrentCompetition = new CurrentCompetition();
                    }
                }
            }
        }
        return sCurrentCompetition;
    }

    public void setEventCode(String eventCode)
    {
        m_eventCode = (eventCode != null) ? eventCode.trim() : "EVTX";
    }

    public String getEventCode()
    {
        return m_eventCode;
    }

    public void setCompName(String compName)
    {
        m_compName = (compName != null) ? compName.trim() : "COMPX";
    }

    /**
     * Serializes the current competition data to a {@link JSONObject}.
     *
     * @return the serialized JSONObject
     * @throws JSONException if serialization fails
     */
    public JSONObject toJSON() throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(KEY_COMP_NAME, m_compName);
        json.put(KEY_EVENT_CODE, m_eventCode);
        return json;
    }
}
