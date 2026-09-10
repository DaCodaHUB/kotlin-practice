package com.speechify.practice.model

enum class ContentSource(val precedence: Int) {
    IMPORT(1),
    PARTNER(2),
    USER_LIBRARY(3)
}

data class ContentPayload(
    val source: ContentSource,
    val externalId: String,
    val canonicalUrl: String?,
    val body: String,
    val audioMetadata: AudioMetadata? = null
)

data class AudioMetadata(
    val durationSeconds: Long?,
    val language: String?
)

data class Chapter(
    val startSeconds: Long,
    val title: String
)

data class ParsedContent(
    val source: ContentSource,
    val externalId: String,
    val canonicalUrl: String?,
    val title: String,
    val chapters: List<Chapter>,
    val audioMetadata: AudioMetadata?
)

