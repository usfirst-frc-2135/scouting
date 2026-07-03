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

import java.util.Objects;

/**
 * Dialog for filtering the match history list by event, match number, or scout.
 */
public class MatchFilterDialog extends DialogFragment
{
    private static final String TAG = "MatchFilterDialog";
    private MatchFilterDialogBinding m_binding;

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
        m_binding = MatchFilterDialogBinding.inflate(inflater);

        setupFilters();
        restorePreviousFilters();

        com.google.android.material.dialog.MaterialAlertDialogBuilder builder = new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.filter_matches)
                .setView(m_binding.getRoot())
                .setPositiveButton(R.string.apply, (d, w) -> applyFilters())
                .setNegativeButton(android.R.string.cancel, (d, w) -> dismiss())
                .setNeutralButton(R.string.clear_all, null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> dialog.getButton(Dialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
            Log.d(TAG, "Clear All clicked");
            m_binding.eventOptions.setText("", false);
            m_binding.matchEntry.setText("");
            m_binding.teamOptions.setText("", false);
            m_binding.scoutOptions.setText("", false);
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
                m_binding.teamOptions.setText(team, false);
            }

            String event = args.getString("event");
            if (event != null && !event.isEmpty())
            {
                m_binding.eventOptions.setText(event, false);
            }

            String scout = args.getString("scout");
            if (scout != null && !scout.isEmpty())
            {
                m_binding.scoutOptions.setText(scout, false);
            }

            String match = args.getString("match");
            if (match != null && !match.isEmpty())
            {
                m_binding.matchEntry.setText(match);
            }
        }
    }

    /**
     * Initializes the filter UI components and their adapters.
     */
    private void setupFilters()
    {
        Log.d(TAG, "setupFilters()");
        ScoutedMatches data = ScoutedMatches.getInstance(requireContext());

        // Event Code Filter
        ArrayAdapter<String> eventAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, data.listEventCodes());
        m_binding.eventOptions.setAdapter(eventAdapter);
        m_binding.eventOptions.setOnFocusChangeListener((v, focus) -> {
            if (focus)
            {
                m_binding.eventOptions.showDropDown();
            }
        });

        // Team Filter
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, data.listTeams());
        m_binding.teamOptions.setAdapter(teamAdapter);
        m_binding.teamOptions.setOnFocusChangeListener((v, focus) -> {
            if (focus)
            {
                m_binding.teamOptions.showDropDown();
            }
        });

        // Scout Filter
        ArrayAdapter<String> scoutAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, data.listScouts());
        m_binding.scoutOptions.setAdapter(scoutAdapter);
        m_binding.scoutOptions.setOnFocusChangeListener((v, focus) -> {
            if (focus)
            {
                m_binding.scoutOptions.showDropDown();
            }
        });
    }

    /**
     * Collects the selected filters and sends them back to the fragment via Fragment Result API.
     */
    private void applyFilters()
    {
        Log.d(TAG, "applyFilters() called");
        Bundle result = new Bundle();

        String event = m_binding.eventOptions.getText().toString().trim();
        if (!event.isEmpty())
        {
            result.putString("event code", event);
        }

        String match = Objects.requireNonNull(m_binding.matchEntry.getText()).toString().trim();
        if (!match.isEmpty())
        {
            result.putString("match", match);
        }

        String team = m_binding.teamOptions.getText().toString().trim();
        if (!team.isEmpty())
        {
            result.putString("team", team);
        }

        String scout = m_binding.scoutOptions.getText().toString().trim();
        if (!scout.isEmpty())
        {
            result.putString("scout", scout);
        }

        getParentFragmentManager().setFragmentResult("match_filter_applied", result);
        dismiss();
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
