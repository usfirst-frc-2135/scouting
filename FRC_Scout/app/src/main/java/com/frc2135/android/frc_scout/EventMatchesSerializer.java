package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Serializer class for managing event-related data persistence.
 * Handles match data for specific events and the current event code configuration.
 */
public class EventMatchesSerializer
{
    private static final String TAG = "EventMatchesSerializer";
    private static final String CURRENT_EVENT_CODE_FILENAME = "current_event_code.json";
    private static final String MATCHES_FILE_SUFFIX = "_matches.json";

    private final File m_dataDir;

    /**
     * Constructs an EventMatchesSerializer.
     *
     * @param context the context used to retrieve the internal files directory
     */
    public EventMatchesSerializer(Context context)
    {
        m_dataDir = context.getApplicationContext().getFilesDir();
        Log.d(TAG, "Initialized with data directory: " + m_dataDir.getAbsolutePath());
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
     * Deletes the event match data for a specific event from local storage.
     * If eventCode is null or empty, deletes all event match files.
     *
     * @param eventCode the TBA event code to clear (e.g., "2026casac"), or null to clear all
     * @return the number of files deleted
     */
    public int deleteEventMatches(String eventCode)
    {
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
                if (f.getName().endsWith(MATCHES_FILE_SUFFIX))
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
        return eventCode.trim().toLowerCase(Locale.US) + MATCHES_FILE_SUFFIX;
    }

    /**
     * Persists the current event configuration to internal storage.
     *
     * @param eventMatchesJSON the JSONObject representing the current event matches
     * @throws IOException if writing the file fails
     */
    public void saveCurrentEventCode(JSONObject eventMatchesJSON)
            throws IOException
    {
        if (eventMatchesJSON == null)
        {
            return;
        }

        File file = new File(m_dataDir, CURRENT_EVENT_CODE_FILENAME);
        Log.d(TAG, "Saving current event configuration to: " + file.getAbsolutePath());

        try
        {
            writeStringToFile(file, eventMatchesJSON.toString());
            Log.i(TAG, "Successfully saved current event matches configuration");
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error saving current event matches: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Loads the current event configuration from internal storage.
     *
     * @return a {@link CurrentEventCode} instance, or null if no saved data is found
     * @throws IOException   if reading the file fails
     * @throws JSONException if parsing the JSON data fails
     */
    public CurrentEventCode loadCurrentEventCode()
            throws IOException, JSONException
    {
        File file = new File(m_dataDir, CURRENT_EVENT_CODE_FILENAME);
        if (!file.exists())
        {
            Log.d(TAG, "No current event code file found at: " + file.getAbsolutePath());
            return null;
        }

        Log.d(TAG, "Loading current event code from: " + file.getAbsolutePath());
        String jsonString = readStringFromFile(file);

        if (jsonString.isEmpty())
        {
            return null;
        }

        JSONObject object = (JSONObject) new JSONTokener(jsonString).nextValue();
        CurrentEventCode currentEventCode = new CurrentEventCode(object);
        Log.i(TAG, "Successfully loaded current event code: " + currentEventCode.getEventCode());
        return currentEventCode;
    }

    /**
     * Helper to write a string to a file using UTF-8 encoding.
     */
    private void writeStringToFile(File file, String content)
            throws IOException
    {
        try (FileOutputStream out = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
        {
            writer.write(content);
        }
    }

    /**
     * Helper to read a string from a file using UTF-8 encoding.
     */
    private String readStringFromFile(File file)
            throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileInputStream in = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }
}
