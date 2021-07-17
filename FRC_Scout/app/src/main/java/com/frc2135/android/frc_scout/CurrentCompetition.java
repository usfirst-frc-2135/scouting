package com.frc2135.android.frc_scout;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class CurrentCompetition {

    private String eventCode;
    private String compName;
    private static CurrentCompetition sCurrentCompetition;

    public CurrentCompetition(Context mAppContext){
        CompetitionDataSerializer compDataSerializer = new CompetitionDataSerializer(mAppContext, "current_competition.json");

    }

    public CurrentCompetition(JSONObject json) throws JSONException{
        eventCode = json.getString("eventCode");
        compName = json.getString("compName");

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


    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("compName", compName);
        json.put("eventCode", eventCode);

        return json;
    }
}
