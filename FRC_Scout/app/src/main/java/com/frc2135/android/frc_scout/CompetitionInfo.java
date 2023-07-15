package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.view.View;
import android.graphics.Color;
import android.view.Gravity;

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

public class CompetitionInfo {
    private static final String TAG = "CompetitionInfo";
    private static final String DEVICE_DATA_PATH = "/data/user/0/com.frc2135.android.frc_scout/files";

    // Data members
    private String    m_eventCode;
    private String    m_eventName;
    private JSONArray m_jsonData;
    private boolean   m_bEventDataLoaded;

    private static CompetitionInfo sCompetitionInfo;

    private CompetitionInfo(String eventCode){
        m_eventCode = eventCode;
        m_bEventDataLoaded = false;
        m_jsonData = null;
    } 

    public static CompetitionInfo get(Context context,String eventCode, boolean bForceReload) {
        if(sCompetitionInfo != null) {
            String oldEventCode = sCompetitionInfo.getEventCode();
            if(bForceReload || !oldEventCode.equals(eventCode)) {
                sCompetitionInfo.setEventCode(eventCode);
                Log.d(TAG, "Resetting existing "+oldEventCode+" CompetitionInfo for new eventCode "+eventCode);
                sCompetitionInfo.readEventMatchesJSON(context,true);
            }
        }
        else if(sCompetitionInfo == null) {
            Log.d(TAG, "Creating a new sCompetitionInfo for eventCode "+eventCode);
            sCompetitionInfo = new CompetitionInfo(eventCode);
            // Read in matches json file if there is one.
            sCompetitionInfo.readEventMatchesJSON(context,true);
        }
        return sCompetitionInfo;
    }

    public String getEventCode(){
        return m_eventCode; 
    }

    public void setEventCode(String eventCode){
        m_eventCode = eventCode; 
        m_bEventDataLoaded = false;
        m_jsonData = null;
    }

    public boolean readEventMatchesJSON(Context context,boolean bSilent){
        String filename = DEVICE_DATA_PATH +"/"+ m_eventCode.trim().toLowerCase() + "matches.json";
        Log.d(TAG, "Looking for matches JSON file: "+filename);
        File file = new File(filename);

        // Check if file exists before trying to read it.
        if(file.exists()) {
            Log.d(TAG, "Reading in matches JSON file");
            BufferedReader reader = null;
            try {
                // Open and read the file into a StringBuilder.
                InputStream in = context.openFileInput(file.getName().trim());
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonString = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }

                // Parse the JSON using JSONTokener.
                m_jsonData = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
                Log.d(TAG, "setting m_bEventDataLoaded = true");
                m_bEventDataLoaded = true;

                // Show success Toast msg 
                String msg = "Successfully read event "+m_eventCode +" matches.json file ";
                Log.d(TAG, msg);
                Toast toastS = Toast.makeText(context, msg, Toast.LENGTH_LONG);
                toastS.setGravity(Gravity.CENTER,0,0);
                toastS.show();

            } catch (FileNotFoundException err) {
                if(!bSilent) {
                    // Show error Toast msg 
                    String errMsg = "ERROR reading event matches file: \n" + err;
                    Log.e(TAG, errMsg);
                    Toast toastM = Toast.makeText(context, errMsg, Toast.LENGTH_LONG);
                    View view2 = toastM.getView();
                    view2.setBackgroundColor(Color.RED);
                    toastM.setGravity(Gravity.CENTER,0,0);
                    toastM.show();
                }
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return m_bEventDataLoaded;
    }

   public boolean isEventDataLoaded() {
        Log.d(TAG, "isEventDataLoaded() returning "+ m_bEventDataLoaded);
        return m_bEventDataLoaded;
    }

    public String[] getEventMatches(String ec) throws JSONException, IOException {
        if(m_jsonData != null) {
            String[] matches = new String[m_jsonData.length()+1];
            matches[0] = "No match selected";
            if(ec.equals(m_eventCode)){
                for(int i= 1; i < m_jsonData.length(); i++){
                    matches[i] = ((JSONObject)m_jsonData.get(i)).getString("comp_level") + ((JSONObject)m_jsonData.get(i)).getString("match_number");
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
        if(m_jsonData != null) {
            JSONObject tempB = new JSONObject();
            JSONObject tempR = new JSONObject();
            JSONArray redTeams = new JSONArray();
            JSONArray blueTeams = new JSONArray();
            for(int i= 0; i < m_jsonData.length(); i++){
                if((((JSONObject)m_jsonData.get(i)).getString("comp_level") + ((JSONObject)m_jsonData.get(i)).getString("match_number")).equals(matchnum.trim().toLowerCase())){
                     bMatchNumFound = true;
                     JSONObject alliances = (JSONObject)m_jsonData.get(i);
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
