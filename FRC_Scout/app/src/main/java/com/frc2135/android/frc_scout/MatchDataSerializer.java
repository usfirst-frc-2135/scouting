package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Serializer class for managing match data and scout configuration persistence.
 * Handles individual match JSON files and the main settings configuration.
 */
public class MatchDataSerializer
{
    private static final String TAG = "MatchDataSerializer";
    private final Context m_context;
    private final String m_settingsFileName;
    private final File m_dataDir;

    /**
     * Constructs a MatchDataSerializer.
     *
     * @param context the context used to access internal storage
     * @param settingsFileName the name of the main settings file
     */
    public MatchDataSerializer(Context context, String settingsFileName)
    {
        m_context = context.getApplicationContext();
        m_settingsFileName = settingsFileName;
        m_dataDir = m_context.getFilesDir();
        Log.d(TAG, "Initialized with data directory: " + m_dataDir.getAbsolutePath());
    }

    /**
     * Persists current scout names and global settings.
     *
     * @throws JSONException if configuration data serialization fails
     * @throws IOException if writing the settings file fails
     */
    public void saveScoutNames() throws JSONException, IOException
    {
        Log.d(TAG, "Saving scout names configuration");

        JSONArray array = new JSONArray();
        array.put(Settings.get(m_context).toJSON());

        File file = new File(m_dataDir, m_settingsFileName);
        try (FileOutputStream out = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
        {
            writer.write(array.toString());
            Log.i(TAG, "Successfully wrote settings file: " + m_settingsFileName);
        }
    }

    /**
     * Saves data for a single match to its unique JSON file.
     *
     * @param matchData the match record to save
     * @throws JSONException if match data serialization fails
     * @throws IOException if writing the match file fails
     */
    public void saveMatchData(MatchData matchData) throws JSONException, IOException
    {
        if (matchData == null) return;

        JSONArray array = new JSONArray();
        array.put(matchData.toJSON());

        String filename = matchData.getMatchFileName();
        File file = new File(m_dataDir, filename);

        try (FileOutputStream out = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
        {
            writer.write(array.toString());
            Log.i(TAG, "Successfully wrote match file: " + filename);
        }
    }

    /**
     * Saves all match records in the provided list.
     *
     * @param matchHistory the list of match records to save
     * @throws JSONException if any match record serialization fails
     * @throws IOException if writing any match file fails
     */
    public void saveAllMatchData(List<MatchData> matchHistory) throws JSONException, IOException
    {
        if (matchHistory == null) return;
        Log.d(TAG, "Saving " + matchHistory.size() + " matches to disk");
        for (MatchData match : matchHistory)
        {
            saveMatchData(match);
        }
    }

    /**
     * Saves both global settings and all provided match records.
     *
     * @param matchHistory the list of match records to save
     * @throws JSONException if serialization fails
     * @throws IOException if file writing fails
     */
    public void saveAllData(List<MatchData> matchHistory) throws JSONException, IOException
    {
        saveScoutNames();
        saveAllMatchData(matchHistory);
    }

    /**
     * Loads all individual match data files from internal storage.
     *
     * @return a list of loaded MatchData objects
     */
    public ArrayList<MatchData> loadMatchData()
    {
        ArrayList<MatchData> matchHistory = new ArrayList<>();
        Log.d(TAG, "Scanning for match data files");

        File[] files = m_dataDir.listFiles();
        if (files == null) return matchHistory;

        for (File file : files)
        {
            String filename = file.getName();
            // Match files are identified by their UUID-based filename length (usually 36 chars + .json)
            if (filename.length() > 30 && filename.endsWith(".json") && !filename.contains("matches") && !filename.equals(m_settingsFileName))
            {
                try
                {
                    matchHistory.add(loadSingleMatch(file));
                    Log.d(TAG, "Successfully loaded match file: " + filename);
                }
                catch (IOException | JSONException e)
                {
                    Log.e(TAG, "Error loading match file " + filename + ": " + e.getMessage());
                }
            }
        }
        return matchHistory;
    }

    private MatchData loadSingleMatch(File file) throws IOException, JSONException
    {
        StringBuilder jsonString = new StringBuilder();
        try (FileInputStream in = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            return new MatchData(array.getJSONObject(0));
        }
    }

    /**
     * Loads the main scout settings configuration.
     *
     * @return the loaded Settings object, or null if the file doesn't exist
     * @throws IOException if reading the file fails
     * @throws JSONException if parsing the JSON fails
     */
    public Settings loadScoutNames() throws IOException, JSONException
    {
        File file = new File(m_dataDir, m_settingsFileName);
        if (!file.exists()) return null;

        Log.d(TAG, "Loading settings from: " + m_settingsFileName);
        StringBuilder jsonString = new StringBuilder();

        try (FileInputStream in = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                jsonString.append(line);
            }
            
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            Settings settings = new Settings(array.getJSONObject(0));
            Log.i(TAG, "Successfully loaded settings file");
            return settings;
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
    }
}
