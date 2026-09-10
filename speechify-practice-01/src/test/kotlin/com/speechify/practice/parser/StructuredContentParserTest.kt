package com.speechify.practice.parser

import com.speechify.practice.model.ContentPayload
import com.speechify.practice.model.ContentSource
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StructuredContentParserTest {
    private val parser = StructuredContentParser()

    @Test
    fun `parses a title and valid chapter lines`() {
        val result = parser.parse(payload("A Brief History\n00:00 | Introduction\n01:05 | The story"))

        requireNotNull(result)
        assertEquals("A Brief History", result.title)
        assertEquals(listOf(0L, 65L), result.chapters.map { it.startSeconds })
    }

    @Test
    fun `ignores incomplete chapter lines`() {
        val result = parser.parse(payload("Read later\nnot a chapter\n02:03 | Valid\n03:00 |  "))

        requireNotNull(result)
        assertEquals(listOf("Valid"), result.chapters.map { it.title })
    }

    @Test
    fun `rejects a chapter timestamp with seconds outside a minute`() {
        val result = parser.parse(payload("Read later\n00:60 | Invalid\n01:02 | Valid"))

        requireNotNull(result)
        assertEquals(listOf("Valid"), result.chapters.map { it.title })
    }

    @Test
    fun `returns null for blank payloads`() {
        assertNull(parser.parse(payload(" \n\t")))
    }

    private fun payload(body: String) = ContentPayload(ContentSource.IMPORT, "id", null, body)
}

