package com.bignerdranch.android.qrgen_new;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class MatchHistory {
    private static final String TAG = "MatchHistory";
    private static final String FILENAME = "matches.csv";

    private ArrayList<MatchData> mMatchHistory;
    private MatchDataSerializer mSerializer;

    private static MatchHistory sMatchHistory;
    private Context mAppContext;


    private MatchHistory(Context appContext){
        mAppContext = appContext;
        mSerializer = new MatchDataSerializer(mAppContext, FILENAME);

        //Rather than start with a new CrimeLab every time, the following code allows the program to call the method loadCrimes() in order to add the previously saved crimes
        try{
            mMatchHistory = mSerializer.loadMatchData();
        }
        catch(Exception e){
            mMatchHistory = new ArrayList<MatchData>();
            Log.e(TAG, "Error loading crimes: ", e);
        }
    }

    public static MatchHistory get(Context c){
        if(sMatchHistory == null) {
            sMatchHistory = new MatchHistory(c.getApplicationContext());
        }
        return sMatchHistory;
    }

    public ArrayList getMatches(){
        return mMatchHistory;
    }

    public void deleteMatch(MatchData m){
        mMatchHistory.remove(m);
    }

    public void addMatch(MatchData m){
        mMatchHistory.add(m);
    }

    public boolean saveMatches(){
        try{
            mSerializer.saveMatches(mMatchHistory);
            Log.d(TAG, "matches saved to file");
            return true;
        }
        catch(Exception e){
            Log.e(TAG, "Error saving crimes:", e);
            return false;
        }
    }



}
