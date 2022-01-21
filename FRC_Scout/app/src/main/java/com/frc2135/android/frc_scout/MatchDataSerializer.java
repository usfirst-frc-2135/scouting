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

public class MatchDataSerializer {
    private static final String TAG = "MatchDataSerializer";
    private final Context m_Context;
    private final String m_FileName;
    private Scouter m_Scouter;

    public MatchDataSerializer(Context c, String f){
        m_Context = c;
        m_FileName = f;
    }

    public void saveData(ArrayList<MatchData> matchHistory) throws JSONException, IOException {
        Log.d(TAG, "saveData() going to save MatchHistory matches to JSON files");

        JSONArray arrayS = new JSONArray(); //Creates a JSON array object to hold the Scouter data 
        arrayS.put(Scouter.get(m_Context).toJSON());
        Writer writerS = null;
        try{
            //This method(openFileOutput) takes a file name and a mode and uses both to create a pathway and a file to open for writing
            OutputStream out = m_Context.openFileOutput(m_FileName, Context.MODE_PRIVATE);
            // This handles converting the written string data to byte code
            writerS = new OutputStreamWriter(out); 
            writerS.write(arrayS.toString());
            Log.d(TAG, "Wrote Scouter file: "+ m_FileName);
        }
        finally{
            if(writerS != null){
                writerS.close();
            }
        }

        // Write out all matchData files.
        for(MatchData mdata: matchHistory){
            JSONArray array = new JSONArray(); // Create a JSON array obj to hold the MatchDatas from MatchHistory
            array.put(mdata.toJSON());
            Writer writerMatches = null;
            try{
                Log.d(TAG, "Saving match to" + mdata.getMatchFileName());
                File fileM = new File("/data/user/0/com.frc2135.android.frc_scout/files"+"/"+mdata.getMatchFileName());
                OutputStream out = new FileOutputStream(fileM);//This method(openFileOutput) takes a file name and a mode and uses both to create a pathway and a file to open for writing
                writerMatches = new OutputStreamWriter(out); // This handles converting the written string data to byte code
                writerMatches.write(array.toString());
//REMOVE                Log.d(TAG, array.toString() + "@");
//REMOVE                Log.d(TAG, fileM.getAbsolutePath() + "%");
                Log.d(TAG, "Wrote match file: "+ fileM.getName());
            }
            finally{
                if(writerMatches != null){
                    writerMatches.close();
                }
            }

        }

    }

    public ArrayList<MatchData> loadMatchData()throws IOException, JSONException {
       // Create a new MatchHistory obj and load it with all the existing match files.
       ArrayList<MatchData> matchHistory = new ArrayList<MatchData>();
       BufferedReader reader = null;
       File file = new File("/data/user/0/com.frc2135.android.frc_scout/files"); // dir to use

       Log.d(TAG, "Going to read in files found at /data/user/0/com.frc2135.android.frc_scout/files");
       File[] jfilesList = file.listFiles();
       if(jfilesList != null) {
           for(File tfile: jfilesList) {
               if(tfile.getName().length()>30) {
                   try {
                       // Open and read the file into a StringBuilder
                       InputStream in = m_Context.openFileInput(tfile.getName().trim());
                       reader = new BufferedReader(new InputStreamReader(in));
                       StringBuilder jsonString = new StringBuilder();
                       String line = null;

                       while ((line = reader.readLine()) != null) {
                           //Line breaks are omitted and irrelevant
                           jsonString.append(line);
                       }

                       //Parse the JSON using JSONTokener
                       JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

                       //Build the array of matches from JSONObjects
                       matchHistory.add(new MatchData(array.getJSONObject(0)));
                       Log.d(TAG, "Reading in file: "+ tfile.getName());
                   } catch (FileNotFoundException e) {
                       //ignore this one; it happens when starting fresh
                       Log.e(TAG, e.toString());
                   } finally {
                       if (reader != null) {
                           reader.close();
                       }
                   }
               }
               else Log.d(TAG, "Ignoring file: "+ tfile.getName());

           }
       }
       return matchHistory;
    }

    public Scouter loadScouterData()throws IOException, JSONException {
        Log.d(TAG, "loadScouterData(): m_FileName = "+m_FileName.toString());
        ArrayList<MatchData> matchHistory = new ArrayList<MatchData>();
        BufferedReader reader = null;
        try {
            //Open and read the file into a StringBuilder
            InputStream in = m_Context.openFileInput(m_FileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                //Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            //Parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            //Build the array of matches from JSONObjects
            m_Scouter = new Scouter(array.getJSONObject(0));
            if(m_Scouter.getPastScouts() != null){
                Log.d(TAG, "Loaded Scouter data from file: "+m_FileName);
                Log.d(TAG, "Past scouts = "+m_Scouter.getPastScouts()[0]);
            }

        } catch (FileNotFoundException e) {
            //ignore this one; it happens when starting fresh
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    return m_Scouter;
    }
}
