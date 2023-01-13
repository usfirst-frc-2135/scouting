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
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class AutonFragment extends Fragment {
    private static final String TAG = "AutonFragment";
    private static final int MAX_POINTS = 5;     // max for valid high or low points total

    /* private RadioGroup  m_RadioGroup;
    private RadioButton m_RadioButton1;
    private RadioButton m_RadioButton2;
    private RadioButton m_RadioButton3;
    private RadioButton m_RadioButton4;
    private RadioButton m_RadioButton5;
    private RadioButton m_RadioButton6; */

    private TextView m_conesBottomRowValue;
    private TextView m_conesMiddleRowValue;
    private TextView m_conesTopRowValue;

    private TextView m_cubesBottomRowValue;
    private TextView m_cubesMiddleRowValue;
    private TextView m_cubesTopRowValue;

    private Button m_coneBottomDecrButton;
    private Button m_coneBottomIncrButton;

    private Button m_coneMiddleDecrButton;
    private Button m_coneMiddleIncrButton;

    private Button m_coneTopDecrButton;
    private Button m_coneTopIncrButton;

    private Button m_cubeBottomDecrButton;
    private Button m_cubeBottomIncrButton;

    private Button m_cubeMiddleDecrButton;
    private Button m_cubeMiddleIncrButton;

    private Button m_cubeTopDecrButton;
    private Button m_cubeTopIncrButton;

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

        /* Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
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
        }); */

        //Sets up TextView that displays cones bottom row points, setting 0 as the default
        m_conesBottomRowValue = v.findViewById(R.id.cone_bottom_text);
        m_conesBottomRowValue.setText(0 + "");
        m_conesBottomRowValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays cones middle row points, setting 0 as the default
        m_conesMiddleRowValue = v.findViewById(R.id.cone_middle_text);
        m_conesMiddleRowValue.setText(0+ "");
        m_conesMiddleRowValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays cones top row points, setting 0 as the default
        m_conesTopRowValue = v.findViewById(R.id.cone_top_text);
        m_conesTopRowValue.setText(0+ "");
        m_conesTopRowValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays cubes bottom row points, setting 0 as the default
        m_cubesBottomRowValue = v.findViewById(R.id.cube_bottom_text);
        m_cubesBottomRowValue.setText(0 + "");
        m_cubesBottomRowValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays cubes middle row points, setting 0 as the default
        m_cubesMiddleRowValue = v.findViewById(R.id.cube_middle_text);
        m_cubesMiddleRowValue.setText(0+ "");
        m_cubesMiddleRowValue.setTextColor(getResources().getColor(R.color.textPrimary));

        //Sets up TextView that displays cubes top row points, setting 0 as the default
        m_cubesTopRowValue = v.findViewById(R.id.cube_top_text);
        m_cubesTopRowValue.setText(0+ "");
        m_cubesTopRowValue.setTextColor(getResources().getColor(R.color.textPrimary));




        //Connects the decrement button for cones bottom row points and sets up a listener that detects when the button is clicked
        m_coneBottomDecrButton = v.findViewById(R.id.cone_bottom_decr);
        m_coneBottomDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_conesBottomRowValue,false);
            }
        });

        //Connects the increment button for cones bottom row points and sets up a listener that detects when the button is clicked
        m_coneBottomIncrButton = v.findViewById(R.id.cone_bottom_incr);
        m_coneBottomIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_conesBottomRowValue,true);
            }
        });

        //Connects the decrement button for cones middle row points and sets up a listener that detects when the button is clicked
        m_coneMiddleDecrButton = v.findViewById(R.id.cone_middle_decr);
        m_coneMiddleDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_conesMiddleRowValue,false);
            }
        });

        //Connects the increment button for cones middle row points and sets up a listener that detects when the button is clicked
        m_coneMiddleIncrButton = v.findViewById(R.id.cone_middle_incr);
        m_coneMiddleIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_conesMiddleRowValue,true);
            }
        });

        //Connects the decrement button for cones top row points and sets up a listener that detects when the button is clicked
        m_coneTopDecrButton = v.findViewById(R.id.cone_top_decr);
        m_coneTopDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_conesTopRowValue,false);
            }
        });

        //Connects the increment button for cones top row points and sets up a listener that detects when the button is clicked
        m_coneTopIncrButton = v.findViewById(R.id.cone_top_incr);
        m_coneTopIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_conesTopRowValue,false);
            }
        });





        //Connects the decrement button for cubes bottom row points and sets up a listener that detects when the button is clicked
        m_cubeBottomDecrButton = v.findViewById(R.id.cube_bottom_decr);
        m_cubeBottomDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_cubesBottomRowValue,false);
            }
        });

        //Connects the increment button for cubes bottom row points and sets up a listener that detects when the button is clicked
        m_cubeBottomIncrButton = v.findViewById(R.id.cube_bottom_incr);
        m_cubeBottomIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_cubesBottomRowValue,true);
            }
        });

        //Connects the decrement button for cubes middle row points and sets up a listener that detects when the button is clicked
        m_cubeMiddleDecrButton = v.findViewById(R.id.cube_middle_decr);
        m_cubeMiddleDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_cubesMiddleRowValue,false);
            }
        });

        //Connects the increment button for cubes middle row points and sets up a listener that detects when the button is clicked
        m_cubeMiddleIncrButton = v.findViewById(R.id.cube_middle_incr);
        m_cubeMiddleIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_cubesMiddleRowValue,true);
            }
        });

        //Connects the decrement button for cubes top row points and sets up a listener that detects when the button is clicked
        m_cubeTopDecrButton = v.findViewById(R.id.cube_top_decr);
        m_cubeTopDecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_cubesTopRowValue,false);
            }
        });

        //Connects the increment button for cubes top row points and sets up a listener that detects when the button is clicked
        m_cubeTopIncrButton = v.findViewById(R.id.cube_top_incr);
        m_cubeTopIncrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increases displayed point value by 1
                updatePointsInt(m_cubesTopRowValue,true);
            }
        });

        //Connects the checkbox for exiting the tarmac and sets up a listener to detect when the checked status is changed
        m_tarmacCheckbox = v.findViewById(R.id.tarmac_checkbox);
        m_tarmacCheckbox.setChecked(m_matchData.getExitedTarmac());

        m_conesBottomRowValue.setText(m_matchData.getAutonConesBottomRow()+"");
        m_conesMiddleRowValue.setText(m_matchData.getAutonConesMiddleRow()+"");
        m_conesTopRowValue.setText(m_matchData.getAutonConesTopRow()+"");
        m_cubesBottomRowValue.setText(m_matchData.getAutonCubesBottomRow()+"");
        m_cubesMiddleRowValue.setText(m_matchData.getAutonCubesMiddleRow()+"");
        m_cubesTopRowValue.setText(m_matchData.getAutonCubesTopRow()+"");
        if(!isValidPoints(m_conesBottomRowValue)) {
            m_conesBottomRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_conesMiddleRowValue)) {
            m_conesMiddleRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_conesTopRowValue)) {
            m_conesTopRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_cubesBottomRowValue)) {
            m_cubesBottomRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_cubesMiddleRowValue)) {
            m_cubesMiddleRowValue.setTextColor(Color.RED);
        }
        if(!isValidPoints(m_cubesTopRowValue)) {
            m_cubesTopRowValue.setTextColor(Color.RED);
        }
        return v;
    }



            /* public int getCurrentStartPosition() {
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
            } */


    public void updateAutonData(){
        /* m_matchData.setStartPosition(getCurrentStartPosition()); */
        m_matchData.setAutonConesBottomRow(Integer.parseInt(m_conesBottomRowValue.getText().toString()));
        m_matchData.setAutonConesMiddleRow(Integer.parseInt(m_conesMiddleRowValue.getText().toString()));
        m_matchData.setAutonConesTopRow(Integer.parseInt(m_conesTopRowValue.getText().toString()));
        m_matchData.setAutonCubesBottomRow(Integer.parseInt(m_cubesBottomRowValue.getText().toString()));
        m_matchData.setAutonCubesMiddleRow(Integer.parseInt(m_cubesMiddleRowValue.getText().toString()));
        m_matchData.setAutonCubesTopRow(Integer.parseInt(m_cubesTopRowValue.getText().toString()));
        m_matchData.setExitedTarmac(m_tarmacCheckbox.isChecked());
    }
}
