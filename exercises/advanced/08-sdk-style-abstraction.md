# SDK-style abstraction
**Difficulty:** Advanced · **Suggested time:** 60 minutes
## Scenario
Other applications need a stable, simple entry point for task operations.
## Requirements
Design a small SDK-style facade with an intentional public API.
## Existing behavior
Models, services, repositories, and cache are separate components.
## Expected behavior
Consumers configure and use the API without knowing internal composition.
## Constraints
Avoid framework dependencies and keep configuration explicit.
## Tests to write
Default configuration, custom dependencies, errors, and API compatibility.
