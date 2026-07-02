package com.frc2135.android.frc_scout;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Data model for a single scouted match.
 * Handles representation of match data and TSV encoding for QR codes.
 */
public class MatchData {
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
    public static String stripTeamNumPrefix(String teamName) {
        if (teamName == null) {
            return "";
        }
        return teamName.replaceAll("^\\D+", "");
    }

    /**
     * Default constructor for creating a new match data record.
     */
    public MatchData() {
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

    public void setTimestamp(Date d) {
        m_timestamp = d;
    }

    public Date getTimestamp() {
        return m_timestamp;
    }

    public String getMatchID() {
        return m_matchID;
    }

    public void setMatchID(String id) {
        m_matchID = id;
    }

    public double getVersion() {
        return m_version;
    }

    public void setVersion(double version) {
        m_version = version;
    }

    public void setEventCode(String code) {
        m_eventCode = code;
    }

    public String getEventCode() {
        return m_eventCode;
    }

    public void setMatchNumber(String num) {
        m_matchNumber = num;
    }

    public String getMatchNumber() {
        return m_matchNumber;
    }

    public void setTeamNumber(String num) {
        m_teamNumber = num;
    }

    public String getTeamNumber() {
        return m_teamNumber;
    }

    public void setTeamAlias(String alias) {
        m_teamAlias = alias;
    }

    public String getTeamAlias() {
        return m_teamAlias != null ? m_teamAlias : "";
    }

    public void setScoutName(String name) {
        if (name == null || name.trim().isEmpty()) {
            m_scoutName = "";
            return;
        }
        String trimmed = name.trim();
        if (trimmed.length() == 1) {
            m_scoutName = trimmed.toUpperCase();
        } else {
            m_scoutName = trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
        }
    }

    public String getScoutName() {
        return m_scoutName;
    }

    // Auton accessors
    public void setAutonHopper(int val) {
        m_autonHopper = val;
    }

    public int getAutonHopper() {
        return m_autonHopper;
    }

    public boolean getAutonPreload() {
        return m_autonPreload;
    }

    public void setAutonPreload(boolean val) {
        m_autonPreload = val;
    }

    public boolean getAutonAzCheckbox() {
        return m_autonAz;
    }

    public void setAutonAzCheckbox(boolean val) {
        m_autonAz = val;
    }

    public boolean getAutonDepotCheckbox() {
        return m_autonDepot;
    }

    public void setAutonDepotCheckbox(boolean val) {
        m_autonDepot = val;
    }

    public boolean getAutonOutpostCheckbox() {
        return m_autonOutpost;
    }

    public void setAutonOutpostCheckbox(boolean val) {
        m_autonOutpost = val;
    }

    public boolean getAutonNzCheckbox() {
        return m_autonNz;
    }

    public void setAutonNzCheckbox(boolean val) {
        m_autonNz = val;
    }

    public void setAutonAccuracyRate(int val) {
        m_autonAccuracyRate = val;
    }

    public int getAutonAccuracyRate() {
        return m_autonAccuracyRate;
    }

    public void setPreloadAccuracyLevel(int val) {
        m_autonPreloadAccRate = val;
    }

    public int getPreloadAccuracyLevel() {
        return m_autonPreloadAccRate;
    }

    public void setAutonClimb(int val) {
        m_autonClimb = val;
    }

    public int getAutonClimb() {
        return m_autonClimb;
    }

    // Teleop accessors
    public void setHoppersUsed(int val) {
        m_hoppersUsed = val;
    }

    public int getHoppersUsed() {
        return m_hoppersUsed;
    }

    public void setAccuracyRate(int val) {
        m_accuracyRate = val;
    }

    public int getAccuracyRate() {
        return m_accuracyRate;
    }

    public void setIntakeAndShoot(boolean val) {
        m_intakeAndShoot = val;
    }

    public boolean getIntakeAndShoot() {
        return m_intakeAndShoot;
    }

    public void setShovelFuel(boolean val) {
        m_shovelFuel = val;
    }

    public boolean getShovelFuel() {
        return m_shovelFuel;
    }

    public void setPassingRate(int val) {
        m_passingRate = val;
    }

    public int getPassingEffectivenessRate() {
        return m_passingRate;
    }

    public void setDefenseRate(int val) {
        m_defenseRate = val;
    }

    public int getDefenseRate() {
        return m_defenseRate;
    }

    public void setDriveAbility(int val) {
        m_drivingAbility = val;
    }

    public int getDriverAbility() {
        return m_drivingAbility;
    }

    public void setPassNeutralZone(int val) {
        m_passedNz = val;
    }

    public int getPassNeutralZone() {
        return m_passedNz;
    }

    public void setPassAllianceZone(int val) {
        m_passedAz = val;
    }

    public int getPassAllianceZone() {
        return m_passedAz;
    }

    public void setTeleopPhoto(int val) {
        m_teleopPhoto = val;
    }

    public int getTeleopPhoto() {
        return m_teleopPhoto;
    }

    // Endgame accessors
    public void setDiedValue(int val) {
        m_diedValue = val;
    }

    public int getDiedValue() {
        return m_diedValue;
    }

    public void setStartClimb(int val) {
        m_startClimb = val;
    }

    public int getStartClimb() {
        return m_startClimb;
    }

    public void setEndgameClimbLevel(int val) {
        m_endgameClimbLevel = val;
    }

    public int getEndgameClimbLevel() {
        return m_endgameClimbLevel;
    }

    public void setEndgameClimbPos(int val) {
        m_endgameClimbPos = val;
    }

    public int getEndgameClimbPos() {
        return m_endgameClimbPos;
    }

    public void setComment(String comment) {
        m_comment = comment;
    }

    public String getComment() {
        return m_comment != null ? m_comment : "";
    }

    public String getOther2() {
        return m_other2;
    }

    public void setOther2(String value) {
        m_other2 = value;
    }

    public String getOther3() {
        return m_other3;
    }

    public void setOther3(String value) {
        m_other3 = value;
    }

    public String getOther4() {
        return m_other4;
    }

    public void setOther4(String value) {
        m_other4 = value;
    }

    public String getMatchFileName() {
        return m_matchID + ".json";
    }

    /**
     * Encodes the match data into a Tab-Separated Values (TSV) string for QR code generation.
     *
     * @return the TSV encoded string
     */
    public String encodeToTSV() {
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
