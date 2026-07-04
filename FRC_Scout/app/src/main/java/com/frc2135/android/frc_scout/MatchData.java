package com.frc2135.android.frc_scout;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Data model for a single scouted match.
 * Handles representation of match data and TSV encoding for QR codes.
 */
public class MatchData
{
    private static final String TAG = "MatchData";

    public static final double M_JSON_FORMAT_VERSION = 26.1;

    // Data members
    private String m_matchID;
    private Date m_timestamp;
    private double m_version;
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
    private String m_other2;
    private String m_other3;
    private String m_other4;

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
     */
    public MatchData()
    {
        Log.d(TAG, "MatchData constructor");
        m_matchID = UUID.randomUUID().toString();
        m_timestamp = Calendar.getInstance().getTime();
        m_version = M_JSON_FORMAT_VERSION;
        m_eventCode = "";
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
        m_matchID = json.optString("matchId", UUID.randomUUID().toString());

        String dateStr = json.optString("timestamp", "");
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        SimpleDateFormat oldFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

        try
        {
            if (dateStr.contains("T"))
            {
                m_timestamp = isoFormat.parse(dateStr);
            }
            else if (!dateStr.isEmpty())
            {
                m_timestamp = oldFormat.parse(dateStr);
            }
            else
            {
                m_timestamp = Calendar.getInstance().getTime();
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, "Failed to parse timestamp: " + e.getMessage());
            m_timestamp = Calendar.getInstance().getTime();
        }

        m_version = json.optDouble("version", M_JSON_FORMAT_VERSION);
        m_eventCode = json.optString("eventCode", "");
        m_matchNumber = json.optString("matchNumber", "");
        m_teamNumber = json.optString("teamNumber", "");
        m_teamAlias = json.optString("teamAlias", "");
        setScoutName(json.optString("scoutName", ""));

        m_autonPreload = json.optBoolean("preload", false);
        m_autonPreloadAccRate = json.optInt("autonPreloadAccRate", 0);
        m_autonHopper = json.optInt("autonHopper", 0);
        m_autonAccuracyRate = json.optInt("autonAccuracyRate", 0);
        m_autonAz = json.optBoolean("autonAz", false);
        m_autonDepot = json.optBoolean("autonDepot", false);
        m_autonOutpost = json.optBoolean("autonOutpost", false);
        m_autonNz = json.optBoolean("autonNz", false);
        m_autonClimb = json.optInt("autonClimb", 0);

        m_hoppersUsed = json.optInt("hoppersUsed", 0);
        m_accuracyRate = json.optInt("accuracyRate", 0);
        m_intakeAndShoot = json.optBoolean("intakeAndShoot", false);
        m_passingRate = json.optInt("passingRate", 5);
        m_defenseRate = json.optInt("defenseRate", 0);
        m_drivingAbility = json.optInt("driveAbility", 6);
        m_passedAz = json.optInt("allianceZone", 3);
        m_passedNz = json.optInt("neutralZone", 3);
        m_teleopPhoto = json.optInt("teleopPhoto", 0);

        m_diedValue = json.optInt("died", 0);
        m_startClimb = json.optInt("startClimb", 0);
        m_endgameClimbLevel = json.optInt("climbLevel", 0);
        m_endgameClimbPos = json.optInt("endgameClimbPos", 0);
        m_comment = json.optString("comments", "");

        m_shovelFuel = json.optBoolean("other1", false);
        m_other2 = json.optString("other2", "0");
        m_other3 = json.optString("other3", "0");
        m_other4 = json.optString("other4", "0");
    }

    /**
     * Serializes this MatchData record to a {@link JSONObject}.
     *
     * @return the serialized JSONObject
     * @throws JSONException if JSON creation fails
     */
    public JSONObject toJSON() throws JSONException
    {
        JSONObject json = new JSONObject();

        json.put("matchId", m_matchID);
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        json.put("timestamp", isoFormat.format(m_timestamp));

        json.put("version", m_version);
        json.put("eventCode", m_eventCode);
        json.put("matchNumber", m_matchNumber);
        json.put("teamNumber", m_teamNumber);
        json.put("teamAlias", m_teamAlias);
        json.put("scoutName", m_scoutName);

        json.put("preload", m_autonPreload);
        json.put("autonPreloadAccRate", m_autonPreloadAccRate);
        json.put("autonHopper", m_autonHopper);
        json.put("autonAccuracyRate", m_autonAccuracyRate);
        json.put("autonAz", m_autonAz);
        json.put("autonDepot", m_autonDepot);
        json.put("autonOutpost", m_autonOutpost);
        json.put("autonNz", m_autonNz);
        json.put("autonClimb", m_autonClimb);

        json.put("hoppersUsed", m_hoppersUsed);
        json.put("accuracyRate", m_accuracyRate);
        json.put("intakeAndShoot", m_intakeAndShoot);
        json.put("passingRate", m_passingRate);
        json.put("defenseRate", m_defenseRate);
        json.put("driveAbility", m_drivingAbility);
        json.put("allianceZone", m_passedAz);
        json.put("neutralZone", m_passedNz);
        json.put("teleopPhoto", m_teleopPhoto);

        json.put("startClimb", m_startClimb);
        json.put("climbLevel", m_endgameClimbLevel);
        json.put("endgameClimbPos", m_endgameClimbPos);
        json.put("died", m_diedValue);
        json.put("comments", m_comment);

        json.put("other1", m_shovelFuel);
        json.put("other2", m_other2);
        json.put("other3", m_other3);
        json.put("other4", m_other4);

        return json;
    }

    /**
     * Sets the timestamp for when this match was scouted.
     *
     * @param d the date and time
     */
    public void setTimestamp(Date d)
    {
        m_timestamp = d;
    }

    /**
     * Returns the timestamp for when this match was scouted.
     *
     * @return the date and time
     */
    public Date getTimestamp()
    {
        return m_timestamp;
    }

    /**
     * Returns the unique identifier for this match record.
     *
     * @return the match UUID string
     */
    public String getMatchID()
    {
        return m_matchID;
    }

    /**
     * Sets the unique identifier for this match record.
     *
     * @param id the match UUID string
     */
    public void setMatchID(String id)
    {
        m_matchID = id;
    }

    /**
     * Returns the version of the match data format.
     *
     * @return the version number
     */
    public double getVersion()
    {
        return m_version;
    }

    /**
     * Sets the version of the match data format.
     *
     * @param version the version number
     */
    public void setVersion(double version)
    {
        m_version = version;
    }

    /**
     * Sets the FRC event code for this match.
     *
     * @param code the event code
     */
    public void setEventCode(String code)
    {
        m_eventCode = code;
    }

    /**
     * Returns the FRC event code for this match.
     *
     * @return the event code
     */
    public String getEventCode()
    {
        return m_eventCode;
    }

    /**
     * Sets the match identifier (e.g., "qm1").
     *
     * @param num the match number string
     */
    public void setMatchNumber(String num)
    {
        m_matchNumber = num;
    }

    /**
     * Returns the match identifier.
     *
     * @return the match number string
     */
    public String getMatchNumber()
    {
        return m_matchNumber;
    }

    /**
     * Sets the FRC team number for this match.
     *
     * @param num the team number string
     */
    public void setTeamNumber(String num)
    {
        m_teamNumber = num;
    }

    /**
     * Returns the FRC team number.
     *
     * @return the team number string
     */
    public String getTeamNumber()
    {
        return m_teamNumber;
    }

    /**
     * Sets the team alias if applicable (e.g. for regional variants).
     *
     * @param alias the team alias string
     */
    public void setTeamAlias(String alias)
    {
        m_teamAlias = alias;
    }

    /**
     * Returns the team alias.
     *
     * @return the team alias string, or empty if none
     */
    public String getTeamAlias()
    {
        return m_teamAlias != null ? m_teamAlias : "";
    }

    /**
     * Sets the name of the scout who recorded this match.
     * Standardizes casing (e.g. "John" or "J").
     *
     * @param name the scout's name
     */
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

    public String getOther2()
    {
        return m_other2;
    }

    public void setOther2(String value)
    {
        m_other2 = value;
    }

    public String getOther3()
    {
        return m_other3;
    }

    public void setOther3(String value)
    {
        m_other3 = value;
    }

    public String getOther4()
    {
        return m_other4;
    }

    public void setOther4(String value)
    {
        m_other4 = value;
    }

    public String getMatchFileName()
    {
        return m_matchID + ".json";
    }

    /**
     * Validates the match data.
     *
     * @return a validation message string, or empty if valid
     */
    public String validate()
    {
        StringBuilder msg = new StringBuilder();

        // Teleop Passing validation
        if (m_passedNz == 3 || m_passedAz == 3)
        {
            if (m_passedNz == 3 && m_passedAz == 3)
            {
                msg.append("Teleop: Passing From Neutral/Alliance Zone buttons must be set!\n");
            }
            else if (m_passedNz == 3)
            {
                msg.append("Teleop: Passing From Neutral Zone button must be set\n");
            }
            else
            {
                msg.append("Teleop: Passing From Alliance Zone button must be set\n");
            }
        }

        // Climb selections validation
        if ((m_startClimb == 0 && (m_endgameClimbLevel != 0 || m_endgameClimbPos != 0)) ||
                (m_endgameClimbLevel == 0 && m_startClimb != 0) ||
                (m_endgameClimbPos == 0 && m_startClimb != 0))
        {
            msg.append("\nEndgame: Start Climb, Climb Level and Climb Position settings don't match!\n");
        }

        // Driver ability validation
        if (m_drivingAbility == 6)
        {
            msg.append("\nTeleop: Driver ability not set!\n");
        }

        // Passing rate validation
        if (((m_passedNz == 1 || m_passedAz == 1) && m_passingRate == 0) || (m_passingRate == 5) || ((m_passedNz == 0 && m_passedAz == 0) && m_passingRate > 0))
        {
            if ((m_passedNz == 1 || m_passedAz == 1) && m_passingRate == 0)
            {
                msg.append("\nTeleop: Passed from zone set, Passing rate not set!\n");
            }
            else if (m_passingRate == 5)
            {
                msg.append("\nTeleop: Passing rate not set!\n");
            }
            else
            {
                msg.append("\nTeleop: Passing rate set, Passed from zone not set!\n");
            }
        }

        return msg.toString();
    }

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
}
