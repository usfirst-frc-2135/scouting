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
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class MatchListFragment extends ListFragment {

    private static final String TAG = "MatchListFragment";
    private ArrayList<MatchData> mMatchData;
    private boolean isSubtitleShown;
    private ListView mListView;
    private Button mAddCrimeButton;
    //private Scouter mScout;
    //private String scoutName;
    //private String scoutDate;


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

        if(Scouter.get(getContext()).getName().equals("") || Scouter.get(getContext()).getDate().equals("")){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Recorded Matches");
            FragmentManager fm = getActivity().getSupportFragmentManager();
            SignInFragment dialog = SignInFragment.newInstance();
            dialog.setTargetFragment(MatchListFragment.this, REQUEST_SIGNIN);
            dialog.show(fm, SITAG);

        }
        else{
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Recorded Matches- " + Scouter.get(getContext()).getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        mMatchData = MatchHistory.get(getActivity()).getMatches();
        MatchAdapter adapter = new MatchAdapter(mMatchData);
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
                    Intent i = new Intent(getActivity(), ScoutingActivity.class);
                    startActivityForResult(i, 0);
                }
            });
        }




        mAddCrimeButton = (Button)v1.findViewById(R.id.empty_button);
        mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), ScoutingActivity.class);
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

        Log.d(TAG, Scouter.get(getContext()).getName());



        return v1;
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        MatchData m = ((MatchAdapter)getListAdapter()).getItem(position);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        QRFragment dialog = QRFragment.newInstance(m);
        dialog.setTargetFragment(MatchListFragment.this, REQUEST_QR);
        dialog.show(fm, QRTAG);
    }

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
        SimpleDateFormat dt1 = new SimpleDateFormat("hh:mm:ss");

        if(date == null) {
            return null;
        }
        else { return (dt1.format(date)); }
    }

    @Override
    public void onResume(){
        super.onResume();
        ((MatchAdapter)getListAdapter()).notifyDataSetChanged();
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

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.popup_menu:
                PopupMenu popup = new PopupMenu(getContext(), getView());
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.settings_context_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sign_out_item:
                                Log.d(TAG, "Sign out clicked");
                                Scouter.get(getContext()).setName("");
                                Scouter.get(getContext()).setDate("");
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                SignInFragment dialog = SignInFragment.newInstance();
                                dialog.setTargetFragment(MatchListFragment.this, REQUEST_SIGNIN);
                                dialog.show(fm, SITAG);
                                return true;
                            case R.id.assign_item:
                                // do your code
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                Log.d(TAG, "Pop up Menu created.");
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.sign_out_item:
                Log.d(TAG, "Sign out clicked");
                Scouter.get(getContext()).setName("");
                Scouter.get(getContext()).setDate("");
                FragmentManager fm = getActivity().getSupportFragmentManager();
                SignInFragment dialog = SignInFragment.newInstance();
                dialog.setTargetFragment(MatchListFragment.this, REQUEST_SIGNIN);
                dialog.show(fm, SITAG);
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
