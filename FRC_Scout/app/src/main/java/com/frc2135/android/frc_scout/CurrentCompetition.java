package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CurrentCompetition {

    private String m_eventCode;
    private String m_compName;
    private static CurrentCompetition sCurrentCompetition;

    public CurrentCompetition(Context mAppContext){
        CompetitionDataSerializer compDataSerializer = new CompetitionDataSerializer(mAppContext, "current_competition.json");

    }

    public CurrentCompetition(JSONObject json) throws JSONException{
        m_eventCode = json.getString("eventCode");
        m_compName = json.getString("compName");
    }

    public static CurrentCompetition get(Context c){
        if(sCurrentCompetition == null) {
            sCurrentCompetition = new CurrentCompetition(c.getApplicationContext());
        }
        return sCurrentCompetition;
    }

    public void setEventCode(String ec){
        m_eventCode = ec;
    }

    public String getEventCode(){
        return m_eventCode;
    }

    public void setCompName(String cn){
        m_compName = cn;
    }

    public String getCompName(){
        return m_compName;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("compName", m_compName);
        json.put("eventCode", m_eventCode);
        return json;
    }
}
