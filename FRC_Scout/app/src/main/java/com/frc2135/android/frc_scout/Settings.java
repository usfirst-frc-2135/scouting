package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton class for managing application settings, including past scouts, team index, and match numbers.
 * Settings are persisted via a JSON file.
 */
public class Settings
{
    private static final String TAG = "Settings";
    private static final String FILENAME = "settings.json";

    // JSON Keys
    private static final String KEY_PAST_SCOUTS = "pastScouts";
    private static final String KEY_SCOUT_NAME_PREFIX = "scoutName"; // Legacy key prefix
    private static final String KEY_TEAM_INDEX = "teamIndex";
    private static final String KEY_SCORING_TABLE_SIDE = "scoringTableSide";

    private final List<String> m_pastScouts;
    private String m_teamIndexStr;
    private boolean m_scoringTableSide;
    private String m_mostRecentScoutName;
    private String m_mostRecentMatchNumber;

    private static volatile Settings sSettings;

    /**
     * Returns the singleton instance of Settings.
     *
     * @param context the context used to initialize the instance
     * @return the singleton Settings instance
     */
    public static Settings get(Context context)
    {
        if (sSettings == null)
        {
            synchronized (Settings.class)
            {
                if (sSettings == null)
                {
                    sSettings = new Settings(context);
                }
            }
        }
        return sSettings;
    }

    private Settings(Context context)
    {
        m_pastScouts = new ArrayList<>();
        m_teamIndexStr = "0 - None";
        m_scoringTableSide = false;
        m_mostRecentScoutName = "";
        m_mostRecentMatchNumber = "";

        MatchDataSerializer serializer = new MatchDataSerializer(context.getApplicationContext(), FILENAME);

        try
        {
            Log.d(TAG, "Loading settings file");
            Settings settings = serializer.loadScoutNames();
            if (settings != null)
            {
                Collections.addAll(m_pastScouts, settings.getPastScouts());
                m_teamIndexStr = settings.getTeamIndexStr();
                m_scoringTableSide = settings.getScoringTableSide();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error loading settings file: ", e);
        }
    }

    /**
     * Constructs a Settings object from a JSONObject.
     * Handles both current (JSONArray) and legacy (individual keys) formats for scout names.
     *
     * @param json the JSONObject containing settings data
     */
    public Settings(JSONObject json)
    {
        Log.d(TAG, "Settings object being created from JSON data");
        m_pastScouts = new ArrayList<>();
        try
        {
            if (json.has(KEY_PAST_SCOUTS))
            {
                JSONArray scoutsArray = json.getJSONArray(KEY_PAST_SCOUTS);
                for (int i = 0; i < scoutsArray.length(); i++)
                {
                    m_pastScouts.add(scoutsArray.getString(i));
                }
            }
            else
            {
                // Fallback to legacy format: scoutName0, scoutName1, ...
                int i = 0;
                while (json.has(KEY_SCOUT_NAME_PREFIX + i))
                {
                    m_pastScouts.add(json.getString(KEY_SCOUT_NAME_PREFIX + i));
                    i++;
                }
            }

            setTeamIndexStr(json.optString(KEY_TEAM_INDEX, "0 - None"));
            int scoringTableSideVal = json.optInt(KEY_SCORING_TABLE_SIDE, 0);
            setScoringTableSide(scoringTableSideVal == 1);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error loading settings from JSON: ", e);
        }
    }

    /**
     * Adds a scout name to the list of past scouts if it doesn't already exist.
     *
     * @param name the scout name to add
     */
    public void addPastScoutNames(String name)
    {
        if (name == null || name.trim().isEmpty())
        {
            return;
        }

        String trimmedName = name.trim();
        for (String existingName : m_pastScouts)
        {
            if (trimmedName.equalsIgnoreCase(existingName.trim()))
            {
                return;
            }
        }
        m_pastScouts.add(trimmedName);
    }

    public String getMostRecentMatchNumber()
    {
        return m_mostRecentMatchNumber;
    }

    public void setMostRecentMatchNumber(String value)
    {
        m_mostRecentMatchNumber = (value != null) ? value : "";
    }

    /**
     * Calculates the next expected match number by incrementing the numeric portion.
     *
     * @return the next match number string, or the original if it contains no digits
     */
    public String getNextExpectedMatchNumber()
    {
        if (m_mostRecentMatchNumber == null || m_mostRecentMatchNumber.isEmpty())
        {
            return "";
        }

        StringBuilder prefix = new StringBuilder();
        StringBuilder numStr = new StringBuilder();

        for (int i = 0; i < m_mostRecentMatchNumber.length(); i++)
        {
            char c = m_mostRecentMatchNumber.charAt(i);
            if (Character.isDigit(c))
            {
                numStr.append(c);
            }
            else
            {
                prefix.append(c);
            }
        }

        //noinspection SizeReplaceableByIsEmpty
        if (numStr.length() == 0)
        {
            return m_mostRecentMatchNumber;
        }

        try
        {
            int newNum = Integer.parseInt(numStr.toString()) + 1;
            return prefix.toString() + newNum;
        }
        catch (NumberFormatException e)
        {
            Log.e(TAG, "Failed to parse match number: " + numStr, e);
            return m_mostRecentMatchNumber;
        }
    }

    public String getTeamIndexStr()
    {
        return m_teamIndexStr;
    }

    /**
     * Returns "red", "blue", or empty string based on the current team index.
     *
     * @return the team color string
     */
    public String getTeamIndexColor()
    {
        if (m_teamIndexStr == null)
        {
            return "";
        }

        return switch (m_teamIndexStr)
        {
            case "1", "2", "3" -> "red";
            case "4", "5", "6" -> "blue";
            default -> "";
        };
    }

    /**
     * Returns a descriptive string for the current team index (e.g. "1 - Red 1").
     *
     * @return the team index description string
     */
    @SuppressWarnings("unused")
    public String getTeamIndexDescription()
    {
        if (m_teamIndexStr == null || m_teamIndexStr.equals("0 - None"))
        {
            return "0 - None";
        }

        return switch (m_teamIndexStr)
        {
            case "1" -> "1 - Red 1";
            case "2" -> "2 - Red 2";
            case "3" -> "3 - Red 3";
            case "4" -> "4 - Blue 1";
            case "5" -> "5 - Blue 2";
            case "6" -> "6 - Blue 3";
            default -> "0 - None";
        };
    }

    public void setTeamIndexStr(String indexStr)
    {
        m_teamIndexStr = (indexStr != null) ? indexStr : "0 - None";
    }

    /**
     * Returns true if given indexStr is a valid team number: 1, 2, 3, 4, 5, or 6.
     */
    public boolean isValidTeamIndexNum(String indexStr)
    {
        if (indexStr == null)
        {
            return false;
        }
        return indexStr.matches("[1-6]");
    }

    /**
     * Returns true if given indexStr is valid: None, 1, 2, 3, 4, 5, or 6.
     */
    @SuppressWarnings("unused")
    public boolean isValidTeamIndexStr(String indexStr)
    {
        return "0 - None".equals(indexStr) || isValidTeamIndexNum(indexStr);
    }

    public boolean getScoringTableSide()
    {
        return m_scoringTableSide;
    }

    public void setScoringTableSide(boolean val)
    {
        m_scoringTableSide = val;
    }

    public String getMostRecentScoutName()
    {
        return m_mostRecentScoutName;
    }

    public void setMostRecentScoutName(String name)
    {
        m_mostRecentScoutName = (name != null) ? name : "";
    }

    public String[] getPastScouts()
    {
        return m_pastScouts.toArray(new String[0]);
    }

    public void clear()
    {
        m_pastScouts.clear();
    }

    /**
     * Serializes settings to a JSONObject.
     *
     * @return the serialized JSONObject
     * @throws JSONException if JSON creation fails
     */
    public JSONObject toJSON()
            throws JSONException
    {
        JSONObject json = new JSONObject();

        JSONArray scoutsArray = new JSONArray();
        for (String name : m_pastScouts)
        {
            scoutsArray.put(name);
        }
        json.put(KEY_PAST_SCOUTS, scoutsArray);

        json.put(KEY_TEAM_INDEX, m_teamIndexStr);
        json.put(KEY_SCORING_TABLE_SIDE, m_scoringTableSide ? 1 : 0);

        Log.d(TAG, "Serialized settings to JSON: " + json);
        return json;
    }
}
