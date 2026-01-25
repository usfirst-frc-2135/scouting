package com.frc2135.android.frc_scout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Random;


/** @noinspection SpellCheckingInspection*/
public class TeleopFragment extends Fragment
{
    private static final String TAG = "TeleopFragment";
    private static final int MAX_NUM_HOPPERS = 7;     // hoppers max expected high total

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
    private RadioButton m_accuracyNA;

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


    private LinearLayout photo;

    private MatchData m_matchData;

    private int m_photoNum;

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

        m_accuracyButtonGroup = v.findViewById(R.id.accuracy_buttons);
        m_accuracyMost = v.findViewById(R.id.accuracy_most);
        m_accuracyThreeFourths = v.findViewById(R.id.accuracy_three_fourths);
        m_accuracyHalf = v.findViewById(R.id.accuracy_half);
        m_accuracyQuarters = v.findViewById(R.id.accuracy_quarter);
        m_accuracyFew = v.findViewById(R.id.accuracy_few);
        m_accuracyNone = v.findViewById(R.id.accuracy_none);
        m_accuracyCannotTell = v.findViewById(R.id.accuracy_cannot_tell);
        m_accuracyNA = v.findViewById(R.id.accuracy_NA);
        m_accuracyMost.setChecked(false);
        m_accuracyThreeFourths.setChecked(false);
        m_accuracyHalf.setChecked(false);
        m_accuracyQuarters.setChecked(false);
        m_accuracyFew.setChecked(false);
        m_accuracyNone.setChecked(false);
        m_accuracyCannotTell.setChecked(false);
        m_accuracyNA.setChecked(false);

        int accValue = m_matchData.getAccuracyRate();
        if (accValue == 0)
            m_accuracyNA.setChecked(true);
        else if(accValue == 1)
            m_accuracyMost.setChecked(true);
        else if(accValue == 2)
            m_accuracyThreeFourths.setChecked(true);
        else if(accValue == 3)
            m_accuracyHalf.setChecked(true);
        else if(accValue == 4)
            m_accuracyQuarters.setChecked(true);
        else if(accValue == 5)
            m_accuracyFew.setChecked(true);
        else if(accValue == 6)
            m_accuracyNone.setChecked(true);
        else if(accValue == 7)
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

        m_photoNum = m_matchData.getTeleopPhoto();
        Log.i(TAG, "onCreateView: from matchdata, image= " + m_photoNum);

        LinearLayout photo = (LinearLayout)v.findViewById(R.id.photo);

        ViewGroup.LayoutParams params = photo.getLayoutParams();

        if (m_photoNum == 0)
        {
            Random random = new Random();
            double rand = random.nextDouble();

            Log.i(TAG, "onCreateView: from matchdata, random = " + rand);

            if (rand < 0.03)
            {
                m_photoNum = 3;
                m_matchData.setTeleopPhoto(3);
                photo.setBackgroundResource(R.drawable.me_and_charlotte);
            }
            else if (rand < 0.05)
            {
                m_photoNum = 5;
                m_matchData.setTeleopPhoto(5);
                photo.setBackgroundResource(R.drawable.me_and_charlotte_2);
            }
            else if (rand < 0.3)
            {
                m_photoNum = 4;
                m_matchData.setTeleopPhoto(4);
                photo.setBackgroundResource(R.drawable.frc_logo);
                params.width = 450;
                photo.setLayoutParams(params);

            }
            else if (rand < 0.4)
            {
                m_photoNum = 2;
                m_matchData.setTeleopPhoto(2);
                photo.setBackgroundResource(R.drawable.rebuilt_logo);
                params.width = 250;
                photo.setLayoutParams(params);
            }
            else if (rand < 0.5)
            {
                m_photoNum = 6;
                m_matchData.setTeleopPhoto(6);
                photo.setBackgroundResource(R.drawable.t2135_logo2);
                params.width = 260;
                photo.setLayoutParams(params);
            }
            else
            {
                m_photoNum = 1;
                m_matchData.setTeleopPhoto(1);
                photo.setBackgroundResource(R.drawable.rebuilt_fuel);
                params.width = 250;
                photo.setLayoutParams(params);

                Random shiny = new Random();
                double shine = shiny.nextDouble();

                Log.i(TAG, "onCreateView: from matchdata, random = " + shine);

                if (shine < 0.1)
                {
                    m_photoNum = 7;
                    m_matchData.setTeleopPhoto(7);
                    photo.setBackgroundResource(R.drawable.rebuilt_fuel_shiny);
                    params.width = 250;
                    photo.setLayoutParams(params);
                }
            }
        }
        else if (m_photoNum == 1)
        {
            photo.setBackgroundResource(R.drawable.rebuilt_fuel);
            params.width = 250;
            photo.setLayoutParams(params);
        }
        else if (m_photoNum == 2)
        {
            photo.setBackgroundResource(R.drawable.rebuilt_logo);
            params.width = 250;
            photo.setLayoutParams(params);
        }
        else if (m_photoNum == 3) {
            photo.setBackgroundResource(R.drawable.me_and_charlotte);
        }
        else if (m_photoNum == 4) {
            photo.setBackgroundResource(R.drawable.frc_logo);
            params.width = 450;
            photo.setLayoutParams(params);
        }
        else if (m_photoNum == 5) {
            photo.setBackgroundResource(R.drawable.me_and_charlotte_2);
        }
        else if (m_photoNum == 6) {
            photo.setBackgroundResource(R.drawable.t2135_logo2);
            params.width = 260;
            photo.setLayoutParams(params);
        }
        else if (m_photoNum == 7) {
            photo.setBackgroundResource(R.drawable.rebuilt_fuel_shiny);
            params.width = 250;
            photo.setLayoutParams(params);
        }
        return v;
    }

    public int getCurrentAccuracyLevel()
    {
        // Returns the integer accuracy level that is current checked in the radio buttons
        int rtn = 0;
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyNA.getId())
        {
            rtn = 0;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyMost.getId())
        {
            rtn = 1;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyThreeFourths.getId())
        {
            rtn = 2;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyHalf.getId())
        {
            rtn = 3;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyQuarters.getId())
        {
            rtn = 4;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyFew.getId())
        {
            rtn = 5;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyNone.getId())
        {
            rtn = 6;
        }
        if (m_accuracyButtonGroup.getCheckedRadioButtonId() == m_accuracyCannotTell.getId())
        {
            rtn = 7;
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
        m_matchData.setTeleopPhoto(m_photoNum);
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
