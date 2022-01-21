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
import java.util.ArrayList;

public class CompetitionDataSerializer {
    private static final String TAG = "CompetitionDataJSONSerializer";
    private final Context mContext;
    private final String mFileName;
    private CurrentCompetition c;

    public CompetitionDataSerializer(Context c, String f) {
        mContext = c;
        mFileName = f;
    }

    public void saveEventData(JSONArray compData) throws JSONException, IOException {
        // Writes out the given compData JSONArray to the '<eventCode>matches.json' file.
        Log.d(TAG, "saveEventData() starting");
        Writer writerScouter = null;
        try {
            File f = new File("/data/user/0/com.frc2135.android.frc_scout/files/"+CurrentCompetition.get(mContext).getEventCode()+"matches.json");
            OutputStream out = new FileOutputStream(f);//This method(openFileOutput) takes a file name and a mode and uses both to create a pathway and a file to open for writing
            writerScouter = new OutputStreamWriter(out); // This handles converting the written string data to byte code
            writerScouter.write(compData.toString());
            Log.d(TAG, "File created: /data/user/0/com.frc2135.android.frc_scout/files/"+CurrentCompetition.get(mContext).getEventCode()+"matches.json");
        } finally {
            if (writerScouter != null) {
                writerScouter.close();
            }
        }
    }

    public void saveCurrentCompetition(JSONObject compJSON) throws IOException {
        Log.d(TAG, "saveCurrentCompetition() starting");
        Writer writerScouter = null;
        // Write out current_competition.json file
        try {
            File fileC = new File("/data/user/0/com.frc2135.android.frc_scout/files/current_competition.json");
            //This method (openFileOutput) takes a file name and a mode and uses both to create a pathway and a file to open for writing.
            OutputStream out = new FileOutputStream(fileC);  
            writerScouter = new OutputStreamWriter(out); // This handles converting the written string data to byte code
            writerScouter.write(compJSON.toString());
            Log.d(TAG, "File created: /data/user/0/com.frc2135.android.frc_scout/files/current_competition.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writerScouter != null) {
                writerScouter.close();
            }
        }
    }


    public CurrentCompetition loadCurrentComp() throws IOException, JSONException {
        // Going to load existing current_competitionjson file");
        Log.d(TAG, "loadCurrentComp() starting");
//REMOVE        ArrayList<MatchData> matchHistory = new ArrayList<MatchData>();
        BufferedReader reader = null;

        CurrentCompetition currComp = null;
        File file = new File("/data/user/0/com.frc2135.android.frc_scout/files");
        File[] test = file.listFiles();
        if (test != null) {
            for (File file1 : test) {
                if (file1.getName().equals("current_competition.json")) {
                    try {
                        //Open and read the file into a StringBuilder
                        InputStream in = mContext.openFileInput(file1.getName().trim());
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder jsonString = new StringBuilder();
                        String line = null;

                        while ((line = reader.readLine()) != null) {
                            //Line breaks are omitted and irrelevant
                            jsonString.append(line);
                        }

                        //Parse the JSON using JSONTokener
                        JSONObject object = (JSONObject) new JSONTokener(jsonString.toString()).nextValue();

                        //Build the array of matches from JSONObjects
                        currComp = new CurrentCompetition(object);
                        Log.d(TAG, "Loaded current competition file: "+file1.toString());
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
        return currComp;
    }
}
