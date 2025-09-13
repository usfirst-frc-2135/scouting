package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;

public class AliasesSerializer
{
    private static final String TAG = "AliasesSerializer";

    // Data members
    private final Context m_context;
    private final String m_dataPath;

    public AliasesSerializer(Context context)
    {
        m_context = context;
        m_dataPath = m_context.getFilesDir().getPath();
        Log.i(TAG, "Data files dir = " + m_dataPath);
    }

    // Takes the JSONArray data from Aliases file on scouting website and writes it out 
    // to <eventCode>_teamAliases.json file on kindle device.
    public void saveAliasesData(String aliasFileBaseName,JSONArray aliasData) throws JSONException, IOException
    {
        Log.d(TAG, "saveAliasesData() starting");
        Writer aliasWriter = null;
        try
        {
            File file1 = new File(m_dataPath + "/" + aliasFileBaseName);
            Log.i(TAG, "Alias file path on device: = " + m_dataPath+"/" + aliasFileBaseName);

            OutputStream out = Files.newOutputStream(file1.toPath());
            aliasWriter = new OutputStreamWriter(out);
            aliasWriter.write(aliasData.toString());
            Log.d(TAG, "Aliases Device Data File saved on device: " + file1);
        } finally
        {
            if (aliasWriter != null)
            {
                aliasWriter.close();
            }
        }
    }
}
