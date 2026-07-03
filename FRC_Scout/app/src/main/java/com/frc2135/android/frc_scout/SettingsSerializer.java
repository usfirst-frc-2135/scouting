package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Serializer class for managing application settings persistence.
 */
public class SettingsSerializer extends BaseJSONSerializer
{
    private static final String TAG = "SettingsSerializer";
    private final String m_fileName;

    // Settings JSON Keys
    private static final String KEY_EVENT_CODE = "eventCode";
    private static final String KEY_PAST_SCOUTS = "pastScouts";
    private static final String KEY_SCOUT_NAME_PREFIX = "scoutName"; // Legacy key prefix
    private static final String KEY_TEAM_INDEX = "teamIndex";
    private static final String KEY_SCORING_TABLE_SIDE = "scoringTableSide";

    public SettingsSerializer(Context context, String fileName)
    {
        super(context);
        m_fileName = fileName;
        Log.d(TAG, "SettingsSerializer constructor");
    }

    public void saveSettings(Settings settings) throws JSONException, IOException
    {
        Log.d(TAG, "Saving settings configuration");
        JSONArray array = new JSONArray();
        array.put(serializeSettings(settings));

        File file = new File(m_dataDir, m_fileName);
        saveJSONArray(file, array);
    }

    public Settings loadSettings() throws IOException, JSONException
    {
        Log.d(TAG, "loadSettings()");
        File file = new File(m_dataDir, m_fileName);
        JSONArray array = loadJSONArray(file);
        if (array == null || array.length() == 0)
        {
            return null;
        }

        Settings settings = deserializeSettings(array.getJSONObject(0));
        Log.i(TAG, "Successfully loaded settings file");
        return settings;
    }

    public JSONObject serializeSettings(Settings s) throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(KEY_EVENT_CODE, s.getEventCode());

        JSONArray scoutsArray = new JSONArray();
        for (String name : s.getPastScouts())
        {
            scoutsArray.put(name);
        }
        json.put(KEY_PAST_SCOUTS, scoutsArray);

        json.put(KEY_TEAM_INDEX, s.getTeamIndexStr());
        json.put(KEY_SCORING_TABLE_SIDE, s.getScoringTableSide() ? 1 : 0);

        return json;
    }

    public Settings deserializeSettings(JSONObject json) throws JSONException
    {
        Settings s = new Settings();
        s.setEventCode(json.optString(KEY_EVENT_CODE, ""));

        if (json.has(KEY_PAST_SCOUTS))
        {
            JSONArray scoutsArray = json.getJSONArray(KEY_PAST_SCOUTS);
            for (int i = 0; i < scoutsArray.length(); i++)
            {
                s.addPastScoutNames(scoutsArray.getString(i));
            }
        }
        else
        {
            // Fallback to legacy format: scoutName0, scoutName1, ...
            int i = 0;
            while (json.has(KEY_SCOUT_NAME_PREFIX + i))
            {
                s.addPastScoutNames(json.getString(KEY_SCOUT_NAME_PREFIX + i));
                i++;
            }
        }

        s.setTeamIndexStr(json.optString(KEY_TEAM_INDEX, "0 - None"));
        int scoringTableSideVal = json.optInt(KEY_SCORING_TABLE_SIDE, 0);
        s.setScoringTableSide(scoringTableSideVal == 1);

        return s;
    }
}
