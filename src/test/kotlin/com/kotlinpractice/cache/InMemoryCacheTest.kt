package com.kotlinpractice.cache

import com.kotlinpractice.model.Task
import com.kotlinpractice.model.task
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InMemoryCacheTest {
    @Test
    fun `new cache misses and unknown keys remain misses`() {
        val cache: Cache<String, Task> = InMemoryCache(ttlMillis = 100, nowMillis = { 0L })

        assertNull(cache.get("missing"))
        cache.put("present", task("present"))
        assertNull(cache.get("missing"))
        assertEquals(task("present"), cache.get("present"))
    }

    @Test
    fun `stores replaces and removes values`() {
        val cache = InMemoryCache<String, Int>()
        cache.put("total", 1)
        cache.put("total", 2)

        assertEquals(2, cache.get("total"))

        cache.remove("total")
        assertNull(cache.get("total"))
    }

    @Test
    fun `value is available immediately and just before expiry`() {
        var now = 1_000L
        val cache = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { now })
        val value = task("one")

        cache.put("one", value)
        assertEquals(value, cache.get("one"))
        now = 1_099L
        assertEquals(value, cache.get("one"))
    }

    @Test
    fun `value misses at the exact expiry boundary and afterwards`() {
        var now = 1_000L
        val cache = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { now })
        cache.put("one", task("one"))

        now = 1_100L
        assertNull(cache.get("one"))
        now = 1_101L
        assertNull(cache.get("one"))
        now = 5_000L
        assertNull(cache.get("one"))
    }

    @Test
    fun `first read after the lifetime misses without an earlier boundary read`() {
        var now = 0L
        val cache = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { now })
        cache.put("one", task("one"))

        now = 500L

        assertNull(cache.get("one"))
    }

    @Test
    fun `lifetime starts at insertion rather than cache creation`() {
        var now = 0L
        val cache = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { now })
        now = 500L

        cache.put("one", task("one"))

        now = 599L
        assertEquals(task("one"), cache.get("one"))
        now = 600L
        assertNull(cache.get("one"))
    }

    @Test
    fun `reads do not extend the lifetime`() {
        var now = 0L
        val cache = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { now })
        cache.put("one", task("one"))

        for (time in listOf(25L, 50L, 99L)) {
            now = time
            assertEquals(task("one"), cache.get("one"))
        }
        now = 100L
        assertNull(cache.get("one"))
    }

    @Test
    fun `overwrite replaces the value and restarts its lifetime`() {
        var now = 0L
        val cache = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { now })
        val original = task("one")
        val updated = original.copy(title = "Updated")
        cache.put("one", original)

        now = 60L
        cache.put("one", updated)
        assertEquals(updated, cache.get("one"))
        now = 100L
        assertEquals(updated, cache.get("one"))
        now = 159L
        assertEquals(updated, cache.get("one"))
        now = 160L
        assertNull(cache.get("one"))
    }

    @Test
    fun `putting the same value also restarts its lifetime`() {
        var now = 0L
        val cache = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { now })
        val value = task("one")
        cache.put("one", value)

        now = 50L
        cache.put("one", value)
        now = 100L
        assertEquals(value, cache.get("one"))
        now = 149L
        assertEquals(value, cache.get("one"))
        now = 150L
        assertNull(cache.get("one"))
    }

    @Test
    fun `expired entry can be replaced without a prior read`() {
        var now = 0L
        val cache = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { now })
        cache.put("one", task("old"))

        now = 150L
        cache.put("one", task("new"))

        assertEquals(task("new"), cache.get("one"))
        now = 249L
        assertEquals(task("new"), cache.get("one"))
        now = 250L
        assertNull(cache.get("one"))
    }

    @Test
    fun `removal immediately invalidates an unexpired value and permits reinsertion`() {
        var now = 0L
        val cache = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { now })
        cache.put("one", task("old"))
        now = 20L

        cache.remove("one")
        assertNull(cache.get("one"))
        cache.remove("one")
        assertNull(cache.get("one"))
        now = 30L
        cache.put("one", task("new"))

        now = 129L
        assertEquals(task("new"), cache.get("one"))
        now = 130L
        assertNull(cache.get("one"))
    }

    @Test
    fun `removing missing and expired keys leaves other entries available`() {
        var now = 0L
        val cache = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { now })
        cache.remove("missing")
        cache.put("old", task("old"))
        now = 50L
        cache.put("fresh", task("fresh"))

        now = 100L
        cache.remove("old")
        cache.remove("missing")

        assertNull(cache.get("old"))
        assertNull(cache.get("missing"))
        assertEquals(task("fresh"), cache.get("fresh"))
        now = 150L
        assertNull(cache.get("fresh"))
    }

    @Test
    fun `each key has an independent value and lifetime`() {
        var now = 0L
        val cache = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { now })
        cache.put("first", task("first"))
        now = 20L
        cache.put("second", task("second"))
        now = 40L
        cache.put("first", task("updated"))

        now = 119L
        assertEquals(task("updated"), cache.get("first"))
        assertEquals(task("second"), cache.get("second"))
        now = 120L
        assertNull(cache.get("second"))
        assertEquals(task("updated"), cache.get("first"))
        now = 140L
        assertNull(cache.get("first"))
    }

    @Test
    fun `zero lifetime never returns stored values`() {
        val cache = InMemoryCache<String, Task>(ttlMillis = 0, nowMillis = { 10L })

        cache.put("one", task("one"))
        assertNull(cache.get("one"))
        cache.put("one", task("replacement"))
        assertNull(cache.get("one"))
    }

    @Test
    fun `omitted lifetime preserves values until replacement or removal`() {
        var now = 0L
        val cache = InMemoryCache<String, Task>(nowMillis = { now })
        cache.put("one", task("old"))

        now = 1_000_000_000L
        assertEquals(task("old"), cache.get("one"))
        cache.put("one", task("new"))
        assertEquals(task("new"), cache.get("one"))
        cache.remove("one")
        assertNull(cache.get("one"))
    }

    @Test
    fun `cache instances do not share values`() {
        val first = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { 0L })
        val second = InMemoryCache<String, Task>(ttlMillis = 100, nowMillis = { 0L })

        first.put("one", task("first"))
        assertNull(second.get("one"))
        second.put("one", task("second"))
        assertEquals(task("first"), first.get("one"))
        first.remove("one")
        assertEquals(task("second"), second.get("one"))
    }
}
