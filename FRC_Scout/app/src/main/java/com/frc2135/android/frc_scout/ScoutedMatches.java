package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Singleton class for managing the collection of {@link MatchData}.
 * Handles loading, saving, sorting, and filtering of match records.
 * Handles its own persistence by extending {@link BaseJSONSerializer}.
 */
public class ScoutedMatches extends BaseJSONSerializer
{
    private static final String TAG = "ScoutedMatches";

    private final List<MatchData> m_scoutedMatches;
    private final Context m_appContext;

    private static volatile ScoutedMatches sScoutedMatches;

    private ScoutedMatches(Context appContext)
    {
        super(appContext);
        Log.d(TAG, "ScoutedMatches constructor");
        m_appContext = appContext.getApplicationContext();
        m_scoutedMatches = loadInitialData();
    }

    private List<MatchData> loadInitialData()
    {
        Log.d(TAG, "loadInitialData()");
        return loadScoutedMatchList();
    }

    /**
     * Returns the thread-safe singleton instance of ScoutedMatches.
     *
     * @param context the context used to initialize the instance
     * @return the singleton instance
     */
    public static ScoutedMatches getInstance(Context context)
    {
        Log.d(TAG, "getInstance()");
        if (sScoutedMatches == null)
        {
            synchronized (ScoutedMatches.class)
            {
                if (sScoutedMatches == null)
                {
                    sScoutedMatches = new ScoutedMatches(context);
                }
            }
        }
        return sScoutedMatches;
    }

    /**
     * @return the full list of scouted matches
     */
    public List<MatchData> getMatches()
    {
        return m_scoutedMatches;
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
        return m_scoutedMatches.stream()
                .filter(m -> Objects.equals(matchId, m.getMatchID()))
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
            m_scoutedMatches.remove(match);
            m_appContext.deleteFile(match.getMatchFileName());
            Log.d(TAG, "Deleted match file: " + match.getMatchFileName());
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
            m_scoutedMatches.add(matchData);
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
        if (matchData == null)
        {
            return false;
        }

        try
        {
            Log.d(TAG, "saveMatchData()");
            JSONArray array = new JSONArray();
            array.put(matchData.toJSON());

            String filename = matchData.getMatchFileName();
            File file = new File(m_dataDir, filename);
            saveJSONArray(file, array);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to save match data for ID: " + matchData.getMatchID(), e);
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
            Settings.getInstance(m_appContext).saveSettingsSilent();
            Log.d(TAG, "Saving all " + m_scoutedMatches.size() + " matches to disk");
            for (MatchData match : m_scoutedMatches)
            {
                saveMatchData(match);
            }
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to save all data", e);
            return false;
        }
    }

    /**
     * Scans the data directory and loads all individual match files.
     *
     * @return a list of loaded MatchData objects
     */
    private ArrayList<MatchData> loadScoutedMatchList()
    {
        ArrayList<MatchData> matchHistory = new ArrayList<>();
        Log.d(TAG, "Scanning for match data files");

        File[] files = m_dataDir.listFiles();
        if (files == null)
        {
            return matchHistory;
        }

        for (File file : files)
        {
            String filename = file.getName();
            // Match files are identified by their UUID-based filename length (usually 36 chars + .json)
            if (filename.length() > 30 && filename.endsWith(".json") && !filename.contains("matches") && !filename.contains("aliases") && !filename.contains("scoutNames"))
            {
                try
                {
                    matchHistory.add(loadSingleMatch(file));
                    Log.d(TAG, "Successfully loaded match file: " + filename);
                }
                catch (IOException | JSONException e)
                {
                    Log.e(TAG, "Error loading match file " + filename + ": " + e.getMessage());
                }
            }
        }
        return matchHistory;
    }

    private MatchData loadSingleMatch(File file)
            throws IOException, JSONException
    {
        JSONArray array = loadJSONArray(file);
        if (array == null || array.length() == 0)
        {
            throw new JSONException("Empty or invalid match data array in file: " + file.getName());
        }
        return new MatchData(array.getJSONObject(0));
    }

    /**
     * Sorts a list of matches by the specified criteria and order.
     *
     * @param list      the list to sort
     * @param criteria  "Date", "Team", or "Match"
     * @param ascending true for ascending, false for descending
     * @return a new sorted ArrayList
     */
    public ArrayList<MatchData> sortMatches(List<MatchData> list, String criteria, boolean ascending)
    {
        ArrayList<MatchData> sortedList = new ArrayList<>(list);
        Comparator<MatchData> finalComparator;

        switch (criteria)
        {
            case "Team" -> finalComparator = Comparator.comparing(m -> {
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
            case "Match" -> finalComparator = Comparator.comparing(m -> {
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
            default ->
                    finalComparator = Comparator.comparing(MatchData::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder()));
        }

        if (!ascending)
        {
            finalComparator = finalComparator.reversed();
        }

        sortedList.sort(finalComparator);
        return sortedList;
    }

    /**
     * Filters a list of matches by multiple criteria simultaneously.
     *
     * @param list     the list to filter
     * @param team     team number filter (exact match), or null
     * @param event    event code filter (exact match), or null
     * @param scout    scout name filter (exact match), or null
     * @param matchNum match number filter (exact match), or null
     * @return a new filtered list
     */
    public List<MatchData> filterMatches(List<MatchData> list, String event, String matchNum, String team, String scout)
    {
        if (list == null)
        {
            return new ArrayList<>();
        }

        return list.stream().filter(m -> {
            boolean matches = true;
            if (event != null && !event.isEmpty())
            {
                matches = Objects.equals(event, m.getEventCode());
            }
            if (matches && matchNum != null && !matchNum.isEmpty())
            {
                matches = Objects.equals(matchNum, m.getMatchNumber());
            }
            if (matches && team != null && !team.isEmpty())
            {
                matches = Objects.equals(team, m.getTeamNumber());
            }
            if (matches && scout != null && !scout.isEmpty())
            {
                matches = Objects.equals(scout, m.getScoutName());
            }
            return matches;
        }).collect(Collectors.toList());
    }

    /**
     * @return an array of unique team numbers present in the history, prefixed with "Select team"
     */
    public String[] listTeams()
    {
        List<String> result = m_scoutedMatches.stream()
                .map(MatchData::getTeamNumber)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        result.add(0, "Select team");
        return result.toArray(new String[0]);
    }

    /**
     * @return an array of unique event codes present in the history, prefixed with "Select event code"
     */
    public String[] listEventCodes()
    {
        List<String> result = m_scoutedMatches.stream()
                .map(MatchData::getEventCode)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        result.add(0, "Select event code");
        return result.toArray(new String[0]);
    }

    /**
     * @return an array of unique scout names present in the history, prefixed with "Select scout"
     */
    public String[] listScouts()
    {
        List<String> result = m_scoutedMatches.stream()
                .map(MatchData::getScoutName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        result.add(0, "Select scout");
        return result.toArray(new String[0]);
    }
}
