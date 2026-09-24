package com.kotlinpractice.cache

import com.kotlinpractice.model.Project
import com.kotlinpractice.model.Task
import com.kotlinpractice.model.User
import com.kotlinpractice.model.task
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame

class LruCacheTest {
    @Test
    fun `capacity must be positive`() {
        for (capacity in listOf(0, -1, Int.MIN_VALUE)) {
            assertFailsWith<IllegalArgumentException> { LruCache<String, Int>(capacity) }
        }
    }

    @Test
    fun `new cache and unknown keys miss`() {
        val cache: Cache<String, Int> = LruCache(2)
        assertNull(cache.get("missing"))
        cache.remove("missing")
        cache.put("a", 1)
        assertNull(cache.get("missing"))
        assertEquals(1, cache.get("a"))
    }

    @Test
    fun `entries remain available up to capacity`() {
        val cache = LruCache<String, Int>(3)
        cache.put("a", 1)
        cache.put("b", 2)
        cache.put("c", 3)
        assertEquals(1, cache.get("a"))
        assertEquals(2, cache.get("b"))
        assertEquals(3, cache.get("c"))
    }

    @Test
    fun `new insertion at capacity evicts oldest untouched entry`() {
        val cache = LruCache<String, Int>(2)
        cache.put("a", 1)
        cache.put("b", 2)
        cache.put("c", 3)
        assertNull(cache.get("a"))
        assertEquals(2, cache.get("b"))
        assertEquals(3, cache.get("c"))
    }

    @Test
    fun `successful reads update recency including repeated reads`() {
        val cache = LruCache<String, Int>(3)
        cache.put("a", 1)
        cache.put("b", 2)
        cache.put("c", 3)
        assertEquals(1, cache.get("a"))
        assertEquals(1, cache.get("a"))
        cache.put("d", 4)
        assertNull(cache.get("b"))
        cache.put("e", 5)
        assertNull(cache.get("c"))
        assertEquals(1, cache.get("a"))
        assertEquals(4, cache.get("d"))
        assertEquals(5, cache.get("e"))
    }

    @Test
    fun `misses do not affect eviction`() {
        val cache = LruCache<String, Int>(2)
        cache.put("a", 1)
        cache.put("b", 2)
        repeat(3) { assertNull(cache.get("missing")) }
        cache.put("c", 3)
        assertNull(cache.get("a"))
        assertEquals(2, cache.get("b"))
        assertEquals(3, cache.get("c"))
    }

    @Test
    fun `replacement preserves other entries and updates recency`() {
        val cache = LruCache<String, Int>(3)
        cache.put("a", 1)
        cache.put("b", 2)
        cache.put("c", 3)
        cache.put("a", 10)
        assertEquals(2, cache.get("b"))
        assertEquals(3, cache.get("c"))
        cache.put("a", 11)
        cache.put("d", 4)
        assertNull(cache.get("b"))
        assertEquals(11, cache.get("a"))
        assertEquals(3, cache.get("c"))
        assertEquals(4, cache.get("d"))
    }

    @Test
    fun `putting the same value updates recency`() {
        val cache = LruCache<String, Int>(2)
        cache.put("a", 1)
        cache.put("b", 2)
        cache.put("a", 1)
        cache.put("c", 3)
        assertNull(cache.get("b"))
        assertEquals(1, cache.get("a"))
        assertEquals(3, cache.get("c"))
    }

    @Test
    fun `removal frees capacity and preserves remaining recency`() {
        val cache = LruCache<String, Int>(3)
        cache.put("a", 1)
        cache.put("b", 2)
        cache.put("c", 3)
        cache.remove("b")
        assertNull(cache.get("b"))
        cache.remove("b")
        cache.remove("unknown")
        cache.put("d", 4)
        cache.put("e", 5)
        assertNull(cache.get("a"))
        assertEquals(3, cache.get("c"))
        assertEquals(4, cache.get("d"))
        assertEquals(5, cache.get("e"))
    }

    @Test
    fun `removed and evicted keys can return as most recent entries`() {
        val cache = LruCache<String, Int>(2)
        cache.put("a", 1)
        cache.put("b", 2)
        cache.remove("a")
        cache.put("a", 10)
        cache.put("c", 3)
        assertNull(cache.get("b"))
        cache.put("b", 20)
        assertNull(cache.get("a"))
        assertEquals(3, cache.get("c"))
        assertEquals(20, cache.get("b"))
    }

    @Test
    fun `capacity one supports replacement eviction and removal`() {
        val cache = LruCache<Int, String>(1)
        cache.put(1, "first")
        cache.put(1, "updated")
        assertEquals("updated", cache.get(1))
        cache.put(2, "second")
        assertNull(cache.get(1))
        assertEquals("second", cache.get(2))
        cache.remove(2)
        assertNull(cache.get(2))
        cache.put(3, "third")
        assertEquals("third", cache.get(3))
    }

    @Test
    fun `equal keys share an entry while hash collisions remain distinct`() {
        data class Key(val id: Int) {
            override fun hashCode(): Int = 0
        }
        val cache = LruCache<Key, String>(2)
        cache.put(Key(1), "old")
        cache.put(Key(2), "second")
        cache.put(Key(1), "new")
        cache.put(Key(3), "third")
        assertNull(cache.get(Key(2)))
        assertEquals("new", cache.get(Key(1)))
        cache.remove(Key(1))
        assertNull(cache.get(Key(1)))
        assertEquals("third", cache.get(Key(3)))
    }

    @Test
    fun `null keys support reads replacement eviction and removal`() {
        val cache = LruCache<String?, Int>(2)
        cache.put(null, 1)
        cache.put("a", 2)
        assertEquals(1, cache.get(null))
        cache.put("b", 3)
        assertNull(cache.get("a"))
        cache.put(null, 10)
        assertEquals(10, cache.get(null))
        cache.remove(null)
        assertNull(cache.get(null))
        cache.put(null, 11)
        cache.put("c", 4)
        cache.put("d", 5)
        assertNull(cache.get(null))
        assertEquals(4, cache.get("c"))
        assertEquals(5, cache.get("d"))
    }

    @Test
    fun `different model and key types work through Cache without copying values`() {
        val tasks: Cache<String, Task> = LruCache(2)
        val users: Cache<Long, User> = LruCache(2)
        val projects: Cache<Pair<String, Int>, Project> = LruCache(2)
        val task = task("t")
        val user = User("u", "User", "user@example.com")
        val project = Project("p", "Project", setOf("u"))
        tasks.put("t", task)
        users.put(42L, user)
        projects.put("team" to 1, project)
        assertSame(task, tasks.get("t"))
        assertSame(user, users.get(42L))
        assertSame(project, projects.get("team" to 1))
        tasks.remove("t")
        users.remove(42L)
        projects.remove("team" to 1)
        assertNull(tasks.get("t"))
        assertNull(users.get(42L))
        assertNull(projects.get("team" to 1))
    }

    @Test
    fun `instances have independent values and recency`() {
        val first = LruCache<String, Int>(2)
        val second = LruCache<String, Int>(2)
        first.put("a", 1)
        first.put("b", 2)
        assertNull(second.get("a"))
        second.put("a", 10)
        second.put("b", 20)
        assertEquals(1, first.get("a"))
        second.put("c", 30)
        assertNull(second.get("a"))
        first.put("c", 3)
        assertNull(first.get("b"))
        first.remove("c")
        assertEquals(30, second.get("c"))
        assertEquals(20, second.get("b"))
        assertEquals(1, first.get("a"))
    }
}
