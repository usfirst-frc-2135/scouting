package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for managing scout names data.
 * Loads and parses scout names from a JSON file.
 */
public class ScoutNames extends BaseJSONSerializer
{
    private static final String TAG = "ScoutNames";
    private static final String FILENAME_SUFFIX = "_scoutNames.json";

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
    public static ScoutNames get(Context context)
    {
        String eventCode = Settings.getInstance(context).getEventCode();
        return get(context, eventCode, false);
    }

    /**
     * Returns the singleton instance of ScoutNames.
     *
     * @param context      the context used for file operations
     * @param eventCode    the FRC event code
     * @param bForceReload whether to force a reload of the JSON data
     * @return the singleton ScoutNames instance
     */
    public static ScoutNames get(Context context, String eventCode, boolean bForceReload)
    {
        Log.d(TAG, "get()");
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
    public static void clear()
    {
        Log.d(TAG, "clear()");
        sScoutNames = null;
    }

    public String getEventCode()
    {
        return m_eventCode;
    }

    public List<String> getScoutNames()
    {
        return m_scoutNames;
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
            JSONArray jsonArray = loadScoutNames(m_eventCode);
            if (jsonArray != null)
            {
                m_scoutNames.clear();
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    m_scoutNames.add(jsonArray.getString(i));
                }
                m_bScoutNamesLoaded = true;
                Log.d(TAG, "Successfully loaded scout names for " + m_eventCode);
                Toast.makeText(context, "Loaded scout names for " + m_eventCode, Toast.LENGTH_SHORT).show();
            }
            else if (!bSilent)
            {
                Log.e(TAG, "Scout names file not found for event: " + m_eventCode);
                Toast.makeText(context, "Scout names file not found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException | IOException e)
        {
            Log.e(TAG, "Error reading scout names file", e);
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
        return eventCode.trim().toLowerCase() + FILENAME_SUFFIX;
    }

    /**
     * Saves the provided JSONArray of scout names data to a JSON file on the device.
     *
     * @param eventCode the FRC event code
     * @param scoutData the JSONArray containing scout names
     * @throws IOException if an error occurs during file writing
     */
    public void saveScoutNames(String eventCode, JSONArray scoutData)
            throws IOException
    {
        if (eventCode == null || scoutData == null)
        {
            Log.w(TAG, "Attempted to save scout names with null eventCode or data");
            return;
        }

        String filename = getFilename(eventCode);
        Log.d(TAG, "Saving scout names info to: " + filename);
        File file = new File(m_dataDir, filename);
        saveJSONArray(file, scoutData);
    }

    /**
     * Loads the scout names from the specified file.
     *
     * @param eventCode the FRC event code
     * @return the loaded JSONArray, or null if the file doesn't exist
     * @throws IOException   if an error occurs during file reading
     * @throws JSONException if the file content is not a valid JSON array
     */
    public JSONArray loadScoutNames(String eventCode)
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
     * @return true if the file was successfully deleted, false otherwise
     */
    public boolean deleteScoutNames(String eventCode)
    {
        if (eventCode == null)
        {
            return false;
        }

        String filename = getFilename(eventCode);
        File file = new File(m_dataDir, filename);
        if (file.exists())
        {
            boolean deleted = file.delete();
            if (deleted)
            {
                Log.i(TAG, "Successfully deleted scout names file: " + filename);
            }
            else
            {
                Log.w(TAG, "Failed to delete scout names file: " + filename);
            }
            return deleted;
        }
        return false;
    }

    public boolean isScoutNamesLoaded()
    {
        return m_bScoutNamesLoaded;
    }
}
