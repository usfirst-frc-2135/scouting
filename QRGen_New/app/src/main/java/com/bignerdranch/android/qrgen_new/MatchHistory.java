package com.bignerdranch.android.qrgen_new;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MatchHistory {
    private static final String TAG = "MatchHistory";
    private static final String FILENAME = "Scouter.json";

    private ArrayList<MatchData> mMatchHistory;
    private MatchDataSerializer mSerializer;

    private static MatchHistory sMatchHistory;
    private Context mAppContext;
    private File externalFilesDir;


    private MatchHistory(Context appContext){
        mAppContext = appContext;
        mSerializer = new MatchDataSerializer(mAppContext, FILENAME);

        //Rather than start with a new matchHistory every time, the following code allows the program to call the method loadCrimes() in order to add the previously saved crimes
        try{
            Log.d(TAG, "Matches being loaded into MatchHistory");
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

    public MatchData getMatch(String x){
        for(MatchData y:mMatchHistory){
            if(y.getMatchID().equals(x)){
                return y;
            }
        }
        Log.d(TAG, "no such match");
        return null;

    }

    public void deleteMatch(MatchData m){
        mMatchHistory.remove(m);
        mAppContext.deleteFile(m.getMatchFileName());
    }

    public void addMatch(MatchData m){
        mMatchHistory.add(m);
    }

    public boolean saveData(){
        try{
            mSerializer.saveData(mMatchHistory);
            Log.d(TAG, "matches saved to file");
            return true;
        }
        catch(Exception e){
            Log.e(TAG, "Error saving data:", e);
            return false;
        }
    }

    public ArrayList sortByTimestamp(){
        ArrayList sorted = new ArrayList<MatchData>();
        boolean isAdded = false;
        if(mMatchHistory.size()>0){
            sorted.add(mMatchHistory.get(0));
            for(int i = 1; i < mMatchHistory.size(); i++){
                for(int j = 0; j <sorted.size(); j++){
                    Date d1 = mMatchHistory.get(i).getTimestamp();
                    Date d2 = ((MatchData)sorted.get(j)).getTimestamp();
                    if(d1.before(d2)){
                        sorted.add(j, mMatchHistory.get(i));
                        j= sorted.size();
                        isAdded = true;
                    }
                }
                if(!isAdded){
                    sorted.add(mMatchHistory.get(i));
                }
                isAdded = false;
            }
        }
        return sorted;
    }

    public ArrayList filterByTeam(String t){
        ArrayList sorted = new ArrayList<MatchData>();
        for(MatchData m: mMatchHistory){
            if(m.getTeamNumber().equals(t)){
                sorted.add(m);
            }
        }
        return sorted;
    }

    public ArrayList filterByCompetition(String c){
        ArrayList sorted = new ArrayList<MatchData>();
        for(MatchData m: mMatchHistory){
            if(m.getCompetition().equals(c)){
                sorted.add(m);
            }
        }
        return sorted;
    }

    public ArrayList filterByScouter(String n){
        ArrayList sorted = new ArrayList<MatchData>();
        for(MatchData m: mMatchHistory){
            if(m.getName().equals(n)){
                sorted.add(m);
            }
        }
        return sorted;
    }

    public ArrayList filterByMatchNumber(String mN){
        ArrayList sorted = new ArrayList<MatchData>();
        for(MatchData m: mMatchHistory){
            if(m.getMatchNumber().equals(mN)){
                sorted.add(m);
            }
        }
        return sorted;
    }




}
