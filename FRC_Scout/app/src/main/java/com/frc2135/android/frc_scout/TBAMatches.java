package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Singleton class for managing event matches.
 * Loads and parses match information from a JSON file specific to an event code.
 * Handles its own persistence by extending {@link BaseJSONSerializer}.
 */
public class TBAMatches extends BaseJSONSerializer
{
    private static final String TAG = "TBAMatches";

    // JSON Keys used by the blue alliance
    private static final String TBA_KEY_ALLIANCES = "alliances";
    private static final String TBA_KEY_BLUE = "blue";
    private static final String TBA_KEY_RED = "red";
    private static final String TBA_KEY_TEAM_KEYS = "team_keys";
    private static final String TBA_KEY_COMP_LEVEL = "comp_level";
    private static final String TBA_KEY_MATCH_NUMBER = "match_number";

    // Data members
    private String m_eventCode;
    private JSONArray m_tbaMatchesJSON;
    private boolean m_bTBAMatchesLoaded;

    private static volatile TBAMatches sTBAMatches;

    private TBAMatches(Context context, String eventCode)
    {
        super(context);
        Log.d(TAG, "TBAMatches constructor");
        m_eventCode = eventCode;
        m_bTBAMatchesLoaded = false;
        m_tbaMatchesJSON = null;
    }

    /**
     * Returns the singleton instance of TBAMatches using the event code from Settings.
     *
     * @param context the context used for file operations
     * @return the singleton TBAMatches instance
     */
    @SuppressWarnings("unused")
    public static TBAMatches getInstance(Context context)
    {
        String eventCode = Settings.getInstance(context).getEventCode();
        return getInstance(context, eventCode, false);
    }

    /**
     * Returns the singleton instance of TBAMatches.
     * If the event code changes or a reload is forced, the data is reloaded.
     *
     * @param context      the context used for file operations and Toast messages
     * @param eventCode    the FRC event code
     * @param bForceReload whether to force a reload of the JSON data
     * @return the singleton TBAMatches instance
     */
    public static TBAMatches getInstance(Context context, String eventCode, boolean bForceReload)
    {
        Log.d(TAG, "getInstance()");
        if (sTBAMatches == null)
        {
            synchronized (TBAMatches.class)
            {
                if (sTBAMatches == null)
                {
                    Log.d(TAG, "Creating new sTBAMatches for eventCode: " + eventCode);
                    sTBAMatches = new TBAMatches(context, eventCode);
                    sTBAMatches.readTBAMatchesJSON(context, true);
                }
            }
        }

        // Handle event code change or forced reload
        synchronized (TBAMatches.class)
        {
            String currentCode = sTBAMatches.getEventCode();
            if (bForceReload || !currentCode.equalsIgnoreCase(eventCode))
            {
                Log.d(TAG, "Updating TBA matches: " + currentCode + " -> " + eventCode);
                sTBAMatches.setEventCode(eventCode);
                sTBAMatches.readTBAMatchesJSON(context, true);
            }
        }
        return sTBAMatches;
    }

    /**
     * Clears the singleton instance of TBAMatches.
     */
    public static void clearTBAMatches()
    {
        synchronized (TBAMatches.class)
        {
            Log.d(TAG, "Clearing TBAMatches instance");
            sTBAMatches = null;
        }
    }

    /**
     * Returns the event code associated with this match data.
     *
     * @return the event code string
     */
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
        m_bTBAMatchesLoaded = false;
        m_tbaMatchesJSON = null;
    }

    /**
     * Reads the event matches JSON file from the device's internal storage.
     *
     * @param context the context used to open the file and show Toasts
     * @param bSilent if true, error Toast messages are suppressed
     */
    public void readTBAMatchesJSON(Context context, boolean bSilent)
    {
        if (!Settings.getInstance(context).isValidEventCode(m_eventCode))
        {
            return;
        }

        Log.d(TAG, "Reading TBA matches JSON for: " + m_eventCode);

        try
        {
            m_tbaMatchesJSON = readTBAMatchesFile(m_eventCode);
            if (m_tbaMatchesJSON != null)
            {
                m_bTBAMatchesLoaded = true;

                String msg = "Successfully loaded " + m_eventCode + " TBA matches";
                Log.d(TAG, msg);
                if (!bSilent)
                {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
            else if (!bSilent)
            {
                Log.e(TAG, "TBAMatches file not found for event: " + m_eventCode);
                Toast.makeText(context, "TBAMatches file not found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException | IOException e)
        {
            handleError(context, "Failed to parse TBA matches for: " + m_eventCode, bSilent, e);
        }
    }

    /**
     * Gets the filename for a given event code.
     *
     * @param eventCode the FRC event code
     * @return the filename
     */
    private String getFilename(String eventCode)
    {
        return eventCode.trim().toLowerCase(Locale.US) + Constants.TBA_MATCHES_FILE_SUFFIX;
    }

    /**
     * Writes event data for a specific event to a JSON file.
     *
     * @param eventCode  the TBA event code
     * @param tbaMatches the JSONArray containing match information
     * @throws IOException if writing the file fails
     */
    public void writeTBAMatchesFile(String eventCode, JSONArray tbaMatches)
            throws IOException
    {
        Log.d(TAG, "Writing TBA matches to file for event: " + eventCode);
        if (eventCode == null || tbaMatches == null)
        {
            Log.w(TAG, "Attempted to save TBA matches with null eventCode or data");
            return;
        }

        // Cleanup existing matches file if it exists
        deleteTBAMatchesFile(eventCode);

        String eventFileName = getFilename(eventCode);
        File file = new File(m_dataDir, eventFileName);

        Log.d(TAG, "Saving TBA matches for " + eventCode + " to: " + file.getAbsolutePath());
        writeStringToFile(file, tbaMatches.toString());
        Log.i(TAG, "Successfully saved " + tbaMatches.length() + " TBA matches for event: " + eventCode);
    }

    /**
     * Loads the event matches data for a specific event.
     *
     * @param eventCode the TBA event code
     * @return the loaded JSONArray, or null if the file doesn't exist
     * @throws IOException   if reading the file fails
     * @throws JSONException if parsing the JSON data fails
     */
    public JSONArray readTBAMatchesFile(String eventCode)
            throws IOException, JSONException
    {
        Log.d(TAG, "Reading TBA matches to file for event: " + eventCode);
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            return null;
        }

        String eventFilename = getFilename(eventCode);
        File file = new File(m_dataDir, eventFilename);
        return loadJSONArray(file);
    }

    /**
     * Deletes the event match data for a specific event from local storage.
     * If eventCode is null or empty, deletes all event match files.
     *
     * @param eventCode the TBA event code to clear (e.g., "2026casac"), or null to clear all
     * @return the number of files deleted
     */
    public int deleteTBAMatchesFile(String eventCode)
    {
        Log.d(TAG, "Deleting TBA matches to file for event: " + eventCode);
        File[] fileList;
        int deletedCount = 0;

        // Delete all files or selected file
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            fileList = m_dataDir.listFiles();
        }
        else
        {
            String eventFilename = getFilename(eventCode);
            fileList = new File[]{new File(m_dataDir, eventFilename)};
        }

        // Walk through list deleting files
        if (fileList != null)
        {
            for (File file : fileList)
            {
                if (file.getName().endsWith(Constants.TBA_MATCHES_FILE_SUFFIX))
                {
                    if (file.exists() && file.delete())
                    {
                        deletedCount++;
                        Log.d(TAG, "Deleted event file: " + file.getName());
                    }
                }
            }
        }

        if (deletedCount > 0)
        {
            TBAMatches.clearTBAMatches();
        }
        return deletedCount;
    }

    /**
     * Log and optionally display an error message for an exception.
     *
     * @param context the context to show the Toast in
     * @param msg     the error message
     * @param bSilent if true, the Toast is suppressed
     * @param e       the exception that occurred
     */
    private void handleError(Context context, String msg, boolean bSilent, Exception e)
    {
        Log.e(TAG, msg, e);
        if (!bSilent)
        {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks whether match data from The Blue Alliance has been successfully loaded.
     *
     * @return true if data is loaded
     */
    public boolean isTBAMatchesLoaded()
    {
        return m_bTBAMatchesLoaded;
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
        Arrays.fill(teams, "");

        if (m_tbaMatchesJSON != null && matchNum != null)
        {
            String targetMatch = matchNum.trim().toLowerCase(Locale.US);
            for (int i = 0; i < m_tbaMatchesJSON.length(); i++)
            {
                JSONObject matchObj = m_tbaMatchesJSON.optJSONObject(i);
                if (matchObj == null)
                {
                    continue;
                }

                String compLevel = matchObj.optString(TBA_KEY_COMP_LEVEL);
                String matchNumber = matchObj.optString(TBA_KEY_MATCH_NUMBER);

                if ((compLevel + matchNumber).equalsIgnoreCase(targetMatch))
                {
                    JSONObject alliances = matchObj.optJSONObject(TBA_KEY_ALLIANCES);
                    if (alliances == null)
                    {
                        continue;
                    }

                    JSONObject blueAlliance = alliances.optJSONObject(TBA_KEY_BLUE);
                    JSONObject redAlliance = alliances.optJSONObject(TBA_KEY_RED);

                    if (blueAlliance != null && redAlliance != null)
                    {
                        JSONArray blueTeams = blueAlliance.optJSONArray(TBA_KEY_TEAM_KEYS);
                        JSONArray redTeams = redAlliance.optJSONArray(TBA_KEY_TEAM_KEYS);

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
