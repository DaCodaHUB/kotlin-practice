# Coroutine service

**Difficulty:** Intermediate | **Suggested time:** 30-40 minutes

## Scenario

A project screen needs tasks and members loaded together. Earlier exercises cover
repository failure reporting and generic caches. This exercise adds a separate,
all-or-nothing concurrent service operation; completed repository, cache, and
TaskService behavior stays unchanged.

## Your task

Complete `ProjectService.loadOverview(projectId)` in
`src/main/kotlin/com/kotlinpractice/service/ProjectService.kt`.
The supplied ProjectDataSource exposes two independent suspending requests.
ProjectOverview contains their task and member lists.

## Behavioral contract

- On success, call each dependency exactly once with the supplied project ID.
  Forward IDs unchanged, including an empty ID.
- The requests overlap: neither may require the other to finish before it starts.
  Return only after both requests succeed.
- Preserve each list's content and order, including duplicates, completed tasks,
  and empty lists. Do not mutate source-owned lists. Completion order does not
  change the result.
- Every invocation loads fresh data. Concurrent invocations on the same service
  have independent results and cancellation.
- A dependency failure fails the whole invocation and cancels unfinished sibling
  work. Do not retry or return partial data. A single ordinary failure propagates
  as the original exception; simultaneous failures have no required winner.
- Caller cancellation and cancellation raised by either dependency terminate the
  invocation as cancellation, never as success or ordinary failure data.
- All dependency work belongs to the invocation's structured lifetime. On success,
  failure, or cancellation, the invocation completes only after its dependency
  work and cleanup finish. Cleanup may suspend. Dependencies cooperate with
  cancellation; stopping non-cooperative code is outside this exercise.
- An immediate failure is allowed to prevent the other request from starting.
  There is no requirement to run unnecessary work after cancellation or failure.
- Retain the caller's coroutine context and structured lifetime. No detached work,
  blocking waits, or hard-coded dispatchers.

## Scope

No caching, timeout policy, retries, filtering, sorting, deduplication, or changes
to completed exercises. No production implementation of the new data source is
needed. The provided tests supply dependencies.

## Tests

ProjectServiceTest covers both completion orders, overlapping requests, exact
arguments and call counts, empty results, order and duplicates, fresh loads,
caller context, failures and cancellation from either dependency, caller
cancellation, suspending cleanup, and independent concurrent invocations.

Tests use controlled suspension and the coroutine test scheduler, without
real-time sleeps. They intentionally fail at the TODO until you solve the exercise.

Run only this exercise from the repository root:

```powershell
.\gradlew.bat test --tests "com.kotlinpractice.service.ProjectServiceTest"
```

On macOS/Linux use `./gradlew` instead of `.\gradlew.bat`.
