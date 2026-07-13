package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Singleton class for managing scout names data.
 * Loads and parses scout names from a JSON file.
 */
public class ScoutNames extends BaseJSONSerializer
{
    private static final String TAG = "ScoutNames";
    private static final String SCOUT_NAME_JSON_KEY = "scoutName";

    private String m_eventCode;
    private List<String> m_scoutNames;
    private boolean m_bScoutNamesLoaded;

    private static volatile ScoutNames sScoutNames;

    private ScoutNames(Context context, String eventCode)
    {
        super(context);
        Log.d(TAG, "ScoutNames constructor");
        m_eventCode = eventCode;
        m_bScoutNamesLoaded = false;
        m_scoutNames = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of ScoutNames using the event code from Settings.
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
     * Returns the singleton instance of ScoutNames.
     *
     * @param context      the context used for file operations
     * @param eventCode    the FRC event code
     * @param bForceReload whether to force a reload of the JSON data
     * @return the singleton ScoutNames instance
     */
    public static ScoutNames getInstance(Context context, String eventCode, boolean bForceReload)
    {
        Log.d(TAG, "getInstance()");
        synchronized (ScoutNames.class)
        {
            if (sScoutNames == null)
            {
                Log.d(TAG, "Creating a new sScoutNames for eventCode " + eventCode);
                sScoutNames = new ScoutNames(context, eventCode);
                sScoutNames.readScoutNamesJSON(context, true);
            }
            else
            {
                String oldEventCode = sScoutNames.getEventCode();
                if (bForceReload || !oldEventCode.equalsIgnoreCase(eventCode))
                {
                    Log.d(TAG, "Resetting ScoutNames: " + oldEventCode + " -> " + eventCode);
                    sScoutNames.setEventCode(eventCode);
                    sScoutNames.readScoutNamesJSON(context, true);
                }
            }
            return sScoutNames;
        }
    }

    @SuppressWarnings("unused")
    public static void clearScoutNames()
    {
        Log.d(TAG, "clear()");
        sScoutNames = null;
    }

    /**
     * Returns the event code associated with this scout list.
     *
     * @return the event code string
     */
    public String getEventCode()
    {
        return m_eventCode;
    }

    /**
     * Returns the raw list of scout names for the current event.
     *
     * @return a list of scout names
     */
    @SuppressWarnings("unused")
    public List<String> getScoutNames()
    {
        return m_scoutNames;
    }

    /**
     * Sets the event code and resets the loaded scout names state.
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
     * Reads the scout names JSON file from internal storage.
     *
     * @param context the context used to open the file
     * @param bSilent if true, error Toast messages are suppressed
     */
    public void readScoutNamesJSON(Context context, boolean bSilent)
    {
        if (m_eventCode == null || m_eventCode.trim().isEmpty())
        {
            return;
        }

        Log.d(TAG, "Looking for scout names for: " + m_eventCode);

        try
        {
            JSONArray jsonArray = readScoutNamesFile(m_eventCode);
            if (jsonArray != null)
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
                m_bScoutNamesLoaded = true;
                Log.d(TAG, "Successfully loaded scout names for " + m_eventCode);
                if (!bSilent)
                {
                    Toast.makeText(context, "Successfully loaded scout names for " + m_eventCode, Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                super.handleToastError(context, TAG, "Scout names file not found for " + m_eventCode, bSilent, null);
            }
        }
        catch (JSONException | IOException e)
        {
            super.handleToastError(context, TAG, "Failed to parse scout names for: " + m_eventCode, bSilent, e);
        }
    }

    /**
     * Gets the filename for a given event code.
     *
     * @param eventCode the FRC event code
     * @return the filename
     */
    private String getFilename(String eventCode)
    {
        return eventCode.trim().toLowerCase(Locale.US) + Constants.SCOUT_NAMES_FILENAME_SUFFIX;
    }

    /**
     * Saves the provided JSONArray of scout names data to a JSON file on the device.
     *
     * @param eventCode the FRC event code
     * @param scoutData the JSONArray containing scout names
     * @param bSilent   if true, error Toast messages are suppressed
     * @return true if successful, false otherwise
     */
    public boolean writeScoutNamesFile(String eventCode, JSONArray scoutData, boolean bSilent)
    {
        if (eventCode == null || scoutData == null)
        {
            Log.w(TAG, "Attempted to save scout names with null eventCode or data");
            return false;
        }

        String filename = getFilename(eventCode);
        Log.d(TAG, "Saving scout names info to: " + filename);

        try
        {
            File file = new File(m_dataDir, filename);
            saveJSONArray(file, scoutData);
            return true;
        }
        catch (IOException e)
        {
            super.handleToastError(m_appContext, TAG, "Failed to write scout names file for: " + eventCode, bSilent, e);
            return false;
        }
    }

    /**
     * Loads the scout names from the specified file.
     *
     * @param eventCode the FRC event code
     * @return the loaded JSONArray, or null if the file doesn't exist
     * @throws IOException   if an error occurs during file reading
     * @throws JSONException if the file content is not a valid JSON array
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
     * Deletes the scout names file from internal storage.
     *
     * @param eventCode the FRC event code
     * @return the number of files deleted (1 if successful, 0 otherwise)
     */
    public int deleteScoutNamesFile(String eventCode)
    {
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            Log.i(TAG, "Invalid event code: " + eventCode);
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

        if (!forceReload && m_bScoutNamesLoaded && m_eventCode != null && m_eventCode.equalsIgnoreCase(eventCode))
        {
            return;
        }

        Log.d(TAG, "Loading scout names for event: " + eventCode);
        readScoutNamesJSON(context, true);
    }

    /**
     * Saves event-specific scout names to local storage.
     *
     * @param context   the context for file operations
     * @param eventCode the FRC event code
     * @param scoutData the JSONArray of scout names
     * @return true if successful, false otherwise
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
     * Clears event-specific scout names from local storage.
     *
     * @param context   the context for file operations
     * @param eventCode the FRC event code
     * @return number of files if successful, zero otherwise
     */
    public int deleteEventScoutNames(@SuppressWarnings("unused") Context context, String eventCode)
    {
        m_bScoutNamesLoaded = false;
        m_scoutNames = new ArrayList<>();
        return deleteScoutNamesFile(eventCode);
    }

    /**
     * Checks whether scout names have been successfully loaded from local storage.
     *
     * @return true if data is loaded
     */
    @SuppressWarnings("unused")
    public boolean isScoutNamesLoaded()
    {
        return m_bScoutNamesLoaded;
    }

    /**
     * Returns a combined list of event-specific scout names and past scouts from Settings.
     *
     * @param context the context to access Settings
     * @return a list of unique scout names
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
