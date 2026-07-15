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
    @SuppressWarnings("unused")
    private static final String TAG = "BaseJSONSerializer";
    protected final File m_dataDir;
    protected final Context m_appContext;

    /**
     * Default constructor for internal use when file operations are not required.
     */
    @SuppressWarnings("unused")
    protected BaseJSONSerializer()
    {
        m_dataDir = null;
        m_appContext = null;
    }

    /**
     * Constructs a BaseJSONSerializer.
     *
     * @param context the context used to retrieve the internal files directory
     */
    protected BaseJSONSerializer(Context context)
    {
        m_appContext = context.getApplicationContext();
        m_dataDir = m_appContext.getFilesDir();
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
            for (String line = reader.readLine(); line != null; line = reader.readLine())
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
        if (file == null || !file.exists())
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
        if (file == null || !file.exists())
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

    /**
     * Log and optionally display an error message for an exception.
     *
     * @param context the context to show the Toast in
     * @param tag     the log tag
     * @param msg     the error message
     * @param bSilent if true, the Toast is suppressed
     * @param e       the exception that occurred
     */
    protected void displayToastMessages(Context context, String tag, String msg, boolean bSilent, Exception e)
    {
        int length;
        if (e == null)
        {
            length = Toast.LENGTH_SHORT;
            Log.i(tag, msg);
        }
        else
        {
            length = Toast.LENGTH_LONG;
            Log.e(tag, msg, e);
        }

        if (context != null && !bSilent)
        {
            Toast.makeText(context, msg, length).show();
        }
    }
}
