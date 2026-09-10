# Collection transformations
**Difficulty:** Beginner · **Suggested time:** 15 minutes
## Scenario
The task list needs a concise reporting view.
## Requirements
Add reusable task-list transformations for filtering active work and grouping by priority.
## Existing behavior
Tasks are returned as a plain list.
## Expected behavior
Transformations preserve task data and collection order where it matters.
## Constraints
Use idiomatic collection operations; do not change repository behavior.
## Tests to write
Empty input, mixed statuses, each priority, and stable ordering.
