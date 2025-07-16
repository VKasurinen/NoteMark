package com.vkasurinen.notemark.notes.network

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedNotesResponse(
    val notes: List<NoteDto>,
    val total: Int
)