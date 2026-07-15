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
            R.id.teleop_accuracy_na,
            R.id.teleop_accuracy_most,
            R.id.teleop_accuracy_three_fourths,
            R.id.teleop_accuracy_half,
            R.id.teleop_accuracy_quarter,
            R.id.teleop_accuracy_few,
            R.id.teleop_accuracy_none
    };

    private static final int[] PASSING_RATE_IDS = {
            R.id.teleop_passing_rate_na,
            R.id.teleop_passing_rate_low,
            R.id.teleop_passing_rate_medium,
            R.id.teleop_passing_rate_large,
            R.id.teleop_passing_rate_tons
    };

    private static final int[] DEFENSE_RATE_IDS = {
            R.id.teleop_defense_na,
            R.id.teleop_defense_low,
            R.id.teleop_defense_med_low,
            R.id.teleop_defense_medium,
            R.id.teleop_defense_med_high,
            R.id.teleop_defense_high
    };

    private static final int[] PASS_NZ_IDS = {
            R.id.teleop_pass_nz_no,
            R.id.teleop_pass_nz_yes
    };

    private static final int[] PASS_AZ_IDS = {
            R.id.teleop_pass_az_no,
            R.id.teleop_pass_az_yes
    };

    private static final int[] DRIVING_ABILITY_IDS = {
            R.id.teleop_driving_ability_na,
            R.id.teleop_driving_ability_slow,
            R.id.teleop_driving_ability_jerky,
            R.id.teleop_driving_ability_avg,
            R.id.teleop_driving_ability_fast,
            R.id.teleop_driving_ability_elite
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
        loadMatchData();
        setupListeners();
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
        if (m_matchData == null)
        {
            return;
        }

        m_binding.teleopHopperTotalText.setText(String.valueOf(m_matchData.getHoppersUsed()));
        updateScoreColor(m_binding.teleopHopperTotalText);

        m_binding.teleopIntakeShootCheckbox.setChecked(m_matchData.getIntakeAndShoot());
        m_binding.teleopHerdedFuelCheckbox.setChecked(m_matchData.getShovelFuel());

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
        m_binding.teleopHopperDecrButton.setOnClickListener(v -> updateTotalsInt(m_binding.teleopHopperTotalText, false));
        m_binding.teleopHopperIncrButton.setOnClickListener(v -> updateTotalsInt(m_binding.teleopHopperTotalText, true));
    }

    private void initAccuracyRate(int value)
    {
        if (value >= 0 && value < ACCURACY_IDS.length)
        {
            m_binding.teleopAccuracyRadioGroup.check(ACCURACY_IDS[value]);
        }
    }

    private void initPassingRate(int value)
    {
        if (value >= 0 && value < PASSING_RATE_IDS.length)
        {
            m_binding.teleopPassingRateRadioGroup.check(PASSING_RATE_IDS[value]);
        }
    }

    private void initDefenseRate(int value)
    {
        if (value >= 0 && value < DEFENSE_RATE_IDS.length)
        {
            m_binding.teleopDefenseRadioGroup.check(DEFENSE_RATE_IDS[value]);
        }
    }

    private void initPassNz(int value)
    {
        if (value >= 0 && value < PASS_NZ_IDS.length)
        {
            m_binding.teleopPassNzRadioGroup.check(PASS_NZ_IDS[value]);
        }
    }

    private void initPassAz(int value)
    {
        if (value >= 0 && value < PASS_AZ_IDS.length)
        {
            m_binding.teleopPassAzRadioGroup.check(PASS_AZ_IDS[value]);
        }
    }

    private void initDriverAbility(int value)
    {
        if (value >= 0 && value < DRIVING_ABILITY_IDS.length)
        {
            m_binding.teleopDrivingAbilityRadioGroup.check(DRIVING_ABILITY_IDS[value]);
        }
    }

    private void setupPhoto()
    {
        m_photoNum = m_matchData.getTeleopPhoto();
        Log.i(TAG, "setupPhoto: image= " + m_photoNum);

        ViewGroup.LayoutParams params = m_binding.teleopPhotoContainer.getLayoutParams();
        if (m_photoNum == 0)
        {
            Random random = new Random();
            double rand = random.nextDouble();
            if (rand < 0.03)
            {
                m_photoNum = 3;
                m_binding.teleopPhotoContainer.setBackgroundResource(R.drawable.me_and_charlotte);
            }
            else if (rand < 0.05)
            {
                m_photoNum = 5;
                m_binding.teleopPhotoContainer.setBackgroundResource(R.drawable.me_and_charlotte_2);
            }
            else if (rand < 0.3)
            {
                m_photoNum = 4;
                m_binding.teleopPhotoContainer.setBackgroundResource(R.drawable.frc_logo);
                params.width = 450;
                m_binding.teleopPhotoContainer.setLayoutParams(params);
            }
            else if (rand < 0.4)
            {
                m_photoNum = 2;
                m_binding.teleopPhotoContainer.setBackgroundResource(R.drawable.rebuilt_logo);
                params.width = 250;
                m_binding.teleopPhotoContainer.setLayoutParams(params);
            }
            else if (rand < 0.5)
            {
                m_photoNum = 6;
                m_binding.teleopPhotoContainer.setBackgroundResource(R.drawable.t2135_logo_square);
                params.width = 260;
                m_binding.teleopPhotoContainer.setLayoutParams(params);
            }
            else
            {
                m_photoNum = 1;
                m_binding.teleopPhotoContainer.setBackgroundResource(R.drawable.rebuilt_fuel);
                params.width = 250;
                m_binding.teleopPhotoContainer.setLayoutParams(params);

                if (random.nextDouble() < 0.1)
                {
                    m_photoNum = 7;
                    m_binding.teleopPhotoContainer.setBackgroundResource(R.drawable.rebuilt_fuel_shiny);
                    params.width = 250;
                    m_binding.teleopPhotoContainer.setLayoutParams(params);
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
                m_binding.teleopPhotoContainer.setBackgroundResource(resId);
            }
            if (width != -1)
            {
                params.width = width;
                m_binding.teleopPhotoContainer.setLayoutParams(params);
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
        int id = m_binding.teleopAccuracyRadioGroup.getCheckedRadioButtonId();
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
        int id = m_binding.teleopPassingRateRadioGroup.getCheckedRadioButtonId();
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
        int id = m_binding.teleopDefenseRadioGroup.getCheckedRadioButtonId();
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
        int id = m_binding.teleopPassNzRadioGroup.getCheckedRadioButtonId();
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
        int id = m_binding.teleopPassAzRadioGroup.getCheckedRadioButtonId();
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
        int id = m_binding.teleopDrivingAbilityRadioGroup.getCheckedRadioButtonId();
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
            m_matchData.setHoppersUsed(Integer.parseInt(m_binding.teleopHopperTotalText.getText().toString()));
        }
        catch (NumberFormatException e)
        {
            Log.e(TAG, "Invalid hopper score value", e);
        }
        m_matchData.setAccuracyRate(getCurrentAccuracyLevel());
        m_matchData.setPassingRate(getPassingEffectivenessRate());
        m_matchData.setTeleopPhoto(m_photoNum);
        m_matchData.setDefenseRate(getCurrentDefenseLevel());
        m_matchData.setIntakeAndShoot(m_binding.teleopIntakeShootCheckbox.isChecked());
        m_matchData.setPassNeutralZone(getPassNeutralZone());
        m_matchData.setPassAllianceZone(getPassAllianceZone());
        m_matchData.setShovelFuel(m_binding.teleopHerdedFuelCheckbox.isChecked());
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
