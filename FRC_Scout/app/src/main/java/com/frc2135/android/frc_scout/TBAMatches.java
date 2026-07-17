package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Singleton class for managing event match data retrieved from The Blue Alliance (TBA).
 * Loads, parses, and persists match information (team keys, match numbers, competition levels) for a specific FRC event.
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

    /**
     * Initializes a new TBAMatches repository for a specific event.
     *
     * @param context   the context used for file operations
     * @param eventCode the FRC event code
     */
    private TBAMatches(Context context, String eventCode)
    {
        super(context);
        Log.v(TAG, "TBAMatches constructor");
        m_eventCode = eventCode;
        m_bTBAMatchesLoaded = false;
        m_tbaMatchesJSON = null;
    }

    /**
     * Returns the singleton instance of TBAMatches using the default event code from application settings.
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
     * Returns the thread-safe singleton instance of TBAMatches.
     * If the requested event code differs from the currently loaded one, or if a reload is forced, it re-initializes the data.
     *
     * @param context      the context used for file operations and display messages
     * @param eventCode    the FRC event code
     * @param bForceReload if true, forces a reload of the matches JSON from storage
     * @return the singleton TBAMatches instance
     */
    public static TBAMatches getInstance(Context context, String eventCode, boolean bForceReload)
    {
        Log.v(TAG, "getInstance");
        if (sTBAMatches == null)
        {
            synchronized (TBAMatches.class)
            {
                if (sTBAMatches == null)
                {
                    Log.i(TAG, "Creating new sTBAMatches for eventCode: " + eventCode);
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
                Log.i(TAG, "Updating TBA matches: " + currentCode + " -> " + eventCode);
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
            Log.v(TAG, "clearTBAMatches");
            sTBAMatches = null;
        }
    }

    /**
     * Returns the FRC event code currently associated with this match repository.
     *
     * @return the event code string
     */
    public String getEventCode()
    {
        return m_eventCode;
    }

    /**
     * Updates the event code and resets the internal state of loaded match data.
     *
     * @param eventCode the new FRC event code
     */
    private void setEventCode(String eventCode)
    {
        m_eventCode = eventCode;
        m_bTBAMatchesLoaded = false;
        m_tbaMatchesJSON = null;
    }

    /**
     * Reads the event matches JSON file from internal storage for the current event.
     *
     * @param context the context used to open the file and show messages
     * @param bSilent if true, error notifications are suppressed
     */
    public void readTBAMatchesJSON(Context context, boolean bSilent)
    {
        if (!Settings.getInstance(context).isValidEventCode(m_eventCode))
        {
            return;
        }

        Log.d(TAG, "readTBAMatchesJSON: eventCode = " + m_eventCode);

        try
        {
            m_tbaMatchesJSON = readTBAMatchesFile(m_eventCode);
            if (m_tbaMatchesJSON != null)
            {
                m_bTBAMatchesLoaded = true;
                super.displayToastMessages(context, TAG, "Successfully loaded TBA matches for " + m_eventCode, false, null);
            }
            else
            {
                super.displayToastMessages(context, TAG, "TBA matches file not found for " + m_eventCode, bSilent, null);
            }
        }
        catch (JSONException | IOException e)
        {
            super.displayToastMessages(context, TAG, "Failed to parse TBA matches for: " + m_eventCode, bSilent, e);
        }
    }

    /**
     * Generates a filename for the TBA matches record associated with an event.
     *
     * @param eventCode the FRC event code
     * @return the generated filename
     */
    private String getFilename(String eventCode)
    {
        return eventCode.trim().toLowerCase(Locale.US) + Constants.TBA_MATCHES_FILE_SUFFIX;
    }

    /**
     * Saves a {@link JSONArray} of match data to a JSON file in local storage.
     *
     * @param eventCode  the FRC event code
     * @param tbaMatches the JSONArray containing match information to persist
     * @param bSilent    if true, error notifications are suppressed
     * @return true if the file was written successfully
     */
    public boolean writeTBAMatchesFile(String eventCode, JSONArray tbaMatches, boolean bSilent)
    {
        if (eventCode == null || tbaMatches == null)
        {
            Log.w(TAG, "Attempted to save TBA matches with null eventCode or data");
            return false;
        }

        Log.d(TAG, "Writing TBA matches to file for event: " + eventCode);

        // Cleanup existing matches file if it exists
        deleteTBAMatchesFile(eventCode);

        String eventFileName = getFilename(eventCode);
        Log.i(TAG, "Saving TBA matches for " + eventCode + " to: " + eventFileName);
        try
        {
            File file = new File(m_dataDir, eventFileName);
            writeStringToFile(file, tbaMatches.toString());
            Log.i(TAG, "Successfully saved " + tbaMatches.length() + " TBA matches for event: " + eventCode);
            return true;
        }
        catch (IOException e)
        {
            super.displayToastMessages(m_appContext, TAG, "Failed to write TBA matches file for: " + eventCode, bSilent, e);
            return false;
        }
    }

    /**
     * Loads the match information JSON file for a specific event from local storage.
     *
     * @param eventCode the FRC event code
     * @return the loaded JSONArray, or null if the file is missing
     * @throws IOException   if reading the file fails
     * @throws JSONException if the file content is not a valid JSONArray
     */
    public JSONArray readTBAMatchesFile(String eventCode)
            throws IOException, JSONException
    {
        Log.d(TAG, "Reading TBA matches from file for event: " + eventCode);
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            return null;
        }

        String eventFilename = getFilename(eventCode);
        File file = new File(m_dataDir, eventFilename);
        return loadJSONArray(file);
    }

    /**
     * Deletes match data records from local storage.
     * If an event code is provided, only that file is deleted. If null, all match files are removed.
     *
     * @param eventCode the FRC event code (e.g., "2026casac"), or null to clear all
     * @return the number of files deleted
     */
    public int deleteTBAMatchesFile(String eventCode)
    {
        Log.d(TAG, "Deleting TBA matches file for event: " + eventCode);
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
                        Log.i(TAG, "Deleted event file: " + file.getName());
                    }
                }
            }
        }

        if (deletedCount > 0)
        {
            TBAMatches.clearTBAMatches();
        }
        else
        {
            Log.w(TAG, "Failed to delete TBA Matches files");
        }

        return deletedCount;
    }

    /**
     * Checks whether match data from The Blue Alliance is currently held in memory.
     *
     * @return true if data is loaded
     */
    public boolean isTBAMatchesLoaded()
    {
        return m_bTBAMatchesLoaded;
    }

    /**
     * Retrieves the official list of team identifiers for a specific match.
     *
     * @param matchNum the match identifier (e.g., "qm1")
     * @return an array of 7 strings: index 0 is a placeholder, 1-3 are Red alliance teams, 4-6 are Blue alliance teams
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
            Log.w(TAG, "getTeams(): Match '" + matchNum + "' not found in loaded data.");
        }
        return teams;
    }
}
