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
    private static final String TAG = "MatchDataJSONSerializer";
    private final Context mContext;
    private final String mFileName;
    private Scouter s;

    public MatchDataSerializer(Context c, String f){
        mContext = c;
        mFileName = f;
    }

    public void saveData(ArrayList<MatchData> matchHistory) throws JSONException, IOException {
        Log.d(TAG, "saveData called");
        JSONArray arrayScouter = new JSONArray(); //Creates a JSON array object to store the data of each match

        arrayScouter.put(Scouter.get(mContext).toJSON());

        Writer writerScouter = null;
        try{
            OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);//This method(openFileOutput) takes a file name and a mode and uses both to create a pathway and a file to open for writing
            writerScouter = new OutputStreamWriter(out); // This handles converting the written string data to byte code
            writerScouter.write(arrayScouter.toString());
        }
        finally{
            if(writerScouter != null){
                writerScouter.close();
            }
        }

        for(MatchData c: matchHistory){
            JSONArray array = new JSONArray();
            array.put(c.toJSON());
            Log.d(TAG, "Data converted to JSON format and added to an array");
            Writer writerMatches = null;
            try{
                Log.d(TAG, "Match saved to" + c.getMatchFileName());
                Log.d(TAG, "====>> Match saved to" + c.getMatchFileName());
                File f = new File("/data/user/0/com.frc2135.android.frc_scout/files"+"/"+c.getMatchFileName());
                OutputStream out = new FileOutputStream(f);//This method(openFileOutput) takes a file name and a mode and uses both to create a pathway and a file to open for writing
                writerMatches = new OutputStreamWriter(out); // This handles converting the written string data to byte code
                writerMatches.write(array.toString());
                Log.d(TAG, array.toString() + "@");
                Log.d(TAG, f.getAbsolutePath() + "%");
            }
            finally{
                if(writerMatches != null){
                    writerMatches.close();
                }
            }

        }

    }

    public ArrayList<MatchData> loadMatchData()throws IOException, JSONException {
        Log.d(TAG, "json being read into matchHistory");
        ArrayList<MatchData> matchHistory = new ArrayList<MatchData>();
        BufferedReader reader = null;

       File file = new File("/data/user/0/com.frc2135.android.frc_scout/files");
       File[] test = file.listFiles();
       if(test != null){
           for(File f: test){
               Log.d(TAG, f.toString() + "*");

               if(f.getName().length()>30){
                   try {
                       //Open and read the file into a StringBuilder
                       InputStream in = mContext.openFileInput(f.getName().trim());
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
                   } catch (FileNotFoundException e) {
                       //ignore this one; it happens when starting fresh
                       Log.e(TAG, e.toString());
                   } finally {
                       if (reader != null) {
                           reader.close();
                       }
                   }
               }

           }
       }

        return matchHistory;
    }

    public Scouter loadScouterData()throws IOException, JSONException {
        Log.d(TAG, "json being read into Scouter");
        Log.d(TAG, "===> mFileName = "+mFileName.toString());
        ArrayList<MatchData> matchHistory = new ArrayList<MatchData>();
        BufferedReader reader = null;
        try {
            //Open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(mFileName);
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
            s=new Scouter(array.getJSONObject(0));
            if(s.getPastScouts() != null)
              Log.d(TAG, s.getPastScouts()[0]);


        } catch (FileNotFoundException e) {
            //ignore this one; it happens when starting fresh
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    return s;
    }
}
