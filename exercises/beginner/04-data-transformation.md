# Data transformation
**Difficulty:** Beginner · **Suggested time:** 15 minutes
## Scenario
A dashboard needs an assignee-to-task-count view.
## Requirements
Transform tasks into a report while handling unassigned work explicitly.
## Existing behavior
Only task lists are available.
## Expected behavior
Every task is counted once.
## Constraints
Use immutable output and collection operators.
## Tests to write
Duplicate assignees, unassigned tasks, and empty lists.
