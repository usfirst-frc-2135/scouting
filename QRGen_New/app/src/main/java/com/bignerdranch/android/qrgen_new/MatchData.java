package com.bignerdranch.android.qrgen_new;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MatchData {
    //Creating private variables for all of the match data that is collected with an addition of an array to hold everything
    private Object[] stats;
    private int autonLowPoints;
    private int autonOuterPoints;
    private int teleopLowPoints;
    private int teleopOuterPoints;
    private int defense;
    private boolean passedInitLine;
    private String extComments;
    private String name;
    private String date;
    private String teamNumber;
    private String matchNumber;
    private boolean climbed;
    private UUID matchID;
    private String competition;
    private Date timestamp;

    public MatchData(Context c){

        //Initializes/constructs everything
        stats = new Object[13];
        setAutonLowPoints(0);
        setAutonOuterPoints(0);
        setTeleopLowPoints(0);
        setTeleopOuterPoints(0);
        setPassedInitLine(false);
        setClimb(false);
        setExtComments("");
        setDefense(0);
        name = Scouter.get(c).getName();
        date = Scouter.get(c).getDate();
        teamNumber = "";
        matchNumber = "";
        matchID = UUID.randomUUID();
        competition = Scouter.get(c).getCompetition();
        timestamp = null;

    }

    public MatchData(JSONObject json) throws JSONException{

        Log.d(TAG, "Matches being created using json data");

        stats = new Object[12];
        matchID = UUID.randomUUID();
        setAutonOuterPoints(json.getInt("auton outer points"));
        setAutonLowPoints(json.getInt("auton low points"));
        setPassedInitLine(json.getBoolean("init_line"));
        setDefense(json.getInt("defense"));
        setExtComments(json.getString("comments"));
        setTeleopLowPoints(json.getInt("teleop low points"));
        setTeleopOuterPoints(json.getInt("teleop outer points"));
        setClimb(json.getBoolean("climbed"));
        setTeamNumber(json.getString("team number"));
        setMatchNumber(json.getString("match number"));
        setTimestamp(new Date(json.getString("timestamp")));
        //setCompetition(json.getString("competition"));   Remember to change once sign out is possible
        name = json.getString("scouter name");
        stats[0] = name;
        date = json.getString("scouting date");
        stats[1] = date;
    }

    //Sets LowPoints to given number
    public void setAutonLowPoints(int x){
        stats[5]=x;
        autonLowPoints = x;
    }

    //Sets HighPoints to given number
    public void setAutonOuterPoints(int y){
        stats[4]=y;
        autonOuterPoints = y;
    }

    //Sets LowPoints to given number
    public void setTeleopLowPoints(int x){
        stats[8]=x;
        teleopLowPoints = x;
    }

    //Sets HighPoints to given number
    public void setTeleopOuterPoints(int y){
        stats[7]=y;
        teleopOuterPoints = y;
    }

    //Sets passedInitLine to given boolean: true for passed, false for no
    public void setPassedInitLine(boolean x){
        stats[6] = x;
        passedInitLine = x;
    }

    //Returns current status of passedInitLine
    public boolean getPassedInitLine(){
        return passedInitLine;
    }

    public void setClimb(boolean x){
        stats[9] = x;
        climbed = x;
    }

    public boolean getClimb(){
        return climbed;
    }

    //Sets defense to an given integer between 0 and 5
    public void setDefense(int x){
        stats[10] = x;
        defense = x;
    }

    //Returns current status of defense
    public int getDefense(){
        return defense;
    }

    //Sets extComments to given String
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
       extComments = y;
    }

    //Returns current status of extComments
    public String getExtComments(){
        return extComments;
    }

    //Returns current status of lowPoints
    public int getAutonLowPoints(){
        return autonLowPoints;
    }

    //Returns current status of highPoints
    public int getAutonHighPoints(){
        return autonOuterPoints;
    }

    //Returns current status of lowPoints
    public int getTeleopLowPoints(){
        return teleopLowPoints;
    }

    //Returns current status of highPoints
    public int getTelopHighPoints(){
        return teleopOuterPoints;
    }

    //Returns current array of match data
    public Object[] getStats(){
        return stats;
    }

    public String getName(){

        return name;
    }

    public String getDate(){
        return date;
    }

    public void setTeamNumber(String n){
        stats[2] = n;
        teamNumber = n;
    }

    public String getTeamNumber(){
        return teamNumber;
    }

    public void setMatchNumber(String n){
        stats[3] = n;
        matchNumber = n;
    }

    public String getMatchNumber(){
        return matchNumber;
    }

    public UUID getMatchID(){
        return matchID;
    }

    public void setCompetition(String x){
        competition = x;
    }

    public String getCompetition(){
        return competition;
    }
    public void setTimestamp(Date d){
        timestamp = d;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String encodeToTSV(){
        String message = "";
        for(Object x: stats){
            message += (x + "\t");
        }
        return message;
    }


    //Purges the record of everything, setting all variables and arrays to initial values
    public void clearStats(){
        setExtComments(null);
        setDefense(0);
        setPassedInitLine(false);
        setAutonOuterPoints(0);
        setAutonLowPoints(0);
    }

    public JSONObject toJSON() throws JSONException {
        //This code uses the JSON class to convert the aspects of each crime into data that can be to a file as JSON
        JSONObject json = new JSONObject();

        json.put("scouter name", name);
        json.put("divider", ",");
        json.put("scouting date", date);
        json.put("divider", ", \n");


        json.put("headings", "Competition, Team Number, Match Number, Auton Outer Port, Auton Lower Port, Crossed Initiation Line, Teleop Outer Port, Teleop Lower Port, Climbed, Defense Rating, Additional Comments \n");
        json.put("competition", competition);
        json.put("divider", ",");
        json.put("team number", teamNumber);
        json.put("divider", ",");
        json.put("match number", matchNumber);
        json.put("divider", ",");
        json.put("auton outer points", autonOuterPoints );
        json.put("divider", ",");
        json.put("auton low points", autonLowPoints);
        json.put("divider", ",");
        json.put("init_line", passedInitLine);
        json.put("divider", ",");
        json.put("teleop low points", teleopLowPoints);
        json.put("divider", ",");
        json.put("teleop outer points", teleopOuterPoints);
        json.put("divider", ",");
        json.put("climbed", climbed);
        json.put("divider", ",");
        json.put("divider", ",");
        json.put("defense", defense);
        json.put("divider", ",");
        json.put("comments", extComments);
        json.put("divider", ",");
        json.put("timestamp", timestamp);
        return json;
    }


}
