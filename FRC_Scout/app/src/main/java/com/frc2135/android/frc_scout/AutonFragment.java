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
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/** @noinspection ALL*/
public class AutonFragment extends Fragment
{
    private static final String TAG = "AutonFragment";
    private static final int MAX_NUM_CORAL = 3;
    //private static final int MAX_NUM_ALGAE = 2;
    private CheckBox m_preloadCheckbox;


    private TextView m_autonHopperTotal;
    private Button m_autonHopperIncrButton;
    private Button m_autonHopperDecrButton;

    private MatchData m_matchData;

    // Check if given field is greater than expected max number.
    private boolean isGreaterThanMax(TextView field,boolean bIsCoral)
    {
        boolean rtn = false;
        int num = Integer.parseInt(field.getText().toString());
        if (bIsCoral == true) {
            if (num > MAX_NUM_CORAL)  // for coral number
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


    public void updateAutonData()
    {

        m_matchData.setAutonHopper(Integer.parseInt(m_autonHopperTotal.getText().toString()));
        m_matchData.setAutonPreload(m_preloadCheckbox.isChecked());

        // Determine the reefscape face for each checkbox.
    }
}
