package com.bignerdranch.android.qrgen_new;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class MatchData {
    //Creating private variables for all of the match data that is collected with an addition of an array to hold everything
    private Object[] stats;
    private int lowPoints;
    private int highPoints;
    private int defense;
    private boolean passedInitLine;
    private String extComments;

    public MatchData(){

        //Initializes/constructs everything
        stats = new Object[5];
        setLowPoints(0);
        setHighPoints(0);
        setPassedInitLine(false);
        setExtComments("");
        setDefense(0);

    }

    //Sets LowPoints to given number
    public void setLowPoints(int x){
        stats[1]=x;
        lowPoints = x;
    }

    //Sets HighPoints to given number
    public void setHighPoints(int y){
        stats[0]=y;
        highPoints = y;
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
        stats[4] = "\"" + x + "\"";
        extComments = "\"" + x + "\"";
    }

    //Returns current status of extComments
    public String getExtComments(){
        return extComments;
    }

    //Returns current status of lowPoints
    public int getLowPoints(){
        return lowPoints;
    }

    //Returns current status of highPoints
    public int getHighPoints(){
        return highPoints;
    }

    //Returns current array of match data
    public Object[] getStats(){
        return stats;
    }

    //Provides a string of all match data separated by commas
    public String toString(){
        String message = "";
        for(Object x: stats){
            message += (x + ",  ");
        }
        return message;
    }


    //Purges the record of everything, setting all variables and arrays to initial values
    public void clearStats(){
        setExtComments(null);
        setDefense(0);
        setPassedInitLine(false);
        setHighPoints(0);
        setLowPoints(0);
    }


}
