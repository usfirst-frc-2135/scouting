/*
 * Copyright (c) 2025 FRC 2135 Presentation Invasion
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
    public static final String TBA_SCHEDULE_FILE_SUFFIX = "_tbaSchedule.json";

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

    /**
     * Intent extra key for passing a match unique identifier.
     */
    public static final String MATCH_ID = "match_id";

    /**
     * Intent extra key for indicating if an activity was launched in edit mode.
     */
    public static final String IN_EDIT_MODE = "in_edit";

    /**
     * Dimension of the QR code image in pixels.
     */
    public static final int QR_CODE_DIMENSION = 639;
}
