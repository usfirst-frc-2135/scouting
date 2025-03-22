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
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/** @noinspection ALL*/
public class AutonFragment extends Fragment
{
    private static final String TAG = "AutonFragment";
    private static final int MAX_NUM_CORAL = 2;
    private static final int MAX_NUM_ALGAE = 2;
    private CheckBox m_leaveCheckbox;

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

    private MatchData m_matchData;

    // Check if given field is greater than expected max number.
    private boolean isGreaterThanMax(TextView field,boolean bIsCoral)
    {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if (bIsCoral == true) {
            if (num > MAX_NUM_CORAL)  // for coral number
                rtn = true;
        } else  // for algae number
        {       
            if (num > MAX_NUM_ALGAE)
                rtn = true;
        }
        return rtn;
    }

    // Sets the new result integer value for the given TextView, either decrementing or 
    // incrementing the shown value. If the decrement case falls below zero, returns 0. 
    // Sets textView color to RED if out of expected range.
    public void updateTotalsInt(TextView tView, boolean bIncr, boolean bIsCoral)
    {
        int result = Integer.parseInt(tView.getText().toString()); // get current value as int
        if (bIncr)
            result += 1;
        else result -= 1;
        if (result < 0) 
            result = 0;
        tView.setText(String.valueOf(result));
        if (isGreaterThanMax(tView,bIsCoral))
        {
            tView.setTextColor(Color.RED);
        }
        else
        {
            Context context = getContext();
            if (context != null)
            {
                tView.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));
            }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        // Creates a view using the specific fragment layout
        View v = inflater.inflate(R.layout.auton_fragment, parent, false);

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

        // Set up Coral L1-L4 incr/decr buttons.
        m_autonL1IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonL1Total, true, true);
            }
        });
        m_autonL2IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updateTotalsInt(m_autonL2Total, true, true);
            }
        });
        m_autonL3IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updateTotalsInt(m_autonL3Total, true, true);
            }
        });
        m_autonL4IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonL4Total, true, true);
            }
        });
        m_autonL1DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonL1Total, false, true);
            }
        });
        m_autonL2DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonL2Total, false, true);
            }
        });
        m_autonL3DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonL3Total, false, true);
            }
        });
        m_autonL4DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonL4Total, false, true);
            }
        });

        m_leaveCheckbox = v.findViewById(R.id.leave_checkbox);
        m_leaveCheckbox.setChecked(m_matchData.getAutonLeave());

        // Set up Algae Net/Proc incr/decr buttons and listeners.
        m_autonAlgaeNetTotal = v.findViewById(R.id.auton_algae_net_total);
        m_autonAlgaeNetTotal.setText(String.valueOf(m_matchData.getAutonAlgaeNet()));
        m_autonAlgaeNetDecrButton = v.findViewById(R.id.auton_algae_net_decr_button);
        m_autonAlgaeNetIncrButton = v.findViewById(R.id.auton_algae_net_incr_button);
        m_autonAlgaeProcTotal = v.findViewById(R.id.auton_algae_proc_total);
        m_autonAlgaeProcTotal.setText(String.valueOf(m_matchData.getAutonAlgaeProcessor()));
        m_autonAlgaeProcDecrButton = v.findViewById(R.id.auton_algae_proc_decr_button);
        m_autonAlgaeProcIncrButton = v.findViewById(R.id.auton_algae_proc_incr_button);

        m_autonAlgaeNetIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonAlgaeNetTotal, true, false);
            }
        });
        m_autonAlgaeNetDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonAlgaeNetTotal, false, false);
            }
        });
        m_autonAlgaeProcIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonAlgaeProcTotal, true, false);
            }
        });
        m_autonAlgaeProcDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonAlgaeProcTotal, false, false);
            }
        });

        // Check coral levels for MAX
        if (isGreaterThanMax(m_autonL1Total,true))
            m_autonL1Total.setTextColor(Color.RED);
        if (isGreaterThanMax(m_autonL2Total,true))
            m_autonL2Total.setTextColor(Color.RED);
        if (isGreaterThanMax(m_autonL3Total,true))
            m_autonL3Total.setTextColor(Color.RED);
        if (isGreaterThanMax(m_autonL4Total,true))
            m_autonL4Total.setTextColor(Color.RED);

        // Check algae for MAX
        if (isGreaterThanMax(m_autonAlgaeNetTotal,false))
            m_autonAlgaeNetTotal.setTextColor(Color.RED);
        if (isGreaterThanMax(m_autonAlgaeProcTotal,false))
            m_autonAlgaeProcTotal.setTextColor(Color.RED);

        return v;
    }


    public void updateAutonData()
    {
        m_matchData.setAutonCoralL1(Integer.parseInt(m_autonL1Total.getText().toString()));
        m_matchData.setAutonCoralL2(Integer.parseInt(m_autonL2Total.getText().toString()));
        m_matchData.setAutonCoralL3(Integer.parseInt(m_autonL3Total.getText().toString()));
        m_matchData.setAutonCoralL4(Integer.parseInt(m_autonL4Total.getText().toString()));

        m_matchData.setAutonAlgaeNet(Integer.parseInt(m_autonAlgaeNetTotal.getText().toString()));
        m_matchData.setAutonAlgaeProcessor(Integer.parseInt(m_autonAlgaeProcTotal.getText().toString()));

        m_matchData.setAutonLeave(m_leaveCheckbox.isChecked());

        // Determine the reefscape face for each checkbox.
    }
}
