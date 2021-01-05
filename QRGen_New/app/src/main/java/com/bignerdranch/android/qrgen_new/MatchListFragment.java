package com.bignerdranch.android.qrgen_new;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;

public class MatchListFragment extends ListFragment {

    private static final String TAG = "MatchListFragment";
    private ArrayList<MatchData> mMatchData;
    private boolean isSubtitleShown;
    private ListView mListView;
    private Button mAddCrimeButton;


    @Override
    public void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Recorded Matches");
         mMatchData = MatchHistory.get(getActivity()).getMatches();

        //CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        //setListAdapter(adapter);
        setHasOptionsMenu(true); //alerts the fragment manager that the it should receive options menu callbacks

        setRetainInstance(true);
        isSubtitleShown = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){

        MatchAdapter adapter = new MatchAdapter(mMatchData);
        View v1 = inflater.inflate(R.layout.match_list, parent, false);
        mListView = v1.findViewById(android.R.id.list);
        setListAdapter(adapter);
        mListView.setAdapter(adapter);
        registerForContextMenu(mListView);

        mAddCrimeButton = (Button)v1.findViewById(R.id.empty_button);
        mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivityForResult(i, 0);
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
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
                        case R.id.menu_item_delete_crime:
                            MatchAdapter adapter = (MatchAdapter)getListAdapter();
                            MatchHistory matchHistory = MatchHistory.get(getActivity());
                            for(int i = adapter.getCount()-1; i>=0; i--){
                                if(getListView().isItemChecked(i)){
                                    matchHistory.deleteMatch(adapter.getItem(i));
                                    //Deletes all selected crimes
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
    //@Override
    /*public void onListItemClick(ListView l, View v, int position, long id){
        MatchData m = ((MatchAdapter)getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), MainActivity.class);
        i.putExtra("fun", "works");
        i.putExtra(MatchFragment.EXTRA_MATCH_ID, m.getId());
        startActivity(i);
    }*/

    private class MatchAdapter extends ArrayAdapter<MatchData> {
        public MatchAdapter(ArrayList<MatchData> matchData){
            super(getActivity(), 0, matchData);
        }
        private Button mAddCrimeButton;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_match, null);
            }
            MatchData m = getItem(position);

            Log.d("MatchListFragment", m.getId()+"");
            Log.d("MatchListFragment", m.getHighPoints()+"");
            Log.d("MatchListFragment", m.getLowPoints()+"");


            TextView mHighPointsDisplay = (TextView)convertView.findViewById(R.id.highpointsdisplay);
            mHighPointsDisplay.setText(m.getHighPoints()+"");

            TextView mLowPointsDisplay = (TextView)convertView.findViewById(R.id.lowpointsdisplay);
            mLowPointsDisplay.setText(m.getLowPoints()+"");

            TextView mInitLineDisplay = (TextView)convertView.findViewById(R.id.initlinedisplay);
            mInitLineDisplay.setText(m.getPassedInitLine()+"");

            TextView mDefenseDisplay = (TextView)convertView.findViewById(R.id.defensedisplay);
            mDefenseDisplay.setText(m.getDefense()+"");

            TextView mCommentsDisplay = (TextView)convertView.findViewById(R.id.commentsdisplay);
            mCommentsDisplay.setText(m.getExtComments());



            return convertView;
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        ((MatchAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onPause(){
        super.onPause();
        MatchHistory.get(getActivity()).saveMatches();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.match_crime_list, menu);
        //This tells the program to inflate the view for the menu when the method is called.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_item_new_match:
                //This controls the button on the menu which allows users to add crimes.
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivityForResult(i, 0);
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
            case R.id.menu_item_delete_crime:
                MatchHistory.get(getActivity()).deleteMatch(m);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }


}
