package com.frc2135.android.frc_scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/** @noinspection ALL*/
public class FilterDialog extends DialogFragment
{

    private static final String TAG = "FilterDialog";

    private boolean m_bFilterTeam;
    private boolean m_bFilterCompetition;
    private boolean m_bFilterScout;
    private boolean m_bFilterMatch;
    private Spinner m_TeamSpinner;
    private Spinner m_CompetitionSpinner;
    private Spinner m_ScoutSpinner;
    private EditText m_MatchEditText;

    @NonNull
    public Dialog onCreateDialog(Bundle SavedInstanceState)
    {
        setCancelable(false);

        Log.i(TAG, "onCreateDialog called");

        View v = requireActivity().getLayoutInflater().inflate(R.layout.filter_dialog, null);

        m_bFilterTeam = false;
        m_bFilterCompetition = false;
        m_bFilterScout = false;
        m_bFilterMatch = false;

        CheckBox teamCheckbox = v.findViewById(R.id.team_select);
        teamCheckbox.setChecked(false);
        teamCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                m_bFilterTeam = isChecked;
                m_TeamSpinner.setEnabled(m_bFilterTeam);
            }
        });

        m_TeamSpinner = v.findViewById(R.id.team_options);
        m_TeamSpinner.setEnabled(m_bFilterTeam);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, MatchHistory.get(getActivity()).listTeams());
        m_TeamSpinner.setAdapter(adapter);

        CheckBox matchCheckbox = v.findViewById(R.id.match_select);
        matchCheckbox.setChecked(false);
        matchCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                m_bFilterMatch = isChecked;
                m_MatchEditText.setEnabled(m_bFilterMatch);
            }
        });

        m_MatchEditText = v.findViewById(R.id.match_entry);
        m_MatchEditText.setEnabled(m_bFilterMatch);
        m_MatchEditText.setHint("Enter match number");

        CheckBox competitionCheckbox = v.findViewById(R.id.competition_select);
        competitionCheckbox.setChecked(false);
        competitionCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                m_bFilterCompetition = isChecked;
                m_CompetitionSpinner.setEnabled(m_bFilterCompetition);
            }
        });

        m_CompetitionSpinner = v.findViewById(R.id.competition_options);
        m_CompetitionSpinner.setEnabled(m_bFilterCompetition);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, MatchHistory.get(getActivity()).listCompetitions());
        m_CompetitionSpinner.setAdapter(adapter1);

        CheckBox scoutCheckbox = v.findViewById(R.id.scout_select);
        scoutCheckbox.setChecked(false);
        scoutCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                m_bFilterScout = isChecked;
                m_ScoutSpinner.setEnabled(m_bFilterScout);
            }
        });

        m_ScoutSpinner = v.findViewById(R.id.scout_options);
        m_ScoutSpinner.setEnabled(m_bFilterScout);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, MatchHistory.get(getActivity()).listScouts());
        m_ScoutSpinner.setAdapter(adapter2);

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                sendResult();
            }
        }).create();

        dialog.setTitle("Filter Matches");

        dialog.show();
        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setBackgroundColor(Color.parseColor("#3F51B5"));

        return dialog;
    }

    public static FilterDialog newInstance()
    {
        Log.i(TAG, "newInstance() called");
        Bundle args = new Bundle();

        FilterDialog dialog = new FilterDialog();
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
            Log.d(TAG, "I want to filter by team");
        }
        if (m_bFilterScout)
        {
            intent.putExtra("scout", m_ScoutSpinner.getSelectedItem().toString());
            Log.d(TAG, "I want to filter by scout");
        }
        if (m_bFilterCompetition)
        {
            Log.d(TAG, m_CompetitionSpinner.getSelectedItem().toString());
            intent.putExtra("competition", m_CompetitionSpinner.getSelectedItem().toString());
            Log.d(TAG, "I want to filter by competition");
        }
        if (m_bFilterMatch)
        {
            intent.putExtra("match", m_MatchEditText.getText().toString());
            Log.d(TAG, "I want to filter by match");
        }
        startActivity(intent);
    }
}


