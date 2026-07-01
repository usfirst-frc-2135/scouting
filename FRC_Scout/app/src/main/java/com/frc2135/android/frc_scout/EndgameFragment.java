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

    private MatchData m_matchData;
    private EndgameFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        ScoutingActivity activity = (ScoutingActivity) getActivity();
        if (activity != null)
        {
            m_matchData = activity.getCurrentMatch();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        binding = EndgameFragmentBinding.inflate(inflater, parent, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreate");
        setupActionBar();
        loadMatchData();
        setupListeners();
        setupDoneButton(false);
    }

    private void setupActionBar()
    {
        ScoutingActivity activity = (ScoutingActivity) getActivity();
        if (activity == null || m_matchData == null)
        {
            return;
        }

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle("Endgame");
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

        binding.comments.setHint("Enter comments here");
        binding.comments.setText(m_matchData.getComment());
    }

    private void setupListeners()
    {
        binding.genQR.setEnabled(true);
        binding.genQRDisabled.setVisibility(View.INVISIBLE);
        binding.genQR.setOnClickListener(view -> {
            updateEndgameData();
            String validationMsg = validateEndgameData();
            if (!validationMsg.isEmpty())
            {
                Log.d(TAG, "Validation failed: " + validationMsg);
                Toast.makeText(getContext(), validationMsg, Toast.LENGTH_LONG).show();
            }
            else
            {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                QRFragment dialog = QRFragment.newInstance(m_matchData);
                dialog.show(fm, QRTAG);
                setupDoneButton(true);
            }
        });

        binding.navToMenuButton.setOnClickListener(view -> {
            updateEndgameData();
            Log.d(TAG, "Saving latest match and scout names");
            MatchListData matchHistory = MatchListData.get(getActivity());
            if (!matchHistory.saveScoutNames())
            {
                Log.e(TAG, "Failed to save scout names!");
            }
            if (!matchHistory.saveMatchData(m_matchData))
            {
                Log.e(TAG, "Failed to save Match Data!");
                Toast.makeText(getContext(), "Error: Failed to save match data!", Toast.LENGTH_SHORT).show();
            }

            Intent i = new Intent(getActivity(), MatchListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            requireActivity().finish();
        });
    }

    private void initStartClimbing(int value)
    {
        int id = switch (value)
        {
            case 0 -> R.id.start_none;
            case 1 -> R.id.start_before;
            case 2 -> R.id.start_bell;
            case 3 -> R.id.start_ten;
            case 4 -> R.id.start_less;
            default -> -1;
        };
        if (id != -1)
        {
            binding.startText.check(id);
        }
    }

    private void initClimbLevel(int value)
    {
        int id = switch (value)
        {
            case 0 -> R.id.endgame_level_na;
            case 1 -> R.id.endgame_level_one;
            case 2 -> R.id.endgame_level_two;
            case 3 -> R.id.endgame_level_three;
            default -> -1;
        };
        if (id != -1)
        {
            binding.endgameLevelClimbGroup.check(id);
        }
    }

    private void initClimbPos(int value)
    {
        int id = switch (value)
        {
            case 0 -> R.id.endgame_climb_na;
            case 1 -> R.id.endgame_climb_back;
            case 2 -> R.id.endgame_climb_left;
            case 3 -> R.id.endgame_climb_front;
            case 4 -> R.id.endgame_climb_right;
            default -> -1;
        };
        if (id != -1)
        {
            binding.endgameClimbButtons.check(id);
        }
    }

    private void initDiedValue(int value)
    {
        int id = switch (value)
        {
            case 0 -> R.id.died_none;
            case 1 -> R.id.died_most;
            case 2 -> R.id.died_min;
            case 3 -> R.id.died_thirty;
            case 4 -> R.id.died_tt;
            case 5 -> R.id.no_show;
            default -> -1;
        };
        if (id != -1)
        {
            binding.diedGroup.check(id);
        }
    }

    private String validateEndgameData()
    {
        StringBuilder msg = new StringBuilder();

        // Validation logic for teleop fields (stored in m_matchData)
        int passNZ = m_matchData.getPassNeutralZone();
        //noinspection ExtractMethodRecommender
        int passAZ = m_matchData.getPassAllianceZone();
        if (passNZ == 3 || passAZ == 3)
        {
            if (passNZ == 3 && passAZ == 3)
            {
                msg.append("Teleop: Passing From Neutral/Alliance Zone buttons must be set!\n");
            }
            else if (passNZ == 3)
            {
                msg.append("Teleop: Passing From Neutral Zone button must be set\n");
            }
            else
            {
                msg.append("Teleop: Passing From Alliance Zone button must be set\n");
            }
        }

        // Validate climb selections
        int sc = m_matchData.getStartClimb();
        int cl = m_matchData.getEndgameClimbLevel();
        int cp = m_matchData.getEndgameClimbPos();
        if ((sc == 0 && (cl != 0 || cp != 0)) || (cl == 0 && sc != 0) || (cp == 0 && sc != 0))
        {
            msg.append("\nEndgame: Start Climb, Climb Level and Climb Position settings don't match!\n");
        }

        // Validate driver ability
        if (m_matchData.getDriverAbility() == 6)
        {
            msg.append("\nTeleop: Driver ability not set!\n");
        }

        // Validate passing rate
        int passRate = m_matchData.getPassingEffectivenessRate();
        if (((passNZ == 1 || passAZ == 1) && passRate == 0) || (passRate == 5) || ((passNZ == 0 && passAZ == 0) && passRate > 0))
        {
            if ((passNZ == 1 || passAZ == 1) && passRate == 0)
            {
                msg.append("\nTeleop: Passed from zone set, Passing rate not set!\n");
            }
            else if (passRate == 5)
            {
                msg.append("\nTeleop: Passing rate not set!\n");
            }
            else
            {
                msg.append("\nTeleop: Passing rate set, Passed from zone not set!\n");
            }
        }

        return msg.toString();
    }

    private int getDiedValue()
    {
        int id = binding.diedGroup.getCheckedRadioButtonId();
        if (id == R.id.died_none)
        {
            return 0;
        }
        if (id == R.id.died_most)
        {
            return 1;
        }
        if (id == R.id.died_min)
        {
            return 2;
        }
        if (id == R.id.died_thirty)
        {
            return 3;
        }
        if (id == R.id.died_tt)
        {
            return 4;
        }
        if (id == R.id.no_show)
        {
            return 5;
        }
        return 0;
    }

    private int getStartClimb()
    {
        int id = binding.startText.getCheckedRadioButtonId();
        if (id == R.id.start_none)
        {
            return 0;
        }
        if (id == R.id.start_before)
        {
            return 1;
        }
        if (id == R.id.start_bell)
        {
            return 2;
        }
        if (id == R.id.start_ten)
        {
            return 3;
        }
        if (id == R.id.start_less)
        {
            return 4;
        }
        return 0;
    }

    private int getClimbPos()
    {
        int id = binding.endgameClimbButtons.getCheckedRadioButtonId();
        if (id == R.id.endgame_climb_na)
        {
            return 0;
        }
        if (id == R.id.endgame_climb_back)
        {
            return 1;
        }
        if (id == R.id.endgame_climb_left)
        {
            return 2;
        }
        if (id == R.id.endgame_climb_front)
        {
            return 3;
        }
        if (id == R.id.endgame_climb_right)
        {
            return 4;
        }
        return 0;
    }

    private int getClimbLevel()
    {
        int id = binding.endgameLevelClimbGroup.getCheckedRadioButtonId();
        if (id == R.id.endgame_level_na)
        {
            return 0;
        }
        if (id == R.id.endgame_level_one)
        {
            return 1;
        }
        if (id == R.id.endgame_level_two)
        {
            return 2;
        }
        if (id == R.id.endgame_level_three)
        {
            return 3;
        }
        return 0;
    }

    public void updateEndgameData()
    {
        if (m_matchData == null || binding == null)
        {
            return;
        }
        m_matchData.setStartClimb(getStartClimb());
        m_matchData.setDiedValue(getDiedValue());
        m_matchData.setComment(binding.comments.getText().toString());
        m_matchData.setEndgameClimbPos(getClimbPos());
        m_matchData.setEndgameClimbLevel(getClimbLevel());
    }

    private void setupDoneButton(boolean bEnable)
    {
        binding.navToMenuButton.setEnabled(bEnable);
        binding.navToMenuButtonDisabled.setVisibility(bEnable ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        binding = null;
    }
}
