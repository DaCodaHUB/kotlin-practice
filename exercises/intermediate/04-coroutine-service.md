# Coroutine service
**Difficulty:** Intermediate · **Suggested time:** 30 minutes
## Scenario
A project screen needs tasks and members loaded together.
## Requirements
Add a service operation that loads independent dependencies concurrently.
## Existing behavior
Repository APIs are suspend functions.
## Expected behavior
Independent work overlaps and results remain deterministic.
## Constraints
Use structured concurrency; do not create unmanaged scopes.
## Tests to write
Success, a dependency failure, ordering, and cancellation.
