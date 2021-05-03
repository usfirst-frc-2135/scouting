package com.frc2135.android.frc_scout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

public class FilterDialog extends DialogFragment {

    private static final String TAG = "FilterDialog";
    private CheckBox mTeamCheckbox;
    private CheckBox mCompetitionCheckbox;
    private CheckBox mScoutCheckbox;
    private CheckBox mMatchCheckbox;

    private boolean filterTeam;
    private boolean filterCompetition;
    private boolean filterScout;
    private boolean filterMatch;

    private Spinner mTeamSpinner;
    private Spinner mCompetitionSpinner;
    private Spinner mScoutSpinner;

    private EditText mMatchEditText;

    private Bundle b;

    public Dialog onCreateDialog(Bundle SavedInstanceState){
        setCancelable(false);

        Log.i(TAG, "onCreateDialog called");

        View v = getActivity().getLayoutInflater().inflate(R.layout.filter_dialog,null);

        filterTeam = false;
        filterCompetition = false;
        filterScout = false;
        filterMatch = false;

        mTeamCheckbox = (CheckBox)v.findViewById(R.id.team_select);
        mTeamCheckbox.setChecked(false);
        mTeamCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterTeam = isChecked;
                mTeamSpinner.setEnabled(filterTeam);
            }
        });

        mTeamSpinner = (Spinner)v.findViewById(R.id.team_options);
        mTeamSpinner.setEnabled(filterTeam);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity() , android.R.layout.select_dialog_item, MatchHistory.get(getActivity()).listTeams());
        mTeamSpinner.setAdapter(adapter);

        mMatchCheckbox = (CheckBox)v.findViewById(R.id.match_select);
        mMatchCheckbox.setChecked(false);
        mMatchCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterMatch = isChecked;
                mMatchEditText.setEnabled(filterMatch);
            }
        });

        mMatchEditText = (EditText)v.findViewById(R.id.match_entry);
        mMatchEditText.setEnabled(filterMatch);
        mMatchEditText.setHint("Enter match number");

        mCompetitionCheckbox = (CheckBox)v.findViewById(R.id.competition_select);
        mCompetitionCheckbox.setChecked(false);
        mCompetitionCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterCompetition = isChecked;
                mCompetitionSpinner.setEnabled(filterCompetition);
            }
        });

        mCompetitionSpinner = (Spinner)v.findViewById(R.id.competition_options);
        mCompetitionSpinner.setEnabled(filterCompetition);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (getActivity() , android.R.layout.select_dialog_item, MatchHistory.get(getActivity()).listCompetitions());
        mCompetitionSpinner.setAdapter(adapter1);

        mScoutCheckbox = (CheckBox)v.findViewById(R.id.scout_select);
        mScoutCheckbox.setChecked(false);
        mScoutCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterScout = isChecked;
                mScoutSpinner.setEnabled(filterScout);
            }
        });

        mScoutSpinner = (Spinner)v.findViewById(R.id.scout_options);
        mScoutSpinner.setEnabled(filterScout);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                (getActivity() , android.R.layout.select_dialog_item, MatchHistory.get(getActivity()).listScouts());
        mScoutSpinner.setAdapter(adapter2);



        return new AlertDialog.Builder(getActivity()).setView(v).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                sendResult(Activity.RESULT_OK);
            }
        }).create();
    }

    public static FilterDialog newInstance(){
        Log.i(TAG, "newInstance() called");
        Bundle args = new Bundle();

        FilterDialog dialog = new FilterDialog();
        dialog.setArguments(args);

        return dialog;
    }

    private void sendResult(int resultCode){
        Log.i(TAG, "sendResult() called");
        Intent i = new Intent(getActivity(), MatchListActivity.class);
        if(filterTeam){
            Log.d(TAG, mTeamSpinner.getSelectedItem().toString());
            i.putExtra("team", mTeamSpinner.getSelectedItem().toString());
            Log.d(TAG, "I want to filter by team");
        }if(filterScout){
            i.putExtra("scout", mScoutSpinner.getSelectedItem().toString());
            Log.d(TAG, "I want to filter by scout");
        }if(filterCompetition){
            Log.d(TAG, mCompetitionSpinner.getSelectedItem().toString());
            i.putExtra("competition", mCompetitionSpinner.getSelectedItem().toString());
            Log.d(TAG, "I want to filter by competition");
        }if(filterMatch){
            i.putExtra("match", mMatchEditText.getText().toString());
            Log.d(TAG, "I want to filter by match");
        }
        startActivity(i);
        getTargetFragment().getActivity().finish();
    }
}


