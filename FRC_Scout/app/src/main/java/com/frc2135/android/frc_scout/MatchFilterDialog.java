package com.frc2135.android.frc_scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.frc2135.android.frc_scout.databinding.MatchFilterDialogBinding;

/**
 * Dialog for filtering the match history list by team, competition, scout, or match number.
 */
public class MatchFilterDialog extends DialogFragment
{
    private static final String TAG = "MatchFilterDialog";
    private MatchFilterDialogBinding binding;

    /**
     * Creates a new instance of MatchFilterDialog.
     *
     * @return a new MatchFilterDialog instance
     */
    public static MatchFilterDialog newInstance()
    {
        return new MatchFilterDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = MatchFilterDialogBinding.inflate(inflater);

        setupFilters();

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle("Filter Matches")
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, (d, w) -> applyFilters())
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton("Clear Filters", (d, w) -> clearAllFilters())
                .create();

        dialog.setOnShowListener(d -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setBackgroundColor(Color.parseColor("#3F51B5"));
            okButton.setTextColor(Color.WHITE);
        });

        return dialog;
    }

    /**
     * Initializes the filter UI components and their listeners.
     */
    private void setupFilters()
    {
        MatchListData data = MatchListData.get(requireContext());

        // Team Filter
        binding.teamOptions.setEnabled(false);
        binding.teamSelect.setOnCheckedChangeListener((v, checked) -> binding.teamOptions.setEnabled(checked));
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, data.listTeams());
        binding.teamOptions.setAdapter(teamAdapter);

        // Competition Filter
        binding.eventOptions.setEnabled(false);
        binding.eventSelect.setOnCheckedChangeListener((v, checked) -> binding.eventOptions.setEnabled(checked));
        ArrayAdapter<String> eventAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, data.listCompetitions());
        binding.eventOptions.setAdapter(eventAdapter);

        // Scout Filter
        binding.scoutOptions.setEnabled(false);
        binding.scoutSelect.setOnCheckedChangeListener((v, checked) -> binding.scoutOptions.setEnabled(checked));
        ArrayAdapter<String> scoutAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, data.listScouts());
        binding.scoutOptions.setAdapter(scoutAdapter);

        // Match Number Filter
        binding.matchEntry.setEnabled(false);
        binding.matchSelect.setOnCheckedChangeListener((v, checked) -> binding.matchEntry.setEnabled(checked));
    }

    /**
     * Collects the selected filters and restarts MatchListActivity with the filter parameters.
     */
    private void applyFilters()
    {
        Log.d(TAG, "applyFilters() called");
        Intent intent = new Intent(requireActivity(), MatchListActivity.class);

        if (binding.teamSelect.isChecked() && binding.teamOptions.getSelectedItem() != null)
        {
            String team = binding.teamOptions.getSelectedItem().toString();
            if (!team.startsWith("Select"))
            {
                intent.putExtra("team", team);
            }
        }

        if (binding.eventSelect.isChecked() && binding.eventOptions.getSelectedItem() != null)
        {
            String comp = binding.eventOptions.getSelectedItem().toString();
            if (!comp.startsWith("Select"))
            {
                intent.putExtra("competition", comp);
            }
        }

        if (binding.scoutSelect.isChecked() && binding.scoutOptions.getSelectedItem() != null)
        {
            String scout = binding.scoutOptions.getSelectedItem().toString();
            if (!scout.startsWith("Select"))
            {
                intent.putExtra("scout", scout);
            }
        }

        if (binding.matchSelect.isChecked())
        {
            String match = binding.matchEntry.getText().toString().trim();
            if (!match.isEmpty())
            {
                intent.putExtra("match", match);
            }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Clears all filter selections and returns to the full match list.
     */
    private void clearAllFilters()
    {
        Log.d(TAG, "clearAllFilters() called");
        Intent intent = new Intent(requireActivity(), MatchListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        binding = null;
    }
}
