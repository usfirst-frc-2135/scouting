package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
public class EventInfoSerializer
{
    private static final String TAG = "EventInfoSerializer";
    private static final String CURRENT_COMP_FILENAME = "current_competition.json";

    private final Context m_context;
    private final File m_dataDir;

    /**
     * Constructs a EventInfoSerializer.
     *
     * @param context the context used to retrieve the internal files directory
     */
    public EventInfoSerializer(Context context)
    {
        m_context = context.getApplicationContext();
        m_dataDir = m_context.getFilesDir();
        Log.d(TAG, "Initialized with data directory: " + m_dataDir.getAbsolutePath());
    }

    /**
     * Saves event data for a specific event to a JSON file.
     *
     * @param eventInfo the JSONArray containing match information
     * @throws IOException if writing the file fails
     */
    public void saveEventInfo(Context context, String eventCode, JSONArray eventInfo)
            throws IOException
    {
        if (context == null || eventCode == null || eventInfo == null)
        {
            return;
        }

        // Cleanup existing matches file if it exists
        deleteEventInfo(context, eventCode);

        String eventFileName = getEventFileName(eventCode);
        File file = new File(m_dataDir, eventFileName);

        Log.d(TAG, "Saving event data to: " + file.getAbsolutePath());

        try (FileOutputStream out = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
        {
            writer.write(eventInfo.toString());
            Log.i(TAG, "Successfully saved " + eventInfo.length() + " matches for event: " + eventCode);
        }
    }

    /**
     * Deletes the event match data for a specific event from local storage.
     * If eventCode is null or empty, deletes all event match files.
     *
     * @param context   the context used to show Toast messages
     * @param eventCode the TBA event code to clear (e.g., "2026casac"), or null to clear all
     * @return true if the file was deleted successfully, or didn't exist
     */
    public boolean deleteEventInfo(Context context, String eventCode)
    {
        File dataDir = context.getFilesDir();
        File[] fileList;
        StringBuilder deletedFiles = new StringBuilder();
        int deletedCount = 0;

        if (eventCode == null || eventCode.trim().isEmpty())
        {
            fileList = dataDir.listFiles();
        }
        else
        {
            String filename = getEventFileName(eventCode);
            fileList = new File[]{new File(dataDir, filename)};
        }

        if (fileList != null)
        {
            for (File f : fileList)
            {
                if (f.getName().contains("_matches.json"))
                {
                    if (f.delete())
                    {
                        deletedCount++;
                        deletedFiles.append(f.getName()).append("\n");
                    }
                }
            }
        }

        if (deletedCount > 0)
        {
            EventInfo.clear();
            Toast.makeText(context, "Deleted:\n" + deletedFiles, Toast.LENGTH_LONG).show();
            return true;
        }
        else if (eventCode != null && !eventCode.trim().isEmpty())
        {
            Toast.makeText(context, "No match files found for " + eventCode, Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return false;
        }
    }

    /**
     * Helper to construct the filename for a given event code.
     *
     * @param eventCode the TBA event code
     * @return the filename string
     */
    private String getEventFileName(String eventCode)
    {
        return eventCode.trim().toLowerCase(Locale.US) + "_matches.json";
    }

    /**
     * Persists the current event configuration to internal storage.
     *
     * @param eventInfoJSON the JSONObject representing the current competition
     * @throws IOException if writing the file fails
     */
    public void saveCurrentEventCode(JSONObject eventInfoJSON)
            throws IOException
    {
        if (eventInfoJSON == null)
        {
            return;
        }

        File file = new File(m_dataDir, CURRENT_COMP_FILENAME);
        Log.d(TAG, "Saving current event configuration to: " + file.getAbsolutePath());

        try (FileOutputStream out = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
        {
            writer.write(eventInfoJSON.toString());
            Log.i(TAG, "Successfully saved current competition configuration");
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error saving current competition: " + e.getMessage(), e);
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
        File file = new File(m_dataDir, CURRENT_COMP_FILENAME);
        if (!file.exists())
        {
            Log.d(TAG, "No current event code file found at: " + file.getAbsolutePath());
            return null;
        }

        Log.d(TAG, "Loading current event code from: " + file.getAbsolutePath());
        StringBuilder jsonString = new StringBuilder();

        try (FileInputStream in = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                jsonString.append(line);
            }

            JSONObject object = (JSONObject) new JSONTokener(jsonString.toString()).nextValue();
            CurrentEventCode currComp = new CurrentEventCode(object);
            Log.i(TAG, "Successfully loaded current event code: " + currComp.getEventCode());
            return currComp;
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
        catch (IOException | JSONException e)
        {
            Log.e(TAG, "Error loading current event code: " + e.getMessage(), e);
            throw e;
        }
    }
}
