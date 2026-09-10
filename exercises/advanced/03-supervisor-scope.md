# Supervisor scope
**Difficulty:** Advanced · **Suggested time:** 45 minutes
## Scenario
Optional project metrics should not cancel core task loading.
## Requirements
Isolate optional child failures while retaining required failures.
## Existing behavior
The service has constructor-injected dependencies.
## Expected behavior
Results identify unavailable optional data without losing core data.
## Constraints
Use a justified supervision boundary.
## Tests to write
Optional failure, required failure, multiple failures, and cancellation.
