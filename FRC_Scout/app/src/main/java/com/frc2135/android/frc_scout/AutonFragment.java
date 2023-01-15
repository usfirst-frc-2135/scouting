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
/* import android.widget.RadioButton;
import android.widget.RadioGroup; */
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


    private TextView m_autonconesBottomRowValue;
    private TextView m_autonconesMiddleRowValue;
    private TextView m_autonconesTopRowValue;

    private TextView m_autoncubesBottomRowValue;
    private TextView m_autoncubesMiddleRowValue;
    private TextView m_autoncubesTopRowValue;

    private Button m_autonconeBottomDecrButton;
    private Button m_autonconeBottomIncrButton;

    private Button m_autonconeMiddleDecrButton;
    private Button m_autonconeMiddleIncrButton;

    private Button m_autonconeTopDecrButton;
    private Button m_autonconeTopIncrButton;

    private Button m_autoncubeBottomDecrButton;
    private Button m_autoncubeBottomIncrButton;

    private Button m_autoncubeMiddleDecrButton;
    private Button m_autoncubeMiddleIncrButton;

    private Button m_autoncubeTopDecrButton;
    private Button m_autoncubeTopIncrButton;

    private RadioGroup m_autonradioGroup;
    private RadioButton m_radio_autonnone;
    private RadioButton m_radio_autondocked;
    private RadioButton m_radio_autonengaged;

    private CheckBox  m_mobilityCheckbox;
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
        m_actionBar.setTitle("Autonomous          Scouting Team "+teamNumber+"         Match "+m_matchData.getMatchNumber());
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



        //Sets up TextView that displays cones bottom row points, setting 0 as the default
        m_autonconesBottomRowValue = v.findViewById(R.id.autoncone_bottom_text);
        m_autonconesBottomRowValue.setText(0 + "");
        m_autonconesBottomRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));

        //Sets up TextView that displays cones middle row points, setting 0 as the default
        m_autonconesMiddleRowValue = v.findViewById(R.id.autoncone_middle_text);
        m_autonconesMiddleRowValue.setText(0+ "");
        m_autonconesMiddleRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));

        //Sets up TextView that displays cones top row points, setting 0 as the default
        m_autonconesTopRowValue = v.findViewById(R.id.autoncone_top_text);
        m_autonconesTopRowValue.setText(0+ "");
        m_autonconesTopRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));

        //Sets up TextView that displays cubes bottom row points, setting 0 as the default
        m_autoncubesBottomRowValue = v.findViewById(R.id.autoncube_bottom_text);
        m_autoncubesBottomRowValue.setText(0 + "");
        m_autoncubesBottomRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));

        //Sets up TextView that displays cubes middle row points, setting 0 as the default
        m_autoncubesMiddleRowValue = v.findViewById(R.id.autoncube_middle_text);
        m_autoncubesMiddleRowValue.setText(0+ "");
        m_autoncubesMiddleRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));

        //Sets up TextView that displays cubes top row points, setting 0 as the default
        m_autoncubesTopRowValue = v.findViewById(R.id.autoncube_top_text);
        m_autoncubesTopRowValue.setText(0+ "");
        m_autoncubesTopRowValue.setTextColor(getResources().getColor(R.color.specialTextPrimary));




        //Connects the decrement button for cones bottom row points and sets up a listener that detects when the button is clicked
        m_autonconeBottomDecrButton = v.findViewById(R.id.autoncone_bottom_decr);
        m_autonconeBottomDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_autonconesBottomRowValue,false);
            }
        });

        //Connects the increment button for cones bottom row points and sets up a listener that detects when the button is clicked
        m_autonconeBottomIncrButton = v.findViewById(R.id.autoncone_bottom_incr);
        m_autonconeBottomIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_autonconesBottomRowValue,true);
            }
        });

        //Connects the decrement button for cones middle row points and sets up a listener that detects when the button is clicked
        m_autonconeMiddleDecrButton = v.findViewById(R.id.autoncone_middle_decr);
        m_autonconeMiddleDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autonconesMiddleRowValue,false);
            }
        });

        //Connects the increment button for cones middle row points and sets up a listener that detects when the button is clicked
        m_autonconeMiddleIncrButton = v.findViewById(R.id.autoncone_middle_incr);
        m_autonconeMiddleIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_autonconesMiddleRowValue,true);
            }
        });

        //Connects the decrement button for cones top row points and sets up a listener that detects when the button is clicked
        m_autonconeTopDecrButton = v.findViewById(R.id.autoncone_top_decr);
        m_autonconeTopDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autonconesTopRowValue,false);
            }
        });

        //Connects the increment button for cones top row points and sets up a listener that detects when the button is clicked
        m_autonconeTopIncrButton = v.findViewById(R.id.autoncone_top_incr);
        m_autonconeTopIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autonconesTopRowValue,true);
            }
        });





        //Connects the decrement button for cubes bottom row points and sets up a listener that detects when the button is clicked
        m_autoncubeBottomDecrButton = v.findViewById(R.id.autoncube_bottom_decr);
        m_autoncubeBottomDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_autoncubesBottomRowValue,false);
            }
        });

        //Connects the increment button for cubes bottom row points and sets up a listener that detects when the button is clicked
        m_autoncubeBottomIncrButton = v.findViewById(R.id.autoncube_bottom_incr);
        m_autoncubeBottomIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_autoncubesBottomRowValue,true);
            }
        });

        //Connects the decrement button for cubes middle row points and sets up a listener that detects when the button is clicked
        m_autoncubeMiddleDecrButton = v.findViewById(R.id.autoncube_middle_decr);
        m_autoncubeMiddleDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autoncubesMiddleRowValue,false);
            }
        });

        //Connects the increment button for cubes middle row points and sets up a listener that detects when the button is clicked
        m_autoncubeMiddleIncrButton = v.findViewById(R.id.autoncube_middle_incr);
        m_autoncubeMiddleIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_autoncubesMiddleRowValue,true);
            }
        });

        //Connects the decrement button for cubes top row points and sets up a listener that detects when the button is clicked
        m_autoncubeTopDecrButton = v.findViewById(R.id.autoncube_top_decr);
        m_autoncubeTopDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autoncubesTopRowValue,false);
            }
        });

        //Connects the increment button for cubes top row points and sets up a listener that detects when the button is clicked
        m_autoncubeTopIncrButton = v.findViewById(R.id.autoncube_top_incr);
        m_autoncubeTopIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_autoncubesTopRowValue,true);
            }
        });

        //Connects the checkbox for exiting the community and sets up a listener to detect when the checked status is changed
        m_mobilityCheckbox = v.findViewById(R.id.mobility_checkbox);
        m_mobilityCheckbox.setChecked(m_matchData.getExitedCommunity());

        m_autonradioGroup = (RadioGroup)v.findViewById(R.id.auton_charge_level);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        m_radio_autonnone = (RadioButton)v.findViewById(R.id.level_autonnone);//Sets up radio button that corresponds to 0
        m_radio_autonnone.setChecked(true);
        m_radio_autondocked = (RadioButton)v.findViewById(R.id.level_autondocked);//Sets up radio button that corresponds to 1
        m_radio_autondocked.setChecked(false);
        m_radio_autonengaged = (RadioButton)v.findViewById(R.id.level_autonengaged);//Sets up radio button that corresponds to 2
        m_radio_autonengaged.setChecked(false);

        int x = m_matchData.getAutonChargeLevel();
        if(x==0)
            m_radio_autonnone.setChecked(true);
        else if(x==1)
            m_radio_autondocked.setChecked(true);
        else if(x==2)
            m_radio_autonengaged.setChecked(true);

        m_autonradioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //Changes m_matchData's climb variable according to which radio button is selected
                m_matchData.setAutonChargeLevel(getCurrentAutonChargeLevel());
            }
        });

        m_autonconesBottomRowValue.setText(m_matchData.getAutonConesBottomRow()+"");
        m_autonconesMiddleRowValue.setText(m_matchData.getAutonConesMiddleRow()+"");
        m_autonconesTopRowValue.setText(m_matchData.getAutonConesTopRow()+"");
        m_autoncubesBottomRowValue.setText(m_matchData.getAutonCubesBottomRow()+"");
        m_autoncubesMiddleRowValue.setText(m_matchData.getAutonCubesMiddleRow()+"");
        m_autoncubesTopRowValue.setText(m_matchData.getAutonCubesTopRow()+"");
        if(!isValidPoints(m_autonconesBottomRowValue)) {
            m_autonconesBottomRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_autonconesMiddleRowValue)) {
            m_autonconesMiddleRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_autonconesTopRowValue)) {
            m_autonconesTopRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_autoncubesBottomRowValue)) {
            m_autoncubesBottomRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_autoncubesMiddleRowValue)) {
            m_autoncubesMiddleRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_autoncubesTopRowValue)) {
            m_autoncubesTopRowValue.setTextColor(Color.RED);
        }
        return v;
    }



    public int getCurrentAutonChargeLevel() {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;

        if (m_autonradioGroup.getCheckedRadioButtonId() == m_radio_autondocked.getId()) {
            rtn = 1;
        }
        else if (m_autonradioGroup.getCheckedRadioButtonId() == m_radio_autonengaged.getId()) {
            rtn = 2;
        }
        return rtn;
    }




    public void updateAutonData(){


        m_matchData.setAutonConesBottomRow(Integer.parseInt(m_autonconesBottomRowValue.getText().toString()));
        m_matchData.setAutonConesMiddleRow(Integer.parseInt(m_autonconesMiddleRowValue.getText().toString()));
        m_matchData.setAutonConesTopRow(Integer.parseInt(m_autonconesTopRowValue.getText().toString()));
        m_matchData.setAutonCubesBottomRow(Integer.parseInt(m_autoncubesBottomRowValue.getText().toString()));
        m_matchData.setAutonCubesMiddleRow(Integer.parseInt(m_autoncubesMiddleRowValue.getText().toString()));
        m_matchData.setAutonCubesTopRow(Integer.parseInt(m_autoncubesTopRowValue.getText().toString()));
        m_matchData.setExitedCommunity(m_mobilityCheckbox.isChecked());

        m_matchData.setAutonConesBottomRow(Integer.parseInt(m_autonconesBottomRowValue.getText().toString()));
        m_matchData.setAutonConesMiddleRow(Integer.parseInt(m_autonconesMiddleRowValue.getText().toString()));
        m_matchData.setAutonConesTopRow(Integer.parseInt(m_autonconesTopRowValue.getText().toString()));
        m_matchData.setAutonCubesBottomRow(Integer.parseInt(m_autoncubesBottomRowValue.getText().toString()));
        m_matchData.setAutonCubesMiddleRow(Integer.parseInt(m_autoncubesMiddleRowValue.getText().toString()));
        m_matchData.setAutonCubesTopRow(Integer.parseInt(m_autoncubesTopRowValue.getText().toString()));

        m_matchData.setAutonChargeLevel(getCurrentAutonChargeLevel());


    }
}
