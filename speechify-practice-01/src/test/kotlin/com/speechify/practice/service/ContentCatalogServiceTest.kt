package com.speechify.practice.service

import com.speechify.practice.model.ContentPayload
import com.speechify.practice.model.ContentSource
import com.speechify.practice.parser.StructuredContentParser
import com.speechify.practice.repository.ContentRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ContentCatalogServiceTest {
    private val parser = StructuredContentParser()

    @Test
    fun `returns parsed content from a configured provider`() = runTest {
        val service = ContentCatalogService(listOf(repository(item("a", ContentSource.IMPORT, "Import title"))), parser)
        assertEquals(listOf("Import title"), service.loadCatalog().map { it.title })
    }

    @Test
    fun `combines providers while retaining the first URL position and preferred source`() = runTest {
        val imported = repository(
            item("one", ContentSource.IMPORT, "Imported copy", "https://example.test/articles/one/"),
            item("two", ContentSource.IMPORT, "Second", "https://example.test/articles/two")
        )
        val library = repository(
            item("one-library", ContentSource.USER_LIBRARY, "Saved copy", " HTTPS://EXAMPLE.TEST/ARTICLES/ONE "),
            item("three", ContentSource.USER_LIBRARY, "Third", "https://example.test/articles/three")
        )
        val service = ContentCatalogService(listOf(imported, library), parser)

        val catalog = service.loadCatalog()

        assertEquals(listOf("Saved copy", "Second", "Third"), catalog.map { it.title })
    }

    @Test
    fun `keeps successful results when a provider is unavailable`() = runTest {
        val unavailable = object : ContentRepository {
            override suspend fun fetchContent(): List<ContentPayload> = error("provider unavailable")
        }
        val available = repository(item("ok", ContentSource.PARTNER, "Available"))

        val catalog = ContentCatalogService(listOf(unavailable, available), parser).loadCatalog()

        assertEquals(listOf("Available"), catalog.map { it.title })
    }

    private fun repository(vararg payloads: ContentPayload): ContentRepository = object : ContentRepository {
        override suspend fun fetchContent(): List<ContentPayload> = payloads.toList()
    }

    private fun item(id: String, source: ContentSource, title: String, url: String? = null) =
        ContentPayload(source, id, url, "$title\n00:00 | Start")
}

