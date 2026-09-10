package com.speechify.practice.cache

import com.speechify.practice.model.ContentSource
import com.speechify.practice.model.ParsedContent
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExpiringContentCacheTest {
    private val item = ParsedContent(ContentSource.IMPORT, "id", null, "Title", emptyList(), null)

    @Test
    fun `returns a cached value before its expiry`() {
        val cache = ExpiringContentCache(Duration.ofMinutes(5), clockAt("2025-01-01T00:00:00Z"))
        cache.put("catalog", listOf(item))
        assertEquals(listOf(item), cache.get("catalog"))
    }

    @Test
    fun `returns null at expiry`() {
        val clock = MutableClock(Instant.parse("2025-01-01T00:00:00Z"))
        val cache = ExpiringContentCache(Duration.ofMinutes(5), clock)
        cache.put("catalog", listOf(item))
        clock.now = clock.now.plus(Duration.ofMinutes(5))
        assertNull(cache.get("catalog"))
    }

    private fun clockAt(instant: String) = Clock.fixed(Instant.parse(instant), ZoneOffset.UTC)

    private class MutableClock(var now: Instant) : Clock() {
        override fun getZone() = ZoneOffset.UTC
        override fun withZone(zone: java.time.ZoneId) = this
        override fun instant(): Instant = now
    }
}

