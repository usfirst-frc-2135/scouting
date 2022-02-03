package com.frc2135.android.frc_scout;

import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
    private ArrayList<MatchData> mMatchData;
    private boolean mIsSubtitleShown;  // TODO Is this ever usd????
    private ListView mListView;
    private Button mAddMatchButton;
    private Spinner mSortSpinner;
    private Button mFilter;
    private ArrayList<MatchData> mDisplayedMatches;
    private MatchAdapter mAdapter;


    private static final int REQUEST_SIGNIN = 1;
    public static final String SITAG = "sign/in";

    private static final int REQUEST_QR = 2;
    public static final String QRTAG = "qr";

    @Override
    public void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

         mMatchData = MatchHistory.get(getActivity()).getMatches();

        setHasOptionsMenu(true); //alerts the fragment manager that the it should receive options menu callbacks

        setRetainInstance(true);
        mIsSubtitleShown = false;

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Recorded Matches");

        Log.i(TAG, "files directory = "+getContext().getFilesDir()+"");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        mDisplayedMatches = MatchHistory.get(getActivity()).sortByTimestamp2(MatchHistory.get(getContext()).getMatches());
        Log.d(TAG, "Initial file search: displayedMatches size = "+mDisplayedMatches.size()+"");
        mAdapter = new MatchAdapter(mDisplayedMatches);
        Intent intent = getActivity().getIntent();
        if(intent.hasExtra("team")){
            mAdapter = new MatchAdapter(MatchHistory.get(getContext()).filterByTeam(mDisplayedMatches, intent.getStringExtra("team")));
            mDisplayedMatches = MatchHistory.get(getContext()).filterByTeam(mDisplayedMatches, intent.getStringExtra("team"));
            Log.d(TAG, "Filtered by team: displayedMatches size = "+mDisplayedMatches.size()+"");
        }
        if(intent.hasExtra("competition")){
            mAdapter = new MatchAdapter(MatchHistory.get(getContext()).filterByCompetition(mDisplayedMatches, intent.getStringExtra("competition")));
            mDisplayedMatches = MatchHistory.get(getContext()).filterByCompetition(mDisplayedMatches, intent.getStringExtra("competition"));
            Log.d(TAG, "Filtered by competition: displayedMatches size = "+mDisplayedMatches.size()+"");
        }
        if(intent.hasExtra("scout")){
            mAdapter = new MatchAdapter(MatchHistory.get(getContext()).filterByScout(mDisplayedMatches, intent.getStringExtra("scout")));
            mDisplayedMatches = MatchHistory.get(getContext()).filterByScout(mDisplayedMatches, intent.getStringExtra("scout"));
            Log.d(TAG, "Filtered by scout: displayedMatches size = "+mDisplayedMatches.size()+"");
        }
        if(intent.hasExtra("match")){
            mAdapter = new MatchAdapter(MatchHistory.get(getContext()).filterByMatchNumber(mDisplayedMatches, intent.getStringExtra("match")));
            mDisplayedMatches = MatchHistory.get(getContext()).filterByMatchNumber(mDisplayedMatches, intent.getStringExtra("match"));
            Log.d(TAG, "Filtered by match: displayedMatches size = "+mDisplayedMatches.size()+"");
        }

        View v1 = inflater.inflate(R.layout.match_list, parent, false);
        mListView = v1.findViewById(android.R.id.list);
        setListAdapter(mAdapter);
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

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
//REMOVE            mDisplayedMatches.add(matchA);
                    Log.d(TAG, "Added to mDisplayedMatches: "+matchA.getMatchID());
                    mAdapter.notifyDataSetChanged();
                    Intent intentA = new Intent(getActivity(), PreMatchActivity.class);
                    intentA.putExtra("match_ID", matchA.getMatchID());
                    startActivityForResult(intentA, 0); }
            });

        mSortSpinner = v1.findViewById(R.id.sort_options);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getContext(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(adapter1);
        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().equals("Newest")){
                    ArrayList temp = MatchHistory.get(getContext()).sortByTimestamp2(mDisplayedMatches);
                    mDisplayedMatches.clear();
                    for(Object mData: temp){
                        Log.d(TAG, "(Newest) Adding to mDisplayedMatches: "+((MatchData)mData).getMatchID());
                        mDisplayedMatches.add((MatchData)mData);
                    }
                }
                if(parent.getItemAtPosition(position).toString().equals("Oldest")){
                    ArrayList temp = MatchHistory.get(getContext()).sortByTimestamp1(mDisplayedMatches);
                    mDisplayedMatches.clear();
                    for(Object mData: temp){
                        Log.d(TAG, "(Oldest) Adding to mDisplayedMatches: "+((MatchData)mData).getMatchID());
                        mDisplayedMatches.add((MatchData)mData);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFilter = v1.findViewById(R.id.filter_text);
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FilterDialog dialog = FilterDialog.newInstance();
                dialog.setTargetFragment(MatchListFragment.this, REQUEST_FILTER);
                dialog.show(fm, "filter_dialog");
            }
        });


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
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
            registerForContextMenu(mListView);
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
        private Button mAddMatchButton;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_match, null);
            }
            MatchData m = getItem(position);

            TextView mMatchSummary = convertView.findViewById(R.id.match_tag_display);
            mMatchSummary.setText(m.getCompetition()+"-"+m.getMatchNumber()+"-"+m.getTeamNumber()+ "-" + formattedDate(m.getTimestamp()));


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
//REMOVE        Log.d(TAG,"onPause() calling MatchHistory::saveData()");
//REMOVE        MatchHistory.get(getActivity()).saveData();
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
                Log.d(TAG,"onPause() calling MatchHistory::saveData()");
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
                dialog.show(fm, "filter_dialog");
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
        MatchData m = mAdapter.getItem(position);

        switch(item.getItemId()){
            case R.id.menu_item_delete_match:
                Log.d(TAG, "Delete match button clicked");
                mDisplayedMatches.remove(m);
                MatchHistory.get(getActivity()).deleteMatch(m);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.edit_match_button:
                Log.d(TAG, "Edit match button clicked");

                mAdapter = (MatchAdapter)getListAdapter();
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
