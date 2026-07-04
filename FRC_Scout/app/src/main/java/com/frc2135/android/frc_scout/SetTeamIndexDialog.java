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
    private SetTeamIndexDialogBinding m_binding;

    private String[] m_indexOptions;

    /**
     * Creates a new instance of SetTeamIndexDialog.
     *
     * @return a new SetTeamIndexDialog instance
     */
    public static SetTeamIndexDialog newInstance()
    {
        return new SetTeamIndexDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        m_binding = SetTeamIndexDialogBinding.inflate(inflater);

        m_settings = Settings.getInstance(requireContext());
        m_indexOptions = m_settings.getTeamIndexOptions();

        setupTeamIndexDropdown();

        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Set Team Index")
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, (d, w) -> saveTeamIndex())
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton(R.string.clear_index, (d, w) -> {
                    m_settings.clearTeamIndexStr();
                    m_binding.setTeamIndexField.setText(m_settings.getTeamIndexStr(), false);
                    getParentFragmentManager().setFragmentResult("team_index_changed", new Bundle());
                })
                .create();
    }

    /**
     * Initializes the dropdown menu with the team index options.
     */
    private void setupTeamIndexDropdown()
    {
        Log.d(TAG, "setupDropdown()");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.set_team_index_dropdown_item, m_indexOptions);
        m_binding.setTeamIndexField.setAdapter(adapter);

        String currentIndex = (m_settings != null) ? m_settings.getTeamIndexStr() : "unknown";
        m_binding.setTeamIndexField.setText(currentIndex, false);
    }

    /**
     * Persists the selected team index to settings and the local filesystem.
     */
    private void saveTeamIndex()
    {
        Log.d(TAG, "saveTeamIndex()");
        String selectedIndex = Objects.requireNonNull(m_binding.setTeamIndexField.getText()).toString();

        if (m_settings != null)
        {
            Log.d(TAG, "Saving team index: " + selectedIndex);
            m_settings.setTeamIndexStr(selectedIndex);
            getParentFragmentManager().setFragmentResult("team_index_changed", new Bundle());
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
        // Re-set adapter to ensure it displays correctly
        setupTeamIndexDropdown();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        m_binding = null;
    }
}
