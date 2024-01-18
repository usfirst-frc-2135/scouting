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

public class MatchData
{
    private static final String TAG = "MatchData";

    // Keys used for reading/writing match JSON files.
    private static final String JSON_KEY_SCOUT_NAME = "scoutName";
    private static final String JSON_KEY_EVENT_CODE = "eventCode";
    private static final String JSON_KEY_TEAM_NUMBER = "teamNumber";
    private static final String JSON_KEY_MATCH_NUMBER = "matchNumber";

    private static final String JSON_KEY_MOBILITY = "mobility"; //REMOVE
    private static final String JSON_KEY_LEAVE = "leave";
    private static final String JSON_KEY_AUTON_CONES_BOTTOM_ROW = "autonConesBottomRow";//REMOVE
    private static final String JSON_KEY_AUTON_CONES_MIDDLE_ROW = "autonConesMiddleRow";//REMOVE
    private static final String JSON_KEY_AUTON_CONES_TOP_ROW = "autonConesTopRow";//REMOVE
    private static final String JSON_KEY_AUTON_AMP_NOTES = "autonAmpNotes";
    private static final String JSON_KEY_AUTON_CUBES_BOTTOM_ROW = "autonCubesBottomRow"; //REMOVE
    private static final String JSON_KEY_AUTON_CUBES_MIDDLE_ROW = "autonCubesMiddleRow"; //REMOVE
    private static final String JSON_KEY_AUTON_CUBES_TOP_ROW = "autonCubesTopRow"; //REMOVE
    private static final String JSON_KEY_AUTON_SPEAKER_NOTES = "autonSpeakerNotes";
    private static final String JSON_KEY_AUTON_CHARGE = "autonCharge"; //REMOVE

    private static final String JSON_KEY_TELEOP_CONES_BOTTOM_ROW = "teleopConesBottomRow"; //REMOVE
    private static final String JSON_KEY_TELEOP_CONES_MIDDLE_ROW = "teleopConesMiddleRow"; //REMOVE
    private static final String JSON_KEY_TELEOP_CONES_TOP_ROW = "teleopConesTopRow"; //REMOVE
    private static final String JSON_KEY_TELEOP_AMP_NOTES = "teleopAmpNotes";
    private static final String JSON_KEY_TELEOP_CUBES_BOTTOM_ROW = "teleopCubesBottomRow"; //REMOVE
    private static final String JSON_KEY_TELEOP_CUBES_MIDDLE_ROW = "teleopCubesMiddleRow"; //REMOVE
    private static final String JSON_KEY_TELEOP_CUBES_TOP_ROW = "teleopCubesTopRow";
    private static final String JSON_KEY_TELEOP_SPEAKER_NOTES = "teleopSpeakerNotes";

    private static final String JSON_KEY_END_GAME_CHARGE = "endGameCharge"; //REMOVE
    private static final String JSON_KEY_END_GAME_STAGE = "endGameStage";
    //ADD HARMONY
    private static final String JSON_KEY_END_GAME_HARMONY = "endGameHarmony";
    //ADD SPOTLIT
    private static final String JSON_KEY_END_GAME_SPOTLIT = "endGameSpotlit";
    //ADD TRAP
    private static final String JSON_KEY_END_GAME_TRAP = "endGameTrap";
    private static final String JSON_KEY_COMMENTS = "comments";
    private static final String JSON_KEY_TIMESTAMP = "timestamp";
    private static final String JSON_KEY_DIED = "died";
    private static final String JSON_KEY_MATCH_ID = "matchId";
    private static final String JSON_KEY_PICKED_UP_CUBE = "pickedUpCube";
    private static final String JSON_KEY_PICKED_UP_UPRIGHT = "pickedUpUpright";
    private static final String JSON_KEY_PICKED_UP_TIPPED = "pickedUpTipped";

    // Data members 
    private int m_autonConesBottomRow; //REMOVE
    private int m_autonConesMiddleRow; //REMOVE
    private int m_autonConesTopRow; //REMOVE
    private int m_autonAmpNotes;
    private int m_autonCubesBottomRow; //REMOVE
    private int m_autonCubesMiddleRow; //REMOVE
    private int m_autonCubesTopRow;  //REMOVE
    private int m_autonChargeLevel; //REMOVE
    private int m_autonSpeakerNotes;
    private boolean m_autonLeaveStartingZone;

    private int m_teleopConesBottomRow; //REMOVE
    private int m_teleopConesMiddleRow; //REMOVE
    private int m_teleopConesTopRow; //REMOVE
    private int m_teleopCubesBottomRow; //REMOVE
    private int m_teleopCubesMiddleRow; //REMOVE
    private int m_teleopCubesTopRow; //REMOVE
    private int m_teleopAmpNotes;
    private int m_teleopSpeakerNotes;


    private int m_endgameChargeLevel; //REMOVE
    private int m_endgameStage;
    private int m_endgameHarmony; //new
    private boolean m_endgameSpotLit; //new
    private boolean m_endgameTrap; //new

    private boolean m_exitedCommunity; //DELETE
    private String m_comment;
    private boolean m_died;
    private String m_name;
    private String m_teamNumber;
    private String m_matchNumber;
    private final String m_matchID;
    private String m_eventCode;
    private Date m_timestamp;
    private boolean m_pickedUpCube;
    private boolean m_pickedUpUpright;
    private boolean m_pickedUpTipped;

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
        setExitedCommunity(false); //REMOVE

        setAutonAmpNotes(0);
        setAutonSpeakerNotes(0);
        setAutonConesBottomRow(0); //REMOVE
        setAutonConesMiddleRow(0); //REMOVE
        setAutonConesTopRow(0); //REMOVE
        setAutonCubesBottomRow(0); //REMOVE
        setAutonCubesMiddleRow(0); //REMOVE
        setAutonCubesTopRow(0); //REMOVE
        setAutonChargeLevel(0); //REMOVE

        setTeleopAmpNotes(0);
        setTeleopSpeakerNotes(0);
        setTeleopConesBottomRow(0); //REMOVE
        setTeleopConesMiddleRow(0); //REMOVE
        setTeleopConesTopRow(0); //REMOVE
        setTeleopCubesBottomRow(0); //REMOVE
        setTeleopCubesMiddleRow(0); //REMOVE
        setTeleopCubesTopRow(0); //REMOVE

        setEndgameChargeLevel(0); //REMOVE
        setEndgameStage(0);
        setEndgameHarmony(0);
        setEndgameSpotLit(false);
        setEndgameTrap(false);
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

        setExitedCommunity(json.getBoolean(JSON_KEY_MOBILITY)); //REMOVE
        setAutonLeave(json.getBoolean(JSON_KEY_LEAVE));

        setAutonAmpNotes(json.getInt(JSON_KEY_AUTON_AMP_NOTES));
        setAutonConesBottomRow(json.getInt(JSON_KEY_AUTON_CONES_BOTTOM_ROW)); //REMOVE
        setAutonConesMiddleRow(json.getInt(JSON_KEY_AUTON_CONES_MIDDLE_ROW)); //REMOVE
        setAutonConesTopRow(json.getInt(JSON_KEY_AUTON_CONES_TOP_ROW)); //REMOVE
        setAutonSpeakerNotes(json.getInt(JSON_KEY_AUTON_SPEAKER_NOTES));
        setAutonCubesBottomRow(json.getInt(JSON_KEY_AUTON_CUBES_BOTTOM_ROW)); //REMOVE
        setAutonCubesMiddleRow(json.getInt(JSON_KEY_AUTON_CUBES_MIDDLE_ROW)); //REMOVE
        setAutonCubesTopRow(json.getInt(JSON_KEY_AUTON_CUBES_TOP_ROW)); //REMOVE
        setAutonChargeLevel(json.getInt(JSON_KEY_AUTON_CHARGE)); //REMOVE

        setTeleopAmpNotes(json.getInt(JSON_KEY_TELEOP_AMP_NOTES));
        setTeleopConesBottomRow(json.getInt(JSON_KEY_TELEOP_CONES_BOTTOM_ROW)); //REMOVE
        setTeleopConesMiddleRow(json.getInt(JSON_KEY_TELEOP_CONES_MIDDLE_ROW)); //REMOVE
        setTeleopConesTopRow(json.getInt(JSON_KEY_TELEOP_CONES_TOP_ROW)); //REMOVE
        setTeleopSpeakerNotes(json.getInt(JSON_KEY_TELEOP_SPEAKER_NOTES));
        setTeleopCubesBottomRow(json.getInt(JSON_KEY_TELEOP_CUBES_BOTTOM_ROW)); //REMOVE
        setTeleopCubesMiddleRow(json.getInt(JSON_KEY_TELEOP_CUBES_MIDDLE_ROW)); //REMOVE
        setTeleopCubesTopRow(json.getInt(JSON_KEY_TELEOP_CUBES_TOP_ROW)); //REMOVE

        setPickedUpCube(json.getBoolean(JSON_KEY_PICKED_UP_CUBE)); //REMOVE
        setPickedUpUpright(json.getBoolean(JSON_KEY_PICKED_UP_UPRIGHT)); //REMOVE
        setPickedUpTipped(json.getBoolean(JSON_KEY_PICKED_UP_TIPPED)); //REMOVE

        setEndgameStage(json.getInt(JSON_KEY_END_GAME_STAGE));
        setEndgameChargeLevel(json.getInt(JSON_KEY_END_GAME_CHARGE)); //REMOVE
        setEndgameHarmony(json.getInt(JSON_KEY_END_GAME_HARMONY));
        setEndgameSpotLit(json.getBoolean(JSON_KEY_END_GAME_SPOTLIT));
        setEndgameTrap(json.getBoolean(JSON_KEY_END_GAME_TRAP));
        setComment(json.getString(JSON_KEY_COMMENTS));

        String dateStr = json.getString(JSON_KEY_TIMESTAMP);
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy", Locale.US);
        Date date = null;
        try
        {
            date = dt.parse(dateStr);
        } catch (Exception err)
        {
            Log.d("timestamp Date string error: ", err.getMessage());
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

    ////////////  m_autonConesBottomRow   /////////////////////

    public void setAutonConesBottomRow(int numCones) //REMOVE
    {
        m_autonConesBottomRow = numCones;
    }

    public int getAutonConesBottomRow() //REMOVE
    {
        return m_autonConesBottomRow;
    }
    public void setAutonAmpNotes(int numNotes)
    {
        m_autonAmpNotes = numNotes;
    }

    public int getAutonAmpNotes()
    {
        return m_autonAmpNotes;
    }

    ////////////  m_autonConesMiddleRow   /////////////////////
    public void setAutonConesMiddleRow(int y) //REMOVE
    {
        m_autonConesMiddleRow = y;
    }

    public int getAutonConesMiddleRow() //REMOVE
    {
        return m_autonConesMiddleRow;
    }

    ////////////  m_autonConesTopRow   /////////////////////
    public void setAutonConesTopRow(int z) //REMOVE
    {
        m_autonConesTopRow = z;
    }

    public int getAutonConesTopRow() //REMOVE
    {
        return m_autonConesTopRow;
    }

    ////////////  m_autonCubesBottomRow   /////////////////////
    public void setAutonCubesBottomRow(int a) //REMOVE
    {
        m_autonCubesBottomRow = a;
    }

    public int getAutonCubesBottomRow() //REMOVE
    {
        return m_autonCubesBottomRow;
    }

    public void setAutonSpeakerNotes(int a)
    {
        m_autonSpeakerNotes = a;
    }

    public int getAutonSpeakerNotes()
    {
        return m_autonSpeakerNotes;
    }

    ////////////  m_autonCubesMiddleRow   /////////////////////
    public void setAutonCubesMiddleRow(int b) //REMOVE
    {
        m_autonCubesMiddleRow = b;
    }

    public int getAutonCubesMiddleRow() //REMOVE
    {
        return m_autonCubesMiddleRow;
    }

    ////////////  m_autonCubesTopRow   /////////////////////
    public void setAutonCubesTopRow(int c) //REMOVE
    {
        m_autonCubesTopRow = c;
    }

    public int getAutonCubesTopRow() //REMOVE
    {
        return m_autonCubesTopRow;
    }

    ////////////  m_exitedCommunity   /////////////////////
    public void setExitedCommunity(boolean x) //REMOVE
    {
        m_exitedCommunity = x;
    }

    public boolean getExitedCommunity() //REMOVE
    {
        return m_exitedCommunity;
    }

    public void setAutonLeave(boolean x)
    {

        m_autonLeaveStartingZone = x;
    }

    public boolean getAutonLeave()
    {
        return m_autonLeaveStartingZone;
    }

    ////////////  m_autonChargeStation   /////////////////////
    public void setAutonChargeLevel(int x) //REMOVE
    {
        m_autonChargeLevel = x;
    }

    public int getAutonChargeLevel() //REMOVE
    {
        return m_autonChargeLevel;
    }

    ////////////  m_teleopConesBottomRow   /////////////////////
    public void setTeleopConesBottomRow(int numCones) //REMOVE
    {
        m_teleopConesBottomRow = numCones;
    }

    public int getTeleopConesBottomRow() //REMOVE
    {
        return m_teleopConesBottomRow;
    }

    public void setTeleopAmpNotes(int numNotes)
    {
        m_teleopAmpNotes = numNotes;
    }

    public int getTeleopAmpNotes()
    {
        return m_teleopAmpNotes;
    }

    ////////////  m_teleopConesMiddleRow   /////////////////////
    public void setTeleopConesMiddleRow(int y) //REMOVE
    {
        m_teleopConesMiddleRow = y;
    }

    public int getTeleopConesMiddleRow() //REMOVE
    {
        return m_teleopConesMiddleRow;
    }

    ////////////  m_TeleopConesTopRow   /////////////////////
    public void setTeleopConesTopRow(int z) //REMOVE
    {
        m_teleopConesTopRow = z;
    }

    public int getTeleopConesTopRow() //REMOVE
    {
        return m_teleopConesTopRow;
    }

    ////////////  m_teleopCubesBottomRow   /////////////////////
    public void setTeleopCubesBottomRow(int a) //REMOVE
    {
        m_teleopCubesBottomRow = a;
    }

    public int getTeleopCubesBottomRow() //REMOVE
    {
        return m_teleopCubesBottomRow;
    }

    public void setTeleopSpeakerNotes(int a)
    {
        m_teleopSpeakerNotes = a;
    }

    public int getTeleopSpeakerNotes()
    {
        return m_teleopSpeakerNotes;
    }

    ////////////  m_teleopCubesMiddleRow   /////////////////////
    public void setTeleopCubesMiddleRow(int b) //REMOVE
    {
        m_teleopCubesMiddleRow = b;
    }

    public int getTeleopCubesMiddleRow() //REMOVE
    {
        return m_teleopCubesMiddleRow;
    }

    ////////////  m_teleopCubesTopRow   /////////////////////
    public void setTeleopCubesTopRow(int c) //REMOVE
    {
        m_teleopCubesTopRow = c;
    }

    public int getTeleopCubesTopRow() //REMOVE
    {
        return m_teleopCubesTopRow;
    }

    ////////////  m_endgameStage   /////////////////////
    public void setEndgameChargeLevel(int x) //REMOVE
    {
        m_endgameChargeLevel = x;
    }

    public int getEndgameChargeLevel() //REMOVE
    {
        return m_endgameChargeLevel;
    }

    public void setEndgameStage(int x)
    {
        m_endgameStage = x;
    }

    public int getEndgameStage()
    {
        return m_endgameStage;
    }

    ////////////  m_endgameHarmony   /////////////////////
    public void setEndgameHarmony(int y)
    {
        m_endgameHarmony = y;
    }

    public int getEndgameHarmony()
    {
        return m_endgameHarmony;
    }

    ////////////  m_endgameSpotLit   /////////////////////
    public void setEndgameSpotLit(boolean z)
    {
        m_endgameSpotLit = z;
    }

    public boolean getEndgameSpotLit()
    {
        return m_endgameSpotLit;
    }

    ////////////  m_endgameTrap   /////////////////////
    public void setEndgameTrap(boolean a)
    {
        m_endgameTrap = a;
    }

    public boolean getEndgameTrap()
    {
        return m_endgameTrap;
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

    ////////////  m_pickedUpCube   /////////////////////
    public void setPickedUpCube(boolean x) //REMOVE
    {
        m_pickedUpCube = x;
    }

    public boolean getPickedUpCube() //REMOVE
    {
        return m_pickedUpCube;
    }

    ////////////  m_pickedUpUpright   /////////////////////
    public void setPickedUpUpright(boolean x) //REMOVE
    {
        m_pickedUpUpright = x;
    }

    public boolean getPickedUpUpright() //REMOVE
    {
        return m_pickedUpUpright;
    }

    ////////////  m_pickedUpTipped   /////////////////////
    public void setPickedUpTipped(boolean x) //REMOVE
    {
        m_pickedUpTipped = x;
    }

    public boolean getPickedUpTipped() //REMOVE
    {
        return m_pickedUpTipped;
    }

    public String encodeToTSV()
    {
        // NOTE! THE ORDER IS IMPORTANT!
        // This is the data that goes into the QR code.

        String headers = "TeamNumber AutonLeaveStartingZone AutonAmpNotes AutonSpeakerNotes TeleopAmpNotes TeleopSpeakerNotes EndgameStage EndgameHarmony EndgameSpotlit EndgameTrap Died MatchNum Competition Scout Comment"; 

        String tsvStr = "";

        // For teamNumber, strip off 'frc' prefix.
        tsvStr += stripTeamNamePrefix(m_teamNumber) + "\t";


        if (m_autonLeaveStartingZone)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";

        tsvStr += m_autonAmpNotes + "\t";
        tsvStr += m_autonSpeakerNotes + "\t";

        tsvStr += m_teleopAmpNotes + "\t";
        tsvStr += m_teleopSpeakerNotes + "\t";

        tsvStr += m_endgameStage + "\t";
        tsvStr += m_endgameHarmony + "\t";

        if (m_endgameSpotLit)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";

        if (m_endgameTrap)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";


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
        //TODO Work on this with new data
        JSONObject json = new JSONObject();

        json.put("divider", ",");
        json.put(JSON_KEY_SCOUT_NAME, m_name);  // Scout name
        json.put("divider", ",");
        json.put("divider", ", \n");

        json.put("headings", "Competition, Team Number, Match Number, Leave Starting Zone, Auton Amp Notes, Auton Speaker Notes, Teleop Amp Notes, Teleop Speaker Notes, Endgame Stage, Endgame Harmony, Endgame Spotlit, Endgame Trap, Died, Comments, Timestamp, MatchID \n");
        json.put(JSON_KEY_EVENT_CODE, m_eventCode);
        json.put("divider", ",");
        json.put(JSON_KEY_TEAM_NUMBER, m_teamNumber);
        json.put("divider", ",");
        json.put(JSON_KEY_MATCH_NUMBER, m_matchNumber);
        json.put("divider", ",");
        json.put("divider", ",");
        json.put(JSON_KEY_LEAVE, m_autonLeaveStartingZone);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_AMP_NOTES, m_autonAmpNotes);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_SPEAKER_NOTES, m_autonSpeakerNotes);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOP_AMP_NOTES, m_teleopAmpNotes);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOP_SPEAKER_NOTES, m_teleopSpeakerNotes);
        json.put("divider", ",");
        json.put(JSON_KEY_END_GAME_STAGE, m_endgameStage);
        json.put("divider", ",");
        json.put(JSON_KEY_END_GAME_HARMONY, m_endgameHarmony);
        json.put("divider", ",");
        json.put(JSON_KEY_END_GAME_SPOTLIT, m_endgameSpotLit);
        json.put("divider", ",");
        json.put(JSON_KEY_END_GAME_TRAP, m_endgameTrap);
        json.put("divider", ",");
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

