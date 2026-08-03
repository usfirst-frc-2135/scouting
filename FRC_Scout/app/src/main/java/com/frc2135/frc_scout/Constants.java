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

/**
 * Central repository for shared constants across the application.
 * <p>
 * This class defines file naming conventions, data serialization formats, remote API endpoints,
 * and key identifiers used for Intents and UI dimensions. It is designed as a utility class
 * and cannot be instantiated.
 */
public final class Constants
{
    /**
     * Prevents instantiation of this utility class.
     */
    private Constants()
    {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // --- File Suffixes and Prefixes ---

    /**
     * The fallback event code used when no specific event has been selected or configured.
     */
    public static final String DEFAULT_EVENT_CODE = "2020event";

    /**
     * Suffix for JSON files stored in internal storage that contain the official match schedule
     * for a specific event, typically downloaded from The Blue Alliance API.
     */
    public static final String TBA_SCHEDULE_FILE_SUFFIX = "_tbaSchedule.json";

    /**
     * Prefix for filenames used to persist individual match scouting records.
     * Files starting with this prefix are considered part of the local scouting database.
     */
    public static final String MATCH_DATA_FILE_PREFIX = "md_";

    /**
     * The standard file extension for all JSON-based data persistence in the app,
     * including match records and configuration files.
     */
    public static final String MATCH_DATA_FILE_SUFFIX = ".json";

    /**
     * Suffix for event-specific files containing mappings between team numbers and their
     * descriptive aliases or names.
     */
    public static final String TEAM_ALIASES_FILENAME_SUFFIX = "_teamAliases.json";

    /**
     * Suffix for event-specific files containing the list of authorized scout names
     * used for auto-population in the user interface.
     */
    public static final String SCOUT_NAMES_FILENAME_SUFFIX = "_scoutNames.json";

    /**
     * The fixed filename for the application's global configuration settings,
     * persisted in the app's internal "files" directory.
     */
    public static final String SETTINGS_FILENAME = "settings.json";

    // --- URL and API Configurations ---

    /**
     * Base endpoint for retrieving team-specific JSON metadata (e.g., scout lists and team aliases)
     * from the organization's web server.
     */
    public static final String TEAM_WEBSITE_JSON_URL = "https://www.frc2135.org/json/";

    /**
     * The root URL for The Blue Alliance (TBA) v3 API matches endpoint.
     * Expects an event code to be appended to form a complete request URL.
     */
    public static final String TBA_EVENT_MATCHES_URL = "https://www.thebluealliance.com/api/v3/event/";

    /**
     * The unique authorization key required for authenticating requests with The Blue Alliance API.
     * This key should be kept secure and rotated if compromised.
     */
    public static final String TBA_AUTH_KEY = "MetfyxQxRpk0do2GygII8alQnV0qaQ8kF9KUIYDrFTMmQr2pPC8Cl4FGdoKlUaAu";

    /**
     * Intent extra key used to pass the unique identifier of a match between Activities or Fragments.
     */
    public static final String MATCH_ID = "match_id";

    /**
     * Intent extra key used to indicate whether a scouting activity should be opened in read-only
     * or interactive edit mode.
     */
    public static final String IN_EDIT_MODE = "in_edit";

    /**
     * The fixed edge dimension (width and height) in pixels for generated QR codes.
     * Balanced for readability on common mobile camera sensors.
     */
    public static final int QR_CODE_DIMENSION = 639;
}
