# Concurrent repository loading
**Difficulty:** Intermediate · **Suggested time:** 40 minutes
## Scenario
Tasks are assembled from several independent repositories.
## Requirements
Load sources concurrently and combine their results.
## Existing behavior
Service and repository boundaries are suspend-based.
## Expected behavior
Successful sources complete without unnecessary serial waits.
## Constraints
Define failure behavior and use structured concurrency.
## Tests to write
Concurrency, partial failure, all failure, and cancellation.
