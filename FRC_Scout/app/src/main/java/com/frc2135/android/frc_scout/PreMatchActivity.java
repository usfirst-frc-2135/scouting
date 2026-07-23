package com.frc2135.android.frc_scout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.frc2135.android.frc_scout.databinding.PreMatchActivityBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Objects;

/**
 * Activity for configuring match-specific metadata before starting a scouting session.
 * <p>
 * This activity handles the entry of the event code, match number, team number, and scout name.
 * It features Material Design components, including validated text inputs and an autocomplete
 * field for scout names. It also integrates with The Blue Alliance (TBA) schedule data to
 * provide intelligent autopopulation and dropdown selection for team numbers based on the
 * current match and assigned team index.
 * <p>
 * It supports both "New Match" and "Edit Match" modes, with appropriate field locking and
 * data reload capabilities to ensure data integrity.
 */
public class PreMatchActivity extends AppCompatActivity
{
    private static final String TAG = "PreMatchActivity";
    private PreMatchActivityBinding m_binding;
    private TBASchedule m_tbaSchedule;
    private MatchData m_matchData;
    private TeamAliases m_teamAliases;
    private ScoutNames m_scoutNames;
    private String m_teamIndexStr;
    private Settings m_settings;
    private boolean m_isEditMode;

    /**
     * Initializes the activity, sets up view binding, and configures UI components.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down
     */
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

        // Add back button confirmation logic
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                showExitConfirmationDialog();
            }
        });
    }

    /**
     * Loads initial data from Intents, application settings, and singleton instances.
     */
    private void loadInitialData()
    {
        String matchId = getIntent().getStringExtra(Constants.MATCH_ID);
        m_matchData = ScoutedMatches.getInstance(getApplicationContext()).getMatch(matchId);

        String eventCode = (m_matchData != null) ? m_matchData.getEventCode().trim() : "";
        m_tbaSchedule = TBASchedule.getInstance(getApplicationContext(), eventCode, false);
        m_teamAliases = TeamAliases.getInstance(getApplicationContext());

        m_settings = Settings.getInstance(getApplicationContext());
        m_teamIndexStr = (m_settings != null) ? m_settings.getTeamIndexStr() : "unknown";

        m_scoutNames = ScoutNames.getInstance(getApplicationContext());

        String inEdit = getIntent().getStringExtra(Constants.IN_EDIT_MODE);
        m_isEditMode = "yes".equalsIgnoreCase(inEdit);
    }

    /**
     * Configures the action bar title and subtitle with the current team index.
     */
    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(R.string.pre_match);
            actionBar.setSubtitle(getString(R.string.team_index_label, m_teamIndexStr));
        }
    }

    /**
     * Sets default values and initial hints for the input fields.
     */
    private void setupViewDefaults()
    {
        m_binding.preMatchErrorMessage.setVisibility(View.GONE);

        if (m_matchData != null)
        {
            m_binding.preMatchEventCodeInput.setText(m_matchData.getEventCode());

            if (m_isEditMode)
            {
                Log.d(TAG, "Activity in Edit Mode: loading and disabling match and team number fields");
                m_binding.preMatchNumberInput.setText(m_matchData.getMatchNumber());
                m_binding.preMatchNumberInput.setEnabled(false);
                m_binding.preMatchTeamNumberInput.setText(m_matchData.getTeamNumber());
                m_binding.preMatchTeamNumberInput.setEnabled(false);
                m_binding.preMatchScoutNameInput.setText(m_matchData.getScoutName());
            }
            else
            {
                Log.d(TAG, "Activity in New Match Mode: loading and enabling match and team number fields");
                m_binding.preMatchNumberInput.setText(getNewMatchNumber());
                m_binding.preMatchNumberInput.setEnabled(true);
                if (!m_matchData.getTeamNumber().isEmpty())
                {
                    m_binding.preMatchTeamNumberInput.setText(m_matchData.getTeamNumber());
                }
                else
                {
                    setTeamNumFromMatchNum();
                }
                m_binding.preMatchTeamNumberInput.setEnabled(true);
                m_binding.preMatchScoutNameInput.setText(getNewScoutName());
            }
        }
        else
        {
            Log.e(TAG, "Match data is null, setting default values");
            m_binding.preMatchEventCodeInput.setText(R.string.missing);
            m_binding.preMatchEventCodeInput.setEnabled(true);
            m_binding.preMatchTeamNumberInput.setText(R.string.missing);
            m_binding.preMatchTeamNumberInput.setEnabled(true);
            m_binding.preMatchNumberInput.setText(R.string.missing);
            m_binding.preMatchNumberInput.setEnabled(true);
            m_binding.preMatchScoutNameInput.setText(R.string.missing);
            m_binding.preMatchScoutNameInput.setEnabled(true);
        }
    }

    /**
     * Determines the initial match number to display based on existing data or settings.
     *
     * @return the match number string (e.g., "qm1")
     */
    private String getNewMatchNumber()
    {
        return (!m_matchData.getMatchNumber().isEmpty())
                ? m_matchData.getMatchNumber()
                : (m_settings != null && !m_settings.getMostRecentMatchNumber().isEmpty())
                  ? m_settings.getNextExpectedMatchNumber()
                  : "qm1";
    }

    /**
     * Determines the initial scout name to display based on existing data or settings.
     *
     * @return the scout name string
     */
    private String getNewScoutName()
    {
        return (!m_matchData.getScoutName().isEmpty())
                ? m_matchData.getScoutName()
                : (m_settings != null && !m_settings.getMostRecentScoutName().isEmpty())
                  ? m_settings.getMostRecentScoutName()
                  : "";
    }

    /**
     * Attaches text watchers and click listeners to the input components.
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
            ArrayAdapter<String> scoutAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, scoutNames);
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
                intent.putExtra(Constants.MATCH_ID, m_matchData.getMatchID());
                startActivity(intent);
            }
        });

        m_binding.preMatchCancelButton.setOnClickListener(view -> showExitConfirmationDialog());
    }

    /**
     * Shows a drop-down list of possible team numbers for the current match based on event data.
     */
    private void showTeamNumberDropDown()
    {
        Log.d(TAG, "Showing team number drop down");
        String matchNumStr = m_binding.preMatchNumberInput.getText().toString().trim().toLowerCase();

        if (!matchNumStr.isEmpty() && m_tbaSchedule != null && m_tbaSchedule.isTBAScheduleLoaded())
        {
            try
            {
                String[] teams = m_tbaSchedule.getMatchTeams(matchNumStr);
                if (teams.length > 0 && !teams[0].isEmpty())
                {
                    // Process team numbers for aliases
                    for (int i = 1; i < teams.length; i++)
                    {
                        teams[i] = m_teamAliases.getAliasForTeamNum(teams[i]);
                    }

                    ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, teams);
                    m_binding.preMatchTeamNumberInput.setAdapter(teamAdapter);
                    m_binding.preMatchTeamNumberInput.setDropDownHeight(500);
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
     * Autopopulates the team number field based on the match number and current team index.
     */
    private void setTeamNumFromMatchNum()
    {
        if (m_settings == null || m_tbaSchedule == null || !m_tbaSchedule.isTBAScheduleLoaded())
        {
            return;
        }

        String matchNumStr = m_binding.preMatchNumberInput.getText().toString().trim();
        if (!matchNumStr.isEmpty() && !m_teamIndexStr.isEmpty() && m_settings.isValidTeamIndexStr(m_teamIndexStr))
        {
            Log.d(TAG, "setTeamNumFromMatchNum: Auto-loading all team numbers for match " + matchNumStr + " index " + m_teamIndexStr);
            try
            {
                String[] matchTeams = m_tbaSchedule.getMatchTeams(matchNumStr);
                int teamIndex = m_settings.getTeamIndex();
                if (teamIndex < matchTeams.length)
                {
                    String tbaTeamNum = matchTeams[teamIndex];
                    Log.d(TAG, "setTeamNumFromMatchNum: Auto-loading team number for tbaTeamNum " + tbaTeamNum);
                    m_binding.preMatchTeamNumberInput.setText(m_teamAliases.getAliasForTeamNum(tbaTeamNum));
                }
            }
            catch (NumberFormatException e)
            {
                Log.e(TAG, "setTeamNumFromMatchNum: Error in auto-loading team number", e);
            }
        }
    }

    /**
     * Persists the entered pre-match data to the current {@link MatchData} object and updates settings.
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
        String scoutName = ScoutUtils.normalizeScoutName(Objects.requireNonNull(m_binding.preMatchScoutNameInput.getText()).toString());

        m_matchData.setEventCode(eventCode);
        m_matchData.setMatchNumber(matchNum);

        String teamNum = m_teamAliases.getTeamNumForAlias(teamNumEntry);
        String teamAlias = (!teamNum.equals(teamNumEntry)) ? teamNumEntry : "";

        m_matchData.setTeamNumber(teamNum);
        m_matchData.setTeamAlias(teamAlias);

        m_matchData.setScoutName(scoutName);
        Log.i(TAG, "Updated MatchData: Team = " + teamNum + ", Alias = " + teamAlias);
    }

    /**
     * Validates that all required fields (event code, match number, team number, scout name) have been filled.
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
        String formatError = getString(R.string.invalid_format);

        boolean isEventCodeValid = ScoutUtils.isValidEventCode(TAG, eventCode);
        m_binding.preMatchEventCodeLayout.setError(eventCode.isEmpty() ? requiredError : (!isEventCodeValid ? formatError : null));

        boolean isMatchNumValid = ScoutUtils.isValidMatchNumber(TAG, matchNum);
        m_binding.preMatchNumberLayout.setError(matchNum.isEmpty() ? requiredError : (!isMatchNumValid ? formatError : null));

        // Note: teamNum might be an alias, so we check the underlying team number.
        String underlyingTeamNum = m_teamAliases.getTeamNumForAlias(teamNum);
        boolean isTeamNumValid = ScoutUtils.isValidTeamNumber(TAG, underlyingTeamNum);
        m_binding.preMatchTeamNumberLayout.setError(teamNum.isEmpty() ? requiredError : (!isTeamNumValid ? formatError : null));

        boolean isScoutNameValid = ScoutUtils.isValidScoutName(TAG, scoutName);
        m_binding.preMatchScoutNameLayout.setError(scoutName.isEmpty() ? requiredError : (!isScoutNameValid ? formatError : null));

        boolean isValid = isEventCodeValid && isMatchNumValid && isTeamNumValid && isScoutNameValid;

        m_binding.preMatchErrorMessage.setVisibility(isValid ? View.GONE : View.VISIBLE);
        return isValid;
    }

    /**
     * Shows a confirmation dialog when the user tries to exit the pre-match entry process.
     * If not in edit mode, the current match data will be deleted upon confirmation.
     */
    private void showExitConfirmationDialog()
    {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Abandon Scouting Data?")
                .setMessage("Are you sure you want to go back? All entered match data will be lost.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (m_matchData != null)
                    {
                        if (m_isEditMode)
                        {
                            Log.i(TAG, "Reloading match data from file for ID: " + m_matchData.getMatchID());
                            if (!ScoutedMatches.getInstance(getApplicationContext()).reloadMatchDataFromFile(m_matchData))
                            {
                                Log.e(TAG, "Failed to reload match data for ID: " + m_matchData.getMatchID());
                            }
                        }
                        else
                        {
                            Log.i(TAG, "Deleting match data for ID: " + m_matchData.getMatchID());
                            ScoutedMatches.getInstance(getApplicationContext()).deleteMatch(m_matchData);
                        }
                    }
                    startActivity(new Intent(this, MatchListActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    /**
     * Perform any final cleanup before an activity is destroyed.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        m_binding = null;
    }
}
