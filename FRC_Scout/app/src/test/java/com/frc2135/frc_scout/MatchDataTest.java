package com.frc2135.frc_scout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class MatchDataTest
{
    private MatchData m_matchData;

    @Before
    public void setUp()
    {
        m_matchData = new MatchData();
        m_matchData.setEventCode("2026casac");
        m_matchData.setMatchNumber("qm1");
        m_matchData.setTeamNumber("2135");
        m_matchData.setScoutName("John S");
    }

    @Test
    public void testEncodeToTSV_Sanitization()
    {
        m_matchData.setComment("Good defense.\tNeeds work\non intake.");
        String tsv = m_matchData.encodeToTSV();
        
        // Split by tabs
        String[] fields = tsv.split("\t");
        
        // Comment is the 28th field (index 27) in encodeToTSV
        String encodedComment = fields[27];
        
        // Check that it's wrapped in quotes and internal tabs/newlines are replaced by spaces
        assertEquals("\"Good defense. Needs work on intake.\"", encodedComment);
    }

    @Test
    public void testEncodeToTSV_QuoteEscaping()
    {
        m_matchData.setComment("Robot was \"fast\".");
        String tsv = m_matchData.encodeToTSV();
        String[] fields = tsv.split("\t");
        String encodedComment = fields[27];
        
        // Double quotes should be escaped as ""
        assertEquals("\"Robot was \"\"fast\"\".\"", encodedComment);
    }

    @Test
    public void testJSONSerializationRoundTrip() throws Exception
    {
        m_matchData.setAutonHopper(1);
        m_matchData.setHoppersUsed(5);
        m_matchData.setComment("Test comment");
        
        JSONObject json = m_matchData.toJSON();
        MatchData restored = new MatchData(json);
        
        assertEquals(m_matchData.getMatchID(), restored.getMatchID());
        assertEquals(m_matchData.getEventCode(), restored.getEventCode());
        assertEquals(m_matchData.getMatchNumber(), restored.getMatchNumber());
        assertEquals(m_matchData.getTeamNumber(), restored.getTeamNumber());
        assertEquals(m_matchData.getScoutName(), restored.getScoutName());
        assertEquals(m_matchData.getAutonHopper(), restored.getAutonHopper());
        assertEquals(m_matchData.getHoppersUsed(), restored.getHoppersUsed());
        assertEquals(m_matchData.getComment(), restored.getComment());
    }

    @Test
    public void testGetMatchDataString_Alignment()
    {
        String debugString = m_matchData.getMatchDataString();
        assertTrue(debugString.contains("Match ID"));
        assertTrue(debugString.contains("Auton Preload"));
        
        String[] lines = debugString.split("\n");
        for (String line : lines)
        {
            if (line.contains("Auton Preload"))
            {
                // Verify that the colon is at a consistent column (around index 24)
                assertEquals(24, line.indexOf(":"));
            }
        }
    }
}
