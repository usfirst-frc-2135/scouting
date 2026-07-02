package com.frc2135.android.frc_scout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.frc2135.android.frc_scout.databinding.PrematchActivityBinding;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Activity for entering pre-match information such as scout name, match number, and team number.
 * It autopopulates team numbers if event data is loaded and handles aliases.
 */
public class PreMatchActivity extends AppCompatActivity
{
    private static final String TAG = "PreMatchActivity";
    private PrematchActivityBinding binding;
    private EventMatches m_eventMatches;
    private MatchData m_matchData;
    private TeamAliases m_aliasNames;
    private ScoutNames m_scoutNames;
    private String m_teamIndexStr;
    private Settings m_settings;
    private boolean m_isEditMode;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        // Apply theme preference before super.onCreate to ensure the correct theme is applied early
        Preferences.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);

        binding = PrematchActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        loadInitialData();
        setupActionBar();
        setupViewDefaults();
        setupListeners();
    }

    /**
     * Loads initial data from Intents and singletons.
     */
    private void loadInitialData()
    {
        String matchId = getIntent().getStringExtra("match_ID");
        m_matchData = MatchListData.getInstance(getApplicationContext()).getMatch(matchId);

        String eventCode = (m_matchData != null) ? m_matchData.getEventCode().trim() : "";
        m_eventMatches = EventMatches.get(getApplicationContext(), eventCode, false);
        m_aliasNames = TeamAliases.get(getApplicationContext(), eventCode, false);
        m_scoutNames = ScoutNames.get(getApplicationContext(), eventCode, false);

        m_settings = Settings.getInstance(getApplicationContext());
        m_teamIndexStr = (m_settings != null) ? m_settings.getTeamIndexStr() : "0 - None";

        String inEdit = getIntent().getStringExtra("in_edit");
        m_isEditMode = "yes".equalsIgnoreCase(inEdit);
    }

    /**
     * Configures the action bar title with the current team index.
     */
    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(R.string.pre_match);
            binding.toolbarTitle.setText(getString(R.string.team_index_label, m_teamIndexStr));
        }
    }

    /**
     * Sets default values and hints for the UI components.
     */
    private void setupViewDefaults()
    {
        binding.errorMessagePm.setVisibility(View.GONE);

        if (m_matchData != null)
        {
            binding.eventCode.setText(m_matchData.getEventCode());
        }

        String scoutName = (m_matchData != null && !m_matchData.getScoutName().isEmpty()) ? m_matchData.getScoutName() :
                (m_settings != null) ? m_settings.getMostRecentScoutName() : "";
        binding.scoutName.setText(scoutName);

        String matchNum = (m_matchData != null && !m_matchData.getMatchNumber().isEmpty()) ? m_matchData.getMatchNumber() :
                (m_settings != null && !m_settings.getMostRecentMatchNumber().isEmpty()) ? m_settings.getNextExpectedMatchNumber() : "qm1";
        binding.matchNumberField.setText(matchNum);

        if (m_matchData != null)
        {
            binding.teamNumberField.setText(m_matchData.getTeamNumber());
        }

        setTeamNumFromMatchNum();
    }

    /**
     * Attaches listeners to the UI components.
     */
    private void setupListeners()
    {
        binding.eventCode.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                binding.compNameLayout.setError(null);
                binding.errorMessagePm.setVisibility(View.GONE);
            }
        });

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
                binding.scoutNameLayout.setError(null);
                binding.errorMessagePm.setVisibility(View.GONE);
            }
        });

        // Scout Name AutoComplete setup
        List<String> scoutNames = new ArrayList<>();
        if (m_scoutNames != null && m_scoutNames.isScoutNamesLoaded())
        {
            scoutNames.addAll(m_scoutNames.getScoutNames());
        }
        if (m_settings != null)
        {
            for (String name : m_settings.getPastScouts())
            {
                if (!scoutNames.contains(name))
                {
                    scoutNames.add(name);
                }
            }
        }

        if (!scoutNames.isEmpty())
        {
            ArrayAdapter<String> scoutAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, scoutNames);
            binding.scoutName.setAdapter(scoutAdapter);
            binding.scoutName.setThreshold(0);
            binding.scoutName.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus)
                {
                    binding.scoutName.showDropDown();
                }
            });
        }

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
                binding.matchNumberLayout.setError(null);
                binding.errorMessagePm.setVisibility(View.GONE);
                setTeamNumFromMatchNum();
            }
        });

        binding.teamNumberField.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                binding.teamNumberLayout.setError(null);
                binding.errorMessagePm.setVisibility(View.GONE);
            }
        });

        binding.teamNumberField.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
            {
                showTeamNumberDropDown();
            }
        });

        binding.startMatchButton.setOnClickListener(view -> {
            if (checkValidData())
            {
                updatePreMatchData();
                Intent intent = new Intent(this, ScoutingActivity.class);
                intent.putExtra("match_ID", m_matchData.getMatchID());
                startActivity(intent);
            }
        });

        binding.preMatchCancelButton.setOnClickListener(view -> {
            if (!m_isEditMode && m_matchData != null)
            {
                MatchListData.getInstance(getApplicationContext()).deleteMatch(m_matchData);
            }
            startActivity(new Intent(this, MatchListActivity.class));
            finish();
        });
    }

    /**
     * Shows a drop-down list of possible team numbers for the current match.
     */
    private void showTeamNumberDropDown()
    {
        Log.d(TAG, "Showing team number drop down");
        String matchNumStr = binding.matchNumberField.getText().toString().trim().toLowerCase();
        boolean bAliasUsed = m_aliasNames != null && m_aliasNames.isTeamAliasesLoaded();

        if (!matchNumStr.isEmpty() && m_eventMatches != null && m_eventMatches.isEventMatchesLoaded())
        {
            try
            {
                String[] teams = m_eventMatches.getMatchTeams(matchNumStr);
                if (teams.length > 0 && !teams[0].isEmpty())
                {
                    // Process team numbers (strip prefix and apply aliases)
                    for (int i = 0; i < teams.length; i++)
                    {
                        String tNum = MatchData.stripTeamNumPrefix(teams[i]);
                        if (bAliasUsed)
                        {
                            String alias = m_aliasNames.getAliasForTeamNum(tNum);
                            if (!alias.isEmpty())
                            {
                                tNum = alias;
                            }
                        }
                        teams[i] = tNum;
                    }

                    ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, teams);
                    binding.teamNumberField.setAdapter(teamAdapter);
                    binding.teamNumberField.setDropDownHeight(620);
                    binding.teamNumberField.showDropDown();
                }
            }
            catch (JSONException | NullPointerException exception)
            {
                Log.e(TAG, "Error fetching teams for dropdown", exception);
            }
        }
    }

    /**
     * Autopopulates the team number field based on the match number and configured team index.
     */
    private void setTeamNumFromMatchNum()
    {
        if (m_settings == null || m_eventMatches == null || !m_eventMatches.isEventMatchesLoaded())
        {
            return;
        }

        String matchNumStr = binding.matchNumberField.getText().toString().trim();
        if (!matchNumStr.isEmpty() && !m_teamIndexStr.isEmpty() && m_settings.isValidTeamIndexNum(m_teamIndexStr))
        {
            Log.d(TAG, "Auto-loading team number for index " + m_teamIndexStr);
            try
            {
                String[] teams = m_eventMatches.getMatchTeams(matchNumStr);
                int teamIndex = Integer.parseInt(m_teamIndexStr);

                if (teamIndex < teams.length)
                {
                    String tbaTeamNum = teams[teamIndex];
                    String teamNumStr = MatchData.stripTeamNumPrefix(tbaTeamNum);

                    if (m_aliasNames != null && m_aliasNames.isTeamAliasesLoaded())
                    {
                        String alias = m_aliasNames.getAliasForTeamNum(teamNumStr);
                        if (!alias.isEmpty())
                        {
                            teamNumStr = alias;
                        }
                    }
                    binding.teamNumberField.setText(teamNumStr);
                }
            }
            catch (JSONException | NumberFormatException e)
            {
                Log.e(TAG, "Error in auto-loading team number", e);
            }
        }
    }

    /**
     * Persists the entered pre-match data to the MatchData object and updates settings.
     */
    public void updatePreMatchData()
    {
        if (m_matchData == null)
        {
            return;
        }

        String scoutName = binding.scoutName.getText().toString().trim();
        String eventCode = Objects.requireNonNull(binding.eventCode.getText()).toString().trim();
        String matchNum = binding.matchNumberField.getText().toString().trim().toLowerCase();
        String teamNumEntry = binding.teamNumberField.getText().toString().trim();

        if (m_settings != null)
        {
            m_settings.addPastScoutNames(scoutName);
            m_settings.setMostRecentScoutName(scoutName);
            m_settings.setMostRecentMatchNumber(matchNum);
        }

        m_matchData.setScoutName(scoutName);
        m_matchData.setEventCode(eventCode);
        m_matchData.setMatchNumber(matchNum);

        String teamNum = teamNumEntry;
        String teamAlias = "";

        // Handle alias detection (e.g., 99#)
        if (m_aliasNames != null && m_aliasNames.isTeamAliasesLoaded() && teamNumEntry.startsWith("99"))
        {
            try
            {
                String bcdNum = m_aliasNames.getTeamNumForAlias(teamNumEntry);
                if (!bcdNum.isEmpty())
                {
                    teamNum = bcdNum;
                    teamAlias = teamNumEntry;
                }
            }
            catch (JSONException e)
            {
                Log.e(TAG, "Error resolving team alias", e);
            }
        }

        m_matchData.setTeamNumber(teamNum);
        m_matchData.setTeamAlias(teamAlias);
        Log.i(TAG, "Updated MatchData: Team=" + teamNum + ", Alias=" + teamAlias);
    }

    /**
     * Validates that all required fields have been filled.
     *
     * @return true if data is valid, false otherwise
     */
    private boolean checkValidData()
    {
        String eventCode = Objects.requireNonNull(binding.eventCode.getText()).toString().trim();
        String scoutName = binding.scoutName.getText().toString().trim();
        String matchNum = binding.matchNumberField.getText().toString().trim();
        String teamNum = binding.teamNumberField.getText().toString().trim();

        String requiredError = getString(R.string.required);
        binding.compNameLayout.setError(eventCode.isEmpty() ? requiredError : null);
        binding.scoutNameLayout.setError(scoutName.isEmpty() ? requiredError : null);
        binding.matchNumberLayout.setError(matchNum.isEmpty() ? requiredError : null);
        binding.teamNumberLayout.setError(teamNum.isEmpty() ? requiredError : null);

        boolean isValid = !eventCode.isEmpty() && !scoutName.isEmpty() && !matchNum.isEmpty() && !teamNum.isEmpty();

        binding.errorMessagePm.setVisibility(isValid ? View.GONE : View.VISIBLE);
        return isValid;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        binding = null;
    }
}
