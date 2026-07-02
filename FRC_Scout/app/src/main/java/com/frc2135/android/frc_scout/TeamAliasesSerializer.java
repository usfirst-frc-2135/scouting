package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
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

/**
 * Serializer class for retrieving and saving team aliases data to persistent storage.
 */
public class TeamAliasesSerializer
{
    private static final String TAG = "TeamAliasesSerializer";
    private static final String FILENAME_SUFFIX = "_teamAliases.json";

    private final File m_dataDir;

    /**
     * Constructs a TeamAliasesSerializer.
     *
     * @param context the context used to retrieve the internal files directory
     */
    public TeamAliasesSerializer(Context context)
    {
        Log.d(TAG, "TeamAliasesSerializer constructor");
        m_dataDir = context.getFilesDir();
        Log.d(TAG, "Initialized with data directory: " + m_dataDir.getAbsolutePath());
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
     * Saves the provided JSONArray of aliases data to a JSON file on the device.
     *
     * @param eventCode the FRC event code
     * @param aliasData the JSONArray containing team-to-alias mapping data
     * @throws IOException if an error occurs during file writing
     */
    public void saveTeamAliases(String eventCode, JSONArray aliasData)
            throws IOException
    {
        if (eventCode == null || aliasData == null)
        {
            Log.w(TAG, "Attempted to save aliases with null eventCode or data");
            return;
        }

        String filename = getFilename(eventCode);
        Log.d(TAG, "Saving aliases info to: " + filename);
        File file = new File(m_dataDir, filename);

        try (FileOutputStream out = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
        {
            writer.write(aliasData.toString());
            Log.i(TAG, "Successfully saved aliases data to " + file.getAbsolutePath());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error saving aliases file: " + filename, e);
            throw e;
        }
    }

    /**
     * Loads the team aliases from the specified file.
     *
     * @param eventCode the FRC event code
     * @return the loaded JSONArray, or null if the file doesn't exist
     * @throws IOException   if an error occurs during file reading
     * @throws JSONException if the file content is not a valid JSON array
     */
    public JSONArray loadTeamAliases(String eventCode)
            throws IOException, JSONException
    {
        if (eventCode == null)
        {
            return null;
        }

        String filename = getFilename(eventCode);
        File file = new File(m_dataDir, filename);
        if (!file.exists())
        {
            Log.d(TAG, "Aliases file does not exist: " + filename);
            return null;
        }

        StringBuilder jsonString = new StringBuilder();
        try (FileInputStream in = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                jsonString.append(line);
            }
        }

        return (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
    }

    /**
     * Deletes the aliases file from internal storage.
     *
     * @param eventCode the FRC event code
     * @return true if the file was successfully deleted, false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean deleteTeamAliases(String eventCode)
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
                Log.i(TAG, "Successfully deleted aliases file: " + filename);
            }
            else
            {
                Log.w(TAG, "Failed to delete aliases file: " + filename);
            }
            return deleted;
        }
        return false;
    }
}
