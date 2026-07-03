package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Serializer class for managing event-related data persistence.
 * Handles match data for specific events and the current event code configuration.
 */
public class EventMatchesSerializer extends BaseJSONSerializer
{
    private static final String TAG = "EventMatchesSerializer";
    private static final String FILENAME_SUFFIX = "_eventMatches.json";

    /**
     * Constructs an EventMatchesSerializer.
     *
     * @param context the context used to retrieve the internal files directory
     */
    public EventMatchesSerializer(Context context)
    {
        super(context);
        Log.d(TAG, "EventMatchesSerializer constructor");
    }

    /**
     * Saves event data for a specific event to a JSON file.
     *
     * @param eventCode    the TBA event code
     * @param eventMatches the JSONArray containing match information
     * @throws IOException if writing the file fails
     */
    public void saveEventMatches(String eventCode, JSONArray eventMatches)
            throws IOException
    {
        Log.d(TAG, "saveEventMatches()");
        if (eventCode == null || eventMatches == null)
        {
            return;
        }

        // Cleanup existing matches file if it exists
        deleteEventMatches(eventCode);

        String eventFileName = getEventFileName(eventCode);
        File file = new File(m_dataDir, eventFileName);

        Log.d(TAG, "Saving event data for " + eventCode + " to: " + file.getAbsolutePath());
        writeStringToFile(file, eventMatches.toString());
        Log.i(TAG, "Successfully saved " + eventMatches.length() + " matches for event: " + eventCode);
    }

    /**
     * Loads the event matches data for a specific event.
     *
     * @param eventCode the TBA event code
     * @return the loaded JSONArray, or null if the file doesn't exist
     * @throws IOException   if reading the file fails
     * @throws JSONException if parsing the JSON data fails
     */
    public JSONArray loadEventMatches(String eventCode)
            throws IOException, JSONException
    {
        Log.d(TAG, "loadEventMatches()");
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            return null;
        }

        String filename = getEventFileName(eventCode);
        File file = new File(m_dataDir, filename);
        return loadJSONArray(file);
    }

    /**
     * Deletes the event match data for a specific event from local storage.
     * If eventCode is null or empty, deletes all event match files.
     *
     * @param eventCode the TBA event code to clear (e.g., "2026casac"), or null to clear all
     * @return the number of files deleted
     */
    public int deleteEventMatches(String eventCode)
    {
        Log.d(TAG, "deleteEventMatches()");
        File[] fileList;
        int deletedCount = 0;

        // Delete all files or selected file
        if (eventCode == null || eventCode.trim().isEmpty())
        {
            fileList = m_dataDir.listFiles();
        }
        else
        {
            String filename = getEventFileName(eventCode);
            fileList = new File[]{new File(m_dataDir, filename)};
        }

        // Walk through list deleting files
        if (fileList != null)
        {
            for (File f : fileList)
            {
                if (f.getName().endsWith(FILENAME_SUFFIX))
                {
                    if (f.exists() && f.delete())
                    {
                        deletedCount++;
                        Log.d(TAG, "Deleted event file: " + f.getName());
                    }
                }
            }
        }

        if (deletedCount > 0)
        {
            EventMatches.clear();
        }
        return deletedCount;
    }

    /**
     * Helper to construct the filename for a given event code.
     *
     * @param eventCode the TBA event code
     * @return the filename string
     */
    private String getEventFileName(String eventCode)
    {
        return eventCode.trim().toLowerCase(Locale.US) + FILENAME_SUFFIX;
    }
}
