package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class MatchHistory
{
    private static final String TAG = "MatchHistory";
    private static final String FILENAME = "Scouter.json";

    private ArrayList<MatchData> m_TotalMatchHistory;
    private final MatchDataSerializer m_Serializer;

    private static MatchHistory sMatchHistory;
    private final Context m_AppContext;

    private MatchHistory(Context appContext)
    {
        m_AppContext = appContext;
        m_Serializer = new MatchDataSerializer(m_AppContext, FILENAME);
        try
        {
            Log.d(TAG, "m_Serializer loading MatchHistory");
            m_TotalMatchHistory = m_Serializer.loadMatchData();
            Log.d(TAG, "Number of matches loaded from m_Serializer: " + m_TotalMatchHistory.size());
        }
        catch (Exception e)
        {
            m_TotalMatchHistory = new ArrayList<>();
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

    public ArrayList<MatchData> getMatches()
    {
        return m_TotalMatchHistory;
    }

    public MatchData getMatch(String matchStr)
    {
        for (MatchData mX : m_TotalMatchHistory)
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
        m_TotalMatchHistory.remove(mY);
        m_AppContext.deleteFile(mY.getMatchFileName());
    }

    public void addMatch(MatchData mData)
    {
        m_TotalMatchHistory.add(mData);
    }

    public boolean saveScouterData()
    {
        try
        {
            Log.d(TAG, "Saving scouter data to JSON file");
            m_Serializer.saveScouterData();
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
            m_Serializer.saveMatchData(matchData);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "saveMatchData(): Error saving data:", e);
            return false;
        }
    }

/*-->REMOVE - this is never used
    public boolean saveAllData()
    {
        try
        {
            Log.d(TAG, "Saving all data to JSON files");
            m_Serializer.saveAllData(m_TotalMatchHistory);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "saveAllData(): Error saving data:", e);
            return false;
        }
    }
<---REMOVE*/

    public ArrayList<MatchData> sortByTimestamp1(ArrayList<MatchData> mdList)
    {
        ArrayList<MatchData> sorted = new ArrayList<>();
        boolean isAdded = false;
        if (mdList.size() > 0)
        {
            sorted.add(mdList.get(0));
            for (int ctr1 = 1; ctr1 < mdList.size(); ctr1++)
            {
                for (int ctr2 = 0; ctr2 < sorted.size(); ctr2++)
                {
                    Date d1 = mdList.get(ctr1).getTimestamp();
                    Date d2 = (sorted.get(ctr2)).getTimestamp();
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

    public ArrayList<MatchData> sortByTimestamp2(ArrayList<MatchData> mdList)
    {
        ArrayList<MatchData> sorted = new ArrayList<>();
        ArrayList<MatchData> temp;
        temp = sortByTimestamp1(mdList);
        for (MatchData mData : temp)
        {
            sorted.add(0, mData);
        }
        return sorted;
    }

    public ArrayList<MatchData> filterByTeam(ArrayList<MatchData> mdList, String teamNumber)
    {
        ArrayList<MatchData> sorted = new ArrayList<>();
        for (MatchData mData : mdList)
        {
            if (mData.getTeamNumber().equals(teamNumber))
            {
                sorted.add(mData);
            }
        }
        return sorted;
    }

    public ArrayList<MatchData> filterByCompetition(ArrayList<MatchData> mdList, String comp)
    {
        ArrayList<MatchData> sorted = new ArrayList<>();
        for (MatchData mData : mdList)
        {
            if (mData.getCompetition().equals(comp))
            {
                sorted.add(mData);
            }
        }
        return sorted;
    }

    public ArrayList<MatchData> filterByScout(ArrayList<MatchData> mdList, String scoutName)
    {
        ArrayList<MatchData> sorted = new ArrayList<>();
        for (MatchData mData : mdList)
        {
            if (mData.getName().equals(scoutName))
            {
                sorted.add(mData);
            }
        }
        return sorted;
    }

    public ArrayList<MatchData> filterByMatchNumber(ArrayList<MatchData> mdList, String matchNum)
    {
        ArrayList<MatchData> sorted = new ArrayList<>();
        for (MatchData mData : mdList)
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
        ArrayList<String> teams = new ArrayList<>();
        for (MatchData mData : m_TotalMatchHistory)
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
            array[i] = teams.get(i - 1);
        }
        return array;
    }

    public String[] listCompetitions()
    {
        ArrayList<String> competitions = new ArrayList<>();
        for (MatchData mData : m_TotalMatchHistory)
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
            array[i] = competitions.get(i - 1);
        }
        return array;
    }

    public String[] listScouts()
    {
        ArrayList<String> scouts = new ArrayList<>();
        for (MatchData mData : m_TotalMatchHistory)
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
            array[i] = scouts.get(i - 1);
        }
        return array;
    }
}
