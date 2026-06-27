package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class MatchListData
{
    private static final String TAG = "MatchListData";
    private static final String FILENAME = "settings.json";

    private ArrayList<MatchData> m_totalMatchListData;
    private final MatchDataSerializer m_serializer;

    private static MatchListData sMatchListData;
    private final Context m_appContext;

    private MatchListData(Context appContext)
    {
        m_appContext = appContext;
        m_serializer = new MatchDataSerializer(m_appContext, FILENAME);
        try
        {
            Log.d(TAG, "m_Serializer loading MatchListData");
            m_totalMatchListData = m_serializer.loadMatchData();
            Log.d(TAG, "Number of matches loaded from m_Serializer: " + m_totalMatchListData.size());
        }
        catch (Exception e)
        {
            m_totalMatchListData = new ArrayList<>();
            Log.e(TAG, "Error loading matchHistory: ", e);
        }
    }

    public static MatchListData get(Context c)
    {
        if (sMatchListData == null)
        {
            Log.d(TAG, "Creating a new sMatchListData");
            sMatchListData = new MatchListData(c.getApplicationContext());
        }
        return sMatchListData;
    }

    public ArrayList<MatchData> getMatches()
    {
        return m_totalMatchListData;
    }

    public MatchData getMatch(String matchStr)
    {
        for (MatchData mX : m_totalMatchListData)
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
        m_totalMatchListData.remove(mY);
        m_appContext.deleteFile(mY.getMatchFileName());
    }

    public void addMatch(MatchData mData)
    {
        m_totalMatchListData.add(mData);
    }

    public boolean saveScoutNames()
    {
        try
        {
            Log.d(TAG, "Saving scout names to JSON file");
            m_serializer.saveScoutNames();
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "saveScoutNames(): Error saving scout names:", e);
            return false;
        }
    }

    public boolean saveMatchData(MatchData matchData)
    {
        try
        {
            Log.d(TAG, "Saving saveMatchData data to JSON files");
            m_serializer.saveMatchData(matchData);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "saveMatchData(): Error saving data:", e);
            return false;
        }
    }

    @SuppressWarnings("unused")
    public boolean saveAllData()
    {
        try
        {
            Log.d(TAG, "Saving all data to JSON files");
            m_serializer.saveAllData(m_totalMatchListData);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "saveAllData(): Error saving data:", e);
            return false;
        }
    }

    public ArrayList<MatchData> sortByTimestamp1(ArrayList<MatchData> mdList)
    {
        ArrayList<MatchData> sorted = new ArrayList<>();
        boolean isAdded = false;
        if (!mdList.isEmpty())
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
            if (mData.getEventCode().equals(comp))
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
        for (MatchData mData : m_totalMatchListData)
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
        for (MatchData mData : m_totalMatchListData)
        {
            String competition = mData.getEventCode();
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
        for (MatchData mData : m_totalMatchListData)
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
