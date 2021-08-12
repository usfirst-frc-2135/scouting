package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CurrentCompetition {

    private String eventCode;
    private String compName;
    private JSONArray data;
    private static CurrentCompetition sCurrentCompetition;

    public CurrentCompetition(Context mAppContext){
        CompetitionDataSerializer compDataSerializer = new CompetitionDataSerializer(mAppContext, "current_competition.json");

    }

    public CurrentCompetition(JSONObject json) throws JSONException{
        eventCode = json.getString("eventCode");
        compName = json.getString("compName");
        //data = json.getJSONArray("data");

    }

    public static CurrentCompetition get(Context c){
        if(sCurrentCompetition == null) {
            sCurrentCompetition = new CurrentCompetition(c.getApplicationContext());
        }

        return sCurrentCompetition;
    }

    public void setEventCode(String ec){
        eventCode = ec;
    }

    public String getEventCode(){
        return eventCode;
    }

    public void setCompName(String cn){
        compName = cn;
    }

    public String getCompName(){
        return compName;
    }

    public JSONArray getData(){
        return data;
    }

    public void setData(JSONArray j){
        data = j;
    }


    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("compName", compName);
        Log.d("CurrentCompetition", compName);
        json.put("eventCode", eventCode);
        //json.put("data", data);
        //Log.d("CurrentCompetition", data.toString().substring(0, 50));

        return json;
    }
}
