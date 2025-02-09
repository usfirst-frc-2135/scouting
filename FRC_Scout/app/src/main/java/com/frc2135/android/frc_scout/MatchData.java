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
    private static final String JSON_KEY_MATCH_NUMBER = "matchNumber";

    private static final String JSON_KEY_LEAVE = "leave";
    private static final String JSON_KEY_AUTON_CORAL_L1 = "autonCoralL1";
    private static final String JSON_KEY_AUTON_CORAL_L2 = "autonCoralL2";
    private static final String JSON_KEY_AUTON_CORAL_L3 = "autonCoralL3";
    private static final String JSON_KEY_AUTON_CORAL_L4 = "autonCoralL4";
    private static final String JSON_KEY_AUTON_ALGAE_NET = "autonAlgaeNet";
    private static final String JSON_KEY_AUTON_ALGAE_PROCESSOR = "autonAlgaeProcessor";
    private static final String JSON_KEY_FLOOR_CORAL= "floorCoral";
    private static final String JSON_KEY_STATION_CORAL = "stationCoral";
    private static final String JSON_KEY_FLOOR_ALGAE= "floorAlgae";
    private static final String JSON_KEY_REEF_ALGAE = "reefAlgae";

    private static final String JSON_KEY_REEFZONE_AB = "reefZoneAB";
    private static final String JSON_KEY_REEFZONE_CD = "reefZoneCD";
    private static final String JSON_KEY_REEFZONE_EF = "reefZoneEF";
    private static final String JSON_KEY_REEFZONE_GH = "reefZoneGH";
    private static final String JSON_KEY_REEFZONE_IJ = "reefZoneIJ";
    private static final String JSON_KEY_REEFZONE_KL = "reefZoneKL";

    private static final String JSON_KEY_TELEOP_CORAL_L1 = "teleopCoralL1";
    private static final String JSON_KEY_TELEOP_CORAL_L2 = "teleopCoralL2";
    private static final String JSON_KEY_TELEOP_CORAL_L3 = "teleopCoralL3";
    private static final String JSON_KEY_TELEOP_CORAL_L4 = "teleopCoralL4";
    private static final String JSON_KEY_TELEOP_ALGAE_NET = "teleopAlgaeNet";
    private static final String JSON_KEY_TELEOP_ALGAE_PROCESSOR = "teleopAlgaeProcessor";
    private static final String JSON_KEY_PICK_UP_CORAL = "pickUpCoral";
    private static final String JSON_KEY_CORAL_ACQUIRED= "coralAcquired";
    private static final String JSON_KEY_ALGAE_ACQUIRED = "algaeAcquired";
    private static final String JSON_KEY_STARTING_POSITION = "startingPosition";

    private static final String JSON_KEY_PICK_UP_ALGAE = "pickUpAlgae";
    private static final String JSON_KEY_KNOCK_OFF_ALGAE = "knockOffAlgae";
    private static final String JSON_KEY_ALGAE_FROM_REEF = "algaeFromReef";
    private static final String JSON_KEY_HOLD_BOTH_ELEMENTS = "holdBothElements";
    private static final String JSON_KEY_PLAYED_DEFENSE = "playedDefense";
    private static final String JSON_KEY_NUMBER_PINS = "numberPins";
    private static final String JSON_KEY_NUMBER_ANCHOR = "numberAnchor";
    private static final String JSON_KEY_NUMBER_CAGE = "numberCage";
    private static final String JSON_KEY_NUMBER_BARGE = "numberBarge";
    private static final String JSON_KEY_NUMBER_REEF = "numberReef";

    private static final String JSON_KEY_END_GAME_BARGE = "endGameBarge";
    private static final String JSON_KEY_END_GAME_START_CLIMB = "endGameStartClimb";
    private static final String JSON_KEY_ROBOT_FOUL = "robotFoul";

    private static final String JSON_KEY_COMMENTS = "comments";
    private static final String JSON_KEY_TIMESTAMP = "timestamp";
    private static final String JSON_KEY_DIED = "died";
    private static final String JSON_KEY_MATCH_ID = "matchId";

    // Data members 

    private int m_autonCoralL1;
    private int m_autonCoralL2;
    private int m_autonCoralL3;
    private int m_autonCoralL4;
    private int m_autonAlgaeNet;
    private int m_autonAlgaeProcessor;
    private boolean m_autonLeaveStartingZone;

    private int m_teleopCoralL1;
    private int m_teleopCoralL2;
    private int m_teleopCoralL3;
    private int m_teleopCoralL4;
    private int m_teleopAlgaeNet;
    private int m_teleopAlgaeProcessor;
    private int m_positionStarting;

    private boolean m_pickUpCoral;
    private boolean m_pickUpAlgae;
    private boolean m_coralFloor;
    private boolean m_coralStation;
    private boolean m_algaeFloor;
    private boolean m_algaeReef;
    private int m_playedDefense;

    private boolean m_reefzoneAB;
    private boolean m_reefzoneCD;
    private boolean m_reefzoneEF;
    private boolean m_reefzoneGH;
    private boolean m_reefzoneIJ;
    private boolean m_reefzoneKL;

    private int m_pinFoul;
    private int m_anchorFoul;
    private int m_cageFoul;
    private int m_bargeFoul;
    private int m_teleopReefFoul;

    private boolean m_knockAlgaeOff;
    private boolean m_algaeFromReef;
    private boolean m_holdBothElements;
    private int m_coralAcquired;
    private int m_algaeAcquired;

    private int m_teleopAmpMisses;
    private int m_teleopSpeakerMisses;
    private int m_teleopPasses;

    private int m_bargeClimb;
    private int m_startClimb;
    private int m_robotFoul;

    private String m_comment;
    private boolean m_died;
    private String m_name;
    private String m_teamNumber;
    private String m_matchNumber;
    private final String m_matchID;
    private String m_eventCode;
    private Date m_timestamp;


    // Utility to strip off "frc" prefix to team number.
    public String stripTeamNamePrefix(String teamName)
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
        m_matchNumber = "";
        setAutonLeave(false);

        setAutonCoralL1(0);
        setAutonCoralL2(0);
        setAutonCoralL3(0);
        setAutonCoralL4(0);
        setAutonAlgaeNet(0);
        setAutonAlgaeProcessor(0);
        setFloorCoral(false);
        setStationCoral(false);

        setTeleopCoralL1(0);
        setTeleopCoralL2(0);
        setTeleopCoralL3(0);
        setTeleopCoralL4(0);
        setTeleopAlgaeNet(0);
        setTeleopAlgaeProcessor(0);

        setPickUpCoral(false);
        setPickUpAlgae(false);
        setPlayedDefense(0);
        setFoulPin(0);
        setFoulAnchor(0);
        setCoralAcquired(0);
        setAlgaeAcquired(0);
        setTeleopPasses(0);

        setReefzone_AB(false);
        setReefzone_CD(false);
        setReefzone_EF(false);
        setReefzone_GH(false);
        setReefzone_IJ(false);
        setReefzone_KL(false);

        setEndgameBarge(0);
        setEndgameStartClimbing(0);
        setComment("");
        setTimestamp(Calendar.getInstance().getTime());
        setDied(false);

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

        setAutonLeave(json.getBoolean(JSON_KEY_LEAVE));

        setAutonCoralL1(json.getInt(JSON_KEY_AUTON_CORAL_L1));
        setAutonCoralL2(json.getInt(JSON_KEY_AUTON_CORAL_L2));
        setAutonCoralL3(json.getInt(JSON_KEY_AUTON_CORAL_L3));
        setAutonCoralL4(json.getInt(JSON_KEY_AUTON_CORAL_L4));
        setAutonAlgaeNet(json.getInt(JSON_KEY_AUTON_ALGAE_NET));
        setAutonAlgaeProcessor(json.getInt(JSON_KEY_AUTON_ALGAE_PROCESSOR));
        setFloorCoral(json.getBoolean(JSON_KEY_FLOOR_CORAL));
        setStationCoral(json.getBoolean(JSON_KEY_STATION_CORAL));
        setFloorAlgae(json.getBoolean(JSON_KEY_FLOOR_ALGAE));
        setReefAlgae(json.getBoolean(JSON_KEY_REEF_ALGAE));

        setCoralAcquired(json.getInt(JSON_KEY_CORAL_ACQUIRED));
        setAlgaeAcquired(json.getInt(JSON_KEY_ALGAE_ACQUIRED));

        setPickUpCoral(json.getBoolean(JSON_KEY_PICK_UP_CORAL));
        setPickUpAlgae(json.getBoolean(JSON_KEY_PICK_UP_CORAL));

        setKnockOffAlgae(json.getBoolean(JSON_KEY_KNOCK_OFF_ALGAE));
        setAlgaeFromReef(json.getBoolean(JSON_KEY_ALGAE_FROM_REEF));
        setHoldBothElements(json.getBoolean(JSON_KEY_HOLD_BOTH_ELEMENTS));

        setReefzone_AB(json.getBoolean(JSON_KEY_REEFZONE_AB));
        setReefzone_CD(json.getBoolean(JSON_KEY_REEFZONE_CD));
        setReefzone_EF(json.getBoolean(JSON_KEY_REEFZONE_EF));
        setReefzone_GH(json.getBoolean(JSON_KEY_REEFZONE_GH));
        setReefzone_IJ(json.getBoolean(JSON_KEY_REEFZONE_IJ));
        setReefzone_KL(json.getBoolean(JSON_KEY_REEFZONE_KL));

        setTeleopCoralL1(json.getInt(JSON_KEY_TELEOP_CORAL_L1));
        setTeleopCoralL2(json.getInt(JSON_KEY_TELEOP_CORAL_L2));
        setTeleopCoralL3(json.getInt(JSON_KEY_TELEOP_CORAL_L3));
        setTeleopCoralL4(json.getInt(JSON_KEY_TELEOP_CORAL_L4));
        setTeleopAlgaeNet(json.getInt(JSON_KEY_TELEOP_ALGAE_NET));
        setTeleopAlgaeProcessor(json.getInt(JSON_KEY_TELEOP_ALGAE_PROCESSOR));

        setFoulPin(json.getInt(JSON_KEY_NUMBER_PINS));
        setFoulAnchor(json.getInt(JSON_KEY_NUMBER_ANCHOR));
        setFoulCage(json.getInt(JSON_KEY_NUMBER_CAGE));
        setFoulBarge(json.getInt(JSON_KEY_NUMBER_BARGE));
        setFoulReef(json.getInt(JSON_KEY_NUMBER_REEF));

        setEndgameBarge(json.getInt(JSON_KEY_END_GAME_BARGE));
        setEndgameStartClimbing(json.getInt(JSON_KEY_END_GAME_START_CLIMB));
        setFoulNumber(json.getInt(JSON_KEY_ROBOT_FOUL));

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

        setDied(json.getBoolean(JSON_KEY_DIED));
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

    ////////////  m_matchNumber   /////////////////////
    public void setMatchNumber(String n)
    {
        m_matchNumber = n;
    }

    public String getMatchNumber()
    {
        return m_matchNumber;
    }


    public void setAutonCoralL1(int numCoral) { m_autonCoralL1 = numCoral; }

    public int getAutonCoralL1()
    {
        return m_autonCoralL1;
    }

    public void setAutonCoralL2(int numCoral) { m_autonCoralL2 = numCoral;}

    public int getAutonCoralL2()
    {
        return m_autonCoralL2;
    }

    public void setAutonCoralL3(int numCoral) {m_autonCoralL3 = numCoral;}

    public int getAutonCoralL3()
    {
        return m_autonCoralL3;
    }

    public void setAutonCoralL4(int numCoral) { m_autonCoralL4 = numCoral; }

    public int getAutonCoralL4()
    {
        return m_autonCoralL4;
    }

    public void setAutonAlgaeNet(int numNet)
    {
        m_autonAlgaeNet = numNet;
    }

    public int getAutonAlgaeNet()
    {
        return m_autonAlgaeNet;
    }

    public void setAutonAlgaeProcessor(int numProcessor)
    {
        m_autonAlgaeProcessor = numProcessor;
    }

    public int getAutonAlgaeProcessor()
    {
        return m_autonAlgaeProcessor;
    }

    ////////////  m_autonLeaveStartingZone   /////////////////////

    public void setAutonLeave(boolean x)
    {

        m_autonLeaveStartingZone = x;
    }

    public boolean getAutonLeave()
    {
        return m_autonLeaveStartingZone;
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

    public void setTeleopCoralL1(int numCoral)
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

    // Reefzones checkboxes
    public void setReefzone_AB(boolean val)
    {
        m_reefzoneAB = val;
    }

    public boolean getReefzone_AB()
    {
        return m_reefzoneAB;
    }

    public void setReefzone_CD(boolean val)
    {
        m_reefzoneCD = val;
    }

    public boolean getReefzone_CD()
    {
        return m_reefzoneCD;
    }

    public void setReefzone_EF(boolean val)
    {
        m_reefzoneEF = val;
    }

    public boolean getReefzone_EF()
    {
        return m_reefzoneEF;
    }

    public void setReefzone_GH(boolean val)
    {
        m_reefzoneGH = val;
    }

    public boolean getReefzone_GH()
    {
        return m_reefzoneGH;
    }

    public void setReefzone_IJ(boolean val)
    {
        m_reefzoneIJ = val;
    }

    public boolean getReefzone_IJ()
    {
        return m_reefzoneIJ;
    }

    public void setReefzone_KL(boolean val)
    {
        m_reefzoneKL = val;
    }

    public boolean getReefzone_KL()
    {
        return m_reefzoneKL;
    }

    public void setPickUpCoral(boolean pickUpCoral)
    {
        m_pickUpCoral = pickUpCoral;
    }

    public boolean getPickUpCoral()
    {
        return m_pickUpCoral;
    }

    public void setPickUpAlgae(boolean pickUpAlgae)
    {
        m_pickUpAlgae = pickUpAlgae;
    }

    public boolean getPickUpAlgae()
    {
        return m_pickUpAlgae;
    }

    public void setStartingPosition(int startingPosition)
    {
        m_positionStarting = startingPosition;
    }

    public int getStartingPosition()
    {
        return m_positionStarting;
    }

    public void setKnockOffAlgae(boolean KnockOffAlgae)
    {
        m_knockAlgaeOff = KnockOffAlgae;
    }

    public boolean getKnockOffAlgae()
    {
        return m_knockAlgaeOff;
    }
    public void setAlgaeFromReef(boolean AlgaeFromReef)
    {

        m_algaeFromReef = AlgaeFromReef;
    }

    public boolean getAlgaeFromReef()
    {
        return m_algaeFromReef;
    }

    public void setHoldBothElements(boolean holdBothElements)
    {

        m_holdBothElements = holdBothElements;
    }

    public boolean getHoldBothElements()
    {
        return m_holdBothElements;
    }
    public void setPlayedDefense(int playedDefense)
    {

        m_playedDefense = playedDefense;
    }

    public int getPlayedDefense()
    {
        return m_playedDefense;
    }

    public void setFoulPin(int numberPins)
    {

        m_pinFoul = numberPins;
    }

    public int getFoulPin()
    {
        return m_pinFoul;
    }

    public void setFoulAnchor(int numberAnchor)
    {

        m_anchorFoul = numberAnchor;
    }

    public int getFoulAnchor()
    {
        return m_anchorFoul;
    }

    public void setFoulCage(int numberCage)
    {

        m_cageFoul = numberCage;
    }

    public int getFoulCage()
    {
        return m_cageFoul;
    }

    public void setFoulBarge(int numberBarge)
    {

        m_bargeFoul= numberBarge;
    }

    public int getFoulBarge()
    {
        return m_bargeFoul;
    }

    public void setFoulReef(int numberReef)
    {

        m_teleopReefFoul= numberReef;
    }

    public int getFoulReef()
    {
        return m_teleopReefFoul;
    }

    public void setCoralAcquired(int coralAcquired)
    {
        m_coralAcquired = coralAcquired;
    }

    public int getCoralAcquired()
    {
        return m_coralAcquired;
    }

    public void setAlgaeAcquired(int algaeAcquired)
    {
        m_algaeAcquired = algaeAcquired;
    }

    public int getAlgaeAcquired()
    {
        return m_algaeAcquired;
    }

    public void setTeleopPasses(int numPasses)
    {
        m_teleopPasses = numPasses;
    }

    public int getTeleopPasses()
    {
        return m_teleopPasses;
    }


    public void setEndgameBarge(int x)
    {
        m_bargeClimb = x;
    }

    public int getEndgameBarge()
    {
        return m_bargeClimb;
    }

    ////////////  m_endgameHarmony   /////////////////////
    public void setEndgameStartClimbing(int y)
    {
        m_startClimb = y;
    }

    public int getEndgameStartClimbing()
    {
        return m_startClimb;
    }

    ////////////  m_endgame   /////////////////////

    public void setFoulNumber(int foulNumber)
    {
        m_robotFoul = foulNumber;
    }

    public int getCurrentFoulNumber()
    {
        return m_robotFoul;
    }
    ////////////  m_endgameTrap   /////////////////////

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

        String headers = "TeamNumber AutonStartingPosition AutonLeaveStartingZone AutonCoralL1 AutonCoralL2 AutonCoralL3 AutonCoralL4 AutonAlgaeNet AutonAlgaeProcessor AutonCoralFloor AutonCoralStation AutonAlgaeFloor AutonAlgaeReef TeleopAcquireCoral TeleopAcquireAlgae TeleopAlgaeFloorPickup TeleopCoralFloorPickup TeleopKnockOffAlgae TeleopAcquireAlgaeFromReef TeleopHoldBothGamePieces TeleopCoralL1 TeleopCoralL2 TeleopCoralL3 TeleopCoralL4 TeleopAlgaeNet TeleopAlgaeProcessor TeleopPlayedDefense TeleopPinFoul TeleopAnchorFoul TeleopCageFoul TeleopBargeFoul TeleopReefZoneFoul EndgameClimb EndgameStartClimbing EndgameFoul Died MatchNum Competition Scout Comment";

        String tsvStr = "";

        // For teamNumber, strip off 'frc' prefix.
        tsvStr += stripTeamNamePrefix(m_teamNumber) + "\t";

        tsvStr += m_positionStarting + "\t";
        if (m_autonLeaveStartingZone)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";

        tsvStr += m_reefzoneAB + "\t";
        tsvStr += m_reefzoneCD + "\t";
        tsvStr += m_reefzoneEF + "\t";
        tsvStr += m_reefzoneGH + "\t";
        tsvStr += m_reefzoneIJ + "\t";
        tsvStr += m_reefzoneKL + "\t";

        tsvStr += m_autonCoralL1 + "\t";
        tsvStr += m_autonCoralL2 + "\t";
        tsvStr += m_autonCoralL3 + "\t";
        tsvStr += m_autonCoralL4 + "\t";
        tsvStr += m_autonAlgaeNet + "\t";
        tsvStr += m_autonAlgaeProcessor + "\t";
        if (m_coralFloor)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";
        if (m_coralStation)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";

        if (m_algaeFloor)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";
        if (m_algaeReef)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";


        tsvStr += m_coralAcquired + "\t";
        tsvStr += m_algaeAcquired + "\t";

        if (m_pickUpCoral)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";

        if (m_pickUpAlgae)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";

        if (m_knockAlgaeOff)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";
        if (m_algaeFromReef)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";
        if (m_holdBothElements)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";

        tsvStr += m_teleopCoralL1 + "\t";
        tsvStr += m_teleopCoralL2 + "\t";
        tsvStr += m_teleopCoralL3 + "\t";
        tsvStr += m_teleopCoralL4 + "\t";
        tsvStr += m_teleopAlgaeNet + "\t";
        tsvStr += m_teleopAlgaeProcessor + "\t";

        tsvStr += m_playedDefense + "\t";

        tsvStr += m_pinFoul + "\t";
        tsvStr += m_anchorFoul + "\t";
        tsvStr += m_cageFoul + "\t";
        tsvStr += m_bargeFoul + "\t";
        tsvStr += m_teleopReefFoul + "\t";

        tsvStr += m_bargeClimb + "\t";
        tsvStr += m_startClimb + "\t";
        tsvStr += m_robotFoul + "\t";

        if (m_died)
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";

        tsvStr += m_matchNumber + "\t";
        tsvStr += m_eventCode + "\t";

        tsvStr += m_name + "\t";   // Scout name
        if (!m_comment.equals(""))
            tsvStr += m_comment + "\t";
        else
            tsvStr += "-" + "\t";

        Log.d(TAG, "MatchData encodeToTSV() columns: " + headers);
        Log.d(TAG, "MatchData encodeToTSV(): " + tsvStr);
        return tsvStr;
    }

    public JSONObject toJSON() throws JSONException
    {
        //This code uses the JSON class to convert the aspects of each match into data that can be saved to a file as JSON
        JSONObject json = new JSONObject();

        json.put("divider", ",");
        json.put(JSON_KEY_SCOUT_NAME, m_name);  // Scout name
        json.put("divider", ",");
        json.put("divider", ", \n");

        json.put("headings", "Competition, Team Number, Match Number, Leave Starting Zone, Starting Position, Leave Starting Position, ReefzoneAB, ReefzoneCD, ReefzoneEF, ReefzoneGH, ReefzoneIJ, ReefzoneKL, Auton Coral L1, Auton Coral L2, Auton Coral L3, Auton Coral L4, Auton Algae Net, Auton Algae Processor, Auton Pick Up Coral Floor, Auton Pick Up Coral Station, Teleop Amp Notes, Teleop Amp Misses, Teleop Speaker Notes, Teleop Speaker Misses, Teleop Passes, Endgame Stage, Endgame Harmony, Endgame Spotlit, Endgame Trap, Died, Comments, Timestamp, MatchID \n");
        json.put(JSON_KEY_EVENT_CODE, m_eventCode);
        json.put("divider", ",");
        json.put(JSON_KEY_TEAM_NUMBER, m_teamNumber);
        json.put("divider", ",");
        json.put(JSON_KEY_MATCH_NUMBER, m_matchNumber);
        json.put("divider", ",");
        json.put(JSON_KEY_STARTING_POSITION, m_positionStarting);
        json.put("divider", ",");
        json.put(JSON_KEY_LEAVE, m_autonLeaveStartingZone);
        json.put("divider", ",");
        json.put(JSON_KEY_REEFZONE_AB, m_reefzoneAB);
        json.put("divider", ",");
        json.put(JSON_KEY_REEFZONE_CD, m_reefzoneCD);
        json.put("divider", ",");
        json.put(JSON_KEY_REEFZONE_EF, m_reefzoneEF);
        json.put("divider", ",");
        json.put(JSON_KEY_REEFZONE_GH, m_reefzoneGH);
        json.put("divider", ",");
        json.put(JSON_KEY_REEFZONE_IJ, m_reefzoneIJ);
        json.put("divider", ",");
        json.put(JSON_KEY_REEFZONE_KL, m_reefzoneKL);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_CORAL_L1, m_autonCoralL1);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_CORAL_L2, m_autonCoralL2);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_CORAL_L3, m_autonCoralL3);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_CORAL_L4, m_autonCoralL4);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_ALGAE_NET, m_autonAlgaeNet);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_ALGAE_PROCESSOR, m_autonAlgaeProcessor);
        json.put("divider", ",");
        json.put(JSON_KEY_FLOOR_CORAL, m_coralFloor);
        json.put("divider", ",");
        json.put(JSON_KEY_STATION_CORAL, m_coralStation);
        json.put("divider", ",");
        json.put(JSON_KEY_FLOOR_ALGAE, m_algaeFloor);
        json.put("divider", ",");
        json.put(JSON_KEY_REEF_ALGAE, m_algaeReef);
        json.put("divider", ",");
        json.put(JSON_KEY_CORAL_ACQUIRED, m_coralAcquired);
        json.put("divider", ",");
        json.put(JSON_KEY_ALGAE_ACQUIRED, m_algaeAcquired);
        json.put("divider", ",");
        json.put(JSON_KEY_PICK_UP_CORAL, m_pickUpCoral);
        json.put("divider", ",");
        json.put(JSON_KEY_PICK_UP_ALGAE, m_pickUpAlgae);
        json.put("divider", ",");
        json.put(JSON_KEY_KNOCK_OFF_ALGAE, m_knockAlgaeOff);
        json.put("divider", ",");
        json.put(JSON_KEY_ALGAE_FROM_REEF, m_algaeFromReef);
        json.put("divider", ",");
        json.put(JSON_KEY_HOLD_BOTH_ELEMENTS, m_holdBothElements);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOP_CORAL_L1, m_teleopCoralL1);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOP_CORAL_L2, m_teleopCoralL2);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOP_CORAL_L3, m_teleopCoralL3);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOP_CORAL_L4, m_teleopCoralL4);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOP_ALGAE_NET, m_teleopAlgaeNet);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOP_ALGAE_PROCESSOR, m_teleopAlgaeProcessor);
        json.put("divider", ",");
        json.put(JSON_KEY_PLAYED_DEFENSE, m_playedDefense);
        json.put("divider", ",");
        json.put(JSON_KEY_NUMBER_PINS, m_pinFoul);
        json.put("divider", ",");
        json.put(JSON_KEY_NUMBER_ANCHOR, m_anchorFoul);
        json.put("divider", ",");
        json.put(JSON_KEY_NUMBER_CAGE, m_cageFoul);
        json.put("divider", ",");
        json.put(JSON_KEY_NUMBER_BARGE, m_bargeFoul);
        json.put("divider", ",");
        json.put(JSON_KEY_NUMBER_REEF, m_teleopReefFoul);
        json.put("divider", ",");
        json.put(JSON_KEY_END_GAME_BARGE, m_bargeClimb);
        json.put("divider", ",");
        json.put(JSON_KEY_END_GAME_START_CLIMB, m_startClimb);
        json.put("divider", ",");
        json.put(JSON_KEY_ROBOT_FOUL, m_robotFoul);
        json.put("divider", ",");
        json.put(JSON_KEY_DIED, m_died);
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

