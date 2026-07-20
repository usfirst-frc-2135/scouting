package com.frc2135.android.frc_scout;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.frc2135.android.frc_scout.databinding.MatchFilterDialogBinding;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Dialog for filtering the match history list by event, match number, team, or scout.
 * This dialog uses the Fragment Result API to communicate the selected filter criteria
 * back to the {@link MatchListFragment}.
 */
public class MatchFilterDialog extends DialogFragment
{
    private static final String TAG = "MatchFilterDialog";
    private MatchFilterDialogBinding m_binding;
    private ScoutedMatches m_scoutedMatches;

    /**
     * Creates a new instance of {@link MatchFilterDialog} with current filter values.
     *
     * @param event the currently filtered event, or null
     * @param match the currently filtered match number, or null
     * @param team  the currently filtered team, or null
     * @param scout the currently filtered scout, or null
     * @return a new MatchFilterDialog instance
     */
    public static MatchFilterDialog newInstance(String event, String match, String team, String scout)
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

    /**
     * Constructs the {@link androidx.appcompat.app.AlertDialog} instance, initializes View Binding,
     * and restores any existing filter values from arguments.
     *
     * @param savedInstanceState if the dialog is being re-initialized from a previous saved state
     * @return the constructed {@link Dialog}
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreateDialog called");

        LayoutInflater inflater = getLayoutInflater();
        m_binding = MatchFilterDialogBinding.inflate(inflater);
        m_scoutedMatches = ScoutedMatches.getInstance(requireContext());

        setupFilters();
        restorePreviousFilters();

        com.google.android.material.dialog.MaterialAlertDialogBuilder builder = new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.filter_matches)
                .setView(m_binding.getRoot())
                .setPositiveButton(R.string.apply, (d, w) -> applyFilters())
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton(R.string.clear_all, null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            MaterialButton clearButton = (MaterialButton) dialog.getButton(Dialog.BUTTON_NEUTRAL);
            if (clearButton != null)
            {
                clearButton.setOnClickListener(v -> {
                    Log.d(TAG, "Clear All clicked");
                    m_binding.matchFilterEventInput.setText("", false);
                    m_binding.matchFilterMatchInput.setText("");
                    m_binding.matchFilterTeamInput.setText("", false);
                    m_binding.matchFilterScoutInput.setText("", false);
                });
            }
        });

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
                m_binding.matchFilterTeamInput.setText(team, false);
            }

            String event = args.getString("event");
            if (event != null && !event.isEmpty())
            {
                m_binding.matchFilterEventInput.setText(event, false);
            }

            String scout = args.getString("scout");
            if (scout != null && !scout.isEmpty())
            {
                m_binding.matchFilterScoutInput.setText(scout, false);
            }

            String match = args.getString("match");
            if (match != null && !match.isEmpty())
            {
                m_binding.matchFilterMatchInput.setText(match);
            }
        }
    }

    /**
     * Initializes the filter UI components and their adapters with data from {@link ScoutedMatches}.
     */
    private void setupFilters()
    {
        Log.d(TAG, "setupFilters");

        // Event Code Filter
        List<String> eventCodes = new ArrayList<>(m_scoutedMatches.listEventCodes());
        eventCodes.add(0, "Select event code");
        ArrayAdapter<String> eventAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, eventCodes);
        m_binding.matchFilterEventInput.setAdapter(eventAdapter);
        m_binding.matchFilterEventInput.setOnFocusChangeListener((v, focus) -> {
            if (focus)
            {
                m_binding.matchFilterEventInput.showDropDown();
            }
        });

        // Team Filter
        List<String> teams = new ArrayList<>(m_scoutedMatches.listTeams());
        teams.add(0, "Select team");
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, teams);
        m_binding.matchFilterTeamInput.setAdapter(teamAdapter);
        m_binding.matchFilterTeamInput.setOnFocusChangeListener((v, focus) -> {
            if (focus)
            {
                m_binding.matchFilterTeamInput.showDropDown();
            }
        });

        // Scout Filter
        List<String> scouts = new ArrayList<>(m_scoutedMatches.listScouts());
        scouts.add(0, "Select scout");
        ArrayAdapter<String> scoutAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, scouts);
        m_binding.matchFilterScoutInput.setAdapter(scoutAdapter);
        m_binding.matchFilterScoutInput.setOnFocusChangeListener((v, focus) -> {
            if (focus)
            {
                m_binding.matchFilterScoutInput.showDropDown();
            }
        });
    }

    /**
     * Collects the selected filters and sends them back to the fragment via Fragment Result API.
     */
    private void applyFilters()
    {
        Log.d(TAG, "applyFilters called");
        Bundle result = new Bundle();

        String event = m_binding.matchFilterEventInput.getText().toString().trim();
        if (!event.isEmpty())
        {
            result.putString("event code", event);
        }

        String match = Objects.requireNonNull(m_binding.matchFilterMatchInput.getText()).toString().trim();
        if (!match.isEmpty())
        {
            result.putString("match", match);
        }

        String team = m_binding.matchFilterTeamInput.getText().toString().trim();
        if (!team.isEmpty())
        {
            result.putString("team", team);
        }

        String scout = m_binding.matchFilterScoutInput.getText().toString().trim();
        if (!scout.isEmpty())
        {
            result.putString("scout", scout);
        }

        getParentFragmentManager().setFragmentResult("match_filter_applied", result);
        dismiss();
    }

    /**
     * Called when the dialog is visible to the user and actively running.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    /**
     * Cleans up the View Binding reference when the fragment view is being destroyed.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
        m_binding = null;
    }
}
