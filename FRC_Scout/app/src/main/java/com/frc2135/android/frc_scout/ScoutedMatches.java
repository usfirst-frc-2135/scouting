/*
 * Copyright (c) 2020-26 FRC 2135 Presentation Invasion
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
 * Singleton class for managing the collection of scouted match records.
 * <p>
 * This class serves as the primary repository for all match data gathered by the user.
 * It handles the batch loading of individual match files (one JSON file per match)
 * from local storage into an in-memory list. It provides high-level APIs for:
 * - Adding, deleting, and searching for specific matches.
 * - Sorting and filtering the match history based on various criteria.
 * - Dynamic data reloads to revert unsaved changes.
 * - Aggregating unique metadata (teams, events, scouts) for UI dropdowns.
 */
public class ScoutedMatches extends BaseJSONSerializer
{
    private static final String TAG = "ScoutedMatches";

    private final List<MatchData> m_scoutedMatches;
    private static volatile ScoutedMatches sScoutedMatches;

    /**
     * Initializes the scouted matches collection by scanning and loading all match data files from local storage.
     *
     * @param appContext the application context used for file operations
     */
    private ScoutedMatches(Context appContext)
    {
        super(appContext);
        Log.v(TAG, "ScoutedMatches constructor");
        m_scoutedMatches = loadInitialData();
    }

    /**
     * Triggers the initial load of match records from the filesystem.
     *
     * @return the list of loaded {@link MatchData} objects
     */
    private List<MatchData> loadInitialData()
    {
        Log.d(TAG, "loadInitialData");
        return loadScoutedMatchesList();
    }

    /**
     * Returns the thread-safe singleton instance of ScoutedMatches.
     *
     * @param context the context used to initialize the instance
     * @return the singleton ScoutedMatches instance
     */
    public static ScoutedMatches getInstance(Context context)
    {
        Log.v(TAG, "getInstance");
        synchronized (ScoutedMatches.class)
        {
            if (sScoutedMatches == null)
            {
                Log.i(TAG, "Creating new sScoutedMatches");
                sScoutedMatches = new ScoutedMatches(context);
            }
            return sScoutedMatches;
        }
    }

    /**
     * Returns the complete list of all scouted match records currently in memory.
     *
     * @return the list of {@link MatchData} objects
     */
    public List<MatchData> getMatchList()
    {
        return m_scoutedMatches;
    }

    /**
     * Searches for a specific match record by its unique identifier.
     *
     * @param matchId the unique UUID string of the match
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
     * Deletes a match record from both the in-memory list and the local filesystem.
     *
     * @param match the MatchData object to remove
     */
    public void deleteMatch(MatchData match)
    {
        if (match != null)
        {
            m_scoutedMatches.remove(match);
            String filename = getMatchFileName(match);
            File file = new File(m_dataDir, filename);
            if (file.exists() && file.delete())
            {
                Log.i(TAG, "Successfully deleted match file: " + filename);
            }
            else
            {
                Log.w(TAG, "Failed to delete match file: " + filename);
            }
        }
    }

    /**
     * Adds a newly created match record to the in-memory collection.
     *
     * @param matchData the match record to add
     */
    public void addMatch(MatchData matchData)
    {
        if (matchData != null)
        {
            m_scoutedMatches.add(matchData);
        }
    }

    /**
     * Generates the standard filename used to persist a match record based on its unique identifier.
     *
     * @param match the match data record
     * @return the generated filename string (e.g., "md_UUID.json")
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
     * Persists a specific match record to its own JSON file in local storage.
     *
     * @param matchData the match record to save
     * @return true if the save operation was successful, false otherwise
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
     * Reloads a specific match record from its JSON file on disk, replacing the in-memory version.
     * This is useful for discarding unsaved changes.
     *
     * @param matchData the match record to reload
     * @return the reloaded MatchData object, or the original if reload fails
     */
    public boolean reloadMatchDataFromFile(MatchData matchData)
    {
        if (matchData == null)
        {
            return false;
        }
        Log.d(TAG, "reloadMatchDataFromFile for ID: " + matchData.getMatchID());
        String filename = getMatchFileName(matchData);
        File file = new File(m_dataDir, filename);
        if (file.exists())
        {
            try
            {
                MatchData reloadedMatch = loadSingleMatchFile(file);
                int index = m_scoutedMatches.indexOf(matchData);
                if (index != -1)
                {
                    m_scoutedMatches.set(index, reloadedMatch);
                }
                return true;
            }
            catch (IOException | JSONException e)
            {
                Log.e(TAG, "Error reloading match data file " + filename + ": " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Scans the application's internal data directory and loads all individual match data files into memory.
     *
     * @return an ArrayList of all successfully loaded {@link MatchData} records
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
            // Match files are identified by their UUID-based filename length (36 chars + prefix/suffix)
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

        Log.i(TAG, "Loaded " + matchHistory.size() + " scouted match files!");

        return matchHistory;
    }

    /**
     * Loads and parses a single match record from its JSON file.
     *
     * @param file the source file to load from
     * @return the parsed MatchData record
     * @throws IOException   if reading the file fails
     * @throws JSONException if the file content is not a valid JSON array containing match data
     */
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
     * Sorts a given list of match records based on specific criteria and order.
     *
     * @param list      the list of matches to sort
     * @param criteria  the sorting criteria; one of "Date", "Team", or "Match"
     * @param ascending true to sort in ascending order, false for descending
     * @return a new sorted ArrayList of match records
     */
    public ArrayList<MatchData> sortMatchList(List<MatchData> list, String criteria, boolean ascending)
    {
        ArrayList<MatchData> sortedList = new ArrayList<>(list);
        Comparator<MatchData> baseComparator = switch (criteria)
        {
            case "Team" -> Comparator.comparing(m -> {
                String digits = m.getTeamNumber();
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
     * Filters a list of match records based on multiple optional criteria.
     *
     * @param list     the source list of match records to filter
     * @param event    the event code to filter by, or null for no event filter
     * @param matchNum the match identifier to filter by, or null for no match filter
     * @param team     the team identifier to filter by, or null for no team filter
     * @param scout    the scout name to filter by, or null for no scout filter
     * @return a new list containing only the match records that meet all non-null criteria
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
     * Aggregates and returns a unique, sorted list of all team identifiers present in the match history.
     *
     * @return a sorted list of unique team identifier strings
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
     * Aggregates and returns a unique, sorted list of all FRC event codes present in the match history.
     *
     * @return a sorted list of unique event code strings
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
     * Aggregates and returns a unique, sorted list of all scout names present in the match history.
     *
     * @return a sorted list of unique scout name strings
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
