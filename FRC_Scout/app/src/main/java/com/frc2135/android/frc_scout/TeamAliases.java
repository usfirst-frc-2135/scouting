package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Singleton class for managing team aliases data.
 * Loads and parses team number to alias mapping from a JSON file.
 * Handles its own persistence by extending {@link BaseJSONSerializer}.
 */
public class TeamAliases extends BaseJSONSerializer
{
    private static final String TAG = "TeamAliases";
    private static final String FILENAME_SUFFIX = "_teamAliases.json";

    private String m_eventCode;
    private JSONArray m_aliasesJSON;
    private boolean m_bAliasesDataLoaded;

    private static volatile TeamAliases sTeamAliases;

    private TeamAliases(Context context, String eventCode)
    {
        super(context);
        Log.d(TAG, "TeamAliases constructor");
        m_eventCode = eventCode;
        m_bAliasesDataLoaded = false;
        m_aliasesJSON = null;
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
                sTeamAliases.readAliasesJSON(context, true);
            }
            else
            {
                String oldEventCode = sTeamAliases.getEventCode();
                if (bForceReload || !oldEventCode.equalsIgnoreCase(eventCode))
                {
                    Log.d(TAG, "Resetting TeamAliases: " + oldEventCode + " -> " + eventCode);
                    sTeamAliases.setEventCode(eventCode);
                    sTeamAliases.readAliasesJSON(context, true);
                }
            }
            return sTeamAliases;
        }
    }

    @SuppressWarnings("unused")
    public static void clear()
    {
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
     * Returns the alias ("99" number) for the given team number.
     *
     * @param teamNumStr the team number (e.g., "2135")
     * @return the alias string, or empty string if not found
     * @throws JSONException if parsing the JSON fails
     */
    public String getAliasForTeamNum(String teamNumStr)
            throws JSONException
    {
        if (m_aliasesJSON == null || teamNumStr == null)
        {
            return "";
        }

        String targetTeamNum = MatchData.stripTeamNumPrefix(teamNumStr);

        for (int i = 0; i < m_aliasesJSON.length(); i++)
        {
            JSONObject obj = m_aliasesJSON.getJSONObject(i);
            if (targetTeamNum.equals(obj.optString("teamNum")))
            {
                return obj.optString("aliasNum", "");
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
        if (m_aliasesJSON == null || myAlias == null)
        {
            return "";
        }

        for (int i = 0; i < m_aliasesJSON.length(); i++)
        {
            JSONObject obj = m_aliasesJSON.getJSONObject(i);
            if (myAlias.equals(obj.optString("aliasNum")))
            {
                return obj.optString("teamNum", "");
            }
        }
        return "";
    }

    public void setEventCode(String eventCode)
    {
        m_eventCode = eventCode;
        m_bAliasesDataLoaded = false;
        m_aliasesJSON = null;
    }

    /**
     * Reads the aliases JSON file from internal storage.
     *
     * @param context the context used to open the file
     * @param bSilent if true, error Toast messages are suppressed
     */
    public void readAliasesJSON(Context context, boolean bSilent)
    {
        if (m_eventCode == null || m_eventCode.trim().isEmpty())
        {
            return;
        }

        Log.d(TAG, "Looking for team aliases for: " + m_eventCode);

        try
        {
            m_aliasesJSON = loadTeamAliases(m_eventCode);
            if (m_aliasesJSON != null)
            {
                m_bAliasesDataLoaded = true;
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
            Log.e(TAG, "Error reading aliases file", e);
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
        return eventCode.trim().toLowerCase() + FILENAME_SUFFIX;
    }

    /**
     * Saves the provided JSONArray of aliases data to a JSON file on the device.
     *
     * @param eventCode the FRC event code
     * @param aliasData the JSONArray containing team-to-alias mapping data
     * @throws IOException if an error occurs during file writing
     */
    public void saveTeamAliases(String eventCode, JSONArray aliasData)
            throws IOException
    {
        if (eventCode == null || aliasData == null)
        {
            Log.w(TAG, "Attempted to save aliases with null eventCode or data");
            return;
        }

        String filename = getFilename(eventCode);
        Log.d(TAG, "Saving aliases info to: " + filename);
        File file = new File(m_dataDir, filename);
        saveJSONArray(file, aliasData);
    }

    /**
     * Loads the team aliases from the specified file.
     *
     * @param eventCode the FRC event code
     * @return the loaded JSONArray, or null if the file doesn't exist
     * @throws IOException   if an error occurs during file reading
     * @throws JSONException if the file content is not a valid JSON array
     */
    public JSONArray loadTeamAliases(String eventCode)
            throws IOException, JSONException
    {
        if (eventCode == null)
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
     */
    public void deleteTeamAliases(String eventCode)
    {
        if (eventCode == null)
        {
            return;
        }

        String filename = getFilename(eventCode);
        File file = new File(m_dataDir, filename);
        if (file.exists())
        {
            boolean deleted = file.delete();
            if (deleted)
            {
                Log.i(TAG, "Successfully deleted aliases file: " + filename);
            }
            else
            {
                Log.w(TAG, "Failed to delete aliases file: " + filename);
            }
        }
    }

    /**
     * Checks whether team aliases have been successfully loaded from local storage.
     *
     * @return true if data is loaded
     */
    public boolean isTeamAliasesLoaded()
    {
        return m_bAliasesDataLoaded;
    }
}
