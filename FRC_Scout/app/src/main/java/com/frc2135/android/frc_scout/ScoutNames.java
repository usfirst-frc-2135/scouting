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
import java.util.Locale;

/**
 * Singleton class for managing the repository of scout names.
 * Loads and parses event-specific scout names from local JSON files and combines them with user-entered historical names from settings.
 * Handles its own persistence by extending {@link BaseJSONSerializer}.
 */
public class ScoutNames extends BaseJSONSerializer
{
    private static final String TAG = "ScoutNames";
    private static final String SCOUT_NAME_JSON_KEY = "scoutName";

    private String m_eventCode;
    private List<String> m_scoutNames;
    private boolean m_bScoutNamesLoaded;

    private static volatile ScoutNames sScoutNames;

    /**
     * Initializes a new ScoutNames repository for a specific event.
     *
     * @param context   the context used for file operations
     * @param eventCode the FRC event code
     */
    private ScoutNames(Context context, String eventCode)
    {
        super(context);
        Log.v(TAG, "ScoutNames constructor");
        m_eventCode = eventCode;
        m_bScoutNamesLoaded = false;
        m_scoutNames = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of ScoutNames using the default event code from application settings.
     *
     * @param context the context used for file operations
     * @return the singleton ScoutNames instance
     */
    public static ScoutNames getInstance(Context context)
    {
        String eventCode = Settings.getInstance(context).getEventCode();
        return getInstance(context, eventCode, false);
    }

    /**
     * Returns the thread-safe singleton instance of ScoutNames.
     * If the requested event code differs from the currently loaded one, it re-initializes for the new event.
     *
     * @param context      the context used for file operations
     * @param eventCode    the FRC event code
     * @param bForceReload if true, forces a reload of the scout names JSON from storage
     * @return the singleton ScoutNames instance
     */
    public static ScoutNames getInstance(Context context, String eventCode, boolean bForceReload)
    {
        Log.v(TAG, "getInstance");
        synchronized (ScoutNames.class)
        {
            if (sScoutNames == null)
            {
                Log.i(TAG, "Creating a new sScoutNames for eventCode " + eventCode);
                sScoutNames = new ScoutNames(context, eventCode);
                sScoutNames.readScoutNamesJSON(context, true);
            }
            else
            {
                String oldEventCode = sScoutNames.getEventCode();
                if (bForceReload || !oldEventCode.equalsIgnoreCase(eventCode))
                {
                    Log.i(TAG, "Resetting ScoutNames: " + oldEventCode + " -> " + eventCode);
                    sScoutNames.setEventCode(eventCode);
                    sScoutNames.readScoutNamesJSON(context, true);
                }
            }
            return sScoutNames;
        }
    }

    /**
     * Clears the singleton instance of ScoutNames.
     */
    @SuppressWarnings("unused")
    public static void clearScoutNames()
    {
        Log.v(TAG, "clearScoutNames");
        sScoutNames = null;
    }

    /**
     * Returns the event code currently associated with this scout list.
     *
     * @return the event code string
     */
    public String getEventCode()
    {
        return m_eventCode;
    }

    /**
     * Returns the raw list of event-specific scout names.
     *
     * @return a list of scout names string
     */
    @SuppressWarnings("unused")
    public List<String> getScoutNames()
    {
        return m_scoutNames;
    }

    /**
     * Updates the event code and resets the internal state of loaded scout names.
     *
     * @param eventCode the new FRC event code
     */
    public void setEventCode(String eventCode)
    {
        m_eventCode = eventCode;
        m_bScoutNamesLoaded = false;
        m_scoutNames = new ArrayList<>();
    }

    /**
     * Reads the scout names JSON file from internal storage for the current event.
     *
     * @param context the context used to open the file and show messages
     * @param bSilent if true, error notifications are suppressed
     */
    public void readScoutNamesJSON(Context context, boolean bSilent)
    {
        if (m_eventCode == null || m_eventCode.trim().isEmpty())
        {
            return;
        }

        Log.i(TAG, "Reading scout names file for: " + m_eventCode);

        try
        {
            JSONArray jsonArray = readScoutNamesFile(m_eventCode);
            if (jsonArray != null)
            {
                parseScoutNamesJSON(jsonArray);
                m_bScoutNamesLoaded = true;
                super.displayToastMessages(context, TAG, "Successfully loaded scout names for " + m_eventCode, false, null);
            }
            else
            {
                super.displayToastMessages(context, TAG, "Scout names file not found for " + m_eventCode, bSilent, null);
            }
        }
        catch (JSONException | IOException e)
        {
            super.displayToastMessages(context, TAG, "Failed to parse scout names for: " + m_eventCode, bSilent, e);
        }
    }

    /**
     * Parses a JSONArray of scout names into the internal list.
     *
     * @param jsonArray the source JSONArray
     * @throws JSONException if the array format is invalid
     */
    private void parseScoutNamesJSON(JSONArray jsonArray)
            throws JSONException
    {
        m_scoutNames.clear();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.optString(SCOUT_NAME_JSON_KEY);
            if (!name.trim().isEmpty())
            {
                m_scoutNames.add(name);
            }
        }
    }

    /**
     * Generates a filename for the scout names record associated with an event.
     *
     * @param eventCode the FRC event code
     * @return the generated filename
     */
    private String getFilename(String eventCode)
    {
        return eventCode.trim().toLowerCase(Locale.US) + Constants.SCOUT_NAMES_FILENAME_SUFFIX;
    }

    /**
     * Saves a {@link JSONArray} of scout names to a JSON file in local storage.
     *
     * @param eventCode the FRC event code
     * @param scoutData the JSONArray containing scout names to persist
     * @param bSilent   if true, error notifications are suppressed
     * @return true if the file was written successfully
     */
    public boolean writeScoutNamesFile(String eventCode, JSONArray scoutData, boolean bSilent)
    {
        if (eventCode == null || scoutData == null)
        {
            Log.e(TAG, "Attempted to save scout names with null eventCode or data");
            return false;
        }

        String filename = getFilename(eventCode);
        Log.i(TAG, "Saving scout names info for: " + eventCode + " to: " + filename);
        try
        {
            File file = new File(m_dataDir, filename);
            saveJSONArray(file, scoutData);
            Log.i(TAG, "Successfully saved " + scoutData.length() + " scout names for event: " + eventCode);
            return true;
        }
        catch (IOException e)
        {
            super.displayToastMessages(m_appContext, TAG, "Failed to write scout names file for: " + eventCode, bSilent, e);
            return false;
        }
    }

    /**
     * Loads the scout names JSON file for a specific event from local storage.
     *
     * @param eventCode the FRC event code
     * @return the loaded JSONArray, or null if the file is missing
     * @throws IOException   if reading the file fails
     * @throws JSONException if the file content is not a valid JSONArray
     */
    public JSONArray readScoutNamesFile(String eventCode)
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
     * Deletes the local scout names record associated with a specific event.
     *
     * @param eventCode the FRC event code
     * @return 1 if a file was deleted, 0 otherwise
     */
    public int deleteScoutNamesFile(String eventCode)
    {
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            Log.e(TAG, "Invalid event code: " + eventCode);
            return 0;
        }

        String filename = getFilename(eventCode);
        File file = new File(m_dataDir, filename);

        if (file.exists() && file.delete())
        {
            Log.i(TAG, "Successfully deleted scout names file: " + filename);
            return 1;
        }

        Log.w(TAG, "Failed to delete scout names file: " + filename);
        return 0;
    }

    /**
     * Ensures event-specific scout names are loaded into the internal repository.
     *
     * @param context     the context used for file operations
     * @param eventCode   the FRC event code
     * @param forceReload if true, reloads from storage even if names are already in memory
     */
    public void loadEventScoutNames(Context context, String eventCode, boolean forceReload)
    {
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            return;
        }

        if (!forceReload && m_bScoutNamesLoaded && m_eventCode != null && m_eventCode.equalsIgnoreCase(eventCode))
        {
            return;
        }

        Log.i(TAG, "Loading scout names for event: " + eventCode);
        readScoutNamesJSON(context, true);
    }

    /**
     * Persists event-specific scout names to storage and updates the internal list.
     *
     * @param context   the context used for file operations
     * @param eventCode the FRC event code
     * @param scoutData the JSONArray of scout names to save
     * @return true if the save operation was successful
     */
    public boolean saveEventScoutNames(Context context, String eventCode, JSONArray scoutData)
    {
        deleteScoutNamesFile(eventCode);
        if (writeScoutNamesFile(eventCode, scoutData, false))
        {
            loadEventScoutNames(context, eventCode, true);
            return true;
        }
        return false;
    }

    /**
     * Clears all scout names from memory and deletes the local record for the specified event.
     *
     * @param context   the context used for file operations
     * @param eventCode the FRC event code
     * @return the number of files deleted (1 if successful)
     */
    public int deleteEventScoutNames(@SuppressWarnings("unused") Context context, String eventCode)
    {
        m_bScoutNamesLoaded = false;
        m_scoutNames = new ArrayList<>();
        return deleteScoutNamesFile(eventCode);
    }

    /**
     * Checks whether scout names for the current event are currently held in memory.
     *
     * @return true if data is loaded
     */
    @SuppressWarnings("unused")
    public boolean isScoutNamesLoaded()
    {
        return m_bScoutNamesLoaded;
    }

    /**
     * Aggregates official event-specific scout names with historical names saved in application settings.
     * Ensures the final list contains unique entries.
     *
     * @param context the context used to retrieve settings
     * @return a combined list of unique scout names
     */
    public List<String> getAllScoutNames(Context context)
    {
        List<String> allNames = new ArrayList<>(m_scoutNames);
        Settings settings = Settings.getInstance(context);
        if (settings != null)
        {
            for (String name : settings.getPastScouts())
            {
                if (!allNames.contains(name))
                {
                    allNames.add(name);
                }
            }
        }
        return allNames;
    }
}
