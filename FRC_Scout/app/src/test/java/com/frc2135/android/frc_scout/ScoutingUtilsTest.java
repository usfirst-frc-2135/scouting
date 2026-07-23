package com.frc2135.android.frc_scout;

import static org.junit.Assert.*;
import org.junit.Test;

public class ScoutingUtilsTest {

    private static final String TAG = "ScoutingUtilsTest";

    @Test
    public void testIsValidEventCode() {
        // Valid event codes
        assertTrue(ScoutingUtils.isValidEventCode(TAG, "2025casac"));
        assertTrue(ScoutingUtils.isValidEventCode(TAG, "2026cur"));
        assertTrue(ScoutingUtils.isValidEventCode(TAG, "2027wrb"));

        // Invalid: too short
        assertFalse(ScoutingUtils.isValidEventCode(TAG, "2025ca"));
        assertFalse(ScoutingUtils.isValidEventCode(TAG, "2027wr"));
        
        // Invalid: too long
        assertFalse(ScoutingUtils.isValidEventCode(TAG, "2025casacas"));

        // Invalid: format
        assertFalse(ScoutingUtils.isValidEventCode(TAG, "abcdcasac"));
        assertFalse(ScoutingUtils.isValidEventCode(TAG, "2025 CAS"));

        // Invalid: year out of range
        assertFalse(ScoutingUtils.isValidEventCode(TAG, "2024casac"));
        assertFalse(ScoutingUtils.isValidEventCode(TAG, "2028casac"));
        
        // Invalid: null/empty
        assertFalse(ScoutingUtils.isValidEventCode(TAG, null));
        assertFalse(ScoutingUtils.isValidEventCode(TAG, ""));
    }

    @Test
    public void testIsValidMatchNumber() {
        // Valid match numbers
        assertTrue(ScoutingUtils.isValidMatchNumber(TAG, "qm1"));
        assertTrue(ScoutingUtils.isValidMatchNumber(TAG, "qm130"));
        assertTrue(ScoutingUtils.isValidMatchNumber(TAG, "sf1"));
        assertTrue(ScoutingUtils.isValidMatchNumber(TAG, "sf13"));
        assertTrue(ScoutingUtils.isValidMatchNumber(TAG, "f1"));
        assertTrue(ScoutingUtils.isValidMatchNumber(TAG, "f3"));

        // Invalid: too short
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, "q"));
        
        // Invalid: too long
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, "qm1234"));

        // Invalid: competition level
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, "pr1"));
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, "xyz1"));

        // Invalid: number out of range
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, "qm0"));
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, "qm131"));
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, "sf0"));
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, "sf14"));
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, "f0"));
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, "f4"));

        // Invalid: not a number
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, "qmabc"));
        
        // Invalid: null/empty
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, null));
        assertFalse(ScoutingUtils.isValidMatchNumber(TAG, ""));
    }

    @Test
    public void testIsValidTeamNumber() {
        // Valid team numbers
        assertTrue(ScoutingUtils.isValidTeamNumber(TAG, "1"));
        assertTrue(ScoutingUtils.isValidTeamNumber(TAG, "2135"));
        assertTrue(ScoutingUtils.isValidTeamNumber(TAG, "10212"));
        assertTrue(ScoutingUtils.isValidTeamNumber(TAG, "2135A"));
        assertTrue(ScoutingUtils.isValidTeamNumber(TAG, "12345B"));

        // Invalid: too long
        assertFalse(ScoutingUtils.isValidTeamNumber(TAG, "1234567"));

        // Invalid: format
        assertFalse(ScoutingUtils.isValidTeamNumber(TAG, "A2135"));
        assertFalse(ScoutingUtils.isValidTeamNumber(TAG, "21 35"));
        assertFalse(ScoutingUtils.isValidTeamNumber(TAG, "2135AB"));

        // Invalid: null/empty
        assertFalse(ScoutingUtils.isValidTeamNumber(TAG, null));
        assertFalse(ScoutingUtils.isValidTeamNumber(TAG, ""));
    }
}
