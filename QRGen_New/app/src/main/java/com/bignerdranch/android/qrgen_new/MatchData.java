package com.bignerdranch.android.qrgen_new;

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
    private UUID mId;
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
    private int matchNumber;
    private boolean climbed;

    public MatchData(){

        //Initializes/constructs everything
        mId = UUID.randomUUID();
        stats = new Object[5];
        setAutonLowPoints(0);
        setAutonOuterPoints(0);
        setTeleopLowPoints(0);
        setTeleopOuterPoints(0);
        setPassedInitLine(false);
        setClimb(false);
        setExtComments("");
        setDefense(0);
        name = Scouter.get().getName();
        date = Scouter.get().getDate();
        teamNumber = "";
        matchNumber = 0;

    }

    public MatchData(JSONObject json) throws JSONException{
        stats = new Object[5];
        mId = UUID.randomUUID();
        setAutonOuterPoints(json.getInt("auton outer points"));
        setAutonLowPoints(json.getInt("auton low points"));
        setPassedInitLine(json.getBoolean("init_line"));
        setDefense(json.getInt("defense"));
        setExtComments(json.getString("comments"));
        setTeleopLowPoints(json.getInt("teleop low points"));
        setTeleopOuterPoints(json.getInt("teleop outer points"));
        setClimb(json.getBoolean("climbed"));
        setTeamNumber(json.getString("team number"));
        setMatchNumber(json.getInt("match number"));
        name = json.getString("scouter name");
        date = json.getString("scouting date");
    }

    //Sets LowPoints to given number
    public void setAutonLowPoints(int x){
        stats[1]=x;
        autonLowPoints = x;
    }

    //Sets HighPoints to given number
    public void setAutonOuterPoints(int y){
        stats[0]=y;
        autonOuterPoints = y;
    }

    //Sets LowPoints to given number
    public void setTeleopLowPoints(int x){
        stats[1]=x;
        teleopLowPoints = x;
    }

    //Sets HighPoints to given number
    public void setTeleopOuterPoints(int y){
        stats[0]=y;
        teleopOuterPoints = y;
    }

    //Sets passedInitLine to given boolean: true for passed, false for no
    public void setPassedInitLine(boolean x){
        stats[2] = x;
        passedInitLine = x;
    }

    //Returns current status of passedInitLine
    public boolean getPassedInitLine(){
        return passedInitLine;
    }

    public void setClimb(boolean x){
        climbed = x;
    }

    public boolean getClimb(){
        return climbed;
    }

    //Sets defense to an given integer between 0 and 5
    public void setDefense(int x){
        stats[3] = x;
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

       stats[4]= y;
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
        Log.d(TAG, name);
        return name;
    }

    public String getDate(){
        return date;
    }

    public void setTeamNumber(String n){
        teamNumber = n;
    }

    public String getTeamNumber(){
        return teamNumber;
    }

    public void setMatchNumber(int n){
        matchNumber = n;
    }

    public int getMatchNumber(){
        return matchNumber;
    }


    //Provides a string of all match data separated by commas
    /*public String toString(){

        String message = "High Points, Low Points, Crossed Initiation Line, Defense Rating, Additional Comments \n";
        for(Object x: stats){
            message += (x + ",");
        }
        return message;
    }*/

    public String encodeToURL(){
        String message = "High Points%2C%20Low Points%2C%20Crossed Initiation Line%2C%20Defense Rating%2C%20Additional Comments%7C";
        for(Object x: stats){
            message += (x + "%2C%20");
        }
        return message;
    }

    /*public String dataNums(){
        String message = "";
        for(Object x: stats){
            message += (x + ",");
        }
        return message;
    }*/

    public UUID getId(){
        return mId;
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
        json.put("headings", "High Points,Low Points,Crossed Initiation Line,Defense Rating,Additional Comments \n");
        json.put("auton outer points", autonOuterPoints );
        json.put("divider", ",");
        json.put("auton low points", autonLowPoints);
        json.put("divider", ",");
        json.put("init_line", passedInitLine);
        json.put("divider", ",");
        json.put("defense", defense);
        json.put("divider", ",");
        json.put("comments", extComments);
        json.put("divider", ",");
        json.put("teleop low point", teleopLowPoints);
        json.put("divider", ",");
        json.put("teleop outer points", teleopOuterPoints);
        json.put("divider", ",");
        json.put("team number", teamNumber);
        json.put("divider", ",");
        json.put("match number", matchNumber);
        json.put("divider", ",");
        json.put("climbed", climbed);
        json.put("divider", ",");
        json.put("scouter name", name);
        json.put("scouting date", date);
    json.put("divider", ",");
        return json;
    }


}
