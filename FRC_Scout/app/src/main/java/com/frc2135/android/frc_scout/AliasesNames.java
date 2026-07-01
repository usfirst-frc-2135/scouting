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

/**
 * Singleton class for managing team aliases data.
 * Loads and parses team number to alias mapping from a JSON file.
 */
public class AliasesNames
{
    private static final String TAG = "AliasesNames";

    private String m_eventCode;
    private JSONArray m_jsonData;
    private boolean m_bAliasesDataLoaded;

    private static volatile AliasesNames sAliasesInfo;

    private AliasesNames(String eventCode)
    {
        m_eventCode = eventCode;
        m_bAliasesDataLoaded = false;
        m_jsonData = null;
    }

    /**
     * Returns the singleton instance of AliasesInfo.
     *
     * @param context      the context used for file operations
     * @param eventCode    the FRC event code
     * @param bForceReload whether to force a reload of the JSON data
     * @return the singleton AliasesInfo instance
     */
    public static AliasesNames get(Context context, String eventCode, boolean bForceReload)
    {
        synchronized (AliasesNames.class)
        {
            if (sAliasesInfo == null)
            {
                Log.d(TAG, "Creating a new sAliasesInfo for eventCode " + eventCode);
                sAliasesInfo = new AliasesNames(eventCode);
                sAliasesInfo.readAliasesJSON(context, true);
            }
            else
            {
                String oldEventCode = sAliasesInfo.getEventCode();
                if (bForceReload || !oldEventCode.equalsIgnoreCase(eventCode))
                {
                    Log.d(TAG, "Resetting AliasesInfo: " + oldEventCode + " -> " + eventCode);
                    sAliasesInfo.setEventCode(eventCode);
                    sAliasesInfo.readAliasesJSON(context, true);
                }
            }
            return sAliasesInfo;
        }
    }

    @SuppressWarnings("unused")
    public static void clear()
    {
        sAliasesInfo = null;
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

        String filename = m_eventCode.trim().toLowerCase() + "_aliases.json";
        File file = new File(context.getFilesDir(), filename);
        Log.d(TAG, "Looking for aliases file: " + file.getAbsolutePath());

        if (file.exists())
        {
            try (InputStream in = context.openFileInput(filename);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in)))
            {
                StringBuilder jsonString = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                {
                    jsonString.append(line);
                }

                m_jsonData = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
                m_bAliasesDataLoaded = true;
                Log.d(TAG, "Successfully loaded aliases for " + m_eventCode);
                Toast.makeText(context, "Loaded aliases for " + m_eventCode, Toast.LENGTH_SHORT).show();
            }
            catch (FileNotFoundException e)
            {
                if (!bSilent)
                {
                    Log.e(TAG, "Aliases file not found: " + filename);
                    Toast.makeText(context, "Aliases file not found", Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException | IOException e)
            {
                Log.e(TAG, "Error reading aliases file", e);
            }
        }
        else
        {
            Log.d(TAG, "Aliases file does not exist: " + filename);
        }
    }

    public boolean isAliasesInfoLoaded()
    {
        return m_bAliasesDataLoaded;
    }
}
