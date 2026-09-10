# Content Catalog Assessment

This service prepares content from several local providers for an audio application. Providers return structured text payloads; the catalog converts them into safe, display-ready entries.

## Getting started

Use JDK 17 or later.

```bash
gradle test
```

## Assessment

Complete the catalog behavior so it meets the requirements below. You may change production code and add tests. Keep public models and interfaces compatible with the existing test suite.

Recommended time: **60–90 minutes**.

### Requirements

- A provider payload contains a title followed by zero or more chapter lines. A chapter line has the format `MM:SS | Chapter title`; blank lines may appear anywhere.
- Invalid chapter lines must not make an otherwise usable payload fail. A chapter timestamp represents a non-negative minute value and seconds from `00` through `59`.
- A payload without a usable title is not displayable. A payload without chapters is still displayable.
- Catalog loading must query every configured provider. Providers are independent: a failure from one must not discard results from the others.
- Provider requests should run concurrently. The returned catalog must not depend on which request completes first.
- Content is a duplicate when its canonical URL matches after trimming whitespace, comparing case-insensitively, and ignoring a single trailing slash. Blank canonical URLs are never duplicates.
- When duplicate content is returned, keep the entry from the source with the highest precedence: `USER_LIBRARY`, then `PARTNER`, then `IMPORT`. If precedence is equal, keep the first configured provider's entry.
- The catalog order follows the first appearance of each unique URL in configured-provider order. Replacing a duplicate must not move its position.
- Malformed payloads and unavailable providers are expected operational conditions; callers should receive the remaining usable results.

### Constraints

This is a Kotlin/JVM project. Do not add network calls, persistence, or Android dependencies. Favor small, readable changes and preserve the existing separation between parsing, providers, service, and cache.

