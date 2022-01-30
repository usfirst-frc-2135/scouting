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
    private static final String JSON_KEY_SCOUTNAME = "scoutname";
    private static final String JSON_KEY_COMPETITION = "competition";
    private static final String JSON_KEY_TEAMNUMBER = "teamnumber";
    private static final String JSON_KEY_MATCHNUMBER = "matchnumber";
    private static final String JSON_KEY_STARTPOS = "startpos";
    private static final String JSON_KEY_AUTONLOWPOINTS = "autonlowpoints";
    private static final String JSON_KEY_AUTONHIGHPOINTS = "autonhighpoints";
    private static final String JSON_KEY_TARMAC = "tarmac";
    private static final String JSON_KEY_TELEOPLOWPOINTS = "teleoplowpoints";
    private static final String JSON_KEY_TELEOPHIGHPOINTS = "teleophighpoints";
    private static final String JSON_KEY_CLIMBED = "climbed";
    private static final String JSON_KEY_COMMENTS = "comments";
    private static final String JSON_KEY_TIMESTAMP = "timestamp";
    private static final String JSON_KEY_DIED = "died";
    private static final String JSON_KEY_MATCHID = "matchid";

    //Creating private variables for all of the match data that is collected 
    private int m_autonLowPoints;
    private int m_autonHighPoints;
    private int m_teleopLowPoints;
    private int m_teleopHighPoints;
    private int m_climbed;
    private int m_startPosition;
    private boolean m_exitedTarmac;
    private String m_extComments;
    private boolean m_died;
    private String m_name;
    private String m_teamNumber;
    private String m_matchNumber;
    private String m_matchID;
    private String m_competition;
    private Date m_timestamp;
    private CompetitionDataSerializer m_competitionDataSerializer;

    public String stripTeamNamePrefix(String teamName){
        String newTeamName = "";
        for(int i = 0; i < teamName.length(); i++){
                if(Character.isDigit(teamName.charAt(i)))
                    newTeamName += teamName.charAt(i);
            }
            return newTeamName;
        }


    ////////////////////////  Default constructor   //////////////////////////////
    public MatchData(Context c) throws IOException, JSONException {

        //Initializes/constructs everything 
        m_name = "";
        m_competition = "compX"; //default
        m_teamNumber = "";
        m_matchNumber = "";
        setStartPosition(0);
        setAutonHighPoints(0);
        setAutonLowPoints(0);
        setExitedTarmac(false);
        setTeleopHighPoints(0);
        setTeleopLowPoints(0);
        setClimb(0);
        setExtComments("");
        setTimestamp(Calendar.getInstance().getTime());
        setDied(false);

        m_matchID = UUID.randomUUID()+"";
        m_competitionDataSerializer = new CompetitionDataSerializer(c, "current_competition.json");
        if(null != m_competitionDataSerializer && null != m_competitionDataSerializer.loadCurrentComp()){
          m_competition = m_competitionDataSerializer.loadCurrentComp().getEventCode();
        }
        Log.d(TAG,"MatchData m_competition set to "+m_competition.toString());
    }

    //////////////////////// constructor from JSON file  //////////////////////////////
    public MatchData(JSONObject json) throws JSONException {

        Log.d(TAG, "MatchData being created using json data");

        setName(json.getString(JSON_KEY_SCOUTNAME));
        setCompetition(json.getString(JSON_KEY_COMPETITION));
        setTeamNumber(json.getString(JSON_KEY_TEAMNUMBER));
        setMatchNumber(json.getString(JSON_KEY_MATCHNUMBER));
        setStartPosition(json.getInt(JSON_KEY_STARTPOS));
        setAutonHighPoints(json.getInt(JSON_KEY_AUTONHIGHPOINTS));
        setAutonLowPoints(json.getInt(JSON_KEY_AUTONLOWPOINTS));
        setExitedTarmac(json.getBoolean(JSON_KEY_TARMAC));
        setTeleopHighPoints(json.getInt(JSON_KEY_TELEOPHIGHPOINTS));
        setTeleopLowPoints(json.getInt(JSON_KEY_TELEOPLOWPOINTS));
        setClimb(json.getInt(JSON_KEY_CLIMBED));
        setExtComments(json.getString(JSON_KEY_COMMENTS));
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
        m_competition = c.toUpperCase();
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

    ////////////  m_startPosition   /////////////////////
    public void setStartPosition(int x){
        m_startPosition = x;
    }

    public int getStartPosition(){
        return m_startPosition;
    }

    ////////////  m_autonHighPoints   /////////////////////
    public void setAutonHighPoints(int y){
        m_autonHighPoints = y;
    }

    public int getAutonHighPoints(){
        return m_autonHighPoints;
    }

    ////////////  m_autonLowPoints   /////////////////////
    public void setAutonLowPoints(int x){
        m_autonLowPoints = x;
    }

    public int getAutonLowPoints(){
        return m_autonLowPoints;
    }

    ////////////  m_exitedTarmac   /////////////////////
    public void setExitedTarmac(boolean x){
        m_exitedTarmac = x;
    }

    public boolean getExitedTarmac(){
        return m_exitedTarmac;
    }

    ////////////  m_teleopHighPoints   /////////////////////
    public void setTeleopHighPoints(int y){
        m_teleopHighPoints = y;
    }

    public int getTelopHighPoints(){
        return m_teleopHighPoints;
    }

    ////////////  m_teleopLowPoints   /////////////////////
    public void setTeleopLowPoints(int x){
        m_teleopLowPoints = x;
    }

    public int getTeleopLowPoints(){
        return m_teleopLowPoints;
    }


    ////////////  m_climbed   /////////////////////
    public void setClimb(int x){
        m_climbed = x;
    }

    public int getClimb(){
        return m_climbed;
    }

    ////////////  m_extComments   /////////////////////
    public void setExtComments(CharSequence x){
       String y = "";
        if(x != null){
           y = x.toString();
           for(int i = 0; i < x.length()-2; i++){
               if(y.charAt(i) == ','){
                   y = y.substring(0, i)+ y.substring(i+1);
                   i++;
               }
           }
       }
       m_extComments = y;
    }

    public String getExtComments(){
        return m_extComments;
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

    public String encodeToTSV(){
        // NOTE! THE ORDER IS IMPORTANT!
        String message = "";

        // For teamNumber, strip off 'frc' prefix.
        message += stripTeamNamePrefix(m_teamNumber) +"\t";

        message += m_startPosition + "\t";

        if(m_exitedTarmac)  // bool value: use 1/0 instead of true/false
            message += "1" + "\t";
        else message += "0" + "\t";

        message += m_autonLowPoints + "\t";
        message += m_autonHighPoints + "\t";
        message += m_teleopLowPoints + "\t";
        message += m_teleopHighPoints + "\t";

        message += m_climbed + "\t";

        if(m_died)
            message += "1" + "\t";
        else message += "0" + "\t";

        message += m_matchNumber + "\t";
        message += m_competition + "\t";

        Log.d(TAG,"MatchData encodeToTSV(): " + message.toString());
        return message;
    }

    public JSONObject toJSON() throws JSONException {
        //This code uses the JSON class to convert the aspects of each match into data that can be saved to a file as JSON
        JSONObject json = new JSONObject();

        json.put("divider", ",");
        json.put(JSON_KEY_SCOUTNAME, m_name);
        json.put("divider", ",");
        json.put("divider", ", \n");

        json.put("headings", "Competition, Team Number, Match Number, Start Position, Auton High Points, Auton Low Points, Exited Tarmac, Teleop Low Port, Teleop Outer Port, Climbed, Died, Comments, Timestamp, MatchID \n");
        json.put(JSON_KEY_COMPETITION, m_competition);
        json.put("divider", ",");
        json.put(JSON_KEY_TEAMNUMBER, m_teamNumber);
        json.put("divider", ",");
        json.put(JSON_KEY_MATCHNUMBER, m_matchNumber);
        json.put("divider", ",");
        json.put(JSON_KEY_STARTPOS, m_startPosition );
        json.put("divider", ",");
        json.put(JSON_KEY_AUTONHIGHPOINTS, m_autonHighPoints );
        json.put("divider", ",");
        json.put(JSON_KEY_AUTONLOWPOINTS, m_autonLowPoints);
        json.put("divider", ",");
        json.put(JSON_KEY_TARMAC, m_exitedTarmac);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOPLOWPOINTS, m_teleopLowPoints);
        json.put("divider", ",");
        json.put(JSON_KEY_TELEOPHIGHPOINTS, m_teleopHighPoints);
        json.put("divider", ",");
        json.put(JSON_KEY_CLIMBED, m_climbed);
        json.put("divider", ",");
        json.put(JSON_KEY_DIED, m_died);
        json.put("divider", ",");
        json.put(JSON_KEY_COMMENTS, m_extComments);
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
