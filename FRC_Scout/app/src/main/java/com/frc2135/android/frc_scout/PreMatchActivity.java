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
//import android.widget.CheckBox; //REMOVE LATER
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
    private AliasesInfo          m_aliasesInfo;
    private static Scouter       m_Scouter;
    private String               m_inEdit; 
    //private CheckBox m_scoringTableSideChbx;// REMOVE LATER

    private void setTeamNumFromMatchNum()
    {
        // If there is a match number and a team index, load that team number from event teams list.
        String matchNumStr = m_matchNumberField.getText().toString().trim();
        if (!matchNumStr.isEmpty() && !m_teamIndexStr.isEmpty() && m_Scouter.isValidTeamIndexNum(m_teamIndexStr) && m_compInfo != null && m_compInfo.isEventDataLoaded())
        {
            Log.d(TAG,"Looking for team number (index = "+m_teamIndexStr+") for match " + matchNumStr);
            try
            {
                String[] teams = m_compInfo.getTeams(matchNumStr);
                int teamIndex = Integer.parseInt(m_teamIndexStr);
                String tbaTeamNum = teams[teamIndex];

                // Strip off "frc" prefix from teamNumStr if needed.
                String teamNumStr = MatchData.stripTeamNumPrefix(tbaTeamNum);
                Log.d(TAG, "Auto-loading team number using index " + m_teamIndexStr + ": " + teamNumStr);
                // If aliases are used, get the alias for this team#.
                if (m_aliasesInfo != null && m_aliasesInfo.isAliasesDataLoaded())
                {
                    String alias = m_aliasesInfo.getAliasForTeamNum(teamNumStr);
                    if (!alias.equals(""))
                    {
                        // Found an alias; that means teamNumStr is the BCD num.
                        // But we want to display the alias in this case. 
                        Log.d(TAG, "For team "+teamNumStr + ", found an alias: " + alias);

                        teamNumStr = alias;
                    }
                }
                m_teamNumberField.setText(teamNumStr);
            }
            catch(JSONException jsonException)
            {
                Log.e(TAG, "For auto-load: couldn't get teams from m_compInfo");
                jsonException.printStackTrace();
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        m_Scouter = Scouter.get(getApplicationContext());
        Log.i(TAG,"INFO! starting PreMatchActivity onCreate()");
        Log.d(TAG,"!!!! starting PreMatchActivity onCreate()");
        String matchId = getIntent().getStringExtra("match_ID");
        m_inEdit = getIntent().getStringExtra("in_edit");  
        m_matchData = MatchHistory.get(getApplicationContext()).getMatch(matchId);
        m_compInfo = CompetitionInfo.get(getApplicationContext(), m_matchData.getEventCode().trim(), false);
        m_aliasesInfo = AliasesInfo.get(getApplicationContext(), m_matchData.getEventCode().trim(), false);

        if (m_Scouter != null)
            m_teamIndexStr = m_Scouter.getTeamIndexStr();
        else
            m_teamIndexStr = "None";

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)

            actionBar.setTitle("Pre-Match                                                Team Index = "+m_teamIndexStr);

        setContentView(R.layout.prematch_activity);

        m_missingFieldErrMsg = findViewById(R.id.error_message_pm);
        m_missingFieldErrMsg.setVisibility(View.INVISIBLE);
        m_missingFieldErrMsg.setTextColor(Color.RED);



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
/* REMOVE->
        m_scoringTableSideChbx = findViewById(R.id.scoring_table_side_ckbx);
        if(m_Scouter != null)
            m_scoringTableSideChbx.setChecked(m_Scouter.getScoringTableSide());
        else m_scoringTableSideChbx.setChecked(false);
<-REMOVE*/
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
                    Log.i(TAG,"!!!!! setting matchData teamNum = "+parent.getItemAtPosition(position).toString());
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
                // Show drop down list of possible team numbers from TBA matchlist (if loaded).
                if (hasFocus)
                {
                    Log.d(TAG, "m_teamNumberField clicked");
                    String matchNumStr = m_matchNumberField.getText().toString().trim().toLowerCase();
                    boolean bAliasUsed = false;
                    if (m_aliasesInfo != null && m_aliasesInfo.isAliasesDataLoaded())
                        bAliasUsed = true;

                    if ((!matchNumStr.isEmpty() && m_compInfo != null && m_compInfo.isEventDataLoaded()))
                    {
                        boolean bTeamsLoadedSuccessfully = false;
                        try
                        {
                            String[] teams = m_compInfo.getTeams(m_matchNumberField.getText().toString().trim());
                            if (!teams[0].equals("")) {
                               bTeamsLoadedSuccessfully = true;

                               // Strip off "frc" prefix from teamNumStr if needed.
                               for(int i = 0; i < teams.length; i++) {
                                  String tnum = teams[i];
                                  tnum = MatchData.stripTeamNumPrefix(tnum);  // strip off prefix
                                  teams[i] = tnum;

                                  // If aliases are used, get the alias (99#) for BCD num.
                                  if (bAliasUsed)
                                  {
                                     String alias = m_aliasesInfo.getAliasForTeamNum(tnum);
                                     if (!alias.equals(""))
                                     {
                                        Log.d(TAG, "For team "+tnum + ", found an alias: " + alias);
                                        teams[i] = alias;
                                     }
                                  }
                               }
                            }
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
        //m_Scouter.setScoringTableSide(m_scoringTableSideChbx.isChecked());//REMOVE LATER
        m_matchData.setName(m_scoutNameField.getText().toString());
        m_matchData.setEventCode(m_competitionField.getText().toString());
        m_matchData.setMatchNumber(m_matchNumberField.getText().toString().trim().toLowerCase());
        String teamNumEntry = m_teamNumberField.getText().toString().trim();
        String teamNum = teamNumEntry;
        String teamAlias = "";

        // If aliases are used and if teamNumEntry starts with "99", then get its BCDnum.
        if (m_aliasesInfo != null && m_aliasesInfo.isAliasesDataLoaded() && teamNumEntry.charAt(0) == '9' && teamNumEntry.charAt(1) == '9')
        {
            try
            {
                String bcdNum = m_aliasesInfo.getTeamNumForAlias(teamNumEntry);
                if (!bcdNum.equals(""))
                {
                    teamNum = bcdNum;
                    teamAlias = teamNumEntry;  // this is the 99#
                }
            }
            catch(JSONException jsonException)
            {
                Log.e(TAG, "For updatePreMatchData(): jsonException when getting teamNum from alias");
                jsonException.printStackTrace();
            }
        } 
        m_matchData.setTeamNumber(teamNum);
        m_matchData.setTeamAlias(teamAlias);
        Log.i(TAG,"--> setting MatchData teamNum = "+teamNum+", alias = "+teamAlias);
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

  //REMOVE  @Override
  //REMOVE  public void onBackPressed()
  //REMOVE  {
    //REMOVELog.d(TAG, "onBack Pressed");
 //REMOVE       super.onBackPressed();
 //REMOVE   }
}



