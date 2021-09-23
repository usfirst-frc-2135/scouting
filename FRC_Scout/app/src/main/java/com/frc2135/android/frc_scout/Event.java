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

    private final String eventCode;
    private String eventName;
    private JSONArray array;
    private final Context mAppContext;

    public Event(Context c, String eC){
        mAppContext = c;
        eventCode = eC;

        File file = new File("/data/user/0/com.frc2135.android.frc_scout/files/"+eventCode.trim().toLowerCase()+"matches.json");
        BufferedReader reader = null;

        try {
            //Open and read the file into a StringBuilder
            InputStream in = mAppContext.openFileInput(file.getName().trim());
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                //Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            //Parse the JSON using JSONTokener
            array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

        } catch (FileNotFoundException e) {
            //ignore this one; it happens when starting fresh
            Log.e("Event", e.toString());
            Toast.makeText(mAppContext, "Event file not found", Toast.LENGTH_LONG);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


    }


    public String[] getEventMatches(String ec) throws JSONException, IOException {
        if(array != null){
            String[] matches = new String[array.length()+1];
            matches[0] = "No match selected";
            if(ec.equals(eventCode)){
                for(int i= 1; i < array.length(); i++){
                    matches[i] = ((JSONObject)array.get(i)).getString("comp_level") + ((JSONObject)array.get(i)).getString("match_number");
                }
                return matches;
            }
        }
        return null;
    }

    public String[] getTeams(String m) throws JSONException {
        Log.d("Event", "getTeams() called");
        int t =0;
        JSONObject tempB = new JSONObject();
        JSONObject tempR = new JSONObject();
        JSONArray redTeams = new JSONArray();
        JSONArray blueTeams = new JSONArray();
        String[] teams = new String[7];
        for(int i= 0; i < array.length(); i++){
           if((((JSONObject)array.get(i)).getString("comp_level") + ((JSONObject)array.get(i)).getString("match_number")).equals(m.trim())){
               Log.d("Event", "match found");
               JSONObject alliances = (JSONObject)array.get(i);
               JSONObject color = (JSONObject)alliances.get("alliances");
               tempB = (JSONObject)color.get("blue");
               blueTeams = (JSONArray)tempB.get("team_keys");
               tempR = (JSONObject)color.get("red");
               redTeams = (JSONArray)tempR.get("team_keys");
            }

        }
        for(int i = 1; i<4; i++){
           teams[0]="No team selected";
           teams[i]= redTeams.getString(i-1);
        }
        for(int i = 4; i <7; i++){
            teams[i]=blueTeams.getString(i-4);
        }

        return teams;
    }


}
