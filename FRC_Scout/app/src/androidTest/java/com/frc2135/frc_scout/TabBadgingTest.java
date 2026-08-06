package com.frc2135.frc_scout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TabBadgingTest {

    private String m_matchId;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        MatchData match = new MatchData();
        match.setMatchNumber("qm1");
        match.setTeamNumber("2135");
        match.setEventCode("2026casac");
        match.setScoutName("Test S");
        ScoutedMatches.getInstance(context).addMatch(match);
        m_matchId = match.getMatchID();
    }

    @Test
    public void testTabBadgingVisibility() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ScoutingActivity.class);
        intent.putExtra(Constants.MATCH_ID, m_matchId);

        try (ActivityScenario<ScoutingActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                BottomNavigationView navView = activity.findViewById(R.id.scouting_activity_nav_view);
                
                // Teleop should have badges initially (since required data like Driver Ability is missing)
                BadgeDrawable teleopBadge = navView.getBadge(R.id.navigation_teleop);
                assertTrue("Teleop should have badge initially", teleopBadge != null && teleopBadge.isVisible());
            });

            // Navigate to Teleop
            onView(withId(R.id.scouting_activity_view_pager)).perform(swipeLeft());
            
            // Wait for swiping and fragment attachment
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

            // Use onActivity to update the UI directly (bypass Espresso flakiness on legacy hardware)
            scenario.onActivity(activity -> {
                // Find TeleopFragment
                TeleopFragment teleopFragment = null;
                for (androidx.fragment.app.Fragment f : activity.getSupportFragmentManager().getFragments()) {
                    if (f instanceof TeleopFragment) {
                        teleopFragment = (TeleopFragment) f;
                        break;
                    }
                }
                
                if (teleopFragment != null && teleopFragment.getView() != null) {
                    // Update UI components directly via their parent RadioGroups/Views
                    android.widget.RadioGroup rgAbility = teleopFragment.getView().findViewById(R.id.teleop_driving_ability_radio_group);
                    rgAbility.check(R.id.teleop_driving_ability_avg);
                    
                    android.widget.RadioGroup rgRate = teleopFragment.getView().findViewById(R.id.teleop_passing_rate_radio_group);
                    rgRate.check(R.id.teleop_passing_rate_medium);
                    
                    com.google.android.material.chip.Chip chipNz = teleopFragment.getView().findViewById(R.id.teleop_pass_nz_chip);
                    chipNz.setChecked(true);
                    
                    com.google.android.material.chip.Chip chipAz = teleopFragment.getView().findViewById(R.id.teleop_pass_az_chip);
                    chipAz.setChecked(true);
                    
                    // Trigger data sync - this updates MatchData and calls updateTabBadges()
                    activity.updateCurrentFragmentData();
                }
            });

            // Wait for UI and badges to update
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

            scenario.onActivity(activity -> {
                BottomNavigationView navView = activity.findViewById(R.id.scouting_activity_nav_view);
                BadgeDrawable teleopBadge = navView.getBadge(R.id.navigation_teleop);
                
                boolean isVisible = (teleopBadge != null && teleopBadge.isVisible());
                String validation = activity.getCurrentMatch().validateTeleop();
                assertFalse("Teleop badge should be hidden when valid. Validation: " + validation, isVisible);
            });
        }
    }
}
