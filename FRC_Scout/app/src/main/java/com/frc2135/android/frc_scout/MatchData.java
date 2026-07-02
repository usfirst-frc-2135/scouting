package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Data model for a single scouted match.
 * Handles serialization to JSON for storage and TSV for QR code generation.
 */
public class MatchData
{
    private static final String TAG = "MatchData";

    public static final double M_JSON_FORMAT_VERSION = 26.1;

    // Keys used for reading/writing match JSON files.

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

    // Data members
    private final String m_matchID;
    private Date m_timestamp;

    private final double m_version;
    private String m_eventCode;
    private String m_matchNumber;
    private String m_teamNumber;
    private String m_teamAlias;
    private String m_scoutName;

    private boolean m_autonPreload;
    private int m_autonPreloadAccRate;
    private int m_autonHopper;
    private int m_autonAccuracyRate;
    private boolean m_autonAz;
    private boolean m_autonDepot;
    private boolean m_autonOutpost;
    private boolean m_autonNz;
    private int m_autonClimb;

    private int m_hoppersUsed;
    private int m_accuracyRate;
    private boolean m_intakeAndShoot;
    private boolean m_shovelFuel;
    private int m_passingRate;
    private int m_defenseRate;
    private int m_drivingAbility;
    private int m_passedAz;
    private int m_passedNz;
    private int m_teleopPhoto;

    private int m_diedValue;
    private int m_endgameClimbPos;
    private int m_startClimb;
    private int m_endgameClimbLevel;

    private String m_comment;
    private final String m_other2;
    private final String m_other3;
    private final String m_other4;

    /**
     * Utility to strip off non-digit prefixes (like "frc") from a team number string.
     *
     * @param teamName the team identifier string
     * @return the numeric portion of the team identifier
     */
    public static String stripTeamNumPrefix(String teamName)
    {
        if (teamName == null)
        {
            return "";
        }
        return teamName.replaceAll("^\\D+", "");
    }

    /**
     * Default constructor for creating a new match data record.
     *
     * @param context the application context for retrieving event match data settings
     * @throws IOException   if loading match data fails
     * @throws JSONException if parsing match data fails
     */
    public MatchData(Context context)
            throws IOException, JSONException
    {
        m_matchID = UUID.randomUUID().toString();
        m_timestamp = Calendar.getInstance().getTime();
        m_version = M_JSON_FORMAT_VERSION;
        m_eventCode = CurrentEventCode.get(context).getEventCode();
        m_matchNumber = "";
        m_teamNumber = "";
        m_teamAlias = "";
        m_scoutName = "";

        m_autonPreload = false;
        m_autonPreloadAccRate = 0;
        m_autonHopper = 0;
        m_autonAccuracyRate = 0;
        m_autonAz = false;
        m_autonDepot = false;
        m_autonOutpost = false;
        m_autonNz = false;
        m_autonClimb = 0;

        m_hoppersUsed = 0;
        m_accuracyRate = 0;
        m_intakeAndShoot = false;
        m_passingRate = 5;
        m_defenseRate = 0;
        m_drivingAbility = 6;
        m_passedNz = 3;
        m_passedAz = 3;
        m_shovelFuel = false;
        m_teleopPhoto = 0;

        m_startClimb = 0;
        m_endgameClimbLevel = 0;
        m_endgameClimbPos = 0;
        m_diedValue = 0;
        m_comment = "";

        m_other2 = "0";
        m_other3 = "0";
        m_other4 = "0";
    }

    /**
     * Constructs a MatchData object from a {@link JSONObject}.
     *
     * @param json the source JSONObject
     */
    public MatchData(JSONObject json)
    {
        Log.d(TAG, "Reconstructing MatchData from JSON");

        m_matchID = json.optString(JSON_KEY_MATCH_ID, UUID.randomUUID().toString());
        String dateStr = json.optString(JSON_KEY_TIMESTAMP, "");
        // Use a consistent ISO format for storage; fall back to the old default format if parsing fails.
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        SimpleDateFormat oldFormat = new SimpleDateFormat("MMM dd hh:mm:ss z yyyy", Locale.US);

        try
        {
            if (dateStr.contains("T"))
            {
                m_timestamp = isoFormat.parse(dateStr);
            }
            else
            {
                m_timestamp = oldFormat.parse(dateStr);
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, "Failed to parse timestamp, using current time: " + e.getMessage());
            m_timestamp = Calendar.getInstance().getTime();
        }

        m_version = json.optDouble(JSON_KEY_VERSION, 26.1);
        setEventCode(json.optString(JSON_KEY_EVENT_CODE, ""));
        setMatchNumber(json.optString(JSON_KEY_MATCH_NUMBER, ""));
        setTeamNumber(json.optString(JSON_KEY_TEAM_NUMBER, ""));
        setTeamAlias(json.optString(JSON_KEY_TEAM_ALIAS, ""));
        setScoutName(json.optString(JSON_KEY_SCOUT_NAME, ""));

        m_autonPreload = json.optBoolean(JSON_KEY_PRELOAD, false);
        m_autonPreloadAccRate = json.optInt(JSON_KEY_AUTON_PRELOAD_ACCURACY_RATE, 0);
        m_autonHopper = json.optInt(JSON_KEY_AUTON_HOPPER, 0);
        m_autonAccuracyRate = json.optInt(JSON_KEY_AUTON_ACCURACY_RATE, 0);
        m_autonAz = json.optBoolean(JSON_KEY_AUTON_AZ, false);
        m_autonDepot = json.optBoolean(JSON_KEY_AUTON_DEPOT, false);
        m_autonOutpost = json.optBoolean(JSON_KEY_AUTON_OUTPOST, false);
        m_autonNz = json.optBoolean(JSON_KEY_AUTON_NZ, false);
        m_autonClimb = json.optInt(JSON_KEY_AUTON_CLIMB, 0);

        m_hoppersUsed = json.optInt(JSON_KEY_HOPPERS_USED, 0);
        m_accuracyRate = json.optInt(JSON_KEY_ACCURACY_RATE, 0);
        m_intakeAndShoot = json.optBoolean(JSON_KEY_INTAKE_AND_SHOOT, false);
        m_passingRate = json.optInt(JSON_KEY_PASSING_RATE, 5);
        m_defenseRate = json.optInt(JSON_KEY_DEFENSE_RATE, 0);
        m_drivingAbility = json.optInt(JSON_KEY_DRIVE_ABILITY, 6);
        m_passedAz = json.optInt(JSON_KEY_PASS_ALLIANCE_ZONE, 3);
        m_passedNz = json.optInt(JSON_KEY_PASS_NEUTRAL_ZONE, 3);
        m_shovelFuel = json.optBoolean(JSON_KEY_OTHER1, false);
        m_teleopPhoto = json.optInt(JSON_KEY_TELEOP_PHOTO, 0);

        m_diedValue = json.optInt(JSON_KEY_DIED, 0);
        m_startClimb = json.optInt(JSON_KEY_START_CLIMB, 0);
        m_endgameClimbLevel = json.optInt(JSON_KEY_CLIMB_LEVEL, 0);
        m_endgameClimbPos = json.optInt(JSON_KEY_ENDGAME_CLIMB_POS, 0);
        m_comment = json.optString(JSON_KEY_COMMENTS, "");

        m_other2 = json.optString(JSON_KEY_OTHER2, "0");
        m_other3 = json.optString(JSON_KEY_OTHER3, "0");
        m_other4 = json.optString(JSON_KEY_OTHER4, "0");
    }

    public void setTimestamp(Date d)
    {
        m_timestamp = d;
    }

    public Date getTimestamp()
    {
        return m_timestamp;
    }

    public String getMatchID()
    {
        return m_matchID;
    }


    public void setEventCode(String code)
    {
        m_eventCode = code;
    }

    public String getEventCode()
    {
        return m_eventCode;
    }

    public void setMatchNumber(String num)
    {
        m_matchNumber = num;
    }

    public String getMatchNumber()
    {
        return m_matchNumber;
    }

    public void setTeamNumber(String num)
    {
        m_teamNumber = num;
    }

    public String getTeamNumber()
    {
        return m_teamNumber;
    }

    public void setTeamAlias(String alias)
    {
        m_teamAlias = alias;
    }

    @SuppressWarnings("unused")
    public String getTeamAlias()
    {
        return m_teamAlias != null ? m_teamAlias : "";
    }

    public void setScoutName(String name)
    {
        if (name == null || name.trim().isEmpty())
        {
            m_scoutName = "";
            return;
        }
        String trimmed = name.trim();
        if (trimmed.length() == 1)
        {
            m_scoutName = trimmed.toUpperCase();
        }
        else
        {
            m_scoutName = trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
        }
    }

    public String getScoutName()
    {
        return m_scoutName;
    }


    // Auton accessors
    public void setAutonHopper(int val)
    {
        m_autonHopper = val;
    }

    public int getAutonHopper()
    {
        return m_autonHopper;
    }

    public boolean getAutonPreload()
    {
        return m_autonPreload;
    }

    public void setAutonPreload(boolean val)
    {
        m_autonPreload = val;
    }

    public boolean getAutonAzCheckbox()
    {
        return m_autonAz;
    }

    public void setAutonAzCheckbox(boolean val)
    {
        m_autonAz = val;
    }

    public boolean getAutonDepotCheckbox()
    {
        return m_autonDepot;
    }

    public void setAutonDepotCheckbox(boolean val)
    {
        m_autonDepot = val;
    }

    public boolean getAutonOutpostCheckbox()
    {
        return m_autonOutpost;
    }

    public void setAutonOutpostCheckbox(boolean val)
    {
        m_autonOutpost = val;
    }

    public boolean getAutonNzCheckbox()
    {
        return m_autonNz;
    }

    public void setAutonNzCheckbox(boolean val)
    {
        m_autonNz = val;
    }

    public void setAutonAccuracyRate(int val)
    {
        m_autonAccuracyRate = val;
    }

    public int getAutonAccuracyRate()
    {
        return m_autonAccuracyRate;
    }

    public void setPreloadAccuracyLevel(int val)
    {
        m_autonPreloadAccRate = val;
    }

    public int getPreloadAccuracyLevel()
    {
        return m_autonPreloadAccRate;
    }

    public void setAutonClimb(int val)
    {
        m_autonClimb = val;
    }

    public int getAutonClimb()
    {
        return m_autonClimb;
    }

    // Teleop accessors
    public void setHoppersUsed(int val)
    {
        m_hoppersUsed = val;
    }

    public int getHoppersUsed()
    {
        return m_hoppersUsed;
    }

    public void setAccuracyRate(int val)
    {
        m_accuracyRate = val;
    }

    public int getAccuracyRate()
    {
        return m_accuracyRate;
    }

    public void setIntakeAndShoot(boolean val)
    {
        m_intakeAndShoot = val;
    }

    public boolean getIntakeAndShoot()
    {
        return m_intakeAndShoot;
    }

    public void setShovelFuel(boolean val)
    {
        m_shovelFuel = val;
    }

    public boolean getShovelFuel()
    {
        return m_shovelFuel;
    }

    public void setPassingRate(int val)
    {
        m_passingRate = val;
    }

    public int getPassingEffectivenessRate()
    {
        return m_passingRate;
    }

    public void setDefenseRate(int val)
    {
        m_defenseRate = val;
    }

    public int getDefenseRate()
    {
        return m_defenseRate;
    }

    public void setDriveAbility(int val)
    {
        m_drivingAbility = val;
    }

    public int getDriverAbility()
    {
        return m_drivingAbility;
    }

    public void setPassNeutralZone(int val)
    {
        m_passedNz = val;
    }

    public int getPassNeutralZone()
    {
        return m_passedNz;
    }

    public void setPassAllianceZone(int val)
    {
        m_passedAz = val;
    }

    public int getPassAllianceZone()
    {
        return m_passedAz;
    }

    public void setTeleopPhoto(int val)
    {
        m_teleopPhoto = val;
    }

    public int getTeleopPhoto()
    {
        return m_teleopPhoto;
    }

    // Endgame accessors
    public void setDiedValue(int val)
    {
        m_diedValue = val;
    }

    public int getDiedValue()
    {
        return m_diedValue;
    }

    public void setStartClimb(int val)
    {
        m_startClimb = val;
    }

    public int getStartClimb()
    {
        return m_startClimb;
    }

    public void setEndgameClimbLevel(int val)
    {
        m_endgameClimbLevel = val;
    }

    public int getEndgameClimbLevel()
    {
        return m_endgameClimbLevel;
    }

    public void setEndgameClimbPos(int val)
    {
        m_endgameClimbPos = val;
    }

    public int getEndgameClimbPos()
    {
        return m_endgameClimbPos;
    }

    public void setComment(String comment)
    {
        m_comment = comment;
    }

    public String getComment()
    {
        return m_comment != null ? m_comment : "";
    }

    @SuppressWarnings("unused")
    /**
     * Encodes the match data into a Tab-Separated Values (TSV) string for QR code generation.
     *
     * @return the TSV encoded string
     */
    public String encodeToTSV()
    {
        String teamAliasClean = (m_teamAlias == null || m_teamAlias.isEmpty()) ? "-" : m_teamAlias;
        String commentClean = (m_comment == null || m_comment.trim().isEmpty()) ? "-" : m_comment.trim();

        // Ensure 32 arguments match exactly 32 format specifiers.
        return String.format(Locale.US,
                "%s\t%s\t%s\t%s\t%s\t%s\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%s\t%d\t%s\t%s\t%s",
                m_version,
                m_eventCode,
                m_matchNumber,
                stripTeamNumPrefix(m_teamNumber),
                teamAliasClean,
                m_scoutName,

                m_diedValue,
                m_autonPreload ? 1 : 0,
                m_autonPreloadAccRate,
                m_autonHopper,
                m_autonAccuracyRate,
                m_autonAz ? 1 : 0,
                m_autonDepot ? 1 : 0,
                m_autonOutpost ? 1 : 0,
                m_autonNz ? 1 : 0,
                m_autonClimb,
                m_hoppersUsed,
                m_accuracyRate,
                m_intakeAndShoot ? 1 : 0,
                m_passingRate,
                m_defenseRate,
                m_drivingAbility,
                m_passedAz,
                m_passedNz,
                m_startClimb,
                m_endgameClimbLevel,
                m_endgameClimbPos,
                commentClean,
                m_shovelFuel ? 1 : 0,
                m_other2,
                m_other3,
                m_other4
        );
    }

    /**
     * Serializes the match data into a {@link JSONObject} for file storage.
     *
     * @return the serialized JSONObject
     * @throws JSONException if serialization fails
     */
    public JSONObject toJSON()
            throws JSONException
    {
        JSONObject json = new JSONObject();

        // Save match ID and timestamp in a stable, machine-readable format.
        json.put(JSON_KEY_MATCH_ID, m_matchID);
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        json.put(JSON_KEY_TIMESTAMP, isoFormat.format(m_timestamp));

        // Match data fields starting with those not specific for a game
        json.put(JSON_KEY_VERSION, m_version);
        json.put(JSON_KEY_EVENT_CODE, m_eventCode);
        json.put(JSON_KEY_MATCH_NUMBER, m_matchNumber);
        json.put(JSON_KEY_TEAM_NUMBER, m_teamNumber);
        json.put(JSON_KEY_TEAM_ALIAS, m_teamAlias);
        json.put(JSON_KEY_SCOUT_NAME, m_scoutName);

        // Game-specific match data fields
        json.put(JSON_KEY_PRELOAD, m_autonPreload);
        json.put(JSON_KEY_AUTON_PRELOAD_ACCURACY_RATE, m_autonPreloadAccRate);
        json.put(JSON_KEY_AUTON_HOPPER, m_autonHopper);
        json.put(JSON_KEY_AUTON_ACCURACY_RATE, m_autonAccuracyRate);
        json.put(JSON_KEY_AUTON_AZ, m_autonAz);
        json.put(JSON_KEY_AUTON_DEPOT, m_autonDepot);
        json.put(JSON_KEY_AUTON_OUTPOST, m_autonOutpost);
        json.put(JSON_KEY_AUTON_NZ, m_autonNz);
        json.put(JSON_KEY_AUTON_CLIMB, m_autonClimb);

        json.put(JSON_KEY_HOPPERS_USED, m_hoppersUsed);
        json.put(JSON_KEY_ACCURACY_RATE, m_accuracyRate);
        json.put(JSON_KEY_INTAKE_AND_SHOOT, m_intakeAndShoot);
        json.put(JSON_KEY_PASSING_RATE, m_passingRate);
        json.put(JSON_KEY_DEFENSE_RATE, m_defenseRate);
        json.put(JSON_KEY_DRIVE_ABILITY, m_drivingAbility);
        json.put(JSON_KEY_PASS_ALLIANCE_ZONE, m_passedAz);
        json.put(JSON_KEY_PASS_NEUTRAL_ZONE, m_passedNz);
        json.put(JSON_KEY_TELEOP_PHOTO, m_teleopPhoto);

        json.put(JSON_KEY_START_CLIMB, m_startClimb);
        json.put(JSON_KEY_CLIMB_LEVEL, m_endgameClimbLevel);
        json.put(JSON_KEY_ENDGAME_CLIMB_POS, m_endgameClimbPos);
        json.put(JSON_KEY_DIED, m_diedValue);
        json.put(JSON_KEY_COMMENTS, m_comment);

        json.put(JSON_KEY_OTHER1, m_shovelFuel);
        json.put(JSON_KEY_OTHER2, m_other2);
        json.put(JSON_KEY_OTHER3, m_other3);
        json.put(JSON_KEY_OTHER4, m_other4);

        return json;
    }

    public String getMatchFileName()
    {
        return m_matchID + ".json";
    }
}
