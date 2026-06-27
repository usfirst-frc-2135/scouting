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

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.frc2135.android.frc_scout.databinding.PrematchActivityBinding;

import org.json.JSONException;

public class PreMatchActivity extends AppCompatActivity
{
    public static final String TAG = "PreMatchActivity";

    private PrematchActivityBinding binding;
    private CompetitionInfo m_compInfo;
    private MatchData m_matchData;
    private AliasesInfo m_aliasInfo;
    private String m_teamIndexStr;
    private static Settings m_scoutName;
    private String m_inEdit;
    // private CheckBox m_scoringTableSideCheckbox;// REMOVE LATER

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "PreMatchActivity created.");
        super.onCreate(savedInstanceState);

        binding = PrematchActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String matchId = getIntent().getStringExtra("match_ID");
        m_matchData = MatchListData.get(getApplicationContext()).getMatch(matchId);
        m_compInfo = CompetitionInfo.get(getApplicationContext(), m_matchData.getEventCode().trim(), false);
        m_aliasInfo = AliasesInfo.get(getApplicationContext(), m_matchData.getEventCode().trim(), false);

        m_scoutName = Settings.get(getApplicationContext());
        m_teamIndexStr = (m_scoutName != null) ? m_scoutName.getTeamIndexStr() : "None";
        m_inEdit = getIntent().getStringExtra("in_edit");


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle("Pre-Match                                                Team Index = " + m_teamIndexStr);
        }

        binding.errorMessagePm.setVisibility(View.INVISIBLE);
        binding.errorMessagePm.setTextColor(Color.RED);

        binding.compName.setHint("Event code");
        binding.compName.setText(m_matchData.getEventCode());
        binding.compName.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                binding.errorMessagePm.setVisibility(View.INVISIBLE);
            }
        });

        binding.scoutName.setHint("Scout name");
        if (m_matchData != null && !m_matchData.getName().isEmpty())
        {
            binding.scoutName.setText(m_matchData.getName());
        }
        else if (m_scoutName != null)
        {
            String mostRecentScoutName = m_scoutName.getMostRecentScoutName();
            if (!mostRecentScoutName.isEmpty())
            {
                binding.scoutName.setText(mostRecentScoutName);
            }
        }
        binding.scoutName.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                binding.errorMessagePm.setVisibility(View.INVISIBLE);
            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(PreMatchActivity.this, android.R.layout.select_dialog_item, Settings.get(getApplicationContext()).getPastScouts());
        binding.scoutName.setAdapter(adapter2);
        binding.scoutName.setThreshold(0);
        binding.scoutName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
            {
                binding.scoutName.showDropDown();
            }
        });
/* REMOVE->
        m_scoringTableSideCheckbox = findViewById(R.id.scoring_table_side_ckbx);
        if(m_scoutNames != null)
            m_scoringTableSideChbx.setChecked(m_scoutNames.getScoringTableSide());
        else m_scoringTableSideChbx.setChecked(false);
<-REMOVE*/

        String str = "qm1";
        if (!m_matchData.getMatchNumber().isEmpty())
        {
            str = m_matchData.getMatchNumber();
        }
        else if (m_scoutName != null && !m_scoutName.getMostRecentMatchNumber().isEmpty())
        {
            str = m_scoutName.getNextExpectedMatchNumber();
        }
        binding.matchNumberField.setText(str);

        binding.matchNumberField.addTextChangedListener(new TextWatcher()
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

        binding.teamNumberField.setText(m_matchData.getTeamNumber());

        setTeamNumFromMatchNum();

        binding.teamNumberField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                binding.errorMessagePm.setVisibility(View.INVISIBLE);
                if (parent != null && parent.getItemAtPosition(position) != null)
                {
                    Log.i(TAG, "!!!!! setting matchData teamNum = " + parent.getItemAtPosition(position).toString());
                    m_matchData.setTeamNumber(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                binding.errorMessagePm.setVisibility(View.INVISIBLE);
                binding.teamNumberField.setSelection(0);
            }
        });

        binding.teamNumberField.setOnFocusChangeListener((v, hasFocus) -> {
            // Show drop down list of possible team numbers from TBA matchList (if loaded).
            if (hasFocus)
            {
                Log.d(TAG, "m_teamNumberField clicked");
                String matchNumStr = binding.matchNumberField.getText().toString().trim().toLowerCase();
                boolean bAliasUsed = m_aliasInfo != null && m_aliasInfo.isAliasesInfoLoaded();

                if ((!matchNumStr.isEmpty() && m_compInfo != null && m_compInfo.isEventDataLoaded()))
                {
                    boolean bTeamsLoadedSuccessfully = false;
                    try
                    {
                        String[] teams = m_compInfo.getTeams(binding.matchNumberField.getText().toString().trim());
                        if (!teams[0].isEmpty())
                        {
                            bTeamsLoadedSuccessfully = true;

                            // Strip off "frc" prefix from teamNumStr if needed.
                            for (int i = 0; i < teams.length; i++)
                            {
                                String tNum = teams[i];
                                tNum = MatchData.stripTeamNumPrefix(tNum);  // strip off prefix
                                teams[i] = tNum;

                                // If aliases are used, get the alias (99#) for BCD num.
                                if (bAliasUsed)
                                {
                                    String alias = m_aliasInfo.getAliasForTeamNum(tNum);
                                    if (!alias.isEmpty())
                                    {
                                        Log.d(TAG, "For team " + tNum + ", found an alias: " + alias);
                                        teams[i] = alias;
                                    }
                                }
                            }
                        }
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(PreMatchActivity.this, android.R.layout.select_dialog_item, teams);
                        binding.teamNumberField.setAdapter(adapter3);
                    }
                    catch (JSONException | NullPointerException exception)
                    {
                        Log.e(TAG, Log.getStackTraceString(exception));
                    }
                    if (bTeamsLoadedSuccessfully)
                    {
                        binding.teamNumberField.setDropDownHeight(620);
                        binding.teamNumberField.showDropDown();
                    }
                }
            }
        });

        binding.startMatchButton.setOnClickListener(view -> {
            if (checkValidData())
            {
                updatePreMatchData();
                Intent intent1 = new Intent(PreMatchActivity.this, ScoutingActivity.class);
                intent1.putExtra("match_ID", m_matchData.getMatchID());
                startActivity(intent1);
            }
        });

        binding.preMatchCancelButton.setOnClickListener(view -> {
            // Only delete match if not currently being edited
            if (!m_inEdit.equals("yes"))
            {
                MatchListData.get(getApplicationContext()).deleteMatch(m_matchData);
            }
            Intent intent2 = new Intent(PreMatchActivity.this, MatchListActivity.class);
            startActivity(intent2);
        });
    }

    private void setTeamNumFromMatchNum()
    {
        // If there is a match number and a team index, load that team number from event teams list.
        String matchNumStr = binding.matchNumberField.getText().toString().trim();
        if (!matchNumStr.isEmpty() && !m_teamIndexStr.isEmpty() && m_scoutName.isValidTeamIndexNum(m_teamIndexStr) && m_compInfo != null && m_compInfo.isEventDataLoaded())
        {
            Log.d(TAG, "Looking for team number (index = " + m_teamIndexStr + ") for match " + matchNumStr);
            try
            {
                String[] teams = m_compInfo.getTeams(matchNumStr);
                int teamIndex = Integer.parseInt(m_teamIndexStr);
                String tbaTeamNum = teams[teamIndex];

                // Strip off "frc" prefix from teamNumStr if needed.
                String teamNumStr = MatchData.stripTeamNumPrefix(tbaTeamNum);
                Log.d(TAG, "Auto-loading team number using index " + m_teamIndexStr + ": " + teamNumStr);
                // If aliases are used, get the alias for this team#.
                if (m_aliasInfo != null && m_aliasInfo.isAliasesInfoLoaded())
                {
                    String alias = m_aliasInfo.getAliasForTeamNum(teamNumStr);
                    if (!alias.isEmpty())
                    {
                        // Found an alias; that means teamNumStr is the BCD num.
                        // But we want to display the alias in this case.
                        Log.d(TAG, "For team " + teamNumStr + ", found an alias: " + alias);
                        teamNumStr = alias;
                    }
                }
                binding.teamNumberField.setText(teamNumStr);
            }
            catch (JSONException jsonException)
            {
                Log.e(TAG, "For auto-load: couldn't get teams from m_compInfo");
                Log.e(TAG, Log.getStackTraceString(jsonException));
            }
        }
    }

    public void updatePreMatchData()
    {
        m_scoutName = Settings.get(getApplicationContext());
        m_scoutName.addPastScoutNames(binding.scoutName.getText().toString());
        m_scoutName.setMostRecentScoutName(binding.scoutName.getText().toString());
        m_scoutName.setMostRecentMatchNumber(binding.matchNumberField.getText().toString().toLowerCase());
        m_matchData.setName(binding.scoutName.getText().toString());
        m_matchData.setEventCode(binding.compName.getText().toString());
        m_matchData.setMatchNumber(binding.matchNumberField.getText().toString().trim().toLowerCase());
        String teamNumEntry = binding.teamNumberField.getText().toString().trim();
        String teamNum = teamNumEntry;
        String teamAlias = "";

        // If aliases are used and if teamNumEntry starts with "99", then get its BCDNum.
        if (m_aliasInfo != null && m_aliasInfo.isAliasesInfoLoaded() && teamNumEntry.length() >= 2 && teamNumEntry.charAt(0) == '9' && teamNumEntry.charAt(1) == '9')
        {
            try
            {
                String bcdNum = m_aliasInfo.getTeamNumForAlias(teamNumEntry);
                if (!bcdNum.isEmpty())
                {
                    teamNum = bcdNum;
                    teamAlias = teamNumEntry;  // this is the 99#
                }
            }
            catch (JSONException jsonException)
            {
                Log.e(TAG, "For updatePreMatchData(): jsonException when getting teamNum from alias");
                Log.e(TAG, Log.getStackTraceString(jsonException));
            }
        }
        m_matchData.setTeamNumber(teamNum);
        m_matchData.setTeamAlias(teamAlias);
        Log.i(TAG, "--> setting MatchData teamNum = " + teamNum + ", alias = " + teamAlias);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkValidData()
    {
        binding.errorMessagePm.setVisibility(View.INVISIBLE);

        // Make sure there are entries for the various fields on this page.
        if (binding.compName.getText().toString().trim().isEmpty() || binding.scoutName.getText().toString().trim().isEmpty() || binding.teamNumberField.getText().toString().trim().isEmpty() || binding.matchNumberField.getText().toString().trim().isEmpty())
        {
            binding.errorMessagePm.setVisibility(View.VISIBLE);
            return false;
        }
        binding.errorMessagePm.setVisibility(View.INVISIBLE);
        return true;
    }

}
