# Kotlin Practice Lab

A small, generic Kotlin/JVM codebase for practicing language features, coding interviews, and production-oriented engineering work. It models tasks, users, and projects with lightweight parser, repository, service, cache, and Flow boundaries.

## Build and test

Requires JDK 17 or newer. Run the complete suite from this directory:

```powershell
.\gradlew.bat test
```

On macOS or Linux, use `./gradlew test`.

Run one test class:

```powershell
.\gradlew.bat test --tests "com.kotlinpractice.parser.DelimitedTaskParserTest"
```

## Structure

```text
src/main/kotlin/com/kotlinpractice/
  model/       Task, user, project, status, and priority models
  repository/  Suspend and Flow-based data boundary
  service/     Task-focused application operations
  parser/      External text-to-domain parsing
  cache/       Generic and task-specific cache abstractions
  util/        Reusable Kotlin extensions
src/test/kotlin/com/kotlinpractice/  Focused unit tests by area
exercises/                          Guided practice tasks by difficulty
```

## Practice workflow

The base branch should always stay working. Select an exercise from [EXERCISES.md](EXERCISES.md), create a focused branch, add tests, and run the full suite before committing.

```bash
git checkout -b exercise/collections
```

Other useful branch names include `exercise/coroutines`, `exercise/flow`, `exercise/concurrency`, and `exercise/cache`. Exercise documents give requirements and test coverage expectations without solutions.
