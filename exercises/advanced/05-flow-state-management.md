# Flow state management
**Difficulty:** Advanced · **Suggested time:** 50 minutes
## Scenario
A task board needs explicit loading, content, and error state.
## Requirements
Create a state holder using `StateFlow`.
## Existing behavior
Task updates are available as a Flow.
## Expected behavior
State transitions are predictable and observable.
## Constraints
Keep mutation encapsulated and preserve cancellation.
## Tests to write
Initial state, success, failure, refresh, and repeated emissions.
