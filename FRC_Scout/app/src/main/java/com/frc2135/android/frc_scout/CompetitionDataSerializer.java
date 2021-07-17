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
    private Context mContext;
    private String mFileName;
    private CurrentCompetition c;

    public CompetitionDataSerializer(Context c, String f) {
        mContext = c;
        mFileName = f;
    }

    public void saveEventData(JSONArray compData) throws JSONException, IOException {
        Log.d(TAG, "saveData called");
        //JSONArray arrayScouter = new JSONArray(); //Creates a JSON array object to store the data of each match
        //arrayScouter.put(Scouter.get(mContext).toJSON());


        Writer writerScouter = null;
        try {
            OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);//This method(openFileOutput) takes a file name and a mode and uses both to create a pathway and a file to open for writing
            writerScouter = new OutputStreamWriter(out); // This handles converting the written string data to byte code
            writerScouter.write(compData.toString());
        } finally {
            if (writerScouter != null) {
                writerScouter.close();
            }
        }


    }

    public void saveCurrentCompetition(JSONObject compJSON) throws IOException {
        Writer writerScouter = null;
        try {
            OutputStream out = mContext.openFileOutput("current_competition.json", Context.MODE_PRIVATE);//This method(openFileOutput) takes a file name and a mode and uses both to create a pathway and a file to open for writing
            writerScouter = new OutputStreamWriter(out); // This handles converting the written string data to byte code
            writerScouter.write(compJSON.toString());
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

    /*public ArrayList<MatchData> loadEventKeys() throws IOException, JSONException {
        Log.d(TAG, "json being read into matchHistory");
        ArrayList<MatchData> matchHistory = new ArrayList<MatchData>();
        BufferedReader reader = null;

        File file = new File("/data/user/0/com.frc2135.android.frc_scout/files");
        File[] test = file.listFiles();
        if (test != null) {
            for (File f : test) {
                Log.d(TAG, f.toString() + "*");
                if (!f.getName().equals("Scouter.json")) {
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
    }*/

    public ArrayList<MatchData> loadEventMatches(String eventKey) throws IOException, JSONException {
        Log.d(TAG, "json being read into matchHistory");
        ArrayList<MatchData> matchHistory = new ArrayList<MatchData>();
        BufferedReader reader = null;

        File file = new File("/data/user/0/com.frc2135.android.frc_scout/files");
        File[] test = file.listFiles();
        if (test != null) {
            for (File f : test) {
                Log.d(TAG, f.toString() + "*");
                if (!f.getName().equals("Scouter.json")) {
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

    public CurrentCompetition loadCurrentComp() throws IOException, JSONException {
        Log.d(TAG, "json being read into matchHistory");
        ArrayList<MatchData> matchHistory = new ArrayList<MatchData>();
        BufferedReader reader = null;

        File file = new File("/data/user/0/com.frc2135.android.frc_scout/files");
        File[] test = file.listFiles();
        if (test != null) {
            for (File f : test) {
                Log.d(TAG, f.toString() + "*");
                if (f.getName().equals("current_competition.json")) {
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
                        JSONObject object = (JSONObject) new JSONTokener(jsonString.toString()).nextValue();
                        //Build the array of matches from JSONObjects
                        c = new CurrentCompetition(object);
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

        return c;
    }


}
