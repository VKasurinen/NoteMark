package com.vkasurinen.notemark.notes.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedNotesResponse(
    val notes: List<NoteResponse>,
    val total: Int
)