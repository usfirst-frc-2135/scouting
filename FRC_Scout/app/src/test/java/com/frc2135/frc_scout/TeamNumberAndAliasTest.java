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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;

public class TeamNumberAndAliasTest
{
    @Test
    public void testNormalizeTeamNumber()
    {
        // Lowercase letter normalization to uppercase
        assertEquals("581B", ScoutUtils.normalizeTeamNumber("581b"));
        assertEquals("2135C", ScoutUtils.normalizeTeamNumber("2135c"));

        // Already uppercase letter
        assertEquals("581B", ScoutUtils.normalizeTeamNumber("581B"));

        // Integer-only team numbers remain unchanged
        assertEquals("2135", ScoutUtils.normalizeTeamNumber("2135"));
        assertEquals("581", ScoutUtils.normalizeTeamNumber("581"));

        // Trimming whitespace
        assertEquals("581B", ScoutUtils.normalizeTeamNumber("  581b  "));

        // Null and empty
        assertEquals("", ScoutUtils.normalizeTeamNumber(null));
        assertEquals("", ScoutUtils.normalizeTeamNumber(""));
    }

    @Test
    public void testTeamNumberRootSeparation()
    {
        String teamWithSuffix = ScoutUtils.normalizeTeamNumber("581B");
        String integerRoot = ScoutUtils.normalizeTeamNumber("581");

        // Suffixed team and integer root must be distinct
        assertNotEquals(teamWithSuffix, integerRoot);
    }

    @Test
    public void testTeamAliasesLookup2135B() throws Exception
    {
        Context mockContext = mock(Context.class);
        Context mockAppContext = mock(Context.class);
        when(mockContext.getApplicationContext()).thenReturn(mockAppContext);
        when(mockAppContext.getFilesDir()).thenReturn(new File("."));

        JSONArray jsonArray = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("teamNum", "2135B");
        obj.put("aliasNum", "9900");
        jsonArray.put(obj);

        TeamAliases teamAliases = TeamAliases.getInstance(mockContext, "2026test", true);
        teamAliases.writeTeamAliasesFile("2026test", jsonArray);

        assertEquals("9900", teamAliases.getAliasForTeamNum("2135B"));
        assertEquals("9900", teamAliases.getAliasForTeamNum("2135b"));
        assertEquals("2135B", teamAliases.getTeamNumForAlias("9900"));
    }
}
