# Cache implementation
**Difficulty:** Intermediate · **Suggested time:** 25 minutes
## Scenario
Frequently requested tasks should avoid repeated repository reads.
## Requirements
Add expiration or invalidation behavior to a cache implementation.
## Existing behavior
The cache supports get, put, and remove.
## Expected behavior
Stale data is not returned after its defined lifetime.
## Constraints
Keep the generic `Cache` contract usable.
## Tests to write
Hit, miss, overwrite, invalidation, and expiry boundaries.
