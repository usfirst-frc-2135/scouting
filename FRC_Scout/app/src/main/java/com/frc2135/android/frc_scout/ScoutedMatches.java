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
        Log.v(TAG, "ScoutedMatches constructor");
        m_appContext = appContext.getApplicationContext();
        m_scoutedMatches = loadInitialData();
    }

    private List<MatchData> loadInitialData()
    {
        Log.v(TAG, "loadInitialData");
        return loadScoutedMatchesList();
    }

    /**
     * Returns the thread-safe singleton instance of ScoutedMatches.
     *
     * @param context the context used to initialize the instance
     * @return the singleton instance
     */
    public static ScoutedMatches getInstance(Context context)
    {
        Log.v(TAG, "getInstance");
        if (sScoutedMatches == null)
        {
            synchronized (ScoutedMatches.class)
            {
                if (sScoutedMatches == null)
                {
                    Log.i(TAG, "Creating new sScoutedMatches");
                    sScoutedMatches = new ScoutedMatches(context);
                }
            }
        }
        return sScoutedMatches;
    }

    /**
     * @return the full list of scouted matches
     */
    public List<MatchData> getMatchList()
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
            String filename = getMatchFileName(match);
            m_appContext.deleteFile(filename);
            Log.i(TAG, "Deleted match file: " + filename);
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
     * Gets the filename for a given match's data.
     *
     * @param match the match data
     * @return the filename string
     */
    public static String getMatchFileName(MatchData match)
    {
        if (match == null)
        {
            return "";
        }
        return Constants.MATCH_DATA_FILE_PREFIX + match.getMatchID() + Constants.MATCH_DATA_FILE_SUFFIX;
    }

    /**
     * Saves a specific match's data to its JSON file.
     *
     * @param matchData the match to save
     * @return true if successful, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean saveMatchDataFile(MatchData matchData)
    {
        if (matchData == null)
        {
            return false;
        }

        try
        {
            Log.d(TAG, "saveMatchDataFile");
            JSONArray newMatch = new JSONArray();
            newMatch.put(matchData.toJSON());

            String filename = getMatchFileName(matchData);
            File file = new File(m_dataDir, filename);
            saveJSONArray(file, newMatch);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to save match data for ID: " + matchData.getMatchID(), e);
            return false;
        }
    }

    /**
     * Scans the data directory and loads all individual match files.
     *
     * @return a list of loaded MatchData objects
     */
    private ArrayList<MatchData> loadScoutedMatchesList()
    {
        ArrayList<MatchData> matchHistory = new ArrayList<>();
        Log.d(TAG, "Scanning for scouted match data files");

        File[] files = m_dataDir.listFiles();
        if (files == null)
        {
            return matchHistory;
        }

        for (File file : files)
        {
            String filename = file.getName();
            // Match files are identified by their UUID-based filename length (36 chars + extension)
            if (filename.length() > 30 &&
                    filename.startsWith(Constants.MATCH_DATA_FILE_PREFIX) &&
                    filename.endsWith(Constants.MATCH_DATA_FILE_SUFFIX)
            )
            {
                try
                {
                    matchHistory.add(loadSingleMatchFile(file));
                    Log.d(TAG, "Successfully loaded match data file: " + filename);
                }
                catch (IOException | JSONException e)
                {
                    Log.e(TAG, "Error loading match data file " + filename + ": " + e.getMessage());
                }
            }
        }
        return matchHistory;
    }

    private MatchData loadSingleMatchFile(File file)
            throws IOException, JSONException
    {
        JSONArray matchData = loadJSONArray(file);
        if (matchData == null || matchData.length() == 0)
        {
            throw new JSONException("Empty or invalid match data array in file: " + file.getName());
        }
        return new MatchData(matchData.getJSONObject(0));
    }

    /**
     * Sorts a list of matches by the specified criteria and order.
     *
     * @param list      the list to sort
     * @param criteria  "Date", "Team", or "Match"
     * @param ascending true for ascending, false for descending
     * @return a new sorted ArrayList
     */
    public ArrayList<MatchData> sortMatchList(List<MatchData> list, String criteria, boolean ascending)
    {
        ArrayList<MatchData> sortedList = new ArrayList<>(list);
        Comparator<MatchData> baseComparator = switch (criteria)
        {
            case "Team" -> Comparator.comparing(m -> {
                String digits = MatchData.extractTeamNumber(m.getTeamNumber());
                return digits.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(digits);
            });
            case "Match" -> Comparator.comparing(m -> {
                String digits = MatchData.extractMatchNumber(m.getMatchNumber());
                return digits.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(digits);
            });
            default -> Comparator.comparing(MatchData::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder()));
        };

        Comparator<MatchData> finalComparator = ascending ? baseComparator : baseComparator.reversed();

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
    public List<MatchData> filterMatchList(List<MatchData> list, String event, String matchNum, String team, String scout)
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
     * @return a list of unique team numbers present in the history
     */
    public List<String> listTeams()
    {
        return m_scoutedMatches.stream()
                .map(MatchData::getTeamNumber)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * @return a list of unique event codes present in the history
     */
    public List<String> listEventCodes()
    {
        return m_scoutedMatches.stream()
                .map(MatchData::getEventCode)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * @return a list of unique scout names present in the history
     */
    public List<String> listScouts()
    {
        return m_scoutedMatches.stream()
                .map(MatchData::getScoutName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
