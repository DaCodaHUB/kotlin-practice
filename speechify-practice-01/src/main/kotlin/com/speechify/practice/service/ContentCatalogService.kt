package com.speechify.practice.service

import com.speechify.practice.model.ParsedContent
import com.speechify.practice.parser.ContentParser
import com.speechify.practice.repository.ContentRepository

class ContentCatalogService(
    private val repositories: List<ContentRepository>,
    private val parser: ContentParser
) {
    suspend fun loadCatalog(): List<ParsedContent> {
        val repository = repositories.firstOrNull() ?: return emptyList()
        return runCatching { repository.fetchContent() }
            .getOrDefault(emptyList())
            .mapNotNull(parser::parse)
    }
}

