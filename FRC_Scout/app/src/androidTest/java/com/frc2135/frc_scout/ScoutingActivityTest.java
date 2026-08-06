package com.frc2135.frc_scout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ScoutingActivityTest {

    private String m_matchId;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        MatchData match = new MatchData();
        match.setMatchNumber("qm1");
        match.setTeamNumber("2135");
        match.setEventCode("2026casac");
        match.setScoutName("Test Scout");
        ScoutedMatches.getInstance(context).addMatch(match);
        m_matchId = match.getMatchID();
    }

    @Test
    public void testNavigationAndSwiping() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ScoutingActivity.class);
        intent.putExtra(Constants.MATCH_ID, m_matchId);

        try (ActivityScenario<ScoutingActivity> scenario = ActivityScenario.launch(intent)) {
            // Check initial state (Autonomous tab selected)
            onView(withId(R.id.navigation_auton)).check(matches(isDisplayed()));

            // Swipe to Teleoperated
            onView(withId(R.id.scouting_activity_view_pager)).perform(swipeLeft());
            onView(withId(R.id.navigation_teleop)).check(matches(isDisplayed()));

            // Swipe to Endgame
            onView(withId(R.id.scouting_activity_view_pager)).perform(swipeLeft());
            onView(withId(R.id.navigation_endgame)).check(matches(isDisplayed()));

            // Swipe back to Teleoperated
            onView(withId(R.id.scouting_activity_view_pager)).perform(swipeRight());
            onView(withId(R.id.navigation_teleop)).check(matches(isDisplayed()));

            // Click BottomNav for Autonomous
            onView(withId(R.id.navigation_auton)).perform(click());
            onView(withId(R.id.navigation_auton)).check(matches(isDisplayed()));
        }
    }
}
