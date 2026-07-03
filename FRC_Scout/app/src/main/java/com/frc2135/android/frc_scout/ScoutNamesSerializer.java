package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

/**
 * Serializer class for retrieving and saving scout names data to persistent storage.
 */
public class ScoutNamesSerializer extends BaseJSONSerializer
{
    private static final String TAG = "ScoutNamesSerializer";
    private static final String FILENAME_SUFFIX = "_scoutNames.json";

    /**
     * Constructs a ScoutNamesSerializer.
     *
     * @param context the context used to retrieve the internal files directory
     */
    public ScoutNamesSerializer(Context context)
    {
        super(context);
        Log.d(TAG, "ScoutNamesSerializer constructor");
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
    @SuppressWarnings("UnusedReturnValue")
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
}
