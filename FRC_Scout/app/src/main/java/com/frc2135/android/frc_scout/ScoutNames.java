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
 * <p>
 * This class handles loading and parsing event-specific scout names from local JSON files
 * and combining them with historical names entered by the user. It maintains an in-memory
 * list of unique scout names to facilitate UI autopopulation.
 * <p>
 * It follows a "Write-through Cache" pattern where successful file writes automatically
 * trigger a refresh of the internal memory state.
 */
public class ScoutNames extends BaseJSONSerializer
{
    private static final String TAG = "ScoutNames";
    private static final String SCOUT_NAME_JSON_KEY = "scoutName";

    // FRC event code for the currently loaded names record. Static to ensure global consistency.
    private static String m_eventCode;
    private final List<String> m_scoutNames;
    private boolean m_bScoutNamesLoaded;

    private static volatile ScoutNames sScoutNames;

    /**
     * Initializes a new ScoutNames repository.
     *
     * @param context the context used for file operations
     */
    private ScoutNames(Context context)
    {
        super(context);
        Log.v(TAG, "ScoutNames constructor");
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
     * <p>
     * If the requested event code differs from the currently loaded one, or if a reload is forced,
     * the repository will re-initialize and attempt to load data from storage.
     *
     * @param context      the context used for file and display operations
     * @param eventCode    the FRC event code
     * @param bForceReload if true, forces a reload of the names from disk even if already loaded
     * @return the singleton ScoutNames instance
     */
    public static ScoutNames getInstance(Context context, String eventCode, boolean bForceReload)
    {
        Log.v(TAG, "getInstance");
        synchronized (ScoutNames.class)
        {
            if (sScoutNames == null)
            {
                Log.i(TAG, "Creating new sScoutNames for eventCode: " + eventCode);
                m_eventCode = eventCode;
                sScoutNames = new ScoutNames(context);
                sScoutNames.loadScoutNamesJSON(true);
            }
            else if (bForceReload || !eventCode.equalsIgnoreCase(m_eventCode))
            {
                Log.i(TAG, "Resetting ScoutNames: " + m_eventCode + " -> " + eventCode);
                m_eventCode = eventCode;
                sScoutNames.m_bScoutNamesLoaded = false;
                sScoutNames.m_scoutNames.clear();
                sScoutNames.loadScoutNamesJSON(true);
            }
            return sScoutNames;
        }
    }

    /**
     * Clears the singleton instance of ScoutNames.
     */
    private static void clearScoutNames()
    {
        synchronized (ScoutNames.class)
        {
            Log.v(TAG, "clearScoutNames");
            sScoutNames = null;
        }
    }

    /**
     * Reads the scout names JSON file from internal storage for the current event.
     *
     * @param bSilent if true, error notifications are suppressed
     */
    private void loadScoutNamesJSON(boolean bSilent)
    {
        if (!ScoutingUtils.isValidEventCode(TAG, m_eventCode))
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
                super.displayToastMessages(m_appContext, TAG, "Successfully read scout names file for " + m_eventCode, bSilent, null);
            }
            else
            {
                super.displayToastMessages(m_appContext, TAG, "Scout names file not found for " + m_eventCode, bSilent, null);
            }
        }
        catch (JSONException | IOException e)
        {
            super.displayToastMessages(m_appContext, TAG, "Failed to parse scout names file for: " + m_eventCode, bSilent, e);
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
     * Loads the scout names JSON file for a specific event from local storage.
     *
     * @param eventCode the FRC event code
     * @return the loaded JSONArray, or null if the file is missing
     * @throws IOException   if reading the file fails
     * @throws JSONException if the file content is not a valid JSONArray
     */
    private JSONArray readScoutNamesFile(String eventCode)
            throws IOException, JSONException
    {
        Log.d(TAG, "Reading scout names from file for event: " + eventCode);
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            return null;
        }

        String filename = getFilename(eventCode);
        File file = new File(m_dataDir, filename);
        return loadJSONArray(file);
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
            Log.w(TAG, "Attempted to save scout names with null eventCode or data");
            return false;
        }

        Log.d(TAG, "Writing scout names to file for event: " + eventCode);

        // Cleanup existing names file if it exists
        deleteScoutNamesFile(eventCode);

        String filename = getFilename(eventCode);
        Log.i(TAG, "Saving scout names for " + eventCode + " to: " + filename);
        try
        {
            File file = new File(m_dataDir, filename);
            saveJSONArray(file, scoutData);
            Log.i(TAG, "Successfully saved " + scoutData.length() + " scout names for event: " + eventCode);
            loadScoutNamesJSON(bSilent);
            return true;
        }
        catch (IOException e)
        {
            super.displayToastMessages(m_appContext, TAG, "Failed to write scout names file for: " + eventCode, bSilent, e);
            return false;
        }
    }

    /**
     * Deletes scout names records from local storage.
     * <p>
     * If an event code is provided, only that file is deleted. If null, all scout name files are removed.
     *
     * @param eventCode the FRC event code, or null to clear all
     * @return the number of files deleted
     */
    public int deleteScoutNamesFile(String eventCode)
    {
        Log.d(TAG, "Deleting scout names file for event: " + eventCode);
        File[] fileList;
        int deletedCount = 0;

        // Delete all files or selected file
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            fileList = m_dataDir.listFiles();
        }
        else
        {
            String filename = getFilename(eventCode);
            fileList = new File[]{new File(m_dataDir, filename)};
        }

        // Walk through list deleting files
        if (fileList != null)
        {
            for (File file : fileList)
            {
                if (file.getName().endsWith(Constants.SCOUT_NAMES_FILENAME_SUFFIX))
                {
                    if (file.exists() && file.delete())
                    {
                        deletedCount++;
                        Log.i(TAG, "Deleted event file: " + file.getName());
                    }
                }
            }
        }

        if (deletedCount > 0)
        {
            ScoutNames.clearScoutNames();
        }
        else
        {
            Log.w(TAG, "Failed to delete scout names files");
        }

        return deletedCount;
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
     * <p>
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
