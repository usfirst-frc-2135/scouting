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
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Fragment that displays a list of all scouted matches.
 * Provides functionality for filtering, sorting, deleting, and starting new scouting sessions.
 * Manages the application's main configuration options through an action bar menu.
 */
public class MatchListFragment extends Fragment
{
    private static final String TAG = "MatchListFragment";

    /**
     * Tag used for the QR code dialog fragment.
     */
    public static final String QRTAG = "qr";

    private List<MatchData> m_displayedMatches = new java.util.ArrayList<>();
    private MatchAdapter m_adapter;
    private MatchListFragmentBinding m_binding;
    private ScoutedMatches m_scoutedMatches;
    private Settings m_settings;

    private String m_eventFilter;
    private String m_matchFilter;
    private String m_teamFilter;
    private String m_scoutFilter;

    private MatchData m_selectedMatch;

    private boolean m_sortAscending = false;

    /**
     * Initializes the fragment and sets up result listeners for match filtering.
     *
     * @param savedInstanceState if the fragment is being re-created from a previous saved state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        m_scoutedMatches = ScoutedMatches.getInstance(requireContext());
        m_settings = Settings.getInstance(requireContext());
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

    /**
     * Inflates the layout for this fragment and initializes UI components.
     *
     * @param inflater           the LayoutInflater object that can be used to inflate views
     * @param parent             if non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState if non-null, this fragment is being re-constructed from a previous saved state
     * @return the root View of the inflated layout
     */
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
        List<MatchData> allMatches = m_scoutedMatches.getMatchList();
        m_displayedMatches = m_scoutedMatches.filterMatchList(allMatches, m_eventFilter, m_matchFilter, m_teamFilter, m_scoutFilter);

        boolean isEmpty = m_displayedMatches.isEmpty();
        m_binding.matchListRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        m_binding.matchListEmptyStateContainer.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        updateSorting(); // This will apply current sort criteria and update the adapter
        Log.i(TAG, "Refreshed list. Displaying " + m_displayedMatches.size() + " scouted matches.");
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * Re-populates the sort adapter to ensure consistency.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume");
        // Ensure dropdown options are populated and correct
        ArrayAdapter<CharSequence> sortAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.dropdown_item, getResources().getTextArray(R.array.sort_criteria_array));
        m_binding.matchListSortInput.setAdapter(sortAdapter);
        refreshMatchList();
    }

    /**
     * Configures the RecyclerView and its adapter for displaying matches.
     */
    private void setupRecyclerView()
    {
        m_binding.matchListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        m_adapter = new MatchAdapter(m_displayedMatches);
        m_binding.matchListRecyclerView.setAdapter(m_adapter);
    }

    /**
     * Sets up the FloatingActionButton for starting a new scouting session.
     */
    private void setupNewMatchButton()
    {
        m_binding.matchListStartMatchFab.setOnClickListener(view -> {
            MatchData newMatch = new MatchData();
            newMatch.setEventCode(m_settings.getEventCode());
            m_scoutedMatches.addMatch(newMatch);

            Intent intent = new Intent(requireContext(), PreMatchActivity.class);
            intent.putExtra(Constants.MATCH_ID, newMatch.getMatchID());
            intent.putExtra(Constants.IN_EDIT_MODE, "no");
            startActivity(intent);
        });
    }

    /**
     * Configures the sort criteria dropdown menu and order toggle button.
     */
    private void setupSortSpinner()
    {
        ArrayAdapter<CharSequence> sortAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.dropdown_item, getResources().getTextArray(R.array.sort_criteria_array));
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

    /**
     * Updates the sort order button icon based on the current direction (ascending/descending).
     */
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

    /**
     * Re-sorts the match list based on the current UI selections and updates the adapter.
     */
    private void updateSorting()
    {
        String criteria = m_binding.matchListSortInput.getText().toString();
        m_displayedMatches = m_scoutedMatches.sortMatchList(m_displayedMatches, criteria, m_sortAscending);
        if (m_adapter != null)
        {
            m_adapter.updateData(m_displayedMatches);
        }
    }

    /**
     * Configures the filter button to show the {@link MatchFilterDialog}.
     */
    private void setupFilterButton()
    {
        m_binding.matchListFilterButton.setOnClickListener(v -> MatchFilterDialog.newInstance(m_eventFilter, m_matchFilter, m_teamFilter, m_scoutFilter).show(requireActivity().getSupportFragmentManager(), "filter_dialog"));
    }

    /**
     * Registers the {@link MenuProvider} for the host activity's action bar.
     * Manages global app options like dark mode, data loading, and clearing application state.
     */
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

    /**
     * Presents a confirmation dialog to clear all non-scouting application data (TBA matches, scout names, aliases, settings).
     */
    private void clearAllData()
    {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear All Application Data?")
                .setMessage("This will permanently delete all TBA matches, team aliases, scout names, and application settings. Scouted match data will not be affected. Continue?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    Log.d(TAG, "Executing Clear All Settings");
                    Context context = requireContext();

                    // 1. Clear TBA Matches
                    TBAMatches.getInstance(context).deleteTBAMatchesFile(null);
                    TBAMatches.clearTBAMatches();

                    // 2. Clear Team Aliases
                    TeamAliases.getInstance(context).deleteTeamAliasesFile(null);
                    TeamAliases.clearTeamAliases();

                    // 3. Clear Scout Names
                    ScoutNames.getInstance(context).deleteScoutNamesFile(null);
                    ScoutNames.clearScoutNames();

                    // 4. Reset Settings
                    m_settings.resetSettings();
                    m_settings.saveSettingsSilent();

                    refreshMatchList();
                    Log.i(TAG, "All configuration data cleared");
                    Toast.makeText(context, "All configuration data cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * ViewHolder class for individual match items in the RecyclerView.
     */
    private class MatchHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private final MatchListCardBinding m_itemBinding;
        private MatchData m_match;

        /**
         * Initializes the ViewHolder and attaches click and context menu listeners.
         *
         * @param itemBinding the view binding for the match card item
         */
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

        /**
         * Binds match data to the UI components of the card.
         *
         * @param match the MatchData object to display
         */
        public void bind(MatchData match)
        {
            m_match = match;
            m_itemBinding.matchCardTeamText.setText(String.format("Team %s", match.getTeamNumber()));
            m_itemBinding.matchCardNumberText.setText(String.format("Match %s", match.getMatchNumber()));
            m_itemBinding.matchCardEventText.setText(match.getEventCode());
            m_itemBinding.matchCardScoutText.setText(String.format("Scout: %s", match.getScoutName()));
            m_itemBinding.matchCardDateText.setText(getFormattedDate(match.getTimestamp()));
        }

        /**
         * Responds to a single click on the match card by showing its QR code.
         *
         * @param v the clicked View
         */
        @Override
        public void onClick(View v)
        {
            QRCodeDialog.newInstance(m_match).show(requireActivity().getSupportFragmentManager(), QRTAG);
        }

        /**
         * Optional hook for handling long click events.
         *
         * @param v the clicked View
         * @return false (event handled via context menu listener)
         */
        @Override
        public boolean onLongClick(View v)
        {
            return false;
        }
    }

    /**
     * RecyclerView adapter for the list of scouted matches.
     */
    private class MatchAdapter extends RecyclerView.Adapter<MatchHolder>
    {
        private List<MatchData> m_matches;

        /**
         * Initializes the adapter with a list of matches.
         *
         * @param matches the initial list of matches to display
         */
        public MatchAdapter(List<MatchData> matches)
        {
            m_matches = matches;
        }

        /**
         * Creates a new ViewHolder instance for a match item.
         */
        @NonNull
        @Override
        public MatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            MatchListCardBinding itemBinding = MatchListCardBinding.inflate(LayoutInflater.from(requireContext()), parent, false);
            return new MatchHolder(itemBinding);
        }

        /**
         * Binds data to the ViewHolder at the specified position.
         */
        @Override
        public void onBindViewHolder(@NonNull MatchHolder holder, int position)
        {
            holder.bind(m_matches.get(position));
        }

        /**
         * Returns the number of items in the list.
         */
        @Override
        public int getItemCount()
        {
            return m_matches.size();
        }

        /**
         * Updates the adapter's data set and notifies the RecyclerView to refresh.
         *
         * @param matches the new list of matches to display
         */
        @SuppressLint("NotifyDataSetChanged")
        public void updateData(List<MatchData> matches)
        {
            m_matches = matches;
            notifyDataSetChanged();
        }
    }

    /**
     * Formats a {@link Date} object into a human-readable string (MMM dd, yyyy HH:mm).
     *
     * @param date the date to format
     * @return the formatted string, or "unknown" if null
     */
    private static String getFormattedDate(Date date)
    {
        if (date == null)
        {
            return "unknown";
        }
        return new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US).format(date);
    }

    /**
     * Cleans up the view binding reference when the fragment view is being destroyed.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
        m_binding = null;
    }

    /**
     * Handles selection of context menu items (Edit/Delete) for individual match items.
     *
     * @param item the menu item that was selected
     * @return true if the event was handled
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item)
    {
        MatchData m = m_selectedMatch;
        if (m == null)
        {
            return super.onContextItemSelected(item);
        }

        int itemID = item.getItemId();
        if (itemID == R.id.menu_item_edit_match)
        {
            Log.d(TAG, "Edit match button clicked");

            Intent preMatchIntent = new Intent(requireContext(), PreMatchActivity.class);
            preMatchIntent.putExtra(Constants.MATCH_ID, Objects.requireNonNull(m).getMatchID());
            preMatchIntent.putExtra(Constants.IN_EDIT_MODE, "yes");
            Log.i(TAG, "Match selected for edit " + m.getMatchNumber() + " ID: " + m.getMatchID());
            m_binding.matchListRecyclerView.clearFocus();

            startActivity(preMatchIntent);
            m_selectedMatch = null;
        }
        else if (itemID == R.id.menu_item_delete_match)
        {
            Log.d(TAG, "Delete match button clicked");
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.delete_match)
                    .setMessage("Are you sure you want to delete this match?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        m_scoutedMatches.deleteMatch(m);
                        refreshMatchList();
                        m_selectedMatch = null;
                        Log.i(TAG, "Match deleted: " + m.getMatchNumber());

                        Snackbar.make(m_binding.getRoot(), "Match deleted", Snackbar.LENGTH_LONG)
                                .setAction("Undo", v -> {
                                    m_scoutedMatches.addMatch(m);
                                    m_scoutedMatches.saveMatchDataFile(m);
                                    refreshMatchList();
                                    Log.i(TAG, "Delete undone for match: " + m.getMatchNumber());
                                }).show();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> m_selectedMatch = null)
                    .show();
        }
        else
        {
            return super.onContextItemSelected(item);
        }
        return true;
    }
}
