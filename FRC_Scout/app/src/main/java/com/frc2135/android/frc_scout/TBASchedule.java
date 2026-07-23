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
 * Singleton class for managing event match schedule data retrieved from The Blue Alliance (TBA).
 * <p>
 * This class handles the loading, parsing, and local persistence of schedule information
 * (team keys, match numbers, competition levels) for a specific FRC event.
 * <p>
 * It follows a "Write-through Cache" pattern where successful file writes automatically
 * trigger a refresh of the internal memory state.
 */
public class TBASchedule extends BaseJSONSerializer
{
    private static final String TAG = "TBASchedule";

    // JSON Keys used by the blue alliance
    private static final String TBA_KEY_ALLIANCES = "alliances";
    private static final String TBA_KEY_BLUE = "blue";
    private static final String TBA_KEY_RED = "red";
    private static final String TBA_KEY_TEAM_KEYS = "team_keys";
    private static final String TBA_KEY_COMP_LEVEL = "comp_level";
    private static final String TBA_KEY_MATCH_NUMBER = "match_number";

    // FRC event code for the currently loaded match records. Static to ensure global consistency.
    private static String m_eventCode;
    private JSONArray m_tbaScheduleJSON;
    private boolean m_bTBAScheduleLoaded;

    private static volatile TBASchedule sTBASchedule;

    /**
     * Initializes a new TBASchedule repository.
     *
     * @param context the context used for file operations
     */
    private TBASchedule(Context context)
    {
        super(context);
        Log.v(TAG, "TBASchedule constructor");
        m_bTBAScheduleLoaded = false;
        m_tbaScheduleJSON = null;
    }

    /**
     * Returns the singleton instance of TBASchedule using the default event code from application settings.
     *
     * @param context the context used for file operations
     * @return the singleton TBASchedule instance
     */
    @SuppressWarnings("unused")
    public static TBASchedule getInstance(Context context)
    {
        String eventCode = Settings.getInstance(context).getEventCode();
        return getInstance(context, eventCode, false);
    }

    /**
     * Returns the thread-safe singleton instance of TBASchedule.
     * <p>
     * If the requested event code differs from the currently loaded one, or if a reload is forced,
     * the repository will re-initialize and attempt to load data from storage.
     *
     * @param context      the context used for file and display operations
     * @param eventCode    the FRC event code
     * @param bForceReload if true, forces a reload of the matches from disk even if already loaded
     * @return the singleton TBASchedule instance
     */
    public static TBASchedule getInstance(Context context, String eventCode, boolean bForceReload)
    {
        Log.v(TAG, "getInstance");
        synchronized (TBASchedule.class)
        {
            if (sTBASchedule == null)
            {
                Log.i(TAG, "Creating new sTBASchedule for eventCode: " + eventCode);
                m_eventCode = eventCode;
                sTBASchedule = new TBASchedule(context);
                sTBASchedule.loadTBAScheduleJSON(true);
            }
            else if (bForceReload || !eventCode.equalsIgnoreCase(m_eventCode))
            {
                Log.i(TAG, "Updating TBA schedule: " + m_eventCode + " -> " + eventCode);
                m_eventCode = eventCode;
                sTBASchedule.m_bTBAScheduleLoaded = false;
                sTBASchedule.m_tbaScheduleJSON = null;
                sTBASchedule.loadTBAScheduleJSON(true);
            }
            return sTBASchedule;
        }
    }

    /**
     * Clears the singleton instance of TBASchedule.
     */
    private static void clearTBASchedule()
    {
        synchronized (TBASchedule.class)
        {
            Log.v(TAG, "clearTBASchedule");
            sTBASchedule = null;
        }
    }

    /**
     * Reads the event matches JSON file from internal storage for the current event.
     *
     * @param bSilent if true, error notifications are suppressed
     */
    private void loadTBAScheduleJSON(boolean bSilent)
    {
        if (!ScoutingUtils.isValidEventCode(TAG, m_eventCode))
        {
            return;
        }

        Log.d(TAG, "loadTBAScheduleJSON: eventCode = " + m_eventCode);

        try
        {
            m_tbaScheduleJSON = readTBAScheduleFile(m_eventCode);
            if (m_tbaScheduleJSON != null)
            {
                m_bTBAScheduleLoaded = true;
                super.displayToastMessages(m_appContext, TAG, "Successfully read TBA schedule file for " + m_eventCode, bSilent, null);
            }
            else
            {
                super.displayToastMessages(m_appContext, TAG, "TBA schedule file not found for " + m_eventCode, bSilent, null);
            }
        }
        catch (JSONException | IOException e)
        {
            super.displayToastMessages(m_appContext, TAG, "Failed to parse TBA schedule file for: " + m_eventCode, bSilent, e);
        }
    }

    /**
     * Generates a filename for the TBA schedule record associated with an event.
     *
     * @param eventCode the FRC event code
     * @return the generated filename
     */
    private String getFilename(String eventCode)
    {
        return eventCode.trim().toLowerCase(Locale.US) + Constants.TBA_SCHEDULE_FILE_SUFFIX;
    }

    /**
     * Loads the match information JSON file for a specific event from local storage.
     *
     * @param eventCode the FRC event code
     * @return the loaded JSONArray, or null if the file is missing
     * @throws IOException   if reading the file fails
     * @throws JSONException if the file content is not a valid JSONArray
     */
    private JSONArray readTBAScheduleFile(String eventCode)
            throws IOException, JSONException
    {
        Log.d(TAG, "Reading TBA schedule from file for event: " + eventCode);
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            return null;
        }

        String eventFilename = getFilename(eventCode);
        File file = new File(m_dataDir, eventFilename);
        return loadJSONArray(file);
    }

    /**
     * Saves a {@link JSONArray} of match schedule data to a JSON file in local storage.
     *
     * @param eventCode   the FRC event code
     * @param tbaSchedule the JSONArray containing match information to persist
     * @param bSilent     if true, error notifications are suppressed
     * @return true if the file was written successfully
     */
    public boolean writeTBAScheduleFile(String eventCode, JSONArray tbaSchedule, boolean bSilent)
    {
        if (eventCode == null || tbaSchedule == null)
        {
            Log.w(TAG, "Attempted to save TBA schedule with null eventCode or data");
            return false;
        }

        Log.d(TAG, "Writing TBA schedule to file for event: " + eventCode);

        // Cleanup existing matches file if it exists
        deleteTBAScheduleFile(eventCode);

        String eventFileName = getFilename(eventCode);
        Log.i(TAG, "Saving TBA schedule for " + eventCode + " to: " + eventFileName);
        try
        {
            File file = new File(m_dataDir, eventFileName);
            saveJSONArray(file, tbaSchedule);
            Log.i(TAG, "Successfully saved " + tbaSchedule.length() + " TBA schedule for event: " + eventCode);
            loadTBAScheduleJSON(bSilent);
            return true;
        }
        catch (IOException e)
        {
            super.displayToastMessages(m_appContext, TAG, "Failed to write TBA schedule file for: " + eventCode, bSilent, e);
            return false;
        }
    }

    /**
     * Deletes match schedule data records from local storage.
     * <p>
     * If an event code is provided, only that file is deleted. If null, all match files are removed.
     *
     * @param eventCode the FRC event code (e.g., "2026casac"), or null to clear all
     * @return the number of files deleted
     */
    public int deleteTBAScheduleFile(String eventCode)
    {
        Log.d(TAG, "Deleting TBA schedule file for event: " + eventCode);
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
                if (file.getName().endsWith(Constants.TBA_SCHEDULE_FILE_SUFFIX))
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
            TBASchedule.clearTBASchedule();
        }
        else
        {
            Log.w(TAG, "Failed to delete TBA Schedule files");
        }

        return deletedCount;
    }

    /**
     * Checks whether match schedule data from The Blue Alliance is currently held in memory.
     *
     * @return true if data is loaded
     */
    public boolean isTBAScheduleLoaded()
    {
        return m_bTBAScheduleLoaded;
    }

    /**
     * Extracts the numeric portion from a team identifier (e.g., "frc2135" -> "2135").
     *
     * @param teamIdentifier the team identifier string
     * @return the numeric portion of the team identifier, or an empty string if null
     */
    private static String removeTeamNumPrefix(String teamIdentifier)
    {
        if (teamIdentifier == null || teamIdentifier.isEmpty())
        {
            return "";
        }
        return teamIdentifier.replaceAll("^\\D+", "");
    }

    /**
     * Retrieves the official list of team identifiers for a specific match.
     * <p>
     * format always removes the "frc" prefix from the team identifiers so the prefix never leaves this method
     *
     * @param matchNum the match identifier (e.g., "qm1")
     * @return an array of 7 strings: index 0 is a placeholder, 1-3 are Red alliance teams, 4-6 are Blue alliance teams
     */
    public String[] getMatchTeams(String matchNum)
    {
        String[] teams = new String[7];
        Arrays.fill(teams, "");

        if (m_tbaScheduleJSON != null && matchNum != null)
        {
            String targetMatch = matchNum.trim().toLowerCase(Locale.US);
            for (int i = 0; i < m_tbaScheduleJSON.length(); i++)
            {
                JSONObject matchObj = m_tbaScheduleJSON.optJSONObject(i);
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
                                teams[j + 1] = removeTeamNumPrefix(redTeams.optString(j, ""));
                            }
                        }
                        if (blueTeams != null)
                        {
                            for (int j = 0; j < Math.min(3, blueTeams.length()); j++)
                            {
                                teams[j + 4] = removeTeamNumPrefix(blueTeams.optString(j, ""));
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
