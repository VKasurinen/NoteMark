package com.vkasurinen.notemark.notes.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val lastEditedAt: String
)