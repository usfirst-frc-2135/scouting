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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class CompetitionDataSerializer
{
    private static final String TAG = "CompetitionDataSerializer";
    private static final String DEVICE_DATA_PATH = "/data/user/0/com.frc2135.android.frc_scout/files";

    // Data members
    private final Context m_context;

    public CompetitionDataSerializer(Context context)
    {
        m_context = context;
    }

    // Takes the JSONArray data from thebluealliance.com event matches and writes it out to <eventCode>_matches.json file.
    public void saveEventData(JSONArray compData) throws JSONException, IOException
    {

        // Writes out the given compData JSONArray to the '<eventCode>matches.json' file.
        Log.d(TAG, "saveEventData() starting");
        Writer compWriter = null;
        try
        {
            File file1 = new File(DEVICE_DATA_PATH + "/" + CurrentCompetition.get(m_context).getEventCode() + "matches.json");
            OutputStream out = new FileOutputStream(file1);
            compWriter = new OutputStreamWriter(out);
            compWriter.write(compData.toString());
            Log.d(TAG, "File created: " + DEVICE_DATA_PATH + "/" + CurrentCompetition.get(m_context).getEventCode() + "matches.json");
        }
        finally
        {
            if (compWriter != null)
            {
                compWriter.close();
            }
        }
    }

    public void saveCurrentCompetition(JSONObject compJSON) throws IOException
    {
        // Write out current_competition.json file.
        Log.d(TAG, "saveCurrentCompetition() starting");
        Writer compWriter = null;
        try
        {
            File fileC = new File(DEVICE_DATA_PATH + "/current_competition.json");
            OutputStream out = new FileOutputStream(fileC);
            compWriter = new OutputStreamWriter(out);
            compWriter.write(compJSON.toString());
            Log.d(TAG, "File created: " + DEVICE_DATA_PATH + "/current_competition.json");
        }
        catch (FileNotFoundException err)
        {
            err.printStackTrace();
        }
        catch (IOException err2)
        {
            err2.printStackTrace();
        }
        finally
        {
            if (compWriter != null)
            {
                compWriter.close();
            }
        }
    }

    public CurrentCompetition loadCurrentComp() throws IOException, JSONException
    {
        // Reads in existing current_competition.json file on the device.
        Log.d(TAG, "loadCurrentComp() starting");
        BufferedReader reader = null;
        CurrentCompetition currComp = null;
        File dirpath = new File(DEVICE_DATA_PATH);
        File[] filelist = dirpath.listFiles();
        if (filelist != null)
        {
            // Go thru files to find current_competition.json file, then load it if found.
            for (File filex : filelist)
            {
                String filename = filex.getName().trim();
                if (filename.equals("current_competition.json"))
                {
                    try
                    {
                        InputStream in = m_context.openFileInput(filename);
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder jsonString = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null)
                        {
                            jsonString.append(line);
                        }
                        JSONObject object = (JSONObject) new JSONTokener(jsonString.toString()).nextValue();
                        currComp = new CurrentCompetition(object);
                        Log.d(TAG, "Loaded current competition file: " + filename);
                    }
                    catch (FileNotFoundException err)
                    {
                        Log.e(TAG, "ERROR loading current_competition.json: " + err);
                    }
                    catch (IOException err2)
                    {
                        err2.printStackTrace();
                    }
                    finally
                    {
                        if (reader != null)
                        {
                            reader.close();
                        }
                    }
                    break;
                }
            }
        }
        return currComp;
    }
}
