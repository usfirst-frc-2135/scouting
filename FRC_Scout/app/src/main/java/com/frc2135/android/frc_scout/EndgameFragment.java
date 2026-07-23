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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.frc2135.android.frc_scout.databinding.EndgameFragmentBinding;

import java.util.Objects;

/**
 * Fragment for recording endgame scouting data (climbing, comments, etc.).
 * Manages UI components for start climb time, climb level, climb position, and whether the robot died during the match.
 */
public class EndgameFragment extends Fragment
{
    private static final String TAG = "EndgameFragment";
    public static final String QRTAG = "qr";

    private static final int[] START_CLIMB_IDS = {
            R.id.endgame_start_climb_na,
            R.id.endgame_start_climb_before,
            R.id.endgame_start_climb_bell,
            R.id.endgame_start_climb_ten,
            R.id.endgame_start_climb_less
    };

    private static final int[] CLIMB_LEVEL_IDS = {
            R.id.endgame_climb_level_na,
            R.id.endgame_climb_level_one,
            R.id.endgame_climb_level_two,
            R.id.endgame_climb_level_three
    };

    private static final int[] CLIMB_POS_IDS = {
            R.id.endgame_climb_pos_na,
            R.id.endgame_climb_pos_back,
            R.id.endgame_climb_pos_left,
            R.id.endgame_climb_pos_front,
            R.id.endgame_climb_pos_right
    };

    private static final int[] DIED_VALUE_IDS = {
            R.id.endgame_died_na,
            R.id.endgame_died_most,
            R.id.endgame_died_min,
            R.id.endgame_died_thirty,
            R.id.endgame_died_tt,
            R.id.endgame_died_no_show
    };

    private MatchData m_matchData;
    private EndgameFragmentBinding m_binding;
    private Settings m_settings;

    /**
     * Initializes the fragment and retrieves the current match data from the parent activity.
     *
     * @param savedInstanceState if the fragment is being re-created from a previous saved state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
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
        m_binding = EndgameFragmentBinding.inflate(inflater, parent, false);
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
        setupDoneButton(false);
    }

    /**
     * Configures the action bar title and background color based on the team alliance.
     */
    private void setupActionBar()
    {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(R.string.endgame_title);

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

        initStartClimbing(m_matchData.getStartClimb());

        initClimbLevel(m_matchData.getEndgameClimbLevel());
        initClimbPos(m_matchData.getEndgameClimbPos());

        initDiedValue(m_matchData.getDiedValue());

        m_binding.endgameCommentsInput.setText(m_matchData.getComment());
    }

    /**
     * Sets up click listeners for the UI components, including QR code generation and match completion.
     */
    private void setupListeners()
    {
        m_binding.endgameGenerateQrButton.setEnabled(true);

        m_binding.endgameGenerateQrButton.setOnClickListener(view -> {
            updateEndgameData();
            String validationMsg = m_matchData.validateEntries();
            if (!validationMsg.isEmpty())
            {
                Log.w(TAG, "Match data validation failed: " + validationMsg);
                Toast.makeText(getContext(), validationMsg, Toast.LENGTH_LONG).show();
            }
            else
            {
                Log.i(TAG, "Match data validation successful: " + validationMsg);
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                QRCodeDialog dialog = QRCodeDialog.newInstance(m_matchData);
                dialog.show(fm, QRTAG);
                setupDoneButton(true);
            }
        });

        m_binding.endgameDoneButton.setOnClickListener(view -> {
            updateEndgameData();

            m_settings.setMostRecentMatchNumber(m_matchData.getMatchNumber());
            m_settings.addPastScoutNames(m_matchData.getScoutName());
            m_settings.setMostRecentScoutName(m_matchData.getScoutName());

            Log.i(TAG, "Saving latest match and scout names");
            if (!m_settings.saveSettingsSilent())
            {
                Log.e(TAG, "Failed to save settings!");
            }
            ScoutedMatches scoutedMatches = ScoutedMatches.getInstance(requireContext());
            if (!scoutedMatches.saveMatchDataFile(m_matchData))
            {
                Log.e(TAG, "Failed to save Match Data!");
                Toast.makeText(requireContext(), "Error: Failed to save match data!", Toast.LENGTH_SHORT).show();
            }

            Intent i = new Intent(requireContext(), MatchListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            requireActivity().finish();
        });
    }

    /**
     * Initializes the start climbing time radio group selection.
     *
     * @param value the index of the selected start climb time
     */
    private void initStartClimbing(int value)
    {
        if (value >= 0 && value < START_CLIMB_IDS.length)
        {
            m_binding.endgameStartClimbRadioGroup.check(START_CLIMB_IDS[value]);
        }
    }

    /**
     * Initializes the climb level radio group selection.
     *
     * @param value the index of the selected climb level
     */
    private void initClimbLevel(int value)
    {
        if (value >= 0 && value < CLIMB_LEVEL_IDS.length)
        {
            m_binding.endgameClimbLevelRadioGroup.check(CLIMB_LEVEL_IDS[value]);
        }
    }

    /**
     * Initializes the climb position radio group selection.
     *
     * @param value the index of the selected climb position
     */
    private void initClimbPos(int value)
    {
        if (value >= 0 && value < CLIMB_POS_IDS.length)
        {
            m_binding.endgameClimbPosRadioGroup.check(CLIMB_POS_IDS[value]);
        }
    }

    /**
     * Initializes the "died" value radio group selection.
     *
     * @param value the index of the selected died value
     */
    private void initDiedValue(int value)
    {
        if (value >= 0 && value < DIED_VALUE_IDS.length)
        {
            m_binding.endgameDiedRadioGroup.check(DIED_VALUE_IDS[value]);
        }
    }

    /**
     * Retrieves the selected "died" value index.
     *
     * @return the index of the selected radio button in the died group
     */
    private int getDiedValue()
    {
        int id = m_binding.endgameDiedRadioGroup.getCheckedRadioButtonId();
        for (int i = 0; i < DIED_VALUE_IDS.length; i++)
        {
            if (id == DIED_VALUE_IDS[i])
            {
                return i;
            }
        }
        return 0;
    }

    /**
     * Retrieves the selected start climb time index.
     *
     * @return the index of the selected radio button in the start climb group
     */
    private int getStartClimb()
    {
        int id = m_binding.endgameStartClimbRadioGroup.getCheckedRadioButtonId();
        for (int i = 0; i < START_CLIMB_IDS.length; i++)
        {
            if (id == START_CLIMB_IDS[i])
            {
                return i;
            }
        }
        return 0;
    }

    /**
     * Retrieves the selected climb position index.
     *
     * @return the index of the selected radio button in the climb position group
     */
    private int getClimbPos()
    {
        int id = m_binding.endgameClimbPosRadioGroup.getCheckedRadioButtonId();
        for (int i = 0; i < CLIMB_POS_IDS.length; i++)
        {
            if (id == CLIMB_POS_IDS[i])
            {
                return i;
            }
        }
        return 0;
    }

    /**
     * Retrieves the selected climb level index.
     *
     * @return the index of the selected radio button in the climb level group
     */
    private int getClimbLevel()
    {
        int id = m_binding.endgameClimbLevelRadioGroup.getCheckedRadioButtonId();
        for (int i = 0; i < CLIMB_LEVEL_IDS.length; i++)
        {
            if (id == CLIMB_LEVEL_IDS[i])
            {
                return i;
            }
        }
        return 0;
    }

    /**
     * Updates the {@link MatchData} object with the current values from the UI components.
     */
    public void updateEndgameData()
    {
        Log.d(TAG, "updateEndgameData");
        if (m_matchData == null || m_binding == null)
        {
            return;
        }
        m_matchData.setStartClimb(getStartClimb());
        m_matchData.setDiedValue(getDiedValue());
        m_matchData.setComment(Objects.requireNonNull(m_binding.endgameCommentsInput.getText()).toString());
        m_matchData.setEndgameClimbPos(getClimbPos());
        m_matchData.setEndgameClimbLevel(getClimbLevel());
    }

    /**
     * Sets the enabled state and visibility of the "Done" button.
     *
     * @param bEnable true to enable the button, false to disable and show the placeholder
     */
    private void setupDoneButton(boolean bEnable)
    {
        m_binding.endgameDoneButton.setEnabled(bEnable);
        if (bEnable)
        {
            Log.d(TAG, "setupDoneButton: Enable Done Button");
            m_binding.endgameDoneButton.setVisibility(View.VISIBLE);
            m_binding.endgameDoneButtonDisabled.setVisibility(View.INVISIBLE);
        }
        else
        {
            Log.d(TAG, "setupDoneButton: Disable Done Button");
            m_binding.endgameDoneButton.setVisibility(View.INVISIBLE);
            m_binding.endgameDoneButtonDisabled.setVisibility(View.VISIBLE);
        }
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
