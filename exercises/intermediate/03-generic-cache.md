# Generic cache
**Difficulty:** Intermediate · **Suggested time:** 30 minutes
## Scenario
Users and projects now need the same caching behavior as tasks.
## Requirements
Refine a reusable cache API without task-specific type assumptions.
## Existing behavior
`InMemoryCache<K, V>` is generic.
## Expected behavior
Multiple model types can use the cache safely.
## Constraints
Preserve task-cache behavior.
## Tests to write
Different key/value types, removal, and null misses.
