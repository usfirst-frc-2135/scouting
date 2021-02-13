package com.bignerdranch.android.qrgen_new;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Scouter {

    private ArrayList<String> pastScouters;
    private ArrayList<String> pastComps;

    private static Scouter sScouter;
    //private ScouterDataSerializer mSerializer;
    private MatchDataSerializer mSerializer;
    private Context mContext;
    private String mFileName;
    private static final String FILENAME = "Scouter.json";
    private static final String TAG = "Scouter";


    private Scouter(Context mAppContext){

        pastScouters = new ArrayList<String>();
        pastComps = new ArrayList<String>();

        mSerializer = new MatchDataSerializer(mAppContext, FILENAME);

        //Rather than start with a new Scouter every time, the following code allows the program to call the method loadScouter() in order to add the previously saved match
       if(pastScouters.size()==0){
           try{
               Log.d(TAG, "Loading scouter");
               for(String x:mSerializer.loadScouterData().getPastScouts()){
                   pastScouters.add(x);
               }
               for(String x:mSerializer.loadScouterData().getPastComps()){
                   pastComps.add(x);
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
        pastComps = new ArrayList<String>();

        try{
            String tag = "scoutername";
            int i=0;
            while(json.has(tag+i)){
                pastScouters.add(json.getString(tag+i +""));
                i++;
            }

            String tag2 = "competition";
            int j=0;
            while(json.has(tag2+j +"")){
                pastComps.add(json.getString(tag+j +""));
                j++;
            }
        }catch(Exception e){
            Log.d(TAG, "Error loading past scouter/comp data");
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
        pastScouters.add(n);
    }

    public String[] getPastScouts(){
        String[] names = new String[pastScouters.size()];
        for(int i = 0; i<names.length; i++){
            names[i]=pastScouters.get(i);
        }
        return names;
    }

    public String[] getPastComps(){
        String[] comps = new String[pastComps.size()];
        for(int i = 0; i<comps.length; i++){
            comps[i]=pastComps.get(i);
        }
        return comps;
    }

    public void addPastComp(String c){
        pastComps.add(c);
    }

    public JSONObject toJSON() throws JSONException {
        //This code uses the JSON class to convert the aspects of each match into data that can be to a file as JSON
        JSONObject json = new JSONObject();

        String logmessage1 = "";
        for(int i = 0; i < pastScouters.size(); i++){
            json.put("scoutername" + i, pastScouters.get(i));
            logmessage1 +="scoutername" + i + pastScouters.get(i);
        }
        for(int i = 0; i < pastComps.size(); i++){
            json.put("competition"+i, pastComps.get(i));
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
