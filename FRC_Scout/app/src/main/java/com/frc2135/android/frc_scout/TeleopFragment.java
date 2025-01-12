package com.frc2135.android.frc_scout;
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


/** @noinspection SpellCheckingInspection*/
public class TeleopFragment extends Fragment
{
    private static final String TAG = "TeleopFragment";
    private static final int MAX_POINTS = 12;     // max for valid high or low points total

    private CheckBox m_pickUpCoral;
    private CheckBox m_pickUpAlgae;
    private CheckBox m_knockAlgaeOff;
    private CheckBox m_algaeFromReef;
    private CheckBox m_holdBothElements;
    private CheckBox m_playedDefense;
    private TextView m_coralAcquire;
    private TextView m_algaeAcquire;
    private TextView m_teleopSpeakerMisses;
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
            m_coralAcquire = v.findViewById(R.id.coral_acquire_text);
            m_coralAcquire.setText("0");
            m_coralAcquire.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));

            m_algaeAcquire = v.findViewById(R.id.algae_acquire_text);
            m_algaeAcquire.setText("0");
            m_algaeAcquire.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));
/*
            m_teleopSpeakerNotes = v.findViewById(R.id.teleop_speaker_scoring_text);
            m_teleopSpeakerNotes.setText("0");
            m_teleopSpeakerNotes.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));

            m_teleopSpeakerMisses = v.findViewById(R.id.teleop_speaker_missing_text);
            m_teleopSpeakerMisses.setText("0");
            m_teleopSpeakerMisses.setTextColor(ContextCompat.getColor(context, R.color.specialTextPrimary));

        }
        */
            //Connects the decrement button for amp scoring and sets up a listener that detects when the button is clicked
            Button coralAcquireDecrButton = v.findViewById(R.id.coral_acquire_decr);
            coralAcquireDecrButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // Decreases displayed point value by 1; sets to 0 if result would be negative.
                    updatePointsInt(m_coralAcquire, false);
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
                    updatePointsInt(m_coralAcquire, true);
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
                updatePointsInt(m_algaeAcquire, true);
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
                updatePointsInt(m_algaeAcquire, false);
            }
        });
/*
        //Connects the decrement button for speaker scoring and sets up a listener that detects when the button is clicked
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

        //Connects the increment button for speaker scoring and sets up a listener that detects when the button is clicked
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


        //Connects the decrement button for speaker missing and sets up a listener that detects when the button is clicked
        Button teleopSpeakerMissesDecrButton = v.findViewById(R.id.teleop_speaker_missing_decr);
        teleopSpeakerMissesDecrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Decreases displayed point value by 1; sets to 0 if result would be negative.
                updatePointsInt(m_teleopSpeakerMisses, false);
            }
        });

        //Connects the increment button for speaker missing and sets up a listener that detects when the button is clicked
        Button teleopSpeakerMissesIncrButton = v.findViewById(R.id.teleop_speaker_missing_incr);
        teleopSpeakerMissesIncrButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Increases displayed point value by 1
                updatePointsInt(m_teleopSpeakerMisses, true);
            }
        });

        */
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

            m_playedDefense = v.findViewById(R.id.played_defense);
            m_playedDefense.setChecked(m_matchData.getPlayedDefense());

            m_coralAcquire.setText(String.valueOf(m_matchData.getCoralAcquire()));
            m_algaeAcquire.setText(String.valueOf(m_matchData.getAlgaeAcquire()));
            // m_teleopSpeakerMisses. setText(String.valueOf(m_matchData.getTeleopSpeakerMisses()));
            // m_teleopAmpMisses.setText(String.valueOf(m_matchData.getTeleopAmpMisses()));
            if (isNotValidPoints(m_coralAcquire))
            {
                m_coralAcquire.setTextColor(Color.RED);
            }
            if (isNotValidPoints(m_algaeAcquire))
            {
                m_algaeAcquire.setTextColor(Color.RED);
            }
            /*
            if (isNotValidPoints(m_teleopAmpMisses))
            {
                m_teleopAmpMisses.setTextColor(Color.RED);
            }
            if (isNotValidPoints(m_teleopSpeakerMisses))
            {
                m_teleopSpeakerMisses.setTextColor(Color.RED);
            }

             */
        }
            return v;
    }

    public void updateTeleopData()
    {
        m_matchData.setCoralAcquire(Integer.parseInt(m_coralAcquire.getText().toString()));

        m_matchData.setAlgaeAcquire(Integer.parseInt(m_algaeAcquire.getText().toString()));

        //      m_matchData.setTeleopAmpMisses(Integer.parseInt(m_teleopAmpMisses.getText().toString()));

        //    m_matchData.setTeleopSpeakerMisses(Integer.parseInt(m_teleopSpeakerMisses.getText().toString()));

        m_matchData.setPickUpCoral(m_pickUpCoral.isChecked());

        m_matchData.setPickUpAlgae(m_pickUpAlgae.isChecked());

        m_matchData.setKnockOffAlgae(m_knockAlgaeOff.isChecked());

        m_matchData.setAlgaeFromReef(m_algaeFromReef.isChecked());

        m_matchData.setHoldBothElements(m_holdBothElements.isChecked());

        m_matchData.setPlayedDefense(m_playedDefense.isChecked());

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
