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
 * Singleton class for managing team aliases data.
 * Loads and parses team number to alias mapping from a JSON file.
 * Handles its own persistence by extending {@link BaseJSONSerializer}.
 */
public class TeamAliases extends BaseJSONSerializer
{
    private static final String TAG = "TeamAliases";
    private static final String TEAM_ALIASES_FILENAME_SUFFIX = "_teamAliases.json";
    private static final String TEAM_NUM_JSON_KEY = "teamNum";
    private static final String ALIAS_NUM_JSON_KEY = "aliasNum";

    private String m_eventCode;
    private JSONArray m_teamAliasesJSON;
    private boolean m_bTeamAliasesDataLoaded;

    private static volatile TeamAliases sTeamAliases;

    private TeamAliases(Context context, String eventCode)
    {
        super(context);
        Log.d(TAG, "TeamAliases constructor");
        m_eventCode = eventCode;
        m_bTeamAliasesDataLoaded = false;
        m_teamAliasesJSON = null;
    }

    /**
     * Returns the singleton instance of TeamAliases using the event code from Settings.
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
     * Returns the singleton instance of TeamAliases.
     *
     * @param context      the context used for file operations
     * @param eventCode    the FRC event code
     * @param bForceReload whether to force a reload of the JSON data
     * @return the singleton TeamAliases instance
     */
    public static TeamAliases getInstance(Context context, String eventCode, boolean bForceReload)
    {
        Log.d(TAG, "getInstance()");
        synchronized (TeamAliases.class)
        {
            if (sTeamAliases == null)
            {
                Log.d(TAG, "Creating a new sTeamAliases for eventCode " + eventCode);
                sTeamAliases = new TeamAliases(context, eventCode);
                sTeamAliases.readTeamAliasesJSON(context, true);
            }
            else
            {
                String oldEventCode = sTeamAliases.getEventCode();
                if (bForceReload || !oldEventCode.equalsIgnoreCase(eventCode))
                {
                    Log.d(TAG, "Resetting TeamAliases: " + oldEventCode + " -> " + eventCode);
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
        Log.d(TAG, "Clearing Team Aliases instance");
        sTeamAliases = null;
    }

    /**
     * Returns the event code associated with this alias mapping.
     *
     * @return the event code string
     */
    public String getEventCode()
    {
        return m_eventCode;
    }

    /**
     * Sets the event code and resets the loaded data state.
     *
     * @param eventCode the new FRC event code
     */
    public void setEventCode(String eventCode)
    {
        m_eventCode = eventCode;
        m_bTeamAliasesDataLoaded = false;
        m_teamAliasesJSON = null;
    }

    /**
     * Reads the aliases JSON file from internal storage.
     *
     * @param context the context used to open the file
     * @param bSilent if true, error Toast messages are suppressed
     */
    public void readTeamAliasesJSON(Context context, boolean bSilent)
    {
        if (m_eventCode == null || m_eventCode.trim().isEmpty())
        {
            return;
        }

        Log.d(TAG, "Looking for team aliases for: " + m_eventCode);

        try
        {
            m_teamAliasesJSON = readTeamAliasesFile(m_eventCode);
            if (m_teamAliasesJSON != null)
            {
                m_bTeamAliasesDataLoaded = true;

                Log.d(TAG, "Successfully loaded aliases for " + m_eventCode);
                Toast.makeText(context, "Loaded aliases for " + m_eventCode, Toast.LENGTH_SHORT).show();
            }
            else if (!bSilent)
            {
                Log.e(TAG, "Aliases file not found for event: " + m_eventCode);
                Toast.makeText(context, "Aliases file not found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException | IOException e)
        {
            handleError(context, "Failed to parse team aliases for: " + m_eventCode, bSilent, e);
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
        return eventCode.trim().toLowerCase(Locale.US) + TEAM_ALIASES_FILENAME_SUFFIX;
    }

    /**
     * Saves the provided JSONArray of aliases data to a JSON file on the device.
     *
     * @param eventCode   the FRC event code
     * @param teamAliases the JSONArray containing team-to-alias mapping data
     * @throws IOException if an error occurs during file writing
     */
    public void writeTeamAliasesFile(String eventCode, JSONArray teamAliases)
            throws IOException
    {
        Log.d(TAG, "Writing team aliases to file for event: " + eventCode);
        if (eventCode == null || teamAliases == null)
        {
            Log.w(TAG, "Attempted to save team aliases with null eventCode or data");
            return;
        }

        String aliasFilename = getFilename(eventCode);
        File file = new File(m_dataDir, aliasFilename);

        Log.d(TAG, "Saving team aliases for " + eventCode + " to: " + aliasFilename);
        saveJSONArray(file, teamAliases);
    }

    /**
     * Loads the team aliases from the specified file.
     *
     * @param eventCode the FRC event code
     * @return the loaded JSONArray, or null if the file doesn't exist
     * @throws IOException   if an error occurs during file reading
     * @throws JSONException if the file content is not a valid JSON array
     */
    public JSONArray readTeamAliasesFile(String eventCode)
            throws IOException, JSONException
    {
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            return null;
        }

        String filename = getFilename(eventCode);
        File file = new File(m_dataDir, filename);
        return loadJSONArray(file);
    }

    /**
     * Deletes the aliases file from internal storage.
     *
     * @param eventCode the FRC event code
     * @return 1 if successful, 0 otherwise
     */
    public int deleteTeamAliasesFile(String eventCode)
    {
        Log.d(TAG, "Deleting team aliases to file for event: " + eventCode);
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            Log.i(TAG, "Invalid event code: " + eventCode);
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
     * Checks whether team aliases have been successfully loaded from local storage.
     *
     * @return true if data is loaded
     */
    public boolean isTeamAliasesLoaded()
    {
        return m_bTeamAliasesDataLoaded;
    }

    /**
     * Returns the alias ("99" number) for the given team number.
     *
     * @param teamNumStr the team number (e.g., "2135")
     * @return the alias string, or empty string if not found
     * @throws JSONException if parsing the JSON fails
     */
    public String getAliasForTeamNum(String teamNumStr)
            throws JSONException
    {
        if (m_teamAliasesJSON == null || teamNumStr == null)
        {
            return "";
        }

        String targetTeamNum = MatchData.stripTeamNumPrefix(teamNumStr);

        for (int i = 0; i < m_teamAliasesJSON.length(); i++)
        {
            JSONObject obj = m_teamAliasesJSON.getJSONObject(i);
            if (targetTeamNum.equals(obj.optString(TEAM_NUM_JSON_KEY)))
            {
                return obj.optString(ALIAS_NUM_JSON_KEY, "");
            }
        }
        return "";
    }

    /**
     * Returns the actual team number for the given alias ("99" number).
     *
     * @param myAlias the alias string (e.g., "9901")
     * @return the team number string, or empty string if not found
     * @throws JSONException if parsing the JSON fails
     */
    public String getTeamNumForAlias(String myAlias)
            throws JSONException
    {
        if (m_teamAliasesJSON == null || myAlias == null)
        {
            return "";
        }

        for (int i = 0; i < m_teamAliasesJSON.length(); i++)
        {
            JSONObject obj = m_teamAliasesJSON.getJSONObject(i);
            if (myAlias.equals(obj.optString(ALIAS_NUM_JSON_KEY)))
            {
                return obj.optString(TEAM_NUM_JSON_KEY, "");
            }
        }
        return "";
    }
}
