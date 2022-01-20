package com.frc2135.android.frc_scout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class AutonFragment extends Fragment {
    private static final String TAG = "AutonFragment";

    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton1;
    private RadioButton mRadioButton2;
    private RadioButton mRadioButton3;
    private RadioButton mRadioButton4;
    private RadioButton mRadioButton5;
    private RadioButton mRadioButton6;

    private TextView mLowPoints;
    private TextView mHighPoints;

    private Button mLowPointsDec;
    private Button mLowPointsInc;
    private Button mHighPointsDec;
    private Button mHighPointsInc;

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

        mRadioGroup = (RadioGroup)v.findViewById(R.id.start_position);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        mRadioButton1 = (RadioButton)v.findViewById(R.id.start_one);//Sets up radio button that corresponds to 1
        mRadioButton2 = (RadioButton)v.findViewById(R.id.start_two);//Sets up radio button that corresponds to 2
        mRadioButton3 = (RadioButton)v.findViewById(R.id.start_three);//Sets up radio button that corresponds to 3
        mRadioButton4 = (RadioButton)v.findViewById(R.id.start_four);//Sets up radio button that corresponds to 4
        mRadioButton5 = (RadioButton)v.findViewById(R.id.start_five);//Sets up radio button that corresponds to 5
        mRadioButton6 = (RadioButton)v.findViewById(R.id.start_six);//Sets up radio button that corresponds to 6

        int x = mMatchData.getStartPosition();
        Log.d(TAG,"Setting up start position to "+x);
        if(x==1)
            mRadioButton1.setChecked(true);
        else if(x==2)
            mRadioButton2.setChecked(true);
        else if(x==3)
            mRadioButton3.setChecked(true);
        else if(x==4)
            mRadioButton4.setChecked(true);
        else if(x==5)
            mRadioButton5.setChecked(true);
        else if(x==6)
            mRadioButton6.setChecked(true);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //Changes mMatchData's start position variable according to which radio button is selected
                mMatchData.setStartPosition(getCurrentStartPosition());
            }
        });

        //Sets up TextView that displays low points, setting 0 as the default
        mLowPoints = v.findViewById(R.id.low_points_text);
        mLowPoints.setText(0 + "");

        //Sets up TextView that displays high points, setting 0 as the default
        mHighPoints = v.findViewById(R.id.high_points_text);
        mHighPoints.setText(0+ "");


        //Connects the decrement button for low points and sets up a listener that detects when the button is clicked
        mLowPointsDec = v.findViewById(R.id.low_points_dec);
        mLowPointsDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(mLowPoints,false);
            }
        });

        //Connects the increment button for low points and sets up a listener that detects when the button is clicked
        mLowPointsInc = v.findViewById(R.id.low_points_inc);
        mLowPointsInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(mLowPoints,true);
            }
        });

        //Connects the decrement button for high points and sets up a listener that detects when the button is clicked
        mHighPointsDec = v.findViewById(R.id.high_points_dec);
        mHighPointsDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(mHighPoints,false);
            }
        });

        //Connects the increment button for high points and sets up a listener that detects when the button is clicked
        mHighPointsInc = v.findViewById(R.id.high_points_inc);
        mHighPointsInc.setOnClickListener(new View.OnClickListener() {
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

            public int getCurrentStartPosition() {
                // Returns the integer start position that is currently checked in the radio buttons
                int rtn = 0;
                if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton1.getId()) {
                    rtn = 1;
                }
                else if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton2.getId()) {
                    rtn = 2;
                }
                else if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton3.getId()) {
                    rtn = 3;
                }
                else if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton4.getId()) {
                    rtn = 4;
                }
                else if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton5.getId()) {
                    rtn = 5;
                }
                else if (mRadioGroup.getCheckedRadioButtonId() == mRadioButton6.getId()) {
                        rtn = 6;
                }

                return rtn;
            }


    public void updateAutonData(){
        mMatchData.setStartPosition(getCurrentStartPosition());
        mMatchData.setAutonLowPoints(Integer.parseInt(mLowPoints.getText().toString()));
        mMatchData.setAutonHighPoints(Integer.parseInt(mHighPoints.getText().toString()));
        mMatchData.setExitedTarmac(mCheckBox.isChecked());
    }
}