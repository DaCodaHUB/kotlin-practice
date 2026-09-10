# Structured concurrency
**Difficulty:** Advanced · **Suggested time:** 45 minutes
## Scenario
A project overview combines independent task, user, and project work.
## Requirements
Implement a structured concurrent aggregation operation.
## Existing behavior
Dependencies use suspend APIs.
## Expected behavior
Children complete before the operation returns and failures follow a defined policy.
## Constraints
No global scopes or detached jobs.
## Tests to write
Success, failure, parent cancellation, and no leaked work.
