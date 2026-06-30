package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton class for managing the collection of {@link MatchData}.
 * Handles loading, saving, sorting, and filtering of match records.
 */
public class MatchListData
{
    private static final String TAG = "MatchListData";
    private static final String FILENAME = "settings.json";

    private final List<MatchData> m_totalMatchListData;
    private final MatchDataSerializer m_serializer;
    private final Context m_appContext;

    private static volatile MatchListData sMatchListData;

    private MatchListData(Context appContext)
    {
        m_appContext = appContext.getApplicationContext();
        m_serializer = new MatchDataSerializer(m_appContext, FILENAME);
        m_totalMatchListData = loadInitialData();
    }

    private List<MatchData> loadInitialData()
    {
        try
        {
            Log.d(TAG, "Loading match list data from serializer");
            List<MatchData> data = m_serializer.loadMatchData();
            if (data != null)
            {
                Log.d(TAG, "Successfully loaded " + data.size() + " matches");
                return data;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error loading match history", e);
        }
        return new ArrayList<>();
    }

    /**
     * Returns the thread-safe singleton instance of MatchListData.
     *
     * @param context the context used to initialize the instance
     * @return the singleton instance
     */
    public static MatchListData get(Context context)
    {
        if (sMatchListData == null)
        {
            synchronized (MatchListData.class)
            {
                if (sMatchListData == null)
                {
                    sMatchListData = new MatchListData(context);
                }
            }
        }
        return sMatchListData;
    }

    /**
     * @return the full list of scouted matches
     */
    public List<MatchData> getMatches()
    {
        return m_totalMatchListData;
    }

    /**
     * Finds a match by its unique ID.
     *
     * @param matchId the unique identifier for the match
     * @return the matching MatchData object, or null if not found
     */
    public MatchData getMatch(String matchId)
    {
        if (matchId == null)
        {
            return null;
        }
        return m_totalMatchListData.stream()
                .filter(m -> matchId.equals(m.getMatchID()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Deletes a match from the list and its corresponding file on disk.
     *
     * @param match the MatchData object to delete
     */
    public void deleteMatch(MatchData match)
    {
        if (match != null)
        {
            m_totalMatchListData.remove(match);
            m_appContext.deleteFile(match.getMatchFileName());
            Log.d(TAG, "Deleted match: " + match.getMatchID());
        }
    }

    /**
     * Adds a new match to the collection.
     *
     * @param matchData the match data to add
     */
    public void addMatch(MatchData matchData)
    {
        if (matchData != null)
        {
            m_totalMatchListData.add(matchData);
        }
    }

    /**
     * Saves the current scout names to the settings file.
     *
     * @return true if successful, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean saveScoutNames()
    {
        try
        {
            m_serializer.saveScoutNames();
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to save scout names", e);
            return false;
        }
    }

    /**
     * Saves a specific match's data to its JSON file.
     *
     * @param matchData the match to save
     * @return true if successful, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean saveMatchData(MatchData matchData)
    {
        try
        {
            m_serializer.saveMatchData(matchData);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to save match data for ID: " + (matchData != null ? matchData.getMatchID() : "null"), e);
            return false;
        }
    }

    /**
     * Saves all match data and settings to persistent storage.
     *
     * @return true if successful, false otherwise
     */
    @SuppressWarnings("unused")
    public boolean saveAllData()
    {
        try
        {
            m_serializer.saveAllData(new ArrayList<>(m_totalMatchListData));
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to save all data", e);
            return false;
        }
    }

    /**
     * Sorts a list of matches by the specified criteria and order.
     * @param list the list to sort
     * @param criteria "Date", "Team", or "Match"
     * @param ascending true for ascending, false for descending
     * @return a new sorted ArrayList
     */
    public ArrayList<MatchData> sortMatches(List<MatchData> list, String criteria, boolean ascending)
    {
        ArrayList<MatchData> sortedList = new ArrayList<>(list);
        Comparator<MatchData> comparator;

        switch (criteria)
        {
            case "Team":
                comparator = Comparator.comparing(m -> {
                    try
                    {
                        // Use regex to extract only the digits from the team number (e.g., "frc2135" -> 2135)
                        String digits = m.getTeamNumber().replaceAll("\\D+", "");
                        return digits.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(digits);
                    }
                    catch (NumberFormatException e)
                    {
                        return Integer.MAX_VALUE;
                    }
                });
                break;
            case "Match":
                comparator = Comparator.comparing(m -> {
                    try
                    {
                        // Use regex to extract only the digits from the match number (e.g., "qm12" -> 12)
                        String digits = m.getMatchNumber().replaceAll("\\D+", "");
                        return digits.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(digits);
                    }
                    catch (NumberFormatException e)
                    {
                        return Integer.MAX_VALUE;
                    }
                });
                break;
            case "Date":
            default:
                comparator = Comparator.comparing(MatchData::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
        }

        if (!ascending)
        {
            comparator = comparator.reversed();
        }

        sortedList.sort(comparator);
        return sortedList;
    }

    public List<MatchData> filterByTeam(List<MatchData> list, String teamNumber)
    {
        if (teamNumber == null)
        {
            return new ArrayList<>(list);
        }
        return list.stream()
                .filter(m -> teamNumber.equals(m.getTeamNumber()))
                .collect(Collectors.toList());
    }

    public List<MatchData> filterByCompetition(List<MatchData> list, String eventCode)
    {
        if (eventCode == null)
        {
            return new ArrayList<>(list);
        }
        return list.stream()
                .filter(m -> eventCode.equals(m.getEventCode()))
                .collect(Collectors.toList());
    }

    public List<MatchData> filterByScout(List<MatchData> list, String scoutName)
    {
        if (scoutName == null)
        {
            return new ArrayList<>(list);
        }
        return list.stream()
                .filter(m -> scoutName.equals(m.getName()))
                .collect(Collectors.toList());
    }

    public List<MatchData> filterByMatchNumber(List<MatchData> list, String matchNum)
    {
        if (matchNum == null)
        {
            return new ArrayList<>(list);
        }
        return list.stream()
                .filter(m -> matchNum.equals(m.getMatchNumber()))
                .collect(Collectors.toList());
    }

    /**
     * @return an array of unique team numbers present in the history, prefixed with "Select team"
     */
    public String[] listTeams()
    {
        List<String> result = m_totalMatchListData.stream()
                .map(MatchData::getTeamNumber)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        result.add(0, "Select team");
        return result.toArray(new String[0]);
    }

    /**
     * @return an array of unique competition codes present in the history, prefixed with "Select competition"
     */
    public String[] listCompetitions()
    {
        List<String> result = m_totalMatchListData.stream()
                .map(MatchData::getEventCode)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        result.add(0, "Select competition");
        return result.toArray(new String[0]);
    }

    /**
     * @return an array of unique scout names present in the history, prefixed with "Select scout"
     */
    public String[] listScouts()
    {
        List<String> result = m_totalMatchListData.stream()
                .map(MatchData::getName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        result.add(0, "Select scout");
        return result.toArray(new String[0]);
    }
}
