# FRC Team Numbers and Aliases Guide

## Overview
In the FIRST Robotics Competition (FRC), teams are registered by a unique, positive integer. Currently, team numbers run from 1 to approximately 12,000 (with future-proofing supported up to 25,000).

## Off-Season Multi-Robot Entries & Suffixes
At unofficial off-season events, teams occasionally bring a second or third robot:
- **Second Robot**: Has the letter **'B'** appended (capitalized, e.g., `2135B`).
- **Third Robot**: Has the letter **'C'** appended (capitalized, e.g., `2135C`).
- Additional robots follow sequentially (`D`, `E`, etc.).

Team numbers with an appended letter are treated as **distinct, separate teams** from their integer-only root team number.

## The Alias System (9900–9999 Range)
Because official FRC scoring systems and tournament management software often only support pure integers, teams with a letter suffix are assigned a temporary numeric **alias** from a reserved pool of numbers in the range **9970 – 9999** (with **9900 – 9999** reserved for current and future use).

## Team Aliases File Format (`_teamAliases.json`)
Event-specific alias mappings are loaded via JSON files (e.g., `2025mttd_teamAliases.json`) structured as an array of mapping objects:

```json
[
  {
    "teamNum": "2135B",
    "aliasNum": "9989"
  }
]
```

- **`teamNum`**: The official team identifier including its uppercase suffix letter (e.g., `2135B`). Must match all characters exactly.
- **`aliasNum`**: The integer alias from the reserved 9900–9999 pool used by scoring systems (e.g., `9989`).
