package com.kotlinpractice.cache

import com.kotlinpractice.model.Task
import java.util.concurrent.ConcurrentHashMap

interface Cache<K, V> {
    fun get(key: K): V?
    fun put(key: K, value: V)
    fun remove(key: K)
}

class InMemoryCache<K, V> : Cache<K, V> {
    private val entries = ConcurrentHashMap<K, V>()

    override fun get(key: K): V? = entries[key]

    override fun put(key: K, value: V) {
        entries[key] = value
    }

    override fun remove(key: K) {
        entries.remove(key)
    }
}

interface TaskCache : Cache<String, Task>

class InMemoryTaskCache : TaskCache {
    private val delegate = InMemoryCache<String, Task>()

    override fun get(key: String): Task? = delegate.get(key)

    override fun put(key: String, value: Task) = delegate.put(key, value)

    override fun remove(key: String) = delegate.remove(key)
}
