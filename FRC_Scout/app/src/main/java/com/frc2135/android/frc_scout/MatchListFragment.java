package com.frc2135.android.frc_scout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;

import com.frc2135.android.frc_scout.databinding.MatchListBinding;

import org.json.JSONException;

import java.io.File;
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
public class MatchListFragment extends ListFragment
{
    private static final String TAG = "MatchListFragment";
    public static final String QRTAG = "qr";

    private List<MatchData> m_displayedMatches;
    private MatchAdapter m_adapter;
    private MatchListBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        ActionBar aBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (aBar != null)
        {
            aBar.setTitle("Recorded Matches");
        }
        setupMenuProvider();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        binding = MatchListBinding.inflate(inflater, parent, false);

        loadInitialMatches();
        applyIntentFilters();

        m_adapter = new MatchAdapter(m_displayedMatches);
        setListAdapter(m_adapter);

        setupListView();
        setupDarkModeToggle();
        setupFab();
        setupSortSpinner();
        setupFilterButton();

        return binding.getRoot();
    }

    private void loadInitialMatches()
    {
        m_displayedMatches = MatchListData.get(requireContext()).sortByTimestamp2(MatchListData.get(requireContext()).getMatches());
        Log.d(TAG, "Loaded " + m_displayedMatches.size() + " matches initially.");
    }

    /**
     * Checks the hosting activity's Intent for filter parameters passed from {@link MatchFilterDialog}.
     */
    private void applyIntentFilters()
    {
        Intent intent = requireActivity().getIntent();
        MatchListData data = MatchListData.get(requireContext());

        if (intent.hasExtra("team"))
        {
            String team = intent.getStringExtra("team");
            m_displayedMatches = data.filterByTeam(m_displayedMatches, team);
            Log.d(TAG, "Filtered by team: " + team);
        }
        if (intent.hasExtra("competition"))
        {
            String comp = intent.getStringExtra("competition");
            m_displayedMatches = data.filterByCompetition(m_displayedMatches, comp);
            Log.d(TAG, "Filtered by competition: " + comp);
        }
        if (intent.hasExtra("scout"))
        {
            String scout = intent.getStringExtra("scout");
            m_displayedMatches = data.filterByScout(m_displayedMatches, scout);
            Log.d(TAG, "Filtered by scout: " + scout);
        }
        if (intent.hasExtra("match"))
        {
            String match = intent.getStringExtra("match");
            m_displayedMatches = data.filterByMatchNumber(m_displayedMatches, match);
            Log.d(TAG, "Filtered by match number: " + match);
        }
    }

    private void setupListView()
    {
        ListView listView = binding.getRoot().findViewById(android.R.id.list);
        registerForContextMenu(listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
            {
                Log.d(TAG, "onItemCheckedStateChanged");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                Log.d(TAG, "onCreateActionMode");
                mode.getMenuInflater().inflate(R.menu.match_list_item_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                Log.d(TAG, "onPrepareActionMode");
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                Log.d(TAG, "onActionItemClicked");
                if (item.getItemId() == R.id.menu_item_delete_match)
                {
                    Log.d(TAG, "onActionItemClicked");
                    MatchListData matchHistory = MatchListData.get(requireContext());
                    ListView lv = getListView();
                    for (int i = m_adapter.getCount() - 1; i >= 0; i--)
                    {
                        if (lv.isItemChecked(i))
                        {
                            matchHistory.deleteMatch(m_adapter.getItem(i));
                            m_displayedMatches.remove(m_adapter.getItem(i));
                        }
                    }
                    mode.finish();
                    m_adapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {
                Log.d(TAG, "onDestroyActionMode");
            }
        });
    }

    private void setupDarkModeToggle()
    {
        Preferences prefs = Preferences.get(requireContext());
        binding.darkToggle.setChecked(prefs.getDarkMode());
        binding.darkToggle.setOnClickListener(v -> {
            Log.d(TAG, "Theme toggle: " + binding.darkToggle.isChecked());
            prefs.setDarkMode(binding.darkToggle.isChecked(), true);
        });
    }

    private void setupFab()
    {
        binding.startMatch.setOnClickListener(view -> {
            try
            {
                MatchData newMatch = new MatchData(requireContext());
                Log.d(TAG, "Creating new match: " + newMatch.getMatchID());
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
        Log.d(TAG, "setupSortSpinner");
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.date_array, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sortOptions.setAdapter(sortAdapter);

        binding.sortOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selection = parent.getItemAtPosition(position).toString();
                MatchListData data = MatchListData.get(requireContext());

                if (selection.equals("Newest"))
                {
                    m_displayedMatches = data.sortByTimestamp2(m_displayedMatches);
                }
                else if (selection.equals("Oldest"))
                {
                    m_displayedMatches = data.sortByTimestamp1(m_displayedMatches);
                }

                m_adapter.clear();
                m_adapter.addAll(m_displayedMatches);
                m_adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "onNothingSelected");
            }
        });
    }

    private void setupFilterButton()
    {
        binding.filterText.setOnClickListener(v -> MatchFilterDialog.newInstance().show(requireActivity().getSupportFragmentManager(), "filter_dialog"));
    }

    @Override
    public void onListItemClick(@NonNull ListView lView, @NonNull View view, int position, long id)
    {
        Log.d(TAG, "onListItemClick");
        MatchData mData = m_adapter.getItem(position);
        if (mData != null)
        {
            QRFragment.newInstance(mData).show(requireActivity().getSupportFragmentManager(), QRTAG);
        }
    }

    private void setupMenuProvider()
    {
        requireActivity().addMenuProvider(new MenuProvider()
        {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
            {
                Log.d(TAG, "onCreateMenu");
                inflater.inflate(R.menu.settings_context_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item)
            {
                Log.d(TAG, "onMenuItemSelected");
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                int itemID = item.getItemId();

                if (itemID == R.id.set_team_index)
                {
                    SetTeamIndexDialog.newInstance().show(fm, "set_team_index_dialog");
                }
                else if (itemID == R.id.load_event_data_tba)
                {
                    LoadEventDialog.newInstance().show(fm, "load_event_dialog");
                }
                else if (itemID == R.id.load_aliases)
                {
                    LoadAliasesDialog.newInstance().show(fm, "load_aliases");
                }
                else if (itemID == R.id.delete_event_data_tba)
                {
                    deleteTbaMatchFiles();
                }
                else if (itemID == R.id.clear_scout_names)
                {
                    Settings.get(requireContext()).clear();
                }
                else if (itemID == R.id.about_screen)
                {
                    startActivity(new Intent(getActivity(), SplashScreenActivity.class));
                    requireActivity().finish();
                }
                return true;
            }

            private void deleteTbaMatchFiles()
            {
                Context context = requireContext();
                File dataDir = context.getFilesDir();
                File[] fileList = dataDir.listFiles();
                int deletedCount = 0;
                StringBuilder deletedFiles = new StringBuilder();

                if (fileList != null)
                {
                    for (File f : fileList)
                    {
                        if (f.getName().contains("matches.json"))
                        {
                            if (f.delete())
                            {
                                deletedCount++;
                                deletedFiles.append(f.getName()).append("\n");
                            }
                        }
                    }
                }

                if (deletedCount > 0)
                {
                    CompetitionInfo.clear();
                    Toast.makeText(context, "Deleted:\n" + deletedFiles, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(context, "No match files found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class MatchAdapter extends ArrayAdapter<MatchData>
    {
        public MatchAdapter(List<MatchData> matchData)
        {
            super(requireActivity(), 0, matchData);
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            View view = convertView;
            if (view == null)
            {
                view = requireActivity().getLayoutInflater().inflate(R.layout.match_list_item, null);
            }
            MatchData m = getItem(position);

            if (m != null)
            {
                TextView mMatchSummary = view.findViewById(R.id.match_tag_display);
                String tStr = String.format("%s-%s-%s-%s",
                        m.getEventCode(),
                        m.getMatchNumber(),
                        m.getTeamNumber(),
                        getFormattedDate(m.getTimestamp()));

                mMatchSummary.setText(tStr);
                mMatchSummary.setTextColor(ContextCompat.getColor(requireContext(), R.color.textPrimary));
            }
            return view;
        }
    }

    /**
     * Formats a date into a standardized string for the match list UI.
     */
    private static String getFormattedDate(Date date)
    {
        if (date == null)
        {
            return "unknown";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        return sdf.format(date);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyMenu");
        binding = null;
    }

    /// /// Set up the match's context menu (Edit match / Delete).
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        requireActivity().getMenuInflater().inflate(R.menu.match_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = Objects.requireNonNull(info).position;
        MatchData m = m_adapter.getItem(position);

        int itemID = item.getItemId();
        if (itemID == R.id.menu_item_delete_match)
        {
            Log.d(TAG, "Delete match button clicked");
            m_displayedMatches.remove(m);
            MatchListData.get(getActivity()).deleteMatch(m);
            m_adapter.notifyDataSetChanged();
        }
        else if (itemID == R.id.edit_match_button)
        {
            Log.d(TAG, "Edit match button clicked");

            m_adapter = (MatchAdapter) getListAdapter();

            Intent data = new Intent(getActivity(), PreMatchActivity.class);
            data.putExtra("match_ID", Objects.requireNonNull(m).getMatchID());
            data.putExtra("in_edit", "yes");
            getListView().clearFocus();

            startActivity(data);
        }
        else
        {
            return super.onContextItemSelected(item);
        }
        return true;
    }
}
