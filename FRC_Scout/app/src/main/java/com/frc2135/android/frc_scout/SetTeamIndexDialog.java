package com.frc2135.android.frc_scout;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.frc2135.android.frc_scout.databinding.SetTeamIndexDialogBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

/**
 * Dialog for setting the team index (scouting position), which is used to autopopulate
 * team numbers during pre-match scouting based on official event schedules.
 */
public class SetTeamIndexDialog extends DialogFragment
{
    private static final String TAG = "SetTeamIndexDialog";

    private Settings m_settings;
    private SetTeamIndexDialogBinding m_binding;

    private String[] m_indexOptions;

    /**
     * Creates a new instance of {@link SetTeamIndexDialog}.
     *
     * @return a new SetTeamIndexDialog instance
     */
    public static SetTeamIndexDialog newInstance()
    {
        return new SetTeamIndexDialog();
    }

    /**
     * Constructs the {@link androidx.appcompat.app.AlertDialog} instance, initializes View Binding,
     * and sets up the team index dropdown menu and listeners.
     *
     * @param savedInstanceState if the dialog is being re-initialized from a previous saved state
     * @return the constructed {@link Dialog}
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

        LayoutInflater inflater = getLayoutInflater();
        m_binding = SetTeamIndexDialogBinding.inflate(inflater);

        m_settings = Settings.getInstance(requireContext());
        m_indexOptions = m_settings.getTeamIndexOptions();

        setupTeamIndexDropdown();

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.set_team_index_title)
                .setView(m_binding.getRoot())
                .setPositiveButton(android.R.string.ok, (d, w) -> saveTeamIndex())
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton(R.string.clear_team_index, (d, w) -> {
                    m_settings.clearTeamIndexStr();
                    m_binding.setTeamIndexInput.setText(m_settings.getTeamIndexStr(), false);
                    getParentFragmentManager().setFragmentResult("team_index_changed", new Bundle());
                })
                .create();

        m_binding.setTeamIndexInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
            {
                Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (okButton != null)
                {
                    okButton.setFocusableInTouchMode(true);
                    okButton.requestFocus();
                }
                return true;
            }
            return false;
        });

        m_binding.setTeamIndexInput.setOnItemClickListener((parent, view, position, id) -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (okButton != null)
            {
                okButton.requestFocus();
            }
        });

        return dialog;
    }

    /**
     * Initializes the dropdown menu with the team index options.
     */
    private void setupTeamIndexDropdown()
    {
        Log.d(TAG, "setupDropdown()");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.set_team_index_dropdown_item, m_indexOptions);
        m_binding.setTeamIndexInput.setAdapter(adapter);

        String currentIndex = (m_settings != null) ? m_settings.getTeamIndexStr() : "unknown";
        m_binding.setTeamIndexInput.setText(currentIndex, false);
    }

    /**
     * Persists the selected team index to settings and the local filesystem.
     */
    private void saveTeamIndex()
    {
        Log.d(TAG, "saveTeamIndex()");
        String selectedIndex = Objects.requireNonNull(m_binding.setTeamIndexInput.getText()).toString();

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
