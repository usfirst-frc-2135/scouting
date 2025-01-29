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
    private static final int MAX_POINTS = 5;     // make sure to update max points - max for valid high or low points total

    private TextView m_autonCoralText;
    private TextView m_autonCoralTextL1;
    private TextView m_autonCoralTextL2;
    private TextView m_autonCoralTextL3;
    private TextView m_autonCoralTextL4;

    private RadioGroup m_autonCoralButtonGroup;
    private RadioButton m_autonCoralButtonL1;
    private RadioButton m_autonCoralButtonL2;
    private RadioButton m_autonCoralButtonL3;
    private RadioButton m_autonCoralButtonL4;
    private RadioButton m_autonCoralButtonNone;

    private Button m_autonCoralIncr;
    private Button m_autonCoralDecr;
    private Button m_autonCoralDecrDisabled;
    private Button m_autonCoralIncrDisabled;
    private Button m_autonCoralIncrL1;
    private Button m_autonCoralDecrL1;
    private Button m_autonCoralIncrL2;
    private Button m_autonCoralDecrL2;
    private Button m_autonCoralIncrL3;
    private Button m_autonCoralDecrL3;
    private Button m_autonCoralIncrL4;
    private Button m_autonCoralDecrL4;

    private RadioGroup m_startingPosition;
    private RadioButton m_rightStart;
    private RadioButton m_middleStart;
    private RadioButton m_leftStart;

    private TextView m_autonCoralL1;
    private TextView m_autonCoralL2;
    private TextView m_autonCoralL3;
    private TextView m_autonCoralL4;

    private TextView m_autonAlgaeText;
    private TextView m_autonAlgaeTextNet;
    private TextView m_autonAlgaeTextProcessor;

    private RadioGroup m_autonAlgaeButtonGroup;
    private RadioButton m_autonAlgaeButtonNet;
    private RadioButton m_autonAlgaeButtonProcessor;
    private RadioButton m_autonAlgaeButtonNone;

    private Button m_autonAlgaeIncr;
    private Button m_autonAlgaeDecr;
    private Button m_autonAlgaeIncrDisabled;
    private Button m_autonAlgaeDecrDisabled;
    private Button m_autonAlgaeIncrNet;
    private Button m_autonAlgaeDecrNet;
    private Button m_autonAlgaeIncrProcessor;
    private Button m_autonAlgaeDecrProcessor;

    private TextView m_autonAlgaeNet;
    private TextView m_autonAlgaeProcessor;

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
            int specialTextPrimaryColor = ContextCompat.getColor(context, R.color.specialTextPrimary);
/*
            // Sets up TextView that displays amp note points, setting 0 as the default
            */
            m_autonCoralText = v.findViewById(R.id.auton_coral_scoring_text);
            m_autonCoralText.setText("0");
            m_autonCoralText.setTextColor(specialTextPrimaryColor);

            // Sets up TextView that displays speaker note points, setting 0 as the default
            m_autonAlgaeText = v.findViewById(R.id.auton_algae_scoring_text);
            m_autonAlgaeText.setText("0");
            m_autonAlgaeText.setTextColor(specialTextPrimaryColor);

            m_autonCoralTextL1 = v.findViewById(R.id.auton_coral_scoring_text_L1);
            m_autonCoralTextL1.setText("0");
            m_autonCoralTextL1.setTextColor(specialTextPrimaryColor);

            m_autonCoralTextL2 = v.findViewById(R.id.auton_coral_scoring_text_L2);
            m_autonCoralTextL2.setText("0");
            m_autonCoralTextL2.setTextColor(specialTextPrimaryColor);

            m_autonCoralTextL3 = v.findViewById(R.id.auton_coral_scoring_text_L3);
            m_autonCoralTextL3.setText("0");
            m_autonCoralTextL3.setTextColor(specialTextPrimaryColor);

            m_autonCoralTextL4 = v.findViewById(R.id.auton_coral_scoring_text_L4);
            m_autonCoralTextL4.setText("0");
            m_autonCoralTextL4.setTextColor(specialTextPrimaryColor);

            m_autonAlgaeTextNet = v.findViewById(R.id.auton_algae_net_scoring);
            m_autonAlgaeTextNet.setText("0");
            m_autonAlgaeTextNet.setTextColor(specialTextPrimaryColor);

            m_autonAlgaeTextProcessor = v.findViewById(R.id.auton_algae_processor_scoring);
            m_autonAlgaeTextProcessor.setText("0");
            m_autonAlgaeTextProcessor.setTextColor(specialTextPrimaryColor);

            m_autonCoralIncr = v.findViewById(R.id.auton_coral_scoring_incr);
            m_autonCoralDecr = v.findViewById(R.id.auton_coral_scoring_decr);
            m_autonCoralIncr.setEnabled(false);
            m_autonCoralDecr.setEnabled(false);
            m_autonCoralDecr.setVisibility(View.INVISIBLE);
            m_autonCoralIncr.setVisibility(View.INVISIBLE);

            m_autonCoralDecrDisabled = v.findViewById(R.id.auton_coral_scoring_decr_disabled);
            m_autonCoralDecrDisabled.setVisibility(View.VISIBLE);
            m_autonCoralIncrDisabled = v.findViewById(R.id.auton_coral_scoring_incr_disabled);
            m_autonCoralIncrDisabled.setVisibility(View.VISIBLE);

            m_autonAlgaeIncr = v.findViewById(R.id.auton_algae_scoring_incr);
            m_autonAlgaeDecr = v.findViewById(R.id.auton_algae_scoring_decr);
            m_autonAlgaeIncr.setEnabled(false);
            m_autonAlgaeDecr.setEnabled(false);
            m_autonAlgaeIncr.setVisibility(View.INVISIBLE);
            m_autonAlgaeDecr.setVisibility(View.INVISIBLE);

            m_autonAlgaeIncrDisabled = v.findViewById(R.id.auton_algae_scoring_incr_disabled);
            m_autonAlgaeIncrDisabled.setVisibility(View.VISIBLE);
            m_autonAlgaeDecrDisabled = v.findViewById(R.id.auton_algae_scoring_decr_disabled);
            m_autonAlgaeDecrDisabled.setVisibility(View.VISIBLE);

            m_autonCoralIncrL1 = v.findViewById(R.id.auton_coral_scoring_incr_L1);
            m_autonCoralIncrL1.setVisibility(View.INVISIBLE);
            m_autonCoralIncrL2 = v.findViewById(R.id.auton_coral_scoring_incr_L2);
            m_autonCoralIncrL2.setVisibility(View.INVISIBLE);
            m_autonCoralIncrL3 = v.findViewById(R.id.auton_coral_scoring_incr_L3);
            m_autonCoralIncrL3.setVisibility(View.INVISIBLE);
            m_autonCoralIncrL4 = v.findViewById(R.id.auton_coral_scoring_incr_L4);
            m_autonCoralIncrL4.setVisibility(View.INVISIBLE);

            m_autonAlgaeIncrNet = v.findViewById(R.id.auton_algae_scoring_incr_net);
            m_autonAlgaeIncrNet.setVisibility(View.INVISIBLE);
            m_autonAlgaeIncrProcessor = v.findViewById(R.id.auton_algae_scoring_incr_processor);
            m_autonAlgaeIncrProcessor.setVisibility(View.INVISIBLE);

            m_autonCoralDecrL1 = v.findViewById(R.id.auton_coral_scoring_decr_L1);
            m_autonCoralDecrL1.setVisibility(View.INVISIBLE);
            m_autonCoralDecrL2 = v.findViewById(R.id.auton_coral_scoring_decr_L2);
            m_autonCoralDecrL2.setVisibility(View.INVISIBLE);
            m_autonCoralDecrL3 = v.findViewById(R.id.auton_coral_scoring_decr_L3);
            m_autonCoralDecrL3.setVisibility(View.INVISIBLE);
            m_autonCoralDecrL4 = v.findViewById(R.id.auton_coral_scoring_decr_L4);
            m_autonCoralDecrL4.setVisibility(View.INVISIBLE);

            m_autonAlgaeDecrNet = v.findViewById(R.id.auton_algae_scoring_decr_net);
            m_autonAlgaeDecrNet.setVisibility(View.INVISIBLE);
            m_autonAlgaeDecrProcessor = v.findViewById(R.id.auton_algae_scoring_decr_processor);
            m_autonAlgaeDecrProcessor.setVisibility(View.INVISIBLE);

            m_autonCoralButtonGroup = v.findViewById(R.id.auton_coral_radio_group);
            m_autonCoralButtonNone = v.findViewById(R.id.auton_coral_none);
            m_autonCoralButtonNone.setChecked(true);
            m_autonCoralButtonL1 = v.findViewById(R.id.auton_coral_L1);
            m_autonCoralButtonL1.setChecked(false);
            m_autonCoralButtonL2 = v.findViewById(R.id.auton_coral_L2);
            m_autonCoralButtonL2.setChecked(false);
            m_autonCoralButtonL3 = v.findViewById(R.id.auton_coral_L3);
            m_autonCoralButtonL3.setChecked(false);
            m_autonCoralButtonL4 = v.findViewById(R.id.auton_coral_L4);
            m_autonCoralButtonL4.setChecked(false);

            m_autonAlgaeButtonGroup = v.findViewById(R.id.auton_algae_radio_group);
            m_autonAlgaeButtonNone = v.findViewById(R.id.auton_algae_none_button);
            m_autonAlgaeButtonNone.setChecked(true);
            m_autonAlgaeButtonNet = v.findViewById(R.id.auton_algae_net_button);
            m_autonAlgaeButtonNet.setChecked(false);
            m_autonAlgaeButtonProcessor = v.findViewById(R.id.auton_algae_processor_button);
            m_autonAlgaeButtonProcessor.setChecked(false);

            m_autonCoralL1 = v.findViewById(R.id.auton_coral_scoring_L1);
            m_autonCoralL2 = v.findViewById(R.id.auton_coral_scoring_L2);
            m_autonCoralL3 = v.findViewById(R.id.auton_coral_scoring_L3);
            m_autonCoralL4 = v.findViewById(R.id.auton_coral_scoring_L4);

            m_autonAlgaeNet = v.findViewById(R.id.auton_algae_net);
            m_autonAlgaeProcessor = v.findViewById(R.id.auton_algae_processor);

            // Setup TextViews that displays points, setting 0 as the default.
            // defense buttons
            m_startingPosition = v.findViewById(R.id.starting_position);// Hooks up the radio group to the controller layer. The radio group contains all of the radio buttons
            m_rightStart = v.findViewById(R.id.right_start);//Sets up radio button that corresponds to 0
            m_middleStart = v.findViewById(R.id.middle_start);//Sets up radio button that corresponds to 1
            m_leftStart  = v.findViewById(R.id.left_start);//Sets up radio button that corresponds to 2
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

            m_autonCoralButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {

                    if (getCurrentCoralLevel() == 0) //none
                    {
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_autonCoralButtonL1.setChecked(false);
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_autonCoralButtonL2.setChecked(false);
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#999999"));
                        m_autonCoralButtonL3.setChecked(false);
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#999999"));
                        m_autonCoralButtonL4.setChecked(false);
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#49B6A9"));
                        m_autonCoralButtonNone.setChecked(true);
                        m_autonCoralIncr.setEnabled(false);
                        m_autonCoralDecr.setEnabled(false);
                        m_autonCoralDecrDisabled.setVisibility(View.VISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.VISIBLE);
                    }
                    else if (getCurrentCoralLevel() == 1) //parked
                    {
                        m_autonCoralButtonL1.setChecked(true);
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#A9FFCB")); //ensures text remains gray
                        m_autonCoralButtonL2.setChecked(false);
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_autonCoralButtonL3.setChecked(false);
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#999999"));
                        m_autonCoralButtonL4.setChecked(false);
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#999999"));
                        m_autonCoralButtonNone.setChecked(false);
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#999999"));
                        m_autonCoralTextL1.setText(String.valueOf(m_matchData.getAutonCoralL1()));
                        m_autonCoralIncr.setEnabled(true);
                        m_autonCoralDecr.setEnabled(true);
                        m_autonCoralText.setText(m_autonCoralTextL1.getText().toString());
                        m_autonCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralDecr.setVisibility(View.INVISIBLE);
                        m_autonCoralIncr.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL1.setVisibility(View.VISIBLE);
                        m_autonCoralIncrL1.setVisibility(View.VISIBLE);
                        m_autonCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL4.setVisibility(View.INVISIBLE);
                    }
                    else if (getCurrentCoralLevel() == 2)
                    {
                        m_autonCoralButtonL1.setChecked(false);
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL2.setChecked(true);
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#49B6A9"));
                        m_autonCoralButtonL3.setChecked(false);
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL4.setChecked(false);
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonNone.setChecked(false);
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralTextL2.setText(String.valueOf(m_matchData.getAutonCoralL2()));
                        m_autonCoralIncr.setEnabled(true);
                        m_autonCoralDecr.setEnabled(true);
                        m_autonCoralText.setText(m_autonCoralTextL2.getText().toString());
                        m_autonCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralDecr.setVisibility(View.INVISIBLE);
                        m_autonCoralIncr.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL2.setVisibility(View.VISIBLE);
                        m_autonCoralIncrL2.setVisibility(View.VISIBLE);
                        m_autonCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL4.setVisibility(View.INVISIBLE);
                    }
                    else if (getCurrentCoralLevel() == 3) //onstage
                    {
                        m_autonCoralButtonL1.setChecked(false);
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL2.setChecked(false);
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL3.setChecked(true);
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#5B37C8"));
                        m_autonCoralButtonL4.setEnabled(false);
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonNone.setEnabled(false);
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralTextL3.setText(String.valueOf(m_matchData.getAutonCoralL3()));
                        m_autonCoralIncr.setEnabled(true);
                        m_autonCoralDecr.setEnabled(true);
                        m_autonCoralText.setText(m_autonCoralTextL3.getText().toString());
                        m_autonCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralDecr.setVisibility(View.INVISIBLE);
                        m_autonCoralIncr.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL3.setVisibility(View.VISIBLE);
                        m_autonCoralIncrL3.setVisibility(View.VISIBLE);
                        m_autonCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL4.setVisibility(View.INVISIBLE);
                    }
                    else if (getCurrentCoralLevel() == 4)
                    {
                        m_autonCoralButtonL1.setChecked(false);
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL2.setChecked(false);
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL3.setChecked(false);
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL4.setChecked(true);
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#9C1DE2"));
                        m_autonCoralButtonNone.setChecked(false);
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralTextL4.setText(String.valueOf(m_matchData.getAutonCoralL4()));
                        m_autonCoralIncr.setEnabled(true);
                        m_autonCoralDecr.setEnabled(true);
                        m_autonCoralText.setText(m_autonCoralTextL4.getText().toString());
                        m_autonCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralDecr.setVisibility(View.INVISIBLE);
                        m_autonCoralIncr.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL4.setVisibility(View.VISIBLE);
                        m_autonCoralIncrL4.setVisibility(View.VISIBLE);
                    }
                }
            });

            m_autonCoralButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {

                    if (m_autonCoralButtonNone.isChecked() == false)
                    {
                        m_autonCoralIncr.setEnabled(true);
                        m_autonCoralDecr.setEnabled(true);
                        m_autonCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        m_autonCoralIncr.setEnabled(false);
                        m_autonCoralDecr.setEnabled(false);
                        m_autonCoralDecrDisabled.setVisibility(View.VISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.VISIBLE);
                        m_autonCoralDecr.setVisibility(View.INVISIBLE);
                        m_autonCoralIncr.setVisibility(View.INVISIBLE);
                    }
                    if (m_autonCoralButtonNone.isChecked())
                    {
                        m_autonCoralText.setText("0");
                        m_autonCoralDecrDisabled.setVisibility(View.VISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.VISIBLE);
                        m_autonCoralDecr.setVisibility(View.INVISIBLE);
                        m_autonCoralIncr.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#49B6A9"));
                        m_autonCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL4.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_autonCoralButtonL1.isChecked())
                    {
                        m_autonCoralText.setText(m_autonCoralTextL1.getText().toString());
                        m_autonCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralDecr.setVisibility(View.INVISIBLE);
                        m_autonCoralIncr.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL1.setVisibility(View.VISIBLE);
                        m_autonCoralIncrL1.setVisibility(View.VISIBLE);
                        m_autonCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_autonCoralL1.setTextColor(Color.parseColor("#A9FFCB"));
                        m_autonCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL4.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#A9FFCB"));
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_autonCoralButtonL2.isChecked())
                    {
                        m_autonCoralText.setText(m_autonCoralTextL2.getText().toString());
                        m_autonCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralDecr.setVisibility(View.INVISIBLE);
                        m_autonCoralIncr.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL2.setVisibility(View.VISIBLE);
                        m_autonCoralIncrL2.setVisibility(View.VISIBLE);
                        m_autonCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_autonCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL2.setTextColor(Color.parseColor("#49B6A9"));
                        m_autonCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL4.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#49B6A9"));
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_autonCoralButtonL3.isChecked())
                    {
                        m_autonCoralText.setText(m_autonCoralTextL3.getText().toString());
                        m_autonCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralDecr.setVisibility(View.INVISIBLE);
                        m_autonCoralIncr.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL3.setVisibility(View.VISIBLE);
                        m_autonCoralIncrL3.setVisibility(View.VISIBLE);
                        m_autonCoralDecrL4.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL4.setVisibility(View.INVISIBLE);
                        m_autonCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL3.setTextColor(Color.parseColor("#5B37C8"));
                        m_autonCoralL4.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#5B37C8"));
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (m_autonCoralButtonL4.isChecked())
                    {
                        m_autonCoralText.setText(m_autonCoralTextL4.getText().toString());
                        m_autonCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralDecr.setVisibility(View.INVISIBLE);
                        m_autonCoralIncr.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL1.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL2.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrL3.setVisibility(View.INVISIBLE);
                        m_autonCoralDecrL4.setVisibility(View.VISIBLE);
                        m_autonCoralIncrL4.setVisibility(View.VISIBLE);
                        m_autonCoralL1.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL2.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL3.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralL4.setTextColor(Color.parseColor("#9C1DE2"));
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#9C1DE2"));
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                    }
                    if (Integer.parseInt(m_autonCoralText.getText().toString()) <= MAX_POINTS)
                    {
                        m_autonCoralText.setTextColor(Color.parseColor("#363636"));
                    }
                    if (Integer.parseInt(m_autonCoralText.getText().toString()) > MAX_POINTS)
                    {
                        m_autonCoralText.setTextColor(Color.RED);
                    }

                    //Changes m_matchData's climb variable according to which radio button is selected
                    //m_matchData.setEndgameHarmony(getCurrentCoralLevel());
                }
            });
        }


        //Connects the increment button for cubes top row points and sets up a listener that detects when the button is clicked
        Button autonCoralIncrButton = v.findViewById(R.id.auton_coral_scoring_incr);
        autonCoralIncrButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                if (getCurrentCoralLevel() == 1)
                {
                    updatePointsInt(m_autonCoralTextL1, true);
                }
                if (getCurrentCoralLevel() == 2)
                {
                    updatePointsInt(m_autonCoralTextL2, true);
                }
                if (getCurrentCoralLevel() == 3)
                {
                    updatePointsInt(m_autonCoralTextL3, true);
                }
                if (getCurrentCoralLevel() == 4)
                {
                    updatePointsInt(m_autonCoralTextL4, true);
                }
                updatePointsInt(m_autonCoralText, true);
            }
        });

        Button autonCoralIncrButtonL1 = v.findViewById(R.id.auton_coral_scoring_incr_L1);
        autonCoralIncrButtonL1.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                if (getCurrentCoralLevel() == 1)
                {
                    updatePointsInt(m_autonCoralTextL1, true);
                }
                if (getCurrentCoralLevel() == 2)
                {
                    updatePointsInt(m_autonCoralTextL2, true);
                }
                if (getCurrentCoralLevel() == 3)
                {
                    updatePointsInt(m_autonCoralTextL3, true);
                }
                if (getCurrentCoralLevel() == 4)
                {
                    updatePointsInt(m_autonCoralTextL4, true);
                }
                updatePointsInt(m_autonCoralText, true);
            }
        });

        Button autonCoralIncrButtonL2 = v.findViewById(R.id.auton_coral_scoring_incr_L2);
        autonCoralIncrButtonL2.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                if (getCurrentCoralLevel() == 1)
                {
                    updatePointsInt(m_autonCoralTextL1, true);
                }
                if (getCurrentCoralLevel() == 2)
                {
                    updatePointsInt(m_autonCoralTextL2, true);
                }
                if (getCurrentCoralLevel() == 3)
                {
                    updatePointsInt(m_autonCoralTextL3, true);
                }
                if (getCurrentCoralLevel() == 4)
                {
                    updatePointsInt(m_autonCoralTextL4, true);
                }
                updatePointsInt(m_autonCoralText, true);
            }
        });

        Button autonCoralIncrButtonL3 = v.findViewById(R.id.auton_coral_scoring_incr_L3);
        autonCoralIncrButtonL3.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                if (getCurrentCoralLevel() == 1)
                {
                    updatePointsInt(m_autonCoralTextL1, true);
                }
                if (getCurrentCoralLevel() == 2)
                {
                    updatePointsInt(m_autonCoralTextL2, true);
                }
                if (getCurrentCoralLevel() == 3)
                {
                    updatePointsInt(m_autonCoralTextL3, true);
                }
                if (getCurrentCoralLevel() == 4)
                {
                    updatePointsInt(m_autonCoralTextL4, true);
                }
                updatePointsInt(m_autonCoralText, true);
            }
        });

        Button autonCoralIncrButtonL4 = v.findViewById(R.id.auton_coral_scoring_incr_L4);
        autonCoralIncrButtonL4.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                if (getCurrentCoralLevel() == 1)
                {
                    updatePointsInt(m_autonCoralTextL1, true);
                }
                if (getCurrentCoralLevel() == 2)
                {
                    updatePointsInt(m_autonCoralTextL2, true);
                }
                if (getCurrentCoralLevel() == 3)
                {
                    updatePointsInt(m_autonCoralTextL3, true);
                }
                if (getCurrentCoralLevel() == 4)
                {
                    updatePointsInt(m_autonCoralTextL4, true);
                }
                updatePointsInt(m_autonCoralText, true);
            }
        });

        //Connects the decrement button for cones top row points and sets up a listener that detects when the button is clicked
        Button autonCoralDecrButton = v.findViewById(R.id.auton_coral_scoring_decr);
        autonCoralDecrButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                if (getCurrentCoralLevel() == 1)
                {
                    updatePointsInt(m_autonCoralTextL1, false);
                }
                if (getCurrentCoralLevel() == 2)
                {
                    updatePointsInt(m_autonCoralTextL2, false);
                }
                if (getCurrentCoralLevel() == 3)
                {
                    updatePointsInt(m_autonCoralTextL3, false);
                }
                if (getCurrentCoralLevel() == 4)
                {
                    updatePointsInt(m_autonCoralTextL4, false);
                }
                updatePointsInt(m_autonCoralText, false);
            }
        });

        Button autonCoralDecrButtonL1 = v.findViewById(R.id.auton_coral_scoring_decr_L1);
        autonCoralDecrButtonL1.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                if (getCurrentCoralLevel() == 1)
                {
                    updatePointsInt(m_autonCoralTextL1, false);
                }
                if (getCurrentCoralLevel() == 2)
                {
                    updatePointsInt(m_autonCoralTextL2, false);
                }
                if (getCurrentCoralLevel() == 3)
                {
                    updatePointsInt(m_autonCoralTextL3, false);
                }
                if (getCurrentCoralLevel() == 4)
                {
                    updatePointsInt(m_autonCoralTextL4, false);
                }
                updatePointsInt(m_autonCoralText, false);
            }
        });

        Button autonCoralDecrButtonL2 = v.findViewById(R.id.auton_coral_scoring_decr_L2);
        autonCoralDecrButtonL2.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                if (getCurrentCoralLevel() == 1)
                {
                    updatePointsInt(m_autonCoralTextL1, false);
                }
                if (getCurrentCoralLevel() == 2)
                {
                    updatePointsInt(m_autonCoralTextL2, false);
                }
                if (getCurrentCoralLevel() == 3)
                {
                    updatePointsInt(m_autonCoralTextL3, false);
                }
                if (getCurrentCoralLevel() == 4)
                {
                    updatePointsInt(m_autonCoralTextL4, false);
                }
                updatePointsInt(m_autonCoralText, false);
            }
        });

        Button autonCoralDecrButtonL3 = v.findViewById(R.id.auton_coral_scoring_decr_L3);
        autonCoralDecrButtonL3.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                if (getCurrentCoralLevel() == 1)
                {
                    updatePointsInt(m_autonCoralTextL1, false);
                }
                if (getCurrentCoralLevel() == 2)
                {
                    updatePointsInt(m_autonCoralTextL2, false);
                }
                if (getCurrentCoralLevel() == 3)
                {
                    updatePointsInt(m_autonCoralTextL3, false);
                }
                if (getCurrentCoralLevel() == 4)
                {
                    updatePointsInt(m_autonCoralTextL4, false);
                }
                updatePointsInt(m_autonCoralText, false);
            }
        });

        Button autonCoralDecrButtonL4 = v.findViewById(R.id.auton_coral_scoring_decr_L4);
        autonCoralDecrButtonL4.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                if (getCurrentCoralLevel() == 1)
                {
                    updatePointsInt(m_autonCoralTextL1, false);
                }
                if (getCurrentCoralLevel() == 2)
                {
                    updatePointsInt(m_autonCoralTextL2, false);
                }
                if (getCurrentCoralLevel() == 3)
                {
                    updatePointsInt(m_autonCoralTextL3, false);
                }
                if (getCurrentCoralLevel() == 4)
                {
                    updatePointsInt(m_autonCoralTextL4, false);
                }
                updatePointsInt(m_autonCoralText, false);
            }
        });

        m_autonAlgaeButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {

                //Changes m_matchData's climb variable according to which radio button is selected
                if (getCurrentAlgaeLevel() == 0) //none
                {
                    m_autonAlgaeButtonNet.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                    m_autonAlgaeButtonNet.setChecked(false);
                    m_autonAlgaeButtonProcessor.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                    m_autonAlgaeButtonProcessor.setChecked(false);
                    m_autonAlgaeButtonNone.setTextColor(Color.parseColor("#49B6A9"));
                    m_autonAlgaeButtonNone.setChecked(true);
                    m_autonAlgaeIncr.setEnabled(false);
                    m_autonAlgaeDecr.setEnabled(false);
                    m_autonAlgaeDecrDisabled.setVisibility(View.VISIBLE);
                    m_autonAlgaeIncrDisabled.setVisibility(View.VISIBLE);
                }
                else if (getCurrentAlgaeLevel() == 1) //parked
                {
                    m_autonAlgaeButtonNet.setChecked(true);
                    m_autonAlgaeButtonNet.setTextColor(Color.parseColor("#A9FFCB")); //ensures text remains gray
                    m_autonAlgaeButtonProcessor.setChecked(false);
                    m_autonAlgaeButtonProcessor.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                    m_autonAlgaeButtonNone.setChecked(false);
                    m_autonAlgaeButtonNone.setTextColor(Color.parseColor("#999999"));
                    m_autonAlgaeTextNet.setText(String.valueOf(m_matchData.getAutonAlgaeNet()));
                    m_autonAlgaeIncr.setEnabled(true);
                    m_autonAlgaeDecr.setEnabled(true);
                    m_autonAlgaeText.setText(m_autonAlgaeTextNet.getText().toString());
                    m_autonAlgaeDecrDisabled.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrDisabled.setVisibility(View.INVISIBLE);
                    m_autonAlgaeDecr.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncr.setVisibility(View.INVISIBLE);
                    m_autonAlgaeDecrNet.setVisibility(View.VISIBLE);
                    m_autonAlgaeIncrNet.setVisibility(View.VISIBLE);
                    m_autonAlgaeDecrProcessor.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrProcessor.setVisibility(View.INVISIBLE);
                }
                else if (getCurrentCoralLevel() == 2) //onstage
                {
                    m_autonAlgaeButtonNet.setChecked(false);
                    m_autonAlgaeButtonNet.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                    m_autonAlgaeButtonProcessor.setChecked(true);
                    m_autonAlgaeButtonProcessor.setTextColor(Color.parseColor("#49B6A9")); //changes text color when stage level is 2
                    m_autonAlgaeButtonNone.setChecked(false);
                    m_autonAlgaeButtonNone.setTextColor(Color.parseColor("#363636"));
                    m_autonAlgaeTextProcessor.setText(String.valueOf(m_matchData.getAutonAlgaeProcessor()));
                    m_autonAlgaeIncr.setEnabled(true);
                    m_autonAlgaeDecr.setEnabled(true);
                    m_autonAlgaeText.setText(m_autonAlgaeTextProcessor.getText().toString());
                    m_autonAlgaeDecrDisabled.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrDisabled.setVisibility(View.INVISIBLE);
                    m_autonAlgaeDecr.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncr.setVisibility(View.INVISIBLE);
                    m_autonAlgaeDecrNet.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrNet.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrProcessor.setVisibility(View.VISIBLE);
                    m_autonAlgaeDecrProcessor.setVisibility(View.VISIBLE);
                }
            }
        });

        m_autonAlgaeButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {

                if (m_autonAlgaeButtonNone.isChecked() == false)
                {
                    m_autonAlgaeIncr.setEnabled(true);
                    m_autonAlgaeDecr.setEnabled(true);
                    m_autonAlgaeDecrDisabled.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrDisabled.setVisibility(View.INVISIBLE);
                }
                else
                {
                    m_autonAlgaeIncr.setEnabled(false);
                    m_autonAlgaeDecr.setEnabled(false);
                    m_autonAlgaeDecrDisabled.setVisibility(View.VISIBLE);
                    m_autonAlgaeIncrDisabled.setVisibility(View.VISIBLE);
                    m_autonAlgaeDecr.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncr.setVisibility(View.INVISIBLE);
                }
                if (m_autonAlgaeButtonNone.isChecked())
                {
                    m_autonAlgaeText.setText("0");
                    m_autonAlgaeDecrDisabled.setVisibility(View.VISIBLE);
                    m_autonAlgaeIncrDisabled.setVisibility(View.VISIBLE);
                    m_autonAlgaeDecr.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncr.setVisibility(View.INVISIBLE);
                    m_autonAlgaeDecrNet.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrNet.setVisibility(View.INVISIBLE);
                    m_autonAlgaeDecrProcessor.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrProcessor.setVisibility(View.INVISIBLE);
                    m_autonAlgaeButtonNet.setTextColor(Color.parseColor("#363636"));
                    m_autonAlgaeButtonProcessor.setTextColor(Color.parseColor("#363636"));
                    m_autonAlgaeButtonNone.setTextColor(Color.parseColor("#49B6A9"));
                    m_autonAlgaeNet.setTextColor(Color.parseColor("#363636"));
                    m_autonAlgaeProcessor.setTextColor(Color.parseColor("#363636"));
                }
                if (m_autonAlgaeButtonNet.isChecked())
                {
                    m_autonAlgaeText.setText(m_autonAlgaeTextNet.getText().toString());
                    m_autonAlgaeDecrDisabled.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrDisabled.setVisibility(View.INVISIBLE);
                    m_autonAlgaeDecr.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncr.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrNet.setVisibility(View.VISIBLE);
                    m_autonAlgaeDecrNet.setVisibility(View.VISIBLE);
                    m_autonAlgaeDecrProcessor.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrProcessor.setVisibility(View.INVISIBLE);
                    m_autonAlgaeNet.setTextColor(Color.parseColor("#A9FFCB"));
                    m_autonAlgaeProcessor.setTextColor(Color.parseColor("#363636"));
                    m_autonAlgaeButtonNet.setTextColor(Color.parseColor("#A9FFCB"));
                    m_autonAlgaeButtonProcessor.setTextColor(Color.parseColor("#363636"));
                    m_autonAlgaeButtonNone.setTextColor(Color.parseColor("#363636"));
                }
                if (m_autonAlgaeButtonProcessor.isChecked())
                {
                    m_autonAlgaeText.setText(m_autonAlgaeTextProcessor.getText().toString());
                    m_autonAlgaeDecrDisabled.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrDisabled.setVisibility(View.INVISIBLE);
                    m_autonAlgaeDecr.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncr.setVisibility(View.INVISIBLE);
                    m_autonAlgaeIncrNet.setVisibility(View.INVISIBLE);
                    m_autonAlgaeDecrNet.setVisibility(View.INVISIBLE);
                    m_autonAlgaeDecrProcessor.setVisibility(View.VISIBLE);
                    m_autonAlgaeIncrProcessor.setVisibility(View.VISIBLE);
                    m_autonAlgaeNet.setTextColor(Color.parseColor("#363636"));
                    m_autonAlgaeProcessor.setTextColor(Color.parseColor("#49B6A9"));
                    m_autonAlgaeButtonNet.setTextColor(Color.parseColor("#363636"));
                    m_autonAlgaeButtonProcessor.setTextColor(Color.parseColor("#49B6A9"));
                    m_autonAlgaeButtonNone.setTextColor(Color.parseColor("#363636"));
                }
                if (Integer.parseInt(m_autonAlgaeText.getText().toString()) <= MAX_POINTS)
                {
                    m_autonAlgaeText.setTextColor(Color.parseColor("#363636"));
                }
                if (Integer.parseInt(m_autonAlgaeText.getText().toString()) > MAX_POINTS)
                {
                    m_autonAlgaeText.setTextColor(Color.RED);
                }
            }
        });

        Button autonAlgaeIncrButton = v.findViewById(R.id.auton_algae_scoring_incr);
        autonAlgaeIncrButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                if (getCurrentAlgaeLevel() == 1)
                {
                    updatePointsInt(m_autonAlgaeTextNet, true);
                }
                if (getCurrentAlgaeLevel() == 2)
                {
                    updatePointsInt(m_autonAlgaeTextProcessor, true);
                }
                updatePointsInt(m_autonAlgaeText, true);
            }
        });

        Button autonAlgaeIncrButtonNet = v.findViewById(R.id.auton_algae_scoring_incr_net);
        autonAlgaeIncrButtonNet.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                if (getCurrentAlgaeLevel() == 1)
                {
                    updatePointsInt(m_autonAlgaeTextNet, true);
                }
                if (getCurrentAlgaeLevel() == 2)
                {
                    updatePointsInt(m_autonAlgaeTextProcessor, true);
                }
                updatePointsInt(m_autonAlgaeText, true);
            }
        });

        Button autonAlgaeIncrButtonProcessor = v.findViewById(R.id.auton_algae_scoring_incr_processor);
        autonAlgaeIncrButtonProcessor.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonAlgaeTextProcessor, true);
                updatePointsInt(m_autonAlgaeText, true);
            }
        });

        //Connects the decrement button for cones top row points and sets up a listener that detects when the button is clicked
        Button autonAlgaeDecrButton = v.findViewById(R.id.auton_algae_scoring_decr);
        autonAlgaeDecrButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                if (getCurrentAlgaeLevel() == 1)
                {
                    updatePointsInt(m_autonAlgaeTextNet, false);
                }
                if (getCurrentAlgaeLevel() == 2)
                {
                    updatePointsInt(m_autonAlgaeTextProcessor, false);
                }
                updatePointsInt(m_autonAlgaeText, false);
            }
        });

        Button autonAlgaeDecrButtonNet = v.findViewById(R.id.auton_algae_scoring_decr_net);
        autonAlgaeDecrButtonNet.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                if (getCurrentAlgaeLevel() == 1)
                {
                    updatePointsInt(m_autonAlgaeTextNet, false);
                }
                if (getCurrentAlgaeLevel() == 2)
                {
                    updatePointsInt(m_autonAlgaeTextProcessor, false);
                }
                updatePointsInt(m_autonAlgaeText, false);
            }
        });

        Button autonAlgaeDecrButtonProcessor = v.findViewById(R.id.auton_algae_scoring_decr_processor);
        autonAlgaeDecrButtonProcessor.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                updatePointsInt(m_autonAlgaeTextProcessor, false);
                updatePointsInt(m_autonAlgaeText, false);
            }
        });

        //Connects the checkbox for leaving starting zone and sets up a listener to detect when the checked status is changed
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


        m_autonCoralTextL1.setText(String.valueOf(m_matchData.getAutonCoralL1()));
        m_autonCoralTextL2.setText(String.valueOf(m_matchData.getAutonCoralL2()));
        m_autonCoralTextL3.setText(String.valueOf(m_matchData.getAutonCoralL3()));
        m_autonCoralTextL4.setText(String.valueOf(m_matchData.getAutonCoralL4()));
        m_autonAlgaeTextNet.setText(String.valueOf(m_matchData.getAutonAlgaeNet()));
        m_autonAlgaeTextProcessor.setText(String.valueOf(m_matchData.getAutonAlgaeProcessor()));
        //m_autonSpeakerNotes.setText(String.valueOf(m_matchData.getAutonSpeakerNotes()));

        if (isNotValidPoints(m_autonCoralText))
        {
            m_autonCoralText.setTextColor(Color.RED);
        }
        if (isNotValidPoints(m_autonAlgaeText))
        {
            m_autonAlgaeText.setTextColor(Color.RED);
        }
        return v;

    }

    public int getCurrentCoralLevel()
    {
        // Returns the integer level that is current checked in the radio buttons
        int rtn = 0;
        if (m_autonCoralButtonGroup.getCheckedRadioButtonId() == m_autonCoralButtonL1.getId())
        {
            rtn = 1;
        }
        else if (m_autonCoralButtonGroup.getCheckedRadioButtonId() == m_autonCoralButtonL2.getId())
        {
            rtn = 2;
        }
        else if (m_autonCoralButtonGroup.getCheckedRadioButtonId() == m_autonCoralButtonL3.getId())
        {
            rtn = 3;
        }
        else if (m_autonCoralButtonGroup.getCheckedRadioButtonId() == m_autonCoralButtonL4.getId())
        {
            rtn = 4;
        }
        return rtn;
    }

    public int getCurrentAlgaeLevel()
    {
        // Returns the integer level that is current checked in the radio buttons
        int rtn = 0;
        if (m_autonAlgaeButtonGroup.getCheckedRadioButtonId() == m_autonAlgaeButtonNet.getId())
        {
            rtn = 1;
        }
        else if (m_autonCoralButtonGroup.getCheckedRadioButtonId() == m_autonAlgaeButtonProcessor.getId())
        {
            rtn = 2;
        }
        return rtn;
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

        m_matchData.setAutonCoralL1(Integer.parseInt(m_autonCoralTextL1.getText().toString()));

        m_matchData.setAutonCoralL2(Integer.parseInt(m_autonCoralTextL2.getText().toString()));

        m_matchData.setAutonCoralL3(Integer.parseInt(m_autonCoralTextL3.getText().toString()));

        m_matchData.setAutonCoralL4(Integer.parseInt(m_autonCoralTextL4.getText().toString()));

        m_matchData.setAutonAlgaeNet(Integer.parseInt(m_autonAlgaeTextNet.getText().toString()));

        m_matchData.setAutonAlgaeProcessor(Integer.parseInt(m_autonAlgaeTextProcessor.getText().toString()));

        m_matchData.setCurrentStartingPosition(getCurrentStartingPosition());

        m_matchData.setAutonLeave(m_leaveCheckbox.isChecked());

        m_matchData.setFloorCoral(m_floorCoral.isChecked());

        m_matchData.setStationCoral(m_stationCoral.isChecked());

        m_matchData.setFloorAlgae(m_floorAlgae.isChecked());

        m_matchData.setReefAlgae(m_reefAlgae.isChecked());




    }
}