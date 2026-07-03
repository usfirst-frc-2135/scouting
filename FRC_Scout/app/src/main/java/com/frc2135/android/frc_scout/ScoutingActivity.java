package com.frc2135.android.frc_scout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.frc2135.android.frc_scout.databinding.ScoutingActivityTabbedBinding;

/**
 * Activity for the main scouting process. It hosts three fragments: Autonomous, Teleoperated, and Endgame.
 * Uses a {@link com.google.android.material.bottomnavigation.BottomNavigationView} for navigation between these stages.
 */
public class ScoutingActivity extends AppCompatActivity
{
    private static final String TAG = "ScoutingActivity";

    private MatchData m_matchData;

    private ScoutingActivityTabbedBinding m_binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Preferences.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        m_binding = ScoutingActivityTabbedBinding.inflate(getLayoutInflater());
        setContentView(m_binding.getRoot());

        setSupportActionBar(m_binding.toolbar);

        String matchId = getIntent().getStringExtra("match_ID");
        Log.d(TAG, "Loading match with ID: " + matchId);
        m_matchData = MatchListData.getInstance(getApplicationContext()).getMatch(matchId);

        updateActionBarTitle();

        // Initializes FragmentManager to host the scouting fragments
        FragmentManager fm = getSupportFragmentManager();

        // Load the initial fragment (Autonomous) if none exists
        if (fm.findFragmentById(R.id.fragmentContainer) == null)
        {
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, createScoutingActivityFragment())
                    .commit();
        }

        // Handle navigation between scouting stages
        m_binding.navView.setOnItemSelectedListener(item -> {
            // Save data from the current fragment before switching
            updateCurrentFragmentData();

            int itemId = item.getItemId();
            Fragment fragment = null;

            if (itemId == R.id.navigation_auton)
            {
                fragment = new AutonFragment();
            }
            else if (itemId == R.id.navigation_teleop)
            {
                fragment = new TeleopFragment();
            }
            else if (itemId == R.id.navigation_endgame)
            {
                fragment = new EndgameFragment();
            }

            if (fragment != null)
            {
                fm.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                updateActionBarTitle();
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu");
        // Future: getMenuInflater().inflate(R.menu.scouting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        Log.d(TAG, "onOptionsItemsSelected");
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the action bar title based on current scouting stage and team/match info.
     */
    private void updateActionBarTitle()
    {
        if (m_matchData == null || getSupportActionBar() == null)
        {
            return;
        }

        String stage = "Scouting";
        int selectedId = m_binding.navView.getSelectedItemId();
        if (selectedId == R.id.navigation_auton)
        {
            stage = "Autonomous";
        }
        else if (selectedId == R.id.navigation_teleop)
        {
            stage = "Teleoperated";
        }
        else if (selectedId == R.id.navigation_endgame)
        {
            stage = "Endgame";
        }

        getSupportActionBar().setTitle(stage);
        m_binding.toolbarTitle.setText(String.format("Team %s - %s", m_matchData.getTeamNumber(), m_matchData.getMatchNumber()));
    }

    /**
     * Updates the MatchData object with the latest inputs from the currently visible fragment.
     */
    private void updateCurrentFragmentData()
    {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof AutonFragment)
        {
            ((AutonFragment) f).updateAutonData();
        }
        else if (f instanceof TeleopFragment)
        {
            ((TeleopFragment) f).updateTeleopData();
        }
        else if (f instanceof EndgameFragment)
        {
            ((EndgameFragment) f).updateEndgameData();
        }
    }

    /**
     * Creates the initial fragment for this activity.
     *
     * @return a new instance of {@link AutonFragment}
     */
    protected Fragment createScoutingActivityFragment()
    {
        return new AutonFragment();
    }

    /**
     * Provides access to the current {@link MatchData} for the hosted fragments.
     *
     * @return the current MatchData object
     */
    protected MatchData getCurrentMatch()
    {
        return m_matchData;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause() - updating data");
        updateCurrentFragmentData();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        m_binding = null;
    }
}
