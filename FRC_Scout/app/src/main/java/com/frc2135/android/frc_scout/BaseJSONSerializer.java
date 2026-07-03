package com.frc2135.android.frc_scout;

import android.content.Context;

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

/**
 * Base class for JSON serialization and persistence.
 * Provides common utility methods for reading and writing JSON data to internal storage.
 */
public abstract class BaseJSONSerializer
{
    //    private static final String TAG = "BaseJSONSerializer";
    protected final File m_dataDir;

    /**
     * Constructs a BaseJSONSerializer.
     *
     * @param context the context used to retrieve the internal files directory
     */
    protected BaseJSONSerializer(Context context)
    {
        m_dataDir = context.getApplicationContext().getFilesDir();
    }

    /**
     * Writes a string to a file using UTF-8 encoding.
     *
     * @param file    the file to write to
     * @param content the string content to write
     * @throws IOException if writing the file fails
     */
    protected void writeStringToFile(File file, String content)
            throws IOException
    {
        try (FileOutputStream out = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
        {
            writer.write(content);
        }
    }

    /**
     * Reads a string from a file using UTF-8 encoding.
     *
     * @param file the file to read from
     * @return the string content of the file
     * @throws IOException if reading the file fails
     */
    protected String readStringFromFile(File file)
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

    /**
     * Saves a {@link JSONArray} to a file.
     *
     * @param file  the target file
     * @param array the JSONArray to save
     * @throws IOException if saving fails
     */
    protected void saveJSONArray(File file, JSONArray array)
            throws IOException
    {
        if (array == null)
        {
            return;
        }
        writeStringToFile(file, array.toString());
    }

    /**
     * Loads a {@link JSONArray} from a file.
     *
     * @param file the source file
     * @return the loaded JSONArray, or null if the file is empty or missing
     * @throws IOException   if reading fails
     * @throws JSONException if parsing fails
     */
    protected JSONArray loadJSONArray(File file)
            throws IOException, JSONException
    {
        if (!file.exists())
        {
            return null;
        }
        String jsonString = readStringFromFile(file);
        if (jsonString.isEmpty())
        {
            return null;
        }
        return (JSONArray) new JSONTokener(jsonString).nextValue();
    }

    /**
     * Saves a {@link JSONObject} to a file.
     *
     * @param file   the target file
     * @param object the JSONObject to save
     * @throws IOException if saving fails
     */
    @SuppressWarnings("unused")
    protected void saveJSONObject(File file, JSONObject object)
            throws IOException
    {
        if (object == null)
        {
            return;
        }
        writeStringToFile(file, object.toString());
    }

    /**
     * Loads a {@link JSONObject} from a file.
     *
     * @param file the source file
     * @return the loaded JSONObject, or null if the file is empty or missing
     * @throws IOException   if reading fails
     * @throws JSONException if parsing fails
     */
    @SuppressWarnings("unused")
    protected JSONObject loadJSONObject(File file)
            throws IOException, JSONException
    {
        if (!file.exists())
        {
            return null;
        }
        String jsonString = readStringFromFile(file);
        if (jsonString.isEmpty())
        {
            return null;
        }
        return (JSONObject) new JSONTokener(jsonString).nextValue();
    }
}
