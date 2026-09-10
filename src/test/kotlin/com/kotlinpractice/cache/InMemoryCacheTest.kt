package com.kotlinpractice.cache

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InMemoryCacheTest {
    @Test
    fun `stores replaces and removes values`() {
        val cache = InMemoryCache<String, Int>()
        cache.put("total", 1)
        cache.put("total", 2)

        assertEquals(2, cache.get("total"))

        cache.remove("total")
        assertNull(cache.get("total"))
    }
}
