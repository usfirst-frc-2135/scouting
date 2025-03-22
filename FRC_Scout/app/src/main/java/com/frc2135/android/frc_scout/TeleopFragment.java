package com.frc2135.android.frc_scout;
import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;



/** @noinspection SpellCheckingInspection*/
public class TeleopFragment extends Fragment
{
    private static final String TAG = "TeleopFragment";
    private static final int MAX_NUM_ALGAE = 7;     // algae max expected high total
    private static final int MAX_NUM_CORAL= 11;     // coral max expected high total 

    private TextView m_coralAcquiredTotal;
    private Button   m_coralAcquiredDecrButton;
    private Button   m_coralAcquiredIncrButton;
    private TextView m_algaeAcquiredTotal;
    private Button   m_algaeAcquiredDecrButton;
    private Button   m_algaeAcquiredIncrButton;
    
    private TextView m_teleopL1Total;
    private TextView m_teleopL2Total;
    private TextView m_teleopL3Total;
    private TextView m_teleopL4Total;
    private Button m_teleopL1IncrButton;
    private Button m_teleopL1DecrButton;
    private Button m_teleopL2IncrButton;
    private Button m_teleopL2DecrButton;
    private Button m_teleopL3IncrButton;
    private Button m_teleopL3DecrButton;
    private Button m_teleopL4IncrButton;
    private Button m_teleopL4DecrButton; 

    private TextView m_teleopAlgaeNetTotal;
    private Button m_teleopAlgaeNetIncrButton;
    private Button m_teleopAlgaeNetDecrButton;
    private TextView m_teleopAlgaeProcTotal;
    private Button m_teleopAlgaeProcIncrButton;
    private Button m_teleopAlgaeProcDecrButton;

    private MatchData m_matchData;

    // Check if pointsTextView field is greater than the MAX_NUM*.
    private boolean isGreaterThanMax(TextView field,boolean bIsCoral)
    {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if (bIsCoral == true) {
            if (num > MAX_NUM_CORAL)  // for coral number
                rtn = true;
        }
        else  // for algae number
        {
            if (num > MAX_NUM_ALGAE)
                rtn = true;
        }
        return rtn;
    }

    // Sets the new result integer value for the given TextView, either decrementing or 
    // incrementing the shown value. If the decrement case falls below zero, returns 0. 
    // Sets textView color to RED if out of expected range.
    public void updateTotalsInt(TextView tview, boolean bIncr, boolean bIsCoral)
    {
        int result = Integer.parseInt(tview.getText().toString()); // get current value as int
        if (bIncr)
            result += 1;
        else
            result -= 1;
        if (result < 0)
            result = 0;
        tview.setText(String.valueOf(result));

        if (isGreaterThanMax(tview,bIsCoral))
        {
            tview.setTextColor(Color.RED);
        }
        else
        {
            Context context = getContext();
            if (context != null)
            {
                tview.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        m_matchData = ((ScoutingActivity) requireActivity()).getCurrentMatch();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null)
        {
            String teamNumber = m_matchData.stripTeamNamePrefix(m_matchData.getTeamNumber());
            actionBar.setTitle("Teleoperated          Scouting Team " + teamNumber + "         Match " + m_matchData.getMatchNumber());
        }
    }

    /**
     * @noinspection Convert2Lambda
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        //Creates a view using the specific fragment layout.
        View v = inflater.inflate(R.layout.teleop_fragment, parent, false);

        // Set up listener for coralAcquired +/-buttons.
        m_coralAcquiredTotal= v.findViewById(R.id.coral_acquired_total);
        m_coralAcquiredTotal.setText(String.valueOf(m_matchData.getCoralAcquired()));
        m_coralAcquiredDecrButton = v.findViewById(R.id.coral_acquired_decr_button);
        m_coralAcquiredIncrButton = v.findViewById(R.id.coral_acquired_incr_button);
        m_coralAcquiredDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_coralAcquiredTotal, false, true);
            }
        });
        m_coralAcquiredIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_coralAcquiredTotal, true, true);
            }
        });

        // Set up listener for algaeAcquired +/-buttons.
        m_algaeAcquiredTotal= v.findViewById(R.id.algae_acquired_total);
        m_algaeAcquiredTotal.setText(String.valueOf(m_matchData.getAlgaeAcquired()));
        m_algaeAcquiredDecrButton = v.findViewById(R.id.algae_acquired_decr_button);
        m_algaeAcquiredIncrButton = v.findViewById(R.id.algae_acquired_incr_button);
        m_algaeAcquiredDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_algaeAcquiredTotal, false, false) ;
            }
        });
        m_algaeAcquiredIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_algaeAcquiredTotal, true, false);
            }
        });

        // Set up Coral L1-L4 incr/decr buttons and totals
        m_teleopL1Total = v.findViewById(R.id.teleop_L1_score_total);
        m_teleopL1Total.setText(String.valueOf(m_matchData.getTeleopCoralL1()));
        m_teleopL1DecrButton = v.findViewById(R.id.teleop_L1_decr_button);
        m_teleopL1IncrButton = v.findViewById(R.id.teleop_L1_incr_button);
        m_teleopL2Total = v.findViewById(R.id.teleop_L2_score_total);
        m_teleopL2Total.setText(String.valueOf(m_matchData.getTeleopCoralL2()));
        m_teleopL2DecrButton = v.findViewById(R.id.teleop_L2_decr_button);
        m_teleopL2IncrButton = v.findViewById(R.id.teleop_L2_incr_button);
        m_teleopL3Total = v.findViewById(R.id.teleop_L3_score_total);
        m_teleopL3Total.setText(String.valueOf(m_matchData.getTeleopCoralL3()));
        m_teleopL3DecrButton = v.findViewById(R.id.teleop_L3_decr_button);
        m_teleopL3IncrButton = v.findViewById(R.id.teleop_L3_incr_button);
        m_teleopL4Total = v.findViewById(R.id.teleop_L4_score_total);
        m_teleopL4Total.setText(String.valueOf(m_matchData.getTeleopCoralL4()));
        m_teleopL4DecrButton = v.findViewById(R.id.teleop_L4_decr_button);
        m_teleopL4IncrButton = v.findViewById(R.id.teleop_L4_incr_button);

        m_teleopL1IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_teleopL1Total, true,true);
            }
        });
        m_teleopL2IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updateTotalsInt(m_teleopL2Total, true,true);
            }
        });
        m_teleopL3IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updateTotalsInt(m_teleopL3Total, true,true);
            }
        });
        m_teleopL4IncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_teleopL4Total, true,true);
            }
        });
        m_teleopL1DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_teleopL1Total, false,true);
            }
        });
        m_teleopL2DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_teleopL2Total, false,true);
            }
        });
        m_teleopL3DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_teleopL3Total, false,true);
            }
        });
        m_teleopL4DecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_teleopL4Total, false,true);
            }
        });

        // Set up listener for Scoring Algae Net +/-buttons.
        m_teleopAlgaeNetTotal= v.findViewById(R.id.teleop_algae_net_total);
        m_teleopAlgaeNetTotal.setText(String.valueOf(m_matchData.getTeleopAlgaeNet()));
        m_teleopAlgaeNetDecrButton = v.findViewById(R.id.teleop_algae_net_decr_button);
        m_teleopAlgaeNetIncrButton = v.findViewById(R.id.teleop_algae_net_incr_button);
        m_teleopAlgaeNetDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_teleopAlgaeNetTotal, false, false);
            }
        });
        m_teleopAlgaeNetIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_teleopAlgaeNetTotal, true, false);
            }
        });

        // Set up listener for Scoring Algae Proc +/-buttons.
        m_teleopAlgaeProcTotal= v.findViewById(R.id.teleop_algae_proc_total);
        m_teleopAlgaeProcTotal.setText(String.valueOf(m_matchData.getTeleopAlgaeProcessor()));
        m_teleopAlgaeProcDecrButton = v.findViewById(R.id.teleop_algae_proc_decr_button);
        m_teleopAlgaeProcIncrButton = v.findViewById(R.id.teleop_algae_proc_incr_button);
        m_teleopAlgaeProcDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_teleopAlgaeProcTotal, false, false);
            }
        });
        m_teleopAlgaeProcIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_teleopAlgaeProcTotal, true, false);
            }
        });

        // Check acquired totals for MAX
        if (isGreaterThanMax(m_coralAcquiredTotal,true))
            m_coralAcquiredTotal.setTextColor(Color.RED);
        if (isGreaterThanMax(m_algaeAcquiredTotal,false))
            m_algaeAcquiredTotal.setTextColor(Color.RED);

        // Check coral levels for MAX
        if (isGreaterThanMax(m_teleopL1Total,true))
            m_teleopL1Total.setTextColor(Color.RED);
        if (isGreaterThanMax(m_teleopL2Total,true))
            m_teleopL2Total.setTextColor(Color.RED);
        if (isGreaterThanMax(m_teleopL3Total,true))
            m_teleopL3Total.setTextColor(Color.RED);
        if (isGreaterThanMax(m_teleopL4Total,true))
            m_teleopL4Total.setTextColor(Color.RED);

        return v;
    }

    public void updateTeleopData()
    {
        m_matchData.setCoralAcquired(Integer.parseInt(m_coralAcquiredTotal.getText().toString()));
        m_matchData.setAlgaeAcquired(Integer.parseInt(m_algaeAcquiredTotal.getText().toString()));

        m_matchData.setTeleopAlgaeNet(Integer.parseInt(m_teleopAlgaeNetTotal.getText().toString()));
        m_matchData.setTeleopAlgaeProcessor(Integer.parseInt(m_teleopAlgaeProcTotal.getText().toString()));

        m_matchData.setTeleopCoralL1(Integer.parseInt(m_teleopL1Total.getText().toString()));
        m_matchData.setTeleopCoralL2(Integer.parseInt(m_teleopL2Total.getText().toString()));
        m_matchData.setTeleopCoralL3(Integer.parseInt(m_teleopL3Total.getText().toString()));
        m_matchData.setTeleopCoralL4(Integer.parseInt(m_teleopL4Total.getText().toString()));

    }
}
