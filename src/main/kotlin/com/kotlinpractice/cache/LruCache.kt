package com.kotlinpractice.cache

/**
 * Intermediate #3: A generic cache with least-recently-used eviction.
 *
 * [capacity] is the maximum number of stored keys and must be positive.
 * Construction with zero or negative capacity throws IllegalArgumentException.
 * A new cache is empty. Keys use equality semantics; values are non-null.
 * Nullable key types are supported, with null behaving like any other key.
 *
 * A successful get returns the stored value and makes its key most recently used.
 * A missing get returns null and leaves stored keys' relative recency unchanged.
 * Every put makes its key most recently used. Putting an existing key replaces its
 * value without evicting another key, even when the value is unchanged. Putting a
 * new key into a full cache evicts the least recently used key.
 *
 * Remove immediately invalidates a key without changing other keys' relative
 * recency. Removing an absent key has no effect. Removed and evicted keys may be
 * inserted again. Each instance has independent contents and recency.
 *
 * Values are returned as stored, without copying. There is no expiration, traversal
 * order API, or concurrency guarantee. Preserve the existing Cache API and the
 * completed InMemoryCache and InMemoryTaskCache behavior.
 */
class LruCache<K, V : Any>(capacity: Int) : Cache<K, V> {
    init {
        TODO("Intermediate #3")
    }

    override fun get(key: K): V? = TODO("Intermediate #3")

    override fun put(key: K, value: V): Unit = TODO("Intermediate #3")

    override fun remove(key: K): Unit = TODO("Intermediate #3")
}
