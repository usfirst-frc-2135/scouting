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
    private static final int MAX_POINTS_ALGAE = 7;     // max for valid high or low points total
    private static final int MAX_POINTS_CORAL= 11;     // max for valid high or low points total

    private CheckBox m_pickUpCoral;
    private CheckBox m_pickUpAlgae;
    private CheckBox m_knockAlgaeOff;
    private CheckBox m_algaeFromReef;
    private CheckBox m_holdBothElements;
    private TextView m_coralAcquire;
    private TextView m_algaeAcquire;

    private RadioGroup m_defenseButtonGroup;
    private RadioButton m_defenseNone;
    private RadioButton m_defenseLow;
    private RadioButton m_defenseMedium;
    private RadioButton m_defenseHigh;

    private RadioGroup m_foulPin;
    private RadioButton m_zeroPin;
    private RadioButton m_onePin;
    private RadioButton m_twoPin;

    private RadioGroup m_foulAnchor;
    private RadioButton m_zeroAnchor;
    private RadioButton m_oneAnchor;
    private RadioButton m_twoAnchor;

    private RadioGroup m_foulCage;
    private RadioButton m_zeroCage;
    private RadioButton m_oneCage;
    private RadioButton m_twoCage;

    private RadioGroup m_teleopFoulBarge;
    private RadioButton m_teleopZeroBarge;
    private RadioButton m_teleopOneBarge;
    private RadioButton m_teleopTwoBarge;

    private RadioGroup m_teleopFoulReef;
    private RadioButton m_teleopZeroReef;
    private RadioButton m_teleopOneReef;
    private RadioButton m_teleopTwoReef;

    private TextView m_teleopCoralText;
    private TextView m_teleopCoralTextL1;
    private TextView m_teleopCoralTextL2;
    private TextView m_teleopCoralTextL3;
    private TextView m_teleopCoralTextL4;

    private RadioGroup m_teleopCoralButtonGroup;
    private RadioButton m_teleopCoralButtonL1;
    private RadioButton m_teleopCoralButtonL2;
    private RadioButton m_teleopCoralButtonL3;
    private RadioButton m_teleopCoralButtonL4;
    private RadioButton m_teleopCoralButtonNone;

    private Button m_teleopCoralIncr;
    private Button m_teleopCoralDecr;
    private Button m_teleopCoralDecrDisabled;
    private Button m_teleopCoralIncrDisabled;
    private Button m_teleopCoralIncrL1;
    private Button m_teleopCoralDecrL1;
    private Button m_teleopCoralIncrL2;
    private Button m_teleopCoralDecrL2;
    private Button m_teleopCoralIncrL3;
    private Button m_teleopCoralDecrL3;
    private Button m_teleopCoralIncrL4;
    private Button m_teleopCoralDecrL4;

    private TextView m_teleopCoralL1;
    private TextView m_teleopCoralL2;
    private TextView m_teleopCoralL3;
    private TextView m_teleopCoralL4;

    private TextView m_teleopAlgaeText;
    private TextView m_teleopAlgaeTextNet;
    private TextView m_teleopAlgaeTextProcessor;

    private RadioGroup m_teleopAlgaeButtonGroup;
    private RadioButton m_teleopAlgaeButtonNet;
    private RadioButton m_teleopAlgaeButtonProcessor;
    private RadioButton m_teleopAlgaeButtonNone;

    private Button m_teleopAlgaeIncr;
    private Button m_teleopAlgaeDecr;
    private Button m_teleopAlgaeIncrDisabled;
    private Button m_teleopAlgaeDecrDisabled;
    private Button m_teleopAlgaeIncrNet;
    private Button m_teleopAlgaeDecrNet;
    private Button m_teleopAlgaeIncrProcessor;
    private Button m_teleopAlgaeDecrProcessor;

    private TextView m_teleopAlgaeNet;
    private TextView m_teleopAlgaeProcessor;

    private MatchData m_matchData;

    // Check if pointsTextView field is greater than the MAX_POINTS.
    private boolean isGreaterThanMax(TextView field,boolean bIsCoral)
    {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if (bIsCoral == true) {
            if (num > MAX_POINTS_CORAL)  // for coral number
                rtn = true;
        } else  // for algae number
        {
            if (num > MAX_POINTS_ALGAE)
                rtn = true;
        }
        return rtn;
    }



    // Sets the new result integer value for the given Button, either decrementing or incrementing it.
    // If the decrement case falls below zero, returns 0. Sets textView to RED if out of valid range.
    public void updatePointsInt(TextView pointsTextView, boolean bIncr, boolean bIsCoral)
    {
        int result = Integer.parseInt(pointsTextView.getText().toString()); // get current value as int
        if (bIncr)
            result += 1;
        else
            result -= 1;
        if (result < 0)
            result = 0;
        pointsTextView.setText(String.valueOf(result));

        if (isGreaterThanMax(pointsTextView,bIsCoral))
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

    /*
     */
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
        Context context = getContext();
        if (context != null)
        {

            int specialTextPrimaryColor = ContextCompat.getColor(context, R.color.specialTextPrimary);

            // Setup TextViews that displays points, setting 0 as the default.
            // defense buttons
            m_teleopCoralText = v.findViewById(R.id.teleop_coral_scoring_text);
            m_teleopCoralText.setText("0");
            m_teleopCoralText.setTextColor(specialTextPrimaryColor);

            // Sets up TextView that displays speaker note points, setting 0 as the default
            m_teleopAlgaeText = v.findViewById(R.id.teleop_algae_scoring_text);
            m_teleopAlgaeText.setText("0");
            m_teleopAlgaeText.setTextColor(specialTextPrimaryColor);

            m_teleopCoralTextL1 = v.findViewById(R.id.teleop_coral_scoring_text_L1);
            m_teleopCoralTextL1.setText("0");
            m_teleopCoralTextL1.setTextColor(specialTextPrimaryColor);

            m_teleopCoralTextL2 = v.findViewById(R.id.teleop_coral_scoring_text_L2);
            m_teleopCoralTextL2.setText("0");
            m_teleopCoralTextL2.setTextColor(specialTextPrimaryColor);

            m_teleopCoralTextL3 = v.findViewById(R.id.teleop_coral_scoring_text_L3);
            m_teleopCoralTextL3.setText("0");
            m_teleopCoralTextL3.setTextColor(specialTextPrimaryColor);

            m_teleopCoralTextL4 = v.findViewById(R.id.teleop_coral_scoring_text_L4);
            m_teleopCoralTextL4.setText("0");
            m_teleopCoralTextL4.setTextColor(specialTextPrimaryColor);

            m_teleopAlgaeTextNet = v.findViewById(R.id.teleop_algae_net_scoring);
            m_teleopAlgaeTextNet.setText("0");
            m_teleopAlgaeTextNet.setTextColor(specialTextPrimaryColor);

            m_teleopAlgaeTextProcessor = v.findViewById(R.id.teleop_algae_processor_scoring);
            m_teleopAlgaeTextProcessor.setText("0");
            m_teleopAlgaeTextProcessor.setTextColor(specialTextPrimaryColor);

            m_teleopCoralIncr = v.findViewById(R.id.teleop_coral_scoring_incr);
            m_teleopCoralDecr = v.findViewById(R.id.teleop_coral_scoring_decr);
            m_teleopCoralIncr.setEnabled(false);
            m_teleopCoralDecr.setEnabled(false);
            m_teleopCoralDecr.setVisibility(View.INVISIBLE);
            m_teleopCoralIncr.setVisibility(View.INVISIBLE);

            m_teleopCoralDecrDisabled = v.findViewById(R.id.teleop_coral_scoring_decr_disabled);
            m_teleopCoralDecrDisabled.setVisibility(View.VISIBLE);
            m_teleopCoralIncrDisabled = v.findViewById(R.id.teleop_coral_scoring_incr_disabled);
            m_teleopCoralIncrDisabled.setVisibility(View.VISIBLE);

            m_teleopAlgaeIncr = v.findViewById(R.id.teleop_algae_scoring_incr);
            m_teleopAlgaeDecr = v.findViewById(R.id.teleop_algae_scoring_decr);
            m_teleopAlgaeIncr.setEnabled(false);
            m_teleopAlgaeDecr.setEnabled(false);
            m_teleopAlgaeIncr.setVisibility(View.INVISIBLE);
            m_teleopAlgaeDecr.setVisibility(View.INVISIBLE);

            m_teleopAlgaeIncrDisabled = v.findViewById(R.id.teleop_algae_scoring_incr_disabled);
            m_teleopAlgaeIncrDisabled.setVisibility(View.VISIBLE);
            m_teleopAlgaeDecrDisabled = v.findViewById(R.id.teleop_algae_scoring_decr_disabled);
            m_teleopAlgaeDecrDisabled.setVisibility(View.VISIBLE);

            m_teleopCoralIncrL1 = v.findViewById(R.id.teleop_coral_scoring_incr_L1);
            m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
            m_teleopCoralIncrL2 = v.findViewById(R.id.teleop_coral_scoring_incr_L2);
            m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
            m_teleopCoralIncrL3 = v.findViewById(R.id.teleop_coral_scoring_incr_L3);
            m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
            m_teleopCoralIncrL4 = v.findViewById(R.id.teleop_coral_scoring_incr_L4);
            m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);

            m_teleopAlgaeIncrNet = v.findViewById(R.id.teleop_algae_scoring_incr_net);
            m_teleopAlgaeIncrNet.setVisibility(View.INVISIBLE);
            m_teleopAlgaeIncrProcessor = v.findViewById(R.id.teleop_algae_scoring_incr_processor);
            m_teleopAlgaeIncrProcessor.setVisibility(View.INVISIBLE);

            m_teleopCoralDecrL1 = v.findViewById(R.id.teleop_coral_scoring_decr_L1);
            m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
            m_teleopCoralDecrL2 = v.findViewById(R.id.teleop_coral_scoring_decr_L2);
            m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
            m_teleopCoralDecrL3 = v.findViewById(R.id.teleop_coral_scoring_decr_L3);
            m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
            m_teleopCoralDecrL4 = v.findViewById(R.id.teleop_coral_scoring_decr_L4);
            m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);

            m_teleopAlgaeDecrNet = v.findViewById(R.id.teleop_algae_scoring_decr_net);
            m_teleopAlgaeDecrNet.setVisibility(View.INVISIBLE);
            m_teleopAlgaeDecrProcessor = v.findViewById(R.id.teleop_algae_scoring_decr_processor);
            m_teleopAlgaeDecrProcessor.setVisibility(View.INVISIBLE);

            m_teleopCoralButtonGroup = v.findViewById(R.id.teleop_coral_radio_group);
            m_teleopCoralButtonNone = v.findViewById(R.id.teleop_coral_none);
            m_teleopCoralButtonNone.setChecked(true);
            m_teleopCoralButtonL1 = v.findViewById(R.id.teleop_coral_L1);
            m_teleopCoralButtonL1.setChecked(false);
            m_teleopCoralButtonL2 = v.findViewById(R.id.teleop_coral_L2);
            m_teleopCoralButtonL2.setChecked(false);
            m_teleopCoralButtonL3 = v.findViewById(R.id.teleop_coral_L3);
            m_teleopCoralButtonL3.setChecked(false);
            m_teleopCoralButtonL4 = v.findViewById(R.id.teleop_coral_L4);
            m_teleopCoralButtonL4.setChecked(false);

            m_teleopAlgaeButtonGroup = v.findViewById(R.id.teleop_algae_radio_group);
            m_teleopAlgaeButtonNone = v.findViewById(R.id.teleop_algae_none_button);
            m_teleopAlgaeButtonNone.setChecked(true);
            m_teleopAlgaeButtonNet = v.findViewById(R.id.teleop_algae_net_button);
            m_teleopAlgaeButtonNet.setChecked(false);
            m_teleopAlgaeButtonProcessor = v.findViewById(R.id.teleop_algae_processor_button);
            m_teleopAlgaeButtonProcessor.setChecked(false);

            m_teleopCoralL1 = v.findViewById(R.id.teleop_coral_scoring_L1);
            m_teleopCoralL2 = v.findViewById(R.id.teleop_coral_scoring_L2);
            m_teleopCoralL3 = v.findViewById(R.id.teleop_coral_scoring_L3);
            m_teleopCoralL4 = v.findViewById(R.id.teleop_coral_scoring_L4);

            m_teleopAlgaeNet = v.findViewById(R.id.teleop_algae_net);
            m_teleopAlgaeProcessor = v.findViewById(R.id.teleop_algae_processor);

            m_defenseButtonGroup = v.findViewById(R.id.defense_buttons);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_defenseNone = v.findViewById(R.id.defense_none);//Sets up radio button that corresponds to 0
            m_defenseLow = v.findViewById(R.id.defense_low);//Sets up radio button that corresponds to 1
            m_defenseMedium = v.findViewById(R.id.defense_medium);//Sets up radio button that corresponds to 2
            m_defenseHigh = v.findViewById(R.id.defense_high);//Sets up radio button that corresponds to 3
            m_defenseNone.setChecked(false);
            m_defenseLow.setChecked(false);
            m_defenseMedium.setChecked(false);
            m_defenseHigh.setChecked(false);

            m_teleopCoralButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {

                    if (getCurrentCoralLevel() == 0) //none
                    {
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_teleopCoralButtonL1.setChecked(false);
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_teleopCoralButtonL2.setChecked(false);
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#999999"));
                        m_teleopCoralButtonL3.setChecked(false);
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#999999"));
                        m_teleopCoralButtonL4.setChecked(false);
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#49B6A9"));
                        m_teleopCoralButtonNone.setChecked(true);
                        m_teleopCoralIncr.setEnabled(false);
                        m_teleopCoralDecr.setEnabled(false);
                        m_teleopCoralDecrDisabled.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.VISIBLE);
                    }
                    else if (getCurrentCoralLevel() == 1) //parked
                    {
                        m_teleopCoralButtonL1.setChecked(true);
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#A9FFCB")); //ensures text remains gray
                        m_teleopCoralButtonL2.setChecked(false);
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_teleopCoralButtonL3.setChecked(false);
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#999999"));
                        m_teleopCoralButtonL4.setChecked(false);
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#999999"));
                        m_teleopCoralButtonNone.setChecked(false);
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#999999"));
                        m_teleopCoralTextL1.setText(String.valueOf(m_matchData.getTeleopCoralL1()));
                        m_teleopCoralIncr.setEnabled(true);
                        m_teleopCoralDecr.setEnabled(true);
                        m_teleopCoralText.setText(m_teleopCoralTextL1.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.VISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);
                    }
                    else if (getCurrentCoralLevel() == 2)
                    {
                        m_teleopCoralButtonL1.setChecked(false);
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL2.setChecked(true);
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#49B6A9"));
                        m_teleopCoralButtonL3.setChecked(false);
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL4.setChecked(false);
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonNone.setChecked(false);
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralTextL2.setText(String.valueOf(m_matchData.getTeleopCoralL2()));
                        m_teleopCoralIncr.setEnabled(true);
                        m_teleopCoralDecr.setEnabled(true);
                        m_teleopCoralText.setText(m_teleopCoralTextL2.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.VISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);
                    }
                    else if (getCurrentCoralLevel() == 3) //onstage
                    {
                        m_teleopCoralButtonL1.setChecked(false);
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL2.setChecked(false);
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL3.setChecked(true);
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#5B37C8"));
                        m_teleopCoralButtonL4.setEnabled(false);
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonNone.setEnabled(false);
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralTextL3.setText(String.valueOf(m_matchData.getTeleopCoralL3()));
                        m_teleopCoralIncr.setEnabled(true);
                        m_teleopCoralDecr.setEnabled(true);
                        m_teleopCoralText.setText(m_teleopCoralTextL3.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.VISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);
                    }
                    else if (getCurrentCoralLevel() == 4)
                    {
                        m_teleopCoralButtonL1.setChecked(false);
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_teleopCoralButtonL2.setChecked(false);
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_teleopCoralButtonL3.setChecked(false);
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_teleopCoralButtonL4.setChecked(true);
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#9C1DE2"));
                        m_teleopCoralButtonNone.setChecked(false);
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralTextL4.setText(String.valueOf(m_matchData.getTeleopCoralL4()));
                        m_teleopCoralIncr.setEnabled(true);
                        m_teleopCoralDecr.setEnabled(true);
                        m_teleopCoralText.setText(m_teleopCoralTextL4.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.VISIBLE);
                    }
                }
            });

            m_teleopCoralButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {

                    if (m_teleopCoralButtonNone.isChecked() == false)
                    {
                        m_teleopCoralIncr.setEnabled(true);
                        m_teleopCoralDecr.setEnabled(true);
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        m_teleopCoralIncr.setEnabled(false);
                        m_teleopCoralDecr.setEnabled(false);
                        m_teleopCoralDecrDisabled.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.VISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                    }
                    if (m_teleopCoralButtonNone.isChecked())
                    {
                        m_teleopCoralText.setText("0");
                        m_teleopCoralDecrDisabled.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.VISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#49B6A9"));
                        m_teleopCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL4.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_teleopCoralButtonL1.isChecked())
                    {
                        m_teleopCoralText.setText(m_teleopCoralTextL1.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.VISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralL1.setTextColor(Color.parseColor("#A9FFCB"));
                        m_teleopCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#A9FFCB"));
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_teleopCoralButtonL2.isChecked())
                    {
                        m_teleopCoralText.setText(m_teleopCoralTextL2.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.VISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL2.setTextColor(Color.parseColor("#49B6A9"));
                        m_teleopCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#49B6A9"));
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_teleopCoralButtonL3.isChecked())
                    {
                        m_teleopCoralText.setText(m_teleopCoralTextL3.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.VISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL3.setTextColor(Color.parseColor("#5B37C8"));
                        m_teleopCoralL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#5B37C8"));
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_teleopCoralButtonL4.isChecked())
                    {
                        m_teleopCoralText.setText(m_teleopCoralTextL4.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.VISIBLE);
                        m_teleopCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL4.setTextColor(Color.parseColor("#9C1DE2"));
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#9C1DE2"));
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (Integer.parseInt(m_teleopCoralText.getText().toString()) <= MAX_POINTS_CORAL)
                    {
                        m_teleopCoralText.setTextColor(Color.parseColor("#363636"));
                    }
                    if (Integer.parseInt(m_teleopCoralText.getText().toString()) > MAX_POINTS_CORAL)
                    {
                        m_teleopCoralText.setTextColor(Color.RED);
                    }

                    //Changes m_matchData's climb variable according to which radio button is selected
                    //m_matchData.setEndgameHarmony(getCurrentCoralLevel());
                }
            });

            m_teleopCoralButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {

                    if (m_teleopCoralButtonNone.isChecked() == false)
                    {
                        m_teleopCoralIncr.setEnabled(true);
                        m_teleopCoralDecr.setEnabled(true);
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        m_teleopCoralIncr.setEnabled(false);
                        m_teleopCoralDecr.setEnabled(false);
                        m_teleopCoralDecrDisabled.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.VISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                    }
                    if (m_teleopCoralButtonNone.isChecked())
                    {
                        m_teleopCoralText.setText("0");
                        m_teleopCoralDecrDisabled.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.VISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL4.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_teleopCoralButtonL1.isChecked())
                    {
                        m_teleopCoralText.setText(m_teleopCoralTextL1.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.VISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralL1.setTextColor(Color.parseColor("#48a14d"));
                        m_teleopCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#48a14d"));
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_teleopCoralButtonL2.isChecked())
                    {
                        m_teleopCoralText.setText(m_teleopCoralTextL2.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.VISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL2.setTextColor(Color.parseColor("#3DB9FC"));
                        m_teleopCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#3DB9FC"));
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_teleopCoralButtonL3.isChecked())
                    {
                        m_teleopCoralText.setText(m_teleopCoralTextL3.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.VISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_teleopCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL3.setTextColor(Color.parseColor("#433DFC"));
                        m_teleopCoralL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#433DFC"));
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_teleopCoralButtonL4.isChecked())
                    {
                        m_teleopCoralText.setText(m_teleopCoralTextL4.getText().toString());
                        m_teleopCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecr.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncr.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_teleopCoralDecrL4.setVisibility(View.VISIBLE);
                        m_teleopCoralIncrL4.setVisibility(View.VISIBLE);
                        m_teleopCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralL4.setTextColor(Color.parseColor("#B331EB"));
                        m_teleopCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_teleopCoralButtonL4.setTextColor(Color.parseColor("#B331EB"));
                        m_teleopCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (Integer.parseInt(m_teleopCoralText.getText().toString()) <= MAX_POINTS_CORAL)
                    {
                        m_teleopCoralText.setTextColor(Color.parseColor("#363636"));
                    }
                    if (Integer.parseInt(m_teleopCoralText.getText().toString()) > MAX_POINTS_CORAL)
                    {
                        m_teleopCoralText.setTextColor(Color.RED);
                    }

                    //Changes m_matchData's climb variable according to which radio button is selected
                    //m_matchData.setEndgameHarmony(getCurrentCoralLevel());
                }
            });

            //Connects the increment button for cubes top row points and sets up a listener that detects when the button is clicked
            Button teleopCoralIncrButton = v.findViewById(R.id.teleop_coral_scoring_incr);
            teleopCoralIncrButton.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Increases displayed point value by 1
                    if (getCurrentCoralLevel() == 1)
                    {
                        updatePointsInt(m_teleopCoralTextL1, true, true);
                    }
                    if (getCurrentCoralLevel() == 2)
                    {
                        updatePointsInt(m_teleopCoralTextL2, true, true);
                    }
                    if (getCurrentCoralLevel() == 3)
                    {
                        updatePointsInt(m_teleopCoralTextL3, true, true);
                    }
                    if (getCurrentCoralLevel() == 4)
                    {
                        updatePointsInt(m_teleopCoralTextL4, true, true);
                    }
                    updatePointsInt(m_teleopCoralText, true, true);
                }
            });

            Button teleopCoralIncrButtonL1 = v.findViewById(R.id.teleop_coral_scoring_incr_L1);
            teleopCoralIncrButtonL1.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Increases displayed point value by 1
                    if (getCurrentCoralLevel() == 1)
                    {
                        updatePointsInt(m_teleopCoralTextL1, true, true);
                    }
                    if (getCurrentCoralLevel() == 2)
                    {
                        updatePointsInt(m_teleopCoralTextL2, true, true);
                    }
                    if (getCurrentCoralLevel() == 3)
                    {
                        updatePointsInt(m_teleopCoralTextL3, true, true);
                    }
                    if (getCurrentCoralLevel() == 4)
                    {
                        updatePointsInt(m_teleopCoralTextL4, true, true);
                    }
                    updatePointsInt(m_teleopCoralText, true, true);
                }
            });

            Button teleopCoralIncrButtonL2 = v.findViewById(R.id.teleop_coral_scoring_incr_L2);
            teleopCoralIncrButtonL2.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Increases displayed point value by 1
                    if (getCurrentCoralLevel() == 1)
                    {
                        updatePointsInt(m_teleopCoralTextL1, true, true);
                    }
                    if (getCurrentCoralLevel() == 2)
                    {
                        updatePointsInt(m_teleopCoralTextL2, true, true);
                    }
                    if (getCurrentCoralLevel() == 3)
                    {
                        updatePointsInt(m_teleopCoralTextL3, true, true);
                    }
                    if (getCurrentCoralLevel() == 4)
                    {
                        updatePointsInt(m_teleopCoralTextL4, true, true);
                    }
                    updatePointsInt(m_teleopCoralText, true, true);
                }
            });

            Button teleopCoralIncrButtonL3 = v.findViewById(R.id.teleop_coral_scoring_incr_L3);
            teleopCoralIncrButtonL3.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Increases displayed point value by 1
                    if (getCurrentCoralLevel() == 1)
                    {
                        updatePointsInt(m_teleopCoralTextL1, true, true);
                    }
                    if (getCurrentCoralLevel() == 2)
                    {
                        updatePointsInt(m_teleopCoralTextL2, true, true);
                    }
                    if (getCurrentCoralLevel() == 3)
                    {
                        updatePointsInt(m_teleopCoralTextL3, true, true);
                    }
                    if (getCurrentCoralLevel() == 4)
                    {
                        updatePointsInt(m_teleopCoralTextL4, true, true);
                    }
                    updatePointsInt(m_teleopCoralText, true, true);
                }
            });

            Button teleopCoralIncrButtonL4 = v.findViewById(R.id.teleop_coral_scoring_incr_L4);
            teleopCoralIncrButtonL4.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Increases displayed point value by 1
                    if (getCurrentCoralLevel() == 1)
                    {
                        updatePointsInt(m_teleopCoralTextL1, true, true);
                    }
                    if (getCurrentCoralLevel() == 2)
                    {
                        updatePointsInt(m_teleopCoralTextL2, true, true);
                    }
                    if (getCurrentCoralLevel() == 3)
                    {
                        updatePointsInt(m_teleopCoralTextL3, true, true);
                    }
                    if (getCurrentCoralLevel() == 4)
                    {
                        updatePointsInt(m_teleopCoralTextL4, true, true);
                    }
                    updatePointsInt(m_teleopCoralText, true, true);
                }
            });

            Button teleopCoralDecrButton = v.findViewById(R.id.teleop_coral_scoring_decr);
            teleopCoralDecrButton.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Decreases displayed point value by 1; sets to 0 if result would be negative
                    if (getCurrentCoralLevel() == 1)
                    {
                        updatePointsInt(m_teleopCoralTextL1, false, true);
                    }
                    if (getCurrentCoralLevel() == 2)
                    {
                        updatePointsInt(m_teleopCoralTextL2, false, true);
                    }
                    if (getCurrentCoralLevel() == 3)
                    {
                        updatePointsInt(m_teleopCoralTextL3, false, true);
                    }
                    if (getCurrentCoralLevel() == 4)
                    {
                        updatePointsInt(m_teleopCoralTextL4, false, true);
                    }
                    updatePointsInt(m_teleopCoralText, false, true);
                }
            });

            Button teleopCoralDecrButtonL1 = v.findViewById(R.id.teleop_coral_scoring_decr_L1);
            teleopCoralDecrButtonL1.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Decreases displayed point value by 1; sets to 0 if result would be negative
                    if (getCurrentCoralLevel() == 1)
                    {
                        updatePointsInt(m_teleopCoralTextL1, false, true);
                    }
                    if (getCurrentCoralLevel() == 2)
                    {
                        updatePointsInt(m_teleopCoralTextL2, false, true);
                    }
                    if (getCurrentCoralLevel() == 3)
                    {
                        updatePointsInt(m_teleopCoralTextL3, false, true);
                    }
                    if (getCurrentCoralLevel() == 4)
                    {
                        updatePointsInt(m_teleopCoralTextL4, false, true);
                    }
                    updatePointsInt(m_teleopCoralText, false, true);
                }
            });

            Button teleopCoralDecrButtonL2 = v.findViewById(R.id.teleop_coral_scoring_decr_L2);
            teleopCoralDecrButtonL2.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Decreases displayed point value by 1; sets to 0 if result would be negative
                    if (getCurrentCoralLevel() == 1)
                    {
                        updatePointsInt(m_teleopCoralTextL1, false, true);
                    }
                    if (getCurrentCoralLevel() == 2)
                    {
                        updatePointsInt(m_teleopCoralTextL2, false, true);
                    }
                    if (getCurrentCoralLevel() == 3)
                    {
                        updatePointsInt(m_teleopCoralTextL3, false, true);
                    }
                    if (getCurrentCoralLevel() == 4)
                    {
                        updatePointsInt(m_teleopCoralTextL4, false, true);
                    }
                    updatePointsInt(m_teleopCoralText, false, true);
                }
            });

            Button teleopCoralDecrButtonL3 = v.findViewById(R.id.teleop_coral_scoring_decr_L3);
            teleopCoralDecrButtonL3.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Decreases displayed point value by 1; sets to 0 if result would be negative
                    if (getCurrentCoralLevel() == 1)
                    {
                        updatePointsInt(m_teleopCoralTextL1, false, true);
                    }
                    if (getCurrentCoralLevel() == 2)
                    {
                        updatePointsInt(m_teleopCoralTextL2, false, true);
                    }
                    if (getCurrentCoralLevel() == 3)
                    {
                        updatePointsInt(m_teleopCoralTextL3, false, true);
                    }
                    if (getCurrentCoralLevel() == 4)
                    {
                        updatePointsInt(m_teleopCoralTextL4, false, true);
                    }
                    updatePointsInt(m_teleopCoralText, false, true);
                }
            });

            Button teleopCoralDecrButtonL4 = v.findViewById(R.id.teleop_coral_scoring_decr_L4);
            teleopCoralDecrButtonL4.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Decreases displayed point value by 1; sets to 0 if result would be negative
                    if (getCurrentCoralLevel() == 1)
                    {
                        updatePointsInt(m_teleopCoralTextL1, false, true);
                    }
                    if (getCurrentCoralLevel() == 2)
                    {
                        updatePointsInt(m_teleopCoralTextL2, false, true);
                    }
                    if (getCurrentCoralLevel() == 3)
                    {
                        updatePointsInt(m_teleopCoralTextL3, false, true);
                    }
                    if (getCurrentCoralLevel() == 4)
                    {
                        updatePointsInt(m_teleopCoralTextL4, false, true);
                    }
                    updatePointsInt(m_teleopCoralText, false, true);
                }
            });

            m_teleopAlgaeButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {

                    //Changes m_matchData's climb variable according to which radio button is selected
                    if (getCurrentAlgaeLevel() == 0) //none
                    {
                        m_teleopAlgaeButtonNet.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_teleopAlgaeButtonNet.setChecked(false);
                        m_teleopAlgaeButtonProcessor.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_teleopAlgaeButtonProcessor.setChecked(false);
                        m_teleopAlgaeButtonNone.setTextColor(Color.parseColor("#49B6A9"));
                        m_teleopAlgaeButtonNone.setChecked(true);
                        m_teleopAlgaeIncr.setEnabled(false);
                        m_teleopAlgaeDecr.setEnabled(false);
                        m_teleopAlgaeDecrDisabled.setVisibility(View.VISIBLE);
                        m_teleopAlgaeIncrDisabled.setVisibility(View.VISIBLE);
                    }
                    else if (getCurrentAlgaeLevel() == 1)
                    {
                        m_teleopAlgaeButtonNet.setChecked(true);
                        m_teleopAlgaeButtonNet.setTextColor(Color.parseColor("#A9FFCB")); //ensures text remains gray
                        m_teleopAlgaeButtonProcessor.setChecked(false);
                        m_teleopAlgaeButtonProcessor.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_teleopAlgaeButtonNone.setChecked(false);
                        m_teleopAlgaeButtonNone.setTextColor(Color.parseColor("#999999"));
                        m_teleopAlgaeTextNet.setText(String.valueOf(m_matchData.getTeleopAlgaeNet()));
                        m_teleopAlgaeIncr.setEnabled(true);
                        m_teleopAlgaeDecr.setEnabled(true);
                        m_teleopAlgaeText.setText(m_teleopAlgaeTextNet.getText().toString());
                        m_teleopAlgaeDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeDecr.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncr.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeDecrNet.setVisibility(View.VISIBLE);
                        m_teleopAlgaeIncrNet.setVisibility(View.VISIBLE);
                        m_teleopAlgaeDecrProcessor.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrProcessor.setVisibility(View.INVISIBLE);
                    }
                    else if (getCurrentAlgaeLevel() == 2) //onstage
                    {
                        m_teleopAlgaeButtonNet.setChecked(false);
                        m_teleopAlgaeButtonNet.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_teleopAlgaeButtonProcessor.setChecked(true);
                        m_teleopAlgaeButtonProcessor.setTextColor(Color.parseColor("#49B6A9")); //changes text color when stage level is 2
                        m_teleopAlgaeButtonNone.setChecked(false);
                        m_teleopAlgaeButtonNone.setTextColor(Color.parseColor("#363636"));
                        m_teleopAlgaeTextProcessor.setText(String.valueOf(m_matchData.getTeleopAlgaeProcessor()));
                        m_teleopAlgaeIncr.setEnabled(true);
                        m_teleopAlgaeDecr.setEnabled(true);
                        m_teleopAlgaeText.setText(m_teleopAlgaeTextProcessor.getText().toString());
                        m_teleopAlgaeDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeDecr.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncr.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeDecrNet.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrNet.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrProcessor.setVisibility(View.VISIBLE);
                        m_teleopAlgaeDecrProcessor.setVisibility(View.VISIBLE);
                    }
                }
            });

            m_teleopAlgaeButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {

                    if (m_teleopAlgaeButtonNone.isChecked() == false)
                    {
                        m_teleopAlgaeIncr.setEnabled(true);
                        m_teleopAlgaeDecr.setEnabled(true);
                        m_teleopAlgaeDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrDisabled.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        m_teleopAlgaeIncr.setEnabled(false);
                        m_teleopAlgaeDecr.setEnabled(false);
                        m_teleopAlgaeDecrDisabled.setVisibility(View.VISIBLE);
                        m_teleopAlgaeIncrDisabled.setVisibility(View.VISIBLE);
                        m_teleopAlgaeDecr.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncr.setVisibility(View.INVISIBLE);
                    }
                    if (m_teleopAlgaeButtonNone.isChecked())
                    {
                        m_teleopAlgaeText.setText("0");
                        m_teleopAlgaeDecrDisabled.setVisibility(View.VISIBLE);
                        m_teleopAlgaeIncrDisabled.setVisibility(View.VISIBLE);
                        m_teleopAlgaeDecr.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncr.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeDecrNet.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrNet.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeDecrProcessor.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrProcessor.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeButtonNet.setTextColor(Color.parseColor("#363636"));
                        m_teleopAlgaeButtonProcessor.setTextColor(Color.parseColor("#363636"));
                        m_teleopAlgaeButtonNone.setTextColor(Color.parseColor("#363636"));
                        m_teleopAlgaeNet.setTextColor(Color.parseColor("#363636"));
                        m_teleopAlgaeProcessor.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_teleopAlgaeButtonNet.isChecked())
                    {
                        m_teleopAlgaeText.setText(m_teleopAlgaeTextNet.getText().toString());
                        m_teleopAlgaeDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeDecr.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncr.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrNet.setVisibility(View.VISIBLE);
                        m_teleopAlgaeDecrNet.setVisibility(View.VISIBLE);
                        m_teleopAlgaeDecrProcessor.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrProcessor.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeNet.setTextColor(Color.parseColor("#2F63FF"));
                        m_teleopAlgaeProcessor.setTextColor(Color.parseColor("#363636"));
                        m_teleopAlgaeButtonNet.setTextColor(Color.parseColor("#2F63FF"));
                        m_teleopAlgaeButtonProcessor.setTextColor(Color.parseColor("#363636"));
                        m_teleopAlgaeButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_teleopAlgaeButtonProcessor.isChecked())
                    {
                        m_teleopAlgaeText.setText(m_teleopAlgaeTextProcessor.getText().toString());
                        m_teleopAlgaeDecrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrDisabled.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeDecr.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncr.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeIncrNet.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeDecrNet.setVisibility(View.INVISIBLE);
                        m_teleopAlgaeDecrProcessor.setVisibility(View.VISIBLE);
                        m_teleopAlgaeIncrProcessor.setVisibility(View.VISIBLE);
                        m_teleopAlgaeNet.setTextColor(Color.parseColor("#363636"));
                        m_teleopAlgaeProcessor.setTextColor(Color.parseColor("#39169A"));
                        m_teleopAlgaeButtonNet.setTextColor(Color.parseColor("#363636"));
                        m_teleopAlgaeButtonProcessor.setTextColor(Color.parseColor("#39169A"));
                        m_teleopAlgaeButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (Integer.parseInt(m_teleopAlgaeText.getText().toString()) <= MAX_POINTS_ALGAE)
                    {
                        m_teleopAlgaeText.setTextColor(Color.parseColor("#363636"));
                    }
                    if (Integer.parseInt(m_teleopAlgaeText.getText().toString()) > MAX_POINTS_ALGAE)
                    {
                        m_teleopAlgaeText.setTextColor(Color.RED);
                    }
                }
            });

            Button teleopAlgaeIncrButton = v.findViewById(R.id.teleop_algae_scoring_incr);
            teleopAlgaeIncrButton.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Increases displayed point value by 1
                    if (getCurrentAlgaeLevel() == 1)
                    {
                        updatePointsInt(m_teleopAlgaeTextNet, true, false);
                    }
                    if (getCurrentAlgaeLevel() == 2)
                    {
                        updatePointsInt(m_teleopAlgaeTextProcessor, true, false);
                    }
                    updatePointsInt(m_teleopAlgaeText, true, false);
                }
            });

            Button teleopAlgaeIncrButtonNet = v.findViewById(R.id.teleop_algae_scoring_incr_net);
            teleopAlgaeIncrButtonNet.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Increases displayed point value by 1
                    if (getCurrentAlgaeLevel() == 1)
                    {
                        updatePointsInt(m_teleopAlgaeTextNet, true, false);
                    }
                    if (getCurrentAlgaeLevel() == 2)
                    {
                        updatePointsInt(m_teleopAlgaeTextProcessor, true, false);
                    }
                    updatePointsInt(m_teleopAlgaeText, true, false);
                }
            });

            Button teleopAlgaeIncrButtonProcessor = v.findViewById(R.id.teleop_algae_scoring_incr_processor);
            teleopAlgaeIncrButtonProcessor.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    updatePointsInt(m_teleopAlgaeTextProcessor, true, false);
                    updatePointsInt(m_teleopAlgaeText, true, false);
                }
            });

            //Connects the decrement button for cones top row points and sets up a listener that detects when the button is clicked
            Button teleopAlgaeDecrButton = v.findViewById(R.id.teleop_algae_scoring_decr);
            teleopAlgaeDecrButton.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Decreases displayed point value by 1; sets to 0 if result would be negative
                    if (getCurrentAlgaeLevel() == 1)
                    {
                        updatePointsInt(m_teleopAlgaeTextNet, false, false);
                    }
                    if (getCurrentAlgaeLevel() == 2)
                    {
                        updatePointsInt(m_teleopAlgaeTextProcessor, false, false);
                    }
                    updatePointsInt(m_teleopAlgaeText, false, false);
                }
            });

            Button teleopAlgaeDecrButtonNet = v.findViewById(R.id.teleop_algae_scoring_decr_net);
            teleopAlgaeDecrButtonNet.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v)
                {
                    //Decreases displayed point value by 1; sets to 0 if result would be negative
                    if (getCurrentAlgaeLevel() == 1)
                    {
                        updatePointsInt(m_teleopAlgaeTextNet, false, false);
                    }
                    if (getCurrentAlgaeLevel() == 2)
                    {
                        updatePointsInt(m_teleopAlgaeTextProcessor, false, false);
                    }
                    updatePointsInt(m_teleopAlgaeText, false, false);
                }
            });

            //Connects the checkbox for leaving starting zone and sets up a listener to detect when the checked status is changed
            m_teleopCoralTextL1.setText(String.valueOf(m_matchData.getTeleopCoralL1()));
            m_teleopCoralTextL2.setText(String.valueOf(m_matchData.getTeleopCoralL2()));
            m_teleopCoralTextL3.setText(String.valueOf(m_matchData.getTeleopCoralL3()));
            m_teleopCoralTextL4.setText(String.valueOf(m_matchData.getTeleopCoralL4()));
            m_teleopAlgaeTextNet.setText(String.valueOf(m_matchData.getTeleopAlgaeNet()));
            m_teleopAlgaeTextProcessor.setText(String.valueOf(m_matchData.getTeleopAlgaeProcessor()));

            if (isGreaterThanMax(m_teleopCoralText, true))
            {
                m_teleopCoralText.setTextColor(Color.RED);
            }
            if (isGreaterThanMax(m_teleopAlgaeText, false))
            {
                m_teleopAlgaeText.setTextColor(Color.RED);
            }

            int defValue = m_matchData.getPlayedDefense();
            if (defValue == 0)
                m_defenseNone.setChecked(true);
            else if(defValue == 1)
              m_defenseLow.setChecked(true);
            else if(defValue == 2)
                m_defenseMedium.setChecked(true);
            else if(defValue == 3)
                m_defenseHigh.setChecked(true);

            //foul pin radio buttons
            m_foulPin = v.findViewById(R.id.foul_pin);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_zeroPin = v.findViewById(R.id.zero_pin);//Sets up radio button that corresponds to 0
            m_onePin  = v.findViewById(R.id.one_pin );//Sets up radio button that corresponds to 1
            m_twoPin  = v.findViewById(R.id.two_pin);//Sets up radio button that corresponds to 2
            m_zeroPin.setChecked(false);
            m_onePin.setChecked(false);
            m_twoPin.setChecked(false);

            int defValuePin = m_matchData.getFoulPin();
            if (defValuePin == 0)
                m_zeroPin.setChecked(true);
            else if(defValuePin == 1)
                m_onePin.setChecked(true);
            else if(defValuePin == 2)
                m_twoPin.setChecked(true);

            //foul touching anchor radio buttons
            m_foulAnchor = v.findViewById(R.id.foul_anchor);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_zeroAnchor = v.findViewById(R.id.zero_anchor);//Sets up radio button that corresponds to 0
            m_oneAnchor  = v.findViewById(R.id.one_anchor);//Sets up radio button that corresponds to 1
            m_twoAnchor  = v.findViewById(R.id.two_anchor);//Sets up radio button that corresponds to 2
            m_zeroAnchor.setChecked(false);
            m_oneAnchor.setChecked(false);
            m_twoAnchor.setChecked(false);

            int defValueAnc = m_matchData.getFoulAnchor();
            if (defValueAnc == 0)
                m_zeroAnchor.setChecked(true);
            else if(defValueAnc == 1)
                m_oneAnchor.setChecked(true);
            else if(defValueAnc == 2)
                m_twoAnchor.setChecked(true);

            //foul touching opponents cage radio buttons
            m_foulCage = v.findViewById(R.id.foul_cage);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_zeroCage = v.findViewById(R.id.zero_cage);//Sets up radio button that corresponds to 0
            m_oneCage  = v.findViewById(R.id.one_cage);//Sets up radio button that corresponds to 1
            m_twoCage  = v.findViewById(R.id.two_cage);//Sets up radio button that corresponds to 2
            m_zeroCage.setChecked(false);
            m_oneCage.setChecked(false);
            m_twoCage.setChecked(false);

            int defValueCage = m_matchData.getFoulCage();
            if (defValueCage == 0)
                m_zeroCage.setChecked(true);
            else if(defValueCage == 1)
                m_oneCage.setChecked(true);
            else if(defValueCage == 2)
                m_twoCage.setChecked(true);


            //foul touching oppent while in barge radio buttons
            m_teleopFoulBarge = v.findViewById(R.id.teleop_foul_barge);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_teleopZeroBarge = v.findViewById(R.id.teleop_zero_barge);//Sets up radio button that corresponds to 0
            m_teleopOneBarge  = v.findViewById(R.id.teleop_one_barge);//Sets up radio button that corresponds to 1
            m_teleopTwoBarge  = v.findViewById(R.id.teleop_two_barge);//Sets up radio button that corresponds to 2
            m_teleopZeroBarge.setChecked(false);
            m_teleopOneBarge.setChecked(false);
            m_teleopTwoBarge.setChecked(false);

            int defValueBarg = m_matchData.getFoulBarge();
            if (defValueBarg == 0)
                m_teleopZeroBarge.setChecked(true);
            else if(defValueBarg == 1)
                m_teleopOneBarge.setChecked(true);
            else if(defValueBarg == 2)
                m_teleopTwoBarge.setChecked(true);


            //foul touching oppent while in reef safe zone radio buttons
            m_teleopFoulReef = v.findViewById(R.id.teleop_foul_reef);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_teleopZeroReef = v.findViewById(R.id.teleop_zero_reef);//Sets up radio button that corresponds to 0
            m_teleopOneReef  = v.findViewById(R.id.teleop_one_reef);//Sets up radio button that corresponds to 1
            m_teleopTwoReef  = v.findViewById(R.id.teleop_two_reef);//Sets up radio button that corresponds to 2
            m_teleopZeroReef.setChecked(false);
            m_teleopOneReef.setChecked(false);
            m_teleopTwoReef.setChecked(false);

            int defValueReef = m_matchData.getFoulReef();
            if (defValueReef == 0)
                m_teleopZeroReef.setChecked(true);
            else if(defValueReef == 1)
                m_teleopOneReef.setChecked(true);
            else if(defValueReef == 2)
                m_teleopTwoReef.setChecked(true);


            // TODO set defense value from matchData

            m_algaeAcquire = v.findViewById(R.id.algae_acquire_text);
            m_algaeAcquire.setText("0");
            m_algaeAcquire.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));
            m_coralAcquire = v.findViewById(R.id.coral_acquire_text);
            m_coralAcquire.setText("0");
            m_coralAcquire.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));


            //Connects the decrement button for amp scoring and sets up a listener that detects when the button is clicked
            Button coralAcquireDecrButton = v.findViewById(R.id.coral_acquire_decr);
            coralAcquireDecrButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // Decreases displayed point value by 1; sets to 0 if result would be negative.
                    updatePointsInt(m_coralAcquire, false, true);
                }
            });


            //Connects the increment button for amp scoring and sets up a listener that detects when the button is clicked
            Button coralAcquireIncrButton = v.findViewById(R.id.coral_acquire_incr);
            coralAcquireIncrButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //Increases displayed point value by 1
                    updatePointsInt(m_coralAcquire, true, true);
                }
            });



        //Connects the increment button for amp misses and sets up a listener that detects when the button is clicked
        Button algaeAcquireIncrButton = v.findViewById(R.id.algae_acquire_incr);
        algaeAcquireIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_algaeAcquire, true, false) ;
            }
        });

        //Connects the decr button for amp misses and sets up a listener that detects when the button is clicked
        Button algaeAcquireDecrButton = v.findViewById(R.id.algae_acquire_decr);
        algaeAcquireDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_algaeAcquire, false, false);
            }
        });

            m_pickUpCoral = v.findViewById(R.id.pickup_coral);
            m_pickUpCoral.setChecked(m_matchData.getPickUpCoral());

            m_pickUpAlgae = v.findViewById(R.id.pickup_algae);
            m_pickUpAlgae.setChecked(m_matchData.getPickUpAlgae());

            m_knockAlgaeOff = v.findViewById(R.id.knock_algae_off);
            m_knockAlgaeOff.setChecked(m_matchData.getKnockOffAlgae());

            m_algaeFromReef = v.findViewById(R.id.algae_from_reef);
            m_algaeFromReef.setChecked(m_matchData.getAlgaeFromReef());

            m_holdBothElements = v.findViewById(R.id.hold_both_elements);
            m_holdBothElements.setChecked(m_matchData.getHoldBothElements());

            m_coralAcquire.setText(String.valueOf(m_matchData.getCoralAcquire()));
            m_algaeAcquire.setText(String.valueOf(m_matchData.getAlgaeAcquire()));

            if (isGreaterThanMax(m_coralAcquire,true))
            {
                m_coralAcquire.setTextColor(Color.RED);
            }
            if (isGreaterThanMax(m_algaeAcquire,false))
            {
                m_algaeAcquire.setTextColor(Color.RED);
            }

        }
        return v;

    }
    
    public int getCurrentCoralLevel()
    {
        // Returns the integer level that is current checked in the radio buttons
        int rtn = 0;
        if (m_teleopCoralButtonGroup.getCheckedRadioButtonId() == m_teleopCoralButtonL1.getId())
        {
            rtn = 1;
        }
        else if (m_teleopCoralButtonGroup.getCheckedRadioButtonId() == m_teleopCoralButtonL2.getId())
        {
            rtn = 2;
        }
        else if (m_teleopCoralButtonGroup.getCheckedRadioButtonId() == m_teleopCoralButtonL3.getId())
        {
            rtn = 3;
        }
        else if (m_teleopCoralButtonGroup.getCheckedRadioButtonId() == m_teleopCoralButtonL4.getId())
        {
            rtn = 4;
        }
        return rtn;
    }

    public int getCurrentAlgaeLevel()
    {
        // Returns the integer level that is current checked in the radio buttons
        int rtn = 0;
        if (m_teleopAlgaeButtonGroup.getCheckedRadioButtonId() == m_teleopAlgaeButtonNet.getId())
        {
            rtn = 1;
        }
        else if (m_teleopCoralButtonGroup.getCheckedRadioButtonId() == m_teleopAlgaeButtonProcessor.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    //used for defense radio buttons
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

    //used for pin foul radio buttons
    public int getCurrentNumberPin()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_foulPin.getCheckedRadioButtonId() == m_zeroPin.getId())
        {
            rtn = 0;
        }
        if (m_foulPin.getCheckedRadioButtonId() == m_onePin.getId())
        {
            rtn = 1;
        }
        if (m_foulPin.getCheckedRadioButtonId() == m_twoPin.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public int getCurrentNumberAnchor()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_foulAnchor.getCheckedRadioButtonId() == m_zeroAnchor.getId())
        {
            rtn = 0;
        }
        if (m_foulAnchor.getCheckedRadioButtonId() == m_oneAnchor.getId())
        {
            rtn = 1;
        }
        if (m_foulAnchor.getCheckedRadioButtonId() == m_twoAnchor.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public int getCurrentNumberCage()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_foulCage.getCheckedRadioButtonId() == m_zeroCage.getId())
        {
            rtn = 0;
        }
        if (m_foulCage.getCheckedRadioButtonId() == m_oneCage.getId())
        {
            rtn = 1;
        }
        if (m_foulCage.getCheckedRadioButtonId() == m_twoCage.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public int getCurrentTeleopNumberBarge()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_teleopFoulBarge.getCheckedRadioButtonId() == m_teleopZeroBarge.getId())
        {
            rtn = 0;
        }
        if (m_teleopFoulBarge.getCheckedRadioButtonId() == m_teleopOneBarge.getId())
        {
            rtn = 1;
        }
        if (m_teleopFoulBarge.getCheckedRadioButtonId() == m_teleopTwoBarge.getId())
        {
            rtn = 2;
        }
        return rtn;
    }

    public int getCurrentTeleopNumberReef()
    {
        // Returns the integer climb level that is current checked in the radio buttons
        int rtn = 0;
        if (m_teleopFoulReef.getCheckedRadioButtonId() == m_teleopZeroReef.getId())
        {
            rtn = 0;
        }
        if (m_teleopFoulReef.getCheckedRadioButtonId() == m_teleopOneReef.getId())
        {
            rtn = 1;
        }
        if (m_teleopFoulReef.getCheckedRadioButtonId() == m_teleopTwoReef.getId())
        {
            rtn = 2;
        }
        return rtn;
    }




    public void updateTeleopData()
    {
        m_matchData.setTeleopCoralL1(Integer.parseInt(m_teleopCoralTextL1.getText().toString()));

        m_matchData.setTeleopCoralL2(Integer.parseInt(m_teleopCoralTextL2.getText().toString()));

        m_matchData.setTeleopCoralL3(Integer.parseInt(m_teleopCoralTextL3.getText().toString()));

        m_matchData.setTeleopCoralL4(Integer.parseInt(m_teleopCoralTextL4.getText().toString()));

        m_matchData.setTeleopAlgaeNet(Integer.parseInt(m_teleopAlgaeTextNet.getText().toString()));

        m_matchData.setTeleopAlgaeProcessor(Integer.parseInt(m_teleopAlgaeTextProcessor.getText().toString()));
        
        m_matchData.setCoralAcquire(Integer.parseInt(m_coralAcquire.getText().toString()));

        m_matchData.setAlgaeAcquire(Integer.parseInt(m_algaeAcquire.getText().toString()));

        m_matchData.setPickUpCoral(m_pickUpCoral.isChecked());

        m_matchData.setPickUpAlgae(m_pickUpAlgae.isChecked());

        m_matchData.setKnockOffAlgae(m_knockAlgaeOff.isChecked());

        m_matchData.setAlgaeFromReef(m_algaeFromReef.isChecked());

        m_matchData.setHoldBothElements(m_holdBothElements.isChecked());

        m_matchData.setPlayedDefense(getCurrentDefenseLevel());

        m_matchData.setFoulPin(getCurrentNumberPin());

        m_matchData.setFoulAnchor(getCurrentNumberAnchor());

        m_matchData.setFoulCage(getCurrentNumberCage());

        m_matchData.setFoulBarge(getCurrentTeleopNumberBarge());

        m_matchData.setFoulReef(getCurrentTeleopNumberReef());


    }

/*REMOVE->
    public String formattedDate(Date d)
    {
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy");
        Date date = null;
        try
        {
            date = dt.parse(d.toString());
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("hh:mm:ss");

        if (date == null)
        {
            return null;
        }
        else
        {
            return (dt1.format(date));
        }
    }
<-REMOVE*/
}
