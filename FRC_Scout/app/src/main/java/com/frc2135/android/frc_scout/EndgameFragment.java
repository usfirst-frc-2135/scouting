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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.frc2135.android.frc_scout.databinding.EndgameFragmentBinding;

/**
 * Fragment for recording endgame scouting data (climbing, comments, etc.).
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        m_matchData = ((ScoutingActivity) requireActivity()).getCurrentMatch();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        m_binding = EndgameFragmentBinding.inflate(inflater, parent, false);
        return m_binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        setupActionBar();
        loadMatchData();
        setupListeners();
        setupDoneButton(false);
    }

    private void setupActionBar()
    {
        ActionBar actionBar = ((ScoutingActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null && m_matchData != null)
        {
            actionBar.setTitle(R.string.endgame_title);
        }
    }

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

    private void setupListeners()
    {
        m_binding.endgameGenerateQrButton.setEnabled(true);
        m_binding.endgameGenerateQrButtonDisabled.setVisibility(View.INVISIBLE);

        m_binding.endgameGenerateQrButton.setOnClickListener(view -> {
            updateEndgameData();
            String validationMsg = m_matchData.validateEntries();
            if (!validationMsg.isEmpty())
            {
                Log.d(TAG, "Validation failed: " + validationMsg);
                Toast.makeText(getContext(), validationMsg, Toast.LENGTH_LONG).show();
            }
            else
            {
                Log.d(TAG, "Validation successful: " + validationMsg);
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                QRCodeDialog dialog = QRCodeDialog.newInstance(m_matchData);
                dialog.show(fm, QRTAG);
                setupDoneButton(true);
            }
        });

        m_binding.endgameDoneButton.setOnClickListener(view -> {
            updateEndgameData();
            Log.d(TAG, "Saving latest match and scout names");
            if (!Settings.getInstance(requireContext()).saveSettingsSilent())
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

    private void initStartClimbing(int value)
    {
        if (value >= 0 && value < START_CLIMB_IDS.length)
        {
            m_binding.endgameStartClimbRadioGroup.check(START_CLIMB_IDS[value]);
        }
    }

    private void initClimbLevel(int value)
    {
        if (value >= 0 && value < CLIMB_LEVEL_IDS.length)
        {
            m_binding.endgameClimbLevelRadioGroup.check(CLIMB_LEVEL_IDS[value]);
        }
    }

    private void initClimbPos(int value)
    {
        if (value >= 0 && value < CLIMB_POS_IDS.length)
        {
            m_binding.endgameClimbPosRadioGroup.check(CLIMB_POS_IDS[value]);
        }
    }

    private void initDiedValue(int value)
    {
        if (value >= 0 && value < DIED_VALUE_IDS.length)
        {
            m_binding.endgameDiedRadioGroup.check(DIED_VALUE_IDS[value]);
        }
    }

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
     * Updates the MatchData object with the latest inputs from this fragment.
     */
    public void updateEndgameData()
    {
        Log.d(TAG, "updateEndgameData()");
        if (m_matchData == null || m_binding == null)
        {
            return;
        }
        m_matchData.setStartClimb(getStartClimb());
        m_matchData.setDiedValue(getDiedValue());
        m_matchData.setComment(m_binding.endgameCommentsInput.getText().toString());
        m_matchData.setEndgameClimbPos(getClimbPos());
        m_matchData.setEndgameClimbLevel(getClimbLevel());
    }

    private void setupDoneButton(boolean bEnable)
    {
        m_binding.endgameDoneButton.setEnabled(bEnable);
        if (bEnable)
        {
            Log.d(TAG, "Enable Done Button");
            m_binding.endgameDoneButton.setVisibility(View.VISIBLE);
            m_binding.endgameDoneButtonDisabled.setVisibility(View.INVISIBLE);
        }
        else
        {
            Log.d(TAG, "Disable Done Button");
            m_binding.endgameDoneButton.setVisibility(View.INVISIBLE);
            m_binding.endgameDoneButtonDisabled.setVisibility(View.VISIBLE);
        }
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
