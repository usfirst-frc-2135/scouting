package com.frc2135.frc_scout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MatchListFragmentTest {

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        ScoutedMatches.getInstance(context).deleteAllMatches();
        
        MatchData match = new MatchData();
        match.setMatchNumber("qm1");
        match.setTeamNumber("2135");
        match.setEventCode("2026casac");
        match.setScoutName("Test Scout");
        ScoutedMatches.getInstance(context).addMatch(match);
        ScoutedMatches.getInstance(context).saveMatchDataFile(match);
    }

    @Test
    public void testCardActionMenu() {
        try (ActivityScenario<MatchListActivity> scenario = ActivityScenario.launch(MatchListActivity.class)) {
            // Check that the match is displayed
            onView(withText("Match qm1")).check(matches(isDisplayed()));

            // Click the three-dot menu button
            onView(withId(R.id.match_card_menu_button)).perform(click());

            // Check if "Display Match" option is visible
            onView(withText("Display Match")).check(matches(isDisplayed()));

            // Click "Display Match"
            onView(withText("Display Match")).perform(click());

            // Verify that the debug dialog (Match Data) is shown
            onView(withText("Match Data")).check(matches(isDisplayed()));
            
            // Verify internal data is present in the dialog
            onView(withText(org.hamcrest.Matchers.containsString("Team Number"))).check(matches(isDisplayed()));

            // Close dialog
            onView(withText("OK")).perform(click());
        }
    }

    @Test
    public void testUndoDeleteMatch() {
        try (ActivityScenario<MatchListActivity> scenario = ActivityScenario.launch(MatchListActivity.class)) {
            // Check that the match is displayed
            onView(withText("Match qm1")).check(matches(isDisplayed()));

            // Click the three-dot menu button
            onView(withId(R.id.match_card_menu_button)).perform(click());

            // Click "Delete Match"
            onView(withText("Delete Match")).perform(click());

            // Confirm deletion in the dialog
            onView(withText("Delete")).perform(click());

            // Verify match is gone (using doesNotExist because it should be removed from RecyclerView)
            onView(withText("Match qm1")).check(doesNotExist());

            // Verify "Match deleted" snackbar shown
            onView(withText("Match deleted")).check(matches(isDisplayed()));

            // Click "Undo" on the snackbar
            onView(withText("Undo")).perform(click());

            // Verify match is back
            onView(withText("Match qm1")).check(matches(isDisplayed()));
        }
    }
}
