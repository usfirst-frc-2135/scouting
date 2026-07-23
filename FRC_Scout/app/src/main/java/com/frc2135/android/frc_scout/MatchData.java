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

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Core data model for a single scouted FRC match.
 * <p>
 * This class encapsulates all parameters gathered during the autonomous, teleoperated,
 * and endgame stages of a match. It provides robust functionality for:
 * - JSON serialization for persistent local storage.
 * - Bidirectional data binding with UI fragments.
 * - Input validation against game-specific rules (e.g., maximum hopper counts).
 * - TSV encoding for high-density QR code generation.
 * <p>
 * Each record is uniquely identified by a UUID to prevent file collisions across different
 * scout tablets and seasons.
 */
public class MatchData
{
    private static final String TAG = "MatchData";

    // JSON Keys
    private static final String KEY_MATCH_ID = "matchId";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_VERSION = "version";
    private static final String KEY_EVENT_CODE = "eventCode";
    private static final String KEY_MATCH_NUMBER = "matchNumber";
    private static final String KEY_TEAM_NUMBER = "teamNumber";
    private static final String KEY_TEAM_ALIAS = "teamAlias";
    private static final String KEY_SCOUT_NAME = "scoutName";

    private static final String KEY_AUTON_PRELOAD = "preload";
    private static final String KEY_AUTON_PRELOAD_ACC_RATE = "autonPreloadAccRate";
    private static final String KEY_AUTON_HOPPER = "autonHopper";
    private static final String KEY_AUTON_ACC_RATE = "autonAccuracyRate";
    private static final String KEY_AUTON_AZ = "autonAz";
    private static final String KEY_AUTON_DEPOT = "autonDepot";
    private static final String KEY_AUTON_OUTPOST = "autonOutpost";
    private static final String KEY_AUTON_NZ = "autonNz";
    private static final String KEY_AUTON_CLIMB = "autonClimb";

    private static final String KEY_TELEOP_HOPPERS_USED = "hoppersUsed";
    private static final String KEY_TELEOP_ACC_RATE = "accuracyRate";
    private static final String KEY_TELEOP_INTAKE_SHOOT = "intakeAndShoot";
    private static final String KEY_TELEOP_PASSING_RATE = "passingRate";
    private static final String KEY_TELEOP_DEFENSE_RATE = "defenseRate";
    private static final String KEY_TELEOP_DRIVE_ABILITY = "driveAbility";
    private static final String KEY_TELEOP_PASS_AZ = "allianceZone";
    private static final String KEY_TELEOP_PASS_NZ = "neutralZone";
    private static final String KEY_TELEOP_PHOTO = "teleopPhoto";

    private static final String KEY_DIED = "died";
    private static final String KEY_START_CLIMB = "startClimb";
    private static final String KEY_CLIMB_LEVEL = "climbLevel";
    @SuppressWarnings("GrazieInspectionRunner")
    private static final String KEY_ENDGAME_CLIMB_POS = "endgameClimbPos";
    private static final String KEY_COMMENTS = "comments";

    private static final String KEY_OTHER1 = "other1";
    private static final String KEY_OTHER2 = "other2";
    private static final String KEY_OTHER3 = "other3";
    private static final String KEY_OTHER4 = "other4";

    /**
     * The version identifier for the JSON format of match data.
     */
    public static final double M_JSON_FORMAT_VERSION = 26.1;

    /**
     * Maximum allowed hopper count in the autonomous stage.
     */
    public static final int MAX_AUTON_HOPPERS = 1;

    /**
     * Maximum allowed hopper count in the teleoperated stage.
     */
    public static final int MAX_TELEOP_HOPPERS = 7;

    // --- Metadata Section ---
    // Unique identifier for this match (used in file names and QR codes)
    private String m_matchID;
    // Date created for this match
    private Date m_timestamp;
    // Version of the match data format
    private double m_version;
    private String m_eventCode;
    private String m_matchNumber;
    private String m_teamNumber;
    private String m_teamAlias;
    private String m_scoutName;

    // --- Autonomous Section ---
    private boolean m_autonPreload;
    private int m_autonPreloadAccRate;
    private int m_autonHopper;
    private int m_autonAccuracyRate;
    private boolean m_autonAz;
    private boolean m_autonDepot;
    private boolean m_autonOutpost;
    private boolean m_autonNz;
    private int m_autonClimb;

    // --- Teleoperated Section ---
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

    // --- Endgame Section ---
    private int m_diedValue;
    private int m_endgameClimbPos;
    private int m_startClimb;
    private int m_endgameClimbLevel;

    private String m_comment;
    private String m_other2;
    private String m_other3;
    private String m_other4;

    /**
     * Extracts the numeric portion from a match identifier (e.g., "qm1" -> "1").
     *
     * @param matchIdentifier the match identifier string
     * @return the numeric portion of the match identifier, or an empty string if null
     */
    public static String extractMatchNumber(String matchIdentifier)
    {
        if (matchIdentifier == null || matchIdentifier.isEmpty())
        {
            return "";
        }
        return matchIdentifier.replaceAll("^\\D+", "");
    }

    /**
     * Default constructor for creating a new match data record.
     * Generates a unique UUID and sets defaults for all scouting parameters.
     */
    public MatchData()
    {
        Log.v(TAG, "MatchData constructor");
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
     * Handles both ISO and legacy date formats for backward compatibility.
     *
     * @param json the source JSONObject
     */
    public MatchData(JSONObject json)
    {
        m_matchID = json.optString(KEY_MATCH_ID, UUID.randomUUID().toString());

        String dateStr = json.optString(KEY_TIMESTAMP, "");
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        SimpleDateFormat oldFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

        try
        {
            if (dateStr.startsWith("202"))
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

        m_version = json.optDouble(KEY_VERSION, M_JSON_FORMAT_VERSION);
        m_eventCode = json.optString(KEY_EVENT_CODE, "");
        m_matchNumber = json.optString(KEY_MATCH_NUMBER, "");
        m_teamNumber = json.optString(KEY_TEAM_NUMBER, "");
        m_teamAlias = json.optString(KEY_TEAM_ALIAS, "");
        setScoutName(json.optString(KEY_SCOUT_NAME, ""));

        m_autonPreload = json.optBoolean(KEY_AUTON_PRELOAD, false);
        m_autonPreloadAccRate = json.optInt(KEY_AUTON_PRELOAD_ACC_RATE, 0);
        m_autonHopper = json.optInt(KEY_AUTON_HOPPER, 0);
        m_autonAccuracyRate = json.optInt(KEY_AUTON_ACC_RATE, 0);
        m_autonAz = json.optBoolean(KEY_AUTON_AZ, false);
        m_autonDepot = json.optBoolean(KEY_AUTON_DEPOT, false);
        m_autonOutpost = json.optBoolean(KEY_AUTON_OUTPOST, false);
        m_autonNz = json.optBoolean(KEY_AUTON_NZ, false);
        m_autonClimb = json.optInt(KEY_AUTON_CLIMB, 0);

        m_hoppersUsed = json.optInt(KEY_TELEOP_HOPPERS_USED, 0);
        m_accuracyRate = json.optInt(KEY_TELEOP_ACC_RATE, 0);
        m_intakeAndShoot = json.optBoolean(KEY_TELEOP_INTAKE_SHOOT, false);
        m_passingRate = json.optInt(KEY_TELEOP_PASSING_RATE, 5);
        m_defenseRate = json.optInt(KEY_TELEOP_DEFENSE_RATE, 0);
        m_drivingAbility = json.optInt(KEY_TELEOP_DRIVE_ABILITY, 6);
        m_passedAz = json.optInt(KEY_TELEOP_PASS_AZ, 3);
        m_passedNz = json.optInt(KEY_TELEOP_PASS_NZ, 3);
        m_teleopPhoto = json.optInt(KEY_TELEOP_PHOTO, 0);

        m_diedValue = json.optInt(KEY_DIED, 0);
        m_startClimb = json.optInt(KEY_START_CLIMB, 0);
        m_endgameClimbLevel = json.optInt(KEY_CLIMB_LEVEL, 0);
        //noinspection GrazieInspectionRunner
        m_endgameClimbPos = json.optInt(KEY_ENDGAME_CLIMB_POS, 0);
        m_comment = json.optString(KEY_COMMENTS, "");

        m_shovelFuel = json.optBoolean(KEY_OTHER1, false);
        m_other2 = json.optString(KEY_OTHER2, "0");
        m_other3 = json.optString(KEY_OTHER3, "0");
        m_other4 = json.optString(KEY_OTHER4, "0");
    }

    /**
     * Serializes this MatchData record to a {@link JSONObject}.
     *
     * @return the serialized JSONObject
     * @throws JSONException if JSON creation fails
     */
    public JSONObject toJSON()
            throws JSONException
    {
        JSONObject json = new JSONObject();

        json.put(KEY_MATCH_ID, m_matchID);
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        json.put(KEY_TIMESTAMP, isoFormat.format(m_timestamp));

        json.put(KEY_VERSION, m_version);
        json.put(KEY_EVENT_CODE, m_eventCode);
        json.put(KEY_MATCH_NUMBER, m_matchNumber);
        json.put(KEY_TEAM_NUMBER, m_teamNumber);
        json.put(KEY_TEAM_ALIAS, m_teamAlias);
        json.put(KEY_SCOUT_NAME, m_scoutName);

        json.put(KEY_AUTON_PRELOAD, m_autonPreload);
        json.put(KEY_AUTON_PRELOAD_ACC_RATE, m_autonPreloadAccRate);
        json.put(KEY_AUTON_HOPPER, m_autonHopper);
        json.put(KEY_AUTON_ACC_RATE, m_autonAccuracyRate);
        json.put(KEY_AUTON_AZ, m_autonAz);
        json.put(KEY_AUTON_DEPOT, m_autonDepot);
        json.put(KEY_AUTON_OUTPOST, m_autonOutpost);
        json.put(KEY_AUTON_NZ, m_autonNz);
        json.put(KEY_AUTON_CLIMB, m_autonClimb);

        json.put(KEY_TELEOP_HOPPERS_USED, m_hoppersUsed);
        json.put(KEY_TELEOP_ACC_RATE, m_accuracyRate);
        json.put(KEY_TELEOP_INTAKE_SHOOT, m_intakeAndShoot);
        json.put(KEY_TELEOP_PASSING_RATE, m_passingRate);
        json.put(KEY_TELEOP_DEFENSE_RATE, m_defenseRate);
        json.put(KEY_TELEOP_DRIVE_ABILITY, m_drivingAbility);
        json.put(KEY_TELEOP_PASS_AZ, m_passedAz);
        json.put(KEY_TELEOP_PASS_NZ, m_passedNz);
        json.put(KEY_TELEOP_PHOTO, m_teleopPhoto);

        json.put(KEY_START_CLIMB, m_startClimb);
        json.put(KEY_CLIMB_LEVEL, m_endgameClimbLevel);
        //noinspection GrazieInspectionRunner
        json.put(KEY_ENDGAME_CLIMB_POS, m_endgameClimbPos);
        json.put(KEY_DIED, m_diedValue);
        json.put(KEY_COMMENTS, m_comment);

        json.put(KEY_OTHER1, m_shovelFuel);
        json.put(KEY_OTHER2, m_other2);
        json.put(KEY_OTHER3, m_other3);
        json.put(KEY_OTHER4, m_other4);

        return json;
    }

    /**
     * Sets the timestamp for when this match was scouted.
     *
     * @param d the date and time
     */
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public void setMatchID(String id)
    {
        m_matchID = id;
    }

    /**
     * Returns the version of the match data format.
     *
     * @return the version number
     */
    @SuppressWarnings("unused")
    public double getVersion()
    {
        return m_version;
    }

    /**
     * Sets the version of the match data format.
     *
     * @param version the version number
     */
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public String getTeamAlias()
    {
        return m_teamAlias != null ? m_teamAlias : "";
    }

    /**
     * Sets the name of the scout who recorded this match.
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
        m_scoutName = name.trim();
    }

    /**
     * Returns the name of the scout who recorded this match.
     *
     * @return the scout's name
     */
    public String getScoutName()
    {
        return m_scoutName;
    }

    /**
     * Sets the number of hoppers used in the autonomous stage.
     *
     * @param val the hopper count
     */
    public void setAutonHopper(int val)
    {
        m_autonHopper = val;
    }

    /**
     * Returns the number of hoppers used in the autonomous stage.
     *
     * @return the hopper count
     */
    public int getAutonHopper()
    {
        return m_autonHopper;
    }

    /**
     * Returns whether fuel was preloaded in the autonomous stage.
     *
     * @return true if fuel was preloaded
     */
    public boolean isAutonPreload()
    {
        return m_autonPreload;
    }

    /**
     * Sets whether fuel was preloaded in the autonomous stage.
     *
     * @param val true if fuel was preloaded
     */
    public void setAutonPreload(boolean val)
    {
        m_autonPreload = val;
    }

    /**
     * Returns whether the alliance zone was used in the autonomous stage.
     *
     * @return true if the alliance zone was used
     */
    public boolean isAutonAz()
    {
        return m_autonAz;
    }

    /**
     * Sets whether the alliance zone was used in the autonomous stage.
     *
     * @param val true if the alliance zone was used
     */
    public void setAutonAz(boolean val)
    {
        m_autonAz = val;
    }

    /**
     * Returns whether the depot was used in the autonomous stage.
     *
     * @return true if the depot was used
     */
    public boolean isAutonDepot()
    {
        return m_autonDepot;
    }

    /**
     * Sets whether the depot was used in the autonomous stage.
     *
     * @param val true if the depot was used
     */
    public void setAutonDepot(boolean val)
    {
        m_autonDepot = val;
    }

    /**
     * Returns whether the outpost was used in the autonomous stage.
     *
     * @return true if the outpost was used
     */
    public boolean isAutonOutpost()
    {
        return m_autonOutpost;
    }

    /**
     * Sets whether the outpost was used in the autonomous stage.
     *
     * @param val true if the outpost was used
     */
    public void setAutonOutpost(boolean val)
    {
        m_autonOutpost = val;
    }

    /**
     * Returns whether the neutral zone was used in the autonomous stage.
     *
     * @return true if the neutral zone was used
     */
    public boolean isAutonNz()
    {
        return m_autonNz;
    }

    /**
     * Sets whether the neutral zone was used in the autonomous stage.
     *
     * @param val true if the neutral zone was used
     */
    public void setAutonNz(boolean val)
    {
        m_autonNz = val;
    }

    /**
     * Sets the autonomous accuracy rate index.
     *
     * @param val the accuracy rate index
     */
    public void setAutonAccuracyRate(int val)
    {
        m_autonAccuracyRate = val;
    }

    /**
     * Returns the autonomous accuracy rate index.
     *
     * @return the accuracy rate index
     */
    public int getAutonAccuracyRate()
    {
        return m_autonAccuracyRate;
    }

    /**
     * Sets the autonomous preload accuracy level index.
     *
     * @param val the accuracy level index
     */
    public void setPreloadAccuracyLevel(int val)
    {
        m_autonPreloadAccRate = val;
    }

    /**
     * Returns the autonomous preload accuracy level index.
     *
     * @return the accuracy level index
     */
    public int getPreloadAccuracyLevel()
    {
        return m_autonPreloadAccRate;
    }

    /**
     * Sets the autonomous climb position index.
     *
     * @param val the climb position index
     */
    public void setAutonClimb(int val)
    {
        m_autonClimb = val;
    }

    /**
     * Returns the autonomous climb position index.
     *
     * @return the climb position index
     */
    public int getAutonClimb()
    {
        return m_autonClimb;
    }

    /**
     * Sets the number of hoppers used in the teleoperated stage.
     *
     * @param val the hopper count
     */
    public void setHoppersUsed(int val)
    {
        m_hoppersUsed = val;
    }

    /**
     * Returns the number of hoppers used in the teleoperated stage.
     *
     * @return the hopper count
     */
    public int getHoppersUsed()
    {
        return m_hoppersUsed;
    }

    /**
     * Sets the teleoperated accuracy rate index.
     *
     * @param val the accuracy rate index
     */
    public void setAccuracyRate(int val)
    {
        m_accuracyRate = val;
    }

    /**
     * Returns the teleoperated accuracy rate index.
     *
     * @return the accuracy rate index
     */
    public int getAccuracyRate()
    {
        return m_accuracyRate;
    }

    /**
     * Sets whether intake and shooting were performed simultaneously in the teleoperated stage.
     *
     * @param val true if both were performed simultaneously
     */
    public void setIntakeAndShoot(boolean val)
    {
        m_intakeAndShoot = val;
    }

    /**
     * Returns whether intake and shooting were performed simultaneously in the teleoperated stage.
     *
     * @return true if both were performed simultaneously
     */
    public boolean getIntakeAndShoot()
    {
        return m_intakeAndShoot;
    }

    /**
     * Sets whether herding fuel was performed in the teleoperated stage.
     *
     * @param val true if herding fuel was performed
     */
    public void setShovelFuel(boolean val)
    {
        m_shovelFuel = val;
    }

    /**
     * Returns whether herding fuel was performed in the teleoperated stage.
     *
     * @return true if herding fuel was performed
     */
    public boolean getShovelFuel()
    {
        return m_shovelFuel;
    }

    /**
     * Sets the passing effectiveness rate index.
     *
     * @param val the passing rate index
     */
    public void setPassingRate(int val)
    {
        m_passingRate = val;
    }

    /**
     * Returns the passing effectiveness rate index.
     *
     * @return the passing rate index
     */
    public int getPassingEffectivenessRate()
    {
        return m_passingRate;
    }

    /**
     * Sets the teleoperated defense rate index.
     *
     * @param val the defense rate index
     */
    public void setDefenseRate(int val)
    {
        m_defenseRate = val;
    }

    /**
     * Returns the teleoperated defense rate index.
     *
     * @return the defense rate index
     */
    public int getDefenseRate()
    {
        return m_defenseRate;
    }

    /**
     * Sets the driving ability index.
     *
     * @param val the driving ability index
     */
    public void setDriveAbility(int val)
    {
        m_drivingAbility = val;
    }

    /**
     * Returns the driving ability index.
     *
     * @return the driving ability index
     */
    public int getDriverAbility()
    {
        return m_drivingAbility;
    }

    /**
     * Sets the selection index for neutral zone passing.
     *
     * @param val the selection index
     */
    public void setPassNeutralZone(int val)
    {
        m_passedNz = val;
    }

    /**
     * Returns the selection index for neutral zone passing.
     *
     * @return the selection index
     */
    public int getPassNeutralZone()
    {
        return m_passedNz;
    }

    /**
     * Sets the selection index for alliance zone passing.
     *
     * @param val the selection index
     */
    public void setPassAllianceZone(int val)
    {
        m_passedAz = val;
    }

    /**
     * Returns the selection index for alliance zone passing.
     *
     * @return the selection index
     */
    public int getPassAllianceZone()
    {
        return m_passedAz;
    }

    /**
     * Sets the identifier for the teleoperated stage placeholder photo.
     *
     * @param val the photo identifier
     */
    public void setTeleopPhoto(int val)
    {
        m_teleopPhoto = val;
    }

    /**
     * Returns the identifier for the teleoperated stage placeholder photo.
     *
     * @return the photo identifier
     */
    public int getTeleopPhoto()
    {
        return m_teleopPhoto;
    }

    /**
     * Sets the died value index, indicating when the robot became disabled.
     *
     * @param val the died value index
     */
    public void setDiedValue(int val)
    {
        m_diedValue = val;
    }

    /**
     * Returns the died value index.
     *
     * @return the died value index
     */
    public int getDiedValue()
    {
        return m_diedValue;
    }

    /**
     * Sets the start climb time index.
     *
     * @param val the start climb index
     */
    public void setStartClimb(int val)
    {
        m_startClimb = val;
    }

    /**
     * Returns the start climb time index.
     *
     * @return the start climb index
     */
    public int getStartClimb()
    {
        return m_startClimb;
    }

    /**
     * Sets the endgame climb level index.
     *
     * @param val the climb level index
     */
    public void setEndgameClimbLevel(int val)
    {
        m_endgameClimbLevel = val;
    }

    /**
     * Returns the endgame climb level index.
     *
     * @return the climb level index
     */
    public int getEndgameClimbLevel()
    {
        return m_endgameClimbLevel;
    }

    /**
     * Sets the endgame climb position index.
     *
     * @param val the climb position index
     */
    public void setEndgameClimbPos(int val)
    {
        m_endgameClimbPos = val;
    }

    /**
     * Returns the endgame climb position index.
     *
     * @return the climb position index
     */
    public int getEndgameClimbPos()
    {
        return m_endgameClimbPos;
    }

    /**
     * Sets the additional comments for the match.
     *
     * @param comment the comment string
     */
    public void setComment(String comment)
    {
        m_comment = comment;
    }

    /**
     * Returns the additional comments for the match.
     *
     * @return the comment string
     */
    public String getComment()
    {
        return m_comment != null ? m_comment : "";
    }

    /**
     * Returns the secondary "other" field value.
     *
     * @return the other2 string
     */
    @SuppressWarnings("unused")
    public String getOther2()
    {
        return m_other2;
    }

    /**
     * Sets the secondary "other" field value.
     *
     * @param value the other2 string
     */
    @SuppressWarnings("unused")
    public void setOther2(String value)
    {
        m_other2 = value;
    }

    /**
     * Returns the tertiary "other" field value.
     *
     * @return the other3 string
     */
    @SuppressWarnings("unused")
    public String getOther3()
    {
        return m_other3;
    }

    /**
     * Sets the tertiary "other" field value.
     *
     * @param value the other3 string
     */
    @SuppressWarnings("unused")
    public void setOther3(String value)
    {
        m_other3 = value;
    }

    /**
     * Returns the quaternary "other" field value.
     *
     * @return the other4 string
     */
    @SuppressWarnings("unused")
    public String getOther4()
    {
        return m_other4;
    }

    /**
     * Sets the quaternary "other" field value.
     *
     * @param value the other4 string
     */
    @SuppressWarnings("unused")
    public void setOther4(String value)
    {
        m_other4 = value;
    }

    /**
     * Validates the match data entries for consistency and completeness.
     *
     * @return a validation message string detailing any errors, or an empty string if all entries are valid
     */
    public String validateEntries()
    {
        return validateAuton() +
                validateTeleop() +
                validateEndgame();
    }

    /**
     * Validates the autonomous stage entries.
     *
     * @return a validation message string, or empty if valid
     */
    private String validateAuton()
    {
        if (m_autonHopper > MAX_AUTON_HOPPERS)
        {
            return "Auton: Hopper score exceeds maximum (" + MAX_AUTON_HOPPERS + ")!\n";
        }
        return "";
    }

    /**
     * Validates the teleoperated stage entries.
     *
     * @return a validation message string, or empty if valid
     */
    private String validateTeleop()
    {
        StringBuilder msg = new StringBuilder();
        // Teleop Hopper validation
        if (m_hoppersUsed > MAX_TELEOP_HOPPERS)
        {
            msg.append("Teleop: Hopper score exceeds maximum (").append(MAX_TELEOP_HOPPERS).append(")!\n");
        }

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

        // Driver ability validation
        if (m_drivingAbility == 6)
        {
            msg.append("\nTeleop: Driver ability not set!\n");
        }
        return msg.toString();
    }

    /**
     * Validates the endgame stage entries.
     *
     * @return a validation message string, or empty if valid
     */
    private String validateEndgame()
    {
        // Climb selections validation
        if ((m_startClimb == 0 && (m_endgameClimbLevel != 0 || m_endgameClimbPos != 0)) ||
                (m_endgameClimbLevel == 0 && m_startClimb != 0) ||
                (m_endgameClimbPos == 0 && m_startClimb != 0))
        {
            return "\nEndgame: Start Climb, Climb Level and Climb Position settings don't match!\n";
        }
        return "";
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

        // Using a list to manage values ensures easy addition/removal and avoids format string errors.
        Object[] values = {
                m_version,
                m_eventCode,
                m_matchNumber,
                m_teamNumber,
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
        };

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++)
        {
            sb.append(values[i].toString());
            if (i < values.length - 1)
            {
                sb.append("\t");
            }
        }
        return sb.toString();
    }

    /**
     * Encodes the match data into a JSON string for QR code generation.
     *
     * @return the JSON encoded string
     */
    @SuppressWarnings("unused")
    public String encodeToJSON()
    {
        String jsonString = "";

        m_teamAlias = (m_teamAlias == null || m_teamAlias.isEmpty()) ? "-" : m_teamAlias;
        m_comment = (m_comment == null || m_comment.trim().isEmpty()) ? "-" : m_comment.trim();

        try
        {
            jsonString = toJSON().toString();
            Log.d(TAG, "JSON Output: " + jsonString);
        }
        catch (JSONException e)
        {
            Log.e(TAG, "Error serializing MatchData to JSON", e);
        }

        return jsonString;
    }
}
