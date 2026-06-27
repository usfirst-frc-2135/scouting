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
 * Singleton class for managing competition event data.
 * Loads and parses match information from a JSON file specific to an event code.
 */
public class CompetitionInfo
{
    private static final String TAG = "CompetitionInfo";

    // JSON Keys
    private static final String KEY_ALLIANCES = "alliances";
    private static final String KEY_BLUE = "blue";
    private static final String KEY_RED = "red";
    private static final String KEY_TEAM_KEYS = "team_keys";
    private static final String KEY_COMP_LEVEL = "comp_level";
    private static final String KEY_MATCH_NUMBER = "match_number";

    // Data members
    private String m_eventCode;
    private JSONArray m_jsonData;
    private boolean m_bEventDataLoaded;

    private static volatile CompetitionInfo sCompetitionInfo;

    private CompetitionInfo(String eventCode)
    {
        m_eventCode = eventCode;
        m_bEventDataLoaded = false;
        m_jsonData = null;
    }

    /**
     * Returns the singleton instance of CompetitionInfo.
     * If the event code changes or a reload is forced, the data is reloaded.
     *
     * @param context the context used for file operations and Toast messages
     * @param eventCode the FRC event code
     * @param bForceReload whether to force a reload of the JSON data
     * @return the singleton CompetitionInfo instance
     */
    public static CompetitionInfo get(Context context, String eventCode, boolean bForceReload)
    {
        synchronized (CompetitionInfo.class)
        {
            if (sCompetitionInfo == null)
            {
                Log.d(TAG, "Creating a new sCompetitionInfo for eventCode " + eventCode);
                sCompetitionInfo = new CompetitionInfo(eventCode);
                sCompetitionInfo.readEventMatchesJSON(context, true);
            }
            else
            {
                String oldEventCode = sCompetitionInfo.getEventCode();
                if (bForceReload || !oldEventCode.equals(eventCode))
                {
                    Log.d(TAG, "Resetting CompetitionInfo: " + oldEventCode + " -> " + eventCode);
                    sCompetitionInfo.setEventCode(eventCode);
                    sCompetitionInfo.readEventMatchesJSON(context, true);
                }
            }
            return sCompetitionInfo;
        }
    }

    /**
     * Clears the singleton instance of CompetitionInfo.
     */
    public static void clear()
    {
        if (sCompetitionInfo != null)
        {
            Log.d(TAG, "Deleting existing sCompetitionInfo");
            sCompetitionInfo = null;
        }
        else
        {
            Log.d(TAG, "No action needed: no existing sCompetitionInfo");
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
    public void setEventCode(String eventCode)
    {
        m_eventCode = eventCode;
        m_bEventDataLoaded = false;
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

        String filename = m_eventCode.trim().toLowerCase() + "matches.json";
        File file = new File(context.getFilesDir(), filename);
        Log.d(TAG, "Looking for matches JSON file: " + file.getAbsolutePath());

        if (file.exists())
        {
            Log.d(TAG, "Attempting to read matches JSON file");
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
                m_bEventDataLoaded = true;

                String msg = "Successfully read " + m_eventCode + " matches file";
                Log.d(TAG, msg);
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
            catch (FileNotFoundException err)
            {
                if (!bSilent)
                {
                    String errMsg = "ERROR reading event " + m_eventCode + " matches file: " + err.getMessage();
                    Log.e(TAG, errMsg);
                    Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show();
                }
            }
            catch (JSONException | IOException e)
            {
                Log.e(TAG, "ERROR reading event matches file: " + e.getMessage());
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        else
        {
            Log.d(TAG, "Matches file does not exist: " + filename);
        }
    }

    public boolean isEventDataLoaded()
    {
        return m_bEventDataLoaded;
    }

    /**
     * Retrieves the team keys for a specific match.
     *
     * @param matchNum the match identifier (e.g., "qm1")
     * @return an array of 7 strings: index 0 is a placeholder, 1-3 are Red teams, 4-6 are Blue teams
     * @throws JSONException if parsing the JSON fails
     */
    public String[] getTeams(String matchNum) throws JSONException
    {
        String[] teams = new String[7];
        for (int i = 0; i < 7; i++) teams[i] = "";

        if (m_jsonData != null && matchNum != null)
        {
            String targetMatch = matchNum.trim().toLowerCase();
            for (int i = 0; i < m_jsonData.length(); i++)
            {
                JSONObject matchObj = m_jsonData.getJSONObject(i);
                String compLevel = matchObj.optString(KEY_COMP_LEVEL);
                String matchNumber = matchObj.optString(KEY_MATCH_NUMBER);

                if ((compLevel + matchNumber).equals(targetMatch))
                {
                    JSONObject alliances = matchObj.getJSONObject(KEY_ALLIANCES);
                    
                    JSONArray blueTeams = alliances.getJSONObject(KEY_BLUE).getJSONArray(KEY_TEAM_KEYS);
                    JSONArray redTeams = alliances.getJSONObject(KEY_RED).getJSONArray(KEY_TEAM_KEYS);

                    teams[0] = "No team selected";
                    for (int j = 0; j < 3; j++)
                    {
                        teams[j + 1] = redTeams.optString(j, "");
                        teams[j + 4] = blueTeams.optString(j, "");
                    }
                    return teams;
                }
            }
            Log.d(TAG, "getTeams(): matchNum '" + matchNum + "' NOT found!");
        }
        return teams;
    }
}
