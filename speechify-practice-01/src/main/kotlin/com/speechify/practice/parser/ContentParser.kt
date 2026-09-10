package com.speechify.practice.parser

import com.speechify.practice.model.Chapter
import com.speechify.practice.model.ContentPayload
import com.speechify.practice.model.ParsedContent

interface ContentParser {
    fun parse(payload: ContentPayload): ParsedContent?
}

class StructuredContentParser : ContentParser {
    override fun parse(payload: ContentPayload): ParsedContent? {
        val lines = payload.body.lineSequence().map(String::trim).toList()
        val titleIndex = lines.indexOfFirst { it.isNotBlank() }
        if (titleIndex == -1) return null

        val title = lines[titleIndex]
        val chapters = lines.drop(titleIndex + 1).mapNotNull(::parseChapter)
        return ParsedContent(
            source = payload.source,
            externalId = payload.externalId,
            canonicalUrl = payload.canonicalUrl,
            title = title,
            chapters = chapters,
            audioMetadata = payload.audioMetadata
        )
    }

    private fun parseChapter(line: String): Chapter? {
        val match = CHAPTER_PATTERN.matchEntire(line) ?: return null
        val minutes = match.groupValues[1].toLongOrNull() ?: return null
        val seconds = match.groupValues[2].toLongOrNull() ?: return null
        val title = match.groupValues[3].trim()
        if (title.isBlank()) return null

        return Chapter(startSeconds = minutes * 60 + seconds, title = title)
    }

    private companion object {
        val CHAPTER_PATTERN = Regex("(\\d+):(\\d{2})\\s*\\|\\s*(.*)")
    }
}

