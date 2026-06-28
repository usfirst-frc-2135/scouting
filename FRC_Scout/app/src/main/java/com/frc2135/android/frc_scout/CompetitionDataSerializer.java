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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Serializer class for managing competition-related data persistence.
 * Handles match data for specific events and the current competition configuration.
 */
public class CompetitionDataSerializer
{
    private static final String TAG = "CompetitionDataSerializer";
    private static final String CURRENT_COMP_FILENAME = "current_competition.json";

    private final Context m_context;
    private final File m_dataDir;

    /**
     * Constructs a CompetitionDataSerializer.
     *
     * @param context the context used to access internal storage
     */
    public CompetitionDataSerializer(Context context)
    {
        m_context = context.getApplicationContext();
        m_dataDir = m_context.getFilesDir();
        Log.d(TAG, "Initialized with data directory: " + m_dataDir.getAbsolutePath());
    }

    /**
     * Saves match data for a competition event to a JSON file.
     *
     * @param compData the JSONArray containing match information
     * @throws JSONException if parsing competition information fails
     * @throws IOException   if writing the file fails
     */
    public void saveEventData(JSONArray compData) throws JSONException, IOException
    {
        if (compData == null)
        {
            return;
        }

        String eventCode = CurrentCompetition.get(m_context).getEventCode();
        String filename = eventCode + "matches.json";
        File file = new File(m_dataDir, filename);

        Log.d(TAG, "Saving event data to: " + file.getAbsolutePath());

        try (FileOutputStream out = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
        {
            writer.write(compData.toString());
            Log.i(TAG, "Successfully saved " + compData.length() + " matches for event: " + eventCode);
        }
    }

    /**
     * Persists the current competition configuration to internal storage.
     *
     * @param compJSON the JSONObject representing the current competition
     * @throws IOException if writing the file fails
     */
    public void saveCurrentCompetition(JSONObject compJSON) throws IOException
    {
        if (compJSON == null)
        {
            return;
        }

        File file = new File(m_dataDir, CURRENT_COMP_FILENAME);
        Log.d(TAG, "Saving current competition configuration to: " + file.getAbsolutePath());

        try (FileOutputStream out = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
        {
            writer.write(compJSON.toString());
            Log.i(TAG, "Successfully saved current competition configuration");
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error saving current competition: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Loads the current competition configuration from internal storage.
     *
     * @return a {@link CurrentCompetition} instance, or null if no saved data is found
     * @throws IOException   if reading the file fails
     * @throws JSONException if parsing the JSON data fails
     */
    public CurrentCompetition loadCurrentComp() throws IOException, JSONException
    {
        File file = new File(m_dataDir, CURRENT_COMP_FILENAME);
        if (!file.exists())
        {
            Log.d(TAG, "No current competition file found at: " + file.getAbsolutePath());
            return null;
        }

        Log.d(TAG, "Loading current competition from: " + file.getAbsolutePath());
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
            CurrentCompetition currComp = new CurrentCompetition(object);
            Log.i(TAG, "Successfully loaded current competition: " + currComp.getEventCode());
            return currComp;
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
        catch (IOException | JSONException e)
        {
            Log.e(TAG, "Error loading current competition: " + e.getMessage(), e);
            throw e;
        }
    }
}
