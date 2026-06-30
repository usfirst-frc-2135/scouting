package com.frc2135.android.frc_scout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.frc2135.android.frc_scout.databinding.MatchFilterDialogBinding;

import java.util.Objects;

/**
 * Dialog for filtering the match history list by team, competition, scout, or match number.
 */
public class MatchFilterDialog extends DialogFragment
{
    private static final String TAG = "MatchFilterDialog";
    private MatchFilterDialogBinding binding;

    /**
     * Creates a new instance of MatchFilterDialog with current filter values.
     *
     * @param team    the currently filtered team, or null
     * @param event   the currently filtered event, or null
     * @param scout   the currently filtered scout, or null
     * @param match   the currently filtered match number, or null
     * @return a new MatchFilterDialog instance
     */
    public static MatchFilterDialog newInstance(String team, String event, String scout, String match)
    {
        MatchFilterDialog fragment = new MatchFilterDialog();
        Bundle args = new Bundle();
        args.putString("team", team);
        args.putString("event", event);
        args.putString("scout", scout);
        args.putString("match", match);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog called");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = MatchFilterDialogBinding.inflate(inflater);

        setupFilters();
        restorePreviousFilters();

        com.google.android.material.dialog.MaterialAlertDialogBuilder builder = new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Filter Matches")
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, (d, w) -> applyFilters())
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton("Clear Filters", (d, w) -> clearAllFilters());

        return builder.create();
    }

    /**
     * Restores the filter values from arguments if they exist.
     */
    private void restorePreviousFilters()
    {
        Bundle args = getArguments();
        if (args != null)
        {
            String team = args.getString("team");
            if (team != null && !team.isEmpty())
            {
                binding.teamSelect.setChecked(true);
                binding.teamLayout.setEnabled(true);
                binding.teamOptions.setText(team, false);
            }

            String event = args.getString("event");
            if (event != null && !event.isEmpty())
            {
                binding.eventSelect.setChecked(true);
                binding.eventLayout.setEnabled(true);
                binding.eventOptions.setText(event, false);
            }

            String scout = args.getString("scout");
            if (scout != null && !scout.isEmpty())
            {
                binding.scoutSelect.setChecked(true);
                binding.scoutLayout.setEnabled(true);
                binding.scoutOptions.setText(scout, false);
            }

            String match = args.getString("match");
            if (match != null && !match.isEmpty())
            {
                binding.matchSelect.setChecked(true);
                binding.matchLayout.setEnabled(true);
                binding.matchEntry.setText(match);
            }
        }
    }

    /**
     * Initializes the filter UI components and their listeners.
     */
    private void setupFilters()
    {
        MatchListData data = MatchListData.get(requireContext());

        // Team Filter
        binding.teamLayout.setEnabled(false);
        binding.teamSelect.setOnCheckedChangeListener((v, checked) -> {
            binding.teamLayout.setEnabled(checked);
            if (checked) binding.teamOptions.showDropDown();
        });
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, data.listTeams());
        binding.teamOptions.setAdapter(teamAdapter);

        // Competition Filter
        binding.eventLayout.setEnabled(false);
        binding.eventSelect.setOnCheckedChangeListener((v, checked) -> {
            binding.eventLayout.setEnabled(checked);
            if (checked) binding.eventOptions.showDropDown();
        });
        ArrayAdapter<String> eventAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, data.listCompetitions());
        binding.eventOptions.setAdapter(eventAdapter);

        // Scout Filter
        binding.scoutLayout.setEnabled(false);
        binding.scoutSelect.setOnCheckedChangeListener((v, checked) -> {
            binding.scoutLayout.setEnabled(checked);
            if (checked) binding.scoutOptions.showDropDown();
        });
        ArrayAdapter<String> scoutAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, data.listScouts());
        binding.scoutOptions.setAdapter(scoutAdapter);

        // Match Number Filter
        binding.matchLayout.setEnabled(false);
        binding.matchSelect.setOnCheckedChangeListener((v, checked) -> {
            binding.matchLayout.setEnabled(checked);
            if (checked)
            {
                binding.matchEntry.requestFocus();
            }
        });
    }

    /**
     * Collects the selected filters and restarts MatchListActivity with the filter parameters.
     */
    private void applyFilters()
    {
        Log.d(TAG, "applyFilters() called");
        Intent intent = new Intent(requireActivity(), MatchListActivity.class);

        if (binding.teamSelect.isChecked())
        {
            String team = binding.teamOptions.getText().toString();
            if (!team.isEmpty() && !team.startsWith("Select"))
            {
                intent.putExtra("team", team);
            }
        }

        if (binding.eventSelect.isChecked())
        {
            String comp = binding.eventOptions.getText().toString();
            if (!comp.isEmpty() && !comp.startsWith("Select"))
            {
                intent.putExtra("competition", comp);
            }
        }

        if (binding.scoutSelect.isChecked())
        {
            String scout = binding.scoutOptions.getText().toString();
            if (!scout.isEmpty() && !scout.startsWith("Select"))
            {
                intent.putExtra("scout", scout);
            }
        }

        if (binding.matchSelect.isChecked())
        {
            String match = Objects.requireNonNull(binding.matchEntry.getText()).toString().trim();
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
