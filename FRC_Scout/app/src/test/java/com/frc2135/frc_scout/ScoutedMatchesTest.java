package com.frc2135.frc_scout;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScoutedMatchesTest
{
    private ScoutedMatches m_scoutedMatches;
    private List<MatchData> m_testList;

    @Before
    public void setUp()
    {
        Context mockContext = mock(Context.class);
        Context mockAppContext = mock(Context.class);
        when(mockContext.getApplicationContext()).thenReturn(mockAppContext);
        when(mockAppContext.getFilesDir()).thenReturn(new File(".")); // Use current dir for test

        m_scoutedMatches = ScoutedMatches.getInstance(mockContext);
        
        m_testList = new ArrayList<>();
        
        MatchData m1 = new MatchData();
        m1.setMatchNumber("qm10");
        m1.setTeamNumber("2135");
        m1.setScoutName("Alice");
        m1.setEventCode("2026casac");
        
        MatchData m2 = new MatchData();
        m2.setMatchNumber("qm2");
        m2.setTeamNumber("254");
        m2.setScoutName("Bob");
        m2.setEventCode("2026casac");
        
        MatchData m3 = new MatchData();
        m3.setMatchNumber("qm5");
        m3.setTeamNumber("1678");
        m3.setScoutName("Alice");
        m3.setEventCode("2026cur");
        
        m_testList.add(m1);
        m_testList.add(m2);
        m_testList.add(m3);
    }

    @Test
    public void testSortByMatchNumber()
    {
        List<MatchData> sorted = m_scoutedMatches.sortMatchList(m_testList, "Match", true);
        assertEquals("qm2", sorted.get(0).getMatchNumber());
        assertEquals("qm5", sorted.get(1).getMatchNumber());
        assertEquals("qm10", sorted.get(2).getMatchNumber());
    }

    @Test
    public void testSortByTeamNumber()
    {
        List<MatchData> sorted = m_scoutedMatches.sortMatchList(m_testList, "Team", true);
        assertEquals("254", sorted.get(0).getTeamNumber());
        assertEquals("1678", sorted.get(1).getTeamNumber());
        assertEquals("2135", sorted.get(2).getTeamNumber());
    }

    @Test
    public void testFilterByScout()
    {
        List<MatchData> filtered = m_scoutedMatches.filterMatchList(m_testList, null, null, null, "Alice");
        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(m -> m.getScoutName().equals("Alice")));
    }

    @Test
    public void testFilterByEventAndScout()
    {
        List<MatchData> filtered = m_scoutedMatches.filterMatchList(m_testList, "2026casac", null, null, "Alice");
        assertEquals(1, filtered.size());
        assertEquals("qm10", filtered.get(0).getMatchNumber());
    }
    
    private void assertTrue(boolean condition) {
        if(!condition) throw new AssertionError();
    }
}
