package com.frc2135.android.frc_scout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.frc2135.android.frc_scout.databinding.EndgameFragmentBinding;

public class EndgameFragment extends Fragment
{
    private static final String TAG = "EndgameFragment";
    public static final String QRTAG = "qr";

    private MatchData m_matchData;
    private EndgameFragmentBinding binding;

    private void setupDoneButton(boolean bEnable)
    {
        if (bEnable)
        {
            Log.d(TAG, "--> ! Enabling DONE");
            binding.navToMenuButton.setEnabled(true);
            binding.navToMenuButtonDisabled.setVisibility(View.INVISIBLE);
        }
        else
        {
            Log.d(TAG, "--> ! Disabling DONE ");
            binding.navToMenuButton.setEnabled(false);
            binding.navToMenuButtonDisabled.setVisibility(View.VISIBLE);
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
                String teamNumber = m_matchData.getTeamNumber();
                String teamAlias = m_matchData.getTeamAlias();
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null)
                {
                    if (!teamAlias.isEmpty())
                    {
                        actionBar.setTitle("Endgame               Scouting Team " + teamAlias + "          Match " + m_matchData.getMatchNumber());
                    }
                    else
                    {
                        actionBar.setTitle("Endgame               Scouting Team " + teamNumber + "          Match " + m_matchData.getMatchNumber());
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        //Creates a view using the specific fragment layout.
        //Connects the checkbox for if the robot dies and sets up a listener to detect when the checked status is changed
        //HOLD        m_diedCheckbox = v.findViewById(R.id.died_checkbox_true);
        //HOLD        m_diedCheckbox.setChecked(m_matchData.getDied());// Default is unchecked
        //HOLD        m_diedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        //HOLD        {
        //HOLD            @Override
        //HOLD            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        //HOLD            {
        //HOLD                updateEndgameData();
        //HOLD            }
        //HOLD        });
        binding = EndgameFragmentBinding.inflate(inflater, parent, false);

        int startClimb = m_matchData.getStartClimb();
        if (startClimb == 0)
        {
            binding.startNone.setChecked(true);
        }
        else if (startClimb == 1)
        {
            binding.startBefore.setChecked(true);
        }
        else if (startClimb == 2)
        {
            binding.startBell.setChecked(true);
        }
        else if (startClimb == 3)
        {
            binding.startTen.setChecked(true);
        }
        else if (startClimb == 4)
        {
            binding.startLess.setChecked(true);
        }

        int climbLevel = m_matchData.getEndgameClimbLevel();
        if (climbLevel == 0)
        {
            binding.endgameLevelNa.setChecked(true);
        }
        else if (climbLevel == 1)
        {
            binding.endgameLevelOne.setChecked(true);
        }
        else if (climbLevel == 2)
        {
            binding.endgameLevelTwo.setChecked(true);
        }
        else if (climbLevel == 3)
        {
            binding.endgameLevelThree.setChecked(true);
        }

        int climbPos = m_matchData.getEndgameClimbPos();
        if (climbPos == 0)
        {
            binding.endgameClimbNa.setChecked(true);
        }
        else if (climbPos == 1)
        {
            binding.endgameClimbBack.setChecked(true);
        }
        else if (climbPos == 2)
        {
            binding.endgameClimbLeft.setChecked(true);
        }
        else if (climbPos == 3)
        {
            binding.endgameClimbFront.setChecked(true);
        }
        else if (climbPos == 4)
        {
            binding.endgameClimbRight.setChecked(true);
        }

        int diedValue = m_matchData.getDiedValue();
        if (diedValue == 0)
        {
            binding.diedNone.setChecked(true);
        }
        else if (diedValue == 1)
        {
            binding.diedMost.setChecked(true);
        }
        else if (diedValue == 2)
        {
            binding.diedMin.setChecked(true);
        }
        else if (diedValue == 3)
        {
            binding.diedThirty.setChecked(true);
        }
        else if (diedValue == 4)
        {
            binding.diedTt.setChecked(true);
        }
        else if (diedValue == 5)
        //Sets up an EditText that allows users to input any additional comments
        {
            //Setting an onClickListener makes it so that our button actually senses for when it is clicked, and when it is clicked, it will proceed with onClick()
            binding.noShow.setChecked(true);
        }

        binding.comments.setHint("Enter comments here");
        binding.comments.setText(m_matchData.getComment());

        binding.genQR.setEnabled(true);
        binding.genQRDisabled.setVisibility(View.INVISIBLE);
        binding.genQR.setOnClickListener(view -> {
            updateEndgameData();
            Log.d(TAG, "Clicked on QR Code");
            StringBuilder msg = new StringBuilder();
            boolean bError = false;
            int passNZ = m_matchData.getPassNeutralZone();
            int passAZ = m_matchData.getPassAllianceZone();
            if (passNZ == 3 || passAZ == 3)
            {
                if (passNZ == 3 && passAZ == 3)
                {
                    msg.append("Teleop: Passing From Neutral Zone and Alliance Zone buttons must be set!\n");
                }
                else if (passNZ == 3)
                {
                    msg.append("Teleop: Passing From Neutral Zone button must be set\n");
                }
                else
                {
                    msg.append("Teleop: Passing From Alliance Zone  button must be set\n");
                }
                bError = true;
            }
            // Check climb selections
            int sc = m_matchData.getStartClimb();
            int cl = m_matchData.getEndgameClimbLevel();
            int cp = m_matchData.getEndgameClimbPos();
            if (sc == 0 && (cl != 0 || cp != 0) ||
                    cl == 0 && sc != 0 ||
                    cp == 0 && sc != 0)
            {
                msg.append("\nEndgame: Start Climb, Climb Level and Climb Position settings don't match!\n");
                bError = true;
            }
            // check driverAbility
            if (m_matchData.getDriverAbility() == 6)
            {
                msg.append("\nTeleop: Driver ability not set!\n");
                bError = true;
            }
            //check passing rate
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
                bError = true;
            }

            if (bError)
            {
                Log.d(TAG, msg.toString());
                Toast.makeText(getContext(), msg.toString(), Toast.LENGTH_LONG).show();
            }
            else
            {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                QRFragment dialog = QRFragment.newInstance(m_matchData);
                dialog.show(fm, QRTAG);
                setupDoneButton(true);
            }
        });

        // Save the latest Scouter and MatchData JSON files.
        binding.navToMenuButton.setOnClickListener(view -> {
            updateEndgameData();
            Log.d(TAG, "EndgameFragment DONE onClick() saving latest match and Scouter files\n");
            MatchHistory matchHistory = MatchHistory.get(getActivity());
            if (!matchHistory.saveScouterData())
            {
                Log.d(TAG, "ERROR - unable to save Scouter Data!");
                // TODO - issue a toast msg here??
            }
            if (!matchHistory.saveMatchData(m_matchData))
            {
                Log.d(TAG, "ERROR - unable to save Match Data!");
                // TODO - issue a toast msg here??
            }
            // Go back to MatchListActivity page
            Intent i = new Intent(getActivity(), MatchListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            requireActivity().finish();
        });

        setupDoneButton(false);
        return binding.getRoot();
    }

    public int getDiedValue()
    {
        // Returns the current value for the Died radio buttons
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

    public int getCurrentStartClimbing()
    {
        // Returns the integer climb level that is currently checked in the radio buttons
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

    public int getEndgameClimbPos()
    {
        // Returns the integer climb level that is currently checked in the radio buttons
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

    public int getEndgameClimbLevel()
    {
        // Returns the integer climb level that is currently checked in the radio buttons
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
        m_matchData.setStartClimb(getCurrentStartClimbing());
        m_matchData.setDiedValue(getDiedValue());
        m_matchData.setComment(binding.comments.getText().toString());
        m_matchData.setEndgameClimbPos(getEndgameClimbPos());
        m_matchData.setEndgameClimbLevel(getEndgameClimbLevel());
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
