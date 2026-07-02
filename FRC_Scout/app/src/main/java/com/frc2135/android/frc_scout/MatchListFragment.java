package com.frc2135.android.frc_scout;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frc2135.android.frc_scout.databinding.MatchListFragmentBinding;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;

import java.io.IOException;
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
    private MatchListFragmentBinding binding;

    private String m_teamFilter;
    private String m_eventFilter;
    private String m_scoutFilter;
    private String m_matchFilter;

    private MatchData m_selectedMatch;

    private boolean m_sortAscending = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setupMenuProvider();

        getParentFragmentManager().setFragmentResultListener("match_filter_applied", this, (requestKey, result) -> {
            Log.d(TAG, "Match filter applied, refreshing list");
            m_eventFilter = result.getString("event code");
            m_matchFilter = result.getString("match");
            m_teamFilter = result.getString("team");
            m_scoutFilter = result.getString("scout");
            refreshList();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        binding = MatchListFragmentBinding.inflate(inflater, parent, false);

        setupRecyclerView();
        setupNewMatchButton();
        setupSortSpinner();
        setupFilterButton();

        loadInitialMatches();

        return binding.getRoot();
    }

    private void loadInitialMatches()
    {
        refreshList();
    }

    private void refreshList()
    {
        MatchListData data = MatchListData.get(requireContext());
        List<MatchData> allMatches = data.getMatches();
        m_displayedMatches = data.filterMatches(allMatches, m_teamFilter, m_eventFilter, m_scoutFilter, m_matchFilter);
        updateSorting(); // This will apply current sort criteria and update the adapter
        Log.d(TAG, "Refreshed list. Displaying " + m_displayedMatches.size() + " matches.");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // Ensure dropdown options are populated and correct
        ArrayAdapter<CharSequence> sortAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, getResources().getTextArray(R.array.sort_criteria_array));
        binding.sortOptions.setAdapter(sortAdapter);
    }

    private void setupRecyclerView()
    {
        binding.matchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        m_adapter = new MatchAdapter(m_displayedMatches);
        binding.matchRecyclerView.setAdapter(m_adapter);
    }

    private void setupNewMatchButton()
    {
        binding.startMatch.setOnClickListener(view -> {
            try
            {
                MatchData newMatch = new MatchData(requireContext());
                MatchListData.get(requireContext()).addMatch(newMatch);

                Intent intent = new Intent(getActivity(), PreMatchActivity.class);
                intent.putExtra("match_ID", newMatch.getMatchID());
                intent.putExtra("in_edit", "no");
                startActivity(intent);
            }
            catch (IOException | JSONException e)
            {
                Log.e(TAG, "Error starting new match", e);
            }
        });
    }

    private void setupSortSpinner()
    {
        ArrayAdapter<CharSequence> sortAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, getResources().getTextArray(R.array.sort_criteria_array));
        binding.sortOptions.setAdapter(sortAdapter);
        binding.sortOptions.setText(sortAdapter.getItem(0), false);

        binding.sortOptions.setOnItemClickListener((parent, view, position, id) -> updateSorting());

        binding.sortOrderButton.setOnClickListener(v -> {
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
            binding.sortOrderButton.setIconResource(R.drawable.ic_sort_ascending);
            binding.sortOrderButton.setContentDescription(getString(R.string.ascending));
        }
        else
        {
            binding.sortOrderButton.setIconResource(R.drawable.ic_sort_descending);
            binding.sortOrderButton.setContentDescription(getString(R.string.descending));
        }
    }

    private void updateSorting()
    {
        String criteria = binding.sortOptions.getText().toString();
        MatchListData data = MatchListData.get(requireContext());

        m_displayedMatches = data.sortMatches(m_displayedMatches, criteria, m_sortAscending);
        if (m_adapter != null)
        {
            m_adapter.updateData(m_displayedMatches);
        }
    }

    private void setupFilterButton()
    {
        binding.filterButton.setOnClickListener(v -> MatchFilterDialog.newInstance(m_teamFilter, m_eventFilter, m_scoutFilter, m_matchFilter).show(requireActivity().getSupportFragmentManager(), "filter_dialog"));
    }

    private void setupMenuProvider()
    {
        requireActivity().addMenuProvider(new MenuProvider()
        {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
            {
                inflater.inflate(R.menu.dark_mode_context_menu, menu);

                MenuItem darkModeItem = menu.findItem(R.id.dark_mode_switch);
                if (darkModeItem != null)
                {
                    View actionView = darkModeItem.getActionView();
                    if (actionView != null)
                    {
                        CompoundButton darkSwitch = actionView.findViewById(R.id.dark_mode_switch_view);
                        if (darkSwitch != null)
                        {
                            Preferences prefs = Preferences.get(requireContext());
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
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item)
            {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                int itemID = item.getItemId();

                if (itemID == R.id.set_team_index)
                {
                    SetTeamIndexDialog.newInstance().show(getParentFragmentManager(), "set_team_index_dialog");
                }
                else if (itemID == R.id.load_event_data_tba)
                {
                    LoadEventDialog.newInstance().show(fm, "load_event_dialog");
                }
                else if (itemID == R.id.load_aliases)
                {
                    LoadTeamAliasesDialog.newInstance().show(fm, "load_aliases");
                }
                else if (itemID == R.id.load_scout_names)
                {
                    LoadScoutNamesDialog.newInstance().show(fm, "load_scouts");
                }
                else if (itemID == R.id.about_screen)
                {
                    startActivity(new Intent(getActivity(), SplashScreenActivity.class));
                    requireActivity().finish();
                }
                return true;
            }
        });
    }

    private class MatchHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        @SuppressWarnings("FieldCanBeLocal")
        private final MaterialCardView m_cardView;
        private final TextView m_teamNumber;
        private final TextView m_matchNumber;
        private final TextView m_eventCode;
        private final TextView m_scoutName;
        private final TextView m_date;
        private MatchData m_match;

        public MatchHolder(View itemView)
        {
            super(itemView);
            m_cardView = itemView.findViewById(R.id.match_card);
            m_teamNumber = itemView.findViewById(R.id.match_team_number);
            m_matchNumber = itemView.findViewById(R.id.match_number);
            m_eventCode = itemView.findViewById(R.id.match_event_code);
            m_scoutName = itemView.findViewById(R.id.match_scout_name);
            m_date = itemView.findViewById(R.id.match_date);
            m_cardView.setOnClickListener(this);
            m_cardView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                requireActivity().getMenuInflater().inflate(R.menu.match_item_context_menu, menu);
                m_selectedMatch = m_match;
            });
        }

        public void bind(MatchData match)
        {
            m_match = match;
            m_teamNumber.setText(String.format("Team %s", match.getTeamNumber()));
            m_matchNumber.setText(String.format("Match %s", match.getMatchNumber()));
            m_eventCode.setText(match.getEventCode());
            m_scoutName.setText(String.format("Scout: %s", match.getScoutName()));
            m_date.setText(getFormattedDate(match.getTimestamp()));
        }

        @Override
        public void onClick(View v)
        {
            QRFragment.newInstance(m_match).show(requireActivity().getSupportFragmentManager(), QRTAG);
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
            View view = LayoutInflater.from(requireActivity()).inflate(R.layout.match_list_item, parent, false);
            return new MatchHolder(view);
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
        binding = null;
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
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Match")
                    .setMessage("Are you sure you want to delete this match? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        MatchListData.get(requireContext()).deleteMatch(m);
                        refreshList();
                        m_selectedMatch = null;
                        Toast.makeText(requireContext(), "Match deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> m_selectedMatch = null)
                    .show();
        }
        else if (itemID == R.id.edit_match_button)
        {
            Log.d(TAG, "Edit match button clicked");

            Intent data = new Intent(getActivity(), PreMatchActivity.class);
            data.putExtra("match_ID", Objects.requireNonNull(m).getMatchID());
            data.putExtra("in_edit", "yes");
            binding.matchRecyclerView.clearFocus();

            startActivity(data);
            m_selectedMatch = null;
        }
        else
        {
            return super.onContextItemSelected(item);
        }
        return true;
    }
}
