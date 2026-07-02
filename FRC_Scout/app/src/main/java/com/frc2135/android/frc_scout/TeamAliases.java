package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Singleton class for managing team aliases data.
 * Loads and parses team number to alias mapping from a JSON file.
 */
public class TeamAliases
{
    private static final String TAG = "TeamAliases";

    private String m_eventCode;
    private JSONArray m_jsonData;
    private boolean m_bAliasesDataLoaded;
    private final TeamAliasesSerializer m_serializer;

    private static volatile TeamAliases sTeamAliases;

    private TeamAliases(Context context, String eventCode)
    {
        Log.d(TAG, "TeamAliases constructor");
        m_eventCode = eventCode;
        m_bAliasesDataLoaded = false;
        m_jsonData = null;
        m_serializer = new TeamAliasesSerializer(context);
    }

    /**
     * Returns the singleton instance of TeamAliases.
     *
     * @param context      the context used for file operations
     * @param eventCode    the FRC event code
     * @param bForceReload whether to force a reload of the JSON data
     * @return the singleton TeamAliases instance
     */
    public static TeamAliases get(Context context, String eventCode, boolean bForceReload)
    {
        Log.d(TAG, "get()");
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
        if (m_jsonData == null || teamNumStr == null)
        {
            return "";
        }

        String targetTeamNum = MatchData.stripTeamNumPrefix(teamNumStr);

        for (int i = 0; i < m_jsonData.length(); i++)
        {
            JSONObject obj = m_jsonData.getJSONObject(i);
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
        if (m_jsonData == null || myAlias == null)
        {
            return "";
        }

        for (int i = 0; i < m_jsonData.length(); i++)
        {
            JSONObject obj = m_jsonData.getJSONObject(i);
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
        m_jsonData = null;
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
            m_jsonData = m_serializer.loadTeamAliases(m_eventCode);
            if (m_jsonData != null)
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

    public boolean isTeamAliasesLoaded()
    {
        return m_bAliasesDataLoaded;
    }
}

