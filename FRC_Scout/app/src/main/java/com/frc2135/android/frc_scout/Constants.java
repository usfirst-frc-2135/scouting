package com.frc2135.android.frc_scout;

/**
 * Central repository for shared constants across the application.
 * Defines file naming conventions, data serialization formats, and remote API endpoints.
 * This class cannot be instantiated.
 */
public final class Constants
{
    /**
     * Prevents instantiation of this utility class.
     */
    private Constants()
    {
    }

    // --- File Suffixes and Prefixes ---

    /**
     * Suffix used for local files containing match data downloaded from The Blue Alliance.
     */
    public static final String TBA_MATCHES_FILE_SUFFIX = "_tbaMatches.json";

    /**
     * Prefix for filenames storing individual scouted match records.
     */
    public static final String MATCH_DATA_FILE_PREFIX = "md_";

    /**
     * Standard extension for match data JSON files.
     */
    public static final String MATCH_DATA_FILE_SUFFIX = ".json";

    /**
     * Suffix for files containing team number to alias mappings for an event.
     */
    public static final String TEAM_ALIASES_FILENAME_SUFFIX = "_teamAliases.json";

    /**
     * Suffix for files containing the official list of scout names for an event.
     */
    public static final String SCOUT_NAMES_FILENAME_SUFFIX = "_scoutNames.json";

    /**
     * Filename for the application-wide configuration settings.
     */
    public static final String SETTINGS_FILENAME = "settings.json";

    // --- URL and API Configurations ---

    /**
     * The base URL for the team's internal JSON data repository (scout names and aliases).
     */
    public static final String TEAM_WEBSITE_JSON_URL = "https://www.frc2135.org/json/";

    /**
     * The base URL for The Blue Alliance API v3 event matches endpoint.
     */
    public static final String TBA_EVENT_MATCHES_URL = "https://www.thebluealliance.com/api/v3/event/";

    /**
     * The unique authentication key required to authorize requests to The Blue Alliance API.
     */
    public static final String TBA_AUTH_KEY = "MetfyxQxRpk0do2GygII8alQnV0qaQ8kF9KUIYDrFTMmQr2pPC8Cl4FGdoKlUaAu";
}
