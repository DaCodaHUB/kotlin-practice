package com.speechify.practice.repository

import com.speechify.practice.model.ContentPayload

interface ContentRepository {
    suspend fun fetchContent(): List<ContentPayload>
}

class InMemoryContentRepository(
    private val items: List<ContentPayload>
) : ContentRepository {
    override suspend fun fetchContent(): List<ContentPayload> = items
}

