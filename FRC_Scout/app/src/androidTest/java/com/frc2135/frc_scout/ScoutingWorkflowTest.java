package com.frc2135.frc_scout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.chip.Chip;
import com.google.android.material.slider.Slider;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ScoutingWorkflowTest {

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        ScoutedMatches.getInstance(context).deleteAllMatches();
        Settings.getInstance(context).setEventCode("2026casac");
    }

    @Test
    public void testFullScoutingLoop() {
        try (ActivityScenario<MatchListActivity> scenario = ActivityScenario.launch(MatchListActivity.class)) {
            // 1. Start new match from list
            onView(withId(R.id.match_list_start_match_fab)).perform(click());

            // 2. Fill Pre-Match info
            onView(withId(R.id.pre_match_number_input)).perform(replaceText("qm1"));
            onView(withId(R.id.pre_match_team_number_input)).perform(replaceText("2135"));
            onView(withId(R.id.pre_match_scout_name_input)).perform(replaceText("Test S"), closeSoftKeyboard());
            onView(withId(R.id.pre_match_start_button)).perform(click());

            // 3. Move through scouting fragments (verify badges/tabs)
            // Wait for ScoutingActivity to load (Auton tab selected)
            onView(withId(R.id.navigation_auton)).check(matches(isDisplayed()));

            onView(withId(R.id.scouting_activity_view_pager)).perform(swipeLeft());
            
            // In Teleop - Use custom actions to set mandatory fields
            onView(withId(R.id.teleop_pass_nz_chip)).perform(scrollTo(), setChipChecked(true));
            onView(withId(R.id.teleop_passing_effectiveness_slider)).perform(scrollTo(), setSliderValue(1.0f));

            // Move to Endgame
            onView(withId(R.id.scouting_activity_view_pager)).perform(swipeLeft());
            
            // Wait for swiping and model update
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

            // In Endgame - Use custom action to set mandatory field (driver ability)
            onView(withId(R.id.endgame_driving_ability_slider)).perform(scrollTo(), setSliderValue(3.0f));
            
            // 4. Finalize
            // Scroll to QR button and click it
            onView(withId(R.id.endgame_generate_qr_button)).perform(scrollTo(), click());
            
            // Wait for dialog to appear (animations enabled on device can delay this)
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

            // Verify QR dialog shown
            onView(withId(R.id.qr_dialog_done_button)).check(matches(isDisplayed()));
            
            // 5. Complete match
            onView(withId(R.id.qr_dialog_done_button)).perform(click());

            // Verify back in MatchList and match is present
            onView(withText("Match qm1")).check(matches(isDisplayed()));
        }
    }

    public static ViewAction setSliderValue(final float value) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "set Slider value to " + value;
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((Slider) view).setValue(value);
            }
        };
    }

    public static ViewAction setChipChecked(final boolean checked) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "set Chip checked to " + checked;
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((Chip) view).setChecked(checked);
            }
        };
    }
}
