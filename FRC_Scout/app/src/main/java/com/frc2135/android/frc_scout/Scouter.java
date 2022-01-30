package com.frc2135.android.frc_scout;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Scouter {

    private final ArrayList<String> m_pastScouters;
    private static Scouter sScouter;
    private MatchDataSerializer m_Serializer;
    private String m_teamIndexStr;
    private String m_mostRecentScoutName;
    private String m_mostRecentMatchNumber;
    private static final String FILENAME = "Scouter.json";
    private static final String TAG = "Scouter";


    private Scouter(Context mAppContext){

        m_pastScouters = new ArrayList<String>();
        m_mostRecentScoutName = "";
        m_mostRecentMatchNumber = "";
        m_teamIndexStr = "None";

        m_Serializer = new MatchDataSerializer(mAppContext, FILENAME);

        // Load the previously saved Scouter data from Scouter.json file.
       if(m_pastScouters.size()==0){
           try{
               Log.d(TAG, "Loading scouter");
               Scouter tmpScouter = m_Serializer.loadScouterData();
               if(tmpScouter != null) {
                   for (String pastScout : tmpScouter.getPastScouts()) {
                       m_pastScouters.add(pastScout);
                   }
                   m_teamIndexStr = tmpScouter.getTeamIndexStr();
               }
           }
           catch(Exception e){
               Log.e(TAG, "Error loading scouter: ", e);
           }
       }

    }

    public Scouter(JSONObject json) throws JSONException{
        Log.d(TAG, "Scouter being created using json data");
        m_pastScouters = new ArrayList<String>();
        try{
            String tag = "scoutername";
            int i=0;
            while(json.has(tag+i)){
                m_pastScouters.add(json.getString(tag+i +""));
                i++;
            }
            setTeamIndexStr(json.getString("teamindex"));
        }catch(Exception e){
            Log.d(TAG, "Error loading past scout data");
            Log.e(TAG, e.toString());
        }
    }

    public static Scouter get(Context c){
        if(sScouter == null) {
            sScouter = new Scouter(c);
        }
        return sScouter;
    }

    public void addPastScouter(String n){
        for(String x: m_pastScouters){
            if(n.trim().equalsIgnoreCase(x.trim())){
                return;
            }
        }
        m_pastScouters.add(n.trim());
    }

    public String getMostRecentMatchNumber(){
        return m_mostRecentMatchNumber;
    }
    public void setMostRecentMatchNumber(String value){
        m_mostRecentMatchNumber = value;
    }
    public String getNextExpectedMatchNumber(){
        String newMatchNumber = "";
        if(m_mostRecentMatchNumber != "") {
            String prefix = "";
            String numStr = "";
            for(int i = 0; i < m_mostRecentMatchNumber.length(); i++){
                if(Character.isDigit(m_mostRecentMatchNumber.charAt(i)))
                    numStr += m_mostRecentMatchNumber.charAt(i);
                else prefix += m_mostRecentMatchNumber.charAt(i);
            }
            newMatchNumber = prefix;
            int newNum = Integer.parseInt(numStr);
            newNum++;
            newMatchNumber += Integer.toString(newNum);
        }
        return newMatchNumber;
    }

    public String getTeamIndexStr(){
        return m_teamIndexStr;
    }
    public void setTeamIndexStr(String indexStr){
        m_teamIndexStr = indexStr;
    }

    // Returns true if given indexStr is a number: 1, 2, 3, 4, 5, or 6
    public boolean isValidTeamIndexNum(String indexStr){
        if(indexStr.equals("1") || indexStr.equals("2") || indexStr.equals("3") || indexStr.equals("4") || indexStr.equals("5") || indexStr.equals("6")) {
            return true;
        }
        return false;
    }

    // Returns true if given indexStr is None, 1, 2, 3, 4, 5, or 6
    public boolean isValidTeamIndexStr(String indexStr){
        if(indexStr.equals("1") || indexStr.equals("2") || indexStr.equals("3") || indexStr.equals("4") || indexStr.equals("5") || indexStr.equals("6") || indexStr.equals("None"))  {
            return true;
        }
        return false;
    }


    public String getMostRecentScoutName(){
        return m_mostRecentScoutName;
    }
    public void setMostRecentScoutName(String name){
        m_mostRecentScoutName = name;
    }

    public String[] getPastScouts(){
        String[] names = new String[m_pastScouters.size()];
        for(int i = 0; i<names.length; i++){
            names[i]=m_pastScouters.get(i);
        }
        return names;
    }


    public void clear(){
        m_pastScouters.clear();
    }


    public JSONObject toJSON() throws JSONException {
        // Writes the Scouter data to Scouter.json file.
        JSONObject json = new JSONObject();

        String logmsg = "";
        for(int i = 0; i < m_pastScouters.size(); i++){
            json.put("scoutername" + i, m_pastScouters.get(i));
            logmsg +="scoutername" + i + "="+m_pastScouters.get(i)+"; ";
        }
        json.put("teamindex", m_teamIndexStr);
        logmsg +="teamindex" +"="+ m_teamIndexStr;

        Log.d(TAG,"Writing to Scouter.json: "+logmsg);
        return json;
    }

    public boolean saveData(Context c){
//???? should this just save Scouter.json????? and not all the matchHistory????
        try{
           Log.d(TAG,"Scouter saveData() calling MatchDataSerializer->saveData(MatchHistory->getMatches())");
            m_Serializer.saveData(MatchHistory.get(c).getMatches());
            Log.d(TAG, "scouters/competitions saved to file");
            return true;
        }
        catch(Exception e){
            Log.e(TAG, "Error saving scouters/competitions:", e);
            return false;
        }
    }

}
