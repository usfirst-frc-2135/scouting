package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CurrentCompetition
{

    private static final String TAG = "CurrentCompetition";

    // Data members
    private String m_eventCode;
    private String m_compName;

    private static CurrentCompetition sCurrentCompetition;

    public CurrentCompetition(Context mAppContext)
    {
        m_eventCode = "COMPX";
        m_compName = "COMPX";
        Log.d(TAG, "constructor: m_eventCode = " + m_eventCode + "; m_compName = " + m_compName);
    }

    public CurrentCompetition(JSONObject json) throws JSONException
    {
        m_eventCode = json.getString("eventCode");
        m_compName = json.getString("compName");
        Log.d(TAG, "constructor from JSON file: m_eventCode = " + m_eventCode + "; m_compName = " + m_compName);
    }

    public static CurrentCompetition get(Context c) throws IOException, JSONException
    {
        if (sCurrentCompetition == null)
        {
            // Read in current_competition.json file, if there is one on the device.
            CompetitionDataSerializer compSerializer = new CompetitionDataSerializer(c);
            sCurrentCompetition = compSerializer.loadCurrentComp();
            if (sCurrentCompetition == null)
                sCurrentCompetition = new CurrentCompetition(c.getApplicationContext());
        }
        return sCurrentCompetition;
    }

    public void setEventCode(String ec)
    {
        m_eventCode = ec;
    }

    public String getEventCode()
    {
        return m_eventCode;
    }

    public void setCompName(String cn)
    {
        m_compName = cn;
    }

    public String getCompName()
    {
        return m_compName;
    }

    public JSONObject toJSON() throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put("compName", m_compName);
        json.put("eventCode", m_eventCode);
        return json;
    }
}
