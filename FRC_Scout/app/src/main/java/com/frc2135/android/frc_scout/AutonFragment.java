package com.frc2135.android.frc_scout;

import android.graphics.Color;
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
    private static final int MAX_POINTS = 5;     // max for valid high or low points total

    private RadioGroup  m_RadioGroup;
    private RadioButton m_RadioButton1;
    private RadioButton m_RadioButton2;
    private RadioButton m_RadioButton3;
    private RadioButton m_RadioButton4;
    private RadioButton m_RadioButton5;
    private RadioButton m_RadioButton6;

    private TextView m_lowPointsValue;
    private TextView m_highPointsValue;

    private Button m_lowDecrButton;
    private Button m_lowIncrButton;
    private Button m_highDecrButton;
    private Button m_highIncrButton;

    private CheckBox  m_tarmacCheckbox;
    private MatchData m_matchData;
    private ActionBar m_actionBar;

    // Check if pointsTextView field has a valid number, equal or less than MAX_POINTS.
    private boolean isValidPoints(TextView field) {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if(num <= MAX_POINTS)
            rtn = true;
        return rtn;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        m_matchData = ((ScoutingActivity)getActivity()).getCurrentMatch();
        Log.d(TAG, m_matchData.getMatchID());
        m_actionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        String teamNumber = m_matchData.stripTeamNamePrefix(m_matchData.getTeamNumber());
        m_actionBar.setTitle("Autonomous                        - scouting Team "+teamNumber);
    }

    // Sets the new result integer value for the given Button, either decrementing or incrementing it.
    // If the decrement case falls below zero, returns 0. Sets textView to RED if out of valid range.
    public void updatePointsInt(TextView pointsTextView,boolean bIncr){
        int result = Integer.parseInt(pointsTextView.getText().toString()); // get current value as int
        if(bIncr)
            result += 1;
        else result -= 1;
        if(result < 0)
            result = 0;
        pointsTextView.setText(result + "");
        if(!isValidPoints(pointsTextView)) {
            pointsTextView.setTextColor(Color.RED);
        }
        else pointsTextView.setTextColor(getResources().getColor(R.color.textPrimary));
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        // Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.auton_fragment, parent, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();

        // Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        m_RadioGroup = (RadioGroup)v.findViewById(R.id.start_position);
        m_RadioButton1 = (RadioButton)v.findViewById(R.id.start_one);//Sets up radio button that corresponds to 1
        m_RadioButton2 = (RadioButton)v.findViewById(R.id.start_two);//Sets up radio button that corresponds to 2
        m_RadioButton3 = (RadioButton)v.findViewById(R.id.start_three);//Sets up radio button that corresponds to 3
        m_RadioButton4 = (RadioButton)v.findViewById(R.id.start_four);//Sets up radio button that corresponds to 4
        m_RadioButton5 = (RadioButton)v.findViewById(R.id.start_five);//Sets up radio button that corresponds to 5
        m_RadioButton6 = (RadioButton)v.findViewById(R.id.start_six);//Sets up radio button that corresponds to 6

        int x = m_matchData.getStartPosition();
        Log.d(TAG,"Setting up start position to "+x);
        if(x==1)
            m_RadioButton1.setChecked(true);
        else if(x==2)
            m_RadioButton2.setChecked(true);
        else if(x==3)
            m_RadioButton3.setChecked(true);
        else if(x==4)
            m_RadioButton4.setChecked(true);
        else if(x==5)
            m_RadioButton5.setChecked(true);
        else if(x==6)
            m_RadioButton6.setChecked(true);

        m_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //Changes m_matchData's start position variable according to which radio button is selected
                m_matchData.setStartPosition(getCurrentStartPosition());
            }
        });

        //Sets up TextView that displays low points, setting 0 as the default
        m_lowPointsValue = v.findViewById(R.id.low_points_text);
        m_lowPointsValue.setText(0 + "");
        m_lowPointsValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays high points, setting 0 as the default
        m_highPointsValue = v.findViewById(R.id.high_points_text);
        m_highPointsValue.setText(0+ "");
        m_highPointsValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Connects the decrement button for low points and sets up a listener that detects when the button is clicked
        m_lowDecrButton = v.findViewById(R.id.low_points_dec);
        m_lowDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_lowPointsValue,false);
            }
        });

        //Connects the increment button for low points and sets up a listener that detects when the button is clicked
        m_lowIncrButton = v.findViewById(R.id.low_points_inc);
        m_lowIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_lowPointsValue,true);
            }
        });

        //Connects the decrement button for high points and sets up a listener that detects when the button is clicked
        m_highDecrButton = v.findViewById(R.id.high_points_dec);
        m_highDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_highPointsValue,false);
            }
        });

        //Connects the increment button for high points and sets up a listener that detects when the button is clicked
        m_highIncrButton = v.findViewById(R.id.high_points_inc);
        m_highIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_highPointsValue,true);
            }
        });

        //Connects the checkbox for exiting the tarmac and sets up a listener to detect when the checked status is changed
        m_tarmacCheckbox = v.findViewById(R.id.tarmac_checkbox);
        m_tarmacCheckbox.setChecked(m_matchData.getExitedTarmac());

        m_highPointsValue.setText(m_matchData.getAutonHighPoints()+"");
        m_lowPointsValue.setText(m_matchData.getAutonLowPoints()+"");
        if(!isValidPoints(m_lowPointsValue)) {
            m_lowPointsValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_highPointsValue)) {
            m_highPointsValue.setTextColor(Color.RED);
        }

        return v;
    }

            public int getCurrentStartPosition() {
                // Returns the integer start position that is currently checked in the radio buttons
                int rtn = 0;
                if (m_RadioGroup.getCheckedRadioButtonId() == m_RadioButton1.getId()) {
                    rtn = 1;
                }
                else if (m_RadioGroup.getCheckedRadioButtonId() == m_RadioButton2.getId()) {
                    rtn = 2;
                }
                else if (m_RadioGroup.getCheckedRadioButtonId() == m_RadioButton3.getId()) {
                    rtn = 3;
                }
                else if (m_RadioGroup.getCheckedRadioButtonId() == m_RadioButton4.getId()) {
                    rtn = 4;
                }
                else if (m_RadioGroup.getCheckedRadioButtonId() == m_RadioButton5.getId()) {
                    rtn = 5;
                }
                else if (m_RadioGroup.getCheckedRadioButtonId() == m_RadioButton6.getId()) {
                        rtn = 6;
                }

                return rtn;
            }


    public void updateAutonData(){
        m_matchData.setStartPosition(getCurrentStartPosition());
        m_matchData.setAutonLowPoints(Integer.parseInt(m_lowPointsValue.getText().toString()));
        m_matchData.setAutonHighPoints(Integer.parseInt(m_highPointsValue.getText().toString()));
        m_matchData.setExitedTarmac(m_tarmacCheckbox.isChecked());
    }
}
