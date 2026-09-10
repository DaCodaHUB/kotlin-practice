# Deduplication
**Difficulty:** Intermediate · **Suggested time:** 25 minutes
## Scenario
Two imports may contain the same task id.
## Requirements
Define and implement a deterministic merge policy.
## Existing behavior
The in-memory repository keeps the first duplicate.
## Expected behavior
The chosen policy is documented and consistently applied.
## Constraints
Preserve ordering guarantees.
## Tests to write
Duplicate ids, conflicting fields, empty inputs, and order.
