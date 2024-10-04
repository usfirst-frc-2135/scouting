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

    private TextView m_autonAmpNotes;
    private TextView m_autonAmpMisses;
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
            m_autonAmpNotes = v.findViewById(R.id.auton_amp_scoring_text);
            m_autonAmpNotes.setText("0");
            m_autonAmpNotes.setTextColor(specialTextPrimaryColor);

            // Sets up TextView that displays amp misses, setting 0 as the default
            m_autonAmpMisses = v.findViewById(R.id.auton_amp_misses_text);
            m_autonAmpMisses.setText("0");
            m_autonAmpMisses.setTextColor(specialTextPrimaryColor);

            // Sets up TextView that displays speaker note points, setting 0 as the default
            m_autonSpeakerNotes = v.findViewById(R.id.auton_speaker_scoring_text);
            m_autonSpeakerNotes.setText("0");
            m_autonSpeakerNotes.setTextColor(specialTextPrimaryColor);
        }


        //Connects the increment button for cubes top row points and sets up a listener that detects when the button is clicked
        Button autonAmpIncrButton = v.findViewById(R.id.auton_amp_scoring_incr);
        autonAmpIncrButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_autonAmpNotes, true);
            }
        });


        //Connects the decrement button for cones top row points and sets up a listener that detects when the button is clicked
        Button autonAmpDecrButton = v.findViewById(R.id.auton_amp_scoring_decr);
        autonAmpDecrButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autonAmpNotes, false);
            }
        });

        //Connects the increment button for misses and sets up a listener that detects when the button is clicked
        Button autonAmpMissesIncrButton = v.findViewById(R.id.auton_amp_misses_incr);
        autonAmpMissesIncrButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_autonAmpMisses, true);
            }
        });


        //Connects the decrement button for misses and sets up a listener that detects when the button is clicked
        Button autonAmpMissesDecrButton = v.findViewById(R.id.auton_amp_misses_decr);
        autonAmpMissesDecrButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                //Decreases displayed point value by 1; sets to 0 if result would be negative
                updatePointsInt(m_autonAmpMisses, false);
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
        m_autonAmpNotes.setText(String.valueOf(m_matchData.getAutonAmpNotes()));
        m_autonAmpMisses.setText(String.valueOf(m_matchData.getAutonAmpMisses()));
        m_autonSpeakerNotes.setText(String.valueOf(m_matchData.getAutonSpeakerNotes()));

        if (isNotValidPoints(m_autonAmpNotes))
        {
            m_autonAmpNotes.setTextColor(Color.RED);
        }
        if (isNotValidPoints(m_autonAmpMisses))
        {
            m_autonAmpMisses.setTextColor(Color.RED);
        }
        if (isNotValidPoints(m_autonSpeakerNotes))
        {
            m_autonSpeakerNotes.setTextColor(Color.RED);
        }
        return v;


    }



    public void updateAutonData()
    {

        m_matchData.setAutonAmpNotes(Integer.parseInt(m_autonAmpNotes.getText().toString()));

        m_matchData.setAutonAmpMisses(Integer.parseInt(m_autonAmpMisses.getText().toString()));

        m_matchData.setAutonSpeakerNotes(Integer.parseInt(m_autonSpeakerNotes.getText().toString()));

        m_matchData.setAutonLeave(m_leaveCheckbox.isChecked());


    }
}