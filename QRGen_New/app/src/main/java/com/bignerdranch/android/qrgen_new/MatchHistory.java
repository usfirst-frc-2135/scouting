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

    private ArrayList<MatchData> mTotalMatchHistory;
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
            mTotalMatchHistory = mSerializer.loadMatchData();
        }
        catch(Exception e){
            mTotalMatchHistory = new ArrayList<MatchData>();
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
        return mTotalMatchHistory;
    }

    public MatchData getMatch(String x){
        for(MatchData y:mTotalMatchHistory){
            if(y.getMatchID().equals(x)){
                return y;
            }
        }
        Log.d(TAG, "no such match");
        return null;

    }

    public void deleteMatch(MatchData m){
        mTotalMatchHistory.remove(m);
        mAppContext.deleteFile(m.getMatchFileName());
    }

    public void addMatch(MatchData m){
        mTotalMatchHistory.add(m);
    }

    public boolean saveData(){
        try{
            mSerializer.saveData(mTotalMatchHistory);
            Log.d(TAG, "matches saved to file");
            return true;
        }
        catch(Exception e){
            Log.e(TAG, "Error saving data:", e);
            return false;
        }
    }

    public ArrayList sortByTimestamp(ArrayList<MatchData> l){
        ArrayList sorted = new ArrayList<MatchData>();
        boolean isAdded = false;
        if(l.size()>0){
            sorted.add(l.get(0));
            for(int i = 1; i < l.size(); i++){
                for(int j = 0; j <sorted.size(); j++){
                    Date d1 = l.get(i).getTimestamp();
                    Date d2 = ((MatchData)sorted.get(j)).getTimestamp();
                    if(d1.before(d2)){
                        sorted.add(j, l.get(i));
                        j= sorted.size();
                        isAdded = true;
                    }
                }
                if(!isAdded){
                    sorted.add(l.get(i));
                }
                isAdded = false;
            }
        }
        return sorted;
    }

    public ArrayList filterByTeam(ArrayList<MatchData> l, String t){
        ArrayList sorted = new ArrayList<MatchData>();
        for(MatchData m: l){
            if(m.getTeamNumber().equals(t)){
                sorted.add(m);
            }
        }
        return sorted;
    }

    public ArrayList filterByCompetition(ArrayList<MatchData> l , String c){
        ArrayList sorted = new ArrayList<MatchData>();
        for(MatchData m: l){
            if(m.getCompetition().equals(c)){
                sorted.add(m);
            }
        }
        return sorted;
    }

    public ArrayList filterByScout(ArrayList<MatchData> l, String n){
        ArrayList sorted = new ArrayList<MatchData>();
        for(MatchData m: l){
            if(m.getName().equals(n)){
                sorted.add(m);
            }
        }
        return sorted;
    }

    public ArrayList filterByMatchNumber(ArrayList<MatchData> l, String mN){
        ArrayList sorted = new ArrayList<MatchData>();
        for(MatchData m: l){
            if(m.getMatchNumber().equals(mN)){
                sorted.add(m);
            }
        }
        return sorted;
    }
    
    public String[] listTeams(){
        ArrayList teams = new ArrayList<String>();
        for(MatchData m: mTotalMatchHistory){
            String team = m.getTeamNumber();
            if(!teams.contains(team)){
                teams.add(team);
            }
        }
        String[] array = new String[teams.size()+1];
        array[0] = "Select team";
        for(int i = 1; i<array.length; i++){
            array[i] = teams.get(i-1).toString();
        }
        
        return array;
    }

    public String[] listCompetitions(){
        ArrayList competitions = new ArrayList<String>();
        for(MatchData m: mTotalMatchHistory){
            String competition = m.getCompetition();
            if(!competitions.contains(competition)){
                competitions.add(competition);
            }
        }
        String[] array = new String[competitions.size()+1];
        array[0] = "Select competition";
        for(int i = 1; i<array.length; i++){
            array[i] = competitions.get(i-1).toString();
        }

        return array;
    }

    public String[] listScouts(){
        ArrayList scouts = new ArrayList<String>();
        for(MatchData m: mTotalMatchHistory){
            String scout = m.getName();
            if(!scouts.contains(scout)){
                scouts.add(scout);
            }
        }
        String[] array = new String[scouts.size()+1];
        array[0] = "Select scout";
        for(int i = 1; i<array.length; i++){
            array[i] = scouts.get(i-1).toString();
        }

        return array;
    }




}
