package com.frc2135.android.frc_scout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.widget.RadioButton;
import android.widget.RadioGroup;


/** @noinspection SpellCheckingInspection*/
public class TeleopFragment extends Fragment
{
    private static final String TAG = "TeleopFragment";
    private static final int MAX_NUM_HOPPERS = 7;     // algae max expected high total
   // private static final int MAX_NUM_CORAL= 11;     // coral max expected high total

  /*private CheckBox m_pickUpCoralCkbx;
    private CheckBox m_pickUpAlgaeCkbx;
    private CheckBox m_knockAlgaeCkbx;
    private CheckBox m_algaeFromReefCkbx;
    private CheckBox m_holdBothCkbx;*/

    private TextView m_hoppersUsedTotal;
    private Button m_hoppersUsedDecrButton;
    private Button m_hoppersUsedIncrButton;
    /*private TextView m_algaeAcquiredTotal;
    private Button   m_algaeAcquiredDecrButton;
    private Button   m_algaeAcquiredIncrButton;*/

    private RadioGroup m_accuracyButtonGroup;
    private RadioButton m_accuracyMost;
    private RadioButton m_accuracyThreeFourths;
    private RadioButton m_accuracyHalf;
    private RadioButton m_accuracyQuarters;
    private RadioButton m_accuracyFew;
    private RadioButton m_accuracyNone;
    private RadioButton m_accuracyCannotTell;

    private CheckBox m_intakeAndShootCkbx;
    private CheckBox m_NeutralToAlliancePassingCkbx;
    private CheckBox m_AllianceToAlliancePassingCkbx;

    private RadioGroup m_passingEffectivenessButtonsGroup;
    private RadioButton m_passingNA;
    private RadioButton m_passingTons;
    private RadioButton m_passingLarge;
    private RadioButton m_passingMedium;
    private RadioButton m_passingLow;

    private RadioGroup m_defenseButtonGroup;
    private RadioButton m_defenseNone;
    private RadioButton m_defenseLow;
    private RadioButton m_defenseMedium;
    private RadioButton m_defenseHigh;

  /*  private TextView m_teleopL1Total;
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
    private Button m_teleopAlgaeProcDecrButton;*/

    private MatchData m_matchData;

    // Check if pointsTextView field is greater than the MAX_NUM*.
    private boolean isGreaterThanMax(TextView field,boolean bIsHoppers)
    {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if (bIsHoppers == true) {
            if (num > MAX_NUM_HOPPERS)  // for coral number
                rtn = true;
        }
      /*  else  // for algae number
        {
            if (num > MAX_NUM_ALGAE)
                rtn = true;
        }*/
        return rtn;
    }

    // Sets the new result integer value for the given TextView, either decrementing or 
    // incrementing the shown value. If the decrement case falls below zero, returns 0. 
    // Sets textView color to RED if out of expected range.
    public void updateTotalsInt(TextView tview, boolean bIncr, boolean bIsHoppers)
    {
        int result = Integer.parseInt(tview.getText().toString()); // get current value as int
        if (bIncr)
            result += 1;
        else
            result -= 1;
        if (result < 0)
            result = 0;
        tview.setText(String.valueOf(result));

        if (isGreaterThanMax(tview,bIsHoppers))
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
            String teamNumber = m_matchData.getTeamNumber();
            String teamAlias = m_matchData.getTeamAlias();
            if(!teamAlias.equals(""))
                actionBar.setTitle("Teleoperated          Scouting Team " + teamAlias + "         Match " + m_matchData.getMatchNumber());
            else actionBar.setTitle("Teleoperated          Scouting Team " + teamNumber + "         Match " + m_matchData.getMatchNumber());
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

        // Set up listener for hoppersUsed +/-buttons.
        m_hoppersUsedTotal= v.findViewById(R.id.hopper_used_total);
        m_hoppersUsedTotal.setText(String.valueOf(m_matchData.getHoppersUsed()));
        m_hoppersUsedDecrButton = v.findViewById(R.id.hopper_used_decr_button);
        m_hoppersUsedIncrButton = v.findViewById(R.id.hopper_used_incr_button);
        m_hoppersUsedDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_hoppersUsedTotal, false, true);
            }
        });
        m_hoppersUsedIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_hoppersUsedTotal, true, true);
            }
        });

        m_intakeAndShootCkbx = v.findViewById(R.id.intake_and_shoot);
        m_intakeAndShootCkbx.setChecked(m_matchData.getIntakeAndShoot());

        m_NeutralToAlliancePassingCkbx = v.findViewById(R.id.from_neutral_zone_to_alliance_zone);
        m_NeutralToAlliancePassingCkbx.setChecked(m_matchData.getNeutralToAlliancePassing());

        m_AllianceToAlliancePassingCkbx = v.findViewById(R.id.from_alliance_zone_to_other_alliance_zone);
        m_AllianceToAlliancePassingCkbx.setChecked(m_matchData.getAllianceToAlliancePassing());

        // Set up listener for algaeAcquired +/-buttons.
       /* m_algaeAcquiredTotal= v.findViewById(R.id.algae_acquired_total);
        m_algaeAcquiredTotal.setText(String.valueOf(m_matchData.getAlgaeAcquired()));
        m_algaeAcquiredDecrButton = v.findViewById(R.id.algae_acquired_decr_button);
        m_algaeAcquiredIncrButton = v.findViewById(R.id.algae_acquired_incr_button);*/
       /* m_hoppersUsedDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_hoppersUsedTotal, false, false) ;
            }
        });
        m_hoppersUsedIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_hoppersUsedTotal, true, false);
            }
        });*/

        // Set up Coral L1-L4 incr/decr buttons and totals
      /*  m_teleopL1Total = v.findViewById(R.id.teleop_L1_score_total);
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
        m_teleopL4IncrButton = v.findViewById(R.id.teleop_L4_incr_button);*/

        /*m_teleopL1IncrButton.setOnClickListener(new View.OnClickListener()
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
        });*/

        // Set up listener for Scoring Algae Net +/-buttons.
     /*   m_teleopAlgaeNetTotal= v.findViewById(R.id.teleop_algae_net_total);
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
        });*/

        // Set up listener for Scoring Algae Proc +/-buttons.
       /* m_teleopAlgaeProcTotal= v.findViewById(R.id.teleop_algae_proc_total);
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
        });*/


        m_accuracyButtonGroup = v.findViewById(R.id.accuracy_buttons);
        m_accuracyMost = v.findViewById(R.id.accuracy_most);
        m_accuracyThreeFourths = v.findViewById(R.id.accuracy_three_fourths);
        m_accuracyHalf = v.findViewById(R.id.accuracy_half);
        m_accuracyQuarters = v.findViewById(R.id.accuracy_quarter);
        m_accuracyFew = v.findViewById(R.id.accuracy_few);
        m_accuracyNone = v.findViewById(R.id.accuracy_none);
        m_accuracyCannotTell = v.findViewById(R.id.accuracy_cannot_tell);
        m_accuracyMost.setChecked(false);
        m_accuracyThreeFourths.setChecked(false);
        m_accuracyHalf.setChecked(false);
        m_accuracyQuarters.setChecked(false);
        m_accuracyFew.setChecked(false);
        m_accuracyNone.setChecked(false);
        m_accuracyCannotTell.setChecked(false);

        int accValue = m_matchData.getAccuracyRate();
        if (accValue == 0)
            m_accuracyMost.setChecked(true);
        else if(accValue == 1)
            m_accuracyThreeFourths.setChecked(true);
        else if(accValue == 2)
            m_accuracyHalf.setChecked(true);
        else if(accValue == 3)
            m_accuracyQuarters.setChecked(true);
        else if(accValue == 4)
            m_accuracyFew.setChecked(true);
        else if(accValue == 5)
            m_accuracyNone.setChecked(true);
        else if(accValue == 6)
            m_accuracyCannotTell.setChecked(true);

        m_passingEffectivenessButtonsGroup = v.findViewById(R.id.passing_effectiveness_buttons);
        m_passingNA = v.findViewById(R.id.passing_rate_na);
        m_passingTons = v.findViewById(R.id.passing_tons);
        m_passingLarge = v.findViewById(R.id.passing_large);
        m_passingMedium = v.findViewById(R.id.passing_medium);
        m_passingLow = v.findViewById(R.id.passing_low);

        m_passingNA.setChecked(false);
        m_passingTons.setChecked(false);
        m_passingLarge.setChecked(false);
        m_passingMedium.setChecked(false);
        m_passingLow.setChecked(false);

        int passValue = m_matchData.getPassingEffectivenessrate();
        if (passValue == 0)
            m_passingNA.setChecked(true);
        else if(passValue == 1)
          m_passingTons.setChecked(true);
        else if(passValue == 2)
            m_passingLarge.setChecked(true);
        else if(passValue == 3)
            m_passingMedium.setChecked(true);
        else if(passValue == 4)
            m_passingLow.setChecked(true);

        m_defenseButtonGroup = v.findViewById(R.id.defense_buttons);
        m_defenseNone = v.findViewById(R.id.defense_none);
        m_defenseLow = v.findViewById(R.id.defense_low);
        m_defenseMedium = v.findViewById(R.id.defense_medium);
        m_defenseHigh = v.findViewById(R.id.defense_high);
        m_defenseNone.setChecked(false);
        m_defenseLow.setChecked(false);
        m_defenseMedium.setChecked(false);
        m_defenseHigh.setChecked(false);

        int defValue = m_matchData.getPlayedDefense();
        if (defValue == 0)
            m_defenseNone.setChecked(true);
        else if(defValue == 1)
          m_defenseLow.setChecked(true);
        else if(defValue == 2)
            m_defenseMedium.setChecked(true);
        else if(defValue == 3)
            m_defenseHigh.setChecked(true);

        // Check acquired totals for MAX
        if (isGreaterThanMax(m_hoppersUsedTotal,true))
            m_hoppersUsedTotal.setTextColor(Color.RED);
        if (isGreaterThanMax(m_hoppersUsedTotal,false))
            m_hoppersUsedTotal.setTextColor(Color.RED);

        // Check coral levels for MAX
       /* if (isGreaterThanMax(m_teleopL1Total,true))
            m_teleopL1Total.setTextColor(Color.RED);
        if (isGreaterThanMax(m_teleopL2Total,true))
            m_teleopL2Total.setTextColor(Color.RED);
        if (isGreaterThanMax(m_teleopL3Total,true))
            m_teleopL3Total.setTextColor(Color.RED);
        if (isGreaterThanMax(m_teleopL4Total,true))
            m_teleopL4Total.setTextColor(Color.RED);*/

        return v;
    }
    
    public int getCurrentAccuracyLevel()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyMost.getId())
        {
            rtn = 0;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyThreeFourths.getId())
        {
            rtn = 1;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyHalf.getId())
        {
            rtn = 2;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyQuarters.getId())
        {
            rtn = 3;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyFew.getId())
        {
            rtn = 4;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyNone.getId())
        {
            rtn = 5;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyCannotTell.getId())
        {
            rtn = 6;
        }
        return rtn;
    }

    public int getCurrentPassingLevel()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_passingEffectivenessButtonsGroup.getCheckedRadioButtonId() == m_passingNA.getId())
        {
            rtn = 0;
        }
        if (m_passingEffectivenessButtonsGroup.getCheckedRadioButtonId() == m_passingTons.getId())
        {
            rtn = 1;
        }
        if (m_passingEffectivenessButtonsGroup.getCheckedRadioButtonId() == m_passingLarge.getId())
        {
            rtn = 2;
        }
        if (m_passingEffectivenessButtonsGroup.getCheckedRadioButtonId() == m_passingMedium.getId())
        {
            rtn = 3;
        }
        if (m_passingEffectivenessButtonsGroup.getCheckedRadioButtonId() == m_passingLow.getId())
        {
            rtn = 4;
        }
        return rtn;
    }

    public int getCurrentDefenseLevel()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_defenseButtonGroup.getCheckedRadioButtonId() == m_defenseNone.getId())
        {
            rtn = 0;
        }
        if (m_defenseButtonGroup.getCheckedRadioButtonId() == m_defenseLow.getId())
        {
            rtn = 1;
        }
        if (m_defenseButtonGroup.getCheckedRadioButtonId() == m_defenseMedium.getId())
        {
            rtn = 2;
        }
        if (m_defenseButtonGroup.getCheckedRadioButtonId() == m_defenseHigh .getId())
        {
            rtn = 3;
        }
        return rtn;
    }

    public void updateTeleopData()
    {
        m_matchData.setHoppersUsed(Integer.parseInt(m_hoppersUsedTotal.getText().toString()));
        m_matchData.setAccuracyRate(getCurrentAccuracyLevel());
        m_matchData.setPassingEffectivenessRate(getCurrentPassingLevel());
        //m_matchData.setHoppersUsed(Integer.parseInt(m_algaeAcquiredTotal.getText().toString()));

       /* m_matchData.setTeleopAlgaeNet(Integer.parseInt(m_teleopAlgaeNetTotal.getText().toString()));
        m_matchData.setTeleopAlgaeProcessor(Integer.parseInt(m_teleopAlgaeProcTotal.getText().toString()));

        m_matchData.setTeleopCoralL1(Integer.parseInt(m_teleopL1Total.getText().toString()));
        m_matchData.setTeleopCoralL2(Integer.parseInt(m_teleopL2Total.getText().toString()));
        m_matchData.setTeleopCoralL3(Integer.parseInt(m_teleopL3Total.getText().toString()));
        m_matchData.setTeleopCoralL4(Integer.parseInt(m_teleopL4Total.getText().toString()));*/
        m_matchData.setPlayedDefense(getCurrentDefenseLevel());

        m_matchData.setIntakeAndShoot(m_intakeAndShootCkbx.isChecked());
        m_matchData.setNeutralToAlliancePassing(m_NeutralToAlliancePassingCkbx.isChecked());
        m_matchData.setAllianceToAlliancePassing(m_AllianceToAlliancePassingCkbx.isChecked());

    }
}
