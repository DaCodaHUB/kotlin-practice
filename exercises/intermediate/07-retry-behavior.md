# Retry behavior
**Difficulty:** Intermediate · **Suggested time:** 35 minutes
## Scenario
A transient source failure should be retried before surfacing an error.
## Requirements
Add bounded retry behavior around a suspend repository operation.
## Existing behavior
Repository failures propagate to callers.
## Expected behavior
Transient success returns data; exhausted retries fail clearly.
## Constraints
Respect coroutine cancellation and avoid real-time test delays.
## Tests to write
Immediate success, eventual success, exhaustion, and cancellation.
