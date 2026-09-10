# Repository error handling
**Difficulty:** Intermediate · **Suggested time:** 30 minutes
## Scenario
One task provider can fail while another remains available.
## Requirements
Add a failure-aware repository boundary and preserve usable results.
## Existing behavior
`TaskRepository` returns tasks through suspending calls.
## Expected behavior
Failures are represented clearly without hiding cancellation.
## Constraints
Keep existing callers compatible.
## Tests to write
Success, provider failure, mixed providers, and cancellation.
