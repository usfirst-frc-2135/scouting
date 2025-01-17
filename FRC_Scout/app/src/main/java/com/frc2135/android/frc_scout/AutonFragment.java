package com.frc2135.android.frc_scout;

import android.content.Context;
import android.content.res.ColorStateList;
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

    private TextView m_autonSpeakerNotes;
    private CheckBox m_leaveCheckbox;
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
            m_autonSpeakerNotes = v.findViewById(R.id.auton_speaker_scoring_text);
            m_autonSpeakerNotes.setText("0");
            m_autonSpeakerNotes.setTextColor(specialTextPrimaryColor);

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

            m_autonCoralButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {

                    //Changes m_matchData's climb variable according to which radio button is selected
                    //REMOVE --> m_matchData.setEndgameStage(getCurrentCoralLevel());
                    if (getCurrentCoralLevel() == 0) //none
                    {
                        //radio_endGame0.setChecked(true);
                        //radio_endGame0.setTextColor(Color.parseColor("#999999")); //ensures that text remains gray
                        //radio_endGame0.setEnabled(false);
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_autonCoralButtonL1.setChecked(false);
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_autonCoralButtonL2.setChecked(false);
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#999999"));
                        m_autonCoralButtonL3.setChecked(false);
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#999999"));
                        m_autonCoralButtonL4.setChecked(false);
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#999999"));
                        m_autonCoralButtonNone.setChecked(true);
                        m_autonCoralIncr.setEnabled(false);
                        m_autonCoralDecr.setEnabled(false);
                        m_autonCoralDecrDisabled.setVisibility(View.VISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.VISIBLE);
                        //m_endGameRadioGroupHarmony.setBackgroundColor(Color.parseColor("#d5e5ee")); //ensures that background remains this color
                    }
                    else if (getCurrentCoralLevel() == 1) //parked
                    {
                        //radio_endGame0.setChecked(true);
                        //radio_endGame0.setEnabled(false);
                        //radio_endGame0.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
                        m_autonCoralButtonL1.setChecked(true);
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#999999")); //ensures text remains gray
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
                        m_autonCoralDecr.setVisibility(View.VISIBLE);
                        m_autonCoralIncr.setVisibility(View.VISIBLE);
                        //m_endGameRadioGroupHarmony.setBackgroundColor(Color.parseColor("#d5e5ee")); //ensures background remains this color
                    }
                    else if (getCurrentCoralLevel() == 2) //onstage
                    {
                        //radio_endGame0.setEnabled(true);
                        //radio_endGame0.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL1.setChecked(false);
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL2.setChecked(true);
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL3.setChecked(false);
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
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
                        m_autonCoralDecr.setVisibility(View.VISIBLE);
                        m_autonCoralIncr.setVisibility(View.VISIBLE);
                        //m_endGameRadioGroupHarmony.setBackgroundColor(Color.parseColor("#CEEAF5")); //changes background color when stage level is 2
                    }
                    else if (getCurrentCoralLevel() == 3) //onstage
                    {
                        //radio_endGame0.setEnabled(true);
                        //radio_endGame0.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL1.setChecked(false);
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL2.setChecked(false);
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL3.setChecked(true);
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
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
                        m_autonCoralDecr.setVisibility(View.VISIBLE);
                        m_autonCoralIncr.setVisibility(View.VISIBLE);
                        //m_endGameRadioGroupHarmony.setBackgroundColor(Color.parseColor("#CEEAF5")); //changes background color when stage level is 2
                    }
                    else if (getCurrentCoralLevel() == 4) //onstage
                    {
                        //radio_endGame0.setEnabled(true);
                        //radio_endGame0.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL1.setChecked(false);
                        m_autonCoralButtonL1.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL2.setChecked(false);
                        m_autonCoralButtonL2.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL3.setChecked(false);
                        m_autonCoralButtonL3.setTextColor(Color.parseColor("#363636")); //changes text color when stage level is 2
                        m_autonCoralButtonL4.setChecked(true);
                        m_autonCoralButtonL4.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralButtonNone.setChecked(false);
                        m_autonCoralButtonNone.setTextColor(Color.parseColor("#363636"));
                        m_autonCoralTextL4.setText(String.valueOf(m_matchData.getAutonCoralL4()));
                        m_autonCoralIncr.setEnabled(true);
                        m_autonCoralDecr.setEnabled(true);
                        m_autonCoralText.setText(m_autonCoralTextL4.getText().toString());
                        m_autonCoralDecrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralIncrDisabled.setVisibility(View.INVISIBLE);
                        m_autonCoralDecr.setVisibility(View.VISIBLE);
                        m_autonCoralIncr.setVisibility(View.VISIBLE);
                        //m_endGameRadioGroupHarmony.setBackgroundColor(Color.parseColor("#CEEAF5")); //changes background color when stage level is 2
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
                        m_autonCoralDecr.setVisibility(View.VISIBLE);
                        m_autonCoralIncr.setVisibility(View.VISIBLE);
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
                    }
                    if (m_autonCoralButtonL1.isChecked())
                    {
                        m_autonCoralText.setText(m_autonCoralTextL1.getText().toString());
                    }
                    if (m_autonCoralButtonL2.isChecked())
                    {
                        m_autonCoralText.setText(m_autonCoralTextL2.getText().toString());
                    }
                    if (m_autonCoralButtonL3.isChecked())
                    {
                        m_autonCoralText.setText(m_autonCoralTextL3.getText().toString());
                    }
                    if (m_autonCoralButtonL4.isChecked())
                    {
                        m_autonCoralText.setText(m_autonCoralTextL4.getText().toString());
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


        //Connects the decrement button for cones top row points and sets up a listener that detects when the button is clicked
        Button autonAmpDecrButton = v.findViewById(R.id.auton_coral_scoring_decr);
        autonAmpDecrButton.setOnClickListener(new View.OnClickListener()

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

        //Connects the increment button for cones top row points and sets up a listener that detects when the button is clicked
        Button autonSpeakerIncrButton = v.findViewById(R.id.auton_speaker_scoring_incr);
        autonSpeakerIncrButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
               updatePointsInt(m_autonSpeakerNotes, true);
            }
        });





        //Connects the decrement button for cubes top row points and sets up a listener that detects when the button is clicked
        Button autonSpeakerDecrButton = v.findViewById(R.id.auton_speaker_scoring_decr);
        autonSpeakerDecrButton.setOnClickListener(new View.OnClickListener()


       {
           @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autonSpeakerNotes, false);
            }
        });


        //Connects the checkbox for exiting the community and sets up a listener to detect when the checked status is changed
        m_leaveCheckbox = v.findViewById(R.id.leave_checkbox);
        m_leaveCheckbox.setChecked(m_matchData.getAutonLeave());
        m_autonCoralTextL1.setText(String.valueOf(m_matchData.getAutonCoralL1()));
        m_autonCoralTextL2.setText(String.valueOf(m_matchData.getAutonCoralL2()));
        m_autonCoralTextL3.setText(String.valueOf(m_matchData.getAutonCoralL3()));
        m_autonCoralTextL4.setText(String.valueOf(m_matchData.getAutonCoralL4()));
        //m_autonSpeakerNotes.setText(String.valueOf(m_matchData.getAutonSpeakerNotes()));

        if (isNotValidPoints(m_autonCoralText))
        {
            m_autonCoralText.setTextColor(Color.RED);
        }
        if (isNotValidPoints(m_autonSpeakerNotes))
        {
            m_autonSpeakerNotes.setTextColor(Color.RED);
        }
        return v;


    }

    public int getCurrentCoralLevel()
    {
        // Returns the integer climb level that is current checked in the radio buttons
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



    public void updateAutonData()
    {

        m_matchData.setAutonCoralL1(Integer.parseInt(m_autonCoralTextL1.getText().toString()));

        m_matchData.setAutonCoralL2(Integer.parseInt(m_autonCoralTextL2.getText().toString()));

        m_matchData.setAutonCoralL3(Integer.parseInt(m_autonCoralTextL3.getText().toString()));

        m_matchData.setAutonCoralL4(Integer.parseInt(m_autonCoralTextL4.getText().toString()));

        m_matchData.setAutonSpeakerNotes(Integer.parseInt(m_autonSpeakerNotes.getText().toString()));

        m_matchData.setAutonLeave(m_leaveCheckbox.isChecked());


    }
}