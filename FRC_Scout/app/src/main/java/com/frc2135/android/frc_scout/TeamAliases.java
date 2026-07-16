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
 * Singleton class for managing team aliases data.
 * Loads and parses team number to alias mapping (e.g., mapping a "99" placeholder to a real team number) from local JSON files.
 * Handles its own persistence by extending {@link BaseJSONSerializer}.
 */
public class TeamAliases extends BaseJSONSerializer
{
    private static final String TAG = "TeamAliases";
    private static final String TEAM_NUM_JSON_KEY = "teamNum";
    private static final String ALIAS_NUM_JSON_KEY = "aliasNum";

    private String m_eventCode;
    private final Map<String, String> m_teamToAliasMap;
    private final Map<String, String> m_aliasToTeamMap;
    private boolean m_bTeamAliasesLoaded;

    private static volatile TeamAliases sTeamAliases;

    /**
     * Initializes the team alias repository for a specific event.
     *
     * @param context   the context used for file operations
     * @param eventCode the FRC event code
     */
    private TeamAliases(Context context, String eventCode)
    {
        super(context);
        Log.v(TAG, "TeamAliases constructor");
        m_eventCode = eventCode;
        m_bTeamAliasesLoaded = false;
        m_teamToAliasMap = new HashMap<>();
        m_aliasToTeamMap = new HashMap<>();
    }

    /**
     * Returns the singleton instance of TeamAliases using the default event code from application settings.
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
     * If the requested event code differs from the currently loaded one, it re-initializes for the new event.
     *
     * @param context      the context used for file operations and display messages
     * @param eventCode    the FRC event code
     * @param bForceReload if true, forces a reload of the aliases JSON from storage
     * @return the singleton TeamAliases instance
     */
    public static TeamAliases getInstance(Context context, String eventCode, boolean bForceReload)
    {
        Log.v(TAG, "getInstance");
        synchronized (TeamAliases.class)
        {
            if (sTeamAliases == null)
            {
                Log.i(TAG, "Creating a new sTeamAliases for eventCode " + eventCode);
                sTeamAliases = new TeamAliases(context, eventCode);
                sTeamAliases.readTeamAliasesJSON(context, true);
            }
            else
            {
                String oldEventCode = sTeamAliases.getEventCode();
                if (bForceReload || !oldEventCode.equalsIgnoreCase(eventCode))
                {
                    Log.i(TAG, "Resetting TeamAliases: " + oldEventCode + " -> " + eventCode);
                    sTeamAliases.setEventCode(eventCode);
                    sTeamAliases.readTeamAliasesJSON(context, true);
                }
            }
            return sTeamAliases;
        }
    }

    /**
     * Clears the singleton instance of TeamAliases.
     */
    @SuppressWarnings("unused")
    public static void clearTeamAliases()
    {
        Log.v(TAG, "clearTeamAliases");
        sTeamAliases = null;
    }

    /**
     * Returns the FRC event code currently associated with this alias mapping.
     *
     * @return the event code string
     */
    public String getEventCode()
    {
        return m_eventCode;
    }

    /**
     * Updates the event code and resets the internal state of loaded alias mappings.
     *
     * @param eventCode the new FRC event code
     */
    public void setEventCode(String eventCode)
    {
        m_eventCode = eventCode;
        m_bTeamAliasesLoaded = false;
        m_teamToAliasMap.clear();
        m_aliasToTeamMap.clear();
    }

    /**
     * Reads the aliases JSON file from internal storage for the current event.
     *
     * @param context the context used to open the file and show messages
     * @param bSilent if true, error notifications are suppressed
     */
    public void readTeamAliasesJSON(Context context, boolean bSilent)
    {
        if (m_eventCode == null || m_eventCode.trim().isEmpty())
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
                super.displayToastMessages(context, TAG, "Successfully loaded team aliases for " + m_eventCode, false, null);
            }
            else
            {
                super.displayToastMessages(context, TAG, "Team aliases file not found for " + m_eventCode, bSilent, null);
            }
        }
        catch (JSONException | IOException e)
        {
            super.displayToastMessages(context, TAG, "Failed to parse team aliases for: " + m_eventCode, bSilent, e);
        }
    }

    /**
     * Parses a JSONArray of alias records into internal bidirectional maps.
     *
     * @param jsonArray the source JSONArray
     * @throws JSONException if the array format is invalid
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
     * Generates a filename for the team aliases record associated with an event.
     *
     * @param eventCode the FRC event code
     * @return the generated filename
     */
    private String getFilename(String eventCode)
    {
        return eventCode.trim().toLowerCase(Locale.US) + Constants.TEAM_ALIASES_FILENAME_SUFFIX;
    }

    /**
     * Saves a {@link JSONArray} of alias mappings to a JSON file in local storage.
     *
     * @param eventCode   the FRC event code
     * @param teamAliases the JSONArray containing mapping data to persist
     * @param bSilent     if true, error notifications are suppressed
     * @return true if the file was written successfully
     */
    public boolean writeTeamAliasesFile(String eventCode, JSONArray teamAliases, boolean bSilent)
    {
        if (eventCode == null || teamAliases == null)
        {
            Log.w(TAG, "Attempted to save team aliases with null eventCode or data");
            return false;
        }

        String aliasFilename = getFilename(eventCode);
        Log.d(TAG, "Writing team aliases to file for event: " + eventCode);
        Log.i(TAG, "Saving team aliases info for " + eventCode + " to: " + aliasFilename);

        try
        {
            File file = new File(m_dataDir, aliasFilename);
            saveJSONArray(file, teamAliases);
            return true;
        }
        catch (IOException e)
        {
            super.displayToastMessages(m_appContext, TAG, "Failed to write team aliases file for: " + eventCode, bSilent, e);
            return false;
        }
    }

    /**
     * Loads the team aliases JSON file for a specific event from local storage.
     *
     * @param eventCode the FRC event code
     * @return the loaded JSONArray, or null if the file is missing
     * @throws IOException   if reading the file fails
     * @throws JSONException if the file content is not a valid JSONArray
     */
    public JSONArray readTeamAliasesFile(String eventCode)
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
     * Deletes the local team aliases record associated with a specific event.
     *
     * @param eventCode the FRC event code
     * @return 1 if a file was deleted, 0 otherwise
     */
    public int deleteTeamAliasesFile(String eventCode)
    {
        Log.d(TAG, "Deleting team aliases file for event: " + eventCode);
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            Log.e(TAG, "Invalid event code: " + eventCode);
            return 0;
        }

        String filename = getFilename(eventCode);
        File file = new File(m_dataDir, filename);

        if (file.exists() && file.delete())
        {
            Log.i(TAG, "Successfully deleted aliases file: " + filename);
            return 1;
        }

        Log.w(TAG, "Failed to delete team aliases file: " + filename);
        return 0;
    }

    /**
     * Checks whether team alias mappings for the current event are currently held in memory.
     *
     * @return true if data is loaded
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isTeamAliasesLoaded()
    {
        return m_bTeamAliasesLoaded;
    }

    /**
     * Retrieves the mapped alias for a given real FRC team number.
     *
     * @param teamNumStr the team number (e.g., "2135")
     * @return the alias string (e.g. "9901"), or an empty string if no alias is defined
     */
    public String getAliasForTeamNum(String teamNumStr)
    {
        if (teamNumStr == null)
        {
            return "";
        }

        String targetTeamNum = MatchData.extractTeamNumber(teamNumStr);
        return m_teamToAliasMap.getOrDefault(targetTeamNum, "");
    }

    /**
     * Retrieves the real FRC team number associated with a given alias.
     *
     * @param myAlias the alias string (e.g., "9901")
     * @return the real team number (e.g., "2135"), or an empty string if the alias is unrecognized
     */
    public String getTeamNumForAlias(String myAlias)
    {
        if (myAlias == null)
        {
            return "";
        }

        return m_aliasToTeamMap.getOrDefault(myAlias, "");
    }

    /**
     * Resolves an FRC team identifier to its alias if a mapping exists and aliases are loaded.
     *
     * @param teamNum the raw team number string
     * @return the team alias if it exists, otherwise the original team identifier
     */
    public String resolveAlias(String teamNum)
    {
        if (teamNum == null || !isTeamAliasesLoaded())
        {
            return teamNum;
        }
        String alias = getAliasForTeamNum(teamNum);
        return alias.isEmpty() ? teamNum : alias;
    }

    /**
     * Resolves a possible alias (starting with "99") to its actual FRC team number.
     *
     * @param entry the entered team number or alias string
     * @return the actual team number if the entry is a recognized alias, otherwise the original entry
     */
    public String resolveTeamNumber(String entry)
    {
        if (entry == null || !isTeamAliasesLoaded() || !entry.startsWith("99"))
        {
            return entry;
        }
        String teamNum = getTeamNumForAlias(entry);
        return teamNum.isEmpty() ? entry : teamNum;
    }
}
