package com.frc2135.android.frc_scout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

/** @noinspection Convert2Lambda*/
public class PreMatchActivity extends AppCompatActivity
{
    public static final String TAG = "PreMatchActivity";

    private String               m_teamIndexStr;
    private EditText             m_competitionField;
    private AutoCompleteTextView m_scoutNameField;
    private AutoCompleteTextView m_teamNumberField;
    private EditText             m_matchNumberField;
    private TextView             m_missingFieldErrMsg;
    private MatchData            m_matchData;
    private CompetitionInfo      m_compInfo;
    private static Scouter       m_Scouter;
    private String               m_inEdit; 

    private void setTeamNumFromMatchNum()
    {
        // If there is a match number and a team index, load that team number from event teams list.
        String matchNumStr = m_matchNumberField.getText().toString().trim();
        if (!matchNumStr.isEmpty() && !m_teamIndexStr.isEmpty() && m_Scouter.isValidTeamIndexNum(m_teamIndexStr) && m_compInfo != null && m_compInfo.isEventDataLoaded())
        {
            try
            {
                String[] teams = m_compInfo.getTeams(matchNumStr);
                int teamIndex = Integer.parseInt(m_teamIndexStr);
                String teamNumStr = teams[teamIndex];
                Log.d(TAG, "Preloading team number using index " + m_teamIndexStr + ": " + teamNumStr);
                m_teamNumberField.setText(teamNumStr);
            } catch (JSONException jsonException)
            {
                Log.e(TAG, "For preload: couldn't get teams from m_compInfo");
                jsonException.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        m_Scouter = Scouter.get(getApplicationContext());

        String matchId = getIntent().getStringExtra("match_ID");
        m_inEdit = getIntent().getStringExtra("in_edit");  
        m_matchData = MatchHistory.get(getApplicationContext()).getMatch(matchId);
        m_compInfo = CompetitionInfo.get(getApplicationContext(), m_matchData.getEventCode().trim(), false);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle("Pre-Match");

        setContentView(R.layout.prematch_activity);

        m_missingFieldErrMsg = findViewById(R.id.error_message_pm);
        m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
        m_missingFieldErrMsg.setTextColor(Color.RED);

        if (m_Scouter != null)
            m_teamIndexStr = m_Scouter.getTeamIndexStr();
        else
            m_teamIndexStr = "None";

        m_competitionField = findViewById(R.id.comp_name);
        m_competitionField.setHint("Competition");
        m_competitionField.setText(m_matchData.getEventCode());
        m_competitionField.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {

            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
            }
        });

        m_scoutNameField = findViewById(R.id.scouter_name);
        m_scoutNameField.setHint("Scout Name");
        if (m_matchData != null && !m_matchData.getName().isEmpty())
            m_scoutNameField.setText(m_matchData.getName());
        else if (m_Scouter != null)
        {
            String mostRecentScoutName = m_Scouter.getMostRecentScoutName();
            if (!mostRecentScoutName.isEmpty())
            {
                m_scoutNameField.setText(mostRecentScoutName);
            }
        }
        m_scoutNameField.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(PreMatchActivity.this, android.R.layout.select_dialog_item, Scouter.get(getApplicationContext()).getPastScouts());
        m_scoutNameField.setAdapter(adapter2);
        m_scoutNameField.setThreshold(0);
        m_scoutNameField.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    m_scoutNameField.showDropDown();
                }
            }
        });

        m_matchNumberField = findViewById(R.id.match_number_field);
        if (!m_matchData.getMatchNumber().isEmpty())
            m_matchNumberField.setText(m_matchData.getMatchNumber());
        else if (m_Scouter != null && !m_Scouter.getMostRecentMatchNumber().isEmpty())
            m_matchNumberField.setText(m_Scouter.getNextExpectedMatchNumber());
        else
        {
            String str1 = "qm1";
            m_matchNumberField.setText(str1);
        }

        m_matchNumberField.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {

            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                setTeamNumFromMatchNum();
            }
        });

        m_teamNumberField = findViewById(R.id.team_number_field);
        m_teamNumberField.setText(m_matchData.getTeamNumber());

        setTeamNumFromMatchNum();

        m_teamNumberField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
                if (parent != null && parent.getItemAtPosition(position) != null)
                {
                    m_matchData.setTeamNumber(parent.getItemAtPosition(position).toString());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
                m_teamNumberField.setSelection(0);
            }
        });

        m_teamNumberField.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    Log.d(TAG, "m_teamNumberField clicked");
                    String matchNumStr = m_matchNumberField.getText().toString().trim().toLowerCase();
                    if (!matchNumStr.isEmpty() && m_compInfo != null && m_compInfo.isEventDataLoaded())
                    {
                        boolean bTeamsLoadedSuccessfully = false;
                        try
                        {
                            String[] teams = m_compInfo.getTeams(m_matchNumberField.getText().toString().trim());
                            if (!teams[0].equals(""))
                                bTeamsLoadedSuccessfully = true;
                            ArrayAdapter<String> adapter3 = new ArrayAdapter<>(PreMatchActivity.this, android.R.layout.select_dialog_item, teams);
                            m_teamNumberField.setAdapter(adapter3);
                        } catch (JSONException jsonException)
                        {
                            Log.e(TAG, "finding teams failed");
                            jsonException.printStackTrace();
                        } catch (NullPointerException nullPointerException)
                        {
                            nullPointerException.printStackTrace();
                        }
                        if (bTeamsLoadedSuccessfully)
                        {
                            m_teamNumberField.setDropDownHeight(620);
                            m_teamNumberField.showDropDown();
                        }
                    }
                }
            }
        });

        Button startScoutingButton = findViewById(R.id.start_scouting_button);
        startScoutingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (checkValidData())
                {
                    updatePreMatchData();
                    Intent intent1 = new Intent(PreMatchActivity.this, ScoutingActivity.class);
                    intent1.putExtra("match_ID", m_matchData.getMatchID());
                    startActivity(intent1);
                }
            }
        });

        Button preMatchCancelButton = findViewById(R.id.pre_match_cancel_button);
        preMatchCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               // Only delete match if not currently being edited  
               if (!m_inEdit.equals("yes")) {
                  MatchHistory.get(getApplicationContext()).deleteMatch(m_matchData);
               }
               Intent intent2 = new Intent(PreMatchActivity.this, MatchListActivity.class);
               startActivity(intent2);
            }
        });
    }

    public void updatePreMatchData()
    {
        m_Scouter = Scouter.get(getApplicationContext());
        m_Scouter.addPastScouter(m_scoutNameField.getText().toString());
        m_Scouter.setMostRecentScoutName(m_scoutNameField.getText().toString());
        m_Scouter.setMostRecentMatchNumber(m_matchNumberField.getText().toString().toLowerCase());
        m_matchData.setName(m_scoutNameField.getText().toString());
        m_matchData.setEventCode(m_competitionField.getText().toString());
        m_matchData.setMatchNumber(m_matchNumberField.getText().toString().trim().toLowerCase());
        m_matchData.setTeamNumber(m_teamNumberField.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkValidData()
    {
        m_missingFieldErrMsg.setVisibility(View.INVISIBLE);

        // Make sure there are entries for the various fields on this page.
        if (m_competitionField.getText().toString().trim().equals("") || m_scoutNameField.getText().toString().trim().equals("") || m_teamNumberField.getText().toString().trim().equals("") || m_matchNumberField.getText().toString().trim().equals(""))
        {
            m_missingFieldErrMsg.setVisibility(View.VISIBLE);
            return false;
        }
        m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        Log.d(TAG, "onBack Pressed");
        super.onBackPressed();
    }
}



