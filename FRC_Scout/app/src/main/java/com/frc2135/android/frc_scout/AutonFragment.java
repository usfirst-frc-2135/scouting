package com.frc2135.android.frc_scout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.frc2135.android.frc_scout.databinding.AutonFragmentBinding;

public class AutonFragment extends Fragment
{
    private static final String TAG = "AutonFragment";
    private static final int MAX_NUM_HOPPERS = 1;

    private MatchData m_matchData;
    private AutonFragmentBinding binding;

    // Check if given field is greater than expected max number.
    private boolean isGreaterThanMax(TextView field)
    {
        int num = Integer.parseInt(field.getText().toString());
        return num > MAX_NUM_HOPPERS;
    }

    // Sets the new result integer value for the given TextView, either decrementing or 
    // incrementing the shown value. If the decrement case falls below zero, returns 0. 
    // Sets textView color to RED if out of expected range.
    public void updateTotalsInt(TextView tView, boolean bIncr)
    {
        int result = Integer.parseInt(tView.getText().toString()); // get current value as int
        if (bIncr)
        {
            result += 1;
        }
        else
        {
            result -= 1;
        }
        if (result < 0)
        {
            result = 0;
        }
        tView.setText(String.valueOf(result));
        if (isGreaterThanMax(tView))
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
                ActionBar actionBar = activity.getSupportActionBar();
                String teamNumber = m_matchData.getTeamNumber();
                String teamAlias = m_matchData.getTeamAlias();
                if (actionBar != null)
                {
                    // Use teamAlias if there is one instead of teamNumber.
                    if (!teamAlias.isEmpty())
                    {
                        actionBar.setTitle("Autonomous          Scouting Team " + teamAlias + "         Match " + m_matchData.getMatchNumber());
                    }
                    else
                    {
                        actionBar.setTitle("Autonomous          Scouting Team " + teamNumber + "         Match " + m_matchData.getMatchNumber());
                    }
                    Scouter myScouter = Scouter.get(getContext());
                    if (myScouter != null)
                    {
                        String color = myScouter.getTeamIndexColor();
                        if (color.equals("red"))
                        {
                            actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                        }
                        else if (color.equals("blue"))
                        {
                            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
                        }
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        binding = AutonFragmentBinding.inflate(inflater, parent, false);

        binding.autonHopperScoreTotal.setText(String.valueOf(m_matchData.getAutonHopper()));

        binding.autonHopperIncrButton.setOnClickListener(v -> updateTotalsInt(binding.autonHopperScoreTotal, true));
        binding.autonHopperDecrButton.setOnClickListener(v -> updateTotalsInt(binding.autonHopperScoreTotal, false));

        binding.preloadCheckbox.setChecked(m_matchData.getAutonPreload());
        binding.azCheckbox.setChecked(m_matchData.getAutonAzCheckbox());
        binding.depotCheckbox.setChecked(m_matchData.getAutonDepotCheckbox());
        binding.outpostCheckbox.setChecked(m_matchData.getAutonOutpostCheckbox());
        binding.nzCheckbox.setChecked(m_matchData.getAutonNzCheckbox());

        int preloadAccValue = m_matchData.getPreloadAccuracyLevel();
        if (preloadAccValue == 0)
        {
            binding.autonAccuracyNo.setChecked(true);
        }
        else if (preloadAccValue == 1)
        {
            binding.autonAccuracyA.setChecked(true);
        }
        else if (preloadAccValue == 2)
        {
            binding.autonAccuracyM.setChecked(true);
        }
        else if (preloadAccValue == 3)
        {
            binding.autonAccuracyS.setChecked(true);
        }
        else if (preloadAccValue == 4)
        {
            binding.autonAccuracyF.setChecked(true);
        }
        else if (preloadAccValue == 5)
        {
            binding.autonAccuracyN.setChecked(true);
        }

        int accValue = m_matchData.getAutonAccuracyRate();
        if (accValue == 0)
        {
            binding.autonAccuracyNa.setChecked(true);
        }
        else if (accValue == 1)
        {
            binding.autonAccuracyMost.setChecked(true);
        }
        else if (accValue == 2)
        {
            binding.autonAccuracyThreeFourths.setChecked(true);
        }
        else if (accValue == 3)
        {
            binding.autonAccuracyHalf.setChecked(true);
        }
        else if (accValue == 4)
        {
            binding.autonAccuracyQuarter.setChecked(true);
        }
        else if (accValue == 5)
        {
            binding.autonAccuracyFew.setChecked(true);
        }
        else if (accValue == 6)
        {
            binding.autonAccuracyNone.setChecked(true);
        }

        int climbValue = m_matchData.getAutonClimb();
        if (climbValue == 0)
        {
            binding.autonClimbNa.setChecked(true);
        }
        else if (climbValue == 1)
        {
            binding.autonClimbBack.setChecked(true);
        }
        else if (climbValue == 2)
        {
            binding.autonClimbLeft.setChecked(true);
        }
        else if (climbValue == 3)
        {
            binding.autonClimbFront.setChecked(true);
        }
        else if (climbValue == 4)
        {
            binding.autonClimbRight.setChecked(true);
        }

        // Check Hopper levels for MAX
        if (isGreaterThanMax(binding.autonHopperScoreTotal))
        {
            binding.autonHopperScoreTotal.setTextColor(Color.RED);
        }

        return binding.getRoot();
    }

    public int getAutonAccuracyRate()
    {
        int id = binding.autonAccuracyButtons.getCheckedRadioButtonId();
        if (id == R.id.auton_accuracy_na)
        {
            return 0;
        }
        if (id == R.id.auton_accuracy_most)
        {
            return 1;
        }
        if (id == R.id.auton_accuracy_three_fourths)
        {
            return 2;
        }
        if (id == R.id.auton_accuracy_half)
        {
            return 3;
        }
        if (id == R.id.auton_accuracy_quarter)
        {
            return 4;
        }
        if (id == R.id.auton_accuracy_few)
        {
            return 5;
        }
        if (id == R.id.auton_accuracy_none)
        {
            return 6;
        }
        return 0;
    }

    public int getPreloadAccuracyLevel()
    {
        int id = binding.autonAccuracyBoxes.getCheckedRadioButtonId();
        if (id == R.id.auton_accuracy_no)
        {
            return 0;
        }
        if (id == R.id.auton_accuracy_a)
        {
            return 1;
        }
        if (id == R.id.auton_accuracy_m)
        {
            return 2;
        }
        if (id == R.id.auton_accuracy_s)
        {
            return 3;
        }
        if (id == R.id.auton_accuracy_f)
        {
            return 4;
        }
        if (id == R.id.auton_accuracy_n)
        {
            return 5;
        }
        return 0;
    }

    public int getAutonClimb()
    {
        int id = binding.autonClimbButtons.getCheckedRadioButtonId();
        if (id == R.id.auton_climb_na)
        {
            return 0;
        }
        if (id == R.id.auton_climb_back)
        {
            return 1;
        }
        if (id == R.id.auton_climb_left)
        {
            return 2;
        }
        if (id == R.id.auton_climb_front)
        {
            return 3;
        }
        if (id == R.id.auton_climb_right)
        {
            return 4;
        }
        return 0;
    }

    public void updateAutonData()
    {
        m_matchData.setAutonHopper(Integer.parseInt(binding.autonHopperScoreTotal.getText().toString()));
        m_matchData.setAutonPreload(binding.preloadCheckbox.isChecked());
        m_matchData.setAutonNzCheckbox(binding.nzCheckbox.isChecked());
        m_matchData.setAutonAzCheckbox(binding.azCheckbox.isChecked());
        m_matchData.setAutonDepotCheckbox(binding.depotCheckbox.isChecked());
        m_matchData.setAutonOutpostCheckbox(binding.outpostCheckbox.isChecked());
        m_matchData.setAutonAccuracyRate(getAutonAccuracyRate());
        m_matchData.setPreloadAccuracyLevel(getPreloadAccuracyLevel());
        m_matchData.setAutonClimb(getAutonClimb());
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
