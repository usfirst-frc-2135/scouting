package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Singleton class for managing team alias mappings.
 * <p>
 * Team aliases are used to map temporary or regional team identifiers (often starting with "99")
 * to actual FRC team numbers. This class handles loading these mappings from event-specific
 * JSON files, providing bidirectional resolution, and maintaining an in-memory cache.
 * <p>
 * It follows a "Write-through Cache" pattern where successful file writes automatically
 * trigger a refresh of the internal memory state.
 */
public class TeamAliases extends BaseJSONSerializer
{
    private static final String TAG = "TeamAliases";
    private static final String TEAM_NUM_JSON_KEY = "teamNum";
    private static final String ALIAS_NUM_JSON_KEY = "aliasNum";

    // FRC event code for the currently loaded alias mappings. Static to ensure global consistency.
    private static String m_eventCode;
    private final Map<String, String> m_teamToAliasMap;
    private final Map<String, String> m_aliasToTeamMap;
    private boolean m_bTeamAliasesLoaded;

    private static volatile TeamAliases sTeamAliases;

    /**
     * Initializes a new TeamAliases repository.
     *
     * @param context the context used for file operations and internal storage access
     */
    private TeamAliases(Context context)
    {
        super(context);
        Log.v(TAG, "TeamAliases constructor");
        m_bTeamAliasesLoaded = false;
        m_teamToAliasMap = new HashMap<>();
        m_aliasToTeamMap = new HashMap<>();
    }

    /**
     * Returns the singleton instance of TeamAliases using the current event code from settings.
     *
     * @param context the context used for file operations
     * @return the singleton TeamAliases instance
     */
    public static TeamAliases getInstance(Context context)
    {
        String eventCode = Settings.getInstance(context).getEventCode();
        return getInstance(context, eventCode, false);
    }

    /**
     * Returns the thread-safe singleton instance of TeamAliases.
     * <p>
     * If the requested event code differs from the currently loaded one, or if a reload is forced,
     * the repository will re-initialize and attempt to load data from storage.
     *
     * @param context      the context used for file and display operations
     * @param eventCode    the FRC event code
     * @param bForceReload if true, forces a reload of the aliases from disk even if already loaded
     * @return the singleton TeamAliases instance
     */
    public static TeamAliases getInstance(Context context, String eventCode, boolean bForceReload)
    {
        Log.v(TAG, "getInstance");
        synchronized (TeamAliases.class)
        {
            if (sTeamAliases == null)
            {
                Log.i(TAG, "Creating new sTeamAliases for eventCode: " + eventCode);
                m_eventCode = eventCode;
                sTeamAliases = new TeamAliases(context);
                sTeamAliases.loadTeamAliasesJSON(true);
            }
            else if (bForceReload || !eventCode.equalsIgnoreCase(m_eventCode))
            {
                Log.i(TAG, "Resetting TeamAliases: " + m_eventCode + " -> " + eventCode);
                m_eventCode = eventCode;
                sTeamAliases.m_bTeamAliasesLoaded = false;
                sTeamAliases.m_teamToAliasMap.clear();
                sTeamAliases.m_aliasToTeamMap.clear();
                sTeamAliases.loadTeamAliasesJSON(true);
            }
            return sTeamAliases;
        }
    }

    /**
     * Clears the singleton instance and its internal cache.
     */
    private static void clearTeamAliases()
    {
        synchronized (TeamAliases.class)
        {
            Log.v(TAG, "clearTeamAliases");
            sTeamAliases = null;
        }
    }

    /**
     * Reads the event-specific aliases JSON file from internal storage into memory.
     *
     * @param bSilent if true, success Toast notifications are suppressed
     */
    private void loadTeamAliasesJSON(boolean bSilent)
    {
        if (!ScoutingUtils.isValidEventCode(TAG, m_eventCode))
        {
            return;
        }

        Log.i(TAG, "Reading team aliases file for: " + m_eventCode);

        try
        {
            JSONArray jsonArray = readTeamAliasesFile(m_eventCode);
            if (jsonArray != null)
            {
                parseTeamAliasesJSON(jsonArray);
                m_bTeamAliasesLoaded = true;
                super.displayToastMessages(m_appContext, TAG, "Successfully read team aliases file for " + m_eventCode, bSilent, null);
            }
            else
            {
                super.displayToastMessages(m_appContext, TAG, "Team aliases file not found for " + m_eventCode, bSilent, null);
            }
        }
        catch (JSONException | IOException e)
        {
            super.displayToastMessages(m_appContext, TAG, "Failed to parse team aliases file for: " + m_eventCode, bSilent, e);
        }
    }

    /**
     * Parses a JSONArray of alias objects into internal bidirectional maps.
     *
     * @param jsonArray the JSONArray to parse
     * @throws JSONException if the JSON structure is invalid
     */
    private void parseTeamAliasesJSON(JSONArray jsonArray)
            throws JSONException
    {
        m_teamToAliasMap.clear();
        m_aliasToTeamMap.clear();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject obj = jsonArray.getJSONObject(i);
            String teamNum = obj.optString(TEAM_NUM_JSON_KEY);
            String alias = obj.optString(ALIAS_NUM_JSON_KEY);
            if (!teamNum.isEmpty() && !alias.isEmpty())
            {
                m_teamToAliasMap.put(teamNum, alias);
                m_aliasToTeamMap.put(alias, teamNum);
            }
        }
    }

    /**
     * Generates a standard filename for an event's alias mapping record.
     *
     * @param eventCode the FRC event code
     * @return the generated filename (e.g., "2026casac_aliases.json")
     */
    private String getFilename(String eventCode)
    {
        return eventCode.trim().toLowerCase(Locale.US) + Constants.TEAM_ALIASES_FILENAME_SUFFIX;
    }

    /**
     * Loads the raw team aliases JSONArray for a specific event from storage.
     *
     * @param eventCode the FRC event code
     * @return the loaded JSONArray, or null if the file does not exist
     * @throws IOException   if file reading fails
     * @throws JSONException if the file content is not a valid JSONArray
     */
    private JSONArray readTeamAliasesFile(String eventCode)
            throws IOException, JSONException
    {
        Log.d(TAG, "Reading team aliases from file for event: " + eventCode);
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            return null;
        }

        String filename = getFilename(eventCode);
        File file = new File(m_dataDir, filename);
        return loadJSONArray(file);
    }

    /**
     * Persists a {@link JSONArray} of alias mappings to internal storage.
     * <p>
     * Following the "Write-through Cache" pattern, this method automatically reloads
     * the in-memory state upon a successful disk write.
     *
     * @param eventCode   the FRC event code
     * @param teamAliases the JSONArray of mapping data to save
     * @param bSilent     if true, error/success notifications are suppressed
     * @return true if the file was written and the cache reloaded successfully
     */
    public boolean writeTeamAliasesFile(String eventCode, JSONArray teamAliases, boolean bSilent)
    {
        if (eventCode == null || teamAliases == null)
        {
            Log.w(TAG, "Attempted to save team aliases with null eventCode or data");
            return false;
        }

        Log.d(TAG, "Writing team aliases to file for event: " + eventCode);

        // Cleanup existing aliases file if it exists
        deleteTeamAliasesFile(eventCode);

        String aliasFilename = getFilename(eventCode);
        Log.i(TAG, "Saving team aliases info for " + eventCode + " to: " + aliasFilename);

        try
        {
            File file = new File(m_dataDir, aliasFilename);
            saveJSONArray(file, teamAliases);
            Log.i(TAG, "Successfully saved " + teamAliases.length() + " team aliases for event: " + eventCode);
            loadTeamAliasesJSON(bSilent);
            return true;
        }
        catch (IOException e)
        {
            super.displayToastMessages(m_appContext, TAG, "Failed to write team aliases file for: " + eventCode, bSilent, e);
            return false;
        }
    }

    /**
     * Deletes team alias records from local storage.
     * <p>
     * If an event code is provided, only that file is deleted. If null, all alias files are removed.
     * This method automatically clears the in-memory singleton if any files were deleted.
     *
     * @param eventCode the FRC event code, or null to clear all records
     * @return the number of files successfully deleted
     */
    public int deleteTeamAliasesFile(String eventCode)
    {
        Log.d(TAG, "Deleting team aliases file for event: " + eventCode);
        File[] fileList;
        int deletedCount = 0;

        // Delete all files or selected file
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            fileList = m_dataDir.listFiles();
        }
        else
        {
            String filename = getFilename(eventCode);
            fileList = new File[]{new File(m_dataDir, filename)};
        }

        // Walk through list deleting files
        if (fileList != null)
        {
            for (File file : fileList)
            {
                if (file.getName().endsWith(Constants.TEAM_ALIASES_FILENAME_SUFFIX))
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
            TeamAliases.clearTeamAliases();
        }
        else
        {
            Log.w(TAG, "Failed to delete team aliases files");
        }

        return deletedCount;
    }

    /**
     * Checks whether team alias mappings for the current event are successfully loaded in memory.
     *
     * @return true if the memory cache is populated
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isTeamAliasesLoaded()
    {
        return m_bTeamAliasesLoaded;
    }

    /**
     * Resolves an FRC team identifier to its alias if a mapping exists and aliases are loaded.
     *
     * @param teamNum the raw team number string (e.g., "frc2135" or "2135")
     * @return the team alias if it exists (e.g. "9901"), otherwise the original team identifier
     */
    public String getAliasForTeamNum(String teamNum)
    {
        if (teamNum == null || !isTeamAliasesLoaded())
        {
            return teamNum;
        }
        String alias = m_teamToAliasMap.getOrDefault(teamNum, "");
        return (alias == null || alias.isEmpty()) ? teamNum : alias;
    }

    /**
     * Resolves a possible alias (starting with "99") to its actual FRC team number.
     *
     * @param alias the entered team number or alias string (e.g., "9901")
     * @return the actual team number if the entry is a recognized alias (e.g., "2135"), otherwise the original entry
     */
    public String getTeamNumForAlias(String alias)
    {
        if (alias == null || !isTeamAliasesLoaded() || !alias.startsWith("99"))
        {
            return alias;
        }
        String teamNum = m_aliasToTeamMap.getOrDefault(alias, "");
        return (teamNum == null || teamNum.isEmpty()) ? alias : teamNum;
    }
}
