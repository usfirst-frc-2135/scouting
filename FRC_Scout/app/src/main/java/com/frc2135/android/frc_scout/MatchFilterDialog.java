package com.frc2135.android.frc_scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;


public class MatchFilterDialog extends DialogFragment
{

    private static final String TAG = "MatchFilterDialog";

    private boolean m_bFilterTeam;
    private boolean m_bFilterEvent;
    private boolean m_bFilterScout;
    private boolean m_bFilterMatch;
    private Spinner m_TeamSpinner;
    private Spinner m_matchListSpinner;
    private Spinner m_ScoutSpinner;
    private EditText m_MatchEditText;

    @NonNull
    public Dialog onCreateDialog(Bundle SavedInstanceState)
    {
        setCancelable(false);

        Log.i(TAG, "onCreateDialog called");

        View v = requireActivity().getLayoutInflater().inflate(R.layout.match_filter_dialog, null);

        m_bFilterTeam = false;
        m_bFilterEvent = false;
        m_bFilterScout = false;
        m_bFilterMatch = false;

        CheckBox teamCheckbox = v.findViewById(R.id.team_select);
        teamCheckbox.setChecked(false);
        teamCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            m_bFilterTeam = isChecked;
            m_TeamSpinner.setEnabled(m_bFilterTeam);
        });

        m_TeamSpinner = v.findViewById(R.id.team_options);
        m_TeamSpinner.setEnabled(m_bFilterTeam);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.select_dialog_item, MatchListData.get(getActivity()).listTeams());
        m_TeamSpinner.setAdapter(adapter);

        CheckBox matchCheckbox = v.findViewById(R.id.match_select);
        matchCheckbox.setChecked(false);
        matchCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            m_bFilterMatch = isChecked;
            m_MatchEditText.setEnabled(m_bFilterMatch);
        });

        m_MatchEditText = v.findViewById(R.id.match_entry);
        m_MatchEditText.setEnabled(m_bFilterMatch);
        m_MatchEditText.setHint("Enter match number");

        CheckBox competitionCheckbox = v.findViewById(R.id.event_select);
        competitionCheckbox.setChecked(false);
        competitionCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            m_bFilterEvent = isChecked;
            m_matchListSpinner.setEnabled(m_bFilterEvent);
        });

        m_matchListSpinner = v.findViewById(R.id.event_options);
        m_matchListSpinner.setEnabled(m_bFilterEvent);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, MatchListData.get(getActivity()).listCompetitions());
        m_matchListSpinner.setAdapter(adapter1);

        CheckBox scoutCheckbox = v.findViewById(R.id.scout_select);
        scoutCheckbox.setChecked(false);
        scoutCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            m_bFilterScout = isChecked;
            m_ScoutSpinner.setEnabled(m_bFilterScout);
        });

        m_ScoutSpinner = v.findViewById(R.id.scout_options);
        m_ScoutSpinner.setEnabled(m_bFilterScout);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, MatchListData.get(getActivity()).listScouts());
        m_ScoutSpinner.setAdapter(adapter2);

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).setPositiveButton(android.R.string.ok, (dialog1, which) -> sendResult()).create();

        dialog.setTitle("Filter Matches");

        dialog.show();
        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setBackgroundColor(Color.parseColor("#3F51B5"));

        return dialog;
    }

    public static MatchFilterDialog newInstance()
    {
        Log.i(TAG, "newInstance() called");
        Bundle args = new Bundle();

        MatchFilterDialog dialog = new MatchFilterDialog();
        dialog.setArguments(args);

        return dialog;
    }

    private void sendResult()
    {
        Log.i(TAG, "sendResult() called");
        Intent intent = new Intent(getActivity(), MatchListActivity.class);
        if (m_bFilterTeam)
        {
            Log.d(TAG, m_TeamSpinner.getSelectedItem().toString());
            intent.putExtra("team", m_TeamSpinner.getSelectedItem().toString());
            Log.d(TAG, "Filtering by team");
        }
        if (m_bFilterScout)
        {
            intent.putExtra("scout", m_ScoutSpinner.getSelectedItem().toString());
            Log.d(TAG, "Filtering by filter by scout");
        }
        if (m_bFilterEvent)
        {
            Log.d(TAG, m_matchListSpinner.getSelectedItem().toString());
            intent.putExtra("competition", m_matchListSpinner.getSelectedItem().toString());
            Log.d(TAG, "Filtering by filter by competition");
        }
        if (m_bFilterMatch)
        {
            intent.putExtra("match", m_MatchEditText.getText().toString());
            Log.d(TAG, "Filtering by filter by match");
        }
        startActivity(intent);
    }
}


