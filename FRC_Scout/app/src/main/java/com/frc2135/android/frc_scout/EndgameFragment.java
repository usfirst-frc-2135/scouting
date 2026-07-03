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
    private EndgameFragmentBinding m_binding;

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
        Log.d(TAG, "onCreateView");
        m_binding = EndgameFragmentBinding.inflate(inflater, parent, false);
        return m_binding.getRoot();
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

        m_binding.comments.setText(m_matchData.getComment());
    }

    private void setupListeners()
    {
        m_binding.genQR.setEnabled(true);
        m_binding.genQRDisabled.setVisibility(View.INVISIBLE);
        m_binding.genQR.setOnClickListener(view -> {
            updateEndgameData();
            String validationMsg = m_matchData.validate();
            if (!validationMsg.isEmpty())
            {
                Log.d(TAG, "Validation failed: " + validationMsg);
                Toast.makeText(getContext(), validationMsg, Toast.LENGTH_LONG).show();
            }
            else
            {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                QRDialog dialog = QRDialog.newInstance(m_matchData);
                dialog.show(fm, QRTAG);
                setupDoneButton(true);
            }
        });

        m_binding.navToMenuButton.setOnClickListener(view -> {
            updateEndgameData();
            Log.d(TAG, "Saving latest match and scout names");
            MatchListData matchHistory = MatchListData.getInstance(getActivity());
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
            m_binding.startText.check(id);
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
            m_binding.endgameLevelClimbGroup.check(id);
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
            m_binding.endgameClimbButtons.check(id);
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
            m_binding.diedGroup.check(id);
        }
    }

    private int getDiedValue()
    {
        int id = m_binding.diedGroup.getCheckedRadioButtonId();
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
        int id = m_binding.startText.getCheckedRadioButtonId();
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
        int id = m_binding.endgameClimbButtons.getCheckedRadioButtonId();
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
        int id = m_binding.endgameLevelClimbGroup.getCheckedRadioButtonId();
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
        if (m_matchData == null || m_binding == null)
        {
            return;
        }
        m_matchData.setStartClimb(getStartClimb());
        m_matchData.setDiedValue(getDiedValue());
        m_matchData.setComment(m_binding.comments.getText().toString());
        m_matchData.setEndgameClimbPos(getClimbPos());
        m_matchData.setEndgameClimbLevel(getClimbLevel());
    }

    private void setupDoneButton(boolean bEnable)
    {
        m_binding.navToMenuButton.setEnabled(bEnable);
        m_binding.navToMenuButtonDisabled.setVisibility(bEnable ? View.INVISIBLE : View.VISIBLE);
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
