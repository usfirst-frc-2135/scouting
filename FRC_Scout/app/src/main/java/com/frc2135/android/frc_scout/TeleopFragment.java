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

import com.frc2135.android.frc_scout.databinding.TeleopFragmentBinding;

import java.util.Random;

/**
 * Fragment for recording teleoperated period scouting data.
 */
public class TeleopFragment extends Fragment
{
    private static final String TAG = "TeleopFragment";

    private static final int[] ACCURACY_IDS = {
            R.id.accuracy_NA,
            R.id.accuracy_most,
            R.id.accuracy_three_fourths,
            R.id.accuracy_half,
            R.id.accuracy_quarter,
            R.id.accuracy_few,
            R.id.accuracy_none
    };

    private static final int[] PASSING_RATE_IDS = {
            R.id.passing_rate_na,
            R.id.passing_low,
            R.id.passing_medium,
            R.id.passing_large,
            R.id.passing_tons
    };

    private static final int[] DEFENSE_RATE_IDS = {
            R.id.defense_none,
            R.id.defense_low,
            R.id.defense_medium_low,
            R.id.defense_medium,
            R.id.defense_medium_high,
            R.id.defense_high
    };

    private static final int[] PASS_NZ_IDS = {
            R.id.no_nz,
            R.id.yes_nz
    };

    private static final int[] PASS_AZ_IDS = {
            R.id.no_az,
            R.id.yes_az
    };

    private static final int[] DRIVING_ABILITY_IDS = {
            R.id.driving_na,
            R.id.driving_slow,
            R.id.driving_jerky,
            R.id.driving_avg,
            R.id.driving_fast,
            R.id.driving_elite
    };

    private MatchData m_matchData;
    private TeleopFragmentBinding m_binding;
    private int m_photoNum;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        m_matchData = ((ScoutingActivity) requireActivity()).getCurrentMatch();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        m_binding = TeleopFragmentBinding.inflate(inflater, parent, false);
        return m_binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        setupActionBar();
        if (m_matchData != null)
        {
            loadMatchData();
            setupListeners();
        }
    }

    private void setupActionBar()
    {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null && m_matchData != null)
        {
            actionBar.setTitle(R.string.teleoperated_title);
        }
    }

    private void loadMatchData()
    {
        m_binding.hopperUsedTotal.setText(String.valueOf(m_matchData.getHoppersUsed()));
        updateScoreColor(m_binding.hopperUsedTotal);

        m_binding.intakeAndShoot.setChecked(m_matchData.getIntakeAndShoot());
        m_binding.shovelFuel.setChecked(m_matchData.getShovelFuel());

        initAccuracyRate(m_matchData.getAccuracyRate());
        initPassingRate(m_matchData.getPassingEffectivenessRate());
        initDefenseRate(m_matchData.getDefenseRate());
        initPassNz(m_matchData.getPassNeutralZone());
        initPassAz(m_matchData.getPassAllianceZone());
        initDriverAbility(m_matchData.getDriverAbility());

        setupPhoto();
    }

    private void setupListeners()
    {
        m_binding.hopperUsedDecrButton.setOnClickListener(v -> updateTotalsInt(m_binding.hopperUsedTotal, false));
        m_binding.hopperUsedIncrButton.setOnClickListener(v -> updateTotalsInt(m_binding.hopperUsedTotal, true));
    }

    private void initAccuracyRate(int value)
    {
        if (value >= 0 && value < ACCURACY_IDS.length)
        {
            m_binding.accuracyButtons.check(ACCURACY_IDS[value]);
        }
    }

    private void initPassingRate(int value)
    {
        if (value >= 0 && value < PASSING_RATE_IDS.length)
        {
            m_binding.passingEffectivenessButtons.check(PASSING_RATE_IDS[value]);
        }
    }

    private void initDefenseRate(int value)
    {
        if (value >= 0 && value < DEFENSE_RATE_IDS.length)
        {
            m_binding.defenseButtons.check(DEFENSE_RATE_IDS[value]);
        }
    }

    private void initPassNz(int value)
    {
        if (value >= 0 && value < PASS_NZ_IDS.length)
        {
            m_binding.passNz.check(PASS_NZ_IDS[value]);
        }
    }

    private void initPassAz(int value)
    {
        if (value >= 0 && value < PASS_AZ_IDS.length)
        {
            m_binding.passAz.check(PASS_AZ_IDS[value]);
        }
    }

    private void initDriverAbility(int value)
    {
        if (value >= 0 && value < DRIVING_ABILITY_IDS.length)
        {
            m_binding.drivingButtons.check(DRIVING_ABILITY_IDS[value]);
        }
    }

    private void setupPhoto()
    {
        m_photoNum = m_matchData.getTeleopPhoto();
        Log.i(TAG, "setupPhoto: image= " + m_photoNum);

        ViewGroup.LayoutParams params = m_binding.photo.getLayoutParams();
        if (m_photoNum == 0)
        {
            Random random = new Random();
            double rand = random.nextDouble();
            if (rand < 0.03)
            {
                m_photoNum = 3;
                m_binding.photo.setBackgroundResource(R.drawable.me_and_charlotte);
            }
            else if (rand < 0.05)
            {
                m_photoNum = 5;
                m_binding.photo.setBackgroundResource(R.drawable.me_and_charlotte_2);
            }
            else if (rand < 0.3)
            {
                m_photoNum = 4;
                m_binding.photo.setBackgroundResource(R.drawable.frc_logo);
                params.width = 450;
                m_binding.photo.setLayoutParams(params);
            }
            else if (rand < 0.4)
            {
                m_photoNum = 2;
                m_binding.photo.setBackgroundResource(R.drawable.rebuilt_logo);
                params.width = 250;
                m_binding.photo.setLayoutParams(params);
            }
            else if (rand < 0.5)
            {
                m_photoNum = 6;
                m_binding.photo.setBackgroundResource(R.drawable.t2135_logo_square);
                params.width = 260;
                m_binding.photo.setLayoutParams(params);
            }
            else
            {
                m_photoNum = 1;
                m_binding.photo.setBackgroundResource(R.drawable.rebuilt_fuel);
                params.width = 250;
                m_binding.photo.setLayoutParams(params);

                if (random.nextDouble() < 0.1)
                {
                    m_photoNum = 7;
                    m_binding.photo.setBackgroundResource(R.drawable.rebuilt_fuel_shiny);
                    params.width = 250;
                    m_binding.photo.setLayoutParams(params);
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
                    resId = R.drawable.t2135_logo_square;
                    width = 260;
                    break;
                case 7:
                    resId = R.drawable.rebuilt_fuel_shiny;
                    width = 250;
                    break;
            }
            if (resId != 0)
            {
                m_binding.photo.setBackgroundResource(resId);
            }
            if (width != -1)
            {
                params.width = width;
                m_binding.photo.setLayoutParams(params);
            }
        }
    }

    private boolean isGreaterThanMax(TextView field)
    {
        try
        {
            int num = Integer.parseInt(field.getText().toString());
            return num > MatchData.MAX_TELEOP_HOPPERS;
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

    public void updateTotalsInt(TextView tView, boolean bIncr)
    {
        try
        {
            int currentVal = Integer.parseInt(tView.getText().toString());
            int newVal = bIncr ? currentVal + 1 : Math.max(0, currentVal - 1);
            tView.setText(String.valueOf(newVal));
            updateScoreColor(tView);
        }
        catch (NumberFormatException e)
        {
            Log.e(TAG, "Invalid number format in updateTotalsInt", e);
        }
    }

    public int getCurrentAccuracyLevel()
    {
        int id = m_binding.accuracyButtons.getCheckedRadioButtonId();
        for (int i = 0; i < ACCURACY_IDS.length; i++)
        {
            if (id == ACCURACY_IDS[i])
            {
                return i;
            }
        }
        return 0;
    }

    public int getPassingEffectivenessRate()
    {
        int id = m_binding.passingEffectivenessButtons.getCheckedRadioButtonId();
        for (int i = 0; i < PASSING_RATE_IDS.length; i++)
        {
            if (id == PASSING_RATE_IDS[i])
            {
                return i;
            }
        }
        return 5;
    }

    public int getCurrentDefenseLevel()
    {
        int id = m_binding.defenseButtons.getCheckedRadioButtonId();
        for (int i = 0; i < DEFENSE_RATE_IDS.length; i++)
        {
            if (id == DEFENSE_RATE_IDS[i])
            {
                return i;
            }
        }
        return 0;
    }

    public int getPassNeutralZone()
    {
        int id = m_binding.passNz.getCheckedRadioButtonId();
        for (int i = 0; i < PASS_NZ_IDS.length; i++)
        {
            if (id == PASS_NZ_IDS[i])
            {
                return i;
            }
        }
        return 3;
    }

    public int getPassAllianceZone()
    {
        int id = m_binding.passAz.getCheckedRadioButtonId();
        for (int i = 0; i < PASS_AZ_IDS.length; i++)
        {
            if (id == PASS_AZ_IDS[i])
            {
                return i;
            }
        }
        return 3;
    }

    public int getDriverAbility()
    {
        int id = m_binding.drivingButtons.getCheckedRadioButtonId();
        for (int i = 0; i < DRIVING_ABILITY_IDS.length; i++)
        {
            if (id == DRIVING_ABILITY_IDS[i])
            {
                return i;
            }
        }
        return 6;
    }

    /**
     * Updates the MatchData object with the latest inputs from this fragment.
     */
    public void updateTeleopData()
    {
        Log.d(TAG, "updateTeleopData()");
        if (m_matchData == null)
        {
            return;
        }
        try
        {
            m_matchData.setHoppersUsed(Integer.parseInt(m_binding.hopperUsedTotal.getText().toString()));
        }
        catch (NumberFormatException e)
        {
            Log.e(TAG, "Invalid hopper score value", e);
        }
        m_matchData.setAccuracyRate(getCurrentAccuracyLevel());
        m_matchData.setPassingRate(getPassingEffectivenessRate());
        m_matchData.setTeleopPhoto(m_photoNum);
        m_matchData.setDefenseRate(getCurrentDefenseLevel());
        m_matchData.setIntakeAndShoot(m_binding.intakeAndShoot.isChecked());
        m_matchData.setPassNeutralZone(getPassNeutralZone());
        m_matchData.setPassAllianceZone(getPassAllianceZone());
        m_matchData.setShovelFuel(m_binding.shovelFuel.isChecked());
        m_matchData.setDriveAbility(getDriverAbility());
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
