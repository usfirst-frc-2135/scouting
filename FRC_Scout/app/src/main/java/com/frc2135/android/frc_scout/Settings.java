package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Singleton class for managing application settings, including past scouts, team index, and match numbers.
 * Settings are persisted via a JSON file.
 * Handles its own persistence by extending {@link BaseJSONSerializer}.
 */
public final class Settings extends BaseJSONSerializer
{
    private static final String TAG = "Settings";
    private static final String SETTINGS_FILENAME = "settings.json";

    // Settings JSON Keys
    private static final String KEY_SETTINGS_VERSION = "settingsVersion";
    private static final String KEY_LAST_UPDATED = "lastUpdated";
    private static final String KEY_EVENT_CODE = "eventCode";
    private static final String KEY_TEAM_INDEX = "teamIndex";
    private static final String KEY_MOST_RECENT_MATCH_NUMBER = "mostRecentMatchNumber";
    private static final String KEY_PAST_SCOUTS = "pastScouts";
    private static final String KEY_SCOUT_NAME_PREFIX = "scoutName"; // Legacy key prefix
    private static final String KEY_MOST_RECENT_SCOUT_NAME = "mostRecentScoutName";
    private static final String KEY_SCORING_TABLE_SIDE = "scoringTableSide";

    private static final String DEFAULT_EVENT_CODE = "EVTX";

    private final List<String> m_pastScouts;
    private String m_eventCode;
    private String m_teamIndexStr;
    private String m_mostRecentMatchNumber;
    private String m_mostRecentScoutName;
    private final List<String> m_eventScoutNames;
    private boolean m_bEventScoutNamesLoaded;
    private boolean m_scoringTableSide;
    private final String[] m_teamIndexOptions;

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
        m_eventCode = DEFAULT_EVENT_CODE;
        m_teamIndexOptions = new String[]{
                context.getString(R.string.team_index_none),
                context.getString(R.string.team_index_1),  // red teams
                context.getString(R.string.team_index_2),
                context.getString(R.string.team_index_3),
                context.getString(R.string.team_index_4),  // blue teams
                context.getString(R.string.team_index_5),
                context.getString(R.string.team_index_6)
        };
        m_teamIndexStr = m_teamIndexOptions[0];
        m_mostRecentMatchNumber = "";
        m_pastScouts = new ArrayList<>();
        m_mostRecentScoutName = "";
        m_eventScoutNames = new ArrayList<>();
        m_bEventScoutNamesLoaded = false;
        m_scoringTableSide = false;

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
     * Saves the current settings configuration to internal storage.
     *
     * @throws JSONException if configuration data serialization fails
     * @throws IOException   if writing the settings file fails
     */
    public void saveSettings()
            throws JSONException, IOException
    {
        Log.d(TAG, "saveSettings()");
        File file = new File(m_dataDir, SETTINGS_FILENAME);
        saveJSONObject(file, toJSON());
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
        File file = new File(m_dataDir, SETTINGS_FILENAME);
        if (!file.exists())
        {
            return;
        }

        // Use the base class method to load the root object
        JSONObject json = loadJSONObject(file);
        if (json != null)
        {
            fromJSON(json);
            Log.i(TAG, "Successfully loaded settings from JSONObject format");
            return;
        }

        // Fallback for legacy format (wrapped in a JSONArray)
        String content = readStringFromFile(file);
        if (content != null && content.trim().startsWith("["))
        {
            JSONArray array = new JSONArray(content);
            if (array.length() > 0)
            {
                fromJSON(array.getJSONObject(0));
                Log.i(TAG, "Successfully loaded settings from legacy JSONArray format");
                saveSettingsSilent(); // Migrate to new format immediately
            }
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
        // Version check could be added here in the future
        m_eventCode = json.optString(KEY_EVENT_CODE, DEFAULT_EVENT_CODE);
        m_teamIndexStr = json.optString(KEY_TEAM_INDEX, "0 - None");
        m_mostRecentMatchNumber = json.optString(KEY_MOST_RECENT_MATCH_NUMBER, "");
        m_mostRecentScoutName = json.optString(KEY_MOST_RECENT_SCOUT_NAME, "");

        if (json.has(KEY_PAST_SCOUTS))
        {
            JSONArray scoutsArray = json.getJSONArray(KEY_PAST_SCOUTS);
            m_pastScouts.clear();
            for (int idx = 0; idx < scoutsArray.length(); idx++)
            {
                m_pastScouts.add(scoutsArray.getString(idx));
            }
        }
        else
        {
            // Fallback to legacy format: scoutName0, scoutName1, ...
            int legacyIndex = 0;
            while (json.has(KEY_SCOUT_NAME_PREFIX + legacyIndex))
            {
                m_pastScouts.add(json.getString(KEY_SCOUT_NAME_PREFIX + legacyIndex));
                legacyIndex++;
            }
        }

        m_scoringTableSide = json.optBoolean(KEY_SCORING_TABLE_SIDE, false);
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
        json.put(KEY_SETTINGS_VERSION, 1);
        json.put(KEY_LAST_UPDATED, QRCodeDialog.formattedDate(new java.util.Date()));
        json.put(KEY_EVENT_CODE, m_eventCode);
        json.put(KEY_TEAM_INDEX, m_teamIndexStr);
        json.put(KEY_MOST_RECENT_MATCH_NUMBER, m_mostRecentMatchNumber);
        json.put(KEY_MOST_RECENT_SCOUT_NAME, m_mostRecentScoutName);

        JSONArray scoutsArray = new JSONArray();
        for (String name : m_pastScouts)
        {
            scoutsArray.put(name);
        }
        json.put(KEY_PAST_SCOUTS, scoutsArray);

        json.put(KEY_SCORING_TABLE_SIDE, m_scoringTableSide);

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
     * Sets the current FRC event code.
     *
     * @param eventCode the event code string
     */
    public void setEventCode(String eventCode)
    {
        m_eventCode = (eventCode != null) ? eventCode : "";
        saveSettingsSilent();
    }

    /**
     * Returns the current FRC event code.
     *
     * @return the event code string
     */
    public String getEventCode()
    {
        return m_eventCode;
    }

    /**
     * Sets the team index string (e.g. "1 - Red 1").
     *
     * @param indexStr the team index string from m_teamIndexOptions
     */
    public void setTeamIndexStr(String indexStr)
    {
        m_teamIndexStr = (indexStr != null) ? indexStr : m_teamIndexOptions[0];
        saveSettingsSilent();
    }

    /**
     * Returns the current team index string (e.g. "1 - Red 1").
     *
     * @return the team index string
     */
    public String getTeamIndexStr()
    {
        return m_teamIndexStr;
    }

    /**
     * Clears the current team index string.
     *
     */
    public void clearTeamIndexStr()
    {
        m_teamIndexStr = m_teamIndexOptions[0];
    }

    /**
     * Returns the team index options.
     *
     * @return team index string options
     */
    public String[] getTeamIndexOptions()
    {
        return m_teamIndexOptions;
    }

    /**
     * Returns true if given indexStr is valid
     *
     * @param indexStr the index string to validate
     * @return true if valid
     */
    public boolean isValidTeamIndexStr(String indexStr)
    {
        List<String> indexOptions = Arrays.asList(m_teamIndexOptions);
        if (indexStr == null)
        {
            return false;
        }
        return indexOptions.contains(indexStr);
    }

    /**
     * Returns "red", "blue", or "unknown" based on the current team index.
     *
     * @return the team color string ("red", "blue", or "unknown")
     */
    public String getTeamIndexColor()
    {
        if (m_teamIndexStr == null)
        {
            return "";
        }

        if (m_teamIndexStr.equals(m_teamIndexOptions[1]) || m_teamIndexStr.equals(m_teamIndexOptions[2]) || m_teamIndexStr.equals(m_teamIndexOptions[3]))
        {
            return "red";
        }
        if (m_teamIndexStr.equals(m_teamIndexOptions[4]) || m_teamIndexStr.equals(m_teamIndexOptions[5]) || m_teamIndexStr.equals(m_teamIndexOptions[6]))
        {
            return "blue";
        }
        return "unknown";
    }

    /**
     * Sets the most recent match number used in the application.
     *
     * @param value the match number string
     */
    public void setMostRecentMatchNumber(String value)
    {
        m_mostRecentMatchNumber = (value != null) ? value : "";
        saveSettingsSilent();
    }

    /**
     * Returns the most recent match number used.
     *
     * @return the match number string
     */
    public String getMostRecentMatchNumber()
    {
        return m_mostRecentMatchNumber;
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

    /**
     * Returns the most recent scout name used.
     *
     * @return the scout name
     */
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

    /**
     * Clears the list of past scout names.
     */
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


    /**
     * Returns whether event-specific scout names have been loaded.
     *
     * @return true if loaded
     */
    public boolean isEventScoutNamesLoaded()
    {
        return m_bEventScoutNamesLoaded;
    }

    /**
     * Sets which side of the field the scoring table is on.
     *
     * @param val true for one side, false for the other
     */
    public void setScoringTableSide(boolean val)
    {
        m_scoringTableSide = val;
        saveSettingsSilent();
    }

    /**
     * Returns which side of the field the scoring table is on.
     *
     * @return the side value
     */
    public boolean getScoringTableSide()
    {
        return m_scoringTableSide;
    }
}
