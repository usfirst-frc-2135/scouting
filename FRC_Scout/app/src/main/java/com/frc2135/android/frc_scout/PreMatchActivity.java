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

import com.frc2135.android.frc_scout.databinding.PreMatchActivityBinding;

import java.util.List;
import java.util.Objects;

/**
 * Activity for entering pre-match information such as scout name, match number, and team number.
 * It autopopulates team numbers if event data is loaded and handles aliases.
 */
public class PreMatchActivity extends AppCompatActivity
{
    private static final String TAG = "PreMatchActivity";
    private PreMatchActivityBinding m_binding;
    private TBAMatches m_tbaMatches;
    private MatchData m_matchData;
    private TeamAliases m_teamAliases;
    private ScoutNames m_scoutNames;
    private String m_teamIndexStr;
    private Settings m_settings;
    private boolean m_isEditMode;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreate");
        // Apply theme preference before super.onCreate to ensure the correct theme is applied early
        Preferences.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);

        m_binding = PreMatchActivityBinding.inflate(getLayoutInflater());
        setContentView(m_binding.getRoot());

        setSupportActionBar(m_binding.preMatchActivityToolbar);
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
        m_matchData = ScoutedMatches.getInstance(getApplicationContext()).getMatch(matchId);

        String eventCode = (m_matchData != null) ? m_matchData.getEventCode().trim() : "";
        m_tbaMatches = TBAMatches.getInstance(getApplicationContext(), eventCode, false);
        m_teamAliases = TeamAliases.getInstance(getApplicationContext());

        m_settings = Settings.getInstance(getApplicationContext());
        m_teamIndexStr = (m_settings != null) ? m_settings.getTeamIndexStr() : "unknown";

        m_scoutNames = ScoutNames.getInstance(getApplicationContext());

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
            m_binding.preMatchActivityToolbarTitle.setText(getString(R.string.team_index_label, m_teamIndexStr));
        }
    }

    /**
     * Sets default values and hints for the UI components.
     */
    private void setupViewDefaults()
    {
        m_binding.preMatchErrorMessage.setVisibility(View.GONE);

        if (m_matchData != null)
        {
            m_binding.preMatchEventCodeInput.setText(m_matchData.getEventCode());
        }

        String matchNum = (m_matchData != null && !m_matchData.getMatchNumber().isEmpty()) ? m_matchData.getMatchNumber() :
                (m_settings != null && !m_settings.getMostRecentMatchNumber().isEmpty()) ? m_settings.getNextExpectedMatchNumber() : "qm1";
        m_binding.preMatchNumberInput.setText(matchNum);

        if (m_matchData != null)
        {
            m_binding.preMatchTeamNumberInput.setText(m_matchData.getTeamNumber());
        }

        setTeamNumFromMatchNum();

        String scoutName = (m_matchData != null && !m_matchData.getScoutName().isEmpty()) ? m_matchData.getScoutName() :
                (m_settings != null) ? m_settings.getMostRecentScoutName() : "";
        m_binding.preMatchScoutNameInput.setText(scoutName);
    }

    /**
     * Attaches listeners to the UI components.
     */
    private void setupListeners()
    {
        m_binding.preMatchEventCodeInput.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                m_binding.preMatchEventCodeLayout.setError(null);
                m_binding.preMatchErrorMessage.setVisibility(View.GONE);
            }
        });

        m_binding.preMatchNumberInput.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                m_binding.preMatchNumberLayout.setError(null);
                m_binding.preMatchErrorMessage.setVisibility(View.GONE);
                setTeamNumFromMatchNum();
            }
        });

        m_binding.preMatchTeamNumberInput.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                m_binding.preMatchTeamNumberLayout.setError(null);
                m_binding.preMatchErrorMessage.setVisibility(View.GONE);
            }
        });

        m_binding.preMatchTeamNumberInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
            {
                showTeamNumberDropDown();
            }
        });

        m_binding.preMatchScoutNameInput.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence c, int start, int before, int count)
            {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {
            }

            public void afterTextChanged(Editable c)
            {
                m_binding.preMatchScoutNameLayout.setError(null);
                m_binding.preMatchErrorMessage.setVisibility(View.GONE);
            }
        });

        // Scout Name AutoComplete setup
        List<String> scoutNames = m_scoutNames.getAllScoutNames(this);

        if (!scoutNames.isEmpty())
        {
            ArrayAdapter<String> scoutAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, scoutNames);
            m_binding.preMatchScoutNameInput.setAdapter(scoutAdapter);
            m_binding.preMatchScoutNameInput.setThreshold(0);
            m_binding.preMatchScoutNameInput.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus)
                {
                    m_binding.preMatchScoutNameInput.showDropDown();
                }
            });
        }

        m_binding.preMatchStartButton.setOnClickListener(view -> {
            if (checkValidData())
            {
                updatePreMatchData();
                Intent intent = new Intent(this, ScoutingActivity.class);
                intent.putExtra("match_ID", m_matchData.getMatchID());
                startActivity(intent);
            }
        });

        m_binding.preMatchCancelButton.setOnClickListener(view -> {
            if (!m_isEditMode && m_matchData != null)
            {
                ScoutedMatches.getInstance(getApplicationContext()).deleteMatch(m_matchData);
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
        String matchNumStr = m_binding.preMatchNumberInput.getText().toString().trim().toLowerCase();

        if (!matchNumStr.isEmpty() && m_tbaMatches != null && m_tbaMatches.isTBAMatchesLoaded())
        {
            try
            {
                String[] teams = m_tbaMatches.getMatchTeams(matchNumStr);
                if (teams.length > 0 && !teams[0].isEmpty())
                {
                    // Process team numbers (strip prefix and apply aliases)
                    for (int i = 0; i < teams.length; i++)
                    {
                        String tNum = MatchData.extractTeamNumber(teams[i]);
                        teams[i] = (m_teamAliases != null) ? m_teamAliases.resolveAlias(tNum) : tNum;
                    }

                    ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, teams);
                    m_binding.preMatchTeamNumberInput.setAdapter(teamAdapter);
                    m_binding.preMatchTeamNumberInput.setDropDownHeight(620);
                    m_binding.preMatchTeamNumberInput.showDropDown();
                }
            }
            catch (NullPointerException exception)
            {
                Log.e(TAG, "Error fetching teams for team number dropdown", exception);
            }
        }
    }

    /**
     * Autopopulates the team number field based on the match number and configured team index.
     */
    private void setTeamNumFromMatchNum()
    {
        if (m_settings == null || m_tbaMatches == null || !m_tbaMatches.isTBAMatchesLoaded())
        {
            return;
        }

        String matchNumStr = m_binding.preMatchNumberInput.getText().toString().trim();
        if (!matchNumStr.isEmpty() && !m_teamIndexStr.isEmpty() && m_settings.isValidTeamIndexStr(m_teamIndexStr))
        {
            Log.d(TAG, "setTeamNumFromMatchNum: Auto-loading team number for match " + matchNumStr + " index " + m_teamIndexStr);
            try
            {
                String[] teams = m_tbaMatches.getMatchTeams(matchNumStr);
                int teamIndex = m_settings.getTeamIndex();

                if (teamIndex < teams.length)
                {
                    String tbaTeamNum = teams[teamIndex];
                    String teamNumStr = MatchData.extractTeamNumber(tbaTeamNum);
                    Log.d(TAG, "setTeamNumFromMatchNum: Auto-loading team number for tbaTeamNum " + tbaTeamNum + " teamNumStr " + teamNumStr);
                    m_binding.preMatchTeamNumberInput.setText((m_teamAliases != null) ? m_teamAliases.resolveAlias(teamNumStr) : teamNumStr);
                }
            }
            catch (NumberFormatException e)
            {
                Log.e(TAG, "setTeamNumFromMatchNum: Error in auto-loading team number", e);
            }
        }
    }

    /**
     * Persists the entered pre-match data to the MatchData object and updates settings.
     */
    public void updatePreMatchData()
    {
        Log.d(TAG, "updatePreMatchData");
        if (m_matchData == null)
        {
            return;
        }

        String eventCode = Objects.requireNonNull(m_binding.preMatchEventCodeInput.getText()).toString().trim();
        String matchNum = m_binding.preMatchNumberInput.getText().toString().trim().toLowerCase();
        String teamNumEntry = m_binding.preMatchTeamNumberInput.getText().toString().trim();
        String scoutName = m_binding.preMatchScoutNameInput.getText().toString().trim();

        if (m_settings != null)
        {
            m_settings.setMostRecentMatchNumber(matchNum);
            m_settings.addPastScoutNames(scoutName);
            m_settings.setMostRecentScoutName(scoutName);
        }

        m_matchData.setEventCode(eventCode);
        m_matchData.setMatchNumber(matchNum);

        String teamNum = (m_teamAliases != null) ? m_teamAliases.resolveTeamNumber(teamNumEntry) : teamNumEntry;
        String teamAlias = (!teamNum.equals(teamNumEntry)) ? teamNumEntry : "";

        m_matchData.setTeamNumber(teamNum);
        m_matchData.setTeamAlias(teamAlias);

        m_matchData.setScoutName(scoutName);
        Log.i(TAG, "Updated MatchData: Team=" + teamNum + ", Alias=" + teamAlias);
    }

    /**
     * Validates that all required fields have been filled.
     *
     * @return true if data is valid, false otherwise
     */
    private boolean checkValidData()
    {
        String eventCode = Objects.requireNonNull(m_binding.preMatchEventCodeInput.getText()).toString().trim();
        String matchNum = m_binding.preMatchNumberInput.getText().toString().trim();
        String teamNum = m_binding.preMatchTeamNumberInput.getText().toString().trim();
        String scoutName = m_binding.preMatchScoutNameInput.getText().toString().trim();

        String requiredError = getString(R.string.required);
        m_binding.preMatchEventCodeLayout.setError(eventCode.isEmpty() ? requiredError : null);
        m_binding.preMatchNumberLayout.setError(matchNum.isEmpty() ? requiredError : null);
        m_binding.preMatchTeamNumberLayout.setError(teamNum.isEmpty() ? requiredError : null);
        m_binding.preMatchScoutNameLayout.setError(scoutName.isEmpty() ? requiredError : null);

        boolean isValid = !eventCode.isEmpty() && !matchNum.isEmpty() && !teamNum.isEmpty() && !scoutName.isEmpty();

        m_binding.preMatchErrorMessage.setVisibility(isValid ? View.GONE : View.VISIBLE);
        return isValid;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        m_binding = null;
    }
}
