package com.frc2135.android.frc_scout;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MatchListFragment extends ListFragment
{

    private static final String TAG = "MatchListFragment";

    private ArrayList<MatchData> m_displayedMatches;
    private MatchAdapter m_adapter;
    private SwitchCompat m_darkToggle;
    public static final String QRTAG = "qr";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ActionBar aBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if(aBar != null) {
            aBar.setTitle("Recorded Matches");
        }
        setMenuProvider();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        m_displayedMatches = MatchHistory.get(getActivity()).sortByTimestamp2(MatchHistory.get(getContext()).getMatches());
        Log.d(TAG, "OnCreateView(): displayedMatches size = " + m_displayedMatches.size() + "");
        m_adapter = new MatchAdapter(m_displayedMatches);
        Intent intent = requireActivity().getIntent();
        if (intent.hasExtra("team"))
        {
            m_adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByTeam(m_displayedMatches, intent.getStringExtra("team")));
            m_displayedMatches = MatchHistory.get(getContext()).filterByTeam(m_displayedMatches, intent.getStringExtra("team"));
            Log.d(TAG, "Filtered by team: displayedMatches size = " + m_displayedMatches.size() + "");
        }
        if (intent.hasExtra("competition"))
        {
            m_adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByCompetition(m_displayedMatches, intent.getStringExtra("competition")));
            m_displayedMatches = MatchHistory.get(getContext()).filterByCompetition(m_displayedMatches, intent.getStringExtra("competition"));
            Log.d(TAG, "Filtered by competition: displayedMatches size = " + m_displayedMatches.size() + "");
        }
        if (intent.hasExtra("scout"))
        {
            m_adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByScout(m_displayedMatches, intent.getStringExtra("scout")));
            m_displayedMatches = MatchHistory.get(getContext()).filterByScout(m_displayedMatches, intent.getStringExtra("scout"));
            Log.d(TAG, "Filtered by scout: displayedMatches size = " + m_displayedMatches.size() + "");
        }
        if (intent.hasExtra("match"))
        {
            m_adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByMatchNumber(m_displayedMatches, intent.getStringExtra("match")));
            m_displayedMatches = MatchHistory.get(getContext()).filterByMatchNumber(m_displayedMatches, intent.getStringExtra("match"));
            Log.d(TAG, "Filtered by match: displayedMatches size = " + m_displayedMatches.size() + "");
        }

        //v1 is the name of this view
        View v1 = inflater.inflate(R.layout.match_list, parent, false);
        ListView listView = v1.findViewById(android.R.id.list);
        setListAdapter(m_adapter);
        listView.setAdapter(m_adapter);
        registerForContextMenu(listView);

        m_darkToggle = v1.findViewById(R.id.dark_toggle);

        // Set light or dark mode based on shared preferences (stored in static ScoutPreferences object).
        boolean bNightMode = ScoutPreferences.get(getActivity()).getNightMode();
        if (bNightMode)
        {
            Log.d(TAG, "Setting up dark mode");
            m_darkToggle.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
            Log.d(TAG, "Setting up light mode");
            m_darkToggle.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        m_darkToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (m_darkToggle.isChecked())
                {
                    Log.d(TAG, "m_darkToggle toggled from light to dark");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    ScoutPreferences.get(getActivity()).setNightMode(true);
                }
                else
                {
                    Log.d(TAG, "m_darkToggle toggled from dark to light");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    ScoutPreferences.get(getActivity()).setNightMode(false);
                }
            }
        });

        // Set up Floating Action Button: the "+" button in a circle to start scouting a match.
        FloatingActionButton fab = v1.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener()
        {
            //Setting an onClickListener makes it so that our button actually senses for when it is clicked, and when it is clicked, it will proceed with onClick()
            @Override
            public void onClick(View view)
            {
                MatchData matchA;
                try
                {
                    matchA = new MatchData(getContext());
                    Log.d(TAG, "Creating new MatchData for matchID: " + matchA.getMatchID());
                    MatchHistory.get(getContext()).addMatch(matchA);
                    Log.d(TAG, "Added to m_displayedMatches: " + matchA.getMatchID());
                    m_adapter.notifyDataSetChanged();
                    Intent intentA = new Intent(getActivity(), PreMatchActivity.class);
                    intentA.putExtra("match_ID", matchA.getMatchID());
                    startActivity(intentA);
                }
                catch (IOException | JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        Spinner sortSpinner = v1.findViewById(R.id.sort_options);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getContext(), R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter1);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (parent.getItemAtPosition(position).toString().equals("Newest"))
                {
                    ArrayList<MatchData> temp = MatchHistory.get(getContext()).sortByTimestamp2(m_displayedMatches);
                    m_displayedMatches.clear();
                    for (MatchData mData : temp)
                    {
                        Log.d(TAG, "(Newest) Adding to m_displayedMatches: " + mData.getMatchID());
                        m_displayedMatches.add(mData);
                    }
                }
                if (parent.getItemAtPosition(position).toString().equals("Oldest"))
                {
                    ArrayList<MatchData> temp = MatchHistory.get(getContext()).sortByTimestamp1(m_displayedMatches);
                    m_displayedMatches.clear();
                    for (MatchData mData : temp)
                    {
                        Log.d(TAG, "(Oldest) Adding to m_displayedMatches: " + mData.getMatchID());
                        m_displayedMatches.add(mData);
                    }
                }
                m_adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // Set up Filter pop-up dialog.
        Button filterButton = v1.findViewById(R.id.filter_text);
        filterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                FilterDialog dialog = FilterDialog.newInstance();
                dialog.show(fm, "filter_dialog");
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
            {
                //Not necessary in our implementation
            }

            ////// Set up the match's context menu (Edit match / Delete).
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                //The action mode configures the contextual action bar, not the activity
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.match_list_item_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                //Not necessary in our implementation
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                if (item.getItemId() == R.id.menu_item_delete_match)
                {
                    // Delete all selected matches.
                    MatchAdapter adapter = (MatchAdapter) getListAdapter();
                    if(adapter != null)
                    {
                        MatchHistory matchHistory = MatchHistory.get(getActivity());
                        int aCount = adapter.getCount();
                        for (int i = aCount - 1; i >= 0; i--) 
                        {
                            if (getListView().isItemChecked(i)) 
                            {
                                matchHistory.deleteMatch(adapter.getItem(i));
                            }
                        }
                        mode.finish();
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {
                //Not necessary in our implementation
            }
        });
        return v1;
    }

    // For click on a row with a match ID: will bring up that match's QR code dialog.
    @Override
    public void onListItemClick(@NonNull ListView lView, @NonNull View view, int position, long id)
    {
        MatchAdapter mAdapter = (MatchAdapter) getListAdapter();
        if(mAdapter != null) {
            MatchData mData = mAdapter.getItem(position);
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            view.setActivated(false);
            QRFragment dialog = QRFragment.newInstance(mData);
            dialog.show(fm, QRTAG);
        }
    }

    ////// Set up the 3-dot options menu in right hand top corner.
    private void setMenuProvider() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.settings_context_menu,menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item)
            {
                int itemID = item.getItemId();
                if(itemID == R.id.clear_preferences)
                {
                    Log.d(TAG, "Clear preferences clicked");
                    Scouter.get(getContext()).clear();
                    Log.d(TAG, "Clear Preferences: calling Scouter::clear()");
                }
                else if(itemID == R.id.about_item)
                {
                    Intent i = new Intent(getActivity(), Splash.class);
                    startActivity(i);
                    requireActivity().finish();
                }
                else if(itemID == R.id.load_data_over_network)
                {
                    FragmentManager fm = requireActivity().getSupportFragmentManager();
                    Log.d(TAG, "Going to start LoadEventDialog");
                    LoadEventDialog dialog = LoadEventDialog.newInstance();
                    dialog.show(fm, "filter_dialog");  //TODO - change this to "load_event_dialog"?
                }
                else if(itemID == R.id.set_team_index)
                {
                    FragmentManager fm2 = requireActivity().getSupportFragmentManager();
                    Log.d(TAG, "Going to start SetTeamIndexDlg");
                    SetTeamIndexDlg tiDlg = SetTeamIndexDlg.newInstance();
                    tiDlg.show(fm2, "set_team_index_dialog");
                }
                return true;
            }
        });
    }
    private class MatchAdapter extends ArrayAdapter<MatchData>
    {
        public MatchAdapter(ArrayList<MatchData> matchData)
        {
            super(getActivity(), 0, matchData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = requireActivity().getLayoutInflater().inflate(R.layout.list_item_match, null);
            }
            MatchData m = getItem(position);

            TextView mMatchSummary = convertView.findViewById(R.id.match_tag_display);
            String tStr = m.getCompetition() + "-" + m.getMatchNumber() + "-" + m.getTeamNumber() + "-" + formattedDate(m.getTimestamp());
            mMatchSummary.setText(tStr);
            // Changes text color of the ListView Match List to fit the light/dark mode theme.
            mMatchSummary.setTextColor(ContextCompat.getColor(getContext(),R.color.textPrimary));

            return convertView;
        }
    }

    public String formattedDate(Date myDate)
    {
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy", Locale.US);
        Date date = null;
        try
        {
            date = dt.parse(myDate.toString());
        }
        catch (Exception err)
        {
            Log.d("formattedDate() error: ", err.getMessage());
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-M-dd hh:mm:ss",Locale.US);
        if (date == null)
        {
            return null;
        }
        else
        {
            return (dt1.format(date).substring(0, 9) + "T" + dt1.format(date).substring(10));
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    ////// Set up the match's context menu (Edit match / Delete).
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        requireActivity().getMenuInflater().inflate(R.menu.match_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        MatchData m = m_adapter.getItem(position);

        int itemID = item.getItemId();
        if(itemID == R.id.menu_item_delete_match)
        {
            Log.d(TAG, "Delete match button clicked");
            m_displayedMatches.remove(m);
            MatchHistory.get(getActivity()).deleteMatch(m);
            m_adapter.notifyDataSetChanged();
        }
        else if(itemID == R.id.edit_match_button)
        {
            Log.d(TAG, "Edit match button clicked");

            m_adapter = (MatchAdapter) getListAdapter();

            Intent data = new Intent(getActivity(), PreMatchActivity.class);
            data.putExtra("match_ID", m.getMatchID());
            getListView().clearFocus();

            startActivity(data);
            //Makes data editable once more
        }
        else return super.onContextItemSelected(item);
        return true;
    }
}
