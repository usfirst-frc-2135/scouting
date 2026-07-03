package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Singleton class for managing event matches.
 * Loads and parses match information from a JSON file specific to an event code.
 * Handles its own persistence by extending {@link BaseJSONSerializer}.
 */
public class EventMatches extends BaseJSONSerializer
{
    private static final String TAG = "EventMatches";
    private static final String MATCHES_FILE_SUFFIX = "_matches.json";

    // JSON Keys used by the blue alliance
    private static final String KEY_ALLIANCES = "alliances";
    private static final String KEY_BLUE = "blue";
    private static final String KEY_RED = "red";
    private static final String KEY_TEAM_KEYS = "team_keys";
    private static final String KEY_COMP_LEVEL = "comp_level";
    private static final String KEY_MATCH_NUMBER = "match_number";

    // Data members
    private String m_eventCode;
    private JSONArray m_jsonData;
    private boolean m_bEventMatchesLoaded;

    private static volatile EventMatches sEventMatches;

    private EventMatches(Context context, String eventCode)
    {
        super(context);
        Log.d(TAG, "EventMatches constructor");
        m_eventCode = eventCode;
        m_bEventMatchesLoaded = false;
        m_jsonData = null;
    }

    /**
     * Returns the singleton instance of EventMatches using the event code from Settings.
     *
     * @param context the context used for file operations
     * @return the singleton EventMatches instance
     */
    public static EventMatches getInstance(Context context)
    {
        String eventCode = Settings.getInstance(context).getEventCode();
        return getInstance(context, eventCode, false);
    }

    /**
     * Returns the singleton instance of EventMatches.
     * If the event code changes or a reload is forced, the data is reloaded.
     *
     * @param context      the context used for file operations and Toast messages
     * @param eventCode    the FRC event code
     * @param bForceReload whether to force a reload of the JSON data
     * @return the singleton EventMatches instance
     */
    public static EventMatches getInstance(Context context, String eventCode, boolean bForceReload)
    {
        Log.d(TAG, "getInstance()");
        if (sEventMatches == null)
        {
            synchronized (EventMatches.class)
            {
                if (sEventMatches == null)
                {
                    Log.d(TAG, "Creating new sEventMatches for eventCode: " + eventCode);
                    sEventMatches = new EventMatches(context, eventCode);
                    sEventMatches.readEventMatchesJSON(context, true);
                }
            }
        }

        // Handle event code change or forced reload
        synchronized (EventMatches.class)
        {
            String currentCode = sEventMatches.getEventCode();
            if (bForceReload || !currentCode.equalsIgnoreCase(eventCode))
            {
                Log.d(TAG, "Updating event data: " + currentCode + " -> " + eventCode);
                sEventMatches.setEventCode(eventCode);
                sEventMatches.readEventMatchesJSON(context, true);
            }
        }
        return sEventMatches;
    }

    /**
     * Clears the singleton instance of EventMatches.
     */
    public static void clear()
    {
        synchronized (EventMatches.class)
        {
            Log.d(TAG, "Clearing EventMatches instance");
            sEventMatches = null;
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
        m_bEventMatchesLoaded = false;
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

        Log.d(TAG, "Reading matches JSON for: " + m_eventCode);

        try
        {
            m_jsonData = loadEventMatches(m_eventCode);
            if (m_jsonData != null)
            {
                m_bEventMatchesLoaded = true;

                String msg = "Successfully loaded " + m_eventCode + " matches";
                Log.d(TAG, msg);
                if (!bSilent)
                {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
            else if (!bSilent)
            {
                Log.e(TAG, "Matches file not found for event: " + m_eventCode);
                Toast.makeText(context, "Matches file not found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException | IOException e)
        {
            handleError(context, "Failed to parse match data for: " + m_eventCode, bSilent, e);
        }
    }

    /**
     * Saves event data for a specific event to a JSON file.
     *
     * @param eventCode    the TBA event code
     * @param eventMatches the JSONArray containing match information
     * @throws IOException if writing the file fails
     */
    public void saveEventMatches(String eventCode, JSONArray eventMatches)
            throws IOException
    {
        Log.d(TAG, "saveEventMatches()");
        if (eventCode == null || eventMatches == null)
        {
            return;
        }

        // Cleanup existing matches file if it exists
        deleteEventMatches(eventCode);

        String eventFileName = getEventFileName(eventCode);
        File file = new File(m_dataDir, eventFileName);

        Log.d(TAG, "Saving event data for " + eventCode + " to: " + file.getAbsolutePath());
        writeStringToFile(file, eventMatches.toString());
        Log.i(TAG, "Successfully saved " + eventMatches.length() + " matches for event: " + eventCode);
    }

    /**
     * Loads the event matches data for a specific event.
     *
     * @param eventCode the TBA event code
     * @return the loaded JSONArray, or null if the file doesn't exist
     * @throws IOException   if reading the file fails
     * @throws JSONException if parsing the JSON data fails
     */
    public JSONArray loadEventMatches(String eventCode)
            throws IOException, JSONException
    {
        Log.d(TAG, "loadEventMatches()");
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            return null;
        }

        String filename = getEventFileName(eventCode);
        File file = new File(m_dataDir, filename);
        return loadJSONArray(file);
    }

    /**
     * Deletes the event match data for a specific event from local storage.
     * If eventCode is null or empty, deletes all event match files.
     *
     * @param eventCode the TBA event code to clear (e.g., "2026casac"), or null to clear all
     * @return the number of files deleted
     */
    public int deleteEventMatches(String eventCode)
    {
        Log.d(TAG, "deleteEventMatches()");
        File[] fileList;
        int deletedCount = 0;

        // Delete all files or selected file
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            fileList = m_dataDir.listFiles();
        }
        else
        {
            String filename = getEventFileName(eventCode);
            fileList = new File[]{new File(m_dataDir, filename)};
        }

        // Walk through list deleting files
        if (fileList != null)
        {
            for (File f : fileList)
            {
                if (f.getName().endsWith(MATCHES_FILE_SUFFIX))
                {
                    if (f.exists() && f.delete())
                    {
                        deletedCount++;
                        Log.d(TAG, "Deleted event file: " + f.getName());
                    }
                }
            }
        }

        if (deletedCount > 0)
        {
            EventMatches.clear();
        }
        return deletedCount;
    }

    /**
     * Helper to construct the filename for a given event code.
     *
     * @param eventCode the TBA event code
     * @return the filename string
     */
    private String getEventFileName(String eventCode)
    {
        return eventCode.trim().toLowerCase(Locale.US) + MATCHES_FILE_SUFFIX;
    }

    private void handleError(Context context, String msg, boolean bSilent, Exception e)
    {
        Log.e(TAG, msg, e);
        if (!bSilent)
        {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    public boolean isEventMatchesLoaded()
    {
        return m_bEventMatchesLoaded;
    }

    /**
     * Retrieves the team keys for a specific match.
     *
     * @param matchNum the match identifier (e.g., "qm1")
     * @return an array of 7 strings: index 0 is a placeholder, 1-3 are Red teams, 4-6 are Blue teams
     */
    public String[] getMatchTeams(String matchNum)
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
