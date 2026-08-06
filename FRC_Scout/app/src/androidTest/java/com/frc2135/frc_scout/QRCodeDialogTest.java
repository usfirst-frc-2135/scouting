package com.frc2135.frc_scout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.view.WindowManager;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class QRCodeDialogTest {

    @Test
    public void testBrightnessOverrideOnResume() {
        MatchData match = new MatchData();
        match.setMatchNumber("qm1");
        match.setTeamNumber("2135");
        match.setEventCode("2026casac");

        try (ActivityScenario<MatchListActivity> scenario = ActivityScenario.launch(MatchListActivity.class)) {
            scenario.onActivity(activity -> {
                QRCodeDialog dialog = QRCodeDialog.newInstance(match, false);
                dialog.show(activity.getSupportFragmentManager(), "qr_test");
            });

            // Wait for dialog to be visible
            onView(withText(org.hamcrest.Matchers.containsString("2026casac-qm1-2135"))).check(matches(isDisplayed()));

            scenario.onActivity(activity -> {
                // Find the dialog and check its window brightness
                QRCodeDialog dialog = (QRCodeDialog) activity.getSupportFragmentManager().findFragmentByTag("qr_test");
                if (dialog != null && dialog.getDialog() != null) {
                    WindowManager.LayoutParams lp = dialog.getDialog().getWindow().getAttributes();
                    assertEquals("Brightness should be set to 1.0 (FULL)", WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, lp.screenBrightness, 0.01f);
                }
            });
        }
    }
}
