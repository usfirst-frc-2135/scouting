/*
 * Copyright (c) 2025 FRC 2135 Presentation Invasion
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.frc2135.android.frc_scout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.frc2135.android.frc_scout.databinding.AutonFragmentBinding;

/**
 * Fragment for recording autonomous period scouting data.
 * Manages UI components for preload fuel, hopper usage, fuel sources, and climb position.
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
    private Settings m_settings;

    /**
     * Initializes the fragment and retrieves the current match data from the parent activity.
     *
     * @param savedInstanceState if the fragment is being re-created from a previous saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        m_matchData = ((ScoutingActivity) requireActivity()).getCurrentMatch();
        m_settings = Settings.getInstance(requireContext());
    }

    /**
     * Inflates the layout for this fragment using view binding.
     *
     * @param inflater           the LayoutInflater object that can be used to inflate views
     * @param parent             if non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState if non-null, this fragment is being re-constructed from a previous saved state
     * @return the root View of the inflated layout
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreateView");
        m_binding = AutonFragmentBinding.inflate(inflater, parent, false);
        return m_binding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView} has returned.
     * Sets up the action bar, loads match data into the UI, and initializes click listeners.
     *
     * @param view               the View returned by {@link #onCreateView}
     * @param savedInstanceState if non-null, this fragment is being re-constructed from a previous saved state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "onViewCreated");
        setupActionBar();
        loadMatchData();
        setupListeners();
    }

    /**
     * Configures the action bar title and background color based on the team alliance.
     */
    private void setupActionBar()
    {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(R.string.autonomous_title);

            if (m_settings != null)
            {
                actionBar.setBackgroundDrawable(m_settings.getTeamIndexColor());
            }
        }
    }

    /**
     * Populates the UI components with data from the current {@link MatchData} instance.
     */
    private void loadMatchData()
    {
        if (m_matchData == null)
        {
            return;
        }

        m_binding.autonPreloadChip.setChecked(m_matchData.isAutonPreload());
        initPreloadAccuracy(m_matchData.getPreloadAccuracyLevel());

        m_binding.autonHopperTotalText.setText(String.valueOf(m_matchData.getAutonHopper()));
        updateScoreColor(m_binding.autonHopperTotalText); // Check Hopper levels for MAX
        initAutonAccuracy(m_matchData.getAutonAccuracyRate());

        m_binding.autonAzChip.setChecked(m_matchData.isAutonAz());
        m_binding.autonDepotChip.setChecked(m_matchData.isAutonDepot());
        m_binding.autonOutpostChip.setChecked(m_matchData.isAutonOutpost());
        m_binding.autonNzChip.setChecked(m_matchData.isAutonNz());

        initAutonClimb(m_matchData.getAutonClimb());
    }

    /**
     * Sets up click listeners for the UI components.
     */
    private void setupListeners()
    {
        m_binding.autonHopperDecrButton.setOnClickListener(v -> updateTotalsInt(m_binding.autonHopperTotalText, false));
        m_binding.autonHopperIncrButton.setOnClickListener(v -> updateTotalsInt(m_binding.autonHopperTotalText, true));
    }

    /**
     * Initializes the preload accuracy radio group selection.
     *
     * @param value the index of the selected accuracy level
     */
    private void initPreloadAccuracy(int value)
    {
        if (value >= 0 && value < PRELOAD_ACCURACY_IDS.length)
        {
            m_binding.autonPreloadAccuracyRadioGroup.check(PRELOAD_ACCURACY_IDS[value]);
        }
    }

    /**
     * Initializes the autonomous accuracy radio group selection.
     *
     * @param value the index of the selected accuracy level
     */
    private void initAutonAccuracy(int value)
    {
        if (value >= 0 && value < AUTON_ACCURACY_IDS.length)
        {
            m_binding.autonAccuracyRadioGroup.check(AUTON_ACCURACY_IDS[value]);
        }
    }

    /**
     * Initializes the autonomous climb position radio group selection.
     *
     * @param value the index of the selected climb position
     */
    private void initAutonClimb(int value)
    {
        if (value >= 0 && value < AUTON_CLIMB_IDS.length)
        {
            m_binding.autonClimbRadioGroup.check(AUTON_CLIMB_IDS[value]);
        }
    }

    /**
     * Checks if the value in the given TextView exceeds the maximum allowed autonomous hoppers.
     *
     * @param field the TextView containing the numeric value
     * @return true if the value is greater than {@link MatchData#MAX_AUTON_HOPPERS}
     */
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

    /**
     * Updates the text color of a TextView based on whether its value exceeds the maximum limit.
     *
     * @param tView the TextView to update
     */
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

    /**
     * Updates a numeric total displayed in a TextView.
     *
     * @param tView the TextView to update
     * @param bIncr true to increment, false to decrement (clamped at 0)
     */
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
            Log.e(TAG, "updateTotalsInt: Invalid number format", e);
        }
    }

    /**
     * Retrieves the selected autonomous accuracy rate index.
     *
     * @return the index of the selected radio button in the accuracy group
     */
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

    /**
     * Retrieves the selected preload accuracy level index.
     *
     * @return the index of the selected radio button in the preload accuracy group
     */
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

    /**
     * Retrieves the selected autonomous climb position index.
     *
     * @return the index of the selected radio button in the climb position group
     */
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
     * Updates the {@link MatchData} object with the current values from the UI components.
     */
    public void updateAutonData()
    {
        Log.d(TAG, "updateAutonData");
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
            Log.e(TAG, "updateAutonData: Invalid hopper score value", e);
        }
        m_matchData.setAutonPreload(m_binding.autonPreloadChip.isChecked());
        m_matchData.setAutonNz(m_binding.autonNzChip.isChecked());
        m_matchData.setAutonAz(m_binding.autonAzChip.isChecked());
        m_matchData.setAutonDepot(m_binding.autonDepotChip.isChecked());
        m_matchData.setAutonOutpost(m_binding.autonOutpostChip.isChecked());
        m_matchData.setAutonAccuracyRate(getAutonAccuracyRate());
        m_matchData.setPreloadAccuracyLevel(getPreloadAccuracyLevel());
        m_matchData.setAutonClimb(getAutonClimb());
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    /**
     * Cleans up the view binding reference when the fragment view is being destroyed.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
        m_binding = null;
    }
}
