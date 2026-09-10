# Performance optimization
**Difficulty:** Advanced · **Suggested time:** 40 minutes
## Scenario
A large task report performs redundant passes over data.
## Requirements
Measure and improve the report while retaining behavior.
## Existing behavior
Task collections are ordinary Kotlin collections.
## Expected behavior
The optimized path is correct and avoids unnecessary allocations.
## Constraints
Benchmark responsibly; do not trade away readability without evidence.
## Tests to write
Correctness on large and edge-case collections.
