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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class MatchListFragment extends ListFragment {

    private static final String TAG = "MatchListFragment";
    private static int REQUEST_FILTER = 300;
    private ArrayList<MatchData> mMatchData;
    private boolean isSubtitleShown;
    private ListView mListView;
    private Button mAddMatchButton;
    private Spinner mSortSpinner;
    private Button mFilter;
    private ArrayList<MatchData> displayedMatches;

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
        isSubtitleShown = false;

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Recorded Matches");

        Log.i(TAG, getContext().getFilesDir()+"");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        displayedMatches = MatchHistory.get(getActivity()).sortByTimestamp2(MatchHistory.get(getContext()).getMatches());
        MatchAdapter adapter = new MatchAdapter(displayedMatches);
        Intent i = getActivity().getIntent();
        if(i.hasExtra("team")){
            adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByTeam(displayedMatches, i.getStringExtra("team")));
            displayedMatches = MatchHistory.get(getContext()).filterByTeam(displayedMatches, i.getStringExtra("team"));
            Log.d(TAG, "Filtered by team");
            Log.d(TAG, displayedMatches.size()+"");
        }
        if(i.hasExtra("competition")){
            for(MatchData x: displayedMatches){
                Log.d(TAG, x.getCompetition().toString());
            }
            Log.d(TAG,"*****"+ i.getStringExtra("competition")+"******");
            adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByCompetition(displayedMatches, i.getStringExtra("competition")));
            displayedMatches = MatchHistory.get(getContext()).filterByCompetition(displayedMatches, i.getStringExtra("competition"));
            Log.d(TAG, "Filtered by competition");
            for(MatchData x: displayedMatches){
                Log.d(TAG, x.getTimestamp().toString());
            }
        }
        if(i.hasExtra("scout")){
            adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByScout(displayedMatches, i.getStringExtra("scout")));
            displayedMatches = MatchHistory.get(getContext()).filterByScout(displayedMatches, i.getStringExtra("scout"));
            Log.d(TAG, "Filtered by scout");
            for(MatchData x: displayedMatches){
                Log.d(TAG, x.getTimestamp().toString());
            }
        }
        if(i.hasExtra("match")){
            adapter = new MatchAdapter(MatchHistory.get(getContext()).filterByMatchNumber(displayedMatches, i.getStringExtra("match")));
            displayedMatches = MatchHistory.get(getContext()).filterByMatchNumber(displayedMatches, i.getStringExtra("match"));
            Log.d(TAG, "Filtered by match");
            for(MatchData x: displayedMatches){
                Log.d(TAG, x.getTimestamp().toString());
            }
        }

        View v1 = inflater.inflate(R.layout.match_list, parent, false);
        mListView = v1.findViewById(android.R.id.list);
        setListAdapter(adapter);
        mListView.setAdapter(adapter);
        registerForContextMenu(mListView);

        FloatingActionButton fab = (FloatingActionButton)v1.findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        if(MatchHistory.get(getActivity()).getMatches().size()!=0){
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                //Setting an onClickListener makes it so that our button actually senses for when it is clicked, and when it is clicked, it will proceed with onClick()
                @Override
                public void onClick(View view) {
                    MatchAdapter adapter = ((MatchAdapter)getListAdapter());
                    MatchData m = new MatchData(getContext());
                    MatchHistory.get(getContext()).addMatch(m);
                    displayedMatches.add(m);
                    adapter.notifyDataSetChanged();
                    Intent i = new Intent(getActivity(), PreMatchActivity.class);
                    i.putExtra("match_ID", m.getMatchID());
                    startActivityForResult(i, 0);
                }
            });
        }

        mSortSpinner = (Spinner)v1.findViewById(R.id.sort_options);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getContext(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(adapter1);


        mFilter = (Button) v1.findViewById(R.id.filter_text);
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FilterDialog dialog = FilterDialog.newInstance();
                dialog.setTargetFragment(MatchListFragment.this, REQUEST_FILTER);
                dialog.show(fm, "filter_dialog");
            }
        });

        mAddMatchButton = (Button)v1.findViewById(R.id.empty_button);
        mAddMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchAdapter adapter = ((MatchAdapter)getListAdapter());
                MatchData m = new MatchData(getContext());
                MatchHistory.get(getContext()).addMatch(m);
                displayedMatches.add(m);
                adapter.notifyDataSetChanged();
                Intent i = new Intent(getActivity(), PreMatchActivity.class);
                i.putExtra("match_ID", m.getMatchID());
                startActivityForResult(i, 0);
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

            TextView mMatchSummary = (TextView)convertView.findViewById(R.id.match_tag_display);
            mMatchSummary.setText(m.getCompetition()+"-"+m.getTeamNumber()+"-"+m.getMatchNumber()+ "-" + formattedDate(m.getTimestamp()));


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
        SimpleDateFormat dt1 = new SimpleDateFormat("MMM dd hh:mm:ss");

        if(date == null) {
            return null;
        }
        else { return (dt1.format(date)); }
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
        MatchHistory.get(getActivity()).saveData();
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
                Log.d(TAG, "Clear clicked");
                Scouter.get(getContext()).clear();
                Scouter.get(getContext()).saveData(getContext());
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
        MatchAdapter adapter = ((MatchAdapter)getListAdapter());
        MatchData m = adapter.getItem(position);

        switch(item.getItemId()){
            case R.id.menu_item_delete_match:
                Log.d(TAG, "Delete match button clicked");
                displayedMatches.remove(m);
                MatchHistory.get(getActivity()).deleteMatch(m);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.edit_match_button:
                Log.d(TAG, "Edit match button clicked");

                adapter = (MatchAdapter)getListAdapter();
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