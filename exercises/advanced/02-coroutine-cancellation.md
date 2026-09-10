# Coroutine cancellation
**Difficulty:** Advanced · **Suggested time:** 40 minutes
## Scenario
A long-running task import must stop promptly when its caller leaves.
## Requirements
Make the import cancellation-safe and clean up resources.
## Existing behavior
Repository operations are suspending.
## Expected behavior
Cancellation propagates and is never converted to a normal failure.
## Constraints
Use cooperative cancellation and `finally` where needed.
## Tests to write
Cancellation during work, cleanup, and ordinary failures.
