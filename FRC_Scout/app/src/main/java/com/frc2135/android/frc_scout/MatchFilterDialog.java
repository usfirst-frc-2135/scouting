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
     * @param team  the currently filtered team, or null
     * @param event the currently filtered event, or null
     * @param scout the currently filtered scout, or null
     * @param match the currently filtered match number, or null
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
                .setNeutralButton("Clear Filters", null); // Set listener later to prevent auto-dismiss

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> dialog.getButton(Dialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
            Log.d(TAG, "Clear Filters clicked");
            binding.teamOptions.setText("", false);
            binding.eventOptions.setText("", false);
            binding.scoutOptions.setText("", false);
            binding.matchEntry.setText("");
        }));

        return dialog;
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
                binding.teamOptions.setText(team, false);
            }

            String event = args.getString("event");
            if (event != null && !event.isEmpty())
            {
                binding.eventOptions.setText(event, false);
            }

            String scout = args.getString("scout");
            if (scout != null && !scout.isEmpty())
            {
                binding.scoutOptions.setText(scout, false);
            }

            String match = args.getString("match");
            if (match != null && !match.isEmpty())
            {
                binding.matchEntry.setText(match);
            }
        }
    }

    /**
     * Initializes the filter UI components and their adapters.
     */
    private void setupFilters()
    {
        MatchListData data = MatchListData.get(requireContext());

        // Team Filter
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, data.listTeams());
        binding.teamOptions.setAdapter(teamAdapter);

        // Competition Filter
        ArrayAdapter<String> eventAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, data.listCompetitions());
        binding.eventOptions.setAdapter(eventAdapter);

        // Scout Filter
        ArrayAdapter<String> scoutAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, data.listScouts());
        binding.scoutOptions.setAdapter(scoutAdapter);
    }

    /**
     * Collects the selected filters and restarts MatchListActivity with the filter parameters.
     */
    private void applyFilters()
    {
        Log.d(TAG, "applyFilters() called");
        Intent intent = new Intent(requireActivity(), MatchListActivity.class);

        String team = binding.teamOptions.getText().toString().trim();
        if (!team.isEmpty())
        {
            intent.putExtra("team", team);
        }

        String comp = binding.eventOptions.getText().toString().trim();
        if (!comp.isEmpty())
        {
            intent.putExtra("competition", comp);
        }

        String scout = binding.scoutOptions.getText().toString().trim();
        if (!scout.isEmpty())
        {
            intent.putExtra("scout", scout);
        }

        String match = Objects.requireNonNull(binding.matchEntry.getText()).toString().trim();
        if (!match.isEmpty())
        {
            intent.putExtra("match", match);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
