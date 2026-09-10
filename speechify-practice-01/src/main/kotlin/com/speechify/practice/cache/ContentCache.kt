package com.speechify.practice.cache

import com.speechify.practice.model.ParsedContent
import java.time.Clock
import java.time.Duration
import java.time.Instant

interface ContentCache {
    fun get(key: String): List<ParsedContent>?
    fun put(key: String, value: List<ParsedContent>)
}

class ExpiringContentCache(
    private val ttl: Duration,
    private val clock: Clock = Clock.systemUTC()
) : ContentCache {
    private data class Entry(val value: List<ParsedContent>, val expiresAt: Instant)
    private val entries = mutableMapOf<String, Entry>()

    override fun get(key: String): List<ParsedContent>? {
        val entry = entries[key] ?: return null
        if (!clock.instant().isBefore(entry.expiresAt)) {
            entries.remove(key)
            return null
        }
        return entry.value
    }

    override fun put(key: String, value: List<ParsedContent>) {
        entries[key] = Entry(value.toList(), clock.instant().plus(ttl))
    }
}

