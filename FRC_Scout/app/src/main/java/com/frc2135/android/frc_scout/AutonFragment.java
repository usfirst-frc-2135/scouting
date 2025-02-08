package com.frc2135.android.frc_scout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/** @noinspection ALL*/
public class AutonFragment extends Fragment
{
    private static final String TAG = "AutonFragment";
    private static final int MAX_POINTS = 4;    

    private CheckBox m_reefZoneCkbx1;
    private CheckBox m_reefZoneCkbx2;
    private CheckBox m_reefZoneCkbx3;
    private CheckBox m_reefZoneCkbx4;
    private CheckBox m_reefZoneCkbx5;
    private CheckBox m_reefZoneCkbx6;

    // Reefzone image orientation: 
    //   0 = Red scoringTable side
    //   1 = Red non-scoringTable side
    //   2 = Blue scoringTable side
    //   3 = Blue non-scoringTable side
    private int m_reefzoneOrient;   

    private TextView m_autonL1Total;
    private TextView m_autonL2Total;
    private TextView m_autonL3Total;
    private TextView m_autonL4Total;
    private Button m_autonL1IncrButton;
    private Button m_autonL1DecrButton;
    private Button m_autonL2IncrButton;
    private Button m_autonL2DecrButton;
    private Button m_autonL3IncrButton;
    private Button m_autonL3DecrButton;
    private Button m_autonL4IncrButton;
    private Button m_autonL4DecrButton;

    private TextView m_autonAlgaeNetTotal;
    private TextView m_autonAlgaeProcTotal;
    private Button m_autonAlgaeNetIncrButton;
    private Button m_autonAlgaeNetDecrButton;
    private Button m_autonAlgaeProcIncrButton;
    private Button m_autonAlgaeProcDecrButton;

    private RadioGroup m_startingPosition;
    private RadioButton m_rightStart;
    private RadioButton m_middleStart;
    private RadioButton m_leftStart;

    private CheckBox m_leaveCheckbox;
    private CheckBox m_floorCoral;
    private CheckBox m_stationCoral;
    private CheckBox m_floorAlgae;
    private CheckBox m_reefAlgae;

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

    private void getMatchDataReefscapeZones()
    {
        // Red scoring table side or blue non-scoring table side.
        if(m_reefzoneOrient == 0 || m_reefzoneOrient == 3)   
        {
            m_reefZoneCkbx1.setChecked(m_matchData.getReefzone_GH());
            m_reefZoneCkbx2.setChecked(m_matchData.getReefzone_EF());
            m_reefZoneCkbx3.setChecked(m_matchData.getReefzone_CD());
            m_reefZoneCkbx4.setChecked(m_matchData.getReefzone_AB());
            m_reefZoneCkbx5.setChecked(m_matchData.getReefzone_KL());
            m_reefZoneCkbx6.setChecked(m_matchData.getReefzone_IJ());

        }
        // Red non-scoring table side or Blue scoring table side
        else if(m_reefzoneOrient == 1 || m_reefzoneOrient == 2)   
        {
            m_reefZoneCkbx1.setChecked(m_matchData.getReefzone_AB());
            m_reefZoneCkbx2.setChecked(m_matchData.getReefzone_KL());
            m_reefZoneCkbx3.setChecked(m_matchData.getReefzone_IJ());
            m_reefZoneCkbx4.setChecked(m_matchData.getReefzone_GH());
            m_reefZoneCkbx5.setChecked(m_matchData.getReefzone_EF());
            m_reefZoneCkbx6.setChecked(m_matchData.getReefzone_CD());
        }
    }

    private void setMatchDataReefscapeZones()
    {
        // Red scoring table side or blue non-scoring table side.
        if(m_reefzoneOrient == 0 || m_reefzoneOrient == 3)   
        {
            m_matchData.setReefzone_AB(m_reefZoneCkbx4.isChecked());
            m_matchData.setReefzone_CD(m_reefZoneCkbx3.isChecked());
            m_matchData.setReefzone_EF(m_reefZoneCkbx2.isChecked());
            m_matchData.setReefzone_GH(m_reefZoneCkbx1.isChecked());
            m_matchData.setReefzone_IJ(m_reefZoneCkbx6.isChecked());
            m_matchData.setReefzone_KL(m_reefZoneCkbx5.isChecked());
        }
        // Red non-scoring table side or Blue scoring table side
        else if(m_reefzoneOrient == 1 || m_reefzoneOrient == 2)   
        {
            m_matchData.setReefzone_AB(m_reefZoneCkbx1.isChecked());
            m_matchData.setReefzone_CD(m_reefZoneCkbx6.isChecked());
            m_matchData.setReefzone_EF(m_reefZoneCkbx5.isChecked());
            m_matchData.setReefzone_GH(m_reefZoneCkbx4.isChecked());
            m_matchData.setReefzone_IJ(m_reefZoneCkbx3.isChecked());
            m_matchData.setReefzone_KL(m_reefZoneCkbx2.isChecked());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ScoutingActivity activity = (ScoutingActivity) getActivity();
        if (activity != null)
        {
            m_matchData = activity.getCurrentMatch();
            if (m_matchData != null)
            {
                Log.d(TAG, "New match ID = " + m_matchData.getMatchID());
                ActionBar m_actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                String teamNumber = m_matchData.stripTeamNamePrefix(m_matchData.getTeamNumber());
                if (m_actionBar != null)
                {
                    m_actionBar.setTitle("Autonomous          Scouting Team " + teamNumber + "         Match " + m_matchData.getMatchNumber());
                    Scouter myScouter = Scouter.get(getContext());
                    if (myScouter != null){
                        String color = myScouter.getTeamIndexColor();
                        if (color.equals("red")) {
                            m_actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                        }
                        else if (color.equals("blue")) {
                            m_actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
                        }
                    }
                }
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
        if (result < 0) result = 0;
        pointsTextView.setText(String.valueOf(result));
        if (isNotValidPoints(pointsTextView))
        {
            pointsTextView.setTextColor(Color.RED);
        }
        else
        {
            Context context = getContext();
            if (context != null)
            {
                pointsTextView.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        // Creates a view using the specific fragment layout
        View v = inflater.inflate(R.layout.auton_fragment, parent, false);
        Context context = getContext();
        if (context != null)
        {
            // Set the Reefzone background image. 
            m_reefzoneOrient = 0;   // default (matches the default image in layout file)
            Scouter myScouter = Scouter.get(getContext());
            if (myScouter != null){
                String color = myScouter.getTeamIndexColor();
                boolean scoringTableSide = myScouter.getScoringTableSide();
                LinearLayout rzone_ll = (LinearLayout)v.findViewById(R.id.reef_zones_layout);
                if (color.equals("red")) {
                    if (scoringTableSide) {
                        rzone_ll.setBackgroundResource(R.drawable.reefzone_r1_sctbl);
                        m_reefzoneOrient = 0;
                    }
                    else {
                        rzone_ll.setBackgroundResource(R.drawable.reefzone_r2);
                        m_reefzoneOrient = 1;
                    }
                } else {
                    if (scoringTableSide) {
                        rzone_ll.setBackgroundResource(R.drawable.reefzone_b1_sctbl);
                        m_reefzoneOrient = 2;
                    }
                    else {
                        rzone_ll.setBackgroundResource(R.drawable.reefzone_b2);
                        m_reefzoneOrient = 3;
                    }       
                }       
            }       

            m_autonL1Total = v.findViewById(R.id.auton_L1_score_total);
            m_autonL1Total.setText(String.valueOf(m_matchData.getAutonCoralL1()));
            m_autonL1DecrButton = v.findViewById(R.id.auton_L1_decr_button);
            m_autonL1IncrButton = v.findViewById(R.id.auton_L1_incr_button);
            m_autonL2Total = v.findViewById(R.id.auton_L2_score_total);
            m_autonL2Total.setText(String.valueOf(m_matchData.getAutonCoralL2()));
            m_autonL2DecrButton = v.findViewById(R.id.auton_L2_decr_button);
            m_autonL2IncrButton = v.findViewById(R.id.auton_L2_incr_button);
            m_autonL3Total = v.findViewById(R.id.auton_L3_score_total);
            m_autonL3Total.setText(String.valueOf(m_matchData.getAutonCoralL3()));
            m_autonL3DecrButton = v.findViewById(R.id.auton_L3_decr_button);
            m_autonL3IncrButton = v.findViewById(R.id.auton_L3_incr_button);
            m_autonL4Total = v.findViewById(R.id.auton_L4_score_total);
            m_autonL4Total.setText(String.valueOf(m_matchData.getAutonCoralL4()));
            m_autonL4DecrButton = v.findViewById(R.id.auton_L4_decr_button);
            m_autonL4IncrButton = v.findViewById(R.id.auton_L4_incr_button);

            // Setup TextViews that displays points, setting 0 as the default.
            // defense buttons
            m_startingPosition = v.findViewById(R.id.starting_position);
            m_rightStart = v.findViewById(R.id.right_start);
            m_middleStart = v.findViewById(R.id.middle_start);
            m_leftStart  = v.findViewById(R.id.left_start);
            m_rightStart.setChecked(false);
            m_middleStart.setChecked(false);
            m_leftStart .setChecked(false);

            int defValue = m_matchData.getCurrentStartingPosition();
            if (defValue == 0)
                m_rightStart.setChecked(true);
            else if(defValue == 1)
                m_middleStart.setChecked(true);
            else if(defValue == 2)
                m_leftStart.setChecked(true);

            m_autonAlgaeNetTotal = v.findViewById(R.id.auton_algae_net_total);
            m_autonAlgaeNetTotal.setText(String.valueOf(m_matchData.getAutonAlgaeNet()));
            m_autonAlgaeNetDecrButton = v.findViewById(R.id.auton_algae_net_decr_button);
            m_autonAlgaeNetIncrButton = v.findViewById(R.id.auton_algae_net_incr_button);
            m_autonAlgaeProcTotal = v.findViewById(R.id.auton_algae_proc_total);
            m_autonAlgaeProcTotal.setText(String.valueOf(m_matchData.getAutonAlgaeProcessor()));
            m_autonAlgaeProcDecrButton = v.findViewById(R.id.auton_algae_proc_decr_button);
            m_autonAlgaeProcIncrButton = v.findViewById(R.id.auton_algae_proc_incr_button);
        }

        m_autonL1IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonL1Total, true);
            }
        });

        m_autonL2IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_autonL2Total, true);
            }
        });

        m_autonL3IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_autonL3Total, true);
            }
        });

        m_autonL4IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonL4Total, true);
            }
        });

        m_autonL1DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonL1Total, false);
            }
        });

        m_autonL2DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonL2Total, false);
            }
        });

        m_autonL3DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonL3Total, false);
            }
        });

        m_autonL4DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonL4Total, false);
            }
        });

        //Connects the checkboxes and sets up a listener to detect when checked status is changed
        m_leaveCheckbox = v.findViewById(R.id.leave_checkbox);
        m_leaveCheckbox.setChecked(m_matchData.getAutonLeave());
        m_floorCoral = v.findViewById(R.id.floor_coral);
        m_floorCoral.setChecked(m_matchData.getFloorCoral());
        m_stationCoral = v.findViewById(R.id.station_coral);
        m_stationCoral.setChecked(m_matchData.getStationCoral());
        m_floorAlgae = v.findViewById(R.id.floor_algae);
        m_floorAlgae.setChecked(m_matchData.getFloorAlgae());
        m_reefAlgae = v.findViewById(R.id.reef_algae);
        m_reefAlgae.setChecked(m_matchData.getReefAlgae());


        // Set up listener for each incr/decr button
        m_autonAlgaeNetIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonAlgaeNetTotal, true);
            }
        });

        m_autonAlgaeNetDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonAlgaeNetTotal, false);
            }
        });

        m_autonAlgaeProcIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonAlgaeProcTotal, true);
            }
        });

        m_autonAlgaeProcDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonAlgaeProcTotal, false);
            }
        });

        // Set up reefzone checkboxes from MatchData.
        m_reefZoneCkbx1 = v.findViewById(R.id.reefzone_b1);
        m_reefZoneCkbx2 = v.findViewById(R.id.reefzone_b2);
        m_reefZoneCkbx3 = v.findViewById(R.id.reefzone_b3);
        m_reefZoneCkbx4 = v.findViewById(R.id.reefzone_b4);
        m_reefZoneCkbx5 = v.findViewById(R.id.reefzone_b5);
        m_reefZoneCkbx6 = v.findViewById(R.id.reefzone_b6);
        getMatchDataReefscapeZones();

        return v;
    }

    public int getCurrentStartingPosition()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_startingPosition.getCheckedRadioButtonId() == m_rightStart.getId())
        {
            rtn = 0;
        }
        if (m_startingPosition.getCheckedRadioButtonId() == m_middleStart.getId())
        {
            rtn = 1;
        }
        if (m_startingPosition.getCheckedRadioButtonId() == m_leftStart.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public void updateAutonData()
    {
        m_matchData.setAutonCoralL1(Integer.parseInt(m_autonL1Total.getText().toString()));
        m_matchData.setAutonCoralL2(Integer.parseInt(m_autonL2Total.getText().toString()));
        m_matchData.setAutonCoralL3(Integer.parseInt(m_autonL3Total.getText().toString()));
        m_matchData.setAutonCoralL4(Integer.parseInt(m_autonL4Total.getText().toString()));

        m_matchData.setAutonAlgaeNet(Integer.parseInt(m_autonAlgaeNetTotal.getText().toString()));
        m_matchData.setAutonAlgaeProcessor(Integer.parseInt(m_autonAlgaeProcTotal.getText().toString()));

        m_matchData.setCurrentStartingPosition(getCurrentStartingPosition());
        m_matchData.setAutonLeave(m_leaveCheckbox.isChecked());
        m_matchData.setFloorCoral(m_floorCoral.isChecked());
        m_matchData.setStationCoral(m_stationCoral.isChecked());
        m_matchData.setFloorAlgae(m_floorAlgae.isChecked());
        m_matchData.setReefAlgae(m_reefAlgae.isChecked());

        // Determine the reefscape face for each checkbox.
        setMatchDataReefscapeZones();
    }
}
