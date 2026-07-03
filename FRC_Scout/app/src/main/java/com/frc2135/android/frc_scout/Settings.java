package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for managing application settings, including past scouts, team index, and match numbers.
 * Settings are persisted via a JSON file.
 * Handles its own persistence by extending {@link BaseJSONSerializer}.
 */
public class Settings extends BaseJSONSerializer
{
    private static final String TAG = "Settings";
    private static final String FILENAME = "settings.json";

    // Settings JSON Keys
    private static final String KEY_EVENT_CODE = "eventCode";
    private static final String KEY_PAST_SCOUTS = "pastScouts";
    private static final String KEY_SCOUT_NAME_PREFIX = "scoutName"; // Legacy key prefix
    private static final String KEY_TEAM_INDEX = "teamIndex";
    private static final String KEY_SCORING_TABLE_SIDE = "scoringTableSide";
    private static final String KEY_MOST_RECENT_SCOUT_NAME = "mostRecentScoutName";
    private static final String KEY_MOST_RECENT_MATCH_NUMBER = "mostRecentMatchNumber";

    private final List<String> m_pastScouts;
    private final List<String> m_eventScoutNames;
    private String m_eventCode;
    private String m_teamIndexStr;
    private boolean m_scoringTableSide;
    private String m_mostRecentScoutName;
    private String m_mostRecentMatchNumber;
    private boolean m_bEventScoutNamesLoaded;

    private static volatile Settings sSettings;

    /**
     * Returns the singleton instance of Settings.
     *
     * @param context the context used to initialize the instance
     * @return the singleton Settings instance
     */
    public static Settings getInstance(Context context)
    {
        Log.d(TAG, "getInstance()");
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
        super(context);
        Log.d(TAG, "Settings constructor");
        m_pastScouts = new ArrayList<>();
        m_eventScoutNames = new ArrayList<>();
        m_eventCode = "";
        m_teamIndexStr = "0 - None";
        m_scoringTableSide = false;
        m_mostRecentScoutName = "";
        m_mostRecentMatchNumber = "";
        m_bEventScoutNamesLoaded = false;

        try
        {
            loadSettings();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error loading settings file: ", e);
        }
    }

    /**
     * Default constructor for creating a new Settings object (internal use for deserialization).
     */
    public Settings()
    {
        super(); // Internal use, no file I/O context needed
        Log.d(TAG, "Settings default constructor");
        m_pastScouts = new ArrayList<>();
        m_eventScoutNames = new ArrayList<>();
        m_eventCode = "";
        m_teamIndexStr = "0 - None";
        m_scoringTableSide = false;
        m_mostRecentScoutName = "";
        m_mostRecentMatchNumber = "";
        m_bEventScoutNamesLoaded = false;
    }

    /**
     * Saves the current settings configuration to internal storage.
     *
     * @throws JSONException if configuration data serialization fails
     * @throws IOException   if writing the settings file fails
     */
    public void saveSettings()
            throws JSONException, IOException
    {
        Log.d(TAG, "saveSettings()");
        JSONArray array = new JSONArray();
        array.put(toJSON());

        File file = new File(m_dataDir, FILENAME);
        saveJSONArray(file, array);
    }

    /**
     * Loads the application settings from internal storage.
     *
     * @throws IOException   if reading the file fails
     * @throws JSONException if parsing the JSON fails
     */
    public void loadSettings()
            throws IOException, JSONException
    {
        Log.d(TAG, "loadSettings()");
        File file = new File(m_dataDir, FILENAME);
        JSONArray array = loadJSONArray(file);
        if (array != null && array.length() > 0)
        {
            fromJSON(array.getJSONObject(0));
            Log.i(TAG, "Successfully loaded settings file");
        }
    }

    /**
     * Populates this Settings object from a {@link JSONObject}.
     *
     * @param json the source JSONObject
     * @throws JSONException if parsing fails
     */
    public void fromJSON(JSONObject json)
            throws JSONException
    {
        m_eventCode = json.optString(KEY_EVENT_CODE, "");

        if (json.has(KEY_PAST_SCOUTS))
        {
            JSONArray scoutsArray = json.getJSONArray(KEY_PAST_SCOUTS);
            m_pastScouts.clear();
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

        m_teamIndexStr = json.optString(KEY_TEAM_INDEX, "0 - None");
        int scoringTableSideVal = json.optInt(KEY_SCORING_TABLE_SIDE, 0);
        m_scoringTableSide = (scoringTableSideVal == 1);
        m_mostRecentScoutName = json.optString(KEY_MOST_RECENT_SCOUT_NAME, "");
        m_mostRecentMatchNumber = json.optString(KEY_MOST_RECENT_MATCH_NUMBER, "");
    }

    /**
     * Serializes settings to a {@link JSONObject}.
     *
     * @return the serialized JSONObject
     * @throws JSONException if JSON creation fails
     */
    public JSONObject toJSON()
            throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(KEY_EVENT_CODE, m_eventCode);

        JSONArray scoutsArray = new JSONArray();
        for (String name : m_pastScouts)
        {
            scoutsArray.put(name);
        }
        json.put(KEY_PAST_SCOUTS, scoutsArray);

        json.put(KEY_TEAM_INDEX, m_teamIndexStr);
        json.put(KEY_SCORING_TABLE_SIDE, m_scoringTableSide ? 1 : 0);
        json.put(KEY_MOST_RECENT_SCOUT_NAME, m_mostRecentScoutName);
        json.put(KEY_MOST_RECENT_MATCH_NUMBER, m_mostRecentMatchNumber);

        return json;
    }

    /**
     * Saves the settings to disk, swallowing and logging any exceptions.
     */
    private void saveSettingsSilent()
    {
        try
        {
            saveSettings();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to auto-save settings: ", e);
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
        saveSettingsSilent();
    }

    public String getMostRecentMatchNumber()
    {
        return m_mostRecentMatchNumber;
    }

    public void setEventCode(String eventCode)
    {
        m_eventCode = (eventCode != null) ? eventCode : "";
        saveSettingsSilent();
    }

    public String getEventCode()
    {
        return m_eventCode;
    }

    public void setMostRecentMatchNumber(String value)
    {
        m_mostRecentMatchNumber = (value != null) ? value : "";
        saveSettingsSilent();
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

    /**
     * Returns the current team index string (e.g. "1").
     *
     * @return the team index string
     */
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

    /**
     * Sets the team index string (e.g. "1").
     *
     * @param indexStr the team index string
     */
    public void setTeamIndexStr(String indexStr)
    {
        m_teamIndexStr = (indexStr != null) ? indexStr : "0 - None";
        saveSettingsSilent();
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

    @SuppressWarnings("unused")
    public boolean getScoringTableSide()
    {
        return m_scoringTableSide;
    }

    @SuppressWarnings("unused")
    public void setScoringTableSide(boolean val)
    {
        m_scoringTableSide = val;
        saveSettingsSilent();
    }

    public String getMostRecentScoutName()
    {
        return m_mostRecentScoutName;
    }

    /**
     * Sets the most recent scout name used in the application.
     *
     * @param name the scout name
     */
    public void setMostRecentScoutName(String name)
    {
        m_mostRecentScoutName = (name != null) ? name : "";
        saveSettingsSilent();
    }

    /**
     * Returns the array of unique scout names entered by the user.
     *
     * @return an array of scout names
     */
    public String[] getPastScouts()
    {
        return m_pastScouts.toArray(new String[0]);
    }

    @SuppressWarnings("unused")
    public void clear()
    {
        m_pastScouts.clear();
    }

    /**
     * Loads event-specific scout names from local storage.
     *
     * @param context     the context for file operations
     * @param eventCode   the FRC event code
     * @param forceReload if true, forces a reload even if already loaded for this event
     */
    public void loadEventScoutNames(Context context, String eventCode, boolean forceReload)
    {
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            return;
        }

        if (!forceReload && m_bEventScoutNamesLoaded && m_eventCode != null && m_eventCode.equalsIgnoreCase(eventCode))
        {
            return;
        }

        Log.d(TAG, "Loading scout names for event: " + eventCode);
        ScoutNames scoutNames = ScoutNames.getInstance(context, eventCode, forceReload);
        m_eventScoutNames.clear();
        if (scoutNames != null && scoutNames.isScoutNamesLoaded())
        {
            m_eventScoutNames.addAll(scoutNames.getScoutNames());
            m_bEventScoutNamesLoaded = true;
            Log.d(TAG, "Successfully loaded " + m_eventScoutNames.size() + " event scout names");
        }
        else
        {
            m_bEventScoutNamesLoaded = false;
            Log.d(TAG, "No event scout names loaded for: " + eventCode);
        }
    }

    /**
     * Saves event-specific scout names to local storage.
     *
     * @param context   the context for file operations
     * @param eventCode the FRC event code
     * @param scoutData the JSONArray of scout names
     * @throws IOException if saving fails
     */
    @SuppressWarnings("unused")
    public void saveEventScoutNames(Context context, String eventCode, JSONArray scoutData)
            throws IOException
    {
        ScoutNames scoutNames = ScoutNames.getInstance(context, eventCode, true);
        scoutNames.deleteScoutNames(eventCode);
        scoutNames.saveScoutNames(eventCode, scoutData);
        loadEventScoutNames(context, eventCode, true);
    }

    /**
     * Returns a combined list of all known scout names (past scouts + current event scouts).
     *
     * @return a list of unique scout names
     */
    @SuppressWarnings("unused")
    public List<String> getAllScoutNames()
    {
        List<String> allNames = new ArrayList<>(m_eventScoutNames);
        for (String name : m_pastScouts)
        {
            if (!allNames.contains(name))
            {
                allNames.add(name);
            }
        }
        return allNames;
    }

    @SuppressWarnings("unused")
    public boolean isEventScoutNamesLoaded()
    {
        return m_bEventScoutNamesLoaded;
    }
}
