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

package com.frc2135.frc_scout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.frc2135.frc_scout.databinding.ScoutingActivityBinding;
import com.google.android.material.badge.BadgeDrawable;

/**
 * Activity for the main scouting process. It hosts three fragments: Autonomous, Teleoperated, and Endgame.
 * Uses a {@link androidx.viewpager2.widget.ViewPager2} for swipeable navigation between these stages,
 * synchronized with a {@link com.google.android.material.bottomnavigation.BottomNavigationView}.
 * Handles match data loading, fragment state preservation, and real-time tab badging for validation.
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

        setupViewPager();
        updateActionBarTitle(0);
        updateTabBadges();

        // Handle navigation between scouting stages
        m_binding.scoutingActivityNavView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_auton)
            {
                m_binding.scoutingActivityViewPager.setCurrentItem(0);
            }
            else if (itemId == R.id.navigation_teleop)
            {
                m_binding.scoutingActivityViewPager.setCurrentItem(1);
            }
            else if (itemId == R.id.navigation_endgame)
            {
                m_binding.scoutingActivityViewPager.setCurrentItem(2);
            }
            return true;
        });
    }

    /**
     * Sets up the ViewPager2 with its adapter and a page change callback to synchronize
     * with the BottomNavigationView and update action bar titles and tab badges.
     */
    private void setupViewPager()
    {
        m_binding.scoutingActivityViewPager.setAdapter(new ScoutingPagerAdapter(this));
        m_binding.scoutingActivityViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback()
        {
            @Override
            public void onPageSelected(int position)
            {
                super.onPageSelected(position);
                updateCurrentFragmentData();

                int navItemId = R.id.navigation_auton;
                if (position == 1)
                {
                    navItemId = R.id.navigation_teleop;
                }
                else if (position == 2)
                {
                    navItemId = R.id.navigation_endgame;
                }
                m_binding.scoutingActivityNavView.setSelectedItemId(navItemId);
                updateActionBarTitle(position);
            }
        });
    }

    private static class ScoutingPagerAdapter extends FragmentStateAdapter
    {
        public ScoutingPagerAdapter(AppCompatActivity activity)
        {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position)
        {
            return switch (position)
            {
                case 1 -> new TeleopFragment();
                case 2 -> new EndgameFragment();
                default -> new AutonFragment();
            };
        }

        @Override
        public int getItemCount()
        {
            return 3;
        }
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
    private void updateActionBarTitle(int position)
    {
        if (m_matchData == null || getSupportActionBar() == null)
        {
            return;
        }

        String stage = "Scouting";
        if (position == 0)
        {
            stage = "Autonomous";
        }
        else if (position == 1)
        {
            stage = "Teleoperated";
        }
        else if (position == 2)
        {
            stage = "Endgame";
        }

        getSupportActionBar().setTitle(stage);
        getSupportActionBar().setSubtitle(String.format("Team %s - %s", m_matchData.getTeamNumber(), m_matchData.getMatchNumber()));
    }

    /**
     * Updates the MatchData object with the latest inputs from the currently visible fragment
     * and refreshes the tab badging.
     */
    public void updateCurrentFragmentData()
    {
        Log.d(TAG, "updateCurrentFragmentData");
        for (Fragment f : getSupportFragmentManager().getFragments())
        {
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
        updateTabBadges();
    }

    /**
     * Updates the badge notifications on the bottom navigation tabs based on the validity
     * of each scouting stage's data.
     */
    public void updateTabBadges()
    {
        if (m_matchData == null)
        {
            return;
        }

        updateBadge(R.id.navigation_auton, !m_matchData.validateAuton().isEmpty());
        updateBadge(R.id.navigation_teleop, !m_matchData.validateTeleop().isEmpty());
        updateBadge(R.id.navigation_endgame, !m_matchData.validateEndgame().isEmpty());
    }

    /**
     * Shows or hides a red dot badge on a specific bottom navigation tab.
     *
     * @param itemId  the menu item ID (e.g. R.id.navigation_auton)
     * @param visible true to show the badge, false to hide it
     */
    private void updateBadge(int itemId, boolean visible)
    {
        BadgeDrawable badge = m_binding.scoutingActivityNavView.getOrCreateBadge(itemId);
        badge.setVisible(visible);
        // Standard red dot style
        badge.setBackgroundColor(getColor(R.color.errorColor));
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
     * Provides access to the activity's bottom navigation view for Snackbar anchoring.
     *
     * @return the BottomNavigationView instance
     */
    public com.google.android.material.bottomnavigation.BottomNavigationView getNavView()
    {
        return m_binding.scoutingActivityNavView;
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
