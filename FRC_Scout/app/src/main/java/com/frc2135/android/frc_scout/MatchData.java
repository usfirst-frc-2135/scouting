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
    private static final String JSON_KEY_NUMBER_PINS = "numberPins";
    private static final String JSON_KEY_NUMBER_ANCHOR = "numberAnchor";
    private static final String JSON_KEY_NUMBER_CAGE = "numberCage";
    private static final String JSON_KEY_NUMBER_BARGE = "numberBarge";
    private static final String JSON_KEY_NUMBER_REEF = "numberReef";


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

    private int m_autonCoralL1;
    private int m_autonCoralL2;
    private int m_autonCoralL3;
    private int m_autonCoralL4;
    private int m_autonAlgaeNet;
    private int m_autonAlgaeProcessor;
    private boolean m_autonLeaveStartingZone;


    private boolean m_pickUpCoral;
    private boolean m_pickUpAlgae;
    private int m_playedDefense;
    private int m_pinFoul;
    private int m_anchorFoul;
    private int m_cageFoul;
    private int m_bargeFoul;
    private int m_teleopReefFoul;
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

        setAutonCoralL1(0);
        setAutonCoralL2(0);
        setAutonCoralL3(0);
        setAutonCoralL4(0);
        setAutonAlgaeNet(0);
        setAutonAlgaeProcessor(0);


        setPickUpCoral(false);
        setPickUpAlgae(false);
        setPlayedDefense(0);
        setFoulPin(0);
        setFoulAnchor(0);
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

        setAutonCoralL1(json.getInt(JSON_KEY_AUTON_CORAL_L1));
        setAutonCoralL2(json.getInt(JSON_KEY_AUTON_CORAL_L2));
        setAutonCoralL3(json.getInt(JSON_KEY_AUTON_CORAL_L3));
        setAutonCoralL4(json.getInt(JSON_KEY_AUTON_CORAL_L4));
        setAutonAlgaeNet(json.getInt(JSON_KEY_AUTON_ALGAE_NET));
        setAutonAlgaeProcessor(json.getInt(JSON_KEY_AUTON_ALGAE_PROCESSOR));

        setPickUpCoral(json.getBoolean(JSON_KEY_PICK_UP_CORAL));
        setPickUpAlgae(json.getBoolean(JSON_KEY_PICK_UP_CORAL));
        setCoralAcquire(json.getInt(JSON_KEY_CORAL_ACQUIRE));
        setTeleopAmpMisses(json.getInt(JSON_KEY_TELEOP_AMP_MISSES));
        setAlgaeAcquire(json.getInt(JSON_KEY_ALGAE_ACQUIRE));
        setTeleopSpeakerMisses(json.getInt(JSON_KEY_TELEOP_SPEAKER_MISSES));
        setTeleopPasses(json.getInt(JSON_KEY_TELEOP_PASSES));
        setFoulPin(json.getInt(JSON_KEY_NUMBER_PINS));
        setFoulAnchor(json.getInt(JSON_KEY_NUMBER_ANCHOR));
        setFoulCage(json.getInt(JSON_KEY_NUMBER_CAGE));
        setFoulBarge(json.getInt(JSON_KEY_NUMBER_BARGE));
        setFoulReef(json.getInt(JSON_KEY_NUMBER_REEF));


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
        return m_teleopReefFoul;
    }

    public void setFoulReef(int numberReef)
    {

        m_teleopReefFoul= numberReef;
    }

    public int getFoulReef()
    {
        return m_teleopReefFoul;
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

        String headers = "TeamNumber AutonLeaveStartingZone AutonCoralL1 AutonCoralL2 AutonCoralL3 AutonCoralL4 AutonSpeakerNotes AutonSpeakerMisses Pick Up Coral TeleopAmpNotes TeleopAmpMisses TeleopSpeakerNotes TeleopSpeakerMisses  TeleopPasses EndgameStage EndgameHarmony EndgameSpotlit EndgameTrap Died MatchNum Competition Scout Comment";

        String tsvStr = "";

        // For teamNumber, strip off 'frc' prefix.
        tsvStr += stripTeamNamePrefix(m_teamNumber) + "\t";


        if (m_autonLeaveStartingZone)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else
            tsvStr += "0" + "\t";

        tsvStr += m_autonCoralL1 + "\t";
        tsvStr += m_autonCoralL2 + "\t";
        tsvStr += m_autonCoralL3 + "\t";
        tsvStr += m_autonCoralL4 + "\t";
        tsvStr += m_autonAlgaeNet + "\t";
        tsvStr += m_autonAlgaeProcessor + "\t";

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

        tsvStr += m_playedDefense + "\t";
        tsvStr += m_pinFoul + "\t";
        tsvStr += m_anchorFoul + "\t";
        tsvStr += m_bargeFoul + "\t";
        tsvStr += m_teleopReefFoul + "\t";
        tsvStr += m_cageFoul + "\t";
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

        json.put("headings", "Competition, Team Number, Match Number, Leave Starting Zone, Auton Coral L1, Auton Coral L2, Auton Coral L3, Auton Coral L4, Auton Speaker Notes, Pick Up Coral, Teleop Amp Notes, Teleop Amp Misses, Teleop Speaker Notes, Teleop Speaker Misses, Teleop Passes, Endgame Stage, Endgame Harmony, Endgame Spotlit, Endgame Trap, Died, Comments, Timestamp, MatchID \n");
        json.put(JSON_KEY_EVENT_CODE, m_eventCode);
        json.put("divider", ",");
        json.put(JSON_KEY_TEAM_NUMBER, m_teamNumber);
        json.put("divider", ",");
        json.put(JSON_KEY_MATCH_NUMBER, m_matchNumber);
        json.put("divider", ",");
        json.put("divider", ",");
        json.put(JSON_KEY_LEAVE, m_autonLeaveStartingZone);
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
        json.put(JSON_KEY_NUMBER_PINS, m_pinFoul);
        json.put("divider", ",");
        json.put(JSON_KEY_NUMBER_ANCHOR, m_anchorFoul);
        json.put("divider", ",");
        json.put(JSON_KEY_NUMBER_BARGE, m_bargeFoul);
        json.put("divider", ",");
        json.put(JSON_KEY_NUMBER_REEF, m_teleopReefFoul);
        json.put("divider", ",");
        json.put(JSON_KEY_NUMBER_CAGE, m_cageFoul);
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

