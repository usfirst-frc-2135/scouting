package com.frc2135.android.frc_scout;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Scouter {

    private final ArrayList<String> pastScouters;
    private static Scouter sScouter;
    private MatchDataSerializer mSerializer;
    private Context mContext;
    private String mFileName;
    private static final String FILENAME = "Scouter.json";
    private static final String TAG = "Scouter";


    private Scouter(Context mAppContext){

        pastScouters = new ArrayList<String>();

        mSerializer = new MatchDataSerializer(mAppContext, FILENAME);

        //Rather than start with a new Scouter every time, the following code allows the program to call the method loadScouter() in order to add the previously saved match
       if(pastScouters.size()==0){
           try{
               Log.d(TAG, "Loading scouter");
               for(String x:mSerializer.loadScouterData().getPastScouts()){
                   pastScouters.add(x);
               }


           }
           catch(Exception e){
               Log.e(TAG, "Error loading scouter: ", e);
           }
       }

    }

    public Scouter(JSONObject json) throws JSONException{
        Log.d(TAG, "Scouter being created using json data");

        pastScouters = new ArrayList<String>();

        try{
            String tag = "scoutername";
            int i=0;
            while(json.has(tag+i)){
                pastScouters.add(json.getString(tag+i +""));
                i++;
            }


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
        for(String x: pastScouters){
            if(n.trim().equalsIgnoreCase(x.trim())){
                return;
            }
        }
        pastScouters.add(n.trim());
    }

    public String[] getPastScouts(){
        String[] names = new String[pastScouters.size()];
        for(int i = 0; i<names.length; i++){
            names[i]=pastScouters.get(i);
        }
        return names;
    }


    public void clear(){
        pastScouters.clear();
    }


    public JSONObject toJSON() throws JSONException {
        //This code uses the JSON class to convert the aspects of each match into data that can be to a file as JSON
        JSONObject json = new JSONObject();

        String logmessage1 = "";
        for(int i = 0; i < pastScouters.size(); i++){
            json.put("scoutername" + i, pastScouters.get(i));
            logmessage1 +="scoutername" + i + pastScouters.get(i);
        }

        Log.d(TAG, logmessage1);
        return json;
    }

    public boolean saveData(Context c){
        try{
            mSerializer.saveData(MatchHistory.get(c).getMatches());
            Log.d(TAG, "scouters/competitions saved to file");
            return true;
        }
        catch(Exception e){
            Log.e(TAG, "Error saving scouters/competitions:", e);
            return false;
        }
    }

}
