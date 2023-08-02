package com.frc2135.android.frc_scout;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class AutonFragment extends Fragment
{
    private static final String TAG = "AutonFragment";
    private static final int MAX_POINTS = 5;     // max for valid high or low points total

    private TextView m_autonConesBottomRowValue;
    private TextView m_autonConesMiddleRowValue;
    private TextView m_autonConesTopRowValue;

    private TextView m_autonCubesBottomRowValue;
    private TextView m_autonCubesMiddleRowValue;
    private TextView m_autonCubesTopRowValue;

    private RadioGroup m_autonRadioGroup;
    private RadioButton m_radio_autonDocked;
    private RadioButton m_radio_autonEngaged;

    private CheckBox m_mobilityCheckbox;
    private MatchData m_matchData;

    // Check if pointsTextView field has a valid number, greater than MAX_POINTS.
    private boolean isNotValidPoints(TextView field)
    {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if (num > MAX_POINTS)
            rtn = true;
        return rtn;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ScoutingActivity activity = (ScoutingActivity) getActivity();
        if(activity != null) {
            try {
                m_matchData = activity.getCurrentMatch();
                if(m_matchData != null) {
                    Log.d(TAG, "New match ID = " + m_matchData.getMatchID());
                    ActionBar m_actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    String teamNumber = m_matchData.stripTeamNamePrefix(m_matchData.getTeamNumber());
                    if (m_actionBar != null) {
                        m_actionBar.setTitle("Autonomous          Scouting Team " + teamNumber + "         Match " + m_matchData.getMatchNumber());
                    }
                }
            }
            catch (Exception e) {
                Log.d(TAG,"AutonFragment::onCreate(): getCurrentMatch() threw exception!\n");
            }
        }
    }

    // Sets the new result integer value for the given Button, either decrementing or incrementing it.
    // If the decrement case falls below zero, returns 0. Sets textView to RED if out of valid range.
    public void updatePointsInt(TextView pointsTextView, boolean bIncr)
    {
        int result = Integer.parseInt(pointsTextView.getText().toString()); // get current value as int
        if (bIncr)
            result += 1;
        else
            result -= 1;
        if (result < 0)
            result = 0;
        pointsTextView.setText(String.valueOf(result));
        if (isNotValidPoints(pointsTextView))
        {
            pointsTextView.setTextColor(Color.RED);
        }
        else {
            Context context = getContext();
            if(context != null)  {
               pointsTextView.setTextColor(ContextCompat.getColor(context,R.color.specialTextPrimary));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        // Creates a view using the specific fragment layout, match_data_fragment
        View v = inflater.inflate(R.layout.auton_fragment, parent, false);
        Context context = getContext();
        if(context != null)  {
            int specialTextPrimaryColor = ContextCompat.getColor(context,R.color.specialTextPrimary);

            // Sets up TextView that displays cones bottom row points, setting 0 as the default
            m_autonConesBottomRowValue = v.findViewById(R.id.autoncone_bottom_text);
            m_autonConesBottomRowValue.setText("0");
            m_autonConesBottomRowValue.setTextColor(specialTextPrimaryColor);

            // Sets up TextView that displays cones middle row points, setting 0 as the default
            m_autonConesMiddleRowValue = v.findViewById(R.id.autoncone_middle_text);
            m_autonConesMiddleRowValue.setText("0");
            m_autonConesMiddleRowValue.setTextColor(specialTextPrimaryColor);

            // Sets up TextView that displays cones top row points, setting 0 as the default
            m_autonConesTopRowValue = v.findViewById(R.id.autoncone_top_text);
            m_autonConesTopRowValue.setText("0");
            m_autonConesTopRowValue.setTextColor(specialTextPrimaryColor);

            // Sets up TextView that displays cubes bottom row points, setting 0 as the default
            m_autonCubesBottomRowValue = v.findViewById(R.id.autoncube_bottom_text);
            m_autonCubesBottomRowValue.setText("0");
            m_autonCubesBottomRowValue.setTextColor(specialTextPrimaryColor);

            // Sets up TextView that displays cubes middle row points, setting 0 as the default
            m_autonCubesMiddleRowValue = v.findViewById(R.id.autoncube_middle_text);
            m_autonCubesMiddleRowValue.setText("0");
            m_autonCubesMiddleRowValue.setTextColor(specialTextPrimaryColor);

            // Sets up TextView that displays cubes top row points, setting 0 as the default
            m_autonCubesTopRowValue = v.findViewById(R.id.autoncube_top_text);
            m_autonCubesTopRowValue.setText("0");
            m_autonCubesTopRowValue.setTextColor(specialTextPrimaryColor);
        }

        //Connects the decrement button for cones bottom row points and sets up a listener that detects when the button is clicked
        Button autonConeBottomDecrButton = v.findViewById(R.id.autoncone_bottom_decr);
        //noinspection Convert2Lambda
        autonConeBottomDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_autonConesBottomRowValue, false);
            }
        });

        //Connects the increment button for cones bottom row points and sets up a listener that detects when the button is clicked
        Button autonConeBottomIncrButton = v.findViewById(R.id.autoncone_bottom_incr);
        //noinspection Convert2Lambda
        autonConeBottomIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_autonConesBottomRowValue, true);
            }
        });

        //Connects the decrement button for cones middle row points and sets up a listener that detects when the button is clicked
        Button autonConeMiddleDecrButton = v.findViewById(R.id.autoncone_middle_decr);
        autonConeMiddleDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autonConesMiddleRowValue, false);
            }
        });

        //Connects the increment button for cones middle row points and sets up a listener that detects when the button is clicked
        Button autonConeMiddleIncrButton = v.findViewById(R.id.autoncone_middle_incr);
        autonConeMiddleIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_autonConesMiddleRowValue, true);
            }
        });

        //Connects the decrement button for cones top row points and sets up a listener that detects when the button is clicked
        Button autonConeTopDecrButton = v.findViewById(R.id.autoncone_top_decr);
        autonConeTopDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autonConesTopRowValue, false);
            }
        });

        //Connects the increment button for cones top row points and sets up a listener that detects when the button is clicked
        Button autonConeTopIncrButton = v.findViewById(R.id.autoncone_top_incr);
        autonConeTopIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autonConesTopRowValue, true);
            }
        });

        //Connects the decrement button for cubes bottom row points and sets up a listener that detects when the button is clicked
        Button autonCubeBottomDecrButton = v.findViewById(R.id.autoncube_bottom_decr);
        autonCubeBottomDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_autonCubesBottomRowValue, false);
            }
        });

        //Connects the increment button for cubes bottom row points and sets up a listener that detects when the button is clicked
        Button autonCubeBottomIncrButton = v.findViewById(R.id.autoncube_bottom_incr);
        autonCubeBottomIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_autonCubesBottomRowValue, true);
            }
        });

        //Connects the decrement button for cubes middle row points and sets up a listener that detects when the button is clicked
        Button autonCubeMiddleDecrButton = v.findViewById(R.id.autoncube_middle_decr);
        autonCubeMiddleDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autonCubesMiddleRowValue, false);
            }
        });

        //Connects the increment button for cubes middle row points and sets up a listener that detects when the button is clicked
        Button autonCubeMiddleIncrButton = v.findViewById(R.id.autoncube_middle_incr);
        autonCubeMiddleIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_autonCubesMiddleRowValue, true);
            }
        });

        //Connects the decrement button for cubes top row points and sets up a listener that detects when the button is clicked
        Button autonCubeTopDecrButton = v.findViewById(R.id.autoncube_top_decr);
        autonCubeTopDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autonCubesTopRowValue, false);
            }
        });

        //Connects the increment button for cubes top row points and sets up a listener that detects when the button is clicked
        Button autonCubeTopIncrButton = v.findViewById(R.id.autoncube_top_incr);
        autonCubeTopIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_autonCubesTopRowValue, true);
            }
        });

        //Connects the checkbox for exiting the community and sets up a listener to detect when the checked status is changed
        m_mobilityCheckbox = v.findViewById(R.id.mobility_checkbox);
        m_mobilityCheckbox.setChecked(m_matchData.getExitedCommunity());

        m_autonRadioGroup = v.findViewById(R.id.auton_charge_level);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
        RadioButton radio_autonNone = v.findViewById(R.id.level_autonnone);//Sets up radio button that corresponds to 0
        radio_autonNone.setChecked(true);
        m_radio_autonDocked = v.findViewById(R.id.level_autondocked);//Sets up radio button that corresponds to 1
        m_radio_autonDocked.setChecked(false);
        m_radio_autonEngaged = v.findViewById(R.id.level_autonengaged);//Sets up radio button that corresponds to 2
        m_radio_autonEngaged.setChecked(false);

        int x = m_matchData.getAutonChargeLevel();
        if (x == 0)
            radio_autonNone.setChecked(true);
        else if (x == 1)
            m_radio_autonDocked.setChecked(true);
        else if (x == 2)
            m_radio_autonEngaged.setChecked(true);

        m_autonRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {

                //Changes m_matchData's climb variable according to which radio button is selected
                m_matchData.setAutonChargeLevel(getCurrentAutonChargeLevel());
            }
        });

        m_autonConesBottomRowValue.setText(String.valueOf(m_matchData.getAutonConesBottomRow()));
        m_autonConesMiddleRowValue.setText(String.valueOf(m_matchData.getAutonConesMiddleRow()));
        m_autonConesTopRowValue.setText(String.valueOf(m_matchData.getAutonConesTopRow()));
        m_autonCubesBottomRowValue.setText(String.valueOf(m_matchData.getAutonCubesBottomRow()));
        m_autonCubesMiddleRowValue.setText(String.valueOf(m_matchData.getAutonCubesMiddleRow()));
        m_autonCubesTopRowValue.setText(String.valueOf(m_matchData.getAutonCubesTopRow()));
        if (isNotValidPoints(m_autonConesBottomRowValue))
        {
            m_autonConesBottomRowValue.setTextColor(Color.RED);
        }
        if (isNotValidPoints(m_autonConesMiddleRowValue))
        {
            m_autonConesMiddleRowValue.setTextColor(Color.RED);
        }
        if (isNotValidPoints(m_autonConesTopRowValue))
        {
            m_autonConesTopRowValue.setTextColor(Color.RED);
        }
        if (isNotValidPoints(m_autonCubesBottomRowValue))
        {
            m_autonCubesBottomRowValue.setTextColor(Color.RED);
        }
        if (isNotValidPoints(m_autonCubesMiddleRowValue))
        {
            m_autonCubesMiddleRowValue.setTextColor(Color.RED);
        }
        if (isNotValidPoints(m_autonCubesTopRowValue))
        {
            m_autonCubesTopRowValue.setTextColor(Color.RED);
        }
        return v;
    }

    public int getCurrentAutonChargeLevel()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;

        if (m_autonRadioGroup.getCheckedRadioButtonId() == m_radio_autonDocked.getId())
        {
            rtn = 1;
        }
        else if (m_autonRadioGroup.getCheckedRadioButtonId() == m_radio_autonEngaged.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public void updateAutonData()
    {

        m_matchData.setAutonConesBottomRow(Integer.parseInt(m_autonConesBottomRowValue.getText().toString()));
        m_matchData.setAutonConesMiddleRow(Integer.parseInt(m_autonConesMiddleRowValue.getText().toString()));
        m_matchData.setAutonConesTopRow(Integer.parseInt(m_autonConesTopRowValue.getText().toString()));
        m_matchData.setAutonCubesBottomRow(Integer.parseInt(m_autonCubesBottomRowValue.getText().toString()));
        m_matchData.setAutonCubesMiddleRow(Integer.parseInt(m_autonCubesMiddleRowValue.getText().toString()));
        m_matchData.setAutonCubesTopRow(Integer.parseInt(m_autonCubesTopRowValue.getText().toString()));
        m_matchData.setExitedCommunity(m_mobilityCheckbox.isChecked());

        m_matchData.setAutonConesBottomRow(Integer.parseInt(m_autonConesBottomRowValue.getText().toString()));
        m_matchData.setAutonConesMiddleRow(Integer.parseInt(m_autonConesMiddleRowValue.getText().toString()));
        m_matchData.setAutonConesTopRow(Integer.parseInt(m_autonConesTopRowValue.getText().toString()));
        m_matchData.setAutonCubesBottomRow(Integer.parseInt(m_autonCubesBottomRowValue.getText().toString()));
        m_matchData.setAutonCubesMiddleRow(Integer.parseInt(m_autonCubesMiddleRowValue.getText().toString()));
        m_matchData.setAutonCubesTopRow(Integer.parseInt(m_autonCubesTopRowValue.getText().toString()));

        m_matchData.setAutonChargeLevel(getCurrentAutonChargeLevel());
    }
}
