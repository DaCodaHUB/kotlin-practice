# Nullability
**Difficulty:** Beginner · **Suggested time:** 15 minutes
## Scenario
Task assignment information is incomplete for some records.
## Requirements
Add a safe summary that includes an assignee only when one exists.
## Existing behavior
`assigneeId` and `description` are nullable.
## Expected behavior
Blank optional values are not presented as meaningful data.
## Constraints
Avoid unsafe assertions and do not change models.
## Tests to write
Null, blank, and populated optional fields.
