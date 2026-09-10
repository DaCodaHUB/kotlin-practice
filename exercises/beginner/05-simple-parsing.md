# Simple parsing
**Difficulty:** Beginner · **Suggested time:** 20 minutes
## Scenario
Incoming task records may contain optional fields and extra whitespace.
## Requirements
Extend parser validation for one documented input variation.
## Existing behavior
The parser returns a sealed success or invalid result.
## Expected behavior
Valid records parse; invalid records have a useful reason.
## Constraints
Do not throw for malformed input.
## Tests to write
Whitespace, malformed field counts, bad enums, and optional fields.
