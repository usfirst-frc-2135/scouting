package com.frc2135.android.frc_scout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frc2135.android.frc_scout.databinding.ActionBarSwitchBinding;
import com.frc2135.android.frc_scout.databinding.MatchListCardBinding;
import com.frc2135.android.frc_scout.databinding.MatchListFragmentBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Fragment that displays a list of all scouted matches.
 * Provides functionality for filtering, sorting, deleting, and starting new scouting sessions.
 */
public class MatchListFragment extends Fragment
{
    private static final String TAG = "MatchListFragment";
    public static final String QRTAG = "qr";

    private List<MatchData> m_displayedMatches = new java.util.ArrayList<>();
    private MatchAdapter m_adapter;
    private MatchListFragmentBinding m_binding;

    private String m_eventFilter;
    private String m_matchFilter;
    private String m_teamFilter;
    private String m_scoutFilter;

    private MatchData m_selectedMatch;

    private boolean m_sortAscending = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setupMenuProvider();

        getParentFragmentManager().setFragmentResultListener("match_filter_applied", this, (requestKey, result) -> {
            Log.i(TAG, "Match filter applied, refreshing list");
            m_eventFilter = result.getString("event code");
            m_matchFilter = result.getString("match");
            m_teamFilter = result.getString("team");
            m_scoutFilter = result.getString("scout");
            refreshMatchList();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreateView");
        m_binding = MatchListFragmentBinding.inflate(inflater, parent, false);

        setupRecyclerView();
        setupNewMatchButton();
        setupSortSpinner();
        setupFilterButton();

        refreshMatchList();

        return m_binding.getRoot();
    }

    /**
     * Refreshes the list of displayed matches based on current filters and sorting.
     */
    private void refreshMatchList()
    {
        Log.d(TAG, "refreshMatchList");
        ScoutedMatches scoutedMatches = ScoutedMatches.getInstance(requireContext());
        List<MatchData> allMatches = scoutedMatches.getMatchList();
        m_displayedMatches = scoutedMatches.filterMatchList(allMatches, m_eventFilter, m_matchFilter, m_teamFilter, m_scoutFilter);
        updateSorting(); // This will apply current sort criteria and update the adapter
        Log.i(TAG, "Refreshed list. Displaying " + m_displayedMatches.size() + " scouted matches.");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume");
        // Ensure dropdown options are populated and correct
        ArrayAdapter<CharSequence> sortAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, getResources().getTextArray(R.array.sort_criteria_array));
        m_binding.matchListSortInput.setAdapter(sortAdapter);
    }

    private void setupRecyclerView()
    {
        m_binding.matchListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        m_adapter = new MatchAdapter(m_displayedMatches);
        m_binding.matchListRecyclerView.setAdapter(m_adapter);
    }

    private void setupNewMatchButton()
    {
        m_binding.matchListStartMatchFab.setOnClickListener(view -> {
            MatchData newMatch = new MatchData();
            newMatch.setEventCode(Settings.getInstance(requireContext()).getEventCode());
            ScoutedMatches.getInstance(requireContext()).addMatch(newMatch);

            Intent intent = new Intent(requireContext(), PreMatchActivity.class);
            intent.putExtra("match_ID", newMatch.getMatchID());
            intent.putExtra("in_edit", "no");
            startActivity(intent);
        });
    }

    private void setupSortSpinner()
    {
        ArrayAdapter<CharSequence> sortAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, getResources().getTextArray(R.array.sort_criteria_array));
        m_binding.matchListSortInput.setAdapter(sortAdapter);
        m_binding.matchListSortInput.setText(sortAdapter.getItem(0), false);

        m_binding.matchListSortInput.setOnItemClickListener((parent, view, position, id) -> updateSorting());

        m_binding.matchListSortOrderButton.setOnClickListener(v -> {
            m_sortAscending = !m_sortAscending;
            updateSortButtonIcon();
            updateSorting();
        });
        updateSortButtonIcon();
    }

    private void updateSortButtonIcon()
    {
        if (m_sortAscending)
        {
            m_binding.matchListSortOrderButton.setIconResource(R.drawable.ic_sort_ascending);
            m_binding.matchListSortOrderButton.setContentDescription(getString(R.string.ascending));
        }
        else
        {
            m_binding.matchListSortOrderButton.setIconResource(R.drawable.ic_sort_descending);
            m_binding.matchListSortOrderButton.setContentDescription(getString(R.string.descending));
        }
    }

    private void updateSorting()
    {
        String criteria = m_binding.matchListSortInput.getText().toString();
        ScoutedMatches scoutedMatches = ScoutedMatches.getInstance(requireContext());

        m_displayedMatches = scoutedMatches.sortMatchList(m_displayedMatches, criteria, m_sortAscending);
        if (m_adapter != null)
        {
            m_adapter.updateData(m_displayedMatches);
        }
    }

    private void setupFilterButton()
    {
        m_binding.matchListFilterButton.setOnClickListener(v -> MatchFilterDialog.newInstance(m_eventFilter, m_matchFilter, m_teamFilter, m_scoutFilter).show(requireActivity().getSupportFragmentManager(), "filter_dialog"));
    }

    private void setupMenuProvider()
    {
        requireActivity().addMenuProvider(new MenuProvider()
        {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
            {
                inflater.inflate(R.menu.action_bar_context_menu, menu);

                MenuItem darkModeItem = menu.findItem(R.id.dark_mode_switch);
                if (darkModeItem != null)
                {
                    View actionView = darkModeItem.getActionView();
                    if (actionView != null)
                    {
                        ActionBarSwitchBinding switchBinding = ActionBarSwitchBinding.bind(actionView);
                        CompoundButton darkSwitch = switchBinding.actionBarDarkModeSwitch;
                        Preferences prefs = Preferences.getInstance(requireContext());
                        darkSwitch.setChecked(prefs.isDarkMode());
                        darkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            Log.d(TAG, "Theme toggle: " + isChecked);
                            if (isChecked != prefs.isDarkMode())
                            {
                                prefs.setDarkMode(isChecked);
                            }
                        });
                    }
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item)
            {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                int itemID = item.getItemId();

                if (itemID == R.id.set_team_index_dialog)
                {
                    SetTeamIndexDialog.newInstance().show(getParentFragmentManager(), "set_team_index_dialog");
                }
                else if (itemID == R.id.load_tba_matches_dialog)
                {
                    LoadTBAMatchesDialog.newInstance().show(fm, "load_tba_matches_dialog");
                }
                else if (itemID == R.id.load_scout_names_dialog)
                {
                    LoadScoutNamesDialog.newInstance().show(fm, "load_scout_names_dialog");
                }
                else if (itemID == R.id.load_team_aliases_dialog)
                {
                    LoadTeamAliasesDialog.newInstance().show(fm, "load_team_aliases_dialog");
                }
                else if (itemID == R.id.clear_all_data_dialog)
                {
                    clearAllData();
                }
                else if (itemID == R.id.about_screen_dialog)
                {
                    startActivity(new Intent(requireContext(), SplashScreenActivity.class));
                    requireActivity().finish();
                }
                return true;
            }
        });
    }

    private void clearAllData()
    {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear All Application Data?")
                .setMessage("This will permanently delete all TBA matches, team aliases, scout names, and application settings. Scouted match data will not be affected. Continue?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    Log.d(TAG, "Executing Clear All Data");
                    Context context = requireContext();

                    // 1. Clear TBA Matches
                    TBAMatches.getInstance(context).deleteTBAMatchesFile(null);
                    TBAMatches.clearTBAMatches();

                    // 2. Clear Team Aliases
                    // TeamAliases doesn't have a bulk delete, but we can clear instance
                    // and let the user delete specific ones if needed, or we implement bulk.
                    // For now, clear settings-linked one.
                    String event = Settings.getInstance(context).getEventCode();
                    TeamAliases.getInstance(context).deleteTeamAliasesFile(event);
                    TeamAliases.clearTeamAliases();

                    // 3. Clear Scout Names
                    ScoutNames.getInstance(context).deleteEventScoutNames(context, event);
                    ScoutNames.clearScoutNames();

                    // 4. Reset Settings
                    Settings.getInstance(context).resetSettings();

                    refreshMatchList();
                    Log.i(TAG, "All configuration data cleared");
                    Toast.makeText(context, "All configuration data cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private class MatchHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private final MatchListCardBinding m_itemBinding;
        private MatchData m_match;

        public MatchHolder(MatchListCardBinding itemBinding)
        {
            super(itemBinding.getRoot());
            m_itemBinding = itemBinding;
            m_itemBinding.matchCardContainer.setOnClickListener(this);
            m_itemBinding.matchCardContainer.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                requireActivity().getMenuInflater().inflate(R.menu.match_item_context_menu, menu);
                m_selectedMatch = m_match;
            });
        }

        public void bind(MatchData match)
        {
            m_match = match;
            m_itemBinding.matchCardTeamText.setText(String.format("Team %s", match.getTeamNumber()));
            m_itemBinding.matchCardNumberText.setText(String.format("Match %s", match.getMatchNumber()));
            m_itemBinding.matchCardEventText.setText(match.getEventCode());
            m_itemBinding.matchCardScoutText.setText(String.format("Scout: %s", match.getScoutName()));
            m_itemBinding.matchCardDateText.setText(getFormattedDate(match.getTimestamp()));
        }

        @Override
        public void onClick(View v)
        {
            QRCodeDialog.newInstance(m_match).show(requireActivity().getSupportFragmentManager(), QRTAG);
        }

        @Override
        public boolean onLongClick(View v)
        {
            // Simple context menu or delete option could go here
            return false;
        }
    }

    private class MatchAdapter extends RecyclerView.Adapter<MatchHolder>
    {
        private List<MatchData> m_matches;

        public MatchAdapter(List<MatchData> matches)
        {
            m_matches = matches;
        }

        @NonNull
        @Override
        public MatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            MatchListCardBinding itemBinding = MatchListCardBinding.inflate(LayoutInflater.from(requireContext()), parent, false);
            return new MatchHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull MatchHolder holder, int position)
        {
            holder.bind(m_matches.get(position));
        }

        @Override
        public int getItemCount()
        {
            return m_matches.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void updateData(List<MatchData> matches)
        {
            m_matches = matches;
            notifyDataSetChanged();
        }
    }

    private static String getFormattedDate(Date date)
    {
        if (date == null)
        {
            return "unknown";
        }
        return new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US).format(date);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
        m_binding = null;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item)
    {
        MatchData m = m_selectedMatch;
        if (m == null)
        {
            return super.onContextItemSelected(item);
        }

        int itemID = item.getItemId();
        if (itemID == R.id.menu_item_delete_match)
        {
            Log.d(TAG, "Delete match button clicked");
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.delete_match)
                    .setMessage("Are you sure you want to delete this match? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        ScoutedMatches.getInstance(requireContext()).deleteMatch(m);
                        refreshMatchList();
                        m_selectedMatch = null;
                        Log.i(TAG, "Match deleted " + m.getMatchNumber());
                        Toast.makeText(requireContext(), "Match deleted " + m.getMatchNumber(), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> m_selectedMatch = null)
                    .show();
        }
        else if (itemID == R.id.menu_item_edit_match)
        {
            Log.d(TAG, "Edit match button clicked");

            Intent preMatchIntent = new Intent(requireContext(), PreMatchActivity.class);
            preMatchIntent.putExtra("match_ID", Objects.requireNonNull(m).getMatchID());
            preMatchIntent.putExtra("in_edit", "yes");
            m_binding.matchListRecyclerView.clearFocus();

            startActivity(preMatchIntent);
            m_selectedMatch = null;
        }
        else
        {
            return super.onContextItemSelected(item);
        }
        return true;
    }
}
