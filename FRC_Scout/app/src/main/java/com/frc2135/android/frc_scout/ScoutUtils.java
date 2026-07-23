package com.frc2135.android.frc_scout;

import static java.lang.Integer.parseInt;

import android.util.Log;

public class ScoutUtils
{
    private static final int EVENT_CODE_MIN_LENGTH = 7; // 2026cur
    private static final int EVENT_CODE_MAX_LENGTH = 9; // 2026cacac
    private static final int EVENT_CODE_MIN_YEAR = 2025; // 2026cur
    private static final int EVENT_CODE_MAX_YEAR = 2027; // 2026cacac

    private static final int MATCH_NUM_MIN_LENGTH = 2;  // f1..f3
    private static final int MATCH_NUM_MAX_LENGTH = 5;  // qm1..qm130, sf1..sf13
    private static final int TEAM_NUM_MIN_LENGTH = 1;   // 8
    private static final int TEAM_NUM_MAX_LENGTH = 6;   // 10212A


    /**
     * Validates an event code string format (4-digit year followed by identifier, e.g., 2026casac).
     *
     * @param tag       the logging tag
     * @param eventCode the event code string to validate
     * @return true if the format is valid
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidEventCode(String tag, String eventCode)
    {
        if (eventCode == null || eventCode.isEmpty() || eventCode.length() < EVENT_CODE_MIN_LENGTH)
        {
            Log.w(tag, "isValidEventCode: Invalid event code (too short): " + eventCode);
            return false;
        }

        if (eventCode.length() > EVENT_CODE_MAX_LENGTH)
        {
            Log.w(tag, "isValidEventCode: Invalid event code (too long): " + eventCode);
            return false;
        }

        if (!eventCode.matches("\\d{4}[a-z0-9]+"))
        {
            Log.w(tag, "isValidEventCode: Invalid event code format: " + eventCode);
            return false;
        }

        int year = parseInt(eventCode.substring(0, 4));
        if (year < EVENT_CODE_MIN_YEAR || year > EVENT_CODE_MAX_YEAR)
        {
            Log.w(tag, "isValidEventCode: Invalid event code year: " + eventCode + " ("
                    + EVENT_CODE_MIN_YEAR + ", " + EVENT_CODE_MAX_YEAR + ")");
            return false;
        }

        return true;
    }

    /**
     * Validates a match number string format (one or two character comp_level, multiple digits match number, e.g., qm114).
     * <p>
     * Supported competition levels:
     * - qm: Qualifications (1-150)
     * - sf: Semifinals (1-13)
     * - f: Finals (1-3)
     *
     * @param tag      the logging tag
     * @param matchNum the match number string to validate
     * @return true if the format is valid
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidMatchNumber(String tag, String matchNum)
    {
        if (matchNum == null || matchNum.isEmpty() || matchNum.length() < MATCH_NUM_MIN_LENGTH)
        {
            Log.w(tag, "isValidMatchNumber: Invalid match number (too short): " + matchNum);
            return false;
        }

        if (matchNum.length() > MATCH_NUM_MAX_LENGTH)
        {
            Log.w(tag, "isValidMatchNumber: Invalid match number (too long): " + matchNum);
            return false;
        }

        // Extract competition level and match number
        String compLevel;
        String numStr;
        if (matchNum.charAt(0) == 'f')
        {
            compLevel = matchNum.substring(0, 1);
            numStr = matchNum.substring(1);
        }
        else
        {
            compLevel = matchNum.substring(0, 2);
            numStr = matchNum.substring(2);
        }

        // Validate match number
        int numInt;
        try
        {
            numInt = parseInt(numStr);
        }
        catch (NumberFormatException e)
        {
            Log.w(tag, "isValidMatchNumber: Invalid match number format: " + matchNum + " (not a number)");
            return false;
        }

        if (compLevel.equals("qm") && (numInt >= 1 && numInt <= 150)) // 2026 CMP had 124 matches per division
        {
            return true;
        }
        else if (compLevel.equals("sf") && (numInt >= 1 && numInt <= 13))
        {
            return true;
        }
        else if (compLevel.equals("f") && (numInt >= 1 && numInt <= 3))
        {
            return true;
        }
        else
        {
            Log.w(tag, "isValidMatchNumber: Invalid match number competition level: " + matchNum + " (comp level or number not in range");
            return false;
        }
    }

    /**
     * Validates a team number string format (one to five digits with optional following letter, e.g., 2135A).
     *
     * @param tag     the logging tag
     * @param teamNum the team number string to validate
     * @return true if the format is valid
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidTeamNumber(String tag, String teamNum)
    {
        //noinspection ConstantValue,SizeReplaceableByIsEmpty
        if (teamNum == null || teamNum.isEmpty() || teamNum.length() < TEAM_NUM_MIN_LENGTH)
        {
            Log.w(tag, "isValidTeamNumber: Invalid team number (too short): " + teamNum);
            return false;
        }

        if (teamNum.length() > TEAM_NUM_MAX_LENGTH)
        {
            Log.w(tag, "isValidTeamNumber: Invalid team number (too long): " + teamNum);
            return false;
        }

        if (!teamNum.matches("^\\d{1,5}[A-Z]?"))
        {
            Log.w(tag, "isValidTeamNumber: Invalid team number format: " + teamNum);
            return false;
        }

        return true;
    }

    /**
     * Normalizes a scout name by trimming leading/trailing whitespace and collapsing
     * multiple internal spaces into a single space.
     *
     * @param scoutName the scout name to normalize
     * @return the normalized scout name, or an empty string if null/empty
     */
    public static String normalizeScoutName(String scoutName)
    {
        if (scoutName == null)
        {
            return "";
        }
        return scoutName.trim().replaceAll("\\s+", " ");
    }

    /**
     * Validates that the scout name meets the required format.
     * <p>
     * Requirements:
     * - First name starting with a capital letter, followed by optional letters.
     * - A single space separator.
     * - A single capital letter for the last initial.
     *
     * @param tag       the logging tag
     * @param scoutName the scout name to validate
     * @return true if the name is valid, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidScoutName(String tag, String scoutName)
    {
        if (scoutName == null || scoutName.trim().isEmpty())
        {
            Log.w(tag, "isValidScoutName: Invalid scout name (empty): " + scoutName);
            return false;
        }

        // Normalize before final pattern check
        String normalizedName = normalizeScoutName(scoutName);

        if (!normalizedName.matches("^[A-Z][a-zA-Z]*\\s[A-Z]$"))
        {
            Log.w(tag, "isValidScoutName: Invalid scout name format (first name, last initial): " + normalizedName);
            return false;
        }

        return true;
    }
}
