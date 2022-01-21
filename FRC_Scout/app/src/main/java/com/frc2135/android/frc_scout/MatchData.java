package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static androidx.constraintlayout.widget.Constraints.TAG;

////////////// Stats array contents:
// stats[0] - m_name
// stats[1] - m_competition
// stats[2] - m_teamNumber
// stats[3] - m_matchNumber
// stats[4] - m_startPosition
// stats[5] - m_autonHighPoints
// stats[6] - m_autonLowPoints
// stats[7] - m_exitedTarmac
// stats[8] - m_teleopOuterPoints
// stats[9] - m_teleopLowPoints
// stats[10] - m_climbed
// stats[11] - m_extComments
// stats[12] - m_timestamp
// stats[13] - m_died
// If a new stats[] is added, make sure to adjust STATS_SIZE!
/////////////////////////

public class MatchData {
    //Creating private variables for all of the match data that is collected with an addition of an array to hold everything
    private Object[] stats;
    private int m_autonLowPoints;
    private int m_autonHighPoints;
    private int m_teleopLowPoints;
    private int m_teleopOuterPoints;
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
    private static final int STATS_SIZE = 14;

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
        stats = new Object[STATS_SIZE];
        m_name = "";
        m_competition = "compX"; //default
        m_teamNumber = "";
        m_matchNumber = "";
        setStartPosition(0);
        setAutonHighPoints(0);
        setAutonLowPoints(0);
        setExitedTarmac(false);
        setTeleopOuterPoints(0);
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

    ////////////////////////  Main constructor   //////////////////////////////
    public MatchData(JSONObject json) throws JSONException{

        Log.d(TAG, "Matches being created using json data");

        stats = new Object[STATS_SIZE];
        setName(json.getString("scouter name"));
        setCompetition(json.getString("competition"));
        setTeamNumber(json.getString("team number"));
        setMatchNumber(json.getString("match number"));
        setStartPosition(json.getInt("start position"));
        setAutonHighPoints(json.getInt("auton outer points"));
        setAutonLowPoints(json.getInt("auton low points"));
        setExitedTarmac(json.getBoolean("tarmac"));
        setTeleopOuterPoints(json.getInt("teleop outer points"));
        setTeleopLowPoints(json.getInt("teleop low points"));
        setClimb(json.getInt("climb"));
        setExtComments(json.getString("comments"));
        setTimestamp(new Date(json.getString("timestamp")));
        setDied(json.getBoolean("died"));
        m_matchID = json.getString("id1");
    }

    ////////////  m_matchID   /////////////////////
    public String getMatchID(){
        return m_matchID;
    }

    ////////////  m_name   /////////////////////
    public void setName(String n){
        m_name = n.substring(0,1).toUpperCase()+n.substring(1).toLowerCase();
        stats[0] = m_name;
    }

    public String getName(){
        return m_name;
    }

    ////////////  m_competition   /////////////////////
    public void setCompetition(String c){
        m_competition = c.toUpperCase();
        stats[1] = m_competition;
    }

    public String getCompetition(){
        return m_competition;
    }

    ////////////  m_teamNumber   /////////////////////
    public void setTeamNumber(String n){
        stats[2] = n;
        m_teamNumber = n;
    }

    public String getTeamNumber(){
        return m_teamNumber;
    }

    ////////////  m_matchNumber   /////////////////////
    public void setMatchNumber(String n){
        stats[3] = n;
        m_matchNumber = n;
    }

    public String getMatchNumber(){
        return m_matchNumber;
    }

    ////////////  m_startPosition   /////////////////////
    public void setStartPosition(int x){
        stats[4] = x;
        m_startPosition = x;
    }

    public int getStartPosition(){
        return m_startPosition;
    }

    ////////////  m_autonHighPoints   /////////////////////
    public void setAutonHighPoints(int y){
        stats[5]=y;
        m_autonHighPoints = y;
    }

    public int getAutonHighPoints(){
        return m_autonHighPoints;
    }

    ////////////  m_autonLowPoints   /////////////////////
    public void setAutonLowPoints(int x){
        stats[6]=x;
        m_autonLowPoints = x;
    }

    public int getAutonLowPoints(){
        return m_autonLowPoints;
    }

    ////////////  m_exitedTarmac   /////////////////////
    public void setExitedTarmac(boolean x){
        stats[7] = x;
        m_exitedTarmac = x;
    }

    public boolean getExitedTarmac(){
        return m_exitedTarmac;
    }

    ////////////  m_teleopOuterPoints   /////////////////////
    public void setTeleopOuterPoints(int y){
        stats[8]=y;
        m_teleopOuterPoints = y;
    }

    public int getTelopHighPoints(){
        return m_teleopOuterPoints;
    }

    ////////////  m_teleopLowPoints   /////////////////////
    public void setTeleopLowPoints(int x){
        stats[9]=x;
        m_teleopLowPoints = x;
    }

    public int getTeleopLowPoints(){
        return m_teleopLowPoints;
    }


    ////////////  m_climbed   /////////////////////
    public void setClimb(int x){
        stats[10] = x;
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
       stats[11]= y;
       m_extComments = y;
    }

    public String getExtComments(){
        return m_extComments;
    }

    ////////////  m_timestamp   /////////////////////
    public void setTimestamp(Date d){
        m_timestamp = d;
        stats[12] = m_timestamp;
    }

    public Date getTimestamp() {
        return m_timestamp;
    }


    ////////////  m_died   /////////////////////
    public void setDied(boolean x){
        stats[13] = x;
        m_died = x;
    }

    public boolean getDied(){
        return m_died;
    }

    //Returns current array of match data
    public Object[] getStats(){
        return stats;
    }

    public String encodeToTSV(){
        // The order is important! 
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
        message += m_teleopOuterPoints + "\t";

        message += m_climbed + "\t";

        if(m_died)
            message += "1" + "\t";
        else message += "0" + "\t";

        Log.d(TAG,"MatchData encodeToTSV(): " + message.toString());
        return message;
    }

    public JSONObject toJSON() throws JSONException {
        //This code uses the JSON class to convert the aspects of each match into data that can be saved to a file as JSON
        JSONObject json = new JSONObject();

        json.put("divider", ",");
        json.put("scouter name", m_name);
        json.put("divider", ",");
        json.put("divider", ", \n");


        json.put("headings", "Competition, Team Number, Match Number, Start Position, Auton High Hub, Auton Lower Hub, Exited Tarmac, Teleop Low Port, Teleop Outer Port, Climbed, Died, Comments, Timestamp, MatchID \n");
        json.put("competition", m_competition);
        json.put("divider", ",");
        json.put("team number", m_teamNumber);
        json.put("divider", ",");
        json.put("match number", m_matchNumber);
        json.put("divider", ",");
        json.put("start position", m_startPosition);
        json.put("divider", ",");
        json.put("auton outer points", m_autonHighPoints );
        json.put("divider", ",");
        json.put("auton low points", m_autonLowPoints);
        json.put("divider", ",");
        json.put("tarmac", m_exitedTarmac);
        json.put("divider", ",");
        json.put("teleop low points", m_teleopLowPoints);
        json.put("divider", ",");
        json.put("teleop outer points", m_teleopOuterPoints);
        json.put("divider", ",");
        json.put("climbed", m_climbed);
        json.put("divider", ",");
        json.put("died", m_died);
        json.put("divider", ",");
        json.put("comments", m_extComments);
        json.put("divider", ",");
        json.put("timestamp", m_timestamp);
        json.put("divider", ",");
        json.put("id1", m_matchID);
        return json;
    }

    public String getMatchFileName(){
        return m_matchID+".json";
    }
}
