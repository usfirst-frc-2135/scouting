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
    private static final String JSON_KEY_AUTON_AMP_NOTES = "autonAmpNotes";
    private static final String JSON_KEY_AUTON_AMP_MISSES = "autonAmpMisses";
    private static final String JSON_KEY_AUTON_SPEAKER_NOTES = "autonSpeakerNotes";
    private static final String JSON_KEY_AUTON_SPEAKER_MISSES = "autonSpeakerMisses";


    private static final String JSON_KEY_PICK_UP_CORAL = "pickUpCoral";
    private static final String JSON_KEY_CORAL_ACQUIRE= "coralAcquire";
    private static final String JSON_KEY_ALGAE_ACQUIRE = "algaeAcquire";
    private static final String JSON_KEY_TELEOP_AMP_MISSES = "teleopAmpMisses";
    private static final String JSON_KEY_TELEOP_SPEAKER_MISSES = "teleopSpeakerMisses";
    private static final String JSON_KEY_TELEOP_PASSES = "teleopPasses";
    private static final String JSON_KEY_PICK_UP_ALGAE = "pickUpAlgae";
    private static final String JSON_KEY_KNOCK_OFF_ALGAE = "knockOffAlgae";
    private static final String JSON_KEY_ALGAE_FROM_REEF = "algaeFromReef";
    private static final String JSON_KEY_HOLD_BOTH_ELEMENTS = "holdBothElements";
    private static final String JSON_KEY_PLAYED_DEFENSE = "playedDefense";


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

    // Data members 

    private int m_autonAmpNotes;
    private int m_autonAmpMisses;
    private int m_autonSpeakerNotes;
    private int m_autonSpeakerMisses;
    private boolean m_autonLeaveStartingZone;


    private boolean m_pickUpCoral;
    private boolean m_pickUpAlgae;
    private boolean m_playedDefense;
    private boolean m_knockAlgaeOff;
    private boolean m_algaeFromReef;
    private boolean m_holdBothElements;
    private int m_coralAcquire;
    private int m_algaeAcquire;
    private int m_teleopAmpMisses;
    private int m_teleopSpeakerMisses;
    private int m_teleopPasses;


    private int m_endgameStage;
    private int m_endgameHarmony; //new
    private boolean m_endgameSpotLit; //new
    private boolean m_endgameTrap; //new


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

        setAutonAmpNotes(0);
        setAutonAmpMisses(0);
        setAutonSpeakerNotes(0);
        setAutonSpeakerMisses(0);


        setPickUpCoral(false);
        setPickUpAlgae(false);
        setCoralAcquire(0);
        setTeleopAmpMisses(0);
        setAlgaeAcquire(0);
        setTeleopSpeakerMisses(0);
        setTeleopPasses(0);


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

        setAutonLeave(json.getBoolean(JSON_KEY_LEAVE));

        setAutonAmpNotes(json.getInt(JSON_KEY_AUTON_AMP_NOTES));
        setAutonAmpMisses(json.getInt(JSON_KEY_AUTON_AMP_MISSES));
        setAutonSpeakerNotes(json.getInt(JSON_KEY_AUTON_SPEAKER_NOTES));
        setAutonSpeakerMisses(json.getInt(JSON_KEY_AUTON_SPEAKER_MISSES));

        setPickUpCoral(json.getBoolean(JSON_KEY_PICK_UP_CORAL));
        setPickUpAlgae(json.getBoolean(JSON_KEY_PICK_UP_CORAL));
        setCoralAcquire(json.getInt(JSON_KEY_CORAL_ACQUIRE));
        setTeleopAmpMisses(json.getInt(JSON_KEY_TELEOP_AMP_MISSES));
        setAlgaeAcquire(json.getInt(JSON_KEY_ALGAE_ACQUIRE));
        setTeleopSpeakerMisses(json.getInt(JSON_KEY_TELEOP_SPEAKER_MISSES));
        setTeleopPasses(json.getInt(JSON_KEY_TELEOP_PASSES));

        setEndgameStage(json.getInt(JSON_KEY_END_GAME_STAGE));

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




    public void setAutonAmpNotes(int numNotes)
    {
        m_autonAmpNotes = numNotes;
    }

    public int getAutonAmpNotes()
    {
        return m_autonAmpNotes;
    }
    public void setAutonAmpMisses(int numMisses)
    {
        m_autonAmpMisses = numMisses;
    }

    public int getAutonAmpMisses()
    {
        return m_autonAmpMisses;
    }

    public void setAutonSpeakerNotes(int a)
    {
        m_autonSpeakerNotes = a;
    }

    public int getAutonSpeakerNotes()
    {
        return m_autonSpeakerNotes;
    }

    public void setAutonSpeakerMisses(int aMisses)
    {
        m_autonSpeakerMisses = aMisses;
    }

    public int getAutonSpeakerMisses()
    {
        return m_autonSpeakerMisses;
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
    public void setPlayedDefense(boolean playedDefense)
    {

        m_playedDefense = playedDefense;
    }

    public boolean getPlayedDefense()
    {
        return m_playedDefense;
    }

    public void setCoralAcquire(int coralAcquire)
    {
        m_coralAcquire = coralAcquire;
    }

    public int getCoralAcquire()
    {
        return m_coralAcquire;
    }


    public void setTeleopAmpMisses(int numNotes)
    {
        m_teleopAmpMisses = numNotes;
    }

    public int getTeleopAmpMisses()
    {
        return m_teleopAmpMisses;
    }


    public void setAlgaeAcquire(int algaeAcquire)
    {
        m_algaeAcquire = algaeAcquire;
    }

    public int getAlgaeAcquire()
    {
        return m_algaeAcquire;
    }


    public void setTeleopSpeakerMisses(int a)
    {
        m_teleopSpeakerMisses = a;
    }

    public int getTeleopSpeakerMisses()
    {
        return m_teleopSpeakerMisses;
    }

    public void setTeleopPasses(int numPasses)
    {
        m_teleopPasses = numPasses;
    }

    public int getTeleopPasses()
    {
        return m_teleopPasses;
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


    public String encodeToTSV()
    {
        // NOTE! THE ORDER IS IMPORTANT!
        // This is the data that goes into the QR code.

        String headers = "TeamNumber AutonLeaveStartingZone AutonAmpNotes AutonAmpMisses AutonSpeakerNotes AutonSpeakerMisses Pick Up Coral TeleopAmpNotes TeleopAmpMisses TeleopSpeakerNotes TeleopSpeakerMisses  TeleopPasses EndgameStage EndgameHarmony EndgameSpotlit EndgameTrap Died MatchNum Competition Scout Comment";

        String tsvStr = "";

        // For teamNumber, strip off 'frc' prefix.
        tsvStr += stripTeamNamePrefix(m_teamNumber) + "\t";


        if (m_autonLeaveStartingZone)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";

        tsvStr += m_autonAmpNotes + "\t";
        tsvStr += m_autonAmpMisses + "\t";
        tsvStr += m_autonSpeakerNotes + "\t";
        tsvStr += m_autonSpeakerMisses + "\t";

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

        if (m_playedDefense)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";

        tsvStr += m_coralAcquire + "\t";
        tsvStr += m_teleopAmpMisses + "\t";
        tsvStr += m_algaeAcquire + "\t";
        tsvStr += m_teleopSpeakerMisses + "\t";
        tsvStr += m_teleopPasses + "\t";

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
        JSONObject json = new JSONObject();

        json.put("divider", ",");
        json.put(JSON_KEY_SCOUT_NAME, m_name);  // Scout name
        json.put("divider", ",");
        json.put("divider", ", \n");

        json.put("headings", "Competition, Team Number, Match Number, Leave Starting Zone, Auton Amp Notes, Auton Amp Misses, Auton Speaker Notes, Pick Up Coral, Teleop Amp Notes, Teleop Amp Misses, Teleop Speaker Notes, Teleop Speaker Misses, Teleop Passes, Endgame Stage, Endgame Harmony, Endgame Spotlit, Endgame Trap, Died, Comments, Timestamp, MatchID \n");
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
        json.put(JSON_KEY_AUTON_AMP_MISSES, m_autonAmpMisses);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_SPEAKER_NOTES, m_autonSpeakerNotes);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTON_SPEAKER_MISSES, m_autonSpeakerMisses);
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
        json.put(JSON_KEY_PLAYED_DEFENSE, m_playedDefense);
        json.put("divider", ",");
        json.put(JSON_KEY_CORAL_ACQUIRE, m_coralAcquire);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOP_AMP_MISSES, m_teleopAmpMisses);
        json.put("divider", ",");
        json.put(JSON_KEY_ALGAE_ACQUIRE, m_algaeAcquire);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOP_SPEAKER_MISSES, m_teleopSpeakerMisses);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOP_PASSES, m_teleopPasses);
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

