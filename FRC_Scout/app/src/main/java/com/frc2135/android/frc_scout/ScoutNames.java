package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for managing scout names data.
 * Loads and parses scout names from a JSON file.
 */
public class ScoutNames
{
    private static final String TAG = "ScoutNames";

    private String m_eventCode;
    private List<String> m_scoutNames;
    private boolean m_bScoutNamesLoaded;
    private final ScoutNamesSerializer m_serializer;

    private static volatile ScoutNames sScoutNames;

    private ScoutNames(Context context, String eventCode)
    {
        m_eventCode = eventCode;
        m_bScoutNamesLoaded = false;
        m_scoutNames = new ArrayList<>();
        m_serializer = new ScoutNamesSerializer(context);
    }

    /**
     * Returns the singleton instance of ScoutNames.
     *
     * @param context      the context used for file operations
     * @param eventCode    the FRC event code
     * @param bForceReload whether to force a reload of the JSON data
     * @return the singleton ScoutNames instance
     */
    public static ScoutNames get(Context context, String eventCode, boolean bForceReload)
    {
        synchronized (ScoutNames.class)
        {
            if (sScoutNames == null)
            {
                Log.d(TAG, "Creating a new sScoutNames for eventCode " + eventCode);
                sScoutNames = new ScoutNames(context, eventCode);
                sScoutNames.readScoutNamesJSON(context, true);
            }
            else
            {
                String oldEventCode = sScoutNames.getEventCode();
                if (bForceReload || !oldEventCode.equalsIgnoreCase(eventCode))
                {
                    Log.d(TAG, "Resetting ScoutNames: " + oldEventCode + " -> " + eventCode);
                    sScoutNames.setEventCode(eventCode);
                    sScoutNames.readScoutNamesJSON(context, true);
                }
            }
            return sScoutNames;
        }
    }

    @SuppressWarnings("unused")
    public static void clear()
    {
        sScoutNames = null;
    }

    public String getEventCode()
    {
        return m_eventCode;
    }

    public List<String> getScoutNames()
    {
        return m_scoutNames;
    }

    public void setEventCode(String eventCode)
    {
        m_eventCode = eventCode;
        m_bScoutNamesLoaded = false;
        m_scoutNames = new ArrayList<>();
    }

    /**
     * Reads the scout names JSON file from internal storage.
     *
     * @param context the context used to open the file
     * @param bSilent if true, error Toast messages are suppressed
     */
    public void readScoutNamesJSON(Context context, boolean bSilent)
    {
        if (m_eventCode == null || m_eventCode.trim().isEmpty())
        {
            return;
        }

        Log.d(TAG, "Looking for scout names for: " + m_eventCode);

        try
        {
            JSONArray jsonArray = m_serializer.loadScoutNames(m_eventCode);
            if (jsonArray != null)
            {
                m_scoutNames.clear();
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    m_scoutNames.add(jsonArray.getString(i));
                }
                m_bScoutNamesLoaded = true;
                Log.d(TAG, "Successfully loaded scout names for " + m_eventCode);
                Toast.makeText(context, "Loaded scout names for " + m_eventCode, Toast.LENGTH_SHORT).show();
            }
            else if (!bSilent)
            {
                Log.e(TAG, "Scout names file not found for event: " + m_eventCode);
                Toast.makeText(context, "Scout names file not found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException | IOException e)
        {
            Log.e(TAG, "Error reading scout names file", e);
        }
    }

    public boolean isScoutNamesLoaded()
    {
        return m_bScoutNamesLoaded;
    }
}
