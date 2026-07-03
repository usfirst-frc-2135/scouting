package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

/**
 * Serializer class for retrieving and saving team aliases data to persistent storage.
 */
public class TeamAliasesSerializer extends BaseJSONSerializer
{
    private static final String TAG = "TeamAliasesSerializer";
    private static final String FILENAME_SUFFIX = "_teamAliases.json";

    /**
     * Constructs a TeamAliasesSerializer.
     *
     * @param context the context used to retrieve the internal files directory
     */
    public TeamAliasesSerializer(Context context)
    {
        super(context);
        Log.d(TAG, "TeamAliasesSerializer constructor");
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
        saveJSONArray(file, aliasData);
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
        return loadJSONArray(file);
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
