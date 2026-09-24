# Generic cache

**Difficulty:** Intermediate | **Suggested time:** 30–40 minutes

## Scenario

Tasks, users, and projects need reusable caching with bounded storage.
Intermediate #2 already provides a generic InMemoryCache<K, V> with TTL support.
This exercise extends the original generic-cache learning goal with a separate
capacity-limited cache and least-recently-used eviction.

## Your task

Complete the TODOs in `src/main/kotlin/com/kotlinpractice/cache/LruCache.kt`:
construction and the get, put, and remove operations of `LruCache<K, V : Any>`.
It must remain usable through `Cache<K, V>` with different key and value types.

## Behavioral contract

- Capacity is a positive maximum number of stored keys. Zero or negative capacity
  throws IllegalArgumentException during construction.
- A new cache is empty. Missing keys return null. Values are non-null; nullable
  keys are supported. Keys use equality semantics.
- A successful read makes its key most recently used. A miss leaves the relative
  recency of stored keys unchanged.
- Every put makes its key most recently used. Replacing a value does not evict
  another key, including when the replacement equals the old value.
- Adding a new key to a full cache evicts the least recently used key.
- Removal invalidates the key immediately and preserves other keys' relative
  recency. Removing an absent key is harmless. Removed and evicted keys can be
  inserted again.
- Instances are independent. Values are returned as stored, without copying.

## Scope

Preserve completed exercises, including InMemoryCache, its TTL behavior, and
InMemoryTaskCache. This exercise adds no TTL, concurrency, traversal API, or
performance benchmark requirement. The scaffold intentionally contains no solution.

## Tests

The supplied tests cover the contract and are expected to fail at TODOs until you
complete the exercise. From the repository root:

```powershell
.\gradlew.bat test --tests "com.kotlinpractice.cache.LruCacheTest"
```

Run all regression tests after completing it with `.\gradlew.bat test`.
On macOS/Linux, replace `.\gradlew.bat` with `./gradlew`.
