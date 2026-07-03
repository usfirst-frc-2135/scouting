package com.frc2135.android.frc_scout;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Serializer class for managing match data persistence.
 * Handles individual match JSON files.
 */
public class MatchDataSerializer extends BaseJSONSerializer
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

    public MatchDataSerializer(android.content.Context context)
    {
        super(context);
        Log.d(TAG, "MatchDataSerializer constructor");
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
        saveJSONArray(file, array);
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
        Log.d(TAG, "loadSingleMatch()");
        JSONArray array = loadJSONArray(file);
        if (array == null || array.length() == 0)
        {
            throw new JSONException("Empty or invalid match data array in file: " + file.getName());
        }
        return deserializeMatchData(array.getJSONObject(0));
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
}
