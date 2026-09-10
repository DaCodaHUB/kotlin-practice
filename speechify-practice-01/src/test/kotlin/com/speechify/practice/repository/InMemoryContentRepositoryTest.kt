package com.speechify.practice.repository

import com.speechify.practice.model.ContentPayload
import com.speechify.practice.model.ContentSource
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class InMemoryContentRepositoryTest {
    @Test
    fun `returns its configured payloads`() = runTest {
        val items = listOf(ContentPayload(ContentSource.IMPORT, "one", null, "One"))
        assertEquals(items, InMemoryContentRepository(items).fetchContent())
    }
}

