package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Serializer class for managing match data and scout configuration persistence.
 * Handles individual match JSON files and the main settings configuration.
 */
public class MatchDataSerializer
{
    private static final String TAG = "MatchDataSerializer";

    // JSON keys non-specific to a game
    private static final String JSON_KEY_MATCH_ID = "matchId";
    private static final String JSON_KEY_TIMESTAMP = "timestamp";
    private static final String JSON_KEY_VERSION = "version";
    private static final String JSON_KEY_EVENT_CODE = "eventCode";
    private static final String JSON_KEY_MATCH_NUMBER = "matchNumber";
    private static final String JSON_KEY_TEAM_NUMBER = "teamNumber";
    private static final String JSON_KEY_TEAM_ALIAS = "teamAlias";
    private static final String JSON_KEY_SCOUT_NAME = "scoutName";

    // Game-specific match data json keys

    // Auton data keys
    private static final String JSON_KEY_PRELOAD = "preload";
    private static final String JSON_KEY_AUTON_AZ = "autonAz";
    private static final String JSON_KEY_AUTON_DEPOT = "autonDepot";
    private static final String JSON_KEY_AUTON_OUTPOST = "autonOutpost";
    private static final String JSON_KEY_AUTON_NZ = "autonNz";
    private static final String JSON_KEY_AUTON_ACCURACY_RATE = "autonAccuracyRate";
    private static final String JSON_KEY_AUTON_HOPPER = "autonHopper";
    private static final String JSON_KEY_AUTON_PRELOAD_ACCURACY_RATE = "autonPreloadAccRate";
    private static final String JSON_KEY_AUTON_CLIMB = "autonClimb";

    // Teleop data keys
    private static final String JSON_KEY_HOPPERS_USED = "hoppersUsed";
    private static final String JSON_KEY_ACCURACY_RATE = "accuracyRate";
    private static final String JSON_KEY_INTAKE_AND_SHOOT = "intakeAndShoot";
    private static final String JSON_KEY_PASSING_RATE = "passingRate";
    private static final String JSON_KEY_DEFENSE_RATE = "defenseRate";
    private static final String JSON_KEY_DRIVE_ABILITY = "driveAbility";
    private static final String JSON_KEY_PASS_ALLIANCE_ZONE = "allianceZone";
    private static final String JSON_KEY_PASS_NEUTRAL_ZONE = "neutralZone";
    private static final String JSON_KEY_TELEOP_PHOTO = "teleopPhoto";

    // Endgame data keys
    private static final String JSON_KEY_START_CLIMB = "startClimb";
    private static final String JSON_KEY_CLIMB_LEVEL = "climbLevel";
    @SuppressWarnings("GrazieInspectionRunner")
    private static final String JSON_KEY_ENDGAME_CLIMB_POS = "endgameClimbPos";

    private static final String JSON_KEY_DIED = "died";
    private static final String JSON_KEY_COMMENTS = "comments";
    private static final String JSON_KEY_OTHER1 = "other1"; // Used for shovelFuel
    private static final String JSON_KEY_OTHER2 = "other2";
    private static final String JSON_KEY_OTHER3 = "other3";
    private static final String JSON_KEY_OTHER4 = "other4";

    // Settings JSON Keys
    private static final String KEY_EVENT_CODE = "eventCode";
    private static final String KEY_PAST_SCOUTS = "pastScouts";
    private static final String KEY_SCOUT_NAME_PREFIX = "scoutName"; // Legacy key prefix
    private static final String KEY_TEAM_INDEX = "teamIndex";
    private static final String KEY_SCORING_TABLE_SIDE = "scoringTableSide";

    private final Context m_context;
    private final String m_settingsFileName;
    private final File m_dataDir;

    public MatchDataSerializer(Context context, String settingsFileName)
    {
        Log.d(TAG, "MatchDataSerializer constructor");
        m_context = context.getApplicationContext();
        m_settingsFileName = settingsFileName;
        m_dataDir = m_context.getFilesDir();
        Log.d(TAG, "Initialized with data directory: " + m_dataDir.getAbsolutePath());
    }

    public void saveSettings(Settings settings)
            throws JSONException, IOException
    {
        Log.d(TAG, "Saving settings configuration");

        JSONArray array = new JSONArray();
        array.put(serializeSettings(settings));

        File file = new File(m_dataDir, m_settingsFileName);
        try (FileOutputStream out = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
        {
            writer.write(array.toString());
            Log.i(TAG, "Successfully wrote settings file: " + m_settingsFileName);
        }
    }

    public void saveMatchData(MatchData matchData)
            throws JSONException, IOException
    {
        Log.d(TAG, "saveMatchData()");
        if (matchData == null)
        {
            return;
        }

        JSONArray array = new JSONArray();
        array.put(serializeMatchData(matchData));

        String filename = matchData.getMatchFileName();
        File file = new File(m_dataDir, filename);

        try (FileOutputStream out = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
        {
            writer.write(array.toString());
            Log.i(TAG, "Successfully wrote match file: " + filename);
        }
    }

    public void saveAllMatchData(List<MatchData> matchHistory)
            throws JSONException, IOException
    {
        Log.d(TAG, "saveAllMatchData()");
        if (matchHistory == null)
        {
            return;
        }
        Log.d(TAG, "Saving " + matchHistory.size() + " matches to disk");
        for (MatchData match : matchHistory)
        {
            saveMatchData(match);
        }
    }

    public void saveAllData(List<MatchData> matchHistory)
            throws JSONException, IOException
    {
        Log.d(TAG, "saveAllData()");
        saveSettings(Settings.getInstance(m_context));
        saveAllMatchData(matchHistory);
    }

    public ArrayList<MatchData> loadMatchData()
    {
        Log.d(TAG, "loadMatchData()");
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
            if (filename.length() > 30 && filename.endsWith(".json") && !filename.contains("matches") && !filename.contains("aliases") && !filename.contains("scoutNames") && !filename.equals(m_settingsFileName))
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
        Log.d(TAG, "loadSingleMatch()");
        StringBuilder jsonString = new StringBuilder();
        try (FileInputStream in = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            return deserializeMatchData(array.getJSONObject(0));
        }
    }

    public Settings loadSettings()
            throws IOException, JSONException
    {
        Log.d(TAG, "loadSettings()");
        File file = new File(m_dataDir, m_settingsFileName);
        if (!file.exists())
        {
            return null;
        }

        Log.d(TAG, "Loading settings from: " + m_settingsFileName);
        StringBuilder jsonString = new StringBuilder();

        try (FileInputStream in = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                jsonString.append(line);
            }

            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            Settings settings = deserializeSettings(array.getJSONObject(0));
            Log.i(TAG, "Successfully loaded settings file");
            return settings;
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
    }

    public JSONObject serializeMatchData(MatchData m)
            throws JSONException
    {
        JSONObject json = new JSONObject();

        json.put(JSON_KEY_MATCH_ID, m.getMatchID());
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        json.put(JSON_KEY_TIMESTAMP, isoFormat.format(m.getTimestamp()));

        json.put(JSON_KEY_VERSION, m.getVersion());
        json.put(JSON_KEY_EVENT_CODE, m.getEventCode());
        json.put(JSON_KEY_MATCH_NUMBER, m.getMatchNumber());
        json.put(JSON_KEY_TEAM_NUMBER, m.getTeamNumber());
        json.put(JSON_KEY_TEAM_ALIAS, m.getTeamAlias());
        json.put(JSON_KEY_SCOUT_NAME, m.getScoutName());

        json.put(JSON_KEY_PRELOAD, m.getAutonPreload());
        json.put(JSON_KEY_AUTON_PRELOAD_ACCURACY_RATE, m.getPreloadAccuracyLevel());
        json.put(JSON_KEY_AUTON_HOPPER, m.getAutonHopper());
        json.put(JSON_KEY_AUTON_ACCURACY_RATE, m.getAutonAccuracyRate());
        json.put(JSON_KEY_AUTON_AZ, m.getAutonAzCheckbox());
        json.put(JSON_KEY_AUTON_DEPOT, m.getAutonDepotCheckbox());
        json.put(JSON_KEY_AUTON_OUTPOST, m.getAutonOutpostCheckbox());
        json.put(JSON_KEY_AUTON_NZ, m.getAutonNzCheckbox());
        json.put(JSON_KEY_AUTON_CLIMB, m.getAutonClimb());

        json.put(JSON_KEY_HOPPERS_USED, m.getHoppersUsed());
        json.put(JSON_KEY_ACCURACY_RATE, m.getAccuracyRate());
        json.put(JSON_KEY_INTAKE_AND_SHOOT, m.getIntakeAndShoot());
        json.put(JSON_KEY_PASSING_RATE, m.getPassingEffectivenessRate());
        json.put(JSON_KEY_DEFENSE_RATE, m.getDefenseRate());
        json.put(JSON_KEY_DRIVE_ABILITY, m.getDriverAbility());
        json.put(JSON_KEY_PASS_ALLIANCE_ZONE, m.getPassAllianceZone());
        json.put(JSON_KEY_PASS_NEUTRAL_ZONE, m.getPassNeutralZone());
        json.put(JSON_KEY_TELEOP_PHOTO, m.getTeleopPhoto());

        json.put(JSON_KEY_START_CLIMB, m.getStartClimb());
        json.put(JSON_KEY_CLIMB_LEVEL, m.getEndgameClimbLevel());
        json.put(JSON_KEY_ENDGAME_CLIMB_POS, m.getEndgameClimbPos());
        json.put(JSON_KEY_DIED, m.getDiedValue());
        json.put(JSON_KEY_COMMENTS, m.getComment());

        json.put(JSON_KEY_OTHER1, m.getShovelFuel());
        json.put(JSON_KEY_OTHER2, m.getOther2());
        json.put(JSON_KEY_OTHER3, m.getOther3());
        json.put(JSON_KEY_OTHER4, m.getOther4());

        return json;
    }

    public MatchData deserializeMatchData(JSONObject json)
    {
        MatchData m = new MatchData();

        // Remove old date format in 2027
        m.setMatchID(json.optString(JSON_KEY_MATCH_ID, m.getMatchID()));
        String dateStr = json.optString(JSON_KEY_TIMESTAMP, "");
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        SimpleDateFormat oldFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

        try
        {
            if (dateStr.startsWith("2026") || dateStr.startsWith("2027") ||
                    dateStr.startsWith("2028") || dateStr.startsWith("2029"))
            {
                m.setTimestamp(isoFormat.parse(dateStr));
            }
            else if (!dateStr.isEmpty())
            {
                m.setTimestamp(oldFormat.parse(dateStr));
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, "Failed to parse timestamp: " + e.getMessage());
        }

        m.setVersion(json.optDouble(JSON_KEY_VERSION, MatchData.M_JSON_FORMAT_VERSION));
        m.setEventCode(json.optString(JSON_KEY_EVENT_CODE, ""));
        m.setMatchNumber(json.optString(JSON_KEY_MATCH_NUMBER, ""));
        m.setTeamNumber(json.optString(JSON_KEY_TEAM_NUMBER, ""));
        m.setTeamAlias(json.optString(JSON_KEY_TEAM_ALIAS, ""));
        m.setScoutName(json.optString(JSON_KEY_SCOUT_NAME, ""));

        m.setAutonPreload(json.optBoolean(JSON_KEY_PRELOAD, false));
        m.setPreloadAccuracyLevel(json.optInt(JSON_KEY_AUTON_PRELOAD_ACCURACY_RATE, 0));
        m.setAutonHopper(json.optInt(JSON_KEY_AUTON_HOPPER, 0));
        m.setAutonAccuracyRate(json.optInt(JSON_KEY_AUTON_ACCURACY_RATE, 0));
        m.setAutonAzCheckbox(json.optBoolean(JSON_KEY_AUTON_AZ, false));
        m.setAutonDepotCheckbox(json.optBoolean(JSON_KEY_AUTON_DEPOT, false));
        m.setAutonOutpostCheckbox(json.optBoolean(JSON_KEY_AUTON_OUTPOST, false));
        m.setAutonNzCheckbox(json.optBoolean(JSON_KEY_AUTON_NZ, false));
        m.setAutonClimb(json.optInt(JSON_KEY_AUTON_CLIMB, 0));

        m.setHoppersUsed(json.optInt(JSON_KEY_HOPPERS_USED, 0));
        m.setAccuracyRate(json.optInt(JSON_KEY_ACCURACY_RATE, 0));
        m.setIntakeAndShoot(json.optBoolean(JSON_KEY_INTAKE_AND_SHOOT, false));
        m.setPassingRate(json.optInt(JSON_KEY_PASSING_RATE, 5));
        m.setDefenseRate(json.optInt(JSON_KEY_DEFENSE_RATE, 0));
        m.setDriveAbility(json.optInt(JSON_KEY_DRIVE_ABILITY, 6));
        m.setPassAllianceZone(json.optInt(JSON_KEY_PASS_ALLIANCE_ZONE, 3));
        m.setPassNeutralZone(json.optInt(JSON_KEY_PASS_NEUTRAL_ZONE, 3));
        m.setTeleopPhoto(json.optInt(JSON_KEY_TELEOP_PHOTO, 0));

        m.setDiedValue(json.optInt(JSON_KEY_DIED, 0));
        m.setStartClimb(json.optInt(JSON_KEY_START_CLIMB, 0));
        m.setEndgameClimbLevel(json.optInt(JSON_KEY_CLIMB_LEVEL, 0));
        m.setEndgameClimbPos(json.optInt(JSON_KEY_ENDGAME_CLIMB_POS, 0));
        m.setComment(json.optString(JSON_KEY_COMMENTS, ""));

        m.setShovelFuel(json.optBoolean(JSON_KEY_OTHER1, false));
        m.setOther2(json.optString(JSON_KEY_OTHER2, "0"));
        m.setOther3(json.optString(JSON_KEY_OTHER3, "0"));
        m.setOther4(json.optString(JSON_KEY_OTHER4, "0"));

        return m;
    }

    public JSONObject serializeSettings(Settings s)
            throws JSONException
    {
        JSONObject json = new JSONObject();

        json.put(KEY_EVENT_CODE, s.getEventCode());

        JSONArray scoutsArray = new JSONArray();
        for (String name : s.getPastScouts())
        {
            scoutsArray.put(name);
        }
        json.put(KEY_PAST_SCOUTS, scoutsArray);

        json.put(KEY_TEAM_INDEX, s.getTeamIndexStr());
        json.put(KEY_SCORING_TABLE_SIDE, s.getScoringTableSide() ? 1 : 0);

        return json;
    }

    public Settings deserializeSettings(JSONObject json)
            throws JSONException
    {
        Settings s = new Settings();
        s.setEventCode(json.optString(KEY_EVENT_CODE, ""));

        if (json.has(KEY_PAST_SCOUTS))
        {
            JSONArray scoutsArray = json.getJSONArray(KEY_PAST_SCOUTS);
            for (int i = 0; i < scoutsArray.length(); i++)
            {
                s.addPastScoutNames(scoutsArray.getString(i));
            }
        }
        else
        {
            // Fallback to legacy format: scoutName0, scoutName1, ...
            int i = 0;
            while (json.has(KEY_SCOUT_NAME_PREFIX + i))
            {
                s.addPastScoutNames(json.getString(KEY_SCOUT_NAME_PREFIX + i));
                i++;
            }
        }

        s.setTeamIndexStr(json.optString(KEY_TEAM_INDEX, "0 - None"));
        int scoringTableSideVal = json.optInt(KEY_SCORING_TABLE_SIDE, 0);
        s.setScoringTableSide(scoringTableSideVal == 1);

        return s;
    }
}
