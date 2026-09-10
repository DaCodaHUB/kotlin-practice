# Thread-safe cache
**Difficulty:** Advanced · **Suggested time:** 50 minutes
## Scenario
Many request handlers read and update a shared task cache.
## Requirements
Provide thread-safe expiration and invalidation with minimal contention.
## Existing behavior
The base in-memory cache is reusable.
## Expected behavior
Concurrent reads and writes do not corrupt cache state.
## Constraints
Define value immutability and lock scope.
## Tests to write
Read/write stress, expiry races, invalidation, and independent keys.
