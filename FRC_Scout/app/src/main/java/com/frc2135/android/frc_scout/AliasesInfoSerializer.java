package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Serializer class for retrieving and saving team aliases data to persistent storage.
 */
public class AliasesInfoSerializer
{
    private static final String TAG = "AliasesInfoSerializer";

    private final String m_dataPath;

    /**
     * Constructs an AliasesInfoSerializer.
     *
     * @param context the context used to retrieve the internal files directory
     */
    public AliasesInfoSerializer(Context context)
    {
        m_dataPath = context.getFilesDir().getPath();
        Log.d(TAG, "Data files directory: " + m_dataPath);
    }

    /**
     * Saves the provided JSONArray of aliases data to a JSON file on the device.
     *
     * @param filename  the name of the file to save (e.g., "eventCode_aliases.json")
     * @param aliasData the JSONArray containing team-to-alias mapping data
     * @throws IOException if an error occurs during file writing
     */
    public void saveAliasesInfo(String filename, JSONArray aliasData)
            throws IOException
    {
        if (filename == null || aliasData == null)
        {
            Log.w(TAG, "Attempted to save aliases with null filename or data");
            return;
        }

        Log.d(TAG, "Saving aliases info to: " + filename);
        File file = new File(m_dataPath, filename);

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
}
