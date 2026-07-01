package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Singleton class for managing competition event data.
 * Loads and parses match information from a JSON file specific to an event code.
 */
public class EventInfo
{
    private static final String TAG = "EventInfo";

    // JSON Keys
    private static final String KEY_ALLIANCES = "alliances";
    private static final String KEY_BLUE = "blue";
    private static final String KEY_RED = "red";
    private static final String KEY_TEAM_KEYS = "team_keys";
    private static final String KEY_COMP_LEVEL = "comp_level";
    private static final String KEY_MATCH_NUMBER = "match_number";

    // Data members
    private String m_eventCode;
    private JSONArray m_jsonData;
    private boolean m_bEventInfoLoaded;
    private static volatile EventInfo sEventInfo;

    private EventInfo(String eventCode)
    {
        m_eventCode = eventCode;
        m_bEventInfoLoaded = false;
        m_jsonData = null;
    }

    /**
     * Returns the singleton instance of EventInfo.
     * If the event code changes or a reload is forced, the data is reloaded.
     *
     * @param context      the context used for file operations and Toast messages
     * @param eventCode    the FRC event code
     * @param bForceReload whether to force a reload of the JSON data
     * @return the singleton EventInfo instance
     */
    public static EventInfo get(Context context, String eventCode, boolean bForceReload)
    {
        if (sEventInfo == null)
        {
            synchronized (EventInfo.class)
            {
                if (sEventInfo == null)
                {
                    Log.d(TAG, "Creating new sEventInfo for eventCode: " + eventCode);
                    sEventInfo = new EventInfo(eventCode);
                    sEventInfo.readEventMatchesJSON(context, true);
                }
            }
        }

        // Handle event code change or forced reload
        synchronized (EventInfo.class)
        {
            String currentCode = sEventInfo.getEventCode();
            if (bForceReload || !currentCode.equalsIgnoreCase(eventCode))
            {
                Log.d(TAG, "Updating event data: " + currentCode + " -> " + eventCode);
                sEventInfo.setEventCode(eventCode);
                sEventInfo.readEventMatchesJSON(context, true);
            }
        }
        return sEventInfo;
    }

    /**
     * Clears the singleton instance of EventInfo.
     */
    public static void clear()
    {
        synchronized (EventInfo.class)
        {
            Log.d(TAG, "Clearing EventInfo instance");
            sEventInfo = null;
        }
    }

    public String getEventCode()
    {
        return m_eventCode;
    }

    /**
     * Updates the event code and resets the loaded data state.
     *
     * @param eventCode the new event code
     */
    private void setEventCode(String eventCode)
    {
        m_eventCode = eventCode;
        m_bEventInfoLoaded = false;
        m_jsonData = null;
    }

    /**
     * Reads the event matches JSON file from the device's internal storage.
     *
     * @param context the context used to open the file and show Toasts
     * @param bSilent if true, error Toast messages are suppressed
     */
    public void readEventMatchesJSON(Context context, boolean bSilent)
    {
        if (m_eventCode == null || m_eventCode.trim().isEmpty())
        {
            return;
        }

        String filename = m_eventCode.trim().toLowerCase(Locale.US) + "_matches.json";
        File file = new File(context.getFilesDir(), filename);
        Log.d(TAG, "Reading matches JSON from: " + file.getAbsolutePath());

        if (file.exists())
        {
            try (InputStream in = context.openFileInput(filename);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in)))
            {
                StringBuilder jsonString = new StringBuilder();
                for (String line = reader.readLine(); line != null; line = reader.readLine())
                {
                    jsonString.append(line);
                }

                m_jsonData = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
                m_bEventInfoLoaded = true;

                String msg = "Successfully loaded " + m_eventCode + " matches";
                Log.d(TAG, msg);
                if (!bSilent)
                {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
            catch (FileNotFoundException e)
            {
                handleError(context, "Matches file not found: " + filename, bSilent, e);
            }
            catch (JSONException | IOException e)
            {
                handleError(context, "Failed to parse match data for: " + m_eventCode, bSilent, e);
            }
        }
        else
        {
            Log.d(TAG, "Matches file does not exist: " + filename);
        }
    }

    private void handleError(Context context, String msg, boolean bSilent, Exception e)
    {
        Log.e(TAG, msg, e);
        if (!bSilent)
        {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    public boolean isEventInfoLoaded()
    {
        return m_bEventInfoLoaded;
    }

    /**
     * Retrieves the team keys for a specific match.
     *
     * @param matchNum the match identifier (e.g., "qm1")
     * @return an array of 7 strings: index 0 is a placeholder, 1-3 are Red teams, 4-6 are Blue teams
     */
    public String[] getTeams(String matchNum)
    {
        String[] teams = new String[7];
        for (int i = 0; i < 7; i++)
        {
            teams[i] = "";
        }

        if (m_jsonData != null && matchNum != null)
        {
            String targetMatch = matchNum.trim().toLowerCase(Locale.US);
            for (int i = 0; i < m_jsonData.length(); i++)
            {
                JSONObject matchObj = m_jsonData.optJSONObject(i);
                if (matchObj == null)
                {
                    continue;
                }

                String compLevel = matchObj.optString(KEY_COMP_LEVEL);
                String matchNumber = matchObj.optString(KEY_MATCH_NUMBER);

                if ((compLevel + matchNumber).equalsIgnoreCase(targetMatch))
                {
                    JSONObject alliances = matchObj.optJSONObject(KEY_ALLIANCES);
                    if (alliances == null)
                    {
                        continue;
                    }

                    JSONObject blueAlliance = alliances.optJSONObject(KEY_BLUE);
                    JSONObject redAlliance = alliances.optJSONObject(KEY_RED);

                    if (blueAlliance != null && redAlliance != null)
                    {
                        JSONArray blueTeams = blueAlliance.optJSONArray(KEY_TEAM_KEYS);
                        JSONArray redTeams = redAlliance.optJSONArray(KEY_TEAM_KEYS);

                        teams[0] = "No team selected";
                        if (redTeams != null)
                        {
                            for (int j = 0; j < Math.min(3, redTeams.length()); j++)
                            {
                                teams[j + 1] = redTeams.optString(j, "");
                            }
                        }
                        if (blueTeams != null)
                        {
                            for (int j = 0; j < Math.min(3, blueTeams.length()); j++)
                            {
                                teams[j + 4] = blueTeams.optString(j, "");
                            }
                        }
                        return teams;
                    }
                }
            }
            Log.d(TAG, "getTeams(): Match '" + matchNum + "' not found in loaded data.");
        }
        return teams;
    }
}
