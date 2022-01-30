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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Event {

    public static final String TAG = "Event";
    private final String m_eventCode;
    private String m_eventName;
    private JSONArray m_array;
    private final Context m_appContext;
    private boolean m_bEventDataLoaded;

    public Event(Context context, String eC){
        m_appContext = context;
        m_eventCode = eC;
        m_bEventDataLoaded = false;
        m_array = null;
 
        String filename = "/data/user/0/com.frc2135.android.frc_scout/files/"+m_eventCode.trim().toLowerCase()+"matches.json";
        Log.d(TAG, "Event constructor: going to read in file: "+filename);
        File file = new File(filename);
        BufferedReader reader = null;

        try {
            //Open and read the file into a StringBuilder
            InputStream in = m_appContext.openFileInput(file.getName().trim());
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                //Line breaks are omitted and irrelevant
                jsonString.append(line);
            }

            //Parse the JSON using JSONTokener
            m_array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            Log.d(TAG, "Event constructor: setting m_bEventDataLoaded = true");
            m_bEventDataLoaded = true;

        } catch (FileNotFoundException err) {
            //ignore this one; it happens when starting fresh
            Log.e(TAG, err.toString());
            Toast.makeText(m_appContext, "Event file not found", Toast.LENGTH_LONG);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public boolean isEventDataLoaded() {
        Log.d(TAG, "isEventDataLoaded() returning "+ m_bEventDataLoaded);
        return m_bEventDataLoaded;
    }

    public String[] getEventMatches(String ec) throws JSONException, IOException {
        if(m_array != null) {
            String[] matches = new String[m_array.length()+1];
            matches[0] = "No match selected";
            if(ec.equals(m_eventCode)){
                for(int i= 1; i < m_array.length(); i++){
                    matches[i] = ((JSONObject)m_array.get(i)).getString("comp_level") + ((JSONObject)m_array.get(i)).getString("match_number");
                }
                return matches;
            }
        }
        return null;
    }

    public String[] getTeams(String matchnum) throws JSONException {
        String[] teams = new String[7];
        teams[0] = "";   // initialize with empty strings
        teams[1] = "";
        teams[2] = "";
        teams[3] = "";
        teams[4] = "";
        teams[5] = "";
        teams[6] = "";
        boolean bMatchNumFound = false;
        if(m_array != null) {
            JSONObject tempB = new JSONObject();
            JSONObject tempR = new JSONObject();
            JSONArray redTeams = new JSONArray();
            JSONArray blueTeams = new JSONArray();
            for(int i= 0; i < m_array.length(); i++){
                if((((JSONObject)m_array.get(i)).getString("comp_level") + ((JSONObject)m_array.get(i)).getString("match_number")).equals(matchnum.trim())){
                     bMatchNumFound = true;
                     JSONObject alliances = (JSONObject)m_array.get(i);
                     JSONObject color = (JSONObject)alliances.get("alliances");
                     tempB = (JSONObject)color.get("blue");
                     blueTeams = (JSONArray)tempB.get("team_keys");
                     tempR = (JSONObject)color.get("red");
                     redTeams = (JSONArray)tempR.get("team_keys");
                     break;
                }
            }
            if(bMatchNumFound) {
                teams[0]="No team selected";
                for(int i = 1; i<4; i++){
                    teams[i]= redTeams.getString(i-1);
                }
                for(int i = 4; i <7; i++){
                    teams[i]=blueTeams.getString(i-4);
                }
            }
            else Log.d(TAG, "getTeams(): matchnum '"+matchnum+ "' NOT found!");
        }
        return teams;
    }
}

