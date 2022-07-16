package com.frc2135.android.frc_scout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MatchListFragment extends ListFragment {

    private static final String TAG = "MatchListFragment";
    private static final int REQUEST_FILTER = 300;
    private static final int REQUEST_LOADEVENT = 400;
    private static final int REQUEST_SETTEAMINDEX = 500;

    // Data members
    private ArrayList<MatchData> m_MatchDataList; 
    private Button               m_AddMatchButton;  
    private ListView             m_listView;
    private Spinner              m_sortSpinner;
    private Button               m_filterButton;
    private ArrayList<MatchData> m_displayedMatches;
    private MatchAdapter         m_adapter;
    private SwitchCompat         m_darkToggle;

    private static final int REQUEST_QR = 2;
    public static final String QRTAG = "qr";

    @Override
    public void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        m_MatchDataList = MatchHistory.get(getActivity()).getMatches();

        setHasOptionsMenu(true); //alerts the fragment manager that the it should receive options menu callbacks

        setRetainInstance(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Recorded Matches");

        Log.i(TAG, "files directory = "+getContext().getFilesDir()+"");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        m_displayedMatches = MatchHistory.get(getActivity()).sortByTimestamp2(MatchHistory.get(getContext()).getMatches());
        Log.d(TAG, "Initial file search: displayedMatches size = "+m_displayedMatches.size()+"");
        m_adapter = new MatchAdapter(m_displayedMatches);
        Intent intent = getActivity().getIntent();
        if(intent.hasExtra("team")){
            m_adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByTeam(m_displayedMatches, intent.getStringExtra("team")));
            m_displayedMatches = MatchHistory.get(getContext()).filterByTeam(m_displayedMatches, intent.getStringExtra("team"));
            Log.d(TAG, "Filtered by team: displayedMatches size = "+m_displayedMatches.size()+"");
        }
        if(intent.hasExtra("competition")){
            m_adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByCompetition(m_displayedMatches, intent.getStringExtra("competition")));
            m_displayedMatches = MatchHistory.get(getContext()).filterByCompetition(m_displayedMatches, intent.getStringExtra("competition"));
            Log.d(TAG, "Filtered by competition: displayedMatches size = "+m_displayedMatches.size()+"");
        }
        if(intent.hasExtra("scout")){
            m_adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByScout(m_displayedMatches, intent.getStringExtra("scout")));
            m_displayedMatches = MatchHistory.get(getContext()).filterByScout(m_displayedMatches, intent.getStringExtra("scout"));
            Log.d(TAG, "Filtered by scout: displayedMatches size = "+m_displayedMatches.size()+"");
        }
        if(intent.hasExtra("match")){
            m_adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByMatchNumber(m_displayedMatches, intent.getStringExtra("match")));
            m_displayedMatches = MatchHistory.get(getContext()).filterByMatchNumber(m_displayedMatches, intent.getStringExtra("match"));
            Log.d(TAG, "Filtered by match: displayedMatches size = "+m_displayedMatches.size()+"");
        }

        //v1 is the name of this view
        View v1 = inflater.inflate(R.layout.match_list, parent, false);
        m_listView = v1.findViewById(android.R.id.list);
        setListAdapter(m_adapter);
        m_listView.setAdapter(m_adapter);
        registerForContextMenu(m_listView);

        m_darkToggle = v1.findViewById(R.id.dark_toggle);

        // Set light or dark mode based on shared preferences (stored in static ScoutPreferences object).
        boolean bNightMode = ScoutPreferences.get(getActivity()).getNightMode();
        if (bNightMode){
            Log.d(TAG,"Setting up dark mode");
            m_darkToggle.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } 
        else {
            Log.d(TAG,"Setting up light mode");
            m_darkToggle.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        m_darkToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (m_darkToggle.isChecked()) {
                    Log.d(TAG,"m_darkToggle toggled from light to dark");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    ScoutPreferences.get(getActivity()).setNightMode(true);
                }
                else{
                    Log.d(TAG,"m_darkToggle toggled from dark to light");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    ScoutPreferences.get(getActivity()).setNightMode(false);
                }
            }
        });

        //Note: Floating Action Button is the + button in a circle to start scouting
        FloatingActionButton fab = v1.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
                //Setting an onClickListener makes it so that our button actually senses for when it is clicked, and when it is clicked, it will proceed with onClick()
                @Override
                public void onClick(View view) {
                    MatchData matchA = null;
                    try {
                        matchA = new MatchData(getContext());
                        Log.d(TAG, "Creating new MatchData for matchID: "+matchA.getMatchID());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    MatchHistory.get(getContext()).addMatch(matchA);
                    Log.d(TAG, "Added to m_displayedMatches: "+matchA.getMatchID());
                    m_adapter.notifyDataSetChanged();
                    Intent intentA = new Intent(getActivity(), PreMatchActivity.class);
                    intentA.putExtra("match_ID", matchA.getMatchID());
                    startActivityForResult(intentA, 0); }
            });

        m_sortSpinner = v1.findViewById(R.id.sort_options);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getContext(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_sortSpinner.setAdapter(adapter1);
        m_sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().equals("Newest")){
                    ArrayList temp = MatchHistory.get(getContext()).sortByTimestamp2(m_displayedMatches);
                    m_displayedMatches.clear();
                    for(Object mData: temp){
                        Log.d(TAG, "(Newest) Adding to m_displayedMatches: "+((MatchData)mData).getMatchID());
                        m_displayedMatches.add((MatchData)mData);
                    }
                }
                if(parent.getItemAtPosition(position).toString().equals("Oldest")){
                    ArrayList temp = MatchHistory.get(getContext()).sortByTimestamp1(m_displayedMatches);
                    m_displayedMatches.clear();
                    for(Object mData: temp){
                        Log.d(TAG, "(Oldest) Adding to m_displayedMatches: "+((MatchData)mData).getMatchID());
                        m_displayedMatches.add((MatchData)mData);
                    }
                }
                m_adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        m_filterButton = v1.findViewById(R.id.filter_text);
        m_filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FilterDialog dialog = FilterDialog.newInstance();
                dialog.setTargetFragment(MatchListFragment.this, REQUEST_FILTER);
                dialog.show(fm, "filter_dialog");
            }
        });


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            m_listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            m_listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    //Not necessary in our implementation
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    //The action mode configures the contextual action bar, not the activity
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.match_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    //Not necessary in our implementation
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.menu_item_delete_match:
                            MatchAdapter adapter = (MatchAdapter)getListAdapter();
                            MatchHistory matchHistory = MatchHistory.get(getActivity());
                            for(int i = adapter.getCount()-1; i>=0; i--){
                                if(getListView().isItemChecked(i)){
                                    matchHistory.deleteMatch(adapter.getItem(i));
                                    //Deletes all selected matches
                                }
                            }
                            mode.finish();
                            adapter.notifyDataSetChanged();
                            return true;


                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    //Not necessary in our implementation
                }
            });
        }
        else{
            registerForContextMenu(m_listView);
        }
        return v1;
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        MatchData m = ((MatchAdapter)getListAdapter()).getItem(position);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        v.setActivated(false);
        QRFragment dialog = QRFragment.newInstance(m);
        dialog.setTargetFragment(MatchListFragment.this, REQUEST_QR);
        dialog.show(fm, QRTAG);
    }

    private class MatchAdapter extends ArrayAdapter<MatchData> {
        public MatchAdapter(ArrayList<MatchData> matchData){
            super(getActivity(), 0, matchData);
        }
        private Button m_AddMatchButton;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_match, null);
            }
            MatchData m = getItem(position);

            TextView mMatchSummary = convertView.findViewById(R.id.match_tag_display);
            mMatchSummary.setText(m.getCompetition()+"-"+m.getMatchNumber()+"-"+m.getTeamNumber()+ "-" + formattedDate(m.getTimestamp()));
            //changes text color of the ListView Match List to fit the light/dark mode theme
            mMatchSummary.setTextColor(getResources().getColor(R.color.textPrimary));

            return convertView;
        }

    }

    public String formattedDate(Date d){
        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy");
        Date date = null;
        try{
            date=dt.parse(d.toString());
        }catch(Exception e){
            Log.d("SignInFragment", e.getMessage());
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

        if(date == null) {
            return null;
        }
        else { return (dt1.format(date).substring(0,9)+"T"+dt1.format(date).substring(10)); }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause(){
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings_context_menu, menu);
        //This tells the program to inflate the view for the menu when the method is called.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.clear_preferences:
                Log.d(TAG, "Clear preferences clicked");
                Scouter.get(getContext()).clear();
                Log.d(TAG,"Clear Preferences: calling Scouter::clear()");
                return true;

            case R.id.about_item:
                Intent i = new Intent(getActivity(), Splash.class);
                startActivity(i);
                getActivity().finish();

            case R.id.load_data_over_network:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Log.d(TAG, "Going to start LoadEventDialog");
                LoadEventDialog dialog = LoadEventDialog.newInstance();
                dialog.setTargetFragment(MatchListFragment.this, REQUEST_LOADEVENT);
                dialog.show(fm, "filter_dialog");  //TODO - change this to "load_event_dialog"?
                return true;

            case R.id.set_team_index:
                FragmentManager fm2 = getActivity().getSupportFragmentManager();
                Log.d(TAG, "Going to start SetTeamIndexDlg");
                SetTeamIndexDlg tiDlg = SetTeamIndexDlg.newInstance();
                tiDlg.setTargetFragment(MatchListFragment.this, REQUEST_SETTEAMINDEX);
                tiDlg.show(fm2, "set_team_index_dialog");
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        getActivity().getMenuInflater().inflate(R.menu.match_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        MatchData m = m_adapter.getItem(position);

        switch(item.getItemId()){
            case R.id.menu_item_delete_match:
                Log.d(TAG, "Delete match button clicked");
                m_displayedMatches.remove(m);
                MatchHistory.get(getActivity()).deleteMatch(m);
                m_adapter.notifyDataSetChanged();
                return true;
            case R.id.edit_match_button:
                Log.d(TAG, "Edit match button clicked");

                m_adapter = (MatchAdapter)getListAdapter();
                MatchHistory matchHistory = MatchHistory.get(getActivity());

                Intent data = new Intent(getActivity(), PreMatchActivity.class);
                data.putExtra("match_ID", m.getMatchID());
                getListView().clearFocus();

                startActivity(data);
                //Makes data editable once more
                return true;
        }
        return super.onContextItemSelected(item);
    }







}
