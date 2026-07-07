package com.frc2135.android.frc_scout;

/**
 * Shared constants for the application.
 * Centralizing these values avoids string literal duplication and makes maintenance easier.
 */
public final class Constants
{
    private Constants()
    {
        // Private constructor to prevent instantiation
    }

    // File Suffixes
    public static final String TBA_MATCHES_FILE_SUFFIX = "_tbaMatches.json";
    public static final String TEAM_ALIASES_FILENAME_SUFFIX = "_teamAliases.json";
    public static final String SCOUT_NAMES_FILENAME_SUFFIX = "_scoutNames.json";
    public static final String SETTINGS_FILENAME = "settings.json";
    public static final String MATCH_DATA_FILE_SUFFIX = ".json";

    // URL Paths
    /**
     * The base URL for our website's JSON data.
     */
    public static final String TEAM_WEBSITE_JSON_URL = "https://www.frc2135.org/json/";
    /**
     * The base URL for The Blue Alliance API v3 event matches endpoint.
     */
    public static final String TBA_EVENT_MATCHES_URL = "https://www.thebluealliance.com/api/v3/event/";
    /**
     * The authentication key for The Blue Alliance API. (new for 2026)
     */
    public static final String TBA_AUTH_KEY = "MetfyxQxRpk0do2GygII8alQnV0qaQ8kF9KUIYDrFTMmQr2pPC8Cl4FGdoKlUaAu";
}
