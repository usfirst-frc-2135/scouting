package com.frc2135.android.frc_scout;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.frc2135.android.frc_scout.databinding.TeleopFragmentBinding;

import java.util.Random;

public class TeleopFragment extends Fragment
{
    private static final String TAG = "TeleopFragment";
    private static final int MAX_NUM_HOPPERS = 7;

    private MatchData m_matchData;
    private TeleopFragmentBinding binding;
    private int m_photoNum;

    // Check if pointsTextView field is greater than the MAX_NUM*.
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
        int result = Integer.parseInt(tView.getText().toString());
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
        m_matchData = ((ScoutingActivity) requireActivity()).getCurrentMatch();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null)
        {
            String teamNumber = m_matchData.getTeamNumber();
            String teamAlias = m_matchData.getTeamAlias();
            if (!teamAlias.isEmpty())
            {
                actionBar.setTitle("Teleoperated          Scouting Team " + teamAlias + "         Match " + m_matchData.getMatchNumber());
            }
            else
            {
                actionBar.setTitle("Teleoperated          Scouting Team " + teamNumber + "         Match " + m_matchData.getMatchNumber());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        binding = TeleopFragmentBinding.inflate(inflater, parent, false);

        binding.hopperUsedTotal.setText(String.valueOf(m_matchData.getHoppersUsed()));
        binding.hopperUsedDecrButton.setOnClickListener(v -> updateTotalsInt(binding.hopperUsedTotal, false));
        binding.hopperUsedIncrButton.setOnClickListener(v -> updateTotalsInt(binding.hopperUsedTotal, true));

        binding.intakeAndShoot.setChecked(m_matchData.getIntakeAndShoot());

        int accValue = m_matchData.getAccuracyRate();
        if (accValue == 0)
        {
            binding.accuracyNA.setChecked(true);
        }
        else if (accValue == 1)
        {
            binding.accuracyMost.setChecked(true);
        }
        else if (accValue == 2)
        {
            binding.accuracyThreeFourths.setChecked(true);
        }
        else if (accValue == 3)
        {
            binding.accuracyHalf.setChecked(true);
        }
        else if (accValue == 4)
        {
            binding.accuracyQuarter.setChecked(true);
        }
        else if (accValue == 5)
        {
            binding.accuracyFew.setChecked(true);
        }
        else if (accValue == 6)
        {
            binding.accuracyNone.setChecked(true);
        }

        int passValue = m_matchData.getPassingEffectivenessRate();
        if (passValue == 0)
        {
            binding.passingRateNa.setChecked(true);
        }
        else if (passValue == 4)
        {
            binding.passingTons.setChecked(true);
        }
        else if (passValue == 3)
        {
            binding.passingLarge.setChecked(true);
        }
        else if (passValue == 2)
        {
            binding.passingMedium.setChecked(true);
        }
        else if (passValue == 1)
        {
            binding.passingLow.setChecked(true);
        }

        binding.shovelFuel.setChecked(m_matchData.getShovelFuel());

        int defValue = m_matchData.getDefenseRate();
        if (defValue == 0)
        {
            binding.defenseNone.setChecked(true);
        }
        else if (defValue == 1)
        {
            binding.defenseLow.setChecked(true);
        }
        else if (defValue == 2)
        {
            binding.defenseMediumLow.setChecked(true);
        }
        else if (defValue == 3)
        {
            binding.defenseMedium.setChecked(true);
        }
        else if (defValue == 4)
        {
            binding.defenseMediumHigh.setChecked(true);
        }
        else if (defValue == 5)
        {
            binding.defenseHigh.setChecked(true);
        }

        int nzValue = m_matchData.getPassNeutralZone();
        if (nzValue == 0)
        {
            binding.noNz.setChecked(true);
        }
        else if (nzValue == 1)
        {
            binding.yesNz.setChecked(true);
        }

        int azValue = m_matchData.getPassAllianceZone();
        if (azValue == 0)
        {
            binding.noAz.setChecked(true);
        }
        else if (azValue == 1)
        {
            binding.yesAz.setChecked(true);
        }

        int driveValue = m_matchData.getDriverAbility();
        if (driveValue == 0)
        {
            binding.drivingNa.setChecked(true);
        }
        else if (driveValue == 1)
        {
            binding.drivingSlow.setChecked(true);
        }
        else if (driveValue == 2)
        {
            binding.drivingJerky.setChecked(true);
        }
        else if (driveValue == 3)
        {
            binding.drivingAvg.setChecked(true);
        }
        else if (driveValue == 4)
        {
            binding.drivingFast.setChecked(true);
        }
        else if (driveValue == 5)
        {
            binding.drivingElite.setChecked(true);
        }

        if (isGreaterThanMax(binding.hopperUsedTotal))
        {
            binding.hopperUsedTotal.setTextColor(Color.RED);
        }

        m_photoNum = m_matchData.getTeleopPhoto();
        Log.i(TAG, "onCreateView: from matchData, image= " + m_photoNum);

        ViewGroup.LayoutParams params = binding.photo.getLayoutParams();
        if (m_photoNum == 0)
        {
            Random random = new Random();
            double rand = random.nextDouble();
            Log.i(TAG, "onCreateView: from matchData, random = " + rand);

            if (rand < 0.03)
            {
                m_photoNum = 3;
                binding.photo.setBackgroundResource(R.drawable.me_and_charlotte);
            }
            else if (rand < 0.05)
            {
                m_photoNum = 5;
                binding.photo.setBackgroundResource(R.drawable.me_and_charlotte_2);
            }
            else if (rand < 0.3)
            {
                m_photoNum = 4;
                binding.photo.setBackgroundResource(R.drawable.frc_logo);
                params.width = 450;
                binding.photo.setLayoutParams(params);
            }
            else if (rand < 0.4)
            {
                m_photoNum = 2;
                binding.photo.setBackgroundResource(R.drawable.rebuilt_logo);
                params.width = 250;
                binding.photo.setLayoutParams(params);
            }
            else if (rand < 0.5)
            {
                m_photoNum = 6;
                binding.photo.setBackgroundResource(R.drawable.t2135_logo2);
                params.width = 260;
                binding.photo.setLayoutParams(params);
            }
            else
            {
                m_photoNum = 1;
                binding.photo.setBackgroundResource(R.drawable.rebuilt_fuel);
                params.width = 250;
                binding.photo.setLayoutParams(params);

                if (random.nextDouble() < 0.1)
                {
                    m_photoNum = 7;
                    binding.photo.setBackgroundResource(R.drawable.rebuilt_fuel_shiny);
                    params.width = 250;
                    binding.photo.setLayoutParams(params);
                }
            }
            m_matchData.setTeleopPhoto(m_photoNum);
        }
        else
        {
            int resId = 0;
            int width = -1;
            switch (m_photoNum)
            {
                case 1:
                    resId = R.drawable.rebuilt_fuel;
                    width = 250;
                    break;
                case 2:
                    resId = R.drawable.rebuilt_logo;
                    width = 250;
                    break;
                case 3:
                    resId = R.drawable.me_and_charlotte;
                    break;
                case 4:
                    resId = R.drawable.frc_logo;
                    width = 450;
                    break;
                case 5:
                    resId = R.drawable.me_and_charlotte_2;
                    break;
                case 6:
                    resId = R.drawable.t2135_logo2;
                    width = 260;
                    break;
                case 7:
                    resId = R.drawable.rebuilt_fuel_shiny;
                    width = 250;
                    break;
            }
            if (resId != 0)
            {
                binding.photo.setBackgroundResource(resId);
            }
            if (width != -1)
            {
                params.width = width;
                binding.photo.setLayoutParams(params);
            }
        }
        return binding.getRoot();
    }

    public int getCurrentAccuracyLevel()
    {
        // Returns the integer accuracy level that is currently checked in the radio buttons
        int id = binding.accuracyButtons.getCheckedRadioButtonId();
        if (id == R.id.accuracy_NA)
        {
            return 0;
        }
        if (id == R.id.accuracy_most)
        {
            return 1;
        }
        if (id == R.id.accuracy_three_fourths)
        {
            return 2;
        }
        if (id == R.id.accuracy_half)
        {
            return 3;
        }
        if (id == R.id.accuracy_quarter)
        {
            return 4;
        }
        if (id == R.id.accuracy_few)
        {
            return 5;
        }
        if (id == R.id.accuracy_none)
        {
            return 6;
        }
        return 0;
    }

    public int getPassingEffectivenessRate()
    {
        // Returns the integer climb level that is currently checked in the radio buttons
        int id = binding.passingEffectivenessButtons.getCheckedRadioButtonId();
        if (id == R.id.passing_rate_na)
        {
            return 0;
        }
        if (id == R.id.passing_low)
        {
            return 1;
        }
        if (id == R.id.passing_medium)
        {
            return 2;
        }
        if (id == R.id.passing_large)
        {
            return 3;
        }
        if (id == R.id.passing_tons)
        {
            return 4;
        }
        return 5;
    }

    public int getCurrentDefenseLevel()
    {
        // Returns the integer climb level that is currently checked in the radio buttons
        int id = binding.defenseButtons.getCheckedRadioButtonId();
        if (id == R.id.defense_none)
        {
            return 0;
        }
        if (id == R.id.defense_low)
        {
            return 1;
        }
        if (id == R.id.defense_medium_low)
        {
            return 2;
        }
        if (id == R.id.defense_medium)
        {
            return 3;
        }
        if (id == R.id.defense_medium_high)
        {
            return 4;
        }
        if (id == R.id.defense_high)
        {
            return 5;
        }
        return 0;
    }

    public int getPassNeutralZone()
    {
        // Returns the integer climb level that is currently checked in the radio buttons
        int id = binding.passNz.getCheckedRadioButtonId();
        if (id == R.id.no_nz)
        {
            return 0;
        }
        if (id == R.id.yes_nz)
        {
            return 1;
        }
        return 3;
    }

    public int getPassAllianceZone()
    {
        // Returns the integer climb level that is currently checked in the radio buttons
        int id = binding.passAz.getCheckedRadioButtonId();
        if (id == R.id.no_az)
        {
            return 0;
        }
        if (id == R.id.yes_az)
        {
            return 1;
        }
        return 3;
    }

    public int getDriverAbility()
    {
        // Returns the integer climb level that is currently checked in the radio buttons
        int id = binding.drivingButtons.getCheckedRadioButtonId();
        if (id == R.id.driving_na)
        {
            return 0;
        }
        if (id == R.id.driving_slow)
        {
            return 1;
        }
        if (id == R.id.driving_jerky)
        {
            return 2;
        }
        if (id == R.id.driving_avg)
        {
            return 3;
        }
        if (id == R.id.driving_fast)
        {
            return 4;
        }
        if (id == R.id.driving_elite)
        {
            return 5;
        }
        return 6;
    }

    public void updateTeleopData()
    {
        m_matchData.setHoppersUsed(Integer.parseInt(binding.hopperUsedTotal.getText().toString()));
        m_matchData.setAccuracyRate(getCurrentAccuracyLevel());
        m_matchData.setPassingRate(getPassingEffectivenessRate());
        m_matchData.setTeleopPhoto(m_photoNum);
        m_matchData.setDefenseRate(getCurrentDefenseLevel());
        m_matchData.setIntakeAndShoot(binding.intakeAndShoot.isChecked());
        m_matchData.setPassNeutralZone(getPassNeutralZone());
        m_matchData.setPassAllianceZone(getPassAllianceZone());
        m_matchData.setShovelFuel(binding.shovelFuel.isChecked());
        m_matchData.setDriveAbility(getDriverAbility());
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
