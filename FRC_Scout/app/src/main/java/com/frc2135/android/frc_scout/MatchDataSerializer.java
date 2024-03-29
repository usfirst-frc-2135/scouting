package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.ArrayList;

public class MatchDataSerializer
{
    private static final String TAG = "MatchDataSerializer";
    private final Context m_Context;
    private final String m_FileName;
    private final String m_dataPath;
    private Scouter m_Scouter;

    public MatchDataSerializer(Context context, String fName)
    {
        m_Context = context;
        m_FileName = fName;
        m_dataPath = m_Context.getFilesDir().getPath();
        Log.d(TAG, "Data files path = " + m_dataPath);
    }

    public void saveScouterData() throws JSONException, IOException
    {
        Log.d(TAG, "saveScouterData() starting");

        JSONArray arrayS = new JSONArray(); // Creates a JSON array object to hold the Scouter data 
        arrayS.put(Scouter.get(m_Context).toJSON());
        Writer writerS = null;
        try
        {
            OutputStream out = m_Context.openFileOutput(m_FileName, Context.MODE_PRIVATE);
            writerS = new OutputStreamWriter(out);
            writerS.write(arrayS.toString());
            Log.d(TAG, "Wrote Scouter file: " + m_FileName);
        } catch (FileNotFoundException e)
        {
            Log.d(TAG, "ERROR writing Scouter file: " + e);
        } finally
        {
            if (writerS != null)
            {
                writerS.close();
            }
        }
    }

    public void saveMatchData(MatchData matchData1) throws JSONException, IOException
    {
        // Save this MatchData to JSON file.
        JSONArray array = new JSONArray();
        array.put(matchData1.toJSON());
        Writer writerMatches = null;
        String matchFileName = matchData1.getMatchFileName();
        String fullPathname = m_dataPath + "/" + matchFileName;
        try
        {
            File fileM = new File(fullPathname);
            OutputStream out = new FileOutputStream(fileM);
            writerMatches = new OutputStreamWriter(out);
            writerMatches.write(array.toString());
            Log.d(TAG, "Wrote match file: " + fileM.getName());
        } catch (FileNotFoundException e)
        {
            Log.d(TAG, "ERROR writing Match file " + matchFileName + ": " + e);
        } finally
        {
            if (writerMatches != null)
            {
                writerMatches.close();
            }
        }
    }

/*---> REMOVE - this is never used
    public void saveAllMatchData(ArrayList<MatchData> matchHistory) throws JSONException, IOException
    {
        Log.d(TAG, "saveAllMatchData() going to save MatchHistory matches to JSON files");

        // Write out all matchData files.
        for (MatchData matchData1 : matchHistory)
        {
            saveMatchData(matchData1);
        }
    }
<--- REMOVE*/

/*---> REMOVE - this is never used
    public void saveAllData(ArrayList<MatchData> matchHistory) throws JSONException, IOException
    {
        Log.d(TAG, "saveAllData() going to save Scouter and MatchHistory matches to JSON files");
        saveScouterData();
        saveAllMatchData(matchHistory);
    }
<--- REMOVE*/

    public ArrayList<MatchData> loadMatchData() throws IOException, JSONException
    {
        // Create a new MatchHistory obj and load it with all the existing match files.
        ArrayList<MatchData> matchHistory = new ArrayList<>();
        BufferedReader reader = null;
        File file = new File(m_dataPath); // dir to use

        Log.d(TAG, "Going to read in existing match files found at " + m_dataPath);
        File[] jFilesList = file.listFiles();
        if (jFilesList != null)
        {
            for (File tFile : jFilesList)
            {
                String filename = tFile.getName();

                // Look for 30+ filename length - these are matchData JSON files
                if (filename.length() > 30)
                {
                    try
                    {
                        // Read in matchData JSON file.
                        InputStream in = m_Context.openFileInput(filename.trim());
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder jsonString = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null)
                        {
                            //Line breaks are omitted and irrelevant
                            jsonString.append(line);
                        }
                        JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

                        // Add this match to matchHistory
                        matchHistory.add(new MatchData(array.getJSONObject(0)));
                        Log.d(TAG, "Reading in file: " + filename);
                    } catch (FileNotFoundException e)
                    {
                        //ignore this one; it happens when starting fresh
                        Log.e(TAG, "ERROR in loading file " + filename + ": " + e);
                    } finally
                    {
                        if (reader != null)
                        {
                            reader.close();
                        }
                    }
                }
                else
                    Log.d(TAG, "Ignoring file: " + filename);
            }
        }
        return matchHistory;
    }

    public Scouter loadScouterData() throws IOException, JSONException
    {
        Log.d(TAG, "loadScouterData(): m_FileName = " + m_FileName);
        BufferedReader reader = null;
        try
        {
            //Open and read the file into a StringBuilder
            InputStream in = m_Context.openFileInput(m_FileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null)
            {
                //Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            //Parse the JSON.
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            //Build the array of matches from JSONObjects
            Log.d(TAG, "Loaded Scouter data from file: " + m_FileName);
            m_Scouter = new Scouter(array.getJSONObject(0));
            if (m_Scouter.getPastScouts() != null)
            {
                Log.d(TAG, "Most recent past scout = " + m_Scouter.getPastScouts()[0]);
                Log.d(TAG, "Past teamIndexStr = " + m_Scouter.getTeamIndexStr());
            }
        } catch (FileNotFoundException e)
        {
            //ignore this one; it happens when starting fresh
        } finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        return m_Scouter;
    }
}
