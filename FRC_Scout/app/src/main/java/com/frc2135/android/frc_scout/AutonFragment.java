package com.frc2135.android.frc_scout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class AutonFragment extends Fragment {
    private static final String TAG = "AutonFragment";

    private TextView mLowPoints;
    private TextView mHighPoints;

    private Button mLowPortPointsDec;
    private Button mLowPortPointsInc;
    private Button mHighPortPointsDec;
    private Button mHighPortPointsInc;

    private EditText mTeamNumberField;
    private EditText mMatchNumberField;

    private CheckBox mCheckBox;

    private MatchData mMatchData;

    private ActionBar mActionBar;


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        mMatchData = ((ScoutingActivity)getActivity()).getCurrentMatch();
        Log.d(TAG, mMatchData.getMatchID());
        mActionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        String teamNumber = mMatchData.stripTeamNamePrefix(mMatchData.getTeamNumber());
        mActionBar.setTitle("Autonomous                        - scouting Team "+teamNumber);

    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.auton_fragment, parent, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();

        //Sets up TextView that displays low points, setting 0 as the default
        mLowPoints = v.findViewById(R.id.lowportpoints);
        mLowPoints.setText(0 + "");

        //Sets up TextView that displays high points, setting 0 as the default
        mHighPoints = v.findViewById(R.id.highportpoints);
        mHighPoints.setText(0+ "");


        //Connects the decrement button for low points and sets up a listener that detects when the button is clicked
        mLowPortPointsDec = v.findViewById(R.id.lowportpointsdec);
        mLowPortPointsDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(mLowPoints,false);
            }
        });

        //Connects the increment button for low points and sets up a listener that detects when the button is clicked
        mLowPortPointsInc = v.findViewById(R.id.lowportpointsinc);
        mLowPortPointsInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(mLowPoints,true);
            }
        });

        //Connects the decrement button for high points and sets up a listener that detects when the button is clicked
        mHighPortPointsDec = v.findViewById(R.id.highportpointsdec);
        mHighPortPointsDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(mHighPoints,false);
            }
        });

        //Connects the increment button for high points and sets up a listener that detects when the button is clicked
        mHighPortPointsInc = v.findViewById(R.id.highportpointsinc);
        mHighPortPointsInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(mHighPoints,true);
            }
        });

        //Connects the checkbox for exiting the tarmac and sets up a listener to detect when the checked status is changed
        mCheckBox = v.findViewById(R.id.tarmac_checkbox);
        mCheckBox.setChecked(mMatchData.getExitedTarmac());



        mHighPoints.setText(mMatchData.getAutonHighPoints()+"");
        mLowPoints.setText(mMatchData.getAutonLowPoints()+"");

        return v;
    }

    public void updateAutonData(){
        mMatchData.setAutonLowPoints(Integer.parseInt(mLowPoints.getText().toString()));
        mMatchData.setAutonOuterPoints(Integer.parseInt(mHighPoints.getText().toString()));
        mMatchData.setExitedTarmac(mCheckBox.isChecked());
    }
}
