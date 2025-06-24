package com.vkasurinen.notemark.notes.data.requests

data class PaginatedNotesResponse(
    val notes: List<NoteResponse>,
    val total: Int
)