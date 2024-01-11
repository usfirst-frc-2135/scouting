package com.frc2135.android.frc_scout;

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


public class TeleopFragment extends Fragment
{
    private static final int MAX_POINTS = 30;     // max for valid high or low points total

    private TextView m_teleopAmpNotes;
    private TextView m_teleopSpeakerNotes;
    private MatchData m_matchData;

    // Check if pointsTextView field is greater than the MAX_POINTS.
    private boolean isNotValidPoints(TextView field)
    {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if (num > MAX_POINTS)
            rtn = true;
        return rtn;
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
        if (result < 0)
            result = 0;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        //Creates a view using the specific fragment layout.
        View v = inflater.inflate(R.layout.teleop_fragment, parent, false);
        Context context = getContext();
        if (context != null)
        {
            // Setup TextViews that displays points, setting 0 as the default.
            m_teleopAmpNotes = v.findViewById(R.id.teleop_amp_scoring_text);
            m_teleopAmpNotes.setText("0");
            m_teleopAmpNotes.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));

            m_teleopSpeakerNotes = v.findViewById(R.id.teleop_speaker_scoring_text);
            m_teleopSpeakerNotes.setText("0");
            m_teleopSpeakerNotes.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));
        }

        //Connects the decrement button for cones bottom row points and sets up a listener that detects when the button is clicked
        Button teleopAmpDecrButton = v.findViewById(R.id.teleop_amp_scoring_decr);
        teleopAmpDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_teleopAmpNotes, false);
            }
        });

        //Connects the increment button for cones bottom row points and sets up a listener that detects when the button is clicked
        Button teleopAmpIncrButton = v.findViewById(R.id.teleop_amp_scoring_incr);
        teleopAmpIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopAmpNotes, true);
            }
        });

        //Connects the decrement button for cubes bottom row points and sets up a listener that detects when the button is clicked
        Button teleopSpeakerDecrButton = v.findViewById(R.id.teleop_speaker_scoring_decr);
        teleopSpeakerDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_teleopSpeakerNotes, false);
            }
        });

        //Connects the increment button for cubes bottom row points and sets up a listener that detects when the button is clicked
        Button teleopSpeakerIncrButton = v.findViewById(R.id.teleop_speaker_scoring_incr);
        teleopSpeakerIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopSpeakerNotes, true);
            }
        });

        m_teleopAmpNotes.setText(String.valueOf(m_matchData.getTeleopAmpNotes()));
        m_teleopSpeakerNotes.setText(String.valueOf(m_matchData.getTeleopSpeakerNotes()));
        if (isNotValidPoints(m_teleopAmpNotes))
        {
            m_teleopAmpNotes.setTextColor(Color.RED);
        }
        if (isNotValidPoints(m_teleopSpeakerNotes))
        {
            m_teleopSpeakerNotes.setTextColor(Color.RED);
        }
        return v;
    }

    public void updateTeleopData()
    {
        m_matchData.setTeleopAmpNotes(Integer.parseInt(m_teleopAmpNotes.getText().toString()));
        m_matchData.setTeleopSpeakerNotes(Integer.parseInt(m_teleopSpeakerNotes.getText().toString()));
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
