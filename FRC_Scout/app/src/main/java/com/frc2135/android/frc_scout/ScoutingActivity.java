/*
 * Copyright (c) 2020-26 FRC 2135 Presentation Invasion
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.frc2135.android.frc_scout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.frc2135.android.frc_scout.databinding.ScoutingActivityBinding;

/**
 * Activity for the main scouting process. It hosts three fragments: Autonomous, Teleoperated, and Endgame.
 * Uses a {@link com.google.android.material.bottomnavigation.BottomNavigationView} for navigation between these stages.
 * Handles match data loading and fragment state preservation.
 */
public class ScoutingActivity extends AppCompatActivity
{
    private static final String TAG = "ScoutingActivity";

    private MatchData m_matchData;

    private ScoutingActivityBinding m_binding;

    /**
     * Initializes the activity, sets up the toolbar, and loads the initial scouting fragment.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Preferences.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        m_binding = ScoutingActivityBinding.inflate(getLayoutInflater());
        setContentView(m_binding.getRoot());

        setSupportActionBar(m_binding.scoutingActivityToolbar);

        String matchId = getIntent().getStringExtra(Constants.MATCH_ID);
        Log.i(TAG, "Loading match ID: " + matchId);
        m_matchData = ScoutedMatches.getInstance(getApplicationContext()).getMatch(matchId);

        updateActionBarTitle();

        // Initializes FragmentManager to host the scouting fragments
        FragmentManager fm = getSupportFragmentManager();

        // Load the initial fragment (Autonomous) if none exists
        if (fm.findFragmentById(R.id.scouting_activity_fragment_container) == null)
        {
            fm.beginTransaction()
                    .add(R.id.scouting_activity_fragment_container, createScoutingActivityFragment())
                    .commit();
        }

        // Handle navigation between scouting stages
        m_binding.scoutingActivityNavView.setOnItemSelectedListener(item -> {
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
                        .replace(R.id.scouting_activity_fragment_container, fragment)
                        .commit();
                updateActionBarTitle();
                return true;
            }
            return false;
        });
    }

    /**
     * Initializes the contents of the Activity's standard option menu.
     *
     * @param menu the options menu in which you place your items
     * @return true for the menu to be displayed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.v(TAG, "onCreateOptionsMenu");
        // Future: getMenuInflater().inflate(R.menu.scouting_menu, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item the menu item that was selected
     * @return true if the event was handled here
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        Log.v(TAG, "onOptionsItemSelected");
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
        int selectedId = m_binding.scoutingActivityNavView.getSelectedItemId();
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
        getSupportActionBar().setSubtitle(String.format("Team %s - %s", m_matchData.getTeamNumber(), m_matchData.getMatchNumber()));
    }

    /**
     * Updates the MatchData object with the latest inputs from the currently visible fragment.
     */
    private void updateCurrentFragmentData()
    {
        Log.d(TAG, "updateCurrentFragmentData");
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.scouting_activity_fragment_container);
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
     * Called when the activity is no longer interacting with the user.
     * Triggers a final update of fragment data.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.v(TAG, "onPause");
        updateCurrentFragmentData();
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
