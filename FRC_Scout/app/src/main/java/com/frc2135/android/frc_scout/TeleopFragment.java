package com.frc2135.android.frc_scout;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Date;


public class TeleopFragment extends Fragment {
    private static final String TAG = "TeleopFragment";

    private TextView mLowPoints;
    private TextView mHighPoints;

    private Button mLowPointsDec;
    private Button mLowPointsInc;
    private Button mHighPointsDec;
    private Button mHighPointsInc;
    private MatchData mMatchData;
    private ActionBar mActionBar;

    // Sets the new result integer value for the given Button, either decrementing or incrementing it.
    // If the decrement case falls below zero, returns 0.
    public void updatePointsInt(TextView pointsTextView,boolean bIncr){
      int result = Integer.parseInt(pointsTextView.getText().toString()); // get current value as int
      if(bIncr)
        result += 1;
      else result -= 1;
      if(result < 0)
        result = 0;
      pointsTextView.setText(result + "");
    }
 
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mMatchData = ((ScoutingActivity)getActivity()).getCurrentMatch();
        Log.d(TAG, mMatchData.getMatchID());

        mActionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        String teamNumber = mMatchData.stripTeamNamePrefix(mMatchData.getTeamNumber());
        mActionBar.setTitle("Teleoperated                      - scouting Team "+teamNumber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.teleop_fragment, parent, false);



        //Sets up TextView that displays low points, setting 0 as the default
        mLowPoints = v.findViewById(R.id.lowpoints);
        mLowPoints.setText(0 + "");

        //Sets up TextView that displays high points, setting 0 as the default
        mHighPoints = v.findViewById(R.id.highpoints);
        mHighPoints.setText(0+ "");


        //Connects the decrement button for low points and sets up a listener that detects when the button is clicked
        mLowPointsDec = v.findViewById(R.id.lowpointsdec);
        mLowPointsDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if result goes negative.
                updatePointsInt(mLowPoints,false);
            }
        });

        //Connects the increment button for low points and sets up a listener that detects when the button is clicked
        mLowPointsInc = v.findViewById(R.id.lowpointsinc);
        mLowPointsInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increases displayed point value by 1
                updatePointsInt(mLowPoints,true);
            }
        });

        //Connects the decrement button for high points and sets up a listener that detects when the button is clicked
        mHighPointsDec = v.findViewById(R.id.highpointsdec);
        mHighPointsDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if negative result
                updatePointsInt(mHighPoints,false);
            }
        });

        //Connects the increment button for high points and sets up a listener that detects when the button is clicked
        mHighPointsInc = v.findViewById(R.id.highpointsinc);
        mHighPointsInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(mHighPoints,true);
            }
        });

        mHighPoints.setText(mMatchData.getTelopHighPoints()+"");
        mLowPoints.setText(mMatchData.getTeleopLowPoints()+"");

        return v;
    }

    public void updateTeleopData(){
        mMatchData.setTeleopLowPoints(Integer.parseInt(mLowPoints.getText().toString()));
        mMatchData.setTeleopHighPoints(Integer.parseInt(mHighPoints.getText().toString()));
        MatchHistory.get(getActivity()).saveData();
    }

    public String formattedDate(Date d){
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy");
        Date date = null;
        try{
            date=dt.parse(d.toString());
        }catch(Exception e){
            Log.d("SignInFragment", e.getMessage());
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("hh:mm:ss");

        if(date == null) {
            return null;
        }
        else { return (dt1.format(date)); }
    }


}
