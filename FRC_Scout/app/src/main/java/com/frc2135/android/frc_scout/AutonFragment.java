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
    private static final int MAX_NUM_HOPPERS = 1;
    //private static final int MAX_NUM_ALGAE = 2;
    private CheckBox m_preloadCheckbox;
    private CheckBox m_azCheckbox;
    private CheckBox m_depotCheckbox;
    private CheckBox m_outpostCheckbox;
    private CheckBox m_nzCheckbox;


    private TextView m_autonHopperTotal;
    private Button m_autonHopperIncrButton;
    private Button m_autonHopperDecrButton;

    private RadioGroup m_autonAccuracyButtonGroup;
    private RadioButton m_autonAccuracyMost;
    private RadioButton m_autonAccuracyThreeFourths;
    private RadioButton m_autonAccuracyHalf;
    private RadioButton m_autonAccuracyQuarters;
    private RadioButton m_autonAccuracyFew;
    private RadioButton m_autonAccuracyNone;
    private RadioButton m_autonAccuracyNA;


    private RadioGroup m_autonAccuracyBoxes;
    private RadioButton m_autonAccuracyNo;
    private RadioButton m_autonAccuracyM;
    private RadioButton m_autonAccuracyS;
    private RadioButton m_autonAccuracyF;
    private RadioButton m_autonAccuracyN;
    private RadioButton m_autonAccuracyA;

    private RadioGroup m_autonClimbButtonGroup;
    private RadioButton m_autonLeft;
    private RadioButton m_autonFront;
    private RadioButton m_autonRight;
    private RadioButton m_autonBack;
    private RadioButton m_autonNA;


    private MatchData m_matchData;

    // Check if given field is greater than expected max number.
    private boolean isGreaterThanMax(TextView field,boolean bIsCoral)
    {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if (bIsCoral == true) {
            if (num > MAX_NUM_HOPPERS)  // for coral number
                rtn = true;
        } /* else  // for algae number
        {       
            if (num > MAX_NUM_ALGAE)
                rtn = true;
        }*/
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
                String teamNumber = m_matchData.getTeamNumber();
                String teamAlias = m_matchData.getTeamAlias();
                if (m_actionBar != null)
                {
                    // Use teamAlias if there is one instead of teamNumber.
                    if(!teamAlias.equals(""))
                        m_actionBar.setTitle("Autonomous          Scouting Team " + teamAlias + "         Match " + m_matchData.getMatchNumber());
                    else m_actionBar.setTitle("Autonomous          Scouting Team " + teamNumber + "         Match " + m_matchData.getMatchNumber());
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

        m_autonHopperTotal = v.findViewById(R.id.auton_Hopper_score_total);
        m_autonHopperTotal.setText(String.valueOf(m_matchData.getAutonHopper()));
        m_autonHopperDecrButton = v.findViewById(R.id.auton_Hopper_decr_button);
        m_autonHopperIncrButton = v.findViewById(R.id.auton_Hopper_incr_button);

        // Set up Coral L1-L4 incr/decr buttons.

        m_autonHopperIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonHopperTotal, true, true);
            }
        });

        m_autonHopperDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalsInt(m_autonHopperTotal, false, true);
            }
        });

        m_preloadCheckbox = v.findViewById(R.id.preload_checkbox);
        m_preloadCheckbox.setChecked(m_matchData.getAutonPreload());

        m_azCheckbox = v.findViewById(R.id.az_checkbox);
        m_azCheckbox.setChecked(m_matchData.getAutonAzCheckbox());
        m_depotCheckbox = v.findViewById(R.id.depot_checkbox);
        m_depotCheckbox.setChecked(m_matchData.getAutonDepotCheckbox());
        m_outpostCheckbox = v.findViewById(R.id.outpost_checkbox);
        m_outpostCheckbox.setChecked(m_matchData.getAutonOutpostCheckbox());
        m_nzCheckbox = v.findViewById(R.id.nz_checkbox);
        m_nzCheckbox.setChecked(m_matchData.getAutonNzCheckbox());

        m_autonAccuracyBoxes = v.findViewById(R.id.auton_accuracy_boxes);
        m_autonAccuracyNo = v.findViewById(R.id.auton_accuracy_no);
        m_autonAccuracyM = v.findViewById(R.id.auton_accuracy_m);
        m_autonAccuracyS = v.findViewById(R.id.auton_accuracy_s);
        m_autonAccuracyF = v.findViewById(R.id.auton_accuracy_f);
        m_autonAccuracyN = v.findViewById(R.id.auton_accuracy_n);
        m_autonAccuracyA = v.findViewById(R.id.auton_accuracy_a);
        m_autonAccuracyNo.setChecked(false);
        m_autonAccuracyM.setChecked(false);
        m_autonAccuracyS.setChecked(false);
        m_autonAccuracyF.setChecked(false);
        m_autonAccuracyN.setChecked(false);
        m_autonAccuracyA.setChecked(false);


        m_autonAccuracyButtonGroup = v.findViewById(R.id.auton_accuracy_buttons);
        m_autonAccuracyMost = v.findViewById(R.id.auton_accuracy_most);
        m_autonAccuracyThreeFourths = v.findViewById(R.id.auton_accuracy_three_fourths);
        m_autonAccuracyHalf = v.findViewById(R.id.auton_accuracy_half);
        m_autonAccuracyQuarters = v.findViewById(R.id.auton_accuracy_quarter);
        m_autonAccuracyFew = v.findViewById(R.id.auton_accuracy_few);
        m_autonAccuracyNone = v.findViewById(R.id.auton_accuracy_none);
        m_autonAccuracyNA= v.findViewById(R.id.auton_accuracy_na);
        m_autonAccuracyMost.setChecked(false);
        m_autonAccuracyThreeFourths.setChecked(false);
        m_autonAccuracyHalf.setChecked(false);
        m_autonAccuracyQuarters.setChecked(false);
        m_autonAccuracyFew.setChecked(false);
        m_autonAccuracyNone.setChecked(false);
        m_autonAccuracyNA.setChecked(false);

        m_autonClimbButtonGroup = v.findViewById(R.id.auton_climb_buttons);
        m_autonLeft = v.findViewById(R.id.auton_climb_left);
        m_autonFront = v.findViewById(R.id.auton_climb_front);
        m_autonRight = v.findViewById(R.id.auton_climb_right);
        m_autonBack = v.findViewById(R.id.auton_climb_back);
        m_autonNA = v.findViewById(R.id.auton_climb_na);
        m_autonLeft.setChecked(false);
        m_autonFront.setChecked(false);
        m_autonRight.setChecked(false);
        m_autonBack.setChecked(false);
        m_autonNA.setChecked(false);


        int accValue = m_matchData.getAutonAccuracyRate();
        if (accValue == 0)
            m_autonAccuracyNA.setChecked(true);
        else if(accValue == 1)
            m_autonAccuracyMost.setChecked(true);
        else if(accValue == 2)
            m_autonAccuracyThreeFourths.setChecked(true);
        else if(accValue == 3)
            m_autonAccuracyHalf.setChecked(true);
        else if(accValue == 4)
            m_autonAccuracyQuarters.setChecked(true);
        else if(accValue == 5)
            m_autonAccuracyFew.setChecked(true);
        else if(accValue == 6)
            m_autonAccuracyNone.setChecked(true);


        int BaccValue = m_matchData.getPreloadAccuracyLevel();
        if (BaccValue == 0)
            m_autonAccuracyNo.setChecked(true);
        else if(BaccValue == 1)
            m_autonAccuracyA.setChecked(true);
        else if(BaccValue == 2)
            m_autonAccuracyM.setChecked(true);
        else if(BaccValue == 3)
            m_autonAccuracyS.setChecked(true);
        else if(BaccValue == 4)
            m_autonAccuracyF.setChecked(true);
        else if(BaccValue == 5)
            m_autonAccuracyN.setChecked(true);


        int CaccValue = m_matchData.getAutonClimb();
        if (CaccValue == 0)
            m_autonNA.setChecked(true);
        else if(CaccValue == 1)
            m_autonBack.setChecked(true);
        else if(CaccValue == 2)
            m_autonLeft.setChecked(true);
        else if(CaccValue == 3)
            m_autonFront.setChecked(true);
        else if(CaccValue == 4)
            m_autonRight.setChecked(true);


        // Set up Algae Net/Proc incr/decr buttons and listeners.
        // Check coral levels for MAX
        if (isGreaterThanMax(m_autonHopperTotal,true))
            m_autonHopperTotal.setTextColor(Color.RED);

        // Check algae for MAX
        /*
        if (isGreaterThanMax(m_autonAlgaeNetTotal,false))
            m_autonAlgaeNetTotal.setTextColor(Color.RED);
        if (isGreaterThanMax(m_autonAlgaeProcTotal,false))
            m_autonAlgaeProcTotal.setTextColor(Color.RED);

         */

        return v;
    }

    public int getAutonAccuracyRate()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_autonAccuracyButtonGroup.getCheckedRadioButtonId() == m_autonAccuracyNA.getId())
        {
            rtn = 0;
        }
        if (m_autonAccuracyButtonGroup.getCheckedRadioButtonId() == m_autonAccuracyMost.getId())
        {
            rtn = 1;
        }
        if (m_autonAccuracyButtonGroup.getCheckedRadioButtonId() == m_autonAccuracyThreeFourths.getId())
        {
            rtn = 2;
        }
        if (m_autonAccuracyButtonGroup.getCheckedRadioButtonId() == m_autonAccuracyHalf.getId())
        {
            rtn = 3;
        }
        if (m_autonAccuracyButtonGroup.getCheckedRadioButtonId() == m_autonAccuracyQuarters.getId())
        {
            rtn = 4;
        }
        if (m_autonAccuracyButtonGroup.getCheckedRadioButtonId() == m_autonAccuracyFew.getId())
        {
            rtn = 5;
        }
        if (m_autonAccuracyButtonGroup.getCheckedRadioButtonId() == m_autonAccuracyNone.getId())
        {
            rtn = 6;
        }
        return rtn;
    }


    public int getPreloadAccuracyLevel()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_autonAccuracyBoxes.getCheckedRadioButtonId() == m_autonAccuracyNo.getId())
        {
            rtn = 0;
        }
        if (m_autonAccuracyBoxes.getCheckedRadioButtonId() == m_autonAccuracyA.getId())
        {
            rtn = 1;
        }
        if (m_autonAccuracyBoxes.getCheckedRadioButtonId() == m_autonAccuracyM.getId())
        {
            rtn = 2;
        }
        if (m_autonAccuracyBoxes.getCheckedRadioButtonId() == m_autonAccuracyS.getId())
        {
            rtn = 3;
        }
        if (m_autonAccuracyBoxes.getCheckedRadioButtonId() == m_autonAccuracyF.getId())
        {
            rtn = 4;
        }
        if (m_autonAccuracyBoxes.getCheckedRadioButtonId() == m_autonAccuracyN.getId())
        {
            rtn = 5;
        }
        return rtn;
    }


    public int getAutonClimb()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_autonClimbButtonGroup.getCheckedRadioButtonId() == m_autonNA.getId())
        {
            rtn = 0;
        }
        if (m_autonClimbButtonGroup.getCheckedRadioButtonId() == m_autonBack.getId())
        {
            rtn = 1;
        }
        if (m_autonClimbButtonGroup.getCheckedRadioButtonId() == m_autonLeft.getId())
        {
            rtn = 2;
        }
        if (m_autonClimbButtonGroup.getCheckedRadioButtonId() == m_autonFront.getId())
        {
            rtn = 3;
        }
        if (m_autonClimbButtonGroup.getCheckedRadioButtonId() == m_autonRight.getId())
        {
            rtn = 4;
        }
        return rtn;
    }


    public void updateAutonData()
    {

        m_matchData.setAutonHopper(Integer.parseInt(m_autonHopperTotal.getText().toString()));
        m_matchData.setAutonPreload(m_preloadCheckbox.isChecked());
        m_matchData.setAutonNzCheckbox(m_nzCheckbox.isChecked());
        m_matchData.setAutonAzCheckbox(m_azCheckbox.isChecked());
        m_matchData.setAutonDepotCheckbox(m_depotCheckbox.isChecked());
        m_matchData.setAutonOutpostCheckbox(m_outpostCheckbox.isChecked());
        m_matchData.setAutonAccuracyRate(getAutonAccuracyRate());
        m_matchData.setPreloadAccuracyLevel(getPreloadAccuracyLevel());
        m_matchData.setAutonClimb(getAutonClimb());


        // Determine the reefscape face for each checkbox.
    }
}
