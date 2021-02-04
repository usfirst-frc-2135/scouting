package com.bignerdranch.android.qrgen_new;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Scouter {

    private String mScouterName;
    private String mScoutingDateString;
    private String competition;
    private static Scouter sScouter;
    //private ScouterDataSerializer mSerializer;
    private MatchDataSerializer mSerializer;
    private Context mContext;
    private String mFileName;
    private static final String FILENAME = "Scouter.json";


    private Scouter(Context mAppContext, String n, String d){
        mScouterName = n;
        mScoutingDateString= d;

        mSerializer = new MatchDataSerializer(mAppContext, FILENAME);

        //Rather than start with a new Scouter every time, the following code allows the program to call the method loadScouter() in order to add the previously saved crimes
       if(mScouterName.equals("")){
           try{
               Log.d(TAG, "Loading scouter");
               mScouterName = mSerializer.loadScouterData().getName();
               mScoutingDateString = mSerializer.loadScouterData().getDate();


           }
           catch(Exception e){
               Log.e(TAG, "Error loading scouter: ", e);
           }
       }

    }

    public Scouter(JSONObject json) throws JSONException{
        Log.d(TAG, "Scouter being created using json data");
        mScouterName = json.getString("scouter name");
        mScoutingDateString = json.getString("scouting date");
        competition = json.getString("competition1");


    }


    public static Scouter get(Context c){
        if(sScouter == null) {
            sScouter = new Scouter(c, "", "");
        }
        return sScouter;
    }

    public String getName(){
        return mScouterName;
    }

    public String getDate(){
        return mScoutingDateString;
    }

    public void setName(String n){
        mScouterName = n;
    }

    public void setDate(String d){
        mScoutingDateString = d;
    }

    public void setCompetition(String x){
        competition = x;
    }

    public String getCompetition(){
        return competition;
    }

    public JSONObject toJSON() throws JSONException {
        //This code uses the JSON class to convert the aspects of each crime into data that can be to a file as JSON
        JSONObject json = new JSONObject();

        json.put("scouter name", mScouterName);
        json.put("scouting date", mScoutingDateString);
        json.put("competition1", competition);

        return json;
    }

    public boolean saveData(Context c){
        try{
            mSerializer.saveData(MatchHistory.get(c).getMatches());
            Log.d(TAG, "scouter saved to file");
            return true;
        }
        catch(Exception e){
            Log.e(TAG, "Error saving scouter:", e);
            return false;
        }
    }
}
