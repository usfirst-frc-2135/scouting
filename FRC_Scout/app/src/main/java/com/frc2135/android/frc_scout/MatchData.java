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

    // Auton data
    private static final String JSON_KEY_STARTING_POSITION = "startingPosition";
    private static final String JSON_KEY_LEAVE = "leave";

    private static final String JSON_KEY_REEFZONE_AB = "reefZoneAB";
    private static final String JSON_KEY_REEFZONE_CD = "reefZoneCD";
    private static final String JSON_KEY_REEFZONE_EF = "reefZoneEF";
    private static final String JSON_KEY_REEFZONE_GH = "reefZoneGH";
    private static final String JSON_KEY_REEFZONE_IJ = "reefZoneIJ";
    private static final String JSON_KEY_REEFZONE_KL = "reefZoneKL";

    private static final String JSON_KEY_AUTON_CORAL_L1 = "autonCoralL1";
    private static final String JSON_KEY_AUTON_CORAL_L2 = "autonCoralL2";
    private static final String JSON_KEY_AUTON_CORAL_L3 = "autonCoralL3";
    private static final String JSON_KEY_AUTON_CORAL_L4 = "autonCoralL4";

    private static final String JSON_KEY_FLOOR_CORAL= "floorCoral";
    private static final String JSON_KEY_STATION_CORAL = "stationCoral";
    private static final String JSON_KEY_FLOOR_ALGAE= "floorAlgae";
    private static final String JSON_KEY_REEF_ALGAE = "reefAlgae";

    private static final String JSON_KEY_AUTON_ALGAE_NET = "autonAlgaeNet";
    private static final String JSON_KEY_AUTON_ALGAE_PROCESSOR = "autonAlgaeProcessor";

    // Teleop data
    private static final String JSON_KEY_CORAL_ACQUIRED= "coralAcquired";
    private static final String JSON_KEY_ALGAE_ACQUIRED = "algaeAcquired";

    private static final String JSON_KEY_PICK_UP_CORAL = "pickUpCoral";
    private static final String JSON_KEY_PICK_UP_ALGAE = "pickUpAlgae";

    private static final String JSON_KEY_KNOCK_OFF_ALGAE = "knockOffAlgae";
    private static final String JSON_KEY_ALGAE_FROM_REEF = "algaeFromReef";
    private static final String JSON_KEY_HOLD_BOTH_ELEMENTS = "holdBothElements";

    private static final String JSON_KEY_TELEOP_ALGAE_NET = "teleopAlgaeNet";
    private static final String JSON_KEY_TELEOP_ALGAE_PROCESSOR = "teleopAlgaeProcessor";

    private static final String JSON_KEY_TELEOP_CORAL_L1 = "teleopCoralL1";
    private static final String JSON_KEY_TELEOP_CORAL_L2 = "teleopCoralL2";
    private static final String JSON_KEY_TELEOP_CORAL_L3 = "teleopCoralL3";
    private static final String JSON_KEY_TELEOP_CORAL_L4 = "teleopCoralL4";

    private static final String JSON_KEY_PLAYED_DEFENSE = "playedDefense";

    // Endgame data
    private static final String JSON_KEY_CAGE_CLIMB = "cageClimb";
    private static final String JSON_KEY_START_CLIMB = "startClimb";

    private static final String JSON_KEY_DIED = "died";
    private static final String JSON_KEY_COMMENTS = "comments";
    private static final String JSON_KEY_TIMESTAMP = "timestamp";
    private static final String JSON_KEY_MATCH_ID = "matchId";

    // Data members 
    // Auton data
    private int m_startingPos;
    private boolean m_autonLeave;

    private boolean m_reefzoneAB;
    private boolean m_reefzoneCD;
    private boolean m_reefzoneEF;
    private boolean m_reefzoneGH;
    private boolean m_reefzoneIJ;
    private boolean m_reefzoneKL;

    private int m_autonCoralL1;
    private int m_autonCoralL2;
    private int m_autonCoralL3;
    private int m_autonCoralL4;

    private boolean m_coralFloor;
    private boolean m_coralStation;
    private boolean m_algaeFloor;
    private boolean m_algaeReef;

    private int m_autonAlgaeNet;
    private int m_autonAlgaeProcessor;

    // Teleop data
    private int m_teleopCoralL1;
    private int m_teleopCoralL2;
    private int m_teleopCoralL3;
    private int m_teleopCoralL4;
    private int m_teleopAlgaeNet;
    private int m_teleopAlgaeProcessor;

    private boolean m_pickUpCoral;
    private boolean m_pickUpAlgae;
    private int m_playedDefense;

    private boolean m_knockAlgaeOff;
    private boolean m_algaeFromReef;
    private boolean m_holdBothElements;
    private int m_coralAcquired;
    private int m_algaeAcquired;

    private int m_cageClimb;
    private int m_startClimb;

    private String m_comment;
    private boolean m_died;
    private String m_name;
    private String m_teamNumber;
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
        m_matchNumber = "";
        // Auton data
        setAutonLeave(false);
        setStartingPosition(0);

        setReefzone_AB(false);
        setReefzone_CD(false);
        setReefzone_EF(false);
        setReefzone_GH(false);
        setReefzone_IJ(false);
        setReefzone_KL(false);

        setAutonCoralL1(0);
        setAutonCoralL2(0);
        setAutonCoralL3(0);
        setAutonCoralL4(0);

        setFloorCoral(false);
        setStationCoral(false);
        setFloorAlgae(false);
        setReefAlgae(false);

        setAutonAlgaeNet(0);
        setAutonAlgaeProcessor(0);

        // Teleop data
        setCoralAcquired(0);
        setAlgaeAcquired(0);

        setPickUpCoral(false);
        setPickUpAlgae(false);

        setKnockOffAlgae(false);
        setAlgaeFromReef(false);
        setHoldBothElements(false);

        setTeleopAlgaeNet(0);
        setTeleopAlgaeProcessor(0);

        setTeleopCoralL1(0);
        setTeleopCoralL2(0);
        setTeleopCoralL3(0);
        setTeleopCoralL4(0);

        setPlayedDefense(0);

        // Endgame data
        setCageClimb(0);
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
        setStartingPosition(json.getInt(JSON_KEY_STARTING_POSITION));
        setAutonLeave(json.getBoolean(JSON_KEY_LEAVE));

        setReefzone_AB(json.getBoolean(JSON_KEY_REEFZONE_AB));
        setReefzone_CD(json.getBoolean(JSON_KEY_REEFZONE_CD));
        setReefzone_EF(json.getBoolean(JSON_KEY_REEFZONE_EF));
        setReefzone_GH(json.getBoolean(JSON_KEY_REEFZONE_GH));
        setReefzone_IJ(json.getBoolean(JSON_KEY_REEFZONE_IJ));
        setReefzone_KL(json.getBoolean(JSON_KEY_REEFZONE_KL));

        setAutonCoralL1(json.getInt(JSON_KEY_AUTON_CORAL_L1));
        setAutonCoralL2(json.getInt(JSON_KEY_AUTON_CORAL_L2));
        setAutonCoralL3(json.getInt(JSON_KEY_AUTON_CORAL_L3));
        setAutonCoralL4(json.getInt(JSON_KEY_AUTON_CORAL_L4));

        setFloorCoral(json.getBoolean(JSON_KEY_FLOOR_CORAL));
        setStationCoral(json.getBoolean(JSON_KEY_STATION_CORAL));
        setFloorAlgae(json.getBoolean(JSON_KEY_FLOOR_ALGAE));
        setReefAlgae(json.getBoolean(JSON_KEY_REEF_ALGAE));

        setAutonAlgaeNet(json.getInt(JSON_KEY_AUTON_ALGAE_NET));
        setAutonAlgaeProcessor(json.getInt(JSON_KEY_AUTON_ALGAE_PROCESSOR));

        // Teleop data
        setCoralAcquired(json.getInt(JSON_KEY_CORAL_ACQUIRED));
        setAlgaeAcquired(json.getInt(JSON_KEY_ALGAE_ACQUIRED));

        setPickUpCoral(json.getBoolean(JSON_KEY_PICK_UP_ALGAE));
        setPickUpAlgae(json.getBoolean(JSON_KEY_PICK_UP_CORAL));

        setKnockOffAlgae(json.getBoolean(JSON_KEY_KNOCK_OFF_ALGAE));
        setAlgaeFromReef(json.getBoolean(JSON_KEY_ALGAE_FROM_REEF));
        setHoldBothElements(json.getBoolean(JSON_KEY_HOLD_BOTH_ELEMENTS));

        setTeleopAlgaeNet(json.getInt(JSON_KEY_TELEOP_ALGAE_NET));
        setTeleopAlgaeProcessor(json.getInt(JSON_KEY_TELEOP_ALGAE_PROCESSOR));

        setTeleopCoralL1(json.getInt(JSON_KEY_TELEOP_CORAL_L1));
        setTeleopCoralL2(json.getInt(JSON_KEY_TELEOP_CORAL_L2));
        setTeleopCoralL3(json.getInt(JSON_KEY_TELEOP_CORAL_L3));
        setTeleopCoralL4(json.getInt(JSON_KEY_TELEOP_CORAL_L4));

        setPlayedDefense(json.getInt(JSON_KEY_PLAYED_DEFENSE));

        setCageClimb(json.getInt(JSON_KEY_CAGE_CLIMB));
        setStartClimb(json.getInt(JSON_KEY_START_CLIMB));

        setDied(json.getBoolean(JSON_KEY_DIED));
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

    ////////////  m_autonLeave   /////////////////////

    public void setAutonLeave(boolean x)
    {

        m_autonLeave = x;
    }

    public boolean getAutonLeave()
    {
        return m_autonLeave;
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
        m_startingPos = startingPosition;
    }

    public int getStartingPosition()
    {
        return m_startingPos;
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

    public void setCageClimb(int x)
    {
        m_cageClimb = x;
    }

    public int getCageClimb()
    {
        return m_cageClimb;
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

//HOLD        String headers = "TeamNumber AutonStartingPos AutonLeave ReefzoneAB ReefzoneCD ReefzoneEF ReefzoneGH ReefzoneIJ ReefzoneKL AutonCoralL1 AutonCoralL2 AutonCoralL3 AutonCoralL4 AutonAlgaeNet AutonAlgaeProc AutonCoralFloor AutonCoralStation AutonAlgaeFloor AutonAlgaeReef AcquiredCoral AcquiredAlgae AlgaeFloorPickup CoralFloorPickup KnockOffAlgae AlgaeFromReef HoldBoth TeleopCoralL1 TeleopCoralL2 TeleopCoralL3 TeleopCoralL4 TeleopAlgaeNet TeleopAlgaeProc Defense EndgameClimb EndgameStartClimb Died MatchNum Competition Scout Comment";

        String tsvStr = "";

        // For teamNumber, strip off 'frc' prefix.
        tsvStr += stripTeamNumPrefix(m_teamNumber) + "\t";

        tsvStr += m_startingPos + "\t";
        if (m_autonLeave)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_reefzoneAB) // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";
        if (m_reefzoneCD)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";
        if (m_reefzoneEF)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";
        if(m_reefzoneGH)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";
        if (m_reefzoneIJ)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";
        if (m_reefzoneKL)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_autonCoralL1 + "\t";
        tsvStr += m_autonCoralL2 + "\t";
        tsvStr += m_autonCoralL3 + "\t";
        tsvStr += m_autonCoralL4 + "\t";
        tsvStr += m_autonAlgaeNet + "\t";
        tsvStr += m_autonAlgaeProcessor + "\t";
        if (m_coralFloor)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_coralStation)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_algaeFloor)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_algaeReef)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_coralAcquired + "\t";
        tsvStr += m_algaeAcquired + "\t";

        if (m_pickUpAlgae)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_pickUpCoral)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_knockAlgaeOff)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_algaeFromReef)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if (m_holdBothElements)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_teleopCoralL1 + "\t";
        tsvStr += m_teleopCoralL2 + "\t";
        tsvStr += m_teleopCoralL3 + "\t";
        tsvStr += m_teleopCoralL4 + "\t";
        tsvStr += m_teleopAlgaeNet + "\t";
        tsvStr += m_teleopAlgaeProcessor + "\t";

        tsvStr += m_playedDefense + "\t";

        tsvStr += m_cageClimb + "\t";
        tsvStr += m_startClimb + "\t";

        if (m_died)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_matchNumber + "\t";
        tsvStr += m_eventCode + "\t";

        tsvStr += m_name + "\t";   // Scout name
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
        json.put(JSON_KEY_SCOUT_NAME, m_name);  // Scout name
        json.put("divider", ",");
        json.put("divider", ", \n");

        json.put("headings", "Competition, Team Number, Match Number, Starting Pos, Leave, ReefzoneAB, ReefzoneCD, ReefzoneEF, ReefzoneGH, ReefzoneIJ, ReefzoneKL, Auton Coral L1, Auton Coral L2, Auton Coral L3, Auton Coral L4, Auton Algae Net, Auton Algae Processor, Auton Coral Floor, Auton Coral Station, Auton Algae Floor, Auton Algae Reef, Coral Acquired, Algae Acquired, Teleop Coral Floor, Teleop Algae Floor, Algae From Reef, Hold Both, Teleop Coral L1, Teleop Coral L2, Teleop Coral L3, Teleop Coral L4, Teleop Algae Net, Teleop Algae Processor, Defense, Cage Climb, Start Climb, Died, Comments, Timestamp, MatchID \n");
        json.put(JSON_KEY_EVENT_CODE, m_eventCode);
        json.put("divider", ",");
        json.put(JSON_KEY_TEAM_NUMBER, m_teamNumber);
        json.put("divider", ",");
        json.put(JSON_KEY_MATCH_NUMBER, m_matchNumber);
        json.put("divider", ",");
        json.put(JSON_KEY_STARTING_POSITION, m_startingPos);
        json.put("divider", ",");
        json.put(JSON_KEY_LEAVE, m_autonLeave);
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
        json.put(JSON_KEY_CAGE_CLIMB, m_cageClimb);
        json.put("divider", ",");
        json.put(JSON_KEY_START_CLIMB, m_startClimb);
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

