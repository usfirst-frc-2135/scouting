package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Scouter
{

    private final ArrayList<String> m_pastScouters;
    private static Scouter sScouter;
    private String m_teamIndexStr;
    private boolean m_scoringTableSide;
    private String m_mostRecentScoutName;
    private String m_mostRecentMatchNumber;
    private static final String FILENAME = "Scouter.json";
    private static final String TAG = "Scouter";

    private Scouter(Context mAppContext)
    {

        m_pastScouters = new ArrayList<>();
        m_mostRecentScoutName = "";
        m_mostRecentMatchNumber = "";
        m_teamIndexStr = "None";
        m_scoringTableSide = false;

        MatchDataSerializer serializer = new MatchDataSerializer(mAppContext, FILENAME);

        // Load the previously saved Scouter data from Scouter.json file.
        if (m_pastScouters.size() == 0)
        {
            try
            {
                Log.d(TAG, "Loading scouter");
                Scouter tmpScouter = serializer.loadScouterData();
                if (tmpScouter != null)
                {
                    Collections.addAll(m_pastScouters, tmpScouter.getPastScouts());
                    m_teamIndexStr = tmpScouter.getTeamIndexStr();
                    m_scoringTableSide = tmpScouter.getScoringTableSide();
                }
            } catch (Exception e)
            {
                Log.e(TAG, "Error loading scouter: ", e);
            }
        }
    }

    public Scouter(JSONObject json)
    {
        Log.d(TAG, "Scouter being created using json data");
        m_pastScouters = new ArrayList<>();
        try
        {
            String tag = "scouterName";
            int i = 0;
            while (json.has(tag + i))
            {
                m_pastScouters.add(json.getString(tag + i + ""));
                i++;
            }
            setTeamIndexStr(json.getString("teamIndex"));
            int scoringTableSideVal = json.getInt("scoringTableSide");
            if(scoringTableSideVal == 1)
               setScoringTableSide(true);
            else setScoringTableSide(false);
        } catch (Exception e)
        {
            Log.d(TAG, "Error loading Scouter JSON file");
            Log.e(TAG, e.toString());
        }
    }

    public static Scouter get(Context c)
    {
        if (sScouter == null)
        {
            sScouter = new Scouter(c);
        }
        return sScouter;
    }

    public void addPastScouter(String n)
    {
        for (String x : m_pastScouters)
        {
            if (n.trim().equalsIgnoreCase(x.trim()))
            {
                return;
            }
        }
        m_pastScouters.add(n.trim());
    }

    public String getMostRecentMatchNumber()
    {
        return m_mostRecentMatchNumber;
    }

    public void setMostRecentMatchNumber(String value)
    {
        m_mostRecentMatchNumber = value;
    }

    public String getNextExpectedMatchNumber()
    {
        String newMatchNumber = "";
        if (!m_mostRecentMatchNumber.equals(""))
        {
            StringBuilder prefix = new StringBuilder();
            StringBuilder numStr = new StringBuilder();
            for (int i = 0; i < m_mostRecentMatchNumber.length(); i++)
            {
                if (Character.isDigit(m_mostRecentMatchNumber.charAt(i)))
                    numStr.append(m_mostRecentMatchNumber.charAt(i));
                else
                    prefix.append(m_mostRecentMatchNumber.charAt(i));
            }
            newMatchNumber = prefix.toString();
            int newNum = Integer.parseInt(numStr.toString());
            newNum++;
            newMatchNumber += Integer.toString(newNum);
        }
        return newMatchNumber;
    }

    public String getTeamIndexStr()
    {
        return m_teamIndexStr;
    }

    //returns "red", "blue", or "" based on current team index
    public String getTeamIndexColor()
    {
        String currentTeamIndex = getTeamIndexStr();
        if (currentTeamIndex.equals("1") || currentTeamIndex.equals("2") || currentTeamIndex.equals("3")) {
            return "red";
        } else if (currentTeamIndex.equals("4") || currentTeamIndex.equals("5") || currentTeamIndex.equals("6")) {
            return "blue";
    }
        return "";
    }

    public void setTeamIndexStr(String indexStr)
    {
        m_teamIndexStr = indexStr;
    }

    // Returns true if given indexStr is a number: 1, 2, 3, 4, 5, or 6
    public boolean isValidTeamIndexNum(String indexStr)
    {
        return indexStr.equals("1") || indexStr.equals("2") || indexStr.equals("3") || indexStr.equals("4") || indexStr.equals("5") || indexStr.equals("6");
    }

    // Returns true if given indexStr is None, 1, 2, 3, 4, 5, or 6
    public boolean isValidTeamIndexStr(String indexStr)
    {
        return indexStr.equals("1") || indexStr.equals("2") || indexStr.equals("3") || indexStr.equals("4") || indexStr.equals("5") || indexStr.equals("6") || indexStr.equals("None");
    }

    public boolean getScoringTableSide()
    {
        return m_scoringTableSide;
    }

    public void setScoringTableSide(boolean val)
    {
        m_scoringTableSide = val;
    }

    public String getMostRecentScoutName()
    {
        return m_mostRecentScoutName;
    }

    public void setMostRecentScoutName(String name)
    {
        m_mostRecentScoutName = name;
    }

    public String[] getPastScouts()
    {
        String[] names = new String[m_pastScouters.size()];
        for (int i = 0; i < names.length; i++)
        {
            names[i] = m_pastScouters.get(i);
        }
        return names;
    }

    public void clear()
    {
        m_pastScouters.clear();
    }

    public JSONObject toJSON() throws JSONException
    {
        // Writes the Scouter data to Scouter.json file.
        JSONObject json = new JSONObject();

        StringBuilder logMsg = new StringBuilder();
        for (int i = 0; i < m_pastScouters.size(); i++)
        {
            json.put("scouterName" + i, m_pastScouters.get(i));
            logMsg.append("scouterName").append(i).append("=").append(m_pastScouters.get(i)).append("; ");
        }
        json.put("teamIndex", m_teamIndexStr);
        logMsg.append("teamIndex" + "=").append(m_teamIndexStr);

        if(m_scoringTableSide) {
            json.put("scoringTableSide", 1);
            logMsg.append("scoringTableSide=1");
        }
        else {
            json.put("scoringTableSide", 0);
            logMsg.append("scoringTableSide=0");
        }

        Log.d(TAG, "--->>> Writing to Scouter.json: " + logMsg);
        return json;
    }

}
