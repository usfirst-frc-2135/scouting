package com.bignerdranch.android.qrgen_new;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MatchDataSerializer {
    private static final String TAG = "MatchDataJSONSerializer";
    private Context mContext;
    private String mFileName;
    private Scouter s;

    public MatchDataSerializer(Context c, String f){
        mContext = c;
        mFileName = f;
    }

    public void saveData(ArrayList<MatchData> matchHistory) throws JSONException, IOException {
        Log.d(TAG, "saveData called");
        JSONArray array = new JSONArray(); //Creates a JSON array object to store the data of each match

        array.put(Scouter.get(mContext).toJSON());

        for(MatchData c: matchHistory){
            array.put(c.toJSON());
            Log.d(TAG, "Data converted to JSON format and added to an array");
        }

        Writer writer = null;
        try{
            OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);//This method(openFileOutput) takes a file name and a mode and uses both to create a pathway and a file to open for writing
            writer = new OutputStreamWriter(out); // This handles converting the written string data to byte code
            writer.write(array.toString());
        }
        finally{
            if(writer != null){
                writer.close();
            }
        }
    }

    public ArrayList<MatchData> loadMatchData()throws IOException, JSONException {
        Log.d(TAG, "json being read into matchHistory");
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
            //Build the array of crimes from JSONObjects
            for (int i = 1; i < array.length(); i++) {
                matchHistory.add(new MatchData(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            //ignore this one; it happens when starting fresh
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return matchHistory;
    }

    public Scouter loadScouterData()throws IOException, JSONException {
        Log.d(TAG, "json being read into Scouter");
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
            //Build the array of crimes from JSONObjects
            s=new Scouter(array.getJSONObject(0));
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
