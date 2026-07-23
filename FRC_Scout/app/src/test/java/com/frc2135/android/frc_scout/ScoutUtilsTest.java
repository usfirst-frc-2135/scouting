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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ScoutUtilsTest
{

    private static final String TAG = "ScoutingUtilsTest";

    @Test
    public void testIsValidEventCode()
    {
        // Valid event codes
        assertTrue(ScoutUtils.isValidEventCode(TAG, "2025casac"));
        assertTrue(ScoutUtils.isValidEventCode(TAG, "2026cur"));
        assertTrue(ScoutUtils.isValidEventCode(TAG, "2027wrb"));

        // Invalid: too short
        assertFalse(ScoutUtils.isValidEventCode(TAG, "2025ca"));
        assertFalse(ScoutUtils.isValidEventCode(TAG, "2027wr"));

        // Invalid: too long
        assertFalse(ScoutUtils.isValidEventCode(TAG, "2025casacas"));

        // Invalid: format
        assertFalse(ScoutUtils.isValidEventCode(TAG, "abcdcasac"));
        assertFalse(ScoutUtils.isValidEventCode(TAG, "2025 CAS"));

        // Invalid: year out of range
        assertFalse(ScoutUtils.isValidEventCode(TAG, "2024casac"));
        assertFalse(ScoutUtils.isValidEventCode(TAG, "2028casac"));

        // Invalid: null/empty
        assertFalse(ScoutUtils.isValidEventCode(TAG, null));
        assertFalse(ScoutUtils.isValidEventCode(TAG, ""));
    }

    @Test
    public void testIsValidMatchNumber()
    {
        // Valid match numbers
        assertTrue(ScoutUtils.isValidMatchNumber(TAG, "qm1"));
        assertTrue(ScoutUtils.isValidMatchNumber(TAG, "qm130"));
        assertTrue(ScoutUtils.isValidMatchNumber(TAG, "sf1"));
        assertTrue(ScoutUtils.isValidMatchNumber(TAG, "sf13"));
        assertTrue(ScoutUtils.isValidMatchNumber(TAG, "f1"));
        assertTrue(ScoutUtils.isValidMatchNumber(TAG, "f3"));

        // Invalid: too short
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, "q"));

        // Invalid: too long
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, "qm1234"));

        // Invalid: competition level
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, "pr1"));
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, "xyz1"));

        // Invalid: number out of range
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, "qm0"));
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, "qm151"));
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, "sf0"));
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, "sf14"));
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, "f0"));
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, "f4"));

        // Invalid: not a number
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, "qmabc"));

        // Invalid: null/empty
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, null));
        assertFalse(ScoutUtils.isValidMatchNumber(TAG, ""));
    }

    @Test
    public void testIsValidTeamNumber()
    {
        // Valid team numbers
        assertTrue(ScoutUtils.isValidTeamNumber(TAG, "1"));
        assertTrue(ScoutUtils.isValidTeamNumber(TAG, "2135"));
        assertTrue(ScoutUtils.isValidTeamNumber(TAG, "10212"));
        assertTrue(ScoutUtils.isValidTeamNumber(TAG, "2135A"));
        assertTrue(ScoutUtils.isValidTeamNumber(TAG, "12345B"));

        // Invalid: too long
        assertFalse(ScoutUtils.isValidTeamNumber(TAG, "1234567"));

        // Invalid: format
        assertFalse(ScoutUtils.isValidTeamNumber(TAG, "A2135"));
        assertFalse(ScoutUtils.isValidTeamNumber(TAG, "21 35"));
        assertFalse(ScoutUtils.isValidTeamNumber(TAG, "2135AB"));

        // Invalid: null/empty
        assertFalse(ScoutUtils.isValidTeamNumber(TAG, null));
        assertFalse(ScoutUtils.isValidTeamNumber(TAG, ""));
    }

    @Test
    public void testIsValidScoutName()
    {
        // Valid scout names
        assertTrue(ScoutUtils.isValidScoutName(TAG, "John S"));
        assertTrue(ScoutUtils.isValidScoutName(TAG, "A B"));

        // Valid with extra spaces (should be cleaned before validation)
        assertTrue(ScoutUtils.isValidScoutName(TAG, "John  S"));
        assertTrue(ScoutUtils.isValidScoutName(TAG, " John S "));
        assertTrue(ScoutUtils.isValidScoutName(TAG, "  A   B  "));

        // Invalid: first name not capitalized
        assertFalse(ScoutUtils.isValidScoutName(TAG, "john S"));

        // Invalid: last name not capitalized
        assertFalse(ScoutUtils.isValidScoutName(TAG, "John s"));

        // Invalid: both not capitalized
        assertFalse(ScoutUtils.isValidScoutName(TAG, "john s"));

        // Invalid: too short/no space
        assertFalse(ScoutUtils.isValidScoutName(TAG, "John"));
        assertFalse(ScoutUtils.isValidScoutName(TAG, "S"));

        // Invalid: too many characters in last name
        assertFalse(ScoutUtils.isValidScoutName(TAG, "John Smith"));

        // Invalid: ends with special character
        assertFalse(ScoutUtils.isValidScoutName(TAG, "John S."));

        // Invalid: null/empty/only spaces
        assertFalse(ScoutUtils.isValidScoutName(TAG, null));
        assertFalse(ScoutUtils.isValidScoutName(TAG, ""));
        assertFalse(ScoutUtils.isValidScoutName(TAG, "   "));
    }
}
