package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MatchData {
    private static final String TAG = "MatchData";

    // Keys used for reading/writing match JSON files.
    private static final String JSON_KEY_SCOUTNAME = "scoutname";
    private static final String JSON_KEY_COMPETITION = "competition";
    private static final String JSON_KEY_TEAMNUMBER = "teamnumber";
    private static final String JSON_KEY_MATCHNUMBER = "matchnumber";

    private static final String JSON_KEY_MOBILITY = "mobility";
    private static final String JSON_KEY_AUTONCONESBOTTOMROW = "autonconesbottomrow";
    private static final String JSON_KEY_AUTONCONESMIDDLEROW = "autonconesmiddlerow";
    private static final String JSON_KEY_AUTONCONESTOPROW = "autonconestoprow";
    private static final String JSON_KEY_AUTONCUBESBOTTOMROW = "autoncubesbottomrow";
    private static final String JSON_KEY_AUTONCUBESMIDDLEROW = "autoncubesmiddlerow";
    private static final String JSON_KEY_AUTONCUBESTOPROW = "autoncubestoprow";
    private static final String JSON_KEY_AUTONCHARGE = "autoncharge";


    private static final String JSON_KEY_TELEOPCONESBOTTOMROW = "teleopconesbottomrow";
    private static final String JSON_KEY_TELEOPCONESMIDDLEROW = "teleopconesmiddlerow";
    private static final String JSON_KEY_TELEOPCONESTOPROW = "teleopconestoprow";
    private static final String JSON_KEY_TELEOPCUBESBOTTOMROW = "teleopcubesbottomrow";
    private static final String JSON_KEY_TELEOPCUBESMIDDLEROW = "teleopcubesmiddlerow";
    private static final String JSON_KEY_TELEOPCUBESTOPROW = "teleopcubestoprow";


    private static final String JSON_KEY_ENDGAMECHARGE = "endgamecharge";
    private static final String JSON_KEY_COMMENTS = "comments";
    private static final String JSON_KEY_TIMESTAMP = "timestamp";
    private static final String JSON_KEY_DIED = "died";
    private static final String JSON_KEY_MATCHID = "matchid";
    private static final String JSON_KEY_PICKEDUPCUBE = "pickedupcube";
    private static final String JSON_KEY_PICKEDUPUPRIGHT = "pickedupupright";
    private static final String JSON_KEY_PICKEDUPTIPPED = "pickeduptipped";

    // Data members 
    private int     m_autonConesBottomRow;
    private int     m_autonConesMiddleRow;
    private int     m_autonConesTopRow;
    private int     m_autonCubesBottomRow;
    private int     m_autonCubesMiddleRow;
    private int     m_autonCubesTopRow;
    private int     m_autonChargeLevel;

    private int     m_teleopConesBottomRow;
    private int     m_teleopConesMiddleRow;
    private int     m_teleopConesTopRow;
    private int     m_teleopCubesBottomRow;
    private int     m_teleopCubesMiddleRow;
    private int     m_teleopCubesTopRow;


    private int     m_endgameChargeLevel;

    private boolean m_exitedCommunity;
    private String  m_comment;
    private boolean m_died;
    private String  m_name;
    private String  m_teamNumber;
    private String  m_matchNumber;
    private String  m_matchID;
    private String  m_competition;
    private Date    m_timestamp;
    private boolean m_pickedUpCube;
    private boolean m_pickedUpUpright;
    private boolean m_pickedUpTipped;

    // Utility to strip off "frc" prefix to team number.
    public String stripTeamNamePrefix(String teamName){
        String newTeamName = "";
        for(int i = 0; i < teamName.length(); i++){
                if(Character.isDigit(teamName.charAt(i)))
                    newTeamName += teamName.charAt(i);
            }
            return newTeamName;
        }


    ////////////////////////  Default constructor   //////////////////////////////
    public MatchData(Context context) throws IOException, JSONException {

        m_name = "";
        m_teamNumber = "";
        m_matchNumber = "";
        setExitedCommunity(false);
        setAutonConesBottomRow(0);
        setAutonConesMiddleRow(0);
        setAutonConesTopRow(0);
        setAutonCubesBottomRow(0);
        setAutonCubesMiddleRow(0);
        setAutonCubesTopRow(0);
        setAutonChargeLevel(0);

        setTeleopConesBottomRow(0);
        setTeleopConesMiddleRow(0);
        setTeleopConesTopRow(0);
        setTeleopCubesBottomRow(0);
        setTeleopCubesMiddleRow(0);
        setTeleopCubesTopRow(0);

        setEndgameChargeLevel(0);
        setComment("");
        setTimestamp(Calendar.getInstance().getTime());
        setDied(false);

        m_matchID = UUID.randomUUID()+"";

        m_competition = CurrentCompetition.get(context).getEventCode();
        Log.d(TAG,"Default constructor m_competition set to "+m_competition.toString());
    }

    //////////////////////// constructor from JSON file  //////////////////////////////
    public MatchData(JSONObject json) throws JSONException {

        Log.d(TAG, "MatchData being created using json data");

        setName(json.getString(JSON_KEY_SCOUTNAME));
        setCompetition(json.getString(JSON_KEY_COMPETITION));
        setTeamNumber(json.getString(JSON_KEY_TEAMNUMBER));
        setMatchNumber(json.getString(JSON_KEY_MATCHNUMBER));

        setExitedCommunity(json.getBoolean(JSON_KEY_MOBILITY));

        setAutonConesBottomRow(json.getInt(JSON_KEY_AUTONCONESBOTTOMROW));
        setAutonConesMiddleRow(json.getInt(JSON_KEY_AUTONCONESMIDDLEROW));
        setAutonConesTopRow(json.getInt(JSON_KEY_AUTONCONESTOPROW));
        setAutonCubesBottomRow(json.getInt(JSON_KEY_AUTONCUBESBOTTOMROW));
        setAutonCubesMiddleRow(json.getInt(JSON_KEY_AUTONCUBESMIDDLEROW));
        setAutonCubesTopRow(json.getInt(JSON_KEY_AUTONCUBESTOPROW));
        setAutonChargeLevel(json.getInt(JSON_KEY_AUTONCHARGE));

        setTeleopConesBottomRow(json.getInt(JSON_KEY_TELEOPCONESBOTTOMROW));
        setTeleopConesMiddleRow(json.getInt(JSON_KEY_TELEOPCONESMIDDLEROW));
        setTeleopConesTopRow(json.getInt(JSON_KEY_TELEOPCONESTOPROW));
        setTeleopCubesBottomRow(json.getInt(JSON_KEY_TELEOPCUBESBOTTOMROW));
        setTeleopCubesMiddleRow(json.getInt(JSON_KEY_TELEOPCUBESMIDDLEROW));
        setTeleopCubesTopRow(json.getInt(JSON_KEY_TELEOPCUBESTOPROW));

        setPickedUpCube(json.getBoolean(JSON_KEY_PICKEDUPCUBE));
        setPickedUpUpright(json.getBoolean(JSON_KEY_PICKEDUPUPRIGHT));
        setPickedUpTipped(json.getBoolean(JSON_KEY_PICKEDUPTIPPED));

        setEndgameChargeLevel(json.getInt(JSON_KEY_ENDGAMECHARGE));
        setComment(json.getString(JSON_KEY_COMMENTS));
        setTimestamp(new Date(json.getString(JSON_KEY_TIMESTAMP)));
        setDied(json.getBoolean(JSON_KEY_DIED));
        m_matchID = json.getString(JSON_KEY_MATCHID);
    }

    ////////////  m_matchID   /////////////////////
    public String getMatchID(){
        return m_matchID;
    }

    ////////////  m_name   /////////////////////
    public void setName(String n){
        m_name = n.substring(0,1).toUpperCase()+n.substring(1).toLowerCase();
    }

    public String getName(){
        return m_name;
    }

    ////////////  m_competition   /////////////////////
    public void setCompetition(String c){
        m_competition = c;
    }

    public String getCompetition(){
        return m_competition;
    }

    ////////////  m_teamNumber   /////////////////////
    public void setTeamNumber(String n){
        m_teamNumber = n;
    }

    public String getTeamNumber(){
        return m_teamNumber;
    }

    ////////////  m_matchNumber   /////////////////////
    public void setMatchNumber(String n){
        m_matchNumber = n;
    }

    public String getMatchNumber(){
        return m_matchNumber;
    }


    ////////////  m_autonConesBottomRow   /////////////////////
    public void setAutonConesBottomRow(int x){
        m_autonConesBottomRow = x;
    }

    public int getAutonConesBottomRow(){
        return m_autonConesBottomRow;
    }

    ////////////  m_autonConesMiddle Row   /////////////////////
    public void setAutonConesMiddleRow(int y){
        m_autonConesMiddleRow = y;
    }

    public int getAutonConesMiddleRow(){
        return m_autonConesMiddleRow;
    }

    ////////////  m_autonConesTop Row   /////////////////////
    public void setAutonConesTopRow(int z){
        m_autonConesTopRow = z;
    }

    public int getAutonConesTopRow(){
        return m_autonConesTopRow;
    }


    ////////////  m_autonCubesBottomRow   /////////////////////
    public void setAutonCubesBottomRow(int a){
        m_autonCubesBottomRow = a;
    }

    public int getAutonCubesBottomRow(){
        return m_autonCubesBottomRow;
    }

    ////////////  m_autonCubesMiddle Row   /////////////////////
    public void setAutonCubesMiddleRow(int b){
        m_autonCubesMiddleRow = b;
    }

    public int getAutonCubesMiddleRow(){
        return m_autonCubesMiddleRow;
    }

    ////////////  m_autonCubesTop Row   /////////////////////
    public void setAutonCubesTopRow(int c){
        m_autonCubesTopRow = c;
    }

    public int getAutonCubesTopRow(){
        return m_autonCubesTopRow;
    }



    ////////////  m_exitedCommunity   /////////////////////
    public void setExitedCommunity(boolean x){
        m_exitedCommunity = x;
    }

    public boolean getExitedCommunity(){
        return m_exitedCommunity;
    }

    ////////////  m_autonchargestation   /////////////////////
    public void setAutonChargeLevel(int x){
        m_autonChargeLevel = x;
    }

    public int getAutonChargeLevel(){
        return m_autonChargeLevel;
    }



    ////////////  m_teleopConesBottomRow   /////////////////////
    public void setTeleopConesBottomRow(int x){
        m_teleopConesBottomRow = x;
    }

    public int getTeleopConesBottomRow(){
        return m_teleopConesBottomRow;
    }

    ////////////  m_teleopConesMiddle Row   /////////////////////
    public void setTeleopConesMiddleRow(int y){
        m_teleopConesMiddleRow = y;
    }

    public int getTeleopConesMiddleRow(){
        return m_teleopConesMiddleRow;
    }

    ////////////  m_TeleopConesTop Row   /////////////////////
    public void setTeleopConesTopRow(int z){
        m_teleopConesTopRow = z;
    }

    public int getTeleopConesTopRow(){
        return m_teleopConesTopRow;
    }


    ////////////  m_teleopCubesBottomRow   /////////////////////
    public void setTeleopCubesBottomRow(int a){
        m_teleopCubesBottomRow = a;
    }

    public int getTeleopCubesBottomRow(){
        return m_teleopCubesBottomRow;
    }

    ////////////  m_teleopCubesMiddle Row   /////////////////////
    public void setTeleopCubesMiddleRow(int b){
        m_teleopCubesMiddleRow = b;
    }

    public int getTeleopCubesMiddleRow(){
        return m_teleopCubesMiddleRow;
    }

    ////////////  m_teleopCubesTop Row   /////////////////////
    public void setTeleopCubesTopRow(int c){
        m_teleopCubesTopRow = c;
    }

    public int getTeleopCubesTopRow(){
        return m_teleopCubesTopRow;
    }



    ////////////  m_endgamechargestation   /////////////////////
    public void setEndgameChargeLevel(int x){
        m_endgameChargeLevel = x;
    }

    public int getEndgameChargeLevel(){
        return m_endgameChargeLevel;
    }

    ////////////  m_comment   /////////////////////
    public void setComment(String comment){
       m_comment = comment;
    }

    public String getComment(){
        return m_comment;
    }

    ////////////  m_timestamp   /////////////////////
    public void setTimestamp(Date d){
        m_timestamp = d;
    }

    public Date getTimestamp() {
        return m_timestamp;
    }

    ////////////  m_died   /////////////////////
    public void setDied(boolean x){
        m_died = x;
    }

    public boolean getDied(){
        return m_died;
    }

    ////////////  m_pickedUpCube   /////////////////////
    public void setPickedUpCube(boolean x) { m_pickedUpCube = x; }

    public boolean getPickedUpCube() { return m_pickedUpCube; }

    ////////////  m_pickedUpUpright   /////////////////////
    public void setPickedUpUpright(boolean x) { m_pickedUpUpright = x; }

    public boolean getPickedUpUpright() { return m_pickedUpUpright; }

    ////////////  m_pickedUpTipped   /////////////////////
    public void setPickedUpTipped(boolean x) { m_pickedUpTipped = x; }

    public boolean getPickedUpTipped() { return m_pickedUpTipped; }

    public String encodeToTSV(){
        // NOTE! THE ORDER IS IMPORTANT!
        // This is the data that goes into the QR code.

        String headers = "TeamNumber ExitCommunity AutonConesBottom AutonConesMiddle AutonConesTop AutonCubesBottom AutonCubesMiddle AutonCubesTop TeleopConesBottom TeleopConesMiddle TeleopConesTop TeleopCubesBottom TeleopCubesMiddle TeleopCubesTop PickUpCube PickUpUprightCone PickUpTippedCone EndgameChargeLevel Died MatchNum Competition Scout Comment";


        String tsvStr = "";

        // For teamNumber, strip off 'frc' prefix.
        tsvStr += stripTeamNamePrefix(m_teamNumber) +"\t";



        if(m_exitedCommunity)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_autonConesBottomRow + "\t";
        tsvStr += m_autonConesMiddleRow + "\t";
        tsvStr += m_autonConesTopRow + "\t";
        tsvStr += m_autonCubesBottomRow + "\t";
        tsvStr += m_autonCubesMiddleRow + "\t";
        tsvStr += m_autonCubesTopRow + "\t";

        tsvStr += m_autonChargeLevel + "\t";

        tsvStr += m_teleopConesBottomRow + "\t";
        tsvStr += m_teleopConesMiddleRow + "\t";
        tsvStr += m_teleopConesTopRow + "\t";
        tsvStr += m_teleopCubesBottomRow + "\t";
        tsvStr += m_teleopCubesMiddleRow + "\t";
        tsvStr += m_teleopCubesTopRow + "\t";

        if(m_pickedUpCube)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if(m_pickedUpUpright)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        if(m_pickedUpTipped)  // bool value: use 1/0 instead of true/false
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_endgameChargeLevel + "\t";

        if(m_died)
            tsvStr += "1" + "\t";
        else tsvStr += "0" + "\t";

        tsvStr += m_matchNumber + "\t";
        tsvStr += m_competition + "\t";

        tsvStr += m_name + "\t";   // Scout name
        if (!m_comment.equals(""))
          tsvStr += m_comment + "\t";
        else tsvStr += "-" + "\t";

        Log.d(TAG,"MatchData encodeToTSV() columns: " + headers.toString());
        Log.d(TAG,"MatchData encodeToTSV(): " + tsvStr.toString());
        return tsvStr;
    }

    public JSONObject toJSON() throws JSONException {
        //This code uses the JSON class to convert the aspects of each match into data that can be saved to a file as JSON
        JSONObject json = new JSONObject();

        json.put("divider", ",");
        json.put(JSON_KEY_SCOUTNAME, m_name);  // Scout name
        json.put("divider", ",");
        json.put("divider", ", \n");

        json.put("headings", "Competition, Team Number, Match Number, Exit Community, Auton Cones Bottom Row, Auton Cones Middle Row, Auton Cones Top Row, Auton Cubes Bottom Row, Auton Cubes Middle Row, Auton Cubes Top Row, Auton Charge Station, Teleop Cones Bottom Row, Teleop Cones Middle Row, Teleop Cones Top Row, Teleop Cubes Bottom Row, Teleop Cubes Middle Row, Teleop Cubes Top Row, Picked Up Cube, Picked Up Upright Cone, Picked Up Tipped Cone, Endgame Charge Station, Died, Comments, Timestamp, MatchID \n");
        json.put(JSON_KEY_COMPETITION, m_competition);
        json.put("divider", ",");
        json.put(JSON_KEY_TEAMNUMBER, m_teamNumber);
        json.put("divider", ",");
        json.put(JSON_KEY_MATCHNUMBER, m_matchNumber);
        json.put("divider", ",");
        json.put("divider", ",");
        json.put(JSON_KEY_MOBILITY, m_exitedCommunity);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTONCONESBOTTOMROW, m_autonConesBottomRow);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTONCONESMIDDLEROW, m_autonConesMiddleRow );
        json.put("divider", ",");
        json.put(JSON_KEY_AUTONCONESTOPROW, m_autonConesTopRow );
        json.put("divider", ",");
        json.put(JSON_KEY_AUTONCUBESBOTTOMROW, m_autonCubesBottomRow);
        json.put("divider", ",");
        json.put(JSON_KEY_AUTONCUBESMIDDLEROW, m_autonCubesMiddleRow );
        json.put("divider", ",");
        json.put(JSON_KEY_AUTONCUBESTOPROW, m_autonCubesTopRow );
        json.put("divider", ",");
        json.put(JSON_KEY_AUTONCHARGE, m_autonChargeLevel);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOPCONESBOTTOMROW, m_teleopConesBottomRow);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOPCONESMIDDLEROW, m_teleopConesMiddleRow );
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOPCONESTOPROW, m_teleopConesTopRow );
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOPCUBESBOTTOMROW, m_teleopCubesBottomRow);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOPCUBESMIDDLEROW, m_teleopCubesMiddleRow );
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOPCUBESTOPROW, m_teleopCubesTopRow );
        json.put("divider", ",");
        json.put(JSON_KEY_PICKEDUPCUBE, m_pickedUpCube);
        json.put("divider", ",");
        json.put(JSON_KEY_PICKEDUPUPRIGHT, m_pickedUpUpright);
        json.put("divider", ",");
        json.put(JSON_KEY_PICKEDUPTIPPED, m_pickedUpTipped);
        json.put("divider", ",");
        json.put(JSON_KEY_ENDGAMECHARGE, m_endgameChargeLevel);
        json.put("divider", ",");
        json.put(JSON_KEY_DIED, m_died);
        json.put("divider", ",");
        json.put(JSON_KEY_COMMENTS, m_comment);
        json.put("divider", ",");
        json.put(JSON_KEY_TIMESTAMP, m_timestamp);
        json.put("divider", ",");
        json.put(JSON_KEY_MATCHID, m_matchID);
        return json;
    }

    public String getMatchFileName(){
        return m_matchID+".json";
    }

}

