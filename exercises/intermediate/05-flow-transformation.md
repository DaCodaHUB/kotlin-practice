# Flow transformation
**Difficulty:** Intermediate · **Suggested time:** 30 minutes
## Scenario
The UI only needs open high-priority tasks.
## Requirements
Expose a transformed flow from repository observations.
## Existing behavior
The repository exposes `Flow<List<Task>>`.
## Expected behavior
Updates are filtered and mapped without mutating source state.
## Constraints
Do not block flow collection.
## Tests to write
Initial emission, updates, empty results, and completion behavior.
