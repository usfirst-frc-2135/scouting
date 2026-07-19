package com.frc2135.android.frc_scout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Singleton class for managing application settings, including past scouts, team index, and match numbers.
 * Settings are persisted via a JSON file in local storage.
 * Handles its own persistence by extending {@link BaseJSONSerializer}.
 */
public final class Settings extends BaseJSONSerializer
{
    private static final String TAG = "Settings";

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

    private String m_eventCode;
    private String m_teamIndexStr;
    private final String[] m_teamIndexOptions;
    private String m_mostRecentMatchNumber;
    private final List<String> m_pastScouts;
    private String m_mostRecentScoutName;
    private boolean m_scoringTableSide;

    private static volatile Settings sSettings;

    /**
     * Returns the singleton instance of Settings.
     *
     * @param context the context used to initialize the instance
     * @return the singleton Settings instance
     */
    public static Settings getInstance(Context context)
    {
        Log.v(TAG, "getInstance");
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

    /**
     * Initializes the settings repository and attempts to load existing settings from storage.
     *
     * @param context the context used for resource access and file operations
     */
    private Settings(Context context)
    {
        super(context);
        Log.v(TAG, "Settings constructor");
        m_teamIndexOptions = new String[]{
                context.getString(R.string.team_index_none),
                context.getString(R.string.team_index_1),  // red teams
                context.getString(R.string.team_index_2),
                context.getString(R.string.team_index_3),
                context.getString(R.string.team_index_4),  // blue teams
                context.getString(R.string.team_index_5),
                context.getString(R.string.team_index_6)
        };

        m_pastScouts = new ArrayList<>();

        resetSettings();
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
     * Clears all settings to their default values and removes the settings file from disk.
     */
    public void resetSettings()
    {
        Log.d(TAG, "resetSettings");
        m_eventCode = DEFAULT_EVENT_CODE;
        m_teamIndexStr = m_teamIndexOptions[0];
        m_mostRecentMatchNumber = "";
        m_mostRecentScoutName = "";
        m_pastScouts.clear();
        m_scoringTableSide = false;
    }

    /**
     * Loads the application settings from internal storage.
     * Handles both modern JSONObject format and legacy JSONArray formats for backward compatibility.
     *
     * @throws IOException   if reading the file fails
     * @throws JSONException if parsing the JSON content fails
     */
    public void loadSettings()
            throws IOException, JSONException
    {
        Log.d(TAG, "loadSettings");
        File file = new File(m_dataDir, Constants.SETTINGS_FILENAME);
        if (!file.exists())
        {
            Log.i(TAG, "Write defaults to file in JSONObject format");
            saveSettings();
        }

        String content = readStringFromFile(file);
        if (content == null || content.trim().isEmpty())
        {
            Log.e(TAG, "Failed to load settings from file");
            return;
        }

        JSONObject json = new JSONObject(content.trim());
        fromJSON(json);
        Log.i(TAG, "Successfully loaded settings from JSONObject format");
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
        Log.d(TAG, "saveSettings");
        File file = new File(m_dataDir, Constants.SETTINGS_FILENAME);
        saveJSONObject(file, toJSON());
    }

    /**
     * Populates this Settings object from a {@link JSONObject}.
     *
     * @param json the source JSONObject
     * @throws JSONException if parsing fails or expected keys are missing
     */
    public void fromJSON(JSONObject json)
            throws JSONException
    {
        // Version check could be added here in the future
        m_eventCode = json.optString(KEY_EVENT_CODE, DEFAULT_EVENT_CODE);
        m_teamIndexStr = json.optString(KEY_TEAM_INDEX, m_teamIndexOptions[0]);
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
     * Serializes all current application settings to a {@link JSONObject}.
     *
     * @return the serialized JSONObject
     * @throws JSONException if JSON creation fails
     */
    public JSONObject toJSON()
            throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(KEY_SETTINGS_VERSION, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        json.put(KEY_LAST_UPDATED, sdf.format(new java.util.Date()));
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
     * Saves the settings to disk, swallowing and logging any exceptions that occur during I/O.
     *
     * @return true if successful, false otherwise
     */
    public boolean saveSettingsSilent()
    {
        try
        {
            saveSettings();
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to auto-save settings: ", e);
            return false;
        }
    }

    /**
     * Sets the current FRC event code and persists it to storage.
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
     * Validates an event code string format (4-digit year followed by identifier, e.g., 2026casac).
     *
     * @param eventCode the event code string to validate
     * @return true if the format is valid
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValidEventCode(String eventCode)
    {
        if (eventCode == null || eventCode.isEmpty() || eventCode.length() < 7)
        {
            Log.w(TAG, "isValidEventCode: Invalid event code (too short): " + eventCode);
            return false;
        }

        if (!eventCode.matches("\\d{4}[a-z0-9]+"))
        {
            Log.w(TAG, "isValidEventCode: Invalid event code format: " + eventCode);
            return false;
        }

        return true;
    }

    /**
     * Sets the team index selection (e.g. "1 - Red 1") and persists it to storage.
     *
     * @param indexStr the team index string from m_teamIndexOptions
     */
    public void setTeamIndexStr(String indexStr)
    {
        m_teamIndexStr = (indexStr != null) ? indexStr : m_teamIndexOptions[0];
        saveSettingsSilent();
    }

    /**
     * Returns the current team index string selection.
     *
     * @return the team index string
     */
    public String getTeamIndexStr()
    {
        return m_teamIndexStr;
    }

    /**
     * Returns the current team index as an integer (0-6), extracted from the display string.
     *
     * @return the team index numeric value
     */
    public int getTeamIndex()
    {
        if (m_teamIndexStr == null || m_teamIndexStr.isEmpty())
        {
            return 0;
        }

        try
        {
            // The team index string format is "X - Label", where X is the index.
            return Integer.parseInt(m_teamIndexStr.substring(0, 1));
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error parsing team index: " + m_teamIndexStr, e);
            return 0;
        }
    }

    /**
     * Resets the current team index selection to the default ("None").
     */
    public void clearTeamIndexStr()
    {
        m_teamIndexStr = m_teamIndexOptions[0];
        saveSettingsSilent();
    }

    /**
     * Returns the array of available team index display options.
     *
     * @return team index option strings
     */
    public String[] getTeamIndexOptions()
    {
        return m_teamIndexOptions;
    }

    /**
     * Validates whether a given index string is part of the recognized options list.
     *
     * @param indexStr the index string to validate
     * @return true if the string is a valid option
     */
    public boolean isValidTeamIndexStr(String indexStr)
    {
        if (indexStr == null)
        {
            return false;
        }

        List<String> indexOptions = Arrays.asList(m_teamIndexOptions);
        return indexOptions.contains(indexStr);
    }

    /**
     * Returns a {@link ColorDrawable} representing the alliance color associated with the current team index.
     *
     * @return RED for indices 1-3, BLUE for indices 4-6, and GRAY for others
     */
    public ColorDrawable getTeamIndexColor()
    {
        int index = getTeamIndex();
        if (index >= 1 && index <= 3)
        {
            return new ColorDrawable(Color.RED);
        }
        if (index >= 4 && index <= 6)
        {
            return new ColorDrawable(Color.BLUE);
        }

        return new ColorDrawable(Color.GRAY);
    }

    /**
     * Updates the most recent match number record and persists it to storage.
     *
     * @param value the match number identifier (e.g., "qm1")
     */
    public void setMostRecentMatchNumber(String value)
    {
        m_mostRecentMatchNumber = (value != null) ? value : "";
        saveSettingsSilent();
    }

    /**
     * Returns the most recent match identifier used in the app.
     *
     * @return the match identifier string
     */
    public String getMostRecentMatchNumber()
    {
        return m_mostRecentMatchNumber;
    }

    /**
     * Predicts the next expected match number by incrementing the numeric portion of the most recent identifier.
     *
     * @return the incremented match identifier string (e.g., "qm1" -> "qm2")
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
     * Adds a scout name to the historical list of scouts if it is not already present.
     * Triggers a save of the settings repository.
     *
     * @param name the scout name string
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
     * Returns the unique list of scout names gathered over time.
     *
     * @return an array of scout name strings
     */
    public String[] getPastScouts()
    {
        return m_pastScouts.toArray(new String[0]);
    }

    /**
     * Clears the historical list of past scout names from memory.
     */
    @SuppressWarnings("unused")
    public void clearPastScouts()
    {
        m_pastScouts.clear();
    }

    /**
     * Updates the record for the most recently active scout and persists it to storage.
     *
     * @param name the scout name string
     */
    public void setMostRecentScoutName(String name)
    {
        m_mostRecentScoutName = (name != null) ? name : "";
        saveSettingsSilent();
    }

    /**
     * Returns the name of the scout who most recently performed a scouting session.
     *
     * @return the scout name string
     */
    public String getMostRecentScoutName()
    {
        return m_mostRecentScoutName;
    }

    /**
     * Sets which side of the field the scoring table is currently located on.
     *
     * @param val true for one side, false for the other
     */
    @SuppressWarnings("unused")
    public void setScoringTableSide(boolean val)
    {
        m_scoringTableSide = val;
        saveSettingsSilent();
    }

    /**
     * Returns the currently configured side of the field for the scoring table.
     *
     * @return true/false representing field side
     */
    @SuppressWarnings("unused")
    public boolean getScoringTableSide()
    {
        return m_scoringTableSide;
    }
}
