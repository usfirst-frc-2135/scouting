package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class MatchHistory
{
    private static final String TAG = "MatchHistory";
    private static final String FILENAME = "Scouter.json";

    private ArrayList<MatchData> mTotalMatchHistory;
    private final MatchDataSerializer mSerializer;

    private static MatchHistory sMatchHistory;
    private final Context mAppContext;
    private File externalFilesDir;

    private MatchHistory(Context appContext)
    {
        mAppContext = appContext;
        mSerializer = new MatchDataSerializer(mAppContext, FILENAME);
        try
        {
            Log.d(TAG, "mSerializer loading MatchHistory");
            mTotalMatchHistory = mSerializer.loadMatchData();
            Log.d(TAG, "Number of matches loaded from mSerializer: " + mTotalMatchHistory.size());
        }
        catch (Exception e)
        {
            mTotalMatchHistory = new ArrayList<MatchData>();
            Log.e(TAG, "Error loading matchHistory: ", e);
        }
    }

    public static MatchHistory get(Context c)
    {
        if (sMatchHistory == null)
        {
            Log.d(TAG, "Creating a new sMatchHistory");
            sMatchHistory = new MatchHistory(c.getApplicationContext());
        }
        return sMatchHistory;
    }

    public ArrayList getMatches()
    {
        return mTotalMatchHistory;
    }

    public MatchData getMatch(String matchStr)
    {
        for (MatchData mX : mTotalMatchHistory)
        {
            if (mX.getMatchID().equals(matchStr))
            {
                return mX;
            }
        }
        Log.d(TAG, "no match found for getMatch(): " + matchStr);
        return null;
    }

    public void deleteMatch(MatchData mY)
    {
        mTotalMatchHistory.remove(mY);
        mAppContext.deleteFile(mY.getMatchFileName());
    }

    public void addMatch(MatchData mData)
    {
        mTotalMatchHistory.add(mData);
    }

    public boolean saveScouterData()
    {
        try
        {
            Log.d(TAG, "Saving scouter data to JSON file");
            mSerializer.saveScouterData();
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "saveScouterData(): Error saving data:", e);
            return false;
        }
    }

    public boolean saveMatchData(MatchData matchData)
    {
        try
        {
            Log.d(TAG, "Saving saveMatchData data to JSON files");
            mSerializer.saveMatchData(matchData);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "saveMatchData(): Error saving data:", e);
            return false;
        }
    }

    public boolean saveAllData()
    {
        try
        {
            Log.d(TAG, "Saving all data to JSON files");
            mSerializer.saveAllData(mTotalMatchHistory);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "saveAllData(): Error saving data:", e);
            return false;
        }
    }

    public ArrayList sortByTimestamp1(ArrayList<MatchData> mdList)
    {
        ArrayList sorted = new ArrayList<MatchData>();
        boolean isAdded = false;
        if (mdList.size() > 0)
        {
            sorted.add(mdList.get(0));
            for (int ctr1 = 1; ctr1 < mdList.size(); ctr1++)
            {
                for (int ctr2 = 0; ctr2 < sorted.size(); ctr2++)
                {
                    Date d1 = mdList.get(ctr1).getTimestamp();
                    Date d2 = ((MatchData) sorted.get(ctr2)).getTimestamp();
                    if (d1.before(d2))
                    {
                        sorted.add(ctr2, mdList.get(ctr1));
                        ctr2 = sorted.size();
                        isAdded = true;
                    }
                }
                if (!isAdded)
                {
                    sorted.add(mdList.get(ctr1));
                }
                isAdded = false;
            }
        }
        return sorted;
    }

    public ArrayList sortByTimestamp2(ArrayList<MatchData> mdlist)
    {
        ArrayList sorted = new ArrayList<MatchData>();
        ArrayList temp = new ArrayList<MatchData>();
        temp = sortByTimestamp1(mdlist);
        for (Object mData : temp)
        {
            sorted.add(0, mData);
        }
        return sorted;
    }

    public ArrayList filterByTeam(ArrayList<MatchData> mdlist, String teamNumber)
    {
        ArrayList sorted = new ArrayList<MatchData>();
        for (MatchData mData : mdlist)
        {
            if (mData.getTeamNumber().equals(teamNumber))
            {
                sorted.add(mData);
            }
        }
        return sorted;
    }

    public ArrayList filterByCompetition(ArrayList<MatchData> mdlist, String comp)
    {
        ArrayList sorted = new ArrayList<MatchData>();
        for (MatchData mData : mdlist)
        {
            if (mData.getCompetition().equals(comp))
            {
                sorted.add(mData);
            }
        }
        return sorted;
    }

    public ArrayList filterByScout(ArrayList<MatchData> mdlist, String scoutName)
    {
        ArrayList sorted = new ArrayList<MatchData>();
        for (MatchData mData : mdlist)
        {
            if (mData.getName().equals(scoutName))
            {
                sorted.add(mData);
            }
        }
        return sorted;
    }

    public ArrayList filterByMatchNumber(ArrayList<MatchData> mdlist, String matchNum)
    {
        ArrayList sorted = new ArrayList<MatchData>();
        for (MatchData mData : mdlist)
        {
            if (mData.getMatchNumber().equals(matchNum))
            {
                sorted.add(mData);
            }
        }
        return sorted;
    }

    public String[] listTeams()
    {
        ArrayList teams = new ArrayList<String>();
        for (MatchData mData : mTotalMatchHistory)
        {
            String team = mData.getTeamNumber();
            if (!teams.contains(team))
            {
                teams.add(team);
            }
        }
        String[] array = new String[teams.size() + 1];
        array[0] = "Select team";
        for (int i = 1; i < array.length; i++)
        {
            array[i] = teams.get(i - 1).toString();
        }
        return array;
    }

    public String[] listCompetitions()
    {
        ArrayList competitions = new ArrayList<String>();
        for (MatchData mData : mTotalMatchHistory)
        {
            String competition = mData.getCompetition();
            if (!competitions.contains(competition))
            {
                competitions.add(competition);
            }
        }
        String[] array = new String[competitions.size() + 1];
        array[0] = "Select competition";
        for (int i = 1; i < array.length; i++)
        {
            array[i] = competitions.get(i - 1).toString();
        }
        return array;
    }

    public String[] listScouts()
    {
        ArrayList scouts = new ArrayList<String>();
        for (MatchData mData : mTotalMatchHistory)
        {
            String scout = mData.getName();
            if (!scouts.contains(scout))
            {
                scouts.add(scout);
            }
        }
        String[] array = new String[scouts.size() + 1];
        array[0] = "Select scout";
        for (int i = 1; i < array.length; i++)
        {
            array[i] = scouts.get(i - 1).toString();
        }
        return array;
    }
}
