# Repository refactoring
**Difficulty:** Advanced · **Suggested time:** 50 minutes
## Scenario
Task loading has gained caching and failure behavior that obscure its boundary.
## Requirements
Refactor repository composition into clear reusable decorators.
## Existing behavior
`TaskRepository` is a small interface.
## Expected behavior
Callers retain behavior through a cleaner dependency graph.
## Constraints
Use constructor injection and preserve public contracts.
## Tests to write
Decorator order, cache behavior, failures, and regression coverage.
