package com.frc2135.android.frc_scout;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = SetTeamIndexDialogBinding.inflate(inflater);

        m_settings = Settings.get(requireContext());

        setupViewDefaults();
        setupListeners();

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Set Team Index")
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, (d, w) -> saveTeamIndex())
                .setNeutralButton(android.R.string.cancel, (d, w) -> dismiss())
                .create();

        return dialog;
    }

    private void setupViewDefaults()
    {
        binding.setTeamIndexField.setHint("Enter 1-6 or 'None'");

        String currentIndex = "None";
        if (m_settings != null)
        {
            String indexStr = m_settings.getTeamIndexStr();
            if (m_settings.isValidTeamIndexStr(indexStr))
            {
                currentIndex = indexStr;
            }
        }
        binding.setTeamIndexField.setText(currentIndex);
    }

    private void setupListeners()
    {
        binding.setTeamIndexField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                Log.d(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                Log.d(TAG, "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                Log.d(TAG, "afterTextChanged");
                validateInput();
            }
        });
    }

    private void validateInput()
    {
        String input = Objects.requireNonNull(binding.setTeamIndexField.getText()).toString().trim();
        boolean isValid = m_settings != null && m_settings.isValidTeamIndexStr(input);

        if (isValid)
        {
            binding.setTeamIndexLayout.setError(null);
        }
        else
        {
            binding.setTeamIndexLayout.setError(getString(R.string.valid_values));
        }
    }

    private void saveTeamIndex()
    {
        String input = Objects.requireNonNull(binding.setTeamIndexField.getText()).toString().trim();
        if (m_settings != null && m_settings.isValidTeamIndexStr(input))
        {
            Log.d(TAG, "Saving team index: " + input);
            m_settings.setTeamIndexStr(input);
        }
        else
        {
            Log.w(TAG, "Attempted to save invalid team index: " + input);
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        binding = null;
    }
}
