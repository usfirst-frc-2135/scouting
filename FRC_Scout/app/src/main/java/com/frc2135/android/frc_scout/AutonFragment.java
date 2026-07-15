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

/**
 * Fragment for recording autonomous period scouting data.
 */
public class AutonFragment extends Fragment
{
    private static final String TAG = "AutonFragment";

    private static final int[] PRELOAD_ACCURACY_IDS = {
            R.id.auton_preload_accuracy_no,
            R.id.auton_preload_accuracy_all,
            R.id.auton_preload_accuracy_most,
            R.id.auton_preload_accuracy_half,
            R.id.auton_preload_accuracy_few,
            R.id.auton_preload_accuracy_none
    };

    private static final int[] AUTON_ACCURACY_IDS = {
            R.id.auton_accuracy_na,
            R.id.auton_accuracy_most,
            R.id.auton_accuracy_three_fourths,
            R.id.auton_accuracy_half,
            R.id.auton_accuracy_quarter,
            R.id.auton_accuracy_few,
            R.id.auton_accuracy_none
    };

    private static final int[] AUTON_CLIMB_IDS = {
            R.id.auton_climb_na,
            R.id.auton_climb_back,
            R.id.auton_climb_left,
            R.id.auton_climb_front,
            R.id.auton_climb_right
    };

    private MatchData m_matchData;
    private AutonFragmentBinding m_binding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        ScoutingActivity activity = (ScoutingActivity) requireActivity();
        m_matchData = activity.getCurrentMatch();
        if (m_matchData != null)
        {
            Log.d(TAG, "New match ID = " + m_matchData.getMatchID());
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null)
            {
                actionBar.setTitle(R.string.autonomous_title);

                Settings settings = Settings.getInstance(requireContext());
                if (settings != null)
                {
                    String color = settings.getTeamIndexColor();
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        m_binding = AutonFragmentBinding.inflate(inflater, parent, false);

        if (m_matchData != null)
        {
            m_binding.autonHopperTotalText.setText(String.valueOf(m_matchData.getAutonHopper()));

            m_binding.autonHopperIncrButton.setOnClickListener(v -> updateTotalsInt(m_binding.autonHopperTotalText, true));
            m_binding.autonHopperDecrButton.setOnClickListener(v -> updateTotalsInt(m_binding.autonHopperTotalText, false));

            m_binding.autonPreloadCheckbox.setChecked(m_matchData.isAutonPreload());
            m_binding.autonAzCheckbox.setChecked(m_matchData.isAutonAz());
            m_binding.autonDepotCheckbox.setChecked(m_matchData.isAutonDepot());
            m_binding.autonOutpostCheckbox.setChecked(m_matchData.isAutonOutpost());
            m_binding.autonNzCheckbox.setChecked(m_matchData.isAutonNz());

            initPreloadAccuracy(m_matchData.getPreloadAccuracyLevel());
            initAutonAccuracy(m_matchData.getAutonAccuracyRate());
            initAutonClimb(m_matchData.getAutonClimb());

            // Check Hopper levels for MAX
            updateScoreColor(m_binding.autonHopperTotalText);
        }

        return m_binding.getRoot();
    }

    private void initPreloadAccuracy(int value)
    {
        if (value >= 0 && value < PRELOAD_ACCURACY_IDS.length)
        {
            m_binding.autonPreloadAccuracyRadioGroup.check(PRELOAD_ACCURACY_IDS[value]);
        }
    }

    private void initAutonAccuracy(int value)
    {
        if (value >= 0 && value < AUTON_ACCURACY_IDS.length)
        {
            m_binding.autonAccuracyRadioGroup.check(AUTON_ACCURACY_IDS[value]);
        }
    }

    private void initAutonClimb(int value)
    {
        if (value >= 0 && value < AUTON_CLIMB_IDS.length)
        {
            m_binding.autonClimbRadioGroup.check(AUTON_CLIMB_IDS[value]);
        }
    }

    // Check if given field is greater than expected max number.
    private boolean isGreaterThanMax(TextView field)
    {
        try
        {
            int num = Integer.parseInt(field.getText().toString());
            return num > MatchData.MAX_AUTON_HOPPERS;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    private void updateScoreColor(TextView tView)
    {
        if (isGreaterThanMax(tView))
        {
            tView.setTextColor(getResources().getColor(R.color.errorColor, requireContext().getTheme()));
        }
        else
        {
            Context context = getContext();
            if (context != null)
            {
                tView.setTextColor(ContextCompat.getColor(context, R.color.customTextPrimary));
            }
        }
    }

    // Sets the new result integer value for the given TextView, either decrementing or
    // incrementing the shown value. If the decrement case falls below zero, returns 0.
    // Sets textView color to RED if out of expected range.
    public void updateTotalsInt(TextView tView, boolean bIncr)
    {
        try
        {
            int currentVal = Integer.parseInt(tView.getText().toString());
            int newVal;
            if (bIncr)
            {
                newVal = currentVal + 1;
            }
            else
            {
                newVal = Math.max(0, currentVal - 1);
            }
            tView.setText(String.valueOf(newVal));
            updateScoreColor(tView);
        }
        catch (NumberFormatException e)
        {
            Log.e(TAG, "Invalid number format in updateTotalsInt", e);
        }
    }

    public int getAutonAccuracyRate()
    {
        int id = m_binding.autonAccuracyRadioGroup.getCheckedRadioButtonId();
        for (int i = 0; i < AUTON_ACCURACY_IDS.length; i++)
        {
            if (id == AUTON_ACCURACY_IDS[i])
            {
                return i;
            }
        }
        return 0;
    }

    public int getPreloadAccuracyLevel()
    {
        int id = m_binding.autonPreloadAccuracyRadioGroup.getCheckedRadioButtonId();
        for (int i = 0; i < PRELOAD_ACCURACY_IDS.length; i++)
        {
            if (id == PRELOAD_ACCURACY_IDS[i])
            {
                return i;
            }
        }
        return 0;
    }

    public int getAutonClimb()
    {
        int id = m_binding.autonClimbRadioGroup.getCheckedRadioButtonId();
        for (int i = 0; i < AUTON_CLIMB_IDS.length; i++)
        {
            if (id == AUTON_CLIMB_IDS[i])
            {
                return i;
            }
        }
        return 0;
    }

    /**
     * Updates the MatchData object with the latest inputs from this fragment.
     */
    public void updateAutonData()
    {
        Log.d(TAG, "updateAutonData()");
        if (m_matchData == null)
        {
            return;
        }
        try
        {
            m_matchData.setAutonHopper(Integer.parseInt(m_binding.autonHopperTotalText.getText().toString()));
        }
        catch (NumberFormatException e)
        {
            Log.e(TAG, "Invalid hopper score value", e);
        }
        m_matchData.setAutonPreload(m_binding.autonPreloadCheckbox.isChecked());
        m_matchData.setAutonNz(m_binding.autonNzCheckbox.isChecked());
        m_matchData.setAutonAz(m_binding.autonAzCheckbox.isChecked());
        m_matchData.setAutonDepot(m_binding.autonDepotCheckbox.isChecked());
        m_matchData.setAutonOutpost(m_binding.autonOutpostCheckbox.isChecked());
        m_matchData.setAutonAccuracyRate(getAutonAccuracyRate());
        m_matchData.setPreloadAccuracyLevel(getPreloadAccuracyLevel());
        m_matchData.setAutonClimb(getAutonClimb());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        m_binding = null;
    }
}
