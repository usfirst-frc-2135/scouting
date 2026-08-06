# FRC Scout 2026

FRC Scout 2026 is a specialized Android application developed by **Team 2135 Presentation Invasion** for scouting FIRST Robotics Competition (FRC) matches. The app allows scouts to record detailed robot performance data during the Autonomous, Teleoperated, and Endgame stages of a match.

## Key Features

-   **Multi-Stage Scouting**: Dedicated interfaces for Autonomous, Teleoperated, and Endgame periods.
-   **Data Validation**: Real-time validation with visual indicators (tab badges) to ensure all mandatory fields are completed before data finalization.
-   **TBA Integration**: Supports loading match schedules directly from The Blue Alliance (TBA) API for intelligent autopopulation of match and team details.
-   **QR Code Data Transfer**: Encodes match data into high-density Tab-Separated Values (TSV) QR codes for reliable, offline data transfer to a master computer.
-   **Local Persistence**: Saves scouted matches locally as JSON files with support for filtering, sorting, and "Undo Delete" functionality.
-   **Theme Support**: Includes a system-compliant Dark Mode and dynamic alliance-based color schemes.

## Hardware Requirements

The application is optimized for **Kindle Fire HD 8 (API 28)** tablets. Key optimizations include:
-   Adjusted UI layouts for specific screen density.
-   Forced 100% screen brightness during QR code display for optimal scanning reliability.
-   Low-latency data entry components to accommodate legacy hardware performance.

## Tech Stack

-   **Language**: Java
-   **Architecture**: Activity-based with Fragments and ViewPager2.
-   **UI Components**: Material Design 3, ViewBinding, ConstraintLayout.
-   **Networking**: Volley for TBA API integration.
-   **QR Encoding**: ZXing (Zebra Crossing) library.
-   **Background Tasks**: WorkManager for asynchronous data operations.
-   **Testing**: 
    -   **Unit Tests**: JVM-based tests for business logic and data sanitization (JUnit 4, Mockito).
    -   **Instrumented Tests**: Espresso-based UI tests optimized for API 28 hardware timing constraints.

## Getting Started

### Prerequisites
-   Android Studio Jellyfish | 2023.3.1 or newer.
-   Android SDK Platform 34 (API 34) or newer for compilation.
-   Target Device: Android 9.0 (API 28) or newer.

### Build and Run
1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Synchronize Gradle to download dependencies.
4.  Deploy to an emulator or physical device using the `app:assembleDebug` task.

## Data Format (TSV)

The QR code encodes data in the following order (Tab-Separated):
`Version`, `EventCode`, `MatchNumber`, `TeamNumber`, `TeamAlias`, `ScoutName`, `DiedValue`, `AutonPreload`, `AutonPreloadAccRate`, `AutonHopper`, `AutonAccuracyRate`, `AutonAz`, `AutonDepot`, `AutonOutpost`, `AutonNz`, `AutonClimb`, `HoppersUsed`, `AccuracyRate`, `IntakeAndShoot`, `PassingRate`, `DefenseRate`, `DrivingAbility`, `PassedAz`, `PassedNz`, `StartClimb`, `EndgameClimbLevel`, `EndgameClimbPos`, `Comments`, `ShovelFuel`, `Other2`, `Other3`, `Other4`.

## License

Copyright (c) 2020-26 FRC 2135 Presentation Invasion. Distributed under the MIT License. See the header of source files for full permission notice.
