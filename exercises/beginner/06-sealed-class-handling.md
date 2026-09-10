# Sealed class handling
**Difficulty:** Beginner · **Suggested time:** 15 minutes
## Scenario
Callers need a user-facing parser outcome.
## Requirements
Map every `TaskParseResult` variant to a display-safe result.
## Existing behavior
Parsing uses a sealed interface.
## Expected behavior
Both success and invalid outcomes are handled exhaustively.
## Constraints
Do not alter parser semantics.
## Tests to write
One test for every sealed variant.
