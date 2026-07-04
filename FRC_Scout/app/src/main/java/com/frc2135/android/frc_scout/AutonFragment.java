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
    private static final int MAX_NUM_HOPPERS = 1;

    private MatchData m_matchData;
    private AutonFragmentBinding m_binding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        ScoutingActivity activity = (ScoutingActivity) getActivity();
        if (activity != null)
        {
            m_matchData = activity.getCurrentMatch();
            if (m_matchData != null)
            {
                Log.d(TAG, "New match ID = " + m_matchData.getMatchID());
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null)
                {
                    actionBar.setTitle(R.string.autonomous_title);

                    Settings settings = Settings.getInstance(getContext());
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        m_binding = AutonFragmentBinding.inflate(inflater, parent, false);

        if (m_matchData != null)
        {
            m_binding.autonHopperScoreTotal.setText(String.valueOf(m_matchData.getAutonHopper()));

            m_binding.autonHopperIncrButton.setOnClickListener(v -> updateTotalsInt(m_binding.autonHopperScoreTotal, true));
            m_binding.autonHopperDecrButton.setOnClickListener(v -> updateTotalsInt(m_binding.autonHopperScoreTotal, false));

            m_binding.preloadCheckbox.setChecked(m_matchData.getAutonPreload());
            m_binding.azCheckbox.setChecked(m_matchData.getAutonAzCheckbox());
            m_binding.depotCheckbox.setChecked(m_matchData.getAutonDepotCheckbox());
            m_binding.outpostCheckbox.setChecked(m_matchData.getAutonOutpostCheckbox());
            m_binding.nzCheckbox.setChecked(m_matchData.getAutonNzCheckbox());

            initPreloadAccuracy(m_matchData.getPreloadAccuracyLevel());
            initAutonAccuracy(m_matchData.getAutonAccuracyRate());
            initAutonClimb(m_matchData.getAutonClimb());

            // Check Hopper levels for MAX
            updateScoreColor(m_binding.autonHopperScoreTotal);
        }

        return m_binding.getRoot();
    }

    private void initPreloadAccuracy(int value)
    {
        int id = switch (value)
        {
            case 0 -> R.id.auton_accuracy_no;
            case 1 -> R.id.auton_accuracy_a;
            case 2 -> R.id.auton_accuracy_m;
            case 3 -> R.id.auton_accuracy_s;
            case 4 -> R.id.auton_accuracy_f;
            case 5 -> R.id.auton_accuracy_n;
            default -> -1;
        };
        if (id != -1)
        {
            m_binding.autonAccuracyBoxes.check(id);
        }
    }

    private void initAutonAccuracy(int value)
    {
        int id = switch (value)
        {
            case 0 -> R.id.auton_accuracy_na;
            case 1 -> R.id.auton_accuracy_most;
            case 2 -> R.id.auton_accuracy_three_fourths;
            case 3 -> R.id.auton_accuracy_half;
            case 4 -> R.id.auton_accuracy_quarter;
            case 5 -> R.id.auton_accuracy_few;
            case 6 -> R.id.auton_accuracy_none;
            default -> -1;
        };
        if (id != -1)
        {
            m_binding.autonAccuracyButtons.check(id);
        }
    }

    private void initAutonClimb(int value)
    {
        int id = switch (value)
        {
            case 0 -> R.id.auton_climb_na;
            case 1 -> R.id.auton_climb_back;
            case 2 -> R.id.auton_climb_left;
            case 3 -> R.id.auton_climb_front;
            case 4 -> R.id.auton_climb_right;
            default -> -1;
        };
        if (id != -1)
        {
            m_binding.autonClimbButtons.check(id);
        }
    }

    // Check if given field is greater than expected max number.
    private boolean isGreaterThanMax(TextView field)
    {
        try
        {
            int num = Integer.parseInt(field.getText().toString());
            return num > MAX_NUM_HOPPERS;
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
        int id = m_binding.autonAccuracyButtons.getCheckedRadioButtonId();
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
        int id = m_binding.autonAccuracyBoxes.getCheckedRadioButtonId();
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
        int id = m_binding.autonClimbButtons.getCheckedRadioButtonId();
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
            m_matchData.setAutonHopper(Integer.parseInt(m_binding.autonHopperScoreTotal.getText().toString()));
        }
        catch (NumberFormatException e)
        {
            Log.e(TAG, "Invalid hopper score value", e);
        }
        m_matchData.setAutonPreload(m_binding.preloadCheckbox.isChecked());
        m_matchData.setAutonNzCheckbox(m_binding.nzCheckbox.isChecked());
        m_matchData.setAutonAzCheckbox(m_binding.azCheckbox.isChecked());
        m_matchData.setAutonDepotCheckbox(m_binding.depotCheckbox.isChecked());
        m_matchData.setAutonOutpostCheckbox(m_binding.outpostCheckbox.isChecked());
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
