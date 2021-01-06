package com.bignerdranch.android.qrgen_new;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class Scouter {

    private String mScouterName;
    private String mScoutingDateString;
    private static Scouter sScouter;

    private Scouter(String n, String d){
        mScouterName = n;
        mScoutingDateString= d;
    }

    public static Scouter get(){
        if(sScouter == null) {
            sScouter = new Scouter("", "");
        }
        return sScouter;
    }

    public String getName(){
        return mScouterName;
    }

    public String getDate(){
        return mScoutingDateString;
    }

    public void setName(String n){
        mScouterName = n;
    }

    public void setDate(String d){
        mScoutingDateString = d;
    }
}
