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
    private static final String JSON_KEY_STARTING_POSITION = "startingPosition";
    private static final String JSON_KEY_PRELOAD = "preload";
    private static final String JSON_KEY_AUTON_AZ = "autonAz";
    private static final String JSON_KEY_AUTON_DEPOT = "autonDepot";
    private static final String JSON_KEY_AUTON_OUTPOST = "autonOutpost";
    private static final String JSON_KEY_AUTON_NZ = "autonNz";



    private static final String JSON_KEY_AUTON_HOPPER = "autonHopper";

    private static final String JSON_KEY_FLOOR_CORAL= "floorCoral";
    private static final String JSON_KEY_STATION_CORAL = "stationCoral";
    private static final String JSON_KEY_FLOOR_ALGAE= "floorAlgae";
    private static final String JSON_KEY_REEF_ALGAE = "reefAlgae";

    // Teleop data
    private static final String JSON_KEY_HOPPERS_USED= "hoppersUsed";
    private static final String JSON_KEY_ACCURACY_RATE= "accuracyRate";
    private static final String JSON_KEY_AUTON_ACCURACY_RATE= "autonAccuracyRate";
    private static final String JSON_KEY_AUTON_PRELOAD_ACCURACY_RATE= "autonPreloadAccuracyRate";

    private static final String JSON_KEY_AUTON_CLIMB= "autonClimb";
    private static final String JSON_KEY_ENDGAME_CLIMB= "endgameClimb";
    private static final String JSON_KEY_INTAKE_AND_SHOOT= "intakeAndShoot";
    private static final String JSON_KEY_NEUTRAL_TO_ALLIANCE_PASSING = "neutralToAlliancePassing";
    private static final String JSON_KEY_ALLIANCE_TO_ALLIANCE_PASSING = "allianceToAlliancePassing";
    private static final String JSON_KEY_PASSING_EFFECTIVENESS_RATE= "passingEffectivenessRate";
  /*  private static final String JSON_KEY_TELEOP_ALGAE_NET = "teleopAlgaeNet";
    private static final String JSON_KEY_TELEOP_ALGAE_PROCESSOR = "teleopAlgaeProcessor";

    private static final String JSON_KEY_TELEOP_CORAL_L1 = "teleopCoralL1";
    private static final String JSON_KEY_TELEOP_CORAL_L2 = "teleopCoralL2";
    private static final String JSON_KEY_TELEOP_CORAL_L3 = "teleopCoralL3";
    private static final String JSON_KEY_TELEOP_CORAL_L4 = "teleopCoralL4";*/

    private static final String JSON_KEY_PLAYED_DEFENSE = "playedDefense";

    private static final String JSON_KEY_CLIMB_LEVEL = "climbLevel";


    // Endgame data
    private static final String JSON_KEY_DIED = "died";
    private static final String JSON_KEY_START_CLIMB = "startClimb";

    private static final String JSON_KEY_COMMENTS = "comments";
    private static final String JSON_KEY_TIMESTAMP = "timestamp";
    private static final String JSON_KEY_MATCH_ID = "matchId";

    // Data members 
    // Auton data
    private int m_startingPos;
    private boolean m_autonPreload;
    private boolean m_autonAz;
    private boolean m_autonDepot;
    private boolean m_autonOutpost;
    private boolean m_autonNz;

    private int m_autonHopper;


    private boolean m_coralFloor;
    private boolean m_coralStation;
    private boolean m_algaeFloor;
    private boolean m_algaeReef;


    // Teleop data
    //private int m_teleopCoralL1;
    //private int m_teleopCoralL2;
    //private int m_teleopCoralL3;
    //private int m_teleopCoralL4;
    //private int m_teleopAlgaeNet;
    // private int m_teleopAlgaeProcessor;
    private int m_hoppersUsed;
    private int m_accuracyRate;
    private int m_autonAccuracyRate;
    private int m_autonPreloadAccuracyRate;

    private int m_autonClimb;
    private int m_endgameClimb;

    private boolean m_intakeAndShoot;
    private boolean m_neutralToAlliancePassing;
    private boolean m_allianceToAlliancePassing;
    private int m_passingEffectivenessRate;
    private int m_playedDefense;
    private int m_climbLevel;
    private int m_teleopPhoto;

    //endgame
    private int m_diedGroup;
    private int m_startClimb;

    private String m_comment;
    private boolean m_died;
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

        setAutonAzCheckbox(false);
        setAutonDepotCheckbox(false);
        setAutonOutpostCheckbox(false);
        setAutonNzCheckbox(false);

        setAutonHopper(0);

        setFloorCoral(false);
        setStationCoral(false);
        setFloorAlgae(false);
        setReefAlgae(false);

        // Teleop data
        setHoppersUsed(0);
        setAccuracyRate(0);
        setTeleopPhoto(0);

        setIntakeAndShoot(false);
        setNeutralToAlliancePassing(false);
        setAllianceToAlliancePassing(false);
        setPassingEffectivenessRate(0);

        //setTeleopAlgaeNet(0);
        //setTeleopAlgaeProcessor(0);

        //setTeleopCoralL1(0);
        //setTeleopCoralL2(0);
        //setTeleopCoralL3(0);
        //setTeleopCoralL4(0);

        setPlayedDefense(0);

        // Endgame data
        setTimeDied(0);
        setStartClimb(0);

        setDied(false);
        setComment("");
        setTimestamp(Calendar.getInstance().getTime());

        m_matchID = UUID.randomUUID() + "";

        m_eventCode = CurrentCompetition.get(context).getEventCode();
        Log.d(TAG, "Default constructor m_eventCode set to " + m_eventCode);
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
        //setStartingPosition(json.getInt(JSON_KEY_STARTING_POSITION));
        setAutonPreload(json.getBoolean(JSON_KEY_PRELOAD));

        setAutonAzCheckbox(json.getBoolean(JSON_KEY_AUTON_AZ));
        setAutonDepotCheckbox(json.getBoolean(JSON_KEY_AUTON_DEPOT));
        setAutonOutpostCheckbox(json.getBoolean(JSON_KEY_AUTON_OUTPOST));
        setAutonNzCheckbox(json.getBoolean(JSON_KEY_AUTON_NZ));

        setAutonAccuracyRate(json.getInt(JSON_KEY_AUTON_CLIMB));


        setAutonHopper(json.getInt(JSON_KEY_AUTON_HOPPER));

        setFloorCoral(json.getBoolean(JSON_KEY_FLOOR_CORAL));
        setStationCoral(json.getBoolean(JSON_KEY_STATION_CORAL));
        setFloorAlgae(json.getBoolean(JSON_KEY_FLOOR_ALGAE));
        setReefAlgae(json.getBoolean(JSON_KEY_REEF_ALGAE));

        // Teleop data
        setHoppersUsed(json.getInt(JSON_KEY_HOPPERS_USED));
        setAccuracyRate(json.getInt(JSON_KEY_ACCURACY_RATE));
        setAutonAccuracyRate(json.getInt(JSON_KEY_AUTON_ACCURACY_RATE));
        setPreloadAccuracyLevel(json.getInt(JSON_KEY_AUTON_PRELOAD_ACCURACY_RATE));

        setIntakeAndShoot(json.getBoolean(JSON_KEY_INTAKE_AND_SHOOT));
        setNeutralToAlliancePassing(json.getBoolean(JSON_KEY_NEUTRAL_TO_ALLIANCE_PASSING));
        setAllianceToAlliancePassing(json.getBoolean(JSON_KEY_ALLIANCE_TO_ALLIANCE_PASSING));
        setPassingEffectivenessRate(json.getInt(JSON_KEY_PASSING_EFFECTIVENESS_RATE));

        //Endgame data
        setAccuracyRate(json.getInt(JSON_KEY_CLIMB_LEVEL));

       /* setTeleopAlgaeNet(json.getInt(JSON_KEY_TELEOP_ALGAE_NET));
        setTeleopAlgaeProcessor(json.getInt(JSON_KEY_TELEOP_ALGAE_PROCESSOR));

        setTeleopCoralL1(json.getInt(JSON_KEY_TELEOP_CORAL_L1));
        setTeleopCoralL2(json.getInt(JSON_KEY_TELEOP_CORAL_L2));
        setTeleopCoralL3(json.getInt(JSON_KEY_TELEOP_CORAL_L3));
        setTeleopCoralL4(json.getInt(JSON_KEY_TELEOP_CORAL_L4));*/

        setPlayedDefense(json.getInt(JSON_KEY_PLAYED_DEFENSE));

        //endgame
        setTimeDied(json.getInt(JSON_KEY_DIED));
        setStartClimb(json.getInt(JSON_KEY_START_CLIMB));

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




    public void setFloorCoral(boolean floorCoral)
    {

        m_coralFloor = floorCoral;
    }

    public boolean getFloorCoral()
    {
        return m_coralFloor;
    }

    public void setFloorAlgae(boolean floorAlgae)
    {

        m_algaeFloor = floorAlgae;
    }

    public boolean getFloorAlgae()
    {
        return m_algaeFloor;
    }

    public void setReefAlgae(boolean reefAlgae)
    {
        m_algaeReef = reefAlgae;
    }

    public boolean getReefAlgae()
    {
        return m_algaeReef;
    }

    public void setStationCoral(boolean stationCoral)
    {
        m_coralStation = stationCoral;
    }

    public boolean getStationCoral()
    {
        return m_coralStation;
    }
    ///////////Teleop/////////
   /* public void setTeleopCoralL1(int numCoral)
    {
        m_teleopCoralL1 = numCoral;
    }

    public int getTeleopCoralL1()
    {
        return m_teleopCoralL1;
    }

    public void setTeleopCoralL2(int numCoral)
    {
        m_teleopCoralL2 = numCoral;
    }

    public int getTeleopCoralL2()
    {
        return m_teleopCoralL2;
    }

    public void setTeleopCoralL3(int numCoral)
    {
        m_teleopCoralL3 = numCoral;
    }

    public int getTeleopCoralL3()
    {
        return m_teleopCoralL3;
    }

    public void setTeleopCoralL4(int numCoral)
    {
        m_teleopCoralL4 = numCoral;
    }

    public int getTeleopCoralL4()
    {
        return m_teleopCoralL4;
    }

    public void setTeleopAlgaeNet(int numNet)
    {
        m_teleopAlgaeNet = numNet;
    }

    public int getTeleopAlgaeNet()
    {
        return m_teleopAlgaeNet;
    }

    public void setTeleopAlgaeProcessor(int numProcessor)
    {
        m_teleopAlgaeProcessor = numProcessor;
    }

    public int getTeleopAlgaeProcessor()
    {
        return m_teleopAlgaeProcessor;
    }

    public void setStartingPosition(int startingPosition)
    {
        m_startingPos = startingPosition;
    }

    public int getStartingPosition()
    {
        return m_startingPos;
    }*/
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

    public void setPreloadAccuracyLevel(int autonPreloadAccuracyRate)
    {
        m_autonPreloadAccuracyRate = autonPreloadAccuracyRate;
    }

    public int getPreloadAccuracyLevel()
    {
        return m_autonPreloadAccuracyRate;
    }


    public void setAutonClimb(int autonClimb)
    {
        m_autonClimb = autonClimb;
    }

    public int getAutonClimb()
    {
        return m_autonClimb;
    }


    public void setEndgameClimb(int endgameClimb)
    {
        m_endgameClimb = endgameClimb;
    }

    public int getEndgameClimb()
    {
        return m_endgameClimb;
    }



    public void setIntakeAndShoot(boolean intakeAndShoot)
    {
        m_intakeAndShoot = intakeAndShoot;
    }

    public boolean getIntakeAndShoot()
    {
        return m_intakeAndShoot;
    }

    public void setNeutralToAlliancePassing(boolean neutralToAlliancePassing)
    {
        m_neutralToAlliancePassing = neutralToAlliancePassing;
    }

    public boolean getNeutralToAlliancePassing()
    {
        return m_neutralToAlliancePassing;
    }

    public void setAllianceToAlliancePassing(boolean allianceToAlliancePassing)
    {
        m_allianceToAlliancePassing = allianceToAlliancePassing;
    }

    public boolean getAllianceToAlliancePassing()
    {
        return m_allianceToAlliancePassing;
    }

    public void setPassingEffectivenessRate(int passingEffectivenessRate)
    {
        m_passingEffectivenessRate = passingEffectivenessRate;
    }

    public int getPassingEffectivenessrate()
    {
        return m_passingEffectivenessRate;
    }

    public void setPlayedDefense(int playedDefense)
    {

        m_playedDefense = playedDefense;
    }

    public int getPlayedDefense()
    {
        return m_playedDefense;
    }




    public void setEndgameClimbLevel(int climbLevel)
    {

        m_climbLevel = climbLevel;
    }

    public int getEndgameClimbLevel()
    {
        return m_climbLevel;
    }



    public void setTeleopPhoto(int teleopPhoto)
    {
        m_teleopPhoto = teleopPhoto;
    }

    public int getTeleopPhoto()
    {
        return m_teleopPhoto;
    }

    public void setTimeDied(int x)
    {
        m_diedGroup = x;
    }

    public int getTimeDied()
    {
        return m_diedGroup;
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

    ////////////  m_died   /////////////////////
    public void setDied(boolean x)
    {
        m_died = x;
    }

    public boolean getDied()
    {
        return m_died;
    }


    public String encodeToTSV()
    {
        // NOTE! THE ORDER IS IMPORTANT!
        // This is the data that goes into the QR code.

        String tsvStr = "";

        // For teamNumber, strip off 'frc' prefix.
        // Boolean values should be "0" or "1," not "true" or "false"

        tsvStr += m_eventCode + "\t";
        tsvStr += m_matchNumber + "\t";

        tsvStr += stripTeamNumPrefix(m_teamNumber) + "\t";

        if (!m_teamAlias.equals(""))
            tsvStr += m_teamAlias + "\t";
        else tsvStr += "-" + "\t";

        tsvStr += m_name + "\t";   // Scout name

        if (m_died)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_autonPreload)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_autonPreloadAccuracyRate + "\t";

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

        tsvStr += m_endgameClimb + "\t";


        tsvStr += m_hoppersUsed + "\t";
        tsvStr += m_accuracyRate + "\t";

        if (m_intakeAndShoot)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_neutralToAlliancePassing)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_allianceToAlliancePassing)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_passingEffectivenessRate + "\t";

        tsvStr += m_playedDefense + "\t";

        tsvStr += m_climbLevel + "\t";

        tsvStr += m_diedGroup + "\t";
        tsvStr += m_startClimb + "\t";

        if (!m_comment.equals(""))
            tsvStr += m_comment + "\t";
        else tsvStr += "-" + "\t";


//HOLD        Log.d(TAG, "MatchData encodeToTSV() columns: " + headers);
        Log.d(TAG, "MatchData encodeToTSV(): " + tsvStr);
        return tsvStr;
    }

    public JSONObject toJSON() throws JSONException
    {
        //This code uses the JSON class to convert the aspects of each match into data that can be saved to a file as JSON
        JSONObject json = new JSONObject();

        json.put("divider", ",");
        json.put("divider", ", \n");

        json.put("headings", "Competition, Team Number, Match Number, Starting Pos, Preload, Auton Hopper, Auton Algae Net, Auton Algae Processor, Auton Coral Floor, Auton Coral Station, Auton Algae Floor, Teleop Hoppers Used, Accuracy Rate, Intake and Shoot, Neutral to Alliance Passing, Alliance to Alliance Passing, Passing Effectiveness Rate, Defense, Cage Climb, Start Climb, Died, Comments, Timestamp, MatchID \n");
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
        json.put(JSON_KEY_AUTON_PRELOAD_ACCURACY_RATE, m_autonPreloadAccuracyRate);
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
        json.put(JSON_KEY_CLIMB_LEVEL, m_climbLevel);
        json.put("divider", ",");
        json.put(JSON_KEY_INTAKE_AND_SHOOT, m_intakeAndShoot);
        json.put("divider", ",");
        json.put(JSON_KEY_NEUTRAL_TO_ALLIANCE_PASSING, m_neutralToAlliancePassing);
        json.put("divider", ",");
        json.put(JSON_KEY_ALLIANCE_TO_ALLIANCE_PASSING, m_allianceToAlliancePassing);
        json.put("divider", ",");
        json.put(JSON_KEY_PASSING_EFFECTIVENESS_RATE, m_passingEffectivenessRate);
        json.put("divider", ",");
        json.put(JSON_KEY_PLAYED_DEFENSE, m_playedDefense);
        json.put("divider", ",");
        json.put(JSON_KEY_DIED, m_diedGroup);
        json.put("divider", ",");
        json.put(JSON_KEY_START_CLIMB, m_startClimb);
        json.put("divider", ",");
        json.put(JSON_KEY_ENDGAME_CLIMB, m_endgameClimb);
        json.put("divider", ",");
        json.put(JSON_KEY_COMMENTS, m_comment);
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

