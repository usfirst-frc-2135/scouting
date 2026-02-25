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

/** @noinspection DataFlowIssue*/
public class MatchData
{
    private static final String TAG = "MatchData";

    // Keys used for reading/writing match JSON files.
    private static final String JSON_KEY_SCOUT_NAME = "scoutName";
    private static final String JSON_KEY_EVENT_CODE = "eventCode";
    private static final String JSON_KEY_TEAM_NUMBER = "teamNumber";
    private static final String JSON_KEY_TEAM_ALIAS = "teamAlias";
    private static final String JSON_KEY_MATCH_NUMBER = "matchNumber";

    // Auton data
    private static final String JSON_KEY_VERSION = "version";
    private static final String JSON_KEY_STARTING_POSITION = "startingPosition";
    private static final String JSON_KEY_PRELOAD = "preload";
    private static final String JSON_KEY_AUTON_AZ = "autonAz";
    private static final String JSON_KEY_AUTON_DEPOT = "autonDepot";
    private static final String JSON_KEY_AUTON_OUTPOST = "autonOutpost";
    private static final String JSON_KEY_AUTON_NZ = "autonNz";
    private static final String JSON_KEY_AUTON_ACCURACY_RATE= "autonAccuracyRate";
    private static final String JSON_KEY_AUTON_HOPPER = "autonHopper";
    private static final String JSON_KEY_AUTON_PRELOAD_ACCURACY_RATE= "autonPreloadAccRate";
    private static final String JSON_KEY_AUTON_CLIMB= "autonClimb";

    // Teleop data
    private static final String JSON_KEY_HOPPERS_USED= "hoppersUsed";
    private static final String JSON_KEY_ACCURACY_RATE= "accuracyRate";
    private static final String JSON_KEY_INTAKE_AND_SHOOT= "intakeAndShoot";
    private static final String JSON_KEY_PASSING_RATE= "passingRate";
    private static final String JSON_KEY_DEFENSE_RATE = "defenseRate";
    private static final String JSON_KEY_DRIVE_ABILITY = "driveAbility";
    private static final String JSON_KEY_PASS_ALLIANCE_ZONE= "allianceZone";
    private static final String JSON_KEY_PASS_NEUTRAL_ZONE= "neutralZone";

    // Endgame data
    private static final String JSON_KEY_START_CLIMB = "startClimb";
    private static final String JSON_KEY_CLIMB_LEVEL = "climbLevel";
    private static final String JSON_KEY_ENDGAME_CLIMB_POS= "endgameClimbPos";

    private static final String JSON_KEY_DIED = "died";
    private static final String JSON_KEY_COMMENTS = "comments";
    private static final String JSON_KEY_OTHER1 = "other1";
    private static final String JSON_KEY_OTHER2 = "other2";
    private static final String JSON_KEY_OTHER3 = "other3";
    private static final String JSON_KEY_OTHER4 = "other4";
    private static final String JSON_KEY_TIMESTAMP = "timestamp";
    private static final String JSON_KEY_MATCH_ID = "matchId";

    // Data members 
    // Auton data
    private double m_version;
    private boolean m_autonPreload;
    private int m_autonPreloadAccRate;
    private int m_autonHopper;
    private int m_autonAccuracyRate;
    private boolean m_autonAz;
    private boolean m_autonDepot;
    private boolean m_autonOutpost;
    private boolean m_autonNz;
    private int m_autonClimb;

    // Teleop data
    private int m_hoppersUsed;
    private int m_accuracyRate;

    private boolean m_intakeAndShoot;
    private int m_passingRate;
    private int m_defenseRate;
    private int m_drivingability;
    private int m_passedAz;
    private int m_passedNz;
    private int m_teleopPhoto;

    //Endgame
    private int m_diedValue;
    private int m_endgameClimbPos;  // climb position
    private int m_startClimb;
    private int m_endgameClimbLevel;

    private String m_comment;
    private String m_other1;
    private String m_other2;
    private String m_other3;
    private String m_other4;
    private String m_name;
    private String m_teamNumber;
    private String m_teamAlias;
    private String m_matchNumber;
    private final String m_matchID;
    private String m_eventCode;
    private Date m_timestamp;


    // Utility to strip off "frc" prefix to team number.
    static public String stripTeamNumPrefix(String teamName)
    {
        StringBuilder newTeamName = new StringBuilder();
        for (int i = 0; i < teamName.length(); i++)
        {
            if (i < 3 && !Character.isDigit(teamName.charAt(i)))
                continue;  // skip the first 3 chars that are not digits
            newTeamName.append(teamName.charAt(i));
        }
        return newTeamName.toString();
    }

    ////////////////////////  Default constructor   //////////////////////////////
    public MatchData(Context context) throws IOException, JSONException
    {
        m_name = "";
        m_teamNumber = "";
        m_teamAlias = "";
        m_matchNumber = "";

        // Auton data
        setAutonPreload(false);
        setPreloadAccuracyLevel(0);
        setAutonHopper(0);
        setAutonAccuracyRate(0);

        setAutonAzCheckbox(false);
        setAutonDepotCheckbox(false);
        setAutonOutpostCheckbox(false);
        setAutonNzCheckbox(false);

        setAutonClimb(0);

        // Teleop data
        setHoppersUsed(0);
        setAccuracyRate(0);
        setIntakeAndShoot(false);

        setPassingRate(0);
        setDefenseRate(0);
        setDriveAbility(0);
        setPassNeutralZone(0);
        setPassAllianceZone(0);

        setTeleopPhoto(0);

        // Endgame data
        setStartClimb(0);
        setEndgameClimbLevel(0);
        setEndgameClimbPos(0);

        setDiedValue(0);
        setComment("");
        setTimestamp(Calendar.getInstance().getTime());

        m_matchID = UUID.randomUUID() + "";

        m_eventCode = CurrentCompetition.get(context).getEventCode();
        Log.d(TAG, "Default constructor m_eventCode set to " + m_eventCode);
        m_other1 = "0";
        m_other2 = "0";
        m_other3 = "0";
        m_other4 = "0";
    }

    //////////////////////// constructor from JSON file  //////////////////////////////
    public MatchData(JSONObject json) throws JSONException
    {
        Log.d(TAG, "MatchData being created using json data");

        setName(json.getString(JSON_KEY_SCOUT_NAME));
        setEventCode(json.getString(JSON_KEY_EVENT_CODE));
        setTeamNumber(json.getString(JSON_KEY_TEAM_NUMBER));
        setMatchNumber(json.getString(JSON_KEY_MATCH_NUMBER));

        // Auton data
        setAutonPreload(json.getBoolean(JSON_KEY_PRELOAD));
        setPreloadAccuracyLevel(json.getInt(JSON_KEY_AUTON_PRELOAD_ACCURACY_RATE));
        setAutonHopper(json.getInt(JSON_KEY_AUTON_HOPPER));
        setAutonAccuracyRate(json.getInt(JSON_KEY_AUTON_ACCURACY_RATE));

        setAutonAzCheckbox(json.getBoolean(JSON_KEY_AUTON_AZ));
        setAutonDepotCheckbox(json.getBoolean(JSON_KEY_AUTON_DEPOT));
        setAutonOutpostCheckbox(json.getBoolean(JSON_KEY_AUTON_OUTPOST));
        setAutonNzCheckbox(json.getBoolean(JSON_KEY_AUTON_NZ));
        setAutonClimb(json.getInt(JSON_KEY_AUTON_CLIMB));

        // Teleop data
        setHoppersUsed(json.getInt(JSON_KEY_HOPPERS_USED));
        setAccuracyRate(json.getInt(JSON_KEY_ACCURACY_RATE));

        setIntakeAndShoot(json.getBoolean(JSON_KEY_INTAKE_AND_SHOOT));
        setPassingRate(json.getInt(JSON_KEY_PASSING_RATE));
        setDefenseRate(json.getInt(JSON_KEY_DEFENSE_RATE));
        setDriveAbility(json.getInt(JSON_KEY_DRIVE_ABILITY));
        setPassAllianceZone(json.getInt(JSON_KEY_PASS_ALLIANCE_ZONE));
        setPassNeutralZone(json.getInt(JSON_KEY_PASS_NEUTRAL_ZONE));

        //Endgame data
        setDiedValue(json.getInt(JSON_KEY_DIED));
        setStartClimb(json.getInt(JSON_KEY_START_CLIMB));
        setEndgameClimbLevel(json.getInt(JSON_KEY_CLIMB_LEVEL));
        setEndgameClimbPos(json.getInt(JSON_KEY_ENDGAME_CLIMB_POS));

        setComment(json.getString(JSON_KEY_COMMENTS));

        String dateStr = json.getString(JSON_KEY_TIMESTAMP);
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy", Locale.US);
        Date date = null;
        try
        {
            date = dt.parse(dateStr);
        } catch (Exception err)
        {
            Log.d("timestamp Date string error: ",err.getMessage());
        }
        setTimestamp(date);

        m_matchID = json.getString(JSON_KEY_MATCH_ID);
    }

    ////////////  m_matchID   /////////////////////
    public String getMatchID()
    {
        return m_matchID;
    }

    ////////////  m_name   /////////////////////
    public void setName(String n)
    {
        m_name = n.substring(0, 1).toUpperCase() + n.substring(1).toLowerCase();
    }

    public String getName()
    {
        return m_name;
    }

    ////////////  m_eventCode   /////////////////////
    public void setEventCode(String c)
    {
        m_eventCode = c;
    }

    public String getEventCode()
    {
        return m_eventCode;
    }

    ////////////  m_teamNumber   /////////////////////
    public void setTeamNumber(String n)
    {
        m_teamNumber = n;
    }

    public String getTeamNumber()
    {
        return m_teamNumber;
    }

    ////////////  m_teamAlias   /////////////////////
    public void setTeamAlias(String alias)
    {
        m_teamAlias = alias;
    }

    public String getTeamAlias()
    {
        return m_teamAlias;
    }

    ////////////  m_matchNumber   /////////////////////
    public void setMatchNumber(String n)
    {
        m_matchNumber = n;
    }

    public String getMatchNumber()
    {
        return m_matchNumber;
    }



    public void setAutonHopper(int numCoral) { m_autonHopper = numCoral; }

    public int getAutonHopper()
    {
        return m_autonHopper;
    }

    public boolean getAutonPreload()
    {
        return m_autonPreload;
    }

    public void setAutonPreload(boolean preload)
    {
        m_autonPreload = preload;
    }

    public boolean getAutonAzCheckbox()
    {
        return m_autonAz;
    }

    public void setAutonAzCheckbox(boolean autonAz)
    {
        m_autonAz = autonAz;
    }

    public boolean getAutonDepotCheckbox()
    {
        return m_autonDepot;
    }

    public void setAutonDepotCheckbox(boolean autonDepot)
    {
        m_autonDepot = autonDepot;
    }

    public boolean getAutonOutpostCheckbox()
    {
        return m_autonOutpost;
    }

    public void setAutonOutpostCheckbox(boolean autonOutpost)
    {
        m_autonOutpost = autonOutpost;
    }

    public boolean getAutonNzCheckbox()
    {
        return m_autonNz;
    }

    public void setAutonNzCheckbox(boolean autonNz)
    {
        m_autonNz = autonNz;
    }

    ///////////Teleop/////////

    public void setHoppersUsed(int hoppersUsed)
    {
        m_hoppersUsed = hoppersUsed;
    }

    public int getHoppersUsed()
    {
        return m_hoppersUsed;
    }

    public void setAccuracyRate(int accuracyRate)
    {
        m_accuracyRate = accuracyRate;
    }

    public int getAccuracyRate()
    {
        return m_accuracyRate;
    }

    public void setAutonAccuracyRate(int autonAccuracyRate)
    {
        m_autonAccuracyRate = autonAccuracyRate;
    }

    public int getAutonAccuracyRate()
    {
        return m_autonAccuracyRate;
    }

    public void setPreloadAccuracyLevel(int autonPreloadAccRate)
    {
        m_autonPreloadAccRate = autonPreloadAccRate;
    }

    public int getPreloadAccuracyLevel()
    {
        return m_autonPreloadAccRate;
    }

    public void setAutonClimb(int autonClimb)
    {
        m_autonClimb = autonClimb;
    }

    public int getAutonClimb()
    {
        return m_autonClimb;
    }

    public void setEndgameClimbPos(int endgameClimbPos)
    {
        m_endgameClimbPos = endgameClimbPos;
    }

    public int getEndgameClimbPos()
    {
        return m_endgameClimbPos;
    }

    public void setIntakeAndShoot(boolean intakeAndShoot)
    {
        m_intakeAndShoot = intakeAndShoot;
    }

    public boolean getIntakeAndShoot()
    {
        return m_intakeAndShoot;
    }

    public void setPassingRate(int passingRate)
    {
        m_passingRate = passingRate;
    }

    public int getPassingEffectivenessrate()
    {
        return m_passingRate;
    }

    public void setDefenseRate(int def)
    {
        m_defenseRate = def;
    }

    public int getDefenseRate()
    {
        return m_defenseRate;
    }

    public void setDriveAbility(int driveAbility)
    {
        m_drivingability = driveAbility;
    }

    public int getDriveAbility()
    {
        return m_drivingability;
    }

    public void setEndgameClimbLevel(int climbLevel)
    {
        m_endgameClimbLevel = climbLevel;
    }

    public int getEndgameClimbLevel()
    {
        return m_endgameClimbLevel;
    }

    public void setPassNeutralZone(int neutralZone)
    {
        m_passedNz = neutralZone;
    }

    public int getPassNeutralZone()
    {
        return m_passedNz;
    }

    public void setPassAllianceZone(int allianceZone)
    {
        m_passedAz = allianceZone;
    }

    public int getPassAllianceZone()
    {
        return m_passedAz;
    }
    
    public void setTeleopPhoto(int teleopPhoto)
    {
        m_teleopPhoto = teleopPhoto;
    }

    public int getTeleopPhoto()
    {
        return m_teleopPhoto;
    }

    public void setDiedValue(int x)
    {
        m_diedValue = x;
    }

    public int getDiedValue()
    {
        return m_diedValue;
    }

    public void setStartClimb(int y)
    {
        m_startClimb = y;
    }

    public int getStartClimb()
    {
        return m_startClimb;
    }

    ////////////  m_comment   /////////////////////
    public void setComment(String comment)
    {
        m_comment = comment;
    }

    public String getComment()
    {
        return m_comment;
    }

    ////////////  m_timestamp   /////////////////////
    public void setTimestamp(Date d)
    {
        m_timestamp = d;
    }

    public Date getTimestamp()
    {
        return m_timestamp;
    }

    public String encodeToTSV()
    {
        // NOTE! THE ORDER IS IMPORTANT!
        // This is the data that goes into the QR code.

        String tsvStr = "";

        // For teamNumber, strip off 'frc' prefix.
        // Boolean values should be "0" or "1," not "true" or "false"

        //sets the version number
        m_version = 26.1;
        tsvStr += m_version + "\t";

        tsvStr += m_eventCode + "\t";
        tsvStr += m_matchNumber + "\t";

        tsvStr += stripTeamNumPrefix(m_teamNumber) + "\t";

        if (!m_teamAlias.equals(""))
            tsvStr += m_teamAlias + "\t";
        else tsvStr += "-" + "\t";

        tsvStr += m_name + "\t";   // Scout name

        tsvStr += m_diedValue + "\t";

        if (m_autonPreload)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_autonPreloadAccRate + "\t";

        tsvStr += m_autonHopper + "\t";

        tsvStr += m_autonAccuracyRate + "\t";

        if (m_autonAz)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_autonDepot)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_autonOutpost)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_autonNz)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_autonClimb + "\t";

        // Teleop data
        tsvStr += m_hoppersUsed + "\t";
        tsvStr += m_accuracyRate + "\t";

        if (m_intakeAndShoot)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_passingRate + "\t";

        tsvStr += m_defenseRate + "\t";

        tsvStr += m_drivingability + "\t";

        tsvStr += m_passedAz + "\t";
        tsvStr += m_passedNz + "\t";

        tsvStr += m_startClimb + "\t";
        tsvStr += m_endgameClimbLevel + "\t";
        tsvStr += m_endgameClimbPos + "\t";   // climb position

        if (!m_comment.equals(""))
            tsvStr += m_comment + "\t";
        else tsvStr += "-" + "\t";

        tsvStr += m_other1 + "\t";   // extra spot 1
        tsvStr += m_other2 + "\t";   // extra spot 2 
        tsvStr += m_other3 + "\t";   // extra spot 3 
        tsvStr += m_other4 + "\t";   // extra spot 4 

        Log.d(TAG, "MatchData encodeToTSV(): " + tsvStr);
        return tsvStr;
    }

    public JSONObject toJSON() throws JSONException
    {
        //This code uses the JSON class to convert the aspects of each match into data that can be saved to a file as JSON
        JSONObject json = new JSONObject();

        json.put("divider", ",");
        json.put("divider", ", \n");

        json.put("headings", "Competition, Team Number, Match Number, Version, Starting Pos, Preload, Auton Hopper, Auton Algae Net, Auton Algae Processor, Auton Coral Floor, Auton Coral Station, Auton Algae Floor, Teleop Hoppers Used, Accuracy Rate, Intake and Shoot, Neutral to Alliance Passing, Alliance to Alliance Passing, Passing Effectiveness Rate, Defense, Cage Climb, Start Climb, Died, Comments, Timestamp, MatchID \n");
        json.put(JSON_KEY_VERSION, m_version);
        json.put("divider", ",");
        json.put(JSON_KEY_EVENT_CODE, m_eventCode);
        json.put("divider", ",");
        json.put(JSON_KEY_MATCH_NUMBER, m_matchNumber);
        json.put("divider", ",");
        json.put(JSON_KEY_TEAM_NUMBER, m_teamNumber);
        json.put("divider", ",");
        json.put(JSON_KEY_TEAM_ALIAS, m_teamAlias);
        json.put("divider", ",");
        json.put(JSON_KEY_SCOUT_NAME, m_name);
        json.put("divider", ",");
        json.put(JSON_KEY_PRELOAD, m_autonPreload);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_PRELOAD_ACCURACY_RATE, m_autonPreloadAccRate);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_HOPPER, m_autonHopper);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_ACCURACY_RATE, m_autonAccuracyRate);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_AZ, m_autonAz);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_DEPOT, m_autonDepot);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_OUTPOST, m_autonOutpost);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_NZ, m_autonNz);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_CLIMB, m_autonClimb);
        json.put("divider", ",");
        json.put(JSON_KEY_HOPPERS_USED, m_hoppersUsed);
        json.put("divider", ",");
        json.put(JSON_KEY_ACCURACY_RATE, m_accuracyRate);
        json.put("divider", ",");
        json.put(JSON_KEY_INTAKE_AND_SHOOT, m_intakeAndShoot);
        json.put("divider", ",");
        json.put(JSON_KEY_PASSING_RATE, m_passingRate);
        json.put("divider", ",");
        json.put(JSON_KEY_DEFENSE_RATE, m_defenseRate);
        json.put("divider", ",");
        json.put(JSON_KEY_DRIVE_ABILITY, m_drivingability);
        json.put("divider", ",");
        json.put(JSON_KEY_PASS_ALLIANCE_ZONE, m_passedAz);
        json.put("divider", ",");
        json.put(JSON_KEY_PASS_NEUTRAL_ZONE, m_passedNz);
        json.put("divider", ",");
        json.put(JSON_KEY_START_CLIMB, m_startClimb);
        json.put("divider", ",");
        json.put(JSON_KEY_CLIMB_LEVEL, m_endgameClimbLevel);
        json.put("divider", ",");
        json.put(JSON_KEY_ENDGAME_CLIMB_POS, m_endgameClimbPos);
        json.put("divider", ",");
        json.put(JSON_KEY_DIED, m_diedValue);
        json.put("divider", ",");
        json.put(JSON_KEY_COMMENTS, m_comment);
        json.put("divider", ",");
        json.put(JSON_KEY_OTHER1, m_other1);   // Holders for extra data if ever needed
        json.put("divider", ",");
        json.put(JSON_KEY_OTHER2, m_other2);
        json.put("divider", ",");
        json.put(JSON_KEY_OTHER3, m_other3);
        json.put("divider", ",");
        json.put(JSON_KEY_OTHER4, m_other4);
        json.put("divider", ",");
        json.put(JSON_KEY_TIMESTAMP, m_timestamp);
        json.put("divider", ",");
        json.put(JSON_KEY_MATCH_ID, m_matchID);
        return json;
    }

    public String getMatchFileName()
    {
        return m_matchID + ".json";
    }
}

