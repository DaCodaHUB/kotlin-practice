package com.kotlinpractice.cache

import com.kotlinpractice.model.Task

interface Cache<K, V> {
    fun get(key: K): V?
    fun put(key: K, value: V)
    fun remove(key: K)
}

/**
 * Intermediate #2: A cache with a configurable entry lifetime.
 *
 * [ttlMillis] is the lifetime in milliseconds from the most recent put for a key.
 * Null means no expiration, preserving the behavior of existing no-argument callers.
 * A configured lifetime is nonnegative; zero makes a value immediately expired.
 * [nowMillis] supplies the current time in milliseconds. For this exercise, time
 * is nondecreasing and elapsed times fit in Long. The default uses the system clock.
 *
 * A newly created cache contains no values. A get returns the latest value for its
 * key only while that value is unexpired. Missing, removed, and expired keys return
 * null. A value expires exactly when its age reaches ttlMillis. Reads do not extend
 * its lifetime. Putting a value replaces any previous value for that key and starts
 * a new lifetime, including when the value is unchanged or the old value expired.
 * Removing a key invalidates its value immediately; removing an absent or expired
 * key has no effect. A removed key can be populated again.
 *
 * Changes to one key do not change another key's value or lifetime. Separate cache
 * instances are independent. Keep the existing Cache contract usable.
 * The exercise specifies no traversal ordering or additional value-copying or
 * ownership requirements.
 */
class InMemoryCache<K, V>(
    private val ttlMillis: Long? = null,
    private val nowMillis: () -> Long = System::currentTimeMillis
) : Cache<K, V> {

    private data class CacheEntry<V>(
        val value: V,
        val putTimeMillis: Long
    )

    private val cacheMap = mutableMapOf<K, CacheEntry<V>>()

    /** Return the unexpired value for [key], or null on a cache miss. */
    override fun get(key: K): V? {
        val entry = cacheMap[key] ?: return null

        if (ttlMillis != null && nowMillis() - entry.putTimeMillis >= ttlMillis) {
            cacheMap.remove(key)
            return null
        }

        return entry.value
    }

    /** Store [value] for [key], replacing its previous value and restarting its lifetime. */
    override fun put(key: K, value: V) {
        cacheMap[key] = CacheEntry(value, nowMillis())
    }

    /** Invalidate [key]; an absent or already expired key requires no action. */
    override fun remove(key: K) {
        cacheMap.remove(key)
    }
}

interface TaskCache : Cache<String, Task>

class InMemoryTaskCache : TaskCache {
    private val delegate = InMemoryCache<String, Task>()

    override fun get(key: String): Task? = delegate.get(key)

    override fun put(key: String, value: Task) = delegate.put(key, value)

    override fun remove(key: String) = delegate.remove(key)
}
