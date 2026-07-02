package com.frc2135.android.frc_scout;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.frc2135.android.frc_scout.databinding.SetTeamIndexDialogBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

/**
 * Dialog for setting the team index, which is used to autopopulate team numbers during scouting.
 */
public class SetTeamIndexDialog extends DialogFragment
{
    private static final String TAG = "SetTeamIndexDialog";

    private Settings m_settings;
    private SetTeamIndexDialogBinding binding;

    /**
     * Creates a new instance of SetTeamIndexDialog.
     *
     * @return a new SetTeamIndexDialog instance
     */
    public static SetTeamIndexDialog newInstance()
    {
        return new SetTeamIndexDialog();
    }

    private final String[] m_options = {"0 - None", "1 - Red 1", "2 - Red 2", "3 - Red 3", "4 - Blue 1", "5 - Blue 2", "6 - Blue 3"};

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = SetTeamIndexDialogBinding.inflate(inflater);

        m_settings = Settings.get(requireContext());

        setupDropdown();

        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Set Team Index")
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, (d, w) -> saveTeamIndex())
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton("Clear", (d, w) -> {
                    binding.setTeamIndexField.setText(m_options[0], false);
                    saveTeamIndex();
                })
                .create();
    }

    private void setupDropdown()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.set_team_index_dropdown_item, m_options);
        binding.setTeamIndexField.setAdapter(adapter);

        String currentIndex = "0 - None";
        if (m_settings != null)
        {
            currentIndex = m_settings.getTeamIndexStr();
        }

        // Map stored value to display value
        String displayValue = m_options[0]; // Default to None
        for (String option : m_options)
        {
            if (option.startsWith(currentIndex))
            {
                displayValue = option;
                break;
            }
        }
        binding.setTeamIndexField.setText(displayValue, false);
    }

    private void saveTeamIndex()
    {
        String selection = Objects.requireNonNull(binding.setTeamIndexField.getText()).toString();
        String indexToSave = "0 - None";

        if (!selection.equals("0 - None"))
        {
            indexToSave = selection.substring(0, 1); // Extract "1", "2", etc.
        }

        if (m_settings != null)
        {
            Log.d(TAG, "Saving team index: " + indexToSave);
            m_settings.setTeamIndexStr(indexToSave);
            MatchListData.get(requireContext()).saveScoutNames();
            getParentFragmentManager().setFragmentResult("team_index_changed", new Bundle());
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
        // Re-set adapter to ensure it displays correctly
        setupDropdown();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        binding = null;
    }
}
